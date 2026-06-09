package com.alight.healthbundle.helper;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BundleSelectionCustomRepository implementation.
 * Tests custom MongoDB query methods for BundleSelection documents.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BundleSelectionCustomRepository Tests")
class BundleSelectionCustomRepositoryTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private BundleSelectionCustomRepository repository;

    private static final String CLIENT_ID = "CLIENT-001";
    private static final String PLATFORM_ID = "PLATFORM-001";
    private static final String BUSINESS_PROC_ID = "BPROC-001";

    @BeforeEach
    void setUp() {
        repository = new BundleSelectionRepositoryImpl(mongoTemplate);
    }

    @Test
    @DisplayName("Should find documents sorted by last modified timestamp")
    void testFindByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified_Success() {
        // Arrange
        Document doc1 = new Document()
                .append("_id", "1")
                .append("clientId", CLIENT_ID)
                .append("platformInternalId", PLATFORM_ID)
                .append("businessProcessReferenceId", BUSINESS_PROC_ID)
                .append("lastModifiedTimeStamp", "2026-02-16T12:00:00Z");

        Document doc2 = new Document()
                .append("_id", "2")
                .append("clientId", CLIENT_ID)
                .append("platformInternalId", PLATFORM_ID)
                .append("businessProcessReferenceId", BUSINESS_PROC_ID)
                .append("lastModifiedTimeStamp", "2026-02-16T11:00:00Z");

        List<Document> expectedDocs = Arrays.asList(doc1, doc2);

        when(mongoTemplate.find(any(Query.class), eq(Document.class), anyString()))
                .thenReturn(expectedDocs);

        // Act
        List<Document> result = repository
                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                        CLIENT_ID, PLATFORM_ID, BUSINESS_PROC_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getString("_id"));
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Document.class), anyString());
    }

    @Test
    @DisplayName("Should return empty list when no documents match")
    void testFindByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified_Empty() {
        // Arrange
        when(mongoTemplate.find(any(Query.class), eq(Document.class), anyString()))
                .thenReturn(new ArrayList<>());

        // Act
        List<Document> result = repository
                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                        "NON_EXISTENT", "NON_EXISTENT", "NON_EXISTENT");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null parameters")
    void testFindByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified_NullParams() {
        // Arrange
        when(mongoTemplate.find(any(Query.class), eq(Document.class), anyString()))
                .thenReturn(new ArrayList<>());

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            repository.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                    null, null, null);
        });
    }

    @Test
    @DisplayName("Should return results in correct order (most recent first)")
    void testResultsSortedByLastModified() {
        // Arrange
        String oldestTime = "2026-02-16T10:00:00Z";
        String middleTime = "2026-02-16T11:00:00Z";
        String newestTime = "2026-02-16T12:00:00Z";

        Document newest = new Document()
                .append("_id", "newest")
                .append("clientId", CLIENT_ID)
                .append("platformInternalId", PLATFORM_ID)
                .append("businessProcessReferenceId", BUSINESS_PROC_ID)
                .append("lastModifiedTimeStamp", newestTime);

        Document middle = new Document()
                .append("_id", "middle")
                .append("clientId", CLIENT_ID)
                .append("platformInternalId", PLATFORM_ID)
                .append("businessProcessReferenceId", BUSINESS_PROC_ID)
                .append("lastModifiedTimeStamp", middleTime);

        Document oldest = new Document()
                .append("_id", "oldest")
                .append("clientId", CLIENT_ID)
                .append("platformInternalId", PLATFORM_ID)
                .append("businessProcessReferenceId", BUSINESS_PROC_ID)
                .append("lastModifiedTimeStamp", oldestTime);

        List<Document> docs = Arrays.asList(newest, middle, oldest);

        when(mongoTemplate.find(any(Query.class), eq(Document.class), anyString()))
                .thenReturn(docs);

        // Act
        List<Document> result = repository
                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                        CLIENT_ID, PLATFORM_ID, BUSINESS_PROC_ID);

        // Assert
        assertEquals(3, result.size());
        assertEquals("newest", result.get(0).getString("_id"));
        assertEquals("middle", result.get(1).getString("_id"));
        assertEquals("oldest", result.get(2).getString("_id"));
    }

    @Test
    @DisplayName("Should handle single document result")
    void testSingleDocumentResult() {
        // Arrange
        Document doc = new Document()
                .append("_id", "single")
                .append("clientId", CLIENT_ID)
                .append("platformInternalId", PLATFORM_ID)
                .append("businessProcessReferenceId", BUSINESS_PROC_ID);

        when(mongoTemplate.find(any(Query.class), eq(Document.class), anyString()))
                .thenReturn(Arrays.asList(doc));

        // Act
        List<Document> result = repository
                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                        CLIENT_ID, PLATFORM_ID, BUSINESS_PROC_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("single", result.get(0).getString("_id"));
    }

    @Test
    @DisplayName("Should handle large number of documents")
    void testLargeDocumentResult() {
        // Arrange
        List<Document> docs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            docs.add(new Document()
                    .append("_id", "doc-" + i)
                    .append("clientId", CLIENT_ID)
                    .append("platformInternalId", PLATFORM_ID)
                    .append("businessProcessReferenceId", BUSINESS_PROC_ID));
        }

        when(mongoTemplate.find(any(Query.class), eq(Document.class), anyString()))
                .thenReturn(docs);

        // Act
        List<Document> result = repository
                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                        CLIENT_ID, PLATFORM_ID, BUSINESS_PROC_ID);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.size());
    }

    @Test
    @DisplayName("Should preserve all document fields")
    void testPreservesAllDocumentFields() {
        // Arrange
        Document doc = new Document()
                .append("_id", "123")
                .append("clientId", CLIENT_ID)
                .append("platformInternalId", PLATFORM_ID)
                .append("businessProcessReferenceId", BUSINESS_PROC_ID)
                .append("evaluationId", "EVAL-001")
                .append("featuredAs", "balanced")
                .append("planYearBeginDate", "2026-01-01")
                .append("lastModifiedTimeStamp", "2026-02-16T12:00:00Z");

        when(mongoTemplate.find(any(Query.class), eq(Document.class), anyString()))
                .thenReturn(Arrays.asList(doc));

        // Act
        List<Document> result = repository
                .findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                        CLIENT_ID, PLATFORM_ID, BUSINESS_PROC_ID);

        // Assert
        Document retrieved = result.get(0);
        assertEquals("123", retrieved.getString("_id"));
        assertEquals(CLIENT_ID, retrieved.getString("clientId"));
        assertEquals(PLATFORM_ID, retrieved.getString("platformInternalId"));
        assertEquals(BUSINESS_PROC_ID, retrieved.getString("businessProcessReferenceId"));
        assertEquals("EVAL-001", retrieved.getString("evaluationId"));
        assertEquals("balanced", retrieved.getString("featuredAs"));
    }

    @Test
    @DisplayName("Should call mongoTemplate with correct collection name")
    void testCorrectCollectionName() {
        // Arrange
        ArgumentCaptor<String> collectionCaptor = ArgumentCaptor.forClass(String.class);

        when(mongoTemplate.find(any(Query.class), eq(Document.class), anyString()))
                .thenReturn(new ArrayList<>());

        // Act
        repository.findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceIdSortedByLastModified(
                CLIENT_ID, PLATFORM_ID, BUSINESS_PROC_ID);

        // Assert
        verify(mongoTemplate).find(
                any(Query.class),
                eq(Document.class),
                collectionCaptor.capture());

        String collectionName = collectionCaptor.getValue();
        assertEquals("bundleSelection", collectionName);
    }
}
