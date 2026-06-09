package com.alight.healthbundle.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alight.healthbundle.dao.EvaluationInputDao;
import com.alight.healthbundle.exceptions.BadRequestException;
import com.alight.healthbundle.model.EvaluationInputRequest;
import com.alight.healthbundle.model.SAVVIRequest;
import com.alight.healthbundle.util.RequestContext;
import com.alight.healthbundle.util.RequestContextExtractor;

@ExtendWith(MockitoExtension.class)
class EvaluationInputServiceTest {

        @Mock
        private EvaluationInputDao dao;

        @Mock
        private RequestContextExtractor requestContextExtractor;

        @InjectMocks
        private EvaluationInputService service;

        private static final String CLIENT_ID = "19968";
        private static final String PLATFORM_INTERNAL_ID = "12853765";
        private static final String BUSINESS_PROCESS_REF_ID = "bp-123";
        private static final String EVALUATION_ID = "eval-456";
        private static final String TOKEN = "test-token";
        private static final String REQUEST_HEADER = "{\"clientId\":\"19968\"}";
        private static final String PLATFORM_ID_HEADER = "header-platform-id";

        @BeforeEach
        void setUp() {
                RequestContext context = new RequestContext(CLIENT_ID, PLATFORM_INTERNAL_ID);
                lenient().when(requestContextExtractor.extractRequestContext(any(), any(), any())).thenReturn(context);
        }

        @Test
        @DisplayName("Get evaluation input successfully")
        void testGetEvaluationInput_Success() {
                EvaluationInputRequest expectedRequest = createValidEvaluationInputRequest();

                when(dao.getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID),
                                eq(EVALUATION_ID)))
                                .thenReturn(expectedRequest);

                EvaluationInputRequest result = service.getEvaluationInput(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
                assertThat(result.getPlatformInternalId()).isEqualTo(PLATFORM_INTERNAL_ID);
                assertThat(result.getBusinessProcessReferenceId()).isEqualTo(BUSINESS_PROCESS_REF_ID);
                verify(dao).getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID, EVALUATION_ID);
        }

        @Test
        @DisplayName("Get evaluation input with null evaluationId")
        void testGetEvaluationInput_NullEvaluationId() {
                EvaluationInputRequest expectedRequest = createValidEvaluationInputRequest();

                when(dao.getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                eq(CLIENT_ID), eq(PLATFORM_INTERNAL_ID), eq(BUSINESS_PROCESS_REF_ID), isNull()))
                                .thenReturn(expectedRequest);

                EvaluationInputRequest result = service.getEvaluationInput(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, null, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                verify(dao).getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                CLIENT_ID, PLATFORM_INTERNAL_ID, BUSINESS_PROCESS_REF_ID, null);
        }

        @Test
        @DisplayName("Get evaluation input throws BadRequestException when businessProcessReferenceId is null")
        void testGetEvaluationInput_NullBusinessProcessReferenceId() {
                assertThatThrownBy(() -> service.getEvaluationInput(
                                TOKEN, REQUEST_HEADER, null, EVALUATION_ID, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Business process reference ID is required");
        }

        @Test
        @DisplayName("Get evaluation input throws BadRequestException when businessProcessReferenceId is blank")
        void testGetEvaluationInput_BlankBusinessProcessReferenceId() {
                assertThatThrownBy(() -> service.getEvaluationInput(
                                TOKEN, REQUEST_HEADER, "  ", EVALUATION_ID, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("Business process reference ID is required");
        }

        @Test
        @DisplayName("Get evaluation input rethrows exception from DAO")
        void testGetEvaluationInput_DaoException() {
                when(dao.getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                anyString(), anyString(), anyString(), anyString()))
                                .thenThrow(new RuntimeException("Database error"));

                assertThatThrownBy(() -> service.getEvaluationInput(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID, PLATFORM_ID_HEADER))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Database error");
        }

        @Test
        @DisplayName("Save evaluation input successfully")
        void testSaveEvaluationInput_Success() {
                EvaluationInputRequest request = createValidEvaluationInputRequest();
                EvaluationInputRequest savedRequest = createValidEvaluationInputRequest();

                when(dao.saveEvaluationInput(any(EvaluationInputRequest.class), eq(PLATFORM_INTERNAL_ID)))
                                .thenReturn(savedRequest);

                EvaluationInputRequest result = service.saveEvaluationInput(
                                request, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(result.getClientId()).isEqualTo(CLIENT_ID);
                assertThat(result.getPlatformInternalId()).isEqualTo(PLATFORM_INTERNAL_ID);
                verify(dao).saveEvaluationInput(any(EvaluationInputRequest.class), eq(PLATFORM_INTERNAL_ID));
        }

        @Test
        @DisplayName("Save evaluation input sets clientId and platformInternalId from context")
        void testSaveEvaluationInput_SetsContextValues() {
                EvaluationInputRequest request = createValidEvaluationInputRequest();
                request.setClientId(null);
                request.setPlatformInternalId(null);
                EvaluationInputRequest savedRequest = createValidEvaluationInputRequest();

                when(dao.saveEvaluationInput(any(EvaluationInputRequest.class), eq(PLATFORM_INTERNAL_ID)))
                                .thenReturn(savedRequest);

                EvaluationInputRequest result = service.saveEvaluationInput(
                                request, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);

                assertThat(result).isNotNull();
                assertThat(request.getClientId()).isEqualTo(CLIENT_ID);
                assertThat(request.getPlatformInternalId()).isEqualTo(PLATFORM_INTERNAL_ID);
        }

        @Test
        @DisplayName("Save evaluation input throws BadRequestException when businessProcessReferenceId is null")
        void testSaveEvaluationInput_NullBusinessProcessReferenceId() {
                EvaluationInputRequest request = new EvaluationInputRequest();
                request.setBusinessProcessReferenceId(null);
                request.setSavviRequest(new SAVVIRequest());

                assertThatThrownBy(() -> service.saveEvaluationInput(
                                request, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("businessProcessReferenceId is required");
        }

        @Test
        @DisplayName("Save evaluation input throws BadRequestException when businessProcessReferenceId is blank")
        void testSaveEvaluationInput_BlankBusinessProcessReferenceId() {
                EvaluationInputRequest request = new EvaluationInputRequest();
                request.setBusinessProcessReferenceId("  ");
                request.setSavviRequest(new SAVVIRequest());

                assertThatThrownBy(() -> service.saveEvaluationInput(
                                request, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("businessProcessReferenceId is required");
        }

        @Test
        @DisplayName("Save evaluation input throws BadRequestException when SAVVIRequest is null")
        void testSaveEvaluationInput_NullSAVVIRequest() {
                EvaluationInputRequest request = new EvaluationInputRequest();
                request.setBusinessProcessReferenceId(BUSINESS_PROCESS_REF_ID);
                request.setSavviRequest(null);

                assertThatThrownBy(() -> service.saveEvaluationInput(
                                request, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("savviRequest is required");
        }

        @Test
        @DisplayName("Save evaluation input rethrows BadRequestException from DAO")
        void testSaveEvaluationInput_DaoBadRequestException() {
                EvaluationInputRequest request = createValidEvaluationInputRequest();

                when(dao.saveEvaluationInput(any(EvaluationInputRequest.class), anyString()))
                                .thenThrow(new BadRequestException("DAO validation error"));

                assertThatThrownBy(() -> service.saveEvaluationInput(
                                request, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("DAO validation error");
        }

        @Test
        @DisplayName("Save evaluation input rethrows generic exception from DAO")
        void testSaveEvaluationInput_DaoGenericException() {
                EvaluationInputRequest request = createValidEvaluationInputRequest();

                when(dao.saveEvaluationInput(any(EvaluationInputRequest.class), anyString()))
                                .thenThrow(new RuntimeException("Database error"));

                assertThatThrownBy(() -> service.saveEvaluationInput(
                                request, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Database error");
        }

        private EvaluationInputRequest createValidEvaluationInputRequest() {
                EvaluationInputRequest request = new EvaluationInputRequest();
                request.setClientId(CLIENT_ID);
                request.setPlatformInternalId(PLATFORM_INTERNAL_ID);
                request.setBusinessProcessReferenceId(BUSINESS_PROCESS_REF_ID);
                request.setEvaluationId(EVALUATION_ID);
                request.setSavviRequest(new SAVVIRequest());
                return request;
        }
}
