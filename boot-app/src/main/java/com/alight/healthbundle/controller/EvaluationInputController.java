package com.alight.healthbundle.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.model.EvaluationInputRequest;
import com.alight.healthbundle.service.EvaluationInputService;

/**
 * REST controller for evaluation input operations.
 * Provides GET and PUT endpoints for managing SAVVI evaluation input data.
 */
@RestController
@RequestMapping("api/bundle/evaluationinput")
@RequiredArgsConstructor
public class EvaluationInputController {

        private static final Logger log = LoggerFactory.getLogger(EvaluationInputController.class);

        private final EvaluationInputService service;

        /**
         * Retrieve evaluation input by business process reference ID.
         * clientId is extracted from alightRequestHeader.
         * platformInternalId is extracted from token if parsable, otherwise from
         * platformInternalId request header.
         */
        @GetMapping
        public ResponseEntity<?> getEvaluationInput(
                        @RequestHeader(value = "alightRequestHeader") String alightRequestHeader,
                        @RequestHeader(value = "alightPersonSessionToken") String alightPersonSessionToken,
                        @RequestHeader(value = "platformInternalId", required = false) String platformInternalIdHeader,
                        @RequestParam(value = "businessProcessReferenceId") String businessProcessReferenceId,
                        @RequestParam(value = "evaluationid", required = false) String evaluationId) {

                log.info(
                                "Received GET evaluation input request - businessProcessReferenceId: {}, evaluationId: {}, platformInternalId header: {}",
                                businessProcessReferenceId, evaluationId, platformInternalIdHeader);
                log.info("Request header " + alightRequestHeader);
                log.debug("Request headers - alightRequestHeader present: {}, alightPersonSessionToken present: {}",
                                alightRequestHeader != null && !alightRequestHeader.isBlank(),
                                alightPersonSessionToken != null && !alightPersonSessionToken.isBlank());

                try {
                        EvaluationInputRequest response = service.getEvaluationInput(
                                        alightPersonSessionToken, alightRequestHeader, businessProcessReferenceId,
                                        evaluationId, platformInternalIdHeader);

                        log.info("Successfully retrieved evaluation input for businessProcessReferenceId: {}, evaluationId: {}",
                                        businessProcessReferenceId, response.getEvaluationId());
                        log.debug("Response contains clientId: {}, timestamp: {}",
                                        response.getClientId(), response.getTimestamp());

                        return ResponseEntity.ok(response);

                } catch (NoObjectFoundException e) {
                        log.info(
                                        "Evaluation input not found - businessProcessReferenceId: {}, evaluationId: {}",
                                        businessProcessReferenceId, evaluationId);
                        return ResponseEntity.status(204)
                                        .body(java.util.Map.of("message", "Evaluation input not found"));
                } catch (Exception e) {
                        log.error(
                                        "Unexpected error retrieving evaluation input - businessProcessReferenceId: {}, error: {}",
                                        businessProcessReferenceId, e.getMessage(), e);
                        throw e;
                }
        }

        /**
         * Save or update evaluation input.
         * clientId is extracted from alightRequestHeader.
         * platformInternalId is extracted from token if parsable, otherwise from
         * platformInternalId request header.
         */
        @PutMapping
        public ResponseEntity<EvaluationInputRequest> putEvaluationInput(
                        @RequestHeader(value = "alightRequestHeader") String alightRequestHeader,
                        @RequestHeader(value = "alightPersonSessionToken") String alightPersonSessionToken,
                        @RequestHeader(value = "platformInternalId", required = false) String platformInternalIdHeader,
                        @Valid @RequestBody EvaluationInputRequest request) {

                log.info(
                                "Received PUT evaluation input request - businessProcessReferenceId: {}, evaluationId: {}, platformInternalId header: {}",
                                request.getBusinessProcessReferenceId(), request.getEvaluationId(),
                                platformInternalIdHeader);
                log.debug("Request details - clientId: {}, timestamp: {}, hasSAVVIRequest: {}",
                                request.getClientId(), request.getTimestamp(), request.getSavviRequest() != null);

                try {
                        EvaluationInputRequest response = service.saveEvaluationInput(
                                        request, alightPersonSessionToken, alightRequestHeader,
                                        platformInternalIdHeader);

                        log.info(
                                        "Successfully saved evaluation input - businessProcessReferenceId: {}, evaluationId: {}, documentId: {}",
                                        response.getBusinessProcessReferenceId(), response.getEvaluationId(),
                                        response.getUpdatedAt() != null ? "updated" : "created");
                        log.debug("Saved evaluation input savedAt: {}, updatedAt: {}",
                                        response.getSavedAt(), response.getUpdatedAt());

                        return ResponseEntity.ok(response);

                } catch (com.alight.healthbundle.exceptions.BadRequestException e) {
                        log.warn("Bad request for evaluation input - businessProcessReferenceId: {}, error: {}",
                                        request.getBusinessProcessReferenceId(), e.getMessage());
                        throw e;
                } catch (Exception e) {
                        log.error(
                                        "Unexpected error saving evaluation input - businessProcessReferenceId: {}, error: {}",
                                        request.getBusinessProcessReferenceId(), e.getMessage(), e);
                        throw e;
                }
        }
}
