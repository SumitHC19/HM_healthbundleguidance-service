package com.alight.healthbundle.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alight.healthbundle.exceptions.BadRequestException;
import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.model.EvaluationResultsResponse;
import com.alight.healthbundle.service.BundleGuidanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bundle")
public class BundleGuidanceController {

    private static final Logger logger = LoggerFactory.getLogger(BundleGuidanceController.class);

    private final BundleGuidanceService bundleGuidanceService;

    public BundleGuidanceController(BundleGuidanceService bundleGuidanceService) {
        this.bundleGuidanceService = bundleGuidanceService;
    }

    @GetMapping(value = "/recommendations/{evaluationid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRecommendations(
            @PathVariable("evaluationid") String evaluationId,
            @RequestHeader String alightRequestHeader,
            @RequestHeader String alightPersonSessionToken) {

        logger.info("Received GET recommendations request for evaluationId: {}", evaluationId);

        if (evaluationId == null || "null".equalsIgnoreCase(evaluationId) || evaluationId.isBlank()) {
            logger.warn("Invalid evaluationId in GET request: {}", evaluationId);
            throw new BadRequestException("evaluationId must not be 'null' or 'Empty'");
        }

        try {
            EvaluationResultsResponse evaluationResultsResponse = bundleGuidanceService
                    .getRecommendations(evaluationId, alightRequestHeader, alightPersonSessionToken);

            logger.info("Successfully retrieved recommendations for evaluationId: {}", evaluationId);
            return ResponseEntity.ok(evaluationResultsResponse);
        } catch (NoObjectFoundException e) {
            logger.info(
                    "Recommendations not found for evaluationId: {}", evaluationId);
            return ResponseEntity.status(204)
                    .body(java.util.Map.of("message", "Recommendations not found"));

        }
    }

    @PutMapping(value = "/recommendations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveRecommendations(
            @Valid @RequestBody EvaluationResultsResponse evaluationResults,
            @RequestHeader(required = false) String alightRequestHeader,
            @RequestHeader(required = false) String alightPersonSessionToken) {

        logger.info("Received PUT recommendations request for evaluationId: {}", evaluationResults.getEvaluationId());
        String evaluationSuccess = "success";
        // Validate evaluationId is present in body
        if (evaluationResults.getEvaluationId() == null || evaluationResults.getEvaluationId().isBlank()) {
            throw new BadRequestException("evaluationId must not be null or empty");
        }

        // Validate bundles is not empty for success status
        if (evaluationSuccess.equalsIgnoreCase(evaluationResults.getEvaluationStatus())
                && evaluationResults.getEvaluationOutput().getBundles().isEmpty()) {
            throw new BadRequestException(
                    "evaluationOutput.bundles must contain at least one bundle for successful evaluations");
        }

        EvaluationResultsResponse savedResponse = bundleGuidanceService
                .saveRecommendations(evaluationResults, alightRequestHeader, alightPersonSessionToken);

        logger.info("Successfully saved recommendations for evaluationId: {}", evaluationResults.getEvaluationId());
        return ResponseEntity.ok(savedResponse);
    }

}
