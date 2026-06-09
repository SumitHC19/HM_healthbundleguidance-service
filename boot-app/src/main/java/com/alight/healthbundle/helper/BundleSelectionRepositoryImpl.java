package com.alight.healthbundle.helper;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BundleSelectionRepositoryImpl implements BundleSelectionCustomRepository {

    private static final Logger logger = LoggerFactory.getLogger(BundleSelectionRepositoryImpl.class);

    private final MongoTemplate mongoTemplate;

    private static final String COLLECTION = "bundleSelection";

    public BundleSelectionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Document saveDocument(Document document) {
        logger.debug("Saving bundle selection document to collection: {}", COLLECTION);
        // inserts if new, updates if _id is present
        Document saved = mongoTemplate.save(document, COLLECTION);
        logger.info("Successfully saved bundle selection document with id: {}", saved.get("_id"));
        return saved;
    }

    @Override
    public Document updateDocument(String id, Document updates) {
        logger.debug("Updating bundle selection document with id: {} in collection: {}", id, COLLECTION);
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();

        // Apply each field update (skip _id)
        updates.forEach((key, value) -> {
            if (!"_id".equals(key)) {
                update.set(key, value);
            }
        });

        // Return the updated document
        Document updated = mongoTemplate.findAndModify(
                query,
                update,
                org.springframework.data.mongodb.core.FindAndModifyOptions.options().returnNew(true),
                Document.class,
                COLLECTION);
        logger.info("Successfully updated bundle selection document with id: {}", id);
        return updated;
    }

    @Override
    public List<Document> findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
            String clientId,
            String platformInternalId,
            String businessProcessReferenceId) {
        logger.debug(
                "Finding bundle selections by clientId: {}, platformInternalId: {}, businessProcessReferenceId: {} sorted by lastModifiedTimeStamp",
                clientId, platformInternalId, businessProcessReferenceId);

        Criteria criteria = Criteria.where("clientId").is(clientId)
                .and("platformInternalId").is(platformInternalId)
                .and("businessProcessReferenceId").is(businessProcessReferenceId);

        Query query = new Query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "lastModifiedTimeStamp"));

        List<Document> documents = mongoTemplate.find(query, Document.class, COLLECTION);
        logger.info("Found {} bundle selection(s) matching criteria", documents.size());
        return documents;
    }

    @Override
    public List<Document> findByEvaluationIdClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
            String evaluationId,
            String clientId,
            String platformInternalId,
            String businessProcessReferenceId) {
        logger.debug(
                "Finding bundle selections by evaluationId: {}, clientId: {}, platformInternalId: {}, businessProcessReferenceId: {} sorted by lastModifiedTimeStamp",
                evaluationId, clientId, platformInternalId, businessProcessReferenceId);

        Criteria criteria = Criteria.where("evaluationId").is(evaluationId)
                .and("clientId").is(clientId)
                .and("platformInternalId").is(platformInternalId)
                .and("businessProcessReferenceId").is(businessProcessReferenceId);

        Query query = new Query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "lastModifiedTimeStamp"));

        List<Document> documents = mongoTemplate.find(query, Document.class, COLLECTION);
        logger.info("Found {} bundle selection(s) matching criteria for evaluationId: {}", documents.size(),
                evaluationId);
        return documents;
    }
}
