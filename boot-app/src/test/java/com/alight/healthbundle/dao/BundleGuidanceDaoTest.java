package com.alight.healthbundle.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alight.healthbundle.document.EvaluationDocument;
import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.helper.EvaluationMongoHelper;
import com.alight.healthbundle.model.EvaluationOutput;
import com.alight.healthbundle.model.EvaluationResultsResponse;

/**
 * Comprehensive tests for BundleGuidanceDao.
 * Tests MongoDB data access operations including queries, saves, and mappings.
 */
@ExtendWith(MockitoExtension.class)
class BundleGuidanceDaoTest {

        @Mock
        private EvaluationMongoHelper evaluationMongoHelper;

        @Captor
        private ArgumentCaptor<EvaluationDocument> documentCaptor;

        private BundleGuidanceDao dao;

        @BeforeEach
        void setUp() {
                dao = new BundleGuidanceDao(evaluationMongoHelper);
        }

        @Test
        @DisplayName("getRecommendations should return mapped response when document exists")
        void testGetRecommendationsSuccess() {
                // Given
                String evaluationId = "test-eval-123";
                String requestHeader = "correlation-id";
                String sessionToken = "session-token";

                EvaluationDocument document = createSampleDocument(evaluationId);
                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.of(document));

                // When
                EvaluationResultsResponse response = dao.getRecommendations(
                                evaluationId, requestHeader, sessionToken);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getEvaluationId()).isEqualTo(evaluationId);
                assertThat(response.getEventType()).isEqualTo("BUNDLE_RECOMMENDATION");
                assertThat(response.getEvaluationType()).isEqualTo("AUTOMATED");
                assertThat(response.getEvaluationStatus()).isEqualTo("COMPLETED");
                assertThat(response.getSource()).isEqualTo("savvi");
                assertThat(response.getSavedAt()).isNotNull();

                verify(evaluationMongoHelper).findByEvaluationId(evaluationId);
        }

        @Test
        @DisplayName("getRecommendations should throw NoObjectFoundException when document not found")
        void testGetRecommendationsNotFound() {
                // Given
                String evaluationId = "non-existent-id";
                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.empty());

                // When/Then
                assertThatThrownBy(() -> dao.getRecommendations(evaluationId, "header", "token"))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("No evaluation data found for evaluationId: " + evaluationId);

                verify(evaluationMongoHelper).findByEvaluationId(evaluationId);
        }

        @Test
        @DisplayName("getRecommendations should throw NoObjectFoundException when MongoDB helper is null")
        void testGetRecommendationsNullHelper() {
                // Given
                BundleGuidanceDao daoWithNullHelper = new BundleGuidanceDao(null);
                String evaluationId = "test-eval-123";

                // When/Then
                assertThatThrownBy(() -> daoWithNullHelper.getRecommendations(evaluationId, "header", "token"))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("MongoDB connection not available");
        }

        @Test
        @DisplayName("getRecommendations should treat document with status 'Inactive' as not found")
        void testGetRecommendationsInactiveDocument() {
                // Given
                String evaluationId = "inactive-eval-001";
                EvaluationDocument document = createSampleDocument(evaluationId);
                document.setDocumentStatus("Inactive");
                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.of(document));

                // When/Then
                assertThatThrownBy(() -> dao.getRecommendations(evaluationId, "header", "token"))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("No evaluation data found for evaluationId: " + evaluationId);

                verify(evaluationMongoHelper).findByEvaluationId(evaluationId);
        }

        @Test
        @DisplayName("saveRecommendations should create new document when not exists")
        void testSaveRecommendationsNewDocument() {
                // Given
                String evaluationId = "new-eval-456";
                EvaluationResultsResponse request = createSampleResponse(evaluationId);

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.empty());

                EvaluationDocument savedDocument = createSampleDocument(evaluationId);
                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenReturn(savedDocument);

                // When
                EvaluationResultsResponse response = dao.saveRecommendations(request, "header", "token");

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getEvaluationId()).isEqualTo(evaluationId);
                assertThat(response.getSource()).isEqualTo("savvi");

                verify(evaluationMongoHelper).findByEvaluationId(evaluationId);
                verify(evaluationMongoHelper).save(any(EvaluationDocument.class));
        }

        @Test
        @DisplayName("saveRecommendations should update existing document when already exists")
        void testSaveRecommendationsUpdateExisting() {
                // Given
                String evaluationId = "existing-eval-789";
                EvaluationResultsResponse request = createSampleResponse(evaluationId);
                request.setEvaluationStatus("UPDATED_STATUS");

                EvaluationDocument existingDocument = createSampleDocument(evaluationId);
                existingDocument.setId("existing-doc-id");

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.of(existingDocument));

                EvaluationDocument updatedDocument = createSampleDocument(evaluationId);
                updatedDocument.setId("existing-doc-id");
                updatedDocument.setEvaluationStatus("UPDATED_STATUS");

                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenReturn(updatedDocument);

                // When
                EvaluationResultsResponse response = dao.saveRecommendations(request, "header", "token");

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getEvaluationId()).isEqualTo(evaluationId);
                assertThat(response.getEvaluationStatus()).isEqualTo("UPDATED_STATUS");

                verify(evaluationMongoHelper).findByEvaluationId(evaluationId);
                verify(evaluationMongoHelper).save(any(EvaluationDocument.class));
        }

        @Test
        @DisplayName("saveRecommendations should throw NoObjectFoundException when MongoDB helper is null")
        void testSaveRecommendationsNullHelper() {
                // Given
                BundleGuidanceDao daoWithNullHelper = new BundleGuidanceDao(null);
                EvaluationResultsResponse request = createSampleResponse("test-eval-123");

                // When/Then
                assertThatThrownBy(() -> daoWithNullHelper.saveRecommendations(request, "header", "token"))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("MongoDB connection not available");

                // Verify save was never called since validation failed
                verify(evaluationMongoHelper, never()).save(any());
        }

        @Test
        @DisplayName("saveRecommendations should set source to 'savvi' for new documents (override request)")
        void testSaveRecommendationsSetsCorrectSource_New() {
                // Given
                String evaluationId = "source-test-123";
                EvaluationResultsResponse request = createSampleResponse(evaluationId);
                request.setSource("originalSource"); // This must be overridden to "savvi"

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.empty());

                // Capture what DAO tries to save; return a realistic saved doc
                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenAnswer(invocation -> {
                                        EvaluationDocument toSave = invocation.getArgument(0);
                                        // Simulate DB returning same doc (as if persisted)
                                        return toSave;
                                });

                // When
                EvaluationResultsResponse response = dao.saveRecommendations(request, "header", "token");

                // Then: assert the doc passed to save had source "savvi"
                verify(evaluationMongoHelper).save(documentCaptor.capture());
                EvaluationDocument persisted = documentCaptor.getValue();
                assertThat(persisted.getSource()).isEqualTo("savvi");
                assertThat(persisted.getEvaluationId()).isEqualTo(evaluationId);
                assertThat(persisted.getSavedAt()).isNotNull();
                // Response should mirror persisted doc
                assertThat(response.getSource()).isEqualTo("savvi");
        }

        @Test
        @DisplayName("saveRecommendations should set savedAt timestamp")
        void testSaveRecommendationsSetsTimestamp() {
                // Given
                String evaluationId = "timestamp-test-456";
                EvaluationResultsResponse request = createSampleResponse(evaluationId);

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.empty());

                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                EvaluationResultsResponse response = dao.saveRecommendations(request, "header", "token");

                // Then
                assertThat(response.getSavedAt()).isNotNull();
                verify(evaluationMongoHelper).save(any(EvaluationDocument.class));
        }

        @Test
        @DisplayName("getRecommendations should map all document fields correctly")
        void testGetRecommendationsFieldMapping() {
                // Given
                String evaluationId = "mapping-test-789";

                EvaluationDocument document = new EvaluationDocument();
                document.setId("doc-id-123");
                document.setEvaluationId(evaluationId);
                document.setEventType("CUSTOM_EVENT");
                document.setEvaluationType("MANUAL");
                document.setEvaluationStatus("IN_PROGRESS");
                document.setSource("test-source");
                document.setSavedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
                document.setCreatedAt(LocalDateTime.of(2024, 1, 10, 9, 0));
                document.setUpdatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

                EvaluationOutput output = new EvaluationOutput();
                output.setBundles(null); // Not critical for this test
                document.setEvaluationOutput(output);

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.of(document));

                // When
                EvaluationResultsResponse response = dao.getRecommendations(evaluationId, "header", "token");

                // Then
                assertThat(response.getEvaluationId()).isEqualTo(evaluationId);
                assertThat(response.getEventType()).isEqualTo("CUSTOM_EVENT");
                assertThat(response.getEvaluationType()).isEqualTo("MANUAL");
                assertThat(response.getEvaluationStatus()).isEqualTo("IN_PROGRESS");
                assertThat(response.getSource()).isEqualTo("test-source");
                assertThat(response.getSavedAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 10, 9, 0));
                assertThat(response.getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
                assertThat(response.getEvaluationOutput()).isNotNull();
        }

        @Test
        @DisplayName("saveRecommendations should preserve existing document metadata when updating (createdAt)")
        void testSaveRecommendationsPreservesMetadata() {
                // Given
                String evaluationId = "metadata-test-111";
                EvaluationResultsResponse request = createSampleResponse(evaluationId);

                EvaluationDocument existingDocument = createSampleDocument(evaluationId);
                existingDocument.setId("original-id");
                existingDocument.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
                LocalDateTime previousUpdatedAt = existingDocument.getUpdatedAt();

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.of(existingDocument));

                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                EvaluationResultsResponse response = dao.saveRecommendations(request, "header", "token");

                // Then
                // The createdAt should be preserved from existing document
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));

                // Verify updatedAt was refreshed
                verify(evaluationMongoHelper).save(documentCaptor.capture());
                EvaluationDocument persisted = documentCaptor.getValue();
                assertThat(persisted.getUpdatedAt()).isNotNull();
                assertThat(persisted.getUpdatedAt()).isNotEqualTo(previousUpdatedAt);
        }

        @Test
        @DisplayName("getRecommendations should handle documents with null optional fields")
        void testGetRecommendationsWithNullFields() {
                // Given
                String evaluationId = "null-fields-test";

                EvaluationDocument document = new EvaluationDocument();
                document.setEvaluationId(evaluationId);
                document.setEventType("EVENT");
                document.setEvaluationType("TYPE");
                document.setEvaluationStatus("STATUS");
                // Leave other fields null

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.of(document));

                // When
                EvaluationResultsResponse response = dao.getRecommendations(evaluationId, "header", "token");

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getEvaluationId()).isEqualTo(evaluationId);
                assertThat(response.getEvaluationOutput()).isNull();
                assertThat(response.getSource()).isNull();
                assertThat(response.getSavedAt()).isNull();
        }

        @Test
        @DisplayName("saveRecommendations should reconstruct legacy document (createdAt == null), preserve _id, and apply defaults")
        void testSaveRecommendations_LegacyDocument_Reconstructs() {
                // Given
                String evaluationId = "legacy-eval-001";
                EvaluationResultsResponse request = new EvaluationResultsResponse();
                request.setEvaluationId(evaluationId);
                // Intentionally set nulls to test defaults in legacy reconstruction
                request.setEventType(null);
                request.setEvaluationType(null);
                request.setEvaluationStatus(null);
                EvaluationOutput output = new EvaluationOutput();
                request.setEvaluationOutput(output);

                EvaluationDocument legacyExisting = new EvaluationDocument();
                legacyExisting.setId("legacy-doc-id");
                legacyExisting.setEvaluationId(evaluationId);
                legacyExisting.setCreatedAt(null); // Legacy signature

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.of(legacyExisting));

                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                EvaluationResultsResponse response = dao.saveRecommendations(request, "header", "token");

                // Then: the DAO should reconstruct a fresh document with same _id and defaults
                // applied
                verify(evaluationMongoHelper).save(documentCaptor.capture());
                EvaluationDocument persisted = documentCaptor.getValue();

                assertThat(persisted.getId()).isEqualTo("legacy-doc-id"); // _id preserved
                assertThat(persisted.getCreatedAt()).isNotNull(); // reconstructed
                assertThat(persisted.getEventType()).isEqualTo("evaluation_result"); // default
                assertThat(persisted.getEvaluationType()).isEqualTo("bundle"); // default
                assertThat(persisted.getEvaluationStatus()).isEqualTo("success"); // default
                assertThat(persisted.getSource()).isEqualTo("savvi");
                assertThat(persisted.getSavedAt()).isNotNull();
                assertThat(persisted.getUpdatedAt()).isNotNull();

                // Response reflects persisted doc
                assertThat(response.getEvaluationId()).isEqualTo(evaluationId);
                assertThat(response.getEventType()).isEqualTo("evaluation_result");
                assertThat(response.getEvaluationType()).isEqualTo("bundle");
                assertThat(response.getEvaluationStatus()).isEqualTo("success");
        }

        @Test
        @DisplayName("saveRecommendations should apply defaults when updating non-legacy doc and request fields are null")
        void testSaveRecommendations_UpdateAppliesDefaultsWhenNulls() {
                // Given
                String evaluationId = "defaults-update-002";

                EvaluationResultsResponse request = new EvaluationResultsResponse();
                request.setEvaluationId(evaluationId);
                request.setEventType(null);
                request.setEvaluationType(null);
                request.setEvaluationStatus(null);
                EvaluationOutput reqOutput = new EvaluationOutput();
                request.setEvaluationOutput(reqOutput);

                EvaluationDocument existing = createSampleDocument(evaluationId);
                existing.setId("existing-id");
                LocalDateTime previousSavedAt = existing.getSavedAt();
                LocalDateTime previousUpdatedAt = existing.getUpdatedAt();

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.of(existing));

                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                EvaluationResultsResponse response = dao.saveRecommendations(request, "header", "token");

                // Then
                verify(evaluationMongoHelper).save(documentCaptor.capture());
                EvaluationDocument persisted = documentCaptor.getValue();

                assertThat(persisted.getEventType()).isEqualTo("evaluation_result");
                assertThat(persisted.getEvaluationType()).isEqualTo("bundle");
                assertThat(persisted.getEvaluationStatus()).isEqualTo("success");
                assertThat(persisted.getSource()).isEqualTo("savvi");
                assertThat(persisted.getSavedAt()).isNotNull().isNotEqualTo(previousSavedAt);
                assertThat(persisted.getUpdatedAt()).isNotNull().isNotEqualTo(previousUpdatedAt);
                assertThat(persisted.getEvaluationOutput()).isSameAs(reqOutput); // preserved from request

                // Response should mirror what was persisted
                assertThat(response.getEventType()).isEqualTo("evaluation_result");
                assertThat(response.getEvaluationType()).isEqualTo("bundle");
                assertThat(response.getEvaluationStatus()).isEqualTo("success");
        }

        @Test
        @DisplayName("saveRecommendations should carry evaluationOutput from request to persisted doc and response")
        void testSaveRecommendations_PreservesEvaluationOutput() {
                // Given
                String evaluationId = "output-preserve-003";
                EvaluationResultsResponse request = createSampleResponse(evaluationId);
                EvaluationOutput out = new EvaluationOutput();
                request.setEvaluationOutput(out);

                when(evaluationMongoHelper.findByEvaluationId(evaluationId))
                                .thenReturn(Optional.empty());

                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // When
                EvaluationResultsResponse response = dao.saveRecommendations(request, "header", "token");

                // Then
                verify(evaluationMongoHelper).save(documentCaptor.capture());
                EvaluationDocument persisted = documentCaptor.getValue();
                assertThat(persisted.getEvaluationOutput()).isSameAs(out);
                assertThat(response.getEvaluationOutput()).isSameAs(out);
        }

        /**
         * Helper method to create a sample EvaluationDocument for testing.
         */
        private EvaluationDocument createSampleDocument(String evaluationId) {
                EvaluationDocument document = new EvaluationDocument();
                document.setId("doc-id-" + evaluationId);
                document.setEvaluationId(evaluationId);
                document.setEventType("BUNDLE_RECOMMENDATION");
                document.setEvaluationType("AUTOMATED");
                document.setEvaluationStatus("COMPLETED");
                document.setSource("savvi");
                document.setSavedAt(LocalDateTime.now());
                document.setCreatedAt(LocalDateTime.now().minusDays(1));
                document.setUpdatedAt(LocalDateTime.now());

                EvaluationOutput output = new EvaluationOutput();
                output.setBundles(null); // Set to null for now, not critical for this test
                document.setEvaluationOutput(output);

                return document;
        }

        /**
         * Helper method to create a sample EvaluationResultsResponse for testing.
         */
        private EvaluationResultsResponse createSampleResponse(String evaluationId) {
                EvaluationResultsResponse response = new EvaluationResultsResponse();
                response.setEvaluationId(evaluationId);
                response.setEventType("BUNDLE_RECOMMENDATION");
                response.setEvaluationType("AUTOMATED");
                response.setEvaluationStatus("COMPLETED");

                EvaluationOutput output = new EvaluationOutput();
                output.setBundles(null); // Set to null for now, not critical for this test
                response.setEvaluationOutput(output);

                return response;
        }
}
