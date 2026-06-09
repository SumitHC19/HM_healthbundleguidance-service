package com.alight.healthbundle.helper;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BundleSelectionRepositoryImplTest {

        @Mock
        private MongoTemplate mongoTemplate;

        private BundleSelectionRepositoryImpl repository;

        @BeforeEach
        void setUp() {
                repository = new BundleSelectionRepositoryImpl(mongoTemplate);
        }

        // --------------------------
        // saveDocument tests
        // --------------------------

        @Test
        void saveDocument_delegatesToMongoTemplateSave_withCorrectCollection() {
                Document input = new Document("clientId", "C1").append("foo", "bar");
                Document saved = new Document("_id", "123").append("clientId", "C1").append("foo", "bar");

                when(mongoTemplate.save(input, "bundleSelection")).thenReturn(saved);

                Document result = repository.saveDocument(input);

                assertSame(saved, result);
                verify(mongoTemplate, times(1)).save(input, "bundleSelection");
                verifyNoMoreInteractions(mongoTemplate);
        }

        @Test
        void saveDocument_whenMongoThrows_propagatesException() {
                Document input = new Document("clientId", "C1");

                when(mongoTemplate.save(input, "bundleSelection"))
                                .thenThrow(new RuntimeException("mongo down"));

                RuntimeException ex = assertThrows(RuntimeException.class, () -> repository.saveDocument(input));
                assertTrue(ex.getMessage().contains("mongo down"));

                verify(mongoTemplate, times(1)).save(input, "bundleSelection");
        }

        // --------------------------
        // updateDocument tests
        // --------------------------

        @Test
        void updateDocument_buildsUpdateSkippingId_andCallsFindAndModify_returnNewTrue() {
                String id = "abc123";

                // Updates contains _id + other fields
                Document updates = new Document("_id", "SHOULD_NOT_BE_UPDATED")
                                .append("clientId", "C1")
                                .append("platformInternalId", "P1")
                                .append("businessProcessReferenceId", "B1")
                                .append("lastModifiedTimeStamp", "2026-02-03T10:00:00Z");

                Document returned = new Document("_id", id)
                                .append("clientId", "C1")
                                .append("platformInternalId", "P1")
                                .append("businessProcessReferenceId", "B1")
                                .append("lastModifiedTimeStamp", "2026-02-03T10:00:00Z");

                // Capture arguments passed to findAndModify
                ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
                ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
                ArgumentCaptor<FindAndModifyOptions> optionsCaptor = ArgumentCaptor
                                .forClass(FindAndModifyOptions.class);

                when(mongoTemplate.findAndModify(
                                queryCaptor.capture(),
                                updateCaptor.capture(),
                                optionsCaptor.capture(),
                                eq(Document.class),
                                eq("bundleSelection"))).thenReturn(returned);

                Document result = repository.updateDocument(id, updates);

                assertSame(returned, result);

                // ✅ Verify query is on _id = id
                Query capturedQuery = queryCaptor.getValue();
                assertNotNull(capturedQuery);

                // Check toString contains "_id" and the id value.
                String queryString = capturedQuery.toString();
                assertTrue(queryString.contains("_id"), "Query should contain _id criteria");
                assertTrue(queryString.contains(id), "Query should contain the provided id");

                // ✅ Verify update sets fields except _id
                Update capturedUpdate = updateCaptor.getValue();
                assertNotNull(capturedUpdate);

                String updateString = capturedUpdate.toString();
                assertTrue(updateString.contains("clientId"), "Update should set clientId");
                assertTrue(updateString.contains("platformInternalId"), "Update should set platformInternalId");
                assertTrue(updateString.contains("businessProcessReferenceId"),
                                "Update should set businessProcessReferenceId");
                assertTrue(updateString.contains("lastModifiedTimeStamp"), "Update should set lastModifiedTimeStamp");
                assertFalse(updateString.contains("SHOULD_NOT_BE_UPDATED"), "Update must not include _id value");
                assertFalse(updateString.contains("\"_id\""), "Update must not set _id");

                // ✅ Verify returnNew(true)
                FindAndModifyOptions options = optionsCaptor.getValue();
                assertNotNull(options);
                assertTrue(options.isReturnNew(),
                                "FindAndModifyOptions should return the updated document (returnNew=true)");

                verify(mongoTemplate, times(1)).findAndModify(any(Query.class), any(Update.class),
                                any(FindAndModifyOptions.class),
                                eq(Document.class), eq("bundleSelection"));
                verifyNoMoreInteractions(mongoTemplate);
        }

        @Test
        void updateDocument_whenOnlyIdProvided_shouldNotSetAnything_butStillCallsFindAndModify() {
                String id = "onlyId";
                Document updates = new Document("_id", id);

                Document returned = new Document("_id", id).append("unchanged", true);

                ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);

                when(mongoTemplate.findAndModify(
                                any(Query.class),
                                updateCaptor.capture(),
                                any(FindAndModifyOptions.class),
                                eq(Document.class),
                                eq("bundleSelection"))).thenReturn(returned);

                Document result = repository.updateDocument(id, updates);

                assertSame(returned, result);

                Update capturedUpdate = updateCaptor.getValue();
                assertNotNull(capturedUpdate);

                // No fields to set => update should effectively be empty
                String updateString = capturedUpdate.toString();
                assertFalse(updateString.contains("_id"), "Update should not include _id");

                verify(mongoTemplate, times(1)).findAndModify(any(Query.class), any(Update.class),
                                any(FindAndModifyOptions.class),
                                eq(Document.class), eq("bundleSelection"));
        }

        @Test
        void updateDocument_whenNoDocumentMatches_returnsNull_andStillUsesReturnNewTrue() {
                String id = "not-found";
                Document updates = new Document("clientId", "C1");

                ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
                ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
                ArgumentCaptor<FindAndModifyOptions> optionsCaptor = ArgumentCaptor
                                .forClass(FindAndModifyOptions.class);

                when(mongoTemplate.findAndModify(
                                queryCaptor.capture(),
                                updateCaptor.capture(),
                                optionsCaptor.capture(),
                                eq(Document.class),
                                eq("bundleSelection"))).thenReturn(null);

                Document result = repository.updateDocument(id, updates);

                assertNull(result, "When MongoTemplate returns null, repository should return null");

                assertTrue(optionsCaptor.getValue().isReturnNew(), "returnNew must be true");

                String qs = queryCaptor.getValue().toString();
                assertTrue(qs.contains("\"_id\" : \"" + id + "\""));

                String us = updateCaptor.getValue().toString();
                assertTrue(us.contains("clientId"), "Update should include clientId field");
        }

        @Test
        void updateDocument_allowsNullValues_andDottedKeys() {
                String id = "with-null-and-dots";
                Document updates = new Document("_id", "IGNORED")
                                .append("clientId", null) // null value
                                .append("nested.field", "value"); // dotted key

                ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);

                when(mongoTemplate.findAndModify(
                                any(Query.class),
                                updateCaptor.capture(),
                                any(FindAndModifyOptions.class),
                                eq(Document.class),
                                eq("bundleSelection"))).thenReturn(new Document("_id", id));

                Document result = repository.updateDocument(id, updates);

                assertNotNull(result);

                String updateString = updateCaptor.getValue().toString();
                // Expect $set to include both keys; toString should contain keys
                assertTrue(updateString.contains("clientId"), "Update should include clientId even if null");
                assertTrue(updateString.contains("nested.field"), "Update should include dotted keys");
                assertFalse(updateString.contains("\"_id\""), "Update must not set _id");
        }

        @Test
        void updateDocument_doesNotMutateCallerUpdates() {
                String id = "immutable";
                Document updates = new Document("_id", "X")
                                .append("clientId", "C1");

                Document snapshot = new Document(updates); // make a copy

                when(mongoTemplate.findAndModify(
                                any(Query.class),
                                any(Update.class),
                                any(FindAndModifyOptions.class),
                                eq(Document.class),
                                eq("bundleSelection"))).thenReturn(new Document("_id", id));

                repository.updateDocument(id, updates);

                assertEquals(snapshot, updates, "Repository must not mutate the provided updates Document");
        }

}
