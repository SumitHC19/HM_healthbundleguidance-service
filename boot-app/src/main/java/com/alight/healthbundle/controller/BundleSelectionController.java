package com.alight.healthbundle.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.model.BundleSelection;
import com.alight.healthbundle.service.BundleSelectionService;

/**
 * REST controller for bundle selection operations.
 * Provides GET and PUT endpoints for managing user bundle selections.
 */
@RestController
@RequestMapping("/api/bundle/selection")
@RequiredArgsConstructor
public class BundleSelectionController {

        private static final Logger log = LoggerFactory.getLogger(BundleSelectionController.class);

        private final BundleSelectionService bundleSelectionService;

        /**
         * Retrieve bundle selection by business process reference ID.
         * clientId is extracted from alightRequestHeader.
         * platformInternalId is extracted from token if parsable, otherwise from
         * platformInternalId request header.
         */
        @GetMapping
        public ResponseEntity<?> getBundleSelection(
                        @RequestHeader(value = "alightRequestHeader") String alightRequestHeader,
                        @RequestHeader(value = "alightPersonSessionToken") String alightPersonSessionToken,
                        @RequestHeader(value = "platformInternalId") String platformInternalIdHeader,
                        @RequestParam(value = "businessProcessReferenceId") String businessProcessReferenceId,
                        @RequestParam(value = "evaluationId", required = false) String evaluationId) {

                log.info(
                                "Received GET bundle selection request - businessProcessReferenceId: {}, evaluationId: {}, platformInternalId header: {}",
                                businessProcessReferenceId, evaluationId, platformInternalIdHeader);
                log.debug("Request headers - alightRequestHeader present: {}, alightPersonSessionToken present: {}",
                                alightRequestHeader != null && !alightRequestHeader.isBlank(),
                                alightPersonSessionToken != null && !alightPersonSessionToken.isBlank());

                try {
                        BundleSelection response = bundleSelectionService.getBundleSelection(
                                        alightPersonSessionToken, alightRequestHeader, businessProcessReferenceId,
                                        evaluationId, platformInternalIdHeader);

                        log.info("Successfully retrieved bundle selection for businessProcessReferenceId: {}, evaluationId: {}",
                                        businessProcessReferenceId, response.getEvaluationId());
                        log.debug("Response contains clientId: {}, featuredAs: {}",
                                        response.getClientId(), response.getFeaturedAs());

                        return ResponseEntity.ok(response);

                } catch (NoObjectFoundException e) {
                        log.info(
                                        "Bundle selection not found - businessProcessReferenceId: {}, evaluationId: {}",
                                        businessProcessReferenceId, evaluationId);
                        return ResponseEntity.status(204)
                                        .body(java.util.Map.of("message", "Bundle selection not found"));

                } catch (Exception e) {
                        log.error(
                                        "Unexpected error retrieving bundle selection - businessProcessReferenceId: {}, error: {}",
                                        businessProcessReferenceId, e.getMessage(), e);
                        throw e;
                }
        }

        /**
         * Save or update bundle selection.
         * clientId is extracted from alightRequestHeader.
         * platformInternalId is extracted from token if parsable, otherwise from
         * platformInternalId request header.
         */
        @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<BundleSelection> putBundleSelection(
                        @RequestHeader(value = "alightRequestHeader") String alightRequestHeader,
                        @RequestHeader(value = "alightPersonSessionToken") String alightPersonSessionToken,
                        @RequestHeader(value = "platformInternalId") String platformInternalIdHeader,
                        @Valid @RequestBody BundleSelection bundleSelection) {

                log.info(
                                "Received PUT bundle selection request - businessProcessReferenceId: {}, evaluationId: {}, platformInternalId header: {}",
                                bundleSelection.getBusinessProcessReferenceId(), bundleSelection.getEvaluationId(),
                                platformInternalIdHeader);
                log.debug("Request details - clientId: {}, featuredAs: {}",
                                bundleSelection.getClientId(), bundleSelection.getFeaturedAs());

                try {
                        BundleSelection response = bundleSelectionService.saveBundleSelection(
                                        bundleSelection, alightPersonSessionToken, alightRequestHeader,
                                        platformInternalIdHeader);

                        log.info(
                                        "Successfully saved bundle selection - businessProcessReferenceId: {}, evaluationId: {}",
                                        response.getBusinessProcessReferenceId(), response.getEvaluationId());
                        log.debug("Saved bundle selection lastModifiedTimeStamp: {}",
                                        response.getLastModifiedTimeStamp());

                        return ResponseEntity.ok(response);

                } catch (com.alight.healthbundle.exceptions.BadRequestException e) {
                        log.warn("Bad request for bundle selection - businessProcessReferenceId: {}, error: {}",
                                        bundleSelection.getBusinessProcessReferenceId(), e.getMessage());
                        throw e;
                } catch (Exception e) {
                        log.error(
                                        "Unexpected error saving bundle selection - businessProcessReferenceId: {}, error: {}",
                                        bundleSelection.getBusinessProcessReferenceId(), e.getMessage(), e);
                        throw e;
                }
        }
}
