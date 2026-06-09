package com.alight.healthbundle.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.alight.healthbundle.document.EvaluationInputDocument;
import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.helper.EvaluationInputMongoHelper;
import com.alight.healthbundle.model.EvaluationInputRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * Data Access Object for evaluation input operations with MongoDB.
 */
@Component
@RequiredArgsConstructor
public class EvaluationInputDao {

    private static final Logger log = LoggerFactory.getLogger(EvaluationInputDao.class);

    private final EvaluationInputMongoHelper mongoHelper;
    private final ObjectMapper objectMapper;

    /**
     * Retrieve evaluation input by business process reference ID and platform
     * internal ID,
     * optionally filtered by evaluation ID.
     *
     * @param businessProcessReferenceId the business process reference ID
     * @param platformInternalId         the platform internal ID
     * @param evaluationId               optional evaluation ID filter
     * @return the evaluation input request
     * @throws NoObjectFoundException if not found
     */
    public EvaluationInputRequest getEvaluationInput(
            String businessProcessReferenceId,
            String platformInternalId,
            String evaluationId) {

        log.info("Fetching evaluation input - businessProcessReferenceId: {}, platformInternalId: {}, evaluationId: {}",
                businessProcessReferenceId, platformInternalId, evaluationId);
        log.debug("Query strategy - evaluationId provided: {}", evaluationId != null && !evaluationId.isBlank());

        try {
            Optional<EvaluationInputDocument> document;

            if (evaluationId != null && !evaluationId.isBlank()) {
                // If evaluation ID is provided, use it as the primary filter
                log.debug("Querying by evaluationId: {}", evaluationId);
                document = mongoHelper.findByEvaluationId(evaluationId);
            } else {
                // Otherwise use business process reference ID and platform internal ID
                log.debug("Querying by businessProcessReferenceId and platformInternalId");
                document = mongoHelper.findByBusinessProcessReferenceIdAndPlatformInternalId(
                        businessProcessReferenceId, platformInternalId);
            }

            if (document.isPresent()) {
                log.debug("Document found with id: {}", document.get().getId());
                return mapToResponse(document.get());
            } else {
                log.debug("No document found matching the query criteria");
                throw new NoObjectFoundException(
                        String.format(
                                "Evaluation input not found for businessProcessReferenceId: %s, platformInternalId: %s, evaluationId: %s",
                                businessProcessReferenceId, platformInternalId, evaluationId));
            }

        } catch (NoObjectFoundException e) {
            // Re-throw NoObjectFoundException as-is
            throw e;
        } catch (Exception e) {
            log.error("Error querying MongoDB for evaluation input - businessProcessReferenceId: {}, error: {}",
                    businessProcessReferenceId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving evaluation input from database", e);
        }
    }

    /**
     * Retrieve evaluation input by clientId, platformInternalId, and
     * businessProcessReferenceId.
     * Returns the latest result based on timestamp (savedAt field).
     * Optionally filters by evaluationId if provided.
     *
     * This method supports both token-based and header-based platformInternalId.
     *
     * @param clientId                   the client ID
     * @param platformInternalId         the platform internal ID (from token or
     *                                   header)
     * @param businessProcessReferenceId the business process reference ID
     * @param evaluationId               optional evaluation ID filter
     * @return the evaluation input request
     * @throws NoObjectFoundException if not found
     */
    public EvaluationInputRequest getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
            String clientId,
            String platformInternalId,
            String businessProcessReferenceId,
            String evaluationId) {

        log.info(
                "Fetching evaluation input - clientId: {}, platformInternalId: {}, businessProcessReferenceId: {}, evaluationId: {}",
                clientId, platformInternalId, businessProcessReferenceId, evaluationId);

        try {
            // Query with sorting by savedAt descending (latest first)
            List<EvaluationInputDocument> documents = mongoHelper
                    .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                            clientId, platformInternalId, businessProcessReferenceId,
                            Sort.by(Sort.Direction.DESC, "savedAt"));

            if (documents != null && !documents.isEmpty()) {
                // If evaluationId is provided, filter by it
                if (evaluationId != null && !evaluationId.isBlank()) {
                    log.debug("Filtering {} documents by evaluationId: {}", documents.size(), evaluationId);
                    documents = documents.stream()
                            .filter(doc -> evaluationId.equals(doc.getEvaluationId()))
                            .toList();

                    if (documents.isEmpty()) {
                        log.debug("No documents found matching evaluationId: {}", evaluationId);
                        throw new NoObjectFoundException(
                                String.format(
                                        "Evaluation input not found for clientId: %s, platformInternalId: %s, businessProcessReferenceId: %s, evaluationId: %s",
                                        clientId, platformInternalId, businessProcessReferenceId, evaluationId));
                    }
                }

                // Return the first (latest) document
                EvaluationInputDocument latestDocument = documents.get(0);
                log.info("Found {} documents, returning latest with id: {}, savedAt: {}",
                        documents.size(), latestDocument.getId(), latestDocument.getSavedAt());
                // If document exists but is marked Inactive, treat as not found
                if (latestDocument != null && latestDocument.getDocumentStatus() != null
                        && "Inactive".equalsIgnoreCase(latestDocument.getDocumentStatus())) {
                    log.info("Evaluation input document found but marked Inactive for evaluationId: {}",
                            latestDocument.getEvaluationId());
                    throw new NoObjectFoundException(String.format("Evaluation input not found for clientId: %s",
                            latestDocument.getEvaluationId()));
                }
                return mapToResponse(latestDocument);
            } else {
                log.debug("No documents found matching the query criteria");
                throw new NoObjectFoundException(
                        String.format(
                                "Evaluation input not found for clientId: %s, platformInternalId: %s, businessProcessReferenceId: %s",
                                clientId, platformInternalId, businessProcessReferenceId));
            }

        } catch (NoObjectFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error querying MongoDB for evaluation input - clientId: {}, error: {}",
                    clientId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving evaluation input from database", e);
        }
    }

    /**
     * Save or update evaluation input.
     *
     * @param request            the evaluation input request
     * @param platformInternalId the platform internal ID from header
     * @return the saved evaluation input
     */
    public EvaluationInputRequest saveEvaluationInput(
            EvaluationInputRequest request,
            String platformInternalId) {

        log.info("Saving evaluation input - businessProcessReferenceId: {}, platformInternalId: {}, evaluationId: {}",
                request.getBusinessProcessReferenceId(), platformInternalId, request.getEvaluationId());

        try {
            // Find existing document if it exists
            Optional<EvaluationInputDocument> existingDoc = findExistingDocument(request, platformInternalId);

            EvaluationInputDocument documentToSave;

            if (existingDoc.isPresent()) {
                // Update existing document
                log.debug("Existing document found with id: {}, updating...", existingDoc.get().getId());
                documentToSave = updateDocument(existingDoc.get(), request);
                log.info("Updating existing evaluation input document with id: {}", documentToSave.getId());
            } else {
                // Create new document
                log.debug("No existing document found, creating new document");
                documentToSave = createDocument(request, platformInternalId);
                log.info("Creating new evaluation input document");
            }

            // Save to MongoDB
            log.debug("Persisting document to MongoDB...");
            EvaluationInputDocument savedDocument = mongoHelper.save(documentToSave);
            log.info("Successfully saved evaluation input document with id: {} for businessProcessReferenceId: {}",
                    savedDocument.getId(), savedDocument.getBusinessProcessReferenceId());
            log.debug("Saved document createdAt: {}, updatedAt: {}",
                    savedDocument.getCreatedAt(), savedDocument.getUpdatedAt());

            return mapToResponse(savedDocument);

        } catch (IllegalArgumentException e) {
            log.error("Invalid data format for evaluation input - businessProcessReferenceId: {}, error: {}",
                    request.getBusinessProcessReferenceId(), e.getMessage(), e);
            throw new com.alight.healthbundle.exceptions.BadRequestException(
                    "Invalid data format: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error saving evaluation input to MongoDB - businessProcessReferenceId: {}, error: {}",
                    request.getBusinessProcessReferenceId(), e.getMessage(), e);
            throw new RuntimeException("Error saving evaluation input to database", e);
        }
    }

    /**
     * Find existing document by evaluation ID or business process reference ID +
     * platform internal ID + client ID.
     * If evaluationId is provided but not found, falls back to searching by
     * businessProcessReferenceId.
     * If multiple documents exist (duplicates), returns the most recent one.
     */
    private Optional<EvaluationInputDocument> findExistingDocument(
            EvaluationInputRequest request,
            String platformInternalId) {

        String clientId = request.getClientId();

        log.debug("Querying by evaluationId: {}, businessProcessReferenceId: {}, clientId: {}, platformInternalId: {}",
                request.getEvaluationId(), request.getBusinessProcessReferenceId(), clientId, platformInternalId);
        Optional<EvaluationInputDocument> document = mongoHelper
                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdAndEvaluationId(
                        clientId, platformInternalId, request.getBusinessProcessReferenceId(),
                        request.getEvaluationId());

        if (document.isPresent()) {
            return document; // Return the found document
        }
        return Optional.empty();
    }

    /**
     * Create new document from request.
     */
    private EvaluationInputDocument createDocument(EvaluationInputRequest request, String platformInternalId) {
        log.debug("Creating new document - clientId: {}, evaluationId: {}",
                request.getClientId(), request.getEvaluationId());

        try {
            LocalDateTime now = LocalDateTime.now();

            Map<String, Object> savviRequestMap = objectMapper.convertValue(
                    request.getSavviRequest(), new TypeReference<Map<String, Object>>() {
                    });

            log.debug("Successfully converted SAVVIRequest to Map with {} keys", savviRequestMap.size());

            return EvaluationInputDocument.builder()
                    .clientId(request.getClientId())
                    .platformInternalId(platformInternalId)
                    .evaluationId(request.getEvaluationId())
                    .businessProcessReferenceId(request.getBusinessProcessReferenceId())
                    .savviRequest(savviRequestMap)
                    .timestamp(request.getTimestamp())
                    .savedAt(now)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Error converting SAVVIRequest to Map: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to convert SAVVIRequest data: " + e.getMessage(), e);
        }
    }

    /**
     * Update existing document with new request data.
     */
    private EvaluationInputDocument updateDocument(
            EvaluationInputDocument existingDoc,
            EvaluationInputRequest request) {

        log.debug("Updating existing document id: {} with new data", existingDoc.getId());

        try {
            LocalDateTime now = LocalDateTime.now();

            Map<String, Object> savviRequestMap = objectMapper.convertValue(
                    request.getSavviRequest(), new TypeReference<Map<String, Object>>() {
                    });

            log.debug("Successfully converted SAVVIRequest to Map with {} keys", savviRequestMap.size());

            existingDoc.setEvaluationId(request.getEvaluationId());
            existingDoc.setSavviRequest(savviRequestMap);
            existingDoc.setTimestamp(request.getTimestamp());
            existingDoc.setSavedAt(now);
            existingDoc.setUpdatedAt(now);

            log.debug("Document updated, new updatedAt: {}", now);

            return existingDoc;

        } catch (IllegalArgumentException e) {
            log.error("Error converting SAVVIRequest to Map during update: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to convert SAVVIRequest data: " + e.getMessage(), e);
        }
    }

    /**
     * Map MongoDB document to response model.
     */
    private EvaluationInputRequest mapToResponse(EvaluationInputDocument document) {
        log.debug("Mapping document to response - id: {}, businessProcessReferenceId: {}",
                document.getId(), document.getBusinessProcessReferenceId());

        try {
            return EvaluationInputRequest.builder()
                    .clientId(document.getClientId())
                    .platformInternalId(document.getPlatformInternalId())
                    .evaluationId(document.getEvaluationId())
                    .businessProcessReferenceId(document.getBusinessProcessReferenceId())
                    .savviRequest(objectMapper.convertValue(document.getSavviRequest(),
                            com.alight.healthbundle.model.SAVVIRequest.class))
                    .timestamp(document.getTimestamp())
                    .savedAt(document.getSavedAt())
                    .createdAt(document.getCreatedAt())
                    .updatedAt(document.getUpdatedAt())
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Error converting Map to SAVVIRequest: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to convert stored data to SAVVIRequest: " + e.getMessage(), e);
        }
    }
}
