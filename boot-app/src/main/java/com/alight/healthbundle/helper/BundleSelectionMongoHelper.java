package com.alight.healthbundle.helper;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.alight.healthbundle.model.BundleSelection;

import java.util.Optional;

import org.bson.Document;

public interface BundleSelectionMongoHelper
                extends MongoRepository<BundleSelection, String> {

        /**
         * Finds bundle selection by clientId, platformInternalId, and
         * businessProcessReferenceId.
         * This matches your requirement to query by these 3 fields.
         */
        @Query("{ 'clientId': ?0, 'platformInternalId': ?1, 'businessProcessReferenceId': ?2 }")
        Optional<Document> findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                        String clientId,
                        String platformInternalId,
                        String businessProcessReferenceId);

}
