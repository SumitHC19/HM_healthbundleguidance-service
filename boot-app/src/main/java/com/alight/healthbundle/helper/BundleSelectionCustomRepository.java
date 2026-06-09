package com.alight.healthbundle.helper;

import org.bson.Document;
import java.util.List;

public interface BundleSelectionCustomRepository {

    Document saveDocument(Document document);

    Document updateDocument(String id, Document updates);

    /**
     * Finds bundle selections by clientId, platformInternalId, and
     * businessProcessReferenceId, sorted by lastModifiedTimeStamp in descending
     * order
     * (latest first).
     */
    List<Document> findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
            String clientId,
            String platformInternalId,
            String businessProcessReferenceId);

    /**
     * Finds bundle selections by evaluationId, clientId, platformInternalId, and
     * businessProcessReferenceId, sorted by lastModifiedTimeStamp in descending
     * order
     * (latest first).
     */
    List<Document> findByEvaluationIdClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
            String evaluationId,
            String clientId,
            String platformInternalId,
            String businessProcessReferenceId);

}
