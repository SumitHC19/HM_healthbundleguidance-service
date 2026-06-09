package com.alight.healthbundle.helper;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.alight.healthbundle.document.EvaluationDocument;

/**
 * MongoDB repository for EvaluationDocument operations.
 * Uses optimized query matching swagger-compliant schema.
 */
@Repository
public interface EvaluationMongoHelper extends MongoRepository<EvaluationDocument, String> {

        /**
         * Finds evaluation document by evaluationId.
         * Simplified query for swagger-compliant schema where evaluationId is the
         * primary key.
         *
         * @param evaluationId Required evaluation identifier
         * @return Optional containing the matching document
         */
        @Query("{ 'evaluationId': ?0 }")
        Optional<EvaluationDocument> findByEvaluationId(String evaluationId);

        /**
         * Finds evaluation document by evaluationId and status.
         *
         * @param evaluationId     Required evaluation identifier
         * @param evaluationStatus Optional evaluation status filter
         * @return Optional containing the matching document
         */
        @Query("{ 'evaluationId': ?0, $or: [ { $expr: { $eq: [?1, null] } }, { 'evaluationStatus': ?1 } ] }")
        Optional<EvaluationDocument> findByEvaluationIdAndStatus(String evaluationId, String evaluationStatus);

        @Query("{ 'evaluationId': { '$in': ?0 } }")
        List<EvaluationDocument> findByListEvaluationIds(List<String> evaluationIds);

        @Query("{ 'evaluationId': { $in: ?0 } }")
        @Update("{ $set: { 'documentStatus': ?1 } }")
        void updateDocumentStatusByEvaluationIds(List<String> evaluationIds, String newStatus);

}
