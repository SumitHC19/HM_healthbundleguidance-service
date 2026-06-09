package com.alight.healthbundle.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alight.healthbundle.document.EvaluationDocument;
import com.alight.healthbundle.document.EvaluationInputDocument;
import com.alight.healthbundle.exceptions.BadRequestException;
import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.helper.BundleSelectionCustomRepository;
import com.alight.healthbundle.helper.BundleSelectionMongoHelper;
import com.alight.healthbundle.helper.EvaluationInputMongoHelper;
import com.alight.healthbundle.helper.EvaluationMongoHelper;
import com.alight.healthbundle.model.Bundle;
import com.alight.healthbundle.model.BundleSelection;
import com.alight.healthbundle.model.EvaluationOutput;
import com.alight.healthbundle.util.RequestContext;
import com.alight.healthbundle.util.RequestContextExtractor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class BundleSelectionServiceTest {

        @Mock
        private BundleSelectionMongoHelper bundleSelectionMongoHelper;

        @Mock
        private BundleSelectionCustomRepository bundleSelectionCustomRepository;

        @Mock
        private EvaluationMongoHelper evaluationMongoHelper;

        @Mock
        private EvaluationInputMongoHelper evaluationInputMongoHelper;

        @Mock
        private RequestContextExtractor requestContextExtractor;

        @Mock
        private ObjectMapper mapper;

        @InjectMocks
        private BundleSelectionService service;

        private static final String CLIENT_ID = "19968";
        private static final String PLATFORM_INTERNAL_ID = "12853765";
        private static final String BUSINESS_PROCESS_REF_ID = "bp-123";
        private static final String EVALUATION_ID = "eval-456";
        private static final String TOKEN = "test-token";
        private static final String REQUEST_HEADER = "{\"clientId\":\"19968\"}";
        private static final String PLATFORM_ID_HEADER = "header-platform-id";
        private static final String FEATURED_AS_BALANCED = "balanced";
        private static final String FEATURED_AS_NONE = "none";

        @BeforeEach
        void setUp() {
                RequestContext context = new RequestContext(CLIENT_ID, PLATFORM_INTERNAL_ID);
                lenient().when(requestContextExtractor.extractRequestContext(any(), any(), any())).thenReturn(context);
        }

        // ---------------------------
        // getBundleSelection tests
        // ---------------------------

        @Test
        @DisplayName("Get bundle selection successfully")
        void testGetBundleSelection_Success() {
                Document doc = new Document()
                                .append("clientId", CLIENT_ID)
                                .append("platformInternalId", PLATFORM_INTERNAL_ID)
                                .append("businessProcessReferenceId", BUSINESS_PROCESS_REF_ID)
                                .append("evaluationId", EVALUATION_ID)
                                .append("featuredAs", FEATURED_AS_BALANCED)
                                .append("lastModifiedTimeStamp", new Date());

                BundleSelection bundleSelection = new BundleSelection();
                bundleSelection.setClientId(CLIENT_ID);
                bundleSelection.setPlatformInternalId(PLATFORM_INTERNAL_ID);
                bundleSelection.setBusinessProcessReferenceId(BUSINESS_PROCESS_REF_ID);
                bundleSelection.setEvaluationId(EVALUATION_ID);
                bundleSelection.setFeaturedAs(FEATURED_AS_BALANCED); // enum now

                when(bundleSelectionCustomRepository
                                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID))
                                .thenReturn(java.util.Arrays.asList(doc));
                when(mapper.convertValue(doc, BundleSelection.class)).thenReturn(bundleSelection);

                BundleSelection result = service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, null, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getEvaluationId()).isEqualTo(EVALUATION_ID);
                assertThat(result.getFeaturedAs()).isEqualTo(FEATURED_AS_BALANCED);
        }

        @Test
        @DisplayName("Get bundle selection with matching evaluationId filter")
        void testGetBundleSelection_WithMatchingEvaluationId() {
                Document doc = new Document()
                                .append("evaluationId", EVALUATION_ID)
                                .append("featuredAs", FEATURED_AS_BALANCED)
                                .append("lastModifiedTimeStamp", new Date());

                BundleSelection bundleSelection = new BundleSelection();
                bundleSelection.setEvaluationId(EVALUATION_ID);
                bundleSelection.setFeaturedAs(FEATURED_AS_BALANCED);

                when(bundleSelectionCustomRepository
                                .findByEvaluationIdClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                EVALUATION_ID, CLIENT_ID, PLATFORM_INTERNAL_ID,
                                                BUSINESS_PROCESS_REF_ID))
                                .thenReturn(java.util.Arrays.asList(doc));
                when(mapper.convertValue(doc, BundleSelection.class)).thenReturn(bundleSelection);

                BundleSelection result = service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getEvaluationId()).isEqualTo(EVALUATION_ID);
                assertThat(result.getFeaturedAs()).isEqualTo(FEATURED_AS_BALANCED);
        }

        @Test
        @DisplayName("Get bundle selection returns empty when evaluationId doesn't match")
        void testGetBundleSelection_EvaluationIdMismatch() {
                when(bundleSelectionCustomRepository
                                .findByEvaluationIdClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                EVALUATION_ID, CLIENT_ID, PLATFORM_INTERNAL_ID,
                                                BUSINESS_PROCESS_REF_ID))
                                .thenReturn(new java.util.ArrayList<>());

                assertThatThrownBy(() -> service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID, PLATFORM_ID_HEADER))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("Bundle selection not found");
        }

        @Test
        @DisplayName("Get bundle selection throws exception when not found")
        void testGetBundleSelection_NotFound() {
                when(bundleSelectionCustomRepository
                                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID))
                                .thenReturn(new java.util.ArrayList<>());

                assertThatThrownBy(() -> service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, null, PLATFORM_ID_HEADER))
                                .isInstanceOf(NoObjectFoundException.class)
                                .hasMessageContaining("Bundle selection not found");
        }

        @Test
        @DisplayName("Get bundle selection throws BadRequestException when businessProcessReferenceId is null")
        void testGetBundleSelection_NullBusinessProcessReferenceId() {
                assertThatThrownBy(() -> service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, null, null, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Business process reference ID is required");
        }

        @Test
        @DisplayName("Get bundle selection throws BadRequestException when businessProcessReferenceId is blank")
        void testGetBundleSelection_BlankBusinessProcessReferenceId() {
                assertThatThrownBy(() -> service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, "  ", null, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Business process reference ID is required");
        }

        @Test
        @DisplayName("Get bundle selection throws RuntimeException when unexpected error occurs")
        void testGetBundleSelection_UnexpectedError() {
                when(bundleSelectionCustomRepository
                                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                anyString(), anyString(), anyString()))
                                .thenThrow(new RuntimeException("Database error"));

                assertThatThrownBy(() -> service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, null, PLATFORM_ID_HEADER))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Failed to retrieve bundle selection");
        }

        @Test
        @DisplayName("Get bundle selection wraps mapper.convertValue errors in RuntimeException")
        void testGetBundleSelection_MapperConversionError() {
                Document doc = new Document()
                                .append("clientId", CLIENT_ID)
                                .append("platformInternalId", PLATFORM_INTERNAL_ID)
                                .append("businessProcessReferenceId", BUSINESS_PROCESS_REF_ID)
                                .append("lastModifiedTimeStamp", new Date());

                when(bundleSelectionCustomRepository
                                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID))
                                .thenReturn(java.util.Arrays.asList(doc));
                when(mapper.convertValue(any(Document.class), eq(BundleSelection.class)))
                                .thenThrow(new IllegalArgumentException("conversion failed"));

                assertThatThrownBy(() -> service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, null, PLATFORM_ID_HEADER))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Failed to retrieve bundle selection");
        }

        @Test
        @DisplayName("Get bundle selection ignores blank evaluationId filter")
        void testGetBundleSelection_BlankEvaluationFilter_IsIgnored() {
                Document doc = new Document()
                                .append("evaluationId", "some-other-eval")
                                .append("featuredAs", FEATURED_AS_BALANCED)
                                .append("lastModifiedTimeStamp", new Date());

                BundleSelection bundleSelection = new BundleSelection();
                bundleSelection.setEvaluationId("some-other-eval");
                bundleSelection.setFeaturedAs(FEATURED_AS_BALANCED);

                when(bundleSelectionCustomRepository
                                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID))
                                .thenReturn(java.util.Arrays.asList(doc));
                when(mapper.convertValue(doc, BundleSelection.class)).thenReturn(bundleSelection);

                BundleSelection result = service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, "   ", PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getEvaluationId()).isEqualTo("some-other-eval");
                assertThat(result.getFeaturedAs()).isEqualTo(FEATURED_AS_BALANCED);
        }

        // --- NEW ---
        @Test
        @DisplayName("Get bundle selection picks the latest when multiple found (first from sorted list)")
        void testGetBundleSelection_MultiplePicksLatest() {
                Document latest = new Document().append("evaluationId", "latest").append("lastModifiedTimeStamp",
                                new Date());
                Document older = new Document().append("evaluationId", "older").append("lastModifiedTimeStamp",
                                new Date(0));

                BundleSelection mappedLatest = new BundleSelection();
                mappedLatest.setEvaluationId("latest");

                when(bundleSelectionCustomRepository
                                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID))
                                .thenReturn(Arrays.asList(latest, older));
                when(mapper.convertValue(eq(latest), eq(BundleSelection.class))).thenReturn(mappedLatest);

                BundleSelection result = service.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, null, PLATFORM_ID_HEADER);

                assertThat(result.getEvaluationId()).isEqualTo("latest");
                verify(mapper, times(1)).convertValue(eq(latest), eq(BundleSelection.class));
                verify(mapper, never()).convertValue(eq(older), eq(BundleSelection.class));
        }

        // ---------------------------
        // saveBundleSelection tests
        // ---------------------------

        @Test
        @DisplayName("Save bundle selection successfully - new document")
        void testSaveBundleSelection_NewDocument() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();

                // evaluation exists with bundles
                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                EvaluationOutput evalOutput = new EvaluationOutput();
                evalOutput.setBundles(java.util.List.of(new Bundle()));
                evalDoc.setEvaluationOutput(evalOutput);
                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenReturn(Optional.of(evalDoc));

                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString()))
                                .thenReturn(Optional.empty());
                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
                assertThat(result.getPlatformInternalId()).isEqualTo(PLATFORM_INTERNAL_ID);
                assertThat(result.getFeaturedAs()).isEqualTo(FEATURED_AS_BALANCED);

                verify(bundleSelectionCustomRepository).saveDocument(docCaptor.capture());
                Document persisted = docCaptor.getValue();
                assertThat(persisted.get("lastModifiedTimeStamp")).isInstanceOf(Date.class);
        }

        @Test
        @DisplayName("Save bundle selection successfully - update existing document")
        void testSaveBundleSelection_UpdateExisting() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();
                Document existingDoc = new Document().append("_id", "existing-id");

                // evaluation exists with bundles
                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                EvaluationOutput evalOutput = new EvaluationOutput();
                evalOutput.setBundles(java.util.List.of(new Bundle()));
                evalDoc.setEvaluationOutput(evalOutput);
                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenReturn(Optional.of(evalDoc));

                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString()))
                                .thenReturn(Optional.of(existingDoc));
                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
                assertThat(result.getFeaturedAs()).isEqualTo(FEATURED_AS_BALANCED);

                verify(bundleSelectionCustomRepository).updateDocument(eq("existing-id"), docCaptor.capture());
                Document persisted = docCaptor.getValue();
                assertThat(persisted.get("lastModifiedTimeStamp")).isInstanceOf(Date.class);
                assertThat(persisted.getString("_id")).isEqualTo("existing-id");
        }

        @Test
        @DisplayName("Save bundle selection when evaluation exists with bundles")
        void testSaveBundleSelection_WithExistingEvaluation() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setEvaluationId(EVALUATION_ID);

                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                EvaluationOutput evalOutput = new EvaluationOutput();
                evalOutput.setBundles(java.util.List.of(new Bundle()));
                evalDoc.setEvaluationOutput(evalOutput);

                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenReturn(Optional.of(evalDoc));
                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString()))
                                .thenReturn(Optional.empty());
                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getEvaluationId()).isEqualTo(EVALUATION_ID);
                assertThat(result.getFeaturedAs()).isEqualTo(FEATURED_AS_BALANCED);
                verify(evaluationMongoHelper).findByEvaluationId(EVALUATION_ID);
                verify(bundleSelectionCustomRepository).saveDocument(any(Document.class));
        }

        @Test
        @DisplayName("Save bundle selection throws BadRequestException when evaluationId not found")
        void testSaveBundleSelection_EvaluationNotFound() {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setEvaluationId(EVALUATION_ID);

                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Invalid evaluationId")
                                .hasMessageContaining("evaluation does not exist");

                verify(evaluationMongoHelper).findByEvaluationId(EVALUATION_ID);
                verify(bundleSelectionCustomRepository, never()).saveDocument(any());
                verify(bundleSelectionCustomRepository, never()).updateDocument(any(), any());
        }

        @Test
        @DisplayName("Save bundle selection throws BadRequestException when evaluation has no bundles")
        void testSaveBundleSelection_EvaluationHasNoBundles() {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setEvaluationId(EVALUATION_ID);

                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                EvaluationOutput evalOutput = new EvaluationOutput();
                evalOutput.setBundles(java.util.Collections.emptyList());
                evalDoc.setEvaluationOutput(evalOutput);

                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenReturn(Optional.of(evalDoc));

                assertThatThrownBy(() -> service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Invalid evaluationId")
                                .hasMessageContaining("has no bundles");

                verify(evaluationMongoHelper).findByEvaluationId(EVALUATION_ID);
                verify(bundleSelectionCustomRepository, never()).saveDocument(any());
                verify(bundleSelectionCustomRepository, never()).updateDocument(any(), any());
        }

        @Test
        @DisplayName("Save bundle selection throws BadRequestException when evaluation output is null")
        void testSaveBundleSelection_EvaluationOutputNull() {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setEvaluationId(EVALUATION_ID);

                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                evalDoc.setEvaluationOutput(null);

                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenReturn(Optional.of(evalDoc));

                assertThatThrownBy(() -> service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Invalid evaluationId");

                verify(evaluationMongoHelper).findByEvaluationId(EVALUATION_ID);
                verify(bundleSelectionCustomRepository, never()).saveDocument(any());
                verify(bundleSelectionCustomRepository, never()).updateDocument(any(), any());
        }

        @Test
        @DisplayName("Save bundle selection succeeds when evaluationId is null")
        void testSaveBundleSelection_EvaluationIdNull() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setEvaluationId(null);
                bundleSelection.setFeaturedAs("none"); // to bypass evaluation check

                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString()))
                                .thenReturn(Optional.empty());
                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getFeaturedAs()).isEqualTo("none");
                verify(evaluationMongoHelper, never()).findByEvaluationId(any());
                verify(bundleSelectionCustomRepository).saveDocument(any(Document.class));
        }

        @Test
        @DisplayName("Save bundle selection succeeds when evaluationId is blank with none featuredAs")
        void testSaveBundleSelection_EvaluationIdBlank() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setEvaluationId("  ");
                bundleSelection.setFeaturedAs("none"); // to bypass evaluation check

                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString()))
                                .thenReturn(Optional.empty());
                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getFeaturedAs()).isEqualTo("none");
                verify(evaluationMongoHelper, never()).findByEvaluationId(any());
                verify(bundleSelectionCustomRepository).saveDocument(any(Document.class));
        }

        @Test
        @DisplayName("Save bundle selection throws BadRequestException when businessProcessReferenceId is null")
        void testSaveBundleSelection_NullBusinessProcessReferenceId() {
                BundleSelection bundleSelection = new BundleSelection();
                bundleSelection.setBusinessProcessReferenceId(null);
                bundleSelection.setFeaturedAs(FEATURED_AS_BALANCED);

                assertThatThrownBy(() -> service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Business process reference ID is required");
        }

        @Test
        @DisplayName("Save bundle selection throws BadRequestException when businessProcessReferenceId is blank")
        void testSaveBundleSelection_BlankBusinessProcessReferenceId() {
                BundleSelection bundleSelection = new BundleSelection();
                bundleSelection.setBusinessProcessReferenceId("  ");
                bundleSelection.setFeaturedAs(FEATURED_AS_BALANCED);

                assertThatThrownBy(() -> service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Business process reference ID is required");
        }

        @Test
        @DisplayName("Save bundle selection throws RuntimeException when serialization fails")
        void testSaveBundleSelection_SerializationError() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();

                // evaluation exists with bundles
                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                EvaluationOutput evalOutput = new EvaluationOutput();
                evalOutput.setBundles(java.util.List.of(new Bundle()));
                evalDoc.setEvaluationOutput(evalOutput);
                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenReturn(Optional.of(evalDoc));

                ObjectMapper configuredMapper = mock(ObjectMapper.class);
                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(configuredMapper);
                when(configuredMapper.writeValueAsString(any()))
                                .thenThrow(new com.fasterxml.jackson.core.JsonProcessingException(
                                                "Serialization error") {
                                });

                assertThatThrownBy(() -> service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Failed to serialize bundle selection");
        }

        @Test
        @DisplayName("Save bundle selection throws BadRequestException when body clientId mismatches header")
        void testSaveBundleSelection_ClientIdMismatch() {
                BundleSelection bundleSelection = new BundleSelection();
                bundleSelection.setBusinessProcessReferenceId(BUSINESS_PROCESS_REF_ID);
                bundleSelection.setFeaturedAs(FEATURED_AS_BALANCED);
                bundleSelection.setClientId("DIFFERENT");

                assertThatThrownBy(() -> service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("clientId in request body must match clientId in request header");

                verifyNoInteractions(evaluationMongoHelper);
                verify(bundleSelectionCustomRepository, never()).saveDocument(any());
                verify(bundleSelectionCustomRepository, never()).updateDocument(any(), any());
        }

        @Test
        @DisplayName("Save bundle selection throws BadRequestException when body platformInternalId mismatches header")
        void testSaveBundleSelection_PlatformInternalIdMismatch() {
                BundleSelection bundleSelection = new BundleSelection();
                bundleSelection.setBusinessProcessReferenceId(BUSINESS_PROCESS_REF_ID);
                bundleSelection.setFeaturedAs(FEATURED_AS_BALANCED);
                bundleSelection.setPlatformInternalId("WRONG-PLATFORM-ID");

                assertThatThrownBy(() -> service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining(
                                                "platformInternalId in request body must match platformInternalId in request header");

                verifyNoInteractions(evaluationMongoHelper);
                verify(bundleSelectionCustomRepository, never()).saveDocument(any());
                verify(bundleSelectionCustomRepository, never()).updateDocument(any(), any());
        }

        @Test
        @DisplayName("Save bundle selection succeeds when body clientId and platformInternalId match header")
        void testSaveBundleSelection_BodyIdsMatch_Passes() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setClientId(CLIENT_ID);
                bundleSelection.setPlatformInternalId(PLATFORM_INTERNAL_ID);

                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString()))
                                .thenReturn(Optional.empty());
                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                EvaluationOutput evalOutput = new EvaluationOutput();
                evalOutput.setBundles(java.util.List.of(new Bundle()));
                evalDoc.setEvaluationOutput(evalOutput);
                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenReturn(Optional.of(evalDoc));

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
                assertThat(result.getPlatformInternalId()).isEqualTo(PLATFORM_INTERNAL_ID);
                assertThat(result.getFeaturedAs()).isEqualTo(FEATURED_AS_BALANCED);
                verify(bundleSelectionCustomRepository).saveDocument(any(Document.class));
        }

        @Test
        @DisplayName("Save bundle selection creates new document when existing is present but _id is null")
        void testSaveBundleSelection_ExistingDocNullId_SavesNew() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();

                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                EvaluationOutput evalOutput = new EvaluationOutput();
                evalOutput.setBundles(java.util.List.of(new Bundle()));
                evalDoc.setEvaluationOutput(evalOutput);
                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID))
                                .thenReturn(Optional.of(evalDoc));

                Document existingDocWithoutId = new Document(); // no _id
                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString()))
                                .thenReturn(Optional.of(existingDocWithoutId));

                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getFeaturedAs()).isEqualTo(FEATURED_AS_BALANCED);
                verify(bundleSelectionCustomRepository, never()).updateDocument(anyString(), any(Document.class));
                verify(bundleSelectionCustomRepository).saveDocument(any(Document.class));
        }

        @Test
        @DisplayName("Save (update path) with featuredAs=none should deactivate all evaluation and evaluation input")
        void testSaveBundleSelection_UpdateWithFeaturedNone_DeactivatesRelated() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setFeaturedAs(FEATURED_AS_NONE);

                // existing doc to force update path
                Document existing = new Document("_id", "id-1");
                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString())).thenReturn(Optional.of(existing));

                // evaluation input present
                EvaluationInputDocument inputDoc = EvaluationInputDocument.builder().id("in-1").build();
                inputDoc.setEvaluationId(EVALUATION_ID);
                List<EvaluationInputDocument> inputDocs = new ArrayList<>();
                inputDocs.add(inputDoc);
                when(evaluationInputMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString(), any())).thenReturn(inputDocs);

                // evaluation present

                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                evalDoc.setEvaluationOutput(new EvaluationOutput());
                evalDoc.getEvaluationOutput().setBundles(java.util.List.of(new Bundle()));
                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID)).thenReturn(Optional.of(evalDoc));

                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                verify(bundleSelectionCustomRepository).updateDocument(eq("id-1"), any(Document.class));

                // both should be saved with status Inactive
                verify(evaluationMongoHelper).updateDocumentStatusByEvaluationIds(
                                argThat(list -> list.contains(EVALUATION_ID)), eq("Inactive"));
                verify(evaluationInputMongoHelper).updateDocumentStatusByEvaluationIds(
                                argThat(list -> list.contains(EVALUATION_ID)), eq("Inactive"));
        }

        @Test
        @DisplayName("Save (update path) with featuredAs=balaced should deactivate all evaluation and evaluation input")
        void testSaveBundleSelection_UpdateWithFeaturedBalanced_DeactivatesRelated() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setFeaturedAs(FEATURED_AS_BALANCED);

                // existing doc to force update path
                Document existing = new Document("_id", "id-1");
                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString())).thenReturn(Optional.of(existing));

                // evaluation input present
                EvaluationInputDocument inputDoc = EvaluationInputDocument.builder().id("in-1").build();
                inputDoc.setEvaluationId(EVALUATION_ID);
                EvaluationInputDocument inputDoc2 = EvaluationInputDocument.builder().id("in-1").build();
                inputDoc2.setEvaluationId("eval-123");
                List<EvaluationInputDocument> inputDocs = new ArrayList<>();
                inputDocs.add(inputDoc);
                inputDocs.add(inputDoc2);
                when(evaluationInputMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString(), any())).thenReturn(inputDocs);

                // evaluation present

                EvaluationDocument evalDoc = new EvaluationDocument();
                evalDoc.setEvaluationId(EVALUATION_ID);
                evalDoc.setEvaluationOutput(new EvaluationOutput());
                evalDoc.getEvaluationOutput().setBundles(java.util.List.of(new Bundle()));
                when(evaluationMongoHelper.findByEvaluationId(EVALUATION_ID)).thenReturn(Optional.of(evalDoc));

                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                verify(bundleSelectionCustomRepository).updateDocument(eq("id-1"), any(Document.class));

                // both should be saved with status Inactive
                verify(evaluationMongoHelper).updateDocumentStatusByEvaluationIds(
                                argThat(list -> !list.contains(EVALUATION_ID)), eq("Inactive"));
                verify(evaluationInputMongoHelper).updateDocumentStatusByEvaluationIds(
                                argThat(list -> !list.contains(EVALUATION_ID)), eq("Inactive"));
        }

        @Test
        @DisplayName("Save with featuredAs=none and null evaluationId should not throw and should not save related docs")
        void testSaveBundleSelection_FeaturedNone_NullEvaluationId_NoThrows() throws Exception {
                BundleSelection bundleSelection = createValidBundleSelection();
                bundleSelection.setEvaluationId(null); // null evaluationId
                bundleSelection.setFeaturedAs(FEATURED_AS_NONE); // triggers the 'none' branch

                when(bundleSelectionMongoHelper.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString())).thenReturn(Optional.empty());
                when(mapper.configure(any(DeserializationFeature.class), anyBoolean())).thenReturn(mapper);
                when(mapper.writeValueAsString(any())).thenReturn("{}");

                BundleSelection result = service.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                verify(evaluationMongoHelper, never()).save(any());
                verify(evaluationInputMongoHelper, never()).save(any());
        }

        // ---------------------------
        // Helpers
        // ---------------------------

        private BundleSelection createValidBundleSelection() {
                BundleSelection bundleSelection = new BundleSelection();
                bundleSelection.setBusinessProcessReferenceId(BUSINESS_PROCESS_REF_ID);
                bundleSelection.setFeaturedAs(FEATURED_AS_BALANCED);
                bundleSelection.setEvaluationId(EVALUATION_ID);
                return bundleSelection;
        }
}
