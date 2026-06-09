package com.alight.healthbundle.dao;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.alight.healthbundle.document.EvaluationDocument;
import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.helper.EvaluationMongoHelper;
import com.alight.healthbundle.model.EvaluationResultsResponse;

/**
 * Data Access Object for bundle guidance evaluation documents.
 * Handles MongoDB queries matching swagger-compliant schema.
 */
@Repository
public class BundleGuidanceDao {

    private static final Logger logger = LoggerFactory.getLogger(BundleGuidanceDao.class);
    private static final String MONGODB_NOT_AVAILABLE_MSG = "MongoDB connection not available";
    private static final String NO_DATA_FOUND_MSG = "No evaluation data found for evaluationId: %s";

    @Nullable
    private final EvaluationMongoHelper evaluationMongoHelper;

    public BundleGuidanceDao(@Nullable EvaluationMongoHelper evaluationMongoHelper) {
        this.evaluationMongoHelper = evaluationMongoHelper;
    }

    /**
     * Retrieves evaluation recommendations from MongoDB.
     *
     * @param evaluationId             Primary identifier (required)
     * @param alightRequestHeader      Request tracking header (currently unused)
     * @param alightPersonSessionToken Session token (currently unused)
     * @return EvaluationResultsResponse containing bundle data
     * @throws NoObjectFoundException if no matching document found or MongoDB
     *                                unavailable
     */
    public EvaluationResultsResponse getRecommendations(
            String evaluationId,
            String alightRequestHeader,
            String alightPersonSessionToken) {

        logger.info("Fetching recommendations - evaluationId: {}", evaluationId);

        validateMongoConnection();

        logger.debug("Starting MongoDB query for evaluationId: {}", evaluationId);
        Optional<EvaluationDocument> document = queryMongoDB(evaluationId);

        // If document exists but is marked Inactive, treat as not found
        if (document.isPresent()) {
            EvaluationDocument doc = document.get();
            if (doc.getDocumentStatus() != null && "Inactive".equalsIgnoreCase(doc.getDocumentStatus())) {
                logger.info("Evaluation document found but marked Inactive for evaluationId: {}", evaluationId);
                throw new NoObjectFoundException(String.format(NO_DATA_FOUND_MSG, evaluationId));
            }
        }

        return document
                .map(this::mapToResponse)
                .orElseThrow(() -> {
                    logger.warn("No evaluation document found for evaluationId: {}", evaluationId);
                    return new NoObjectFoundException(String.format(NO_DATA_FOUND_MSG, evaluationId));
                });
    }

    /**
     * Validates MongoDB connection availability.
     *
     * @throws NoObjectFoundException if MongoDB helper is not available
     */
    private void validateMongoConnection() {
        if (evaluationMongoHelper == null) {
            logger.error("EvaluationMongoHelper is not initialized - MongoDB connection unavailable");
            throw new NoObjectFoundException(MONGODB_NOT_AVAILABLE_MSG);
        }
    }

    /**
     * Queries MongoDB by evaluationId.
     *
     * @param evaluationId Required evaluation identifier
     * @return Optional containing the document if found
     */
    private Optional<EvaluationDocument> queryMongoDB(String evaluationId) {
        logger.debug("Executing MongoDB query for evaluationId: {}", evaluationId);
        return evaluationMongoHelper.findByEvaluationId(evaluationId);
    }

    /**
     * Maps EvaluationDocument to EvaluationResultsResponse.
     *
     * @param document The MongoDB document
     * @return Response object with swagger fields and metadata
     */
    private EvaluationResultsResponse mapToResponse(EvaluationDocument document) {
        logger.debug("Mapping evaluation document to response for evaluationId: {}", document.getEvaluationId());

        EvaluationResultsResponse response = new EvaluationResultsResponse();
        response.setEventType(document.getEventType());
        response.setEvaluationId(document.getEvaluationId());
        response.setEvaluationType(document.getEvaluationType());
        response.setEvaluationStatus(document.getEvaluationStatus());
        response.setEvaluationOutput(document.getEvaluationOutput());

        // Include metadata fields
        response.setSource(document.getSource());
        response.setSavedAt(document.getSavedAt());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());

        return response;
    }

    /**
     * Saves evaluation recommendations to MongoDB.
     * Performs upsert: updates existing document if evaluationId exists, creates
     * new if not.
     *
     * @param evaluationResults        The evaluation results to save
     * @param alightRequestHeader      Request tracking header (currently unused)
     * @param alightPersonSessionToken Session token (currently unused)
     * @return Saved EvaluationResultsResponse
     * @throws NoObjectFoundException if MongoDB unavailable
     */
    public EvaluationResultsResponse saveRecommendations(
            EvaluationResultsResponse evaluationResults,
            String alightRequestHeader,
            String alightPersonSessionToken) {

        logger.info("Saving recommendations - evaluationId: {}", evaluationResults.getEvaluationId());

        validateMongoConnection();

        // Check if document already exists with this evaluationId
        Optional<EvaluationDocument> existingDocument = queryMongoDB(evaluationResults.getEvaluationId());

        EvaluationDocument document = existingDocument
                .map(existing -> updateDocument(existing, evaluationResults))
                .orElseGet(() -> createDocument(evaluationResults));

        // Log detailed document structure before save for validator debugging
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            String documentJson = mapper.writeValueAsString(document);
            logger.info("About to save document for evaluationId: {} - JSON structure: {}",
                    document.getEvaluationId(), documentJson);
        } catch (Exception e) {
            logger.warn("Could not serialize document to JSON for logging: {}", e.getMessage());
        }

        EvaluationDocument savedDocument = evaluationMongoHelper.save(document);

        logger.info("Successfully saved evaluation document with id: {} for evaluationId: {}",
                savedDocument.getId(), savedDocument.getEvaluationId());

        return mapToResponse(savedDocument);
    }

    /**
     * Updates existing document with new evaluation results.
     * For legacy documents (pre-validator), completely reconstructs the document
     * to ensure validator compliance.
     *
     * @param existing The existing document
     * @param results  The new evaluation results
     * @return Updated document
     */
    private EvaluationDocument updateDocument(EvaluationDocument existing, EvaluationResultsResponse results) {
        logger.debug("Updating existing document for evaluationId: {}", results.getEvaluationId());

        // Check if this is a legacy document (created before validator was added)
        boolean isLegacyDocument = (existing.getCreatedAt() == null);

        if (isLegacyDocument) {
            logger.warn(
                    "Legacy document detected for evaluationId: {} (createdAt is null). Reconstructing document with fresh structure for validator compliance.",
                    results.getEvaluationId());

            // Create a completely fresh document structure to match current validator
            // schema
            EvaluationDocument freshDocument = mapToDocument(results);

            // ONLY preserve the MongoDB _id field from the existing document (critical for
            // update vs insert)
            freshDocument.setId(existing.getId());

            // Use current timestamp for createdAt since legacy document didn't have it
            // (this is acceptable as it represents when the record became
            // validator-compliant)
            freshDocument.setCreatedAt(LocalDateTime.now());

            logger.info("Reconstructed legacy document for evaluationId: {} with fresh structure",
                    results.getEvaluationId());
            return freshDocument;
        }

        // For non-legacy documents, update fields normally
        existing.setEvaluationId(results.getEvaluationId());
        existing.setEventType(results.getEventType() != null ? results.getEventType() : "evaluation_result");
        existing.setEvaluationType(results.getEvaluationType() != null ? results.getEvaluationType() : "bundle");
        existing.setEvaluationStatus(results.getEvaluationStatus() != null ? results.getEvaluationStatus() : "success");
        existing.setEvaluationOutput(results.getEvaluationOutput());
        existing.setSource("savvi");
        existing.setSavedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        logger.debug("Updated document for evaluationId: {}, createdAt preserved: {}",
                results.getEvaluationId(), existing.getCreatedAt());
        // updates
        existing.setUpdatedAt(LocalDateTime.now());

        // Log all fields being saved for debugging validator issues
        logger.debug(
                "Document before save - evaluationId: {}, eventType: {}, evaluationType: {}, evaluationStatus: {}, hasOutput: {}, createdAt: {}, updatedAt: {}",
                existing.getEvaluationId(), existing.getEventType(), existing.getEvaluationType(),
                existing.getEvaluationStatus(), (existing.getEvaluationOutput() != null),
                existing.getCreatedAt(), existing.getUpdatedAt());

        return existing;
    }

    /**
     * Creates new document from evaluation results.
     * Ensures all required fields are set for DocumentDB validator.
     *
     * @param results The evaluation results
     * @return New document
     */
    private EvaluationDocument createDocument(EvaluationResultsResponse results) {
        logger.debug("Creating new document for evaluationId: {}", results.getEvaluationId());
        EvaluationDocument doc = mapToDocument(results);

        // Log all fields being saved for debugging validator issues
        logger.debug(
                "New document - evaluationId: {}, eventType: {}, evaluationType: {}, evaluationStatus: {}, hasOutput: {}, createdAt: {}, updatedAt: {}",
                doc.getEvaluationId(), doc.getEventType(), doc.getEvaluationType(),
                doc.getEvaluationStatus(), (doc.getEvaluationOutput() != null),
                doc.getCreatedAt(), doc.getUpdatedAt());

        return doc;
    }

    /**
     * Maps EvaluationResultsResponse to EvaluationDocument.
     * Sets all required fields with defensive null checks for DocumentDB validator.
     *
     * @param response The response object from API
     * @return MongoDB document ready for persistence
     */
    private EvaluationDocument mapToDocument(EvaluationResultsResponse response) {
        logger.debug("Mapping response to evaluation document for evaluationId: {}", response.getEvaluationId());

        EvaluationDocument document = new EvaluationDocument();

        // Explicitly set ALL required fields with defaults for null values
        document.setEvaluationId(response.getEvaluationId());
        document.setEventType(response.getEventType() != null ? response.getEventType() : "evaluation_result");
        document.setEvaluationType(response.getEvaluationType() != null ? response.getEvaluationType() : "bundle");
        document.setEvaluationStatus(
                response.getEvaluationStatus() != null ? response.getEvaluationStatus() : "success");
        document.setEvaluationOutput(response.getEvaluationOutput());

        // Set source as per story requirement
        document.setSource("savvi");

        // Set savedAt timestamp as per story requirement
        document.setSavedAt(LocalDateTime.now());

        // Explicitly set createdAt for new documents (in addition to @CreatedDate
        // annotation)
        document.setCreatedAt(LocalDateTime.now());

        // Explicitly set updatedAt for new documents (in addition to @LastModifiedDate
        // annotation)
        document.setUpdatedAt(LocalDateTime.now());

        return document;
    }
}
