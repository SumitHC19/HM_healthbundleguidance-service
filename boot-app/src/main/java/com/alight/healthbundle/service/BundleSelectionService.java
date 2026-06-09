package com.alight.healthbundle.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alight.healthbundle.document.EvaluationDocument;
import com.alight.healthbundle.document.EvaluationInputDocument;
import com.alight.healthbundle.exceptions.BadRequestException;
import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.helper.BundleSelectionCustomRepository;
import com.alight.healthbundle.helper.BundleSelectionMongoHelper;
import com.alight.healthbundle.helper.EvaluationInputMongoHelper;
import com.alight.healthbundle.helper.EvaluationMongoHelper;
import com.alight.healthbundle.model.BundleSelection;
import com.alight.healthbundle.model.enums.FeaturedAs;
import com.alight.healthbundle.util.RequestContext;
import com.alight.healthbundle.util.RequestContextExtractor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling bundle selection business logic.
 */
@Service
@RequiredArgsConstructor
public class BundleSelectionService {

        private static final Logger log = LoggerFactory.getLogger(BundleSelectionService.class);

        private final BundleSelectionMongoHelper bundleSelectionMongoHelper;
        private final BundleSelectionCustomRepository bundleSelectionCustomRepository;
        private final EvaluationMongoHelper evaluationMongoHelper;
        private final RequestContextExtractor requestContextExtractor;
        private final ObjectMapper mapper;
        private final EvaluationInputMongoHelper evaluationInputMongoHelper;

        private static final String featuredAsNone = "none";
        private static final String featuredAsError = "error";

        /**
         * Retrieve bundle selection using token-based or header-based
         * platformInternalId.
         * Extracts platformInternalId from token if parsable, otherwise from request
         * header.
         * Extracts clientId from alightRequestHeader JSON.
         *
         * If evaluationId is provided, search by evaluationId, clientId,
         * platformInternalId,
         * and businessProcessReferenceId.
         * If evaluationId is not provided, search by clientId, platformInternalId, and
         * businessProcessReferenceId, and if multiple results are found, pick the
         * latest by
         * lastModificationTime.
         */
        public BundleSelection getBundleSelection(
                        String token,
                        String requestHeader,
                        String businessProcessReferenceId,
                        String evaluationId,
                        String platformInternalIdHeader) {

                log.info(
                                "Service processing get bundle selection - businessProcessReferenceId: {}, evaluationId: {}, requestHeader present: {}, platformInternalId header: {}",
                                businessProcessReferenceId, evaluationId, requestHeader != null,
                                platformInternalIdHeader);

                if (businessProcessReferenceId == null || businessProcessReferenceId.isBlank()) {
                        log.warn("Validation failed: businessProcessReferenceId is null or blank");
                        throw new BadRequestException("Business process reference ID is required");
                }

                // Extract clientId and platformInternalId using common utility
                RequestContext context = requestContextExtractor.extractRequestContext(
                                token, requestHeader, platformInternalIdHeader);
                String clientId = context.getClientId();
                String platformInternalId = context.getPlatformInternalId();

                log.info("Extracted request context - clientId: {}, platformInternalId: {}",
                                clientId, platformInternalId);

                try {
                        Optional<Document> documentOpt;
                        List<Document> documents;

                        // Check if evaluationId is provided
                        if (evaluationId != null && !evaluationId.isBlank()) {
                                log.debug(
                                                "Querying by evaluationId - clientId: {}, platformInternalId: {}, businessProcessReferenceId: {}, evaluationId: {}",
                                                clientId, platformInternalId, businessProcessReferenceId, evaluationId);

                                documents = bundleSelectionCustomRepository
                                                .findByEvaluationIdClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                                evaluationId, clientId, platformInternalId,
                                                                businessProcessReferenceId);
                        } else {
                                // evaluationId is null/blank - search without evaluationId
                                log.debug(
                                                "evaluationId not provided, querying by clientId, platformInternalId, and businessProcessReferenceId - clientId: {}, platformInternalId: {}, businessProcessReferenceId: {}",
                                                clientId, platformInternalId, businessProcessReferenceId);

                                documents = bundleSelectionCustomRepository
                                                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                                                                clientId, platformInternalId,
                                                                businessProcessReferenceId);
                        }

                        // Pick the latest by lastModifiedTimeStamp in both cases
                        // Documents are already sorted by lastModifiedTimeStamp DESC from repository
                        if (documents.isEmpty()) {
                                documentOpt = Optional.empty();
                        } else {
                                documentOpt = Optional.of(documents.get(0)); // First is latest
                                if (documents.size() > 1) {
                                        log.debug("Multiple results found ({}), selecting latest by lastModifiedTimeStamp",
                                                        documents.size());
                                }
                        }

                        if (documentOpt.isEmpty()) {
                                log.info(
                                                "Bundle selection not found for clientId: {}, platformInternalId: {}, businessProcessReferenceId: {}, evaluationId: {}",
                                                clientId, platformInternalId, businessProcessReferenceId, evaluationId);
                                throw new NoObjectFoundException("Bundle selection not found");
                        }

                        Document document = documentOpt.get();
                        BundleSelection bundleSelection = mapper.convertValue(document, BundleSelection.class);

                        log.info("Successfully retrieved bundle selection - evaluationId: {}, featuredAs: {}",
                                        bundleSelection.getEvaluationId(), bundleSelection.getFeaturedAs());

                        return bundleSelection;

                } catch (NoObjectFoundException e) {
                        throw e;
                } catch (Exception e) {
                        log.error("Error retrieving bundle selection - clientId: {}, error: {}", clientId,
                                        e.getMessage(), e);
                        throw new RuntimeException("Failed to retrieve bundle selection", e);
                }
        }

        /**
         * Save or update bundle selection.
         * Extracts platformInternalId from token if parsable, otherwise from request
         * header.
         * Extracts clientId from request header.
         *
         * @param bundleSelection          the bundle selection request
         * @param token                    the session token
         * @param requestHeader            the request header (JSON) containing clientId
         * @param platformInternalIdHeader the platform internal ID from header
         *                                 (fallback)
         * @return the saved bundle selection
         */
        public BundleSelection saveBundleSelection(
                        BundleSelection bundleSelection,
                        String token,
                        String requestHeader,
                        String platformInternalIdHeader) {

                log.info(
                                "Service processing save bundle selection - businessProcessReferenceId: {}, evaluationId: {}, platformInternalId header: {}",
                                bundleSelection.getBusinessProcessReferenceId(), bundleSelection.getEvaluationId(),
                                platformInternalIdHeader);

                // Extract clientId and platformInternalId using common utility
                RequestContext context = requestContextExtractor.extractRequestContext(
                                token, requestHeader, platformInternalIdHeader);
                String clientId = context.getClientId();
                String platformInternalId = context.getPlatformInternalId();

                String bodyClientId = bundleSelection.getClientId();
                if (StringUtils.hasText(bodyClientId) && !Objects.equals(clientId, bodyClientId)) {
                        log.info("Validation failed: clientId in request body must match clientId in request header. header='{}', body='{}'",
                                        clientId, bodyClientId);
                        throw new BadRequestException(
                                        "clientId in request body must match clientId in request header");
                }

                String bodyPlatformInternalId = bundleSelection.getPlatformInternalId();
                if (StringUtils.hasText(bodyPlatformInternalId)
                                && !Objects.equals(platformInternalId, bodyPlatformInternalId)) {
                        log.info("Validation failed: platformInternalId in request body must match platformInternalId in request header. header='{}', body='{}'",
                                        platformInternalId, bodyPlatformInternalId);
                        throw new BadRequestException(
                                        "platformInternalId in request body must match platformInternalId in request header");
                }

                log.info("Extracted request context - clientId: {}, platformInternalId: {}",
                                clientId, platformInternalId);

                // Validate required fields
                if (bundleSelection.getBusinessProcessReferenceId() == null
                                || bundleSelection.getBusinessProcessReferenceId().isBlank()) {
                        log.info("Validation failed: businessProcessReferenceId in request body is null or blank");
                        throw new BadRequestException("Business process reference ID is required in request body");
                }

                if ((bundleSelection.getEvaluationId() == null || bundleSelection.getEvaluationId().isBlank())
                                && (!featuredAsNone.equalsIgnoreCase(bundleSelection.getFeaturedAs())
                                                && !featuredAsError
                                                                .equalsIgnoreCase(bundleSelection.getFeaturedAs()))) {
                        log.info("Validation failed: evaluationId in request body is required when featuredAs is not 'none' or 'error'");
                        throw new BadRequestException(
                                        "evaluationId in request body is required when featuredAs is not 'none' or 'error'");
                }

                // Set extracted values
                bundleSelection.setClientId(clientId);
                bundleSelection.setPlatformInternalId(platformInternalId);

                log.info("Validation passed, delegating to save logic - evaluationId: {}",
                                bundleSelection.getEvaluationId());

                // Validate that evaluationId exists in bundleguidance collection
                if (bundleSelection.getEvaluationId() != null && !bundleSelection.getEvaluationId().isBlank()) {
                        log.info("Validating evaluation exists for evaluationId: {}",
                                        bundleSelection.getEvaluationId());
                        Optional<EvaluationDocument> evaluation = evaluationMongoHelper
                                        .findByEvaluationId(bundleSelection.getEvaluationId())
                                        .filter(doc -> doc.getEvaluationOutput() != null
                                                        && !doc.getEvaluationOutput().getBundles().isEmpty());

                        if (evaluation.isEmpty()) {
                                log.info("Validation failed: Evaluation not found or has no bundles for evaluationId: {}",
                                                bundleSelection.getEvaluationId());
                                throw new BadRequestException(
                                                "Invalid evaluationId: evaluation does not exist or has no bundles for evaluationId: "
                                                                + bundleSelection.getEvaluationId());
                        }
                        log.info("Evaluation validated successfully for evaluationId: {}",
                                        bundleSelection.getEvaluationId());
                } else {
                        log.info("No evaluationId provided, skipping evaluation existence validation");
                }

                try {
                        Date timestamp = Date.from(Instant.now());
                        ObjectMapper configuredMapper = mapper
                                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        String jsonString = configuredMapper.writeValueAsString(bundleSelection);
                        Document newDocument = Document.parse(jsonString);
                        newDocument.put("lastModifiedTimeStamp", timestamp);

                        log.info("Checking for existing bundle selection - clientId: {}, platformInternalId: {}, businessProcessReferenceId: {}",
                                        clientId, platformInternalId, bundleSelection.getBusinessProcessReferenceId());

                        // Check if document already exists
                        Optional<Document> existing = bundleSelectionMongoHelper
                                        .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                                                        clientId,
                                                        platformInternalId,
                                                        bundleSelection.getBusinessProcessReferenceId());

                        if (existing.isPresent()) {
                                Object existingId = existing.get().get("_id");
                                if (existingId != null) {
                                        log.info("Updating existing bundle selection with id: {}", existingId);
                                        newDocument.put("_id", existingId);
                                        bundleSelectionCustomRepository.updateDocument(existingId.toString(),
                                                        newDocument);
                                        log.info("Successfully updated bundle selection for evaluationId: {}",
                                                        bundleSelection.getEvaluationId());

                                        updateUserEvaluationDocumentsAccrossSystem(clientId, platformInternalId,
                                                        bundleSelection.getBusinessProcessReferenceId(),
                                                        bundleSelection.getEvaluationId(),
                                                        bundleSelection.getFeaturedAs());

                                        return bundleSelection;
                                }
                        }

                        // Create new document
                        log.info("Creating new bundle selection document for businessProcessReferenceId: {}",
                                        bundleSelection.getBusinessProcessReferenceId());
                        bundleSelectionCustomRepository.saveDocument(newDocument);
                        log.info("Successfully created new bundle selection for businessProcessReferenceId: {}",
                                        bundleSelection.getBusinessProcessReferenceId());

                        updateUserEvaluationDocumentsAccrossSystem(clientId, platformInternalId,
                                        bundleSelection.getBusinessProcessReferenceId(),
                                        bundleSelection.getEvaluationId(),
                                        bundleSelection.getFeaturedAs());

                        return bundleSelection;

                } catch (IOException e) {
                        log.error("Failed to serialize bundle selection for businessProcessReferenceId: {}",
                                        bundleSelection.getBusinessProcessReferenceId(), e);
                        throw new RuntimeException("Failed to serialize bundle selection", e);
                }
        }

        private void updateUserEvaluationDocumentsAccrossSystem(String clientId, String platformInternalId,
                        String businessProcessReferenceId, String evaluationId, String featuredAs) {

                // Find all evaluation input documents for the given clientId,
                // platformInternalId, and businessProcessReferenceId
                List<EvaluationInputDocument> evaluationInputs = evaluationInputMongoHelper
                                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(clientId,
                                                platformInternalId, businessProcessReferenceId, null);

                if (evaluationInputs.isEmpty()) {
                        log.debug("No evaluation input documents found for clientId: {}, platformInternalId: {}, businessProcessReferenceId: {}",
                                        clientId, platformInternalId, businessProcessReferenceId);
                        return;
                }

                // extract evaluationIds for finding evaluation documents
                List<String> evaluationIds = null;

                // If featuredAs is none or error, set all evaluation input documents to
                // Inactive
                if (featuredAsNone.equalsIgnoreCase(featuredAs) || featuredAsError.equalsIgnoreCase(featuredAs)) {
                        evaluationIds = evaluationInputs.stream().map(EvaluationInputDocument::getEvaluationId)
                                        .toList();
                } else {
                        // Only set to Inactive for other evaluationIds if featuredAs is not none or
                        // error
                        evaluationIds = evaluationInputs.stream()
                                        .filter(input -> !evaluationId.equals(input.getEvaluationId()))
                                        .map(EvaluationInputDocument::getEvaluationId)
                                        .toList();
                }
                log.info("Evaluation input documents found for clientId: {}, platformInternalId: {}, businessProcessReferenceId: {} are:{}",
                                clientId, platformInternalId, businessProcessReferenceId, evaluationIds);

                String newStatus = "Inactive";
                try {
                        evaluationInputMongoHelper.updateDocumentStatusByEvaluationIds(evaluationIds, newStatus);
                        log.info("Successfully updated evaluation input documentStatus to {} for evaluationIds: {}",
                                        newStatus, evaluationIds);
                } catch (Exception e) {
                        log.error("Error updating evaluation input documentStatus to {} for evaluationIds: {}, error: {}",
                                        newStatus, evaluationIds, e.getMessage(), e);
                }

                try {
                        evaluationMongoHelper.updateDocumentStatusByEvaluationIds(evaluationIds, newStatus);
                        log.info("Successfully updated bundle guidance documentStatus to {} for evaluationIds: {}",
                                        newStatus, evaluationIds);
                } catch (Exception e) {
                        log.error("Error updating bundle guidance documentStatus to {} for evaluationIds: {}, error: {}",
                                        newStatus, evaluationIds, e.getMessage(), e);
                }

        }
}
