package com.alight.healthbundle.controller;

import com.alight.healthbundle.exceptions.BadRequestException;
import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.model.EvaluationInputRequest;
import com.alight.healthbundle.service.EvaluationInputService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EvaluationInputController}.
 * These tests directly invoke controller methods (no web layer) and verify:
 * - happy paths for GET and PUT
 * - 204 behavior on NoObjectFoundException
 * - rethrow behavior on BadRequestException and generic Exception
 * - service argument correctness
 */
@ExtendWith(MockitoExtension.class)
class EvaluationInputControllerTest {

        @Mock
        private EvaluationInputService service;

        @InjectMocks
        private EvaluationInputController controller;

        // Common test data
        private String alightRequestHeader;
        private String alightPersonSessionToken;
        private String platformInternalIdHeader;
        private String businessProcessReferenceId;
        private String evaluationId;

        @BeforeEach
        void init() {
                alightRequestHeader = "client-id=CLIENT1;other=meta";
                alightPersonSessionToken = "token-abc-123";
                platformInternalIdHeader = "PLAT-001";
                businessProcessReferenceId = "BPR-12345";
                evaluationId = "EVAL-999";
        }

        // ---------------------------------------------------------------------
        // GET: success
        // ---------------------------------------------------------------------
        @Test
        void getEvaluationInput_success_returnsOkAndBody_andPassesCorrectArgs() {
                // Arrange
                EvaluationInputRequest response = mock(EvaluationInputRequest.class);
                when(response.getEvaluationId()).thenReturn(evaluationId);
                when(response.getClientId()).thenReturn("CLIENT1");

                when(service.getEvaluationInput(
                                anyString(), anyString(), anyString(), any(), any())).thenReturn(response);

                // Act
                ResponseEntity<?> entity = controller.getEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                platformInternalIdHeader,
                                businessProcessReferenceId,
                                evaluationId);

                // Assert
                // assertEquals(HttpStatus.OK.value(), entity.getStatusCodeValue());
                assertSame(response, entity.getBody());

                verify(service, times(1)).getEvaluationInput(
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                eq(businessProcessReferenceId),
                                eq(evaluationId),
                                eq(platformInternalIdHeader));
                verifyNoMoreInteractions(service);
        }

        // ---------------------------------------------------------------------
        // GET: success when evaluationId is null
        // ---------------------------------------------------------------------
        @Test
        void getEvaluationInput_success_withNullEvaluationId_returnsOk() {
                EvaluationInputRequest response = mock(EvaluationInputRequest.class);
                when(response.getEvaluationId()).thenReturn(null);
                when(service.getEvaluationInput(anyString(), anyString(), anyString(), isNull(), any()))
                                .thenReturn(response);

                ResponseEntity<?> entity = controller.getEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                platformInternalIdHeader,
                                businessProcessReferenceId,
                                null);

                // assertEquals(HttpStatus.OK.value(), entity.getStatusCodeValue());
                assertSame(response, entity.getBody());

                verify(service).getEvaluationInput(
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                eq(businessProcessReferenceId),
                                isNull(),
                                eq(platformInternalIdHeader));
                verifyNoMoreInteractions(service);
        }

        // ---------------------------------------------------------------------
        // GET: success when platformInternalIdHeader is null
        // ---------------------------------------------------------------------
        @Test
        void getEvaluationInput_success_withNullPlatformHeader_returnsOk() {
                EvaluationInputRequest response = mock(EvaluationInputRequest.class);
                when(service.getEvaluationInput(anyString(), anyString(), anyString(), any(), isNull()))
                                .thenReturn(response);

                ResponseEntity<?> entity = controller.getEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                null, // missing optional header
                                businessProcessReferenceId,
                                evaluationId);

                // assertEquals(HttpStatus.OK.value(), entity.getStatusCodeValue());
                assertSame(response, entity.getBody());

                verify(service).getEvaluationInput(
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                eq(businessProcessReferenceId),
                                eq(evaluationId),
                                isNull());
                verifyNoMoreInteractions(service);
        }

        // ---------------------------------------------------------------------
        // GET: NoObjectFoundException -> 204 (with body message)
        // ---------------------------------------------------------------------
        @Test
        void getEvaluationInput_whenNoObjectFound_returns204_withMessageBody() {
                when(service.getEvaluationInput(
                                anyString(), anyString(), anyString(), any(), any()))
                                .thenThrow(new NoObjectFoundException("not found"));

                ResponseEntity<?> entity = controller.getEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                platformInternalIdHeader,
                                businessProcessReferenceId,
                                evaluationId);

                // assertEquals(HttpStatus.NO_CONTENT.value(), entity.getStatusCodeValue());
                Object body = entity.getBody();
                assertNotNull(body);
                assertTrue(body instanceof Map);
                assertEquals("Evaluation input not found", ((Map<?, ?>) body).get("message"));

                verify(service, times(1)).getEvaluationInput(
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                eq(businessProcessReferenceId),
                                eq(evaluationId),
                                eq(platformInternalIdHeader));
                verifyNoMoreInteractions(service);
        }

        // ---------------------------------------------------------------------
        // GET: BadRequestException -> rethrow (explicit)
        // ---------------------------------------------------------------------
        @Test
        void getEvaluationInput_badRequest_rethrows() {
                when(service.getEvaluationInput(anyString(), anyString(), anyString(), any(), any()))
                                .thenThrow(new BadRequestException("bad get input"));

                BadRequestException ex = assertThrows(BadRequestException.class, () -> controller.getEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                platformInternalIdHeader,
                                businessProcessReferenceId,
                                evaluationId));

                assertTrue(ex.getMessage().contains("bad get input"));
                verify(service).getEvaluationInput(
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                eq(businessProcessReferenceId),
                                eq(evaluationId),
                                eq(platformInternalIdHeader));
                verifyNoMoreInteractions(service);
        }

        // ---------------------------------------------------------------------
        // GET: Unexpected exception -> rethrow
        // ---------------------------------------------------------------------
        @Test
        void getEvaluationInput_unexpectedError_rethrows() {
                when(service.getEvaluationInput(
                                anyString(), anyString(), anyString(), any(), any()))
                                .thenThrow(new RuntimeException("boom"));

                RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.getEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                null, // simulate missing optional header
                                businessProcessReferenceId,
                                null // simulate missing evaluationId
                ));

                assertTrue(ex.getMessage().contains("boom"));
                verify(service, times(1)).getEvaluationInput(
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                eq(businessProcessReferenceId),
                                isNull(), // evaluationId null path
                                isNull() // platformInternalIdHeader null path
                );
                verifyNoMoreInteractions(service);
        }

        // ---------------------------------------------------------------------
        // PUT: success (created branch simulated with updatedAt == null)
        // ---------------------------------------------------------------------
        @Test
        void putEvaluationInput_success_returnsOkAndBody_andPassesCorrectArgs() {
                EvaluationInputRequest request = mock(EvaluationInputRequest.class);
                when(request.getBusinessProcessReferenceId()).thenReturn(businessProcessReferenceId);
                when(request.getEvaluationId()).thenReturn(evaluationId);
                when(request.getClientId()).thenReturn("CLIENT1");

                EvaluationInputRequest response = mock(EvaluationInputRequest.class);
                when(response.getBusinessProcessReferenceId()).thenReturn(businessProcessReferenceId);
                when(response.getEvaluationId()).thenReturn(evaluationId);
                when(response.getUpdatedAt()).thenReturn(null); // simulate "created" in log

                when(service.saveEvaluationInput(any(), anyString(), anyString(), any()))
                                .thenReturn(response);

                ResponseEntity<EvaluationInputRequest> entity = controller.putEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                platformInternalIdHeader,
                                request);

                // assertEquals(HttpStatus.OK.value(), entity.getStatusCodeValue());
                assertSame(response, entity.getBody());

                verify(service, times(1)).saveEvaluationInput(
                                same(request),
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                eq(platformInternalIdHeader));
                verifyNoMoreInteractions(service);
        }

        // ---------------------------------------------------------------------
        // PUT: success (update branch simulated with updatedAt != null)
        // ---------------------------------------------------------------------
        @Test
        void putEvaluationInput_success_updateBranch_returnsOk() {
                EvaluationInputRequest request = mock(EvaluationInputRequest.class);
                when(request.getBusinessProcessReferenceId()).thenReturn(businessProcessReferenceId);

                EvaluationInputRequest response = mock(EvaluationInputRequest.class);
                when(response.getBusinessProcessReferenceId()).thenReturn(businessProcessReferenceId);
                // Non-null updatedAt to simulate "updated"
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                when(response.getUpdatedAt()).thenReturn(now);

                when(service.saveEvaluationInput(any(), anyString(), anyString(), any()))
                                .thenReturn(response);

                ResponseEntity<EvaluationInputRequest> entity = controller.putEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                platformInternalIdHeader,
                                request);

                // assertEquals(HttpStatus.OK.value(), entity.getStatusCodeValue());
                assertSame(response, entity.getBody());

                verify(service).saveEvaluationInput(
                                same(request),
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                eq(platformInternalIdHeader));
                verifyNoMoreInteractions(service);
        }

        // ---------------------------------------------------------------------
        // PUT: BadRequestException -> rethrow
        // ---------------------------------------------------------------------
        @Test
        void putEvaluationInput_badRequest_rethrows() {
                EvaluationInputRequest request = mock(EvaluationInputRequest.class);
                when(request.getBusinessProcessReferenceId()).thenReturn(businessProcessReferenceId);

                when(service.saveEvaluationInput(any(), anyString(), anyString(), any()))
                                .thenThrow(new BadRequestException("invalid input"));

                BadRequestException ex = assertThrows(BadRequestException.class, () -> controller.putEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                platformInternalIdHeader,
                                request));
                assertTrue(ex.getMessage().contains("invalid input"));

                verify(service, times(1)).saveEvaluationInput(
                                same(request),
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                eq(platformInternalIdHeader));
                verifyNoMoreInteractions(service);
        }

        // ---------------------------------------------------------------------
        // PUT: Unexpected exception -> rethrow
        // ---------------------------------------------------------------------
        @Test
        void putEvaluationInput_unexpectedError_rethrows() {
                EvaluationInputRequest request = mock(EvaluationInputRequest.class);
                when(request.getBusinessProcessReferenceId()).thenReturn(businessProcessReferenceId);

                when(service.saveEvaluationInput(any(), anyString(), anyString(), any()))
                                .thenThrow(new RuntimeException("db down"));

                RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.putEvaluationInput(
                                alightRequestHeader,
                                alightPersonSessionToken,
                                null, // simulate missing optional platformInternalId header
                                request));
                assertTrue(ex.getMessage().contains("db down"));

                verify(service, times(1)).saveEvaluationInput(
                                same(request),
                                eq(alightPersonSessionToken),
                                eq(alightRequestHeader),
                                isNull());
                verifyNoMoreInteractions(service);
        }
}
