package com.alight.healthbundle.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.alight.healthbundle.document.EvaluationInputDocument;
import com.alight.healthbundle.exceptions.BadRequestException;
import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.helper.EvaluationInputMongoHelper;
import com.alight.healthbundle.model.EvaluationInputRequest;
import com.alight.healthbundle.model.SAVVIRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class EvaluationInputDaoTest {

        @Mock
        private EvaluationInputMongoHelper mongoHelper;

        @Mock
        private ObjectMapper objectMapper;

        @InjectMocks
        private EvaluationInputDao dao;

        private static final String CLIENT_ID = "19968";
        private static final String PLATFORM_INTERNAL_ID = "12853765";
        private static final String BUSINESS_PROCESS_REF_ID = "bp-123";
        private static final String EVALUATION_ID = "eval-456";

        @BeforeEach
        void setUp() {
                lenient().when(objectMapper.convertValue(any(Map.class), eq(SAVVIRequest.class)))
                                .thenReturn(createSAVVIRequest());
                lenient().when(objectMapper.convertValue(any(SAVVIRequest.class), any(TypeReference.class)))
                                .thenReturn(createSavviRequestMap());
        }

        @Test
        @DisplayName("Get evaluation input by evaluation ID successfully")
        void testGetEvaluationInput_WithEvaluationId_Success() {
                EvaluationInputDocument document = createDocument();
                when(mongoHelper.findByEvaluationId(EVALUATION_ID)).thenReturn(Optional.of(document));

                EvaluationInputRequest result = dao.getEvaluationInput(
                                BUSINESS_PROCESS_REF_ID, PLATFORM_INTERNAL_ID, EVALUATION_ID);

                assertThat(result).isNotNull();
                assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
                assertThat(result.getPlatformInternalId()).isEqualTo(PLATFORM_INTERNAL_ID);
                assertThat(result.getBusinessProcessReferenceId()).isEqualTo(BUSINESS_PROCESS_REF_ID);
                verify(mongoHelper).findByEvaluationId(EVALUATION_ID);
                verify(mongoHelper, never()).findByBusinessProcessReferenceIdAndPlatformInternalId(anyString(),
                                anyString());
        }

        @Test
        @DisplayName("Get evaluation input by business process reference ID and platform internal ID")
        void testGetEvaluationInput_WithoutEvaluationId_Success() {
                EvaluationInputDocument document = createDocument();
                when(mongoHelper.findByBusinessProcessReferenceIdAndPlatformInternalId(
                                BUSINESS_PROCESS_REF_ID, PLATFORM_INTERNAL_ID))
                                .thenReturn(Optional.of(document));

                EvaluationInputRequest result = dao.getEvaluationInput(
                                BUSINESS_PROCESS_REF_ID, PLATFORM_INTERNAL_ID, null);

                assertThat(result).isNotNull();
                assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
                verify(mongoHelper).findByBusinessProcessReferenceIdAndPlatformInternalId(
                                BUSINESS_PROCESS_REF_ID, PLATFORM_INTERNAL_ID);
                verify(mongoHelper, never()).findByEvaluationId(anyString());
        }

        @Test
        @DisplayName("Get evaluation input throws NoObjectFoundException when not found by evaluationId")
        void testGetEvaluationInput_NotFound() {
                when(mongoHelper.findByEvaluationId(EVALUATION_ID)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> dao.getEvaluationInput(
                                BUSINESS_PROCESS_REF_ID, PLATFORM_INTERNAL_ID, EVALUATION_ID))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("Evaluation input not found");
        }

        @Test
        @DisplayName("Get evaluation input throws NoObjectFoundException when not found by BPR+platformInternalId")
        void testGetEvaluationInput_WithoutEvaluationId_NotFound() {
                when(mongoHelper.findByBusinessProcessReferenceIdAndPlatformInternalId(
                                BUSINESS_PROCESS_REF_ID, PLATFORM_INTERNAL_ID)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> dao.getEvaluationInput(
                                BUSINESS_PROCESS_REF_ID, PLATFORM_INTERNAL_ID, null))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("Evaluation input not found");
        }

        @Test
        @DisplayName("Get evaluation input throws RuntimeException when database error occurs")
        void testGetEvaluationInput_DatabaseError() {
                when(mongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenThrow(new RuntimeException("Database connection failed"));

                assertThatThrownBy(() -> dao.getEvaluationInput(
                                BUSINESS_PROCESS_REF_ID, PLATFORM_INTERNAL_ID, EVALUATION_ID))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Error retrieving evaluation input from database");
        }

        @Test
        @DisplayName("Get evaluation input throws RuntimeException when mapToResponse conversion fails")
        void testGetEvaluationInput_MapToResponseConversionError() {
                EvaluationInputDocument document = createDocument();
                when(mongoHelper.findByEvaluationId(EVALUATION_ID)).thenReturn(Optional.of(document));
                when(objectMapper.convertValue(any(Map.class), eq(SAVVIRequest.class)))
                                .thenThrow(new IllegalArgumentException("bad map"));

                assertThatThrownBy(() -> dao.getEvaluationInput(
                                BUSINESS_PROCESS_REF_ID, PLATFORM_INTERNAL_ID, EVALUATION_ID))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Error retrieving evaluation input from database");
        }

        @Test
        @DisplayName("Get evaluation input by clientId and platformInternalId successfully")
        void testGetEvaluationInputByClientId_Success() {
                EvaluationInputDocument document = createDocument();
                List<EvaluationInputDocument> documents = Arrays.asList(document);

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), any(Sort.class)))
                                .thenReturn(documents);

                EvaluationInputRequest result = dao
                                .getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID, null);

                assertThat(result).isNotNull();
                assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
                verify(mongoHelper).findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), any(Sort.class));
        }

        @Test
        @DisplayName("Get evaluation input by clientId filters by evaluationId when provided")
        void testGetEvaluationInputByClientId_WithEvaluationIdFilter() {
                EvaluationInputDocument document1 = createDocument();
                document1.setEvaluationId(EVALUATION_ID);
                EvaluationInputDocument document2 = createDocument();
                document2.setEvaluationId("different-eval");
                List<EvaluationInputDocument> documents = Arrays.asList(document1, document2);

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), any(Sort.class)))
                                .thenReturn(documents);

                EvaluationInputRequest result = dao
                                .getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID,
                                                EVALUATION_ID);

                assertThat(result).isNotNull();
                assertThat(result.getEvaluationId()).isEqualTo(EVALUATION_ID);
        }

        @Test
        @DisplayName("Get evaluation input by clientId returns latest document when multiple exist")
        void testGetEvaluationInputByClientId_ReturnsLatest() {
                EvaluationInputDocument oldDoc = createDocument();
                oldDoc.setSavedAt(LocalDateTime.now().minusDays(1));
                EvaluationInputDocument newDoc = createDocument();
                newDoc.setSavedAt(LocalDateTime.now());
                List<EvaluationInputDocument> documents = Arrays.asList(newDoc, oldDoc);

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), any(Sort.class)))
                                .thenReturn(documents);

                EvaluationInputRequest result = dao
                                .getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID, null);

                assertThat(result).isNotNull();
                assertThat(result.getSavedAt()).isEqualTo(newDoc.getSavedAt());
        }

        @Test
        @DisplayName("Get evaluation input by clientId throws NoObjectFoundException when empty list")
        void testGetEvaluationInputByClientId_EmptyList() {
                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), any(Sort.class)))
                                .thenReturn(new ArrayList<>());

                assertThatThrownBy(() -> dao
                                .getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID, null))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("Evaluation input not found");
        }

        @Test
        @DisplayName("Get evaluation input by clientId throws NoObjectFoundException when evaluationId not matched")
        void testGetEvaluationInputByClientId_EvaluationIdNotMatched() {
                EvaluationInputDocument document = createDocument();
                document.setEvaluationId("different-eval");
                List<EvaluationInputDocument> documents = Arrays.asList(document);

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), any(Sort.class)))
                                .thenReturn(documents);

                assertThatThrownBy(() -> dao
                                .getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID,
                                                EVALUATION_ID))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("Evaluation input not found");
        }

        @Test
        @DisplayName("Get evaluation input by clientId should treat latest Inactive document as not found")
        void testGetEvaluationInputByClientId_InactiveLatest() {
                EvaluationInputDocument inactive = createDocument();
                inactive.setDocumentStatus("Inactive");
                inactive.setSavedAt(LocalDateTime.now());
                List<EvaluationInputDocument> documents = Arrays.asList(inactive);

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), any(Sort.class)))
                                .thenReturn(documents);

                assertThatThrownBy(() -> dao
                                .getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID, null))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("Evaluation input not found");
        }

        @Test
        @DisplayName("Get evaluation input by clientId wraps mapper failure as RuntimeException")
        void testGetEvaluationInputByClientId_MapToResponseConversionError() {
                EvaluationInputDocument document = createDocument();
                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), any(Sort.class)))
                                .thenReturn(List.of(document));
                when(objectMapper.convertValue(any(Map.class), eq(SAVVIRequest.class)))
                                .thenThrow(new IllegalArgumentException("bad map"));

                assertThatThrownBy(() -> dao
                                .getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID, null))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Error retrieving evaluation input from database");
        }

        @Test
        @DisplayName("Get evaluation input by clientId wraps DB errors as RuntimeException")
        void testGetEvaluationInputByClientId_DatabaseError() {
                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), any(Sort.class)))
                                .thenThrow(new RuntimeException("DB down"));

                assertThatThrownBy(() -> dao
                                .getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID, null))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Error retrieving evaluation input from database");
        }

        @Test
        @DisplayName("Save evaluation input creates new document successfully")
        void testSaveEvaluationInput_CreateNew_Success() {
                EvaluationInputRequest request = createRequest();
                EvaluationInputDocument savedDocument = createDocument();

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdAndEvaluationId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID),
                                eq(EVALUATION_ID)))
                                .thenReturn(Optional.empty());
                when(mongoHelper.save(any(EvaluationInputDocument.class))).thenReturn(savedDocument);

                EvaluationInputRequest result = dao.saveEvaluationInput(request, PLATFORM_INTERNAL_ID);

                assertThat(result).isNotNull();
                assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
                verify(mongoHelper).save(any(EvaluationInputDocument.class));
        }

        @Test
        @DisplayName("Save evaluation input updates existing document by evaluationId")
        void testSaveEvaluationInput_UpdateExisting_ByEvaluationId() {
                EvaluationInputRequest request = createRequest();
                EvaluationInputDocument existingDocument = createDocument();
                EvaluationInputDocument savedDocument = createDocument();

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdAndEvaluationId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID),
                                eq(EVALUATION_ID)))
                                .thenReturn(Optional.of(existingDocument));
                when(mongoHelper.save(any(EvaluationInputDocument.class))).thenReturn(savedDocument);

                EvaluationInputRequest result = dao.saveEvaluationInput(request, PLATFORM_INTERNAL_ID);

                assertThat(result).isNotNull();
                verify(mongoHelper).save(
                                argThat(doc -> doc.getId() != null && doc.getId().equals(existingDocument.getId())));
        }

        @Test
        @DisplayName("Save evaluation input updates existing document by businessProcessReferenceId")
        void testSaveEvaluationInput_UpdateExisting_ByBusinessProcessReferenceId() {
                EvaluationInputRequest request = createRequest();
                EvaluationInputDocument existingDocument = createDocument();
                EvaluationInputDocument savedDocument = createDocument();

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdAndEvaluationId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID),
                                eq(EVALUATION_ID)))
                                .thenReturn(Optional.of(existingDocument));
                when(mongoHelper.save(any(EvaluationInputDocument.class))).thenReturn(savedDocument);

                EvaluationInputRequest result = dao.saveEvaluationInput(request, PLATFORM_INTERNAL_ID);

                assertThat(result).isNotNull();
                verify(mongoHelper).save(
                                argThat(doc -> doc.getId() != null && doc.getId().equals(existingDocument.getId())));
        }

        @Test
        @DisplayName("Save evaluation input throws BadRequestException when data format is invalid (create/update mapping)")
        void testSaveEvaluationInput_InvalidDataFormat() {
                EvaluationInputRequest request = createRequest();

                when(objectMapper.convertValue(any(SAVVIRequest.class), any(TypeReference.class)))
                                .thenThrow(new IllegalArgumentException("Invalid format"));

                assertThatThrownBy(() -> dao.saveEvaluationInput(request, PLATFORM_INTERNAL_ID))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Invalid data format");
        }

        @Test
        @DisplayName("Save evaluation input throws RuntimeException when database error occurs before save")
        void testSaveEvaluationInput_DatabaseError() {
                EvaluationInputRequest request = createRequest();

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdAndEvaluationId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID),
                                eq(EVALUATION_ID)))
                                .thenThrow(new RuntimeException("Database connection failed"));

                assertThatThrownBy(() -> dao.saveEvaluationInput(request, PLATFORM_INTERNAL_ID))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Error saving evaluation input to database");
        }

        @Test
        @DisplayName("Save (create) should use platformInternalId from header, not from request")
        void testSaveEvaluationInput_Create_UsesHeaderPlatformInternalId() {
                // Given a request that carries a different platformInternalId than header
                EvaluationInputRequest request = EvaluationInputRequest.builder()
                                .clientId(CLIENT_ID)
                                .platformInternalId("DIFFERENT-IN-REQUEST") // should be ignored for create
                                .evaluationId(EVALUATION_ID)
                                .businessProcessReferenceId(BUSINESS_PROCESS_REF_ID)
                                .savviRequest(createSAVVIRequest())
                                .timestamp("2024-02-11T10:00:00")
                                .build();

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdAndEvaluationId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID),
                                eq(EVALUATION_ID)))
                                .thenReturn(Optional.empty()); // Not found by BPR path

                when(mongoHelper.save(any(EvaluationInputDocument.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                EvaluationInputRequest response = dao.saveEvaluationInput(request, PLATFORM_INTERNAL_ID);

                ArgumentCaptor<EvaluationInputDocument> captor = ArgumentCaptor.forClass(EvaluationInputDocument.class);
                verify(mongoHelper).save(captor.capture());
                EvaluationInputDocument persisted = captor.getValue();

                assertThat(persisted.getPlatformInternalId()).isEqualTo(PLATFORM_INTERNAL_ID);
                assertThat(response.getPlatformInternalId()).isEqualTo(PLATFORM_INTERNAL_ID);
        }

        @Test
        @DisplayName("Save (create) should set createdAt/savedAt/updatedAt and preserve timestamp/body fields")
        void testSaveEvaluationInput_Create_SetsTimestampsAndMapsFields() {
                EvaluationInputRequest request = createRequest();

                when(mongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdAndEvaluationId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID),
                                eq(EVALUATION_ID)))
                                .thenReturn(Optional.empty());

                when(mongoHelper.save(any(EvaluationInputDocument.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                EvaluationInputRequest response = dao.saveEvaluationInput(request, PLATFORM_INTERNAL_ID);

                assertThat(response.getCreatedAt()).isNotNull();
                assertThat(response.getSavedAt()).isNotNull();
                assertThat(response.getUpdatedAt()).isNotNull();
                assertThat(response.getBusinessProcessReferenceId()).isEqualTo(BUSINESS_PROCESS_REF_ID);
                assertThat(response.getEvaluationId()).isEqualTo(EVALUATION_ID);
                assertThat(response.getSavviRequest()).isNotNull();
        }

        // Helper methods

        private EvaluationInputRequest createRequest() {
                return EvaluationInputRequest.builder()
                                .clientId(CLIENT_ID)
                                .platformInternalId(PLATFORM_INTERNAL_ID)
                                .evaluationId(EVALUATION_ID)
                                .businessProcessReferenceId(BUSINESS_PROCESS_REF_ID)
                                .savviRequest(createSAVVIRequest())
                                .timestamp("2024-02-11T10:00:00")
                                .build();
        }

        private EvaluationInputDocument createDocument() {
                LocalDateTime now = LocalDateTime.now();
                return EvaluationInputDocument.builder()
                                .id("doc-123")
                                .clientId(CLIENT_ID)
                                .platformInternalId(PLATFORM_INTERNAL_ID)
                                .evaluationId(EVALUATION_ID)
                                .businessProcessReferenceId(BUSINESS_PROCESS_REF_ID)
                                .savviRequest(createSavviRequestMap())
                                .timestamp("2024-02-11T10:00:00")
                                .savedAt(now)
                                .createdAt(now)
                                .updatedAt(now)
                                .build();
        }

        private SAVVIRequest createSAVVIRequest() {
                SAVVIRequest request = new SAVVIRequest();
                // populate minimally as needed
                return request;
        }

        private Map<String, Object> createSavviRequestMap() {
                Map<String, Object> map = new HashMap<>();
                map.put("subscriber", new HashMap<>());
                map.put("people", new ArrayList<>());
                return map;
        }
}
