package com.alight.healthbundle.helper;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.alight.healthbundle.document.EvaluationInputDocument;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for EvaluationInput documents.
 */
@Repository
public interface EvaluationInputMongoHelper extends MongoRepository<EvaluationInputDocument, String> {

        /**
         * Find evaluation input by evaluation ID.
         *
         * @param evaluationId the evaluation ID
         * @return Optional containing the document if found
         */
        Optional<EvaluationInputDocument> findByEvaluationId(String evaluationId);

        /**
         * Find evaluation input by evaluation ID, client ID, and platform internal ID.
         * This is the proper unique identifier for an evaluation input document.
         * If multiple documents exist (due to data migration), returns the most recent
         * one.
         *
         * @param evaluationId       the evaluation ID
         * @param clientId           the client ID
         * @param platformInternalId the platform internal ID
         * @param sort               sorting criteria (typically by updatedAt or savedAt
         *                           descending)
         * @return List of documents matching the criteria
         */
        List<EvaluationInputDocument> findByEvaluationIdAndClientIdAndPlatformInternalId(
                        String evaluationId, String clientId, String platformInternalId, Sort sort);

        /**
         * Find evaluation input by business process reference ID and platform internal
         * ID.
         *
         * @param businessProcessReferenceId the business process reference ID
         * @param platformInternalId         the platform internal ID
         * @return Optional containing the document if found
         */
        Optional<EvaluationInputDocument> findByBusinessProcessReferenceIdAndPlatformInternalId(
                        String businessProcessReferenceId, String platformInternalId);

        /**
         * Find evaluation input by client ID, platform internal ID, and business
         * process reference ID.
         * This is the proper unique identifier when evaluationId is not provided.
         * If multiple documents exist (due to data migration), returns the most recent
         * one.
         *
         * @param clientId                   the client ID
         * @param platformInternalId         the platform internal ID
         * @param businessProcessReferenceId the business process reference ID
         * @param sort                       sorting criteria (typically by updatedAt or
         *                                   savedAt descending)
         * @return List of documents matching the criteria
         */
        List<EvaluationInputDocument> findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                        String clientId, String platformInternalId, String businessProcessReferenceId, Sort sort);

        /**
         * Find evaluation input by client ID, platform internal ID, business process
         * reference ID, and evaluation ID.
         * This is the proper unique identifier when both evaluationId and
         * businessProcessReferenceId are provided.
         *
         * @param clientId
         * @param platformInternalId
         * @param businessProcessReferenceId
         * @param evaluationId
         * @return Optional containing the document if found
         */
        Optional<EvaluationInputDocument> findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdAndEvaluationId(
                        String clientId, String platformInternalId, String businessProcessReferenceId,
                        String evaluationId);

        @Query("{ 'evaluationId': { $in: ?0 } }")
        @Update("{ $set: { 'documentStatus': ?1 } }")
        void updateDocumentStatusByEvaluationIds(List<String> evaluationIds, String newStatus);
}
