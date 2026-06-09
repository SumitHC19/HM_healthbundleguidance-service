package com.alight.healthbundle.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for BundleSelection model class.
 * Tests data holders, getters, setters, and object validation.
 */
@DisplayName("BundleSelection Model Tests")
class BundleSelectionModelTest {

    @Test
    @DisplayName("Should create BundleSelection with default constructor")
    void testDefaultConstructor() {
        // Act
        BundleSelection bundle = new BundleSelection();

        // Assert
        assertNotNull(bundle);
        assertNull(bundle.getId());
        assertNull(bundle.getClientId());
        assertNull(bundle.getPlatformInternalId());
        assertNull(bundle.getBusinessProcessReferenceId());
        assertNull(bundle.getEvaluationId());
        assertNull(bundle.getFeaturedAs());
        assertNull(bundle.getPlanYearBeginDate());
        assertNull(bundle.getLastModifiedTimeStamp());
    }

    @Test
    @DisplayName("Should set and get id")
    void testIdProperty() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        String id = "test-id-123";

        // Act
        bundle.setId(id);

        // Assert
        assertEquals(id, bundle.getId());
    }

    @Test
    @DisplayName("Should set and get clientId")
    void testClientIdProperty() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        String clientId = "CLIENT-001";

        // Act
        bundle.setClientId(clientId);

        // Assert
        assertEquals(clientId, bundle.getClientId());
    }

    @Test
    @DisplayName("Should set and get evaluationId")
    void testEvaluationIdProperty() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        String evalId = "EVAL-001";

        // Act
        bundle.setEvaluationId(evalId);

        // Assert
        assertEquals(evalId, bundle.getEvaluationId());
    }

    @Test
    @DisplayName("Should set and get businessProcessReferenceId")
    void testBusinessProcessReferenceIdProperty() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        String bprocId = "BPROC-001";

        // Act
        bundle.setBusinessProcessReferenceId(bprocId);

        // Assert
        assertEquals(bprocId, bundle.getBusinessProcessReferenceId());
    }

    @Test
    @DisplayName("Should set and get platformInternalId")
    void testPlatformInternalIdProperty() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        String platformId = "PLATFORM-001";

        // Act
        bundle.setPlatformInternalId(platformId);

        // Assert
        assertEquals(platformId, bundle.getPlatformInternalId());
    }

    @Test
    @DisplayName("Should set and get planYearBeginDate")
    void testPlanYearBeginDateProperty() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        String date = "2026-01-01";

        // Act
        bundle.setPlanYearBeginDate(date);

        // Assert
        assertEquals(date, bundle.getPlanYearBeginDate());
    }

    @Test
    @DisplayName("Should set and get lastModifiedTimeStamp")
    void testLastModifiedTimeStampProperty() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        String timestamp = "2026-02-16T10:00:00Z";

        // Act
        bundle.setLastModifiedTimeStamp(timestamp);

        // Assert
        assertEquals(timestamp, bundle.getLastModifiedTimeStamp());
    }

    @Test
    @DisplayName("Should set multiple properties")
    void testMultipleProperties() {
        // Arrange
        BundleSelection bundle = new BundleSelection();

        // Act
        bundle.setId("123");
        bundle.setClientId("CLIENT-001");
        bundle.setEvaluationId("EVAL-001");
        bundle.setBusinessProcessReferenceId("BPROC-001");
        bundle.setPlatformInternalId("PLATFORM-001");
        bundle.setFeaturedAs("balanced");
        bundle.setPlanYearBeginDate("2026-01-01");
        bundle.setLastModifiedTimeStamp("2026-02-16T10:00:00Z");

        // Assert
        assertEquals("123", bundle.getId());
        assertEquals("CLIENT-001", bundle.getClientId());
        assertEquals("EVAL-001", bundle.getEvaluationId());
        assertEquals("BPROC-001", bundle.getBusinessProcessReferenceId());
        assertEquals("PLATFORM-001", bundle.getPlatformInternalId());
        assertEquals("balanced", bundle.getFeaturedAs());
        assertEquals("2026-01-01", bundle.getPlanYearBeginDate());
        assertEquals("2026-02-16T10:00:00Z", bundle.getLastModifiedTimeStamp());
    }

    @Test
    @DisplayName("Should update properties independently")
    void testIndependentPropertyUpdates() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        bundle.setClientId("OLD-CLIENT");

        // Act
        bundle.setClientId("NEW-CLIENT");

        // Assert
        assertEquals("NEW-CLIENT", bundle.getClientId());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        bundle.setId("123");

        // Act
        bundle.setId(null);
        bundle.setClientId(null);
        bundle.setFeaturedAs(null);

        // Assert
        assertNull(bundle.getId());
        assertNull(bundle.getClientId());
        assertNull(bundle.getFeaturedAs());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Arrange
        BundleSelection bundle = new BundleSelection();

        // Act
        bundle.setClientId("");
        bundle.setEvaluationId("");
        bundle.setBusinessProcessReferenceId("");

        // Assert
        assertEquals("", bundle.getClientId());
        assertEquals("", bundle.getEvaluationId());
        assertEquals("", bundle.getBusinessProcessReferenceId());
    }

    @Test
    @DisplayName("Should create separate instances")
    void testSeparateInstances() {
        // Arrange & Act
        BundleSelection bundle1 = new BundleSelection();
        BundleSelection bundle2 = new BundleSelection();

        bundle1.setClientId("CLIENT-1");
        bundle2.setClientId("CLIENT-2");

        // Assert
        assertNotEquals(bundle1.getClientId(), bundle2.getClientId());
        assertNotSame(bundle1, bundle2);
    }

    @Test
    @DisplayName("Should handle special characters in strings")
    void testSpecialCharacters() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        String specialValue = "VALUE-#@!$%^&*()-_=+[]{};:',.<>?/|\\`~";

        // Act
        bundle.setClientId(specialValue);

        // Assert
        assertEquals(specialValue, bundle.getClientId());
    }

    @Test
    @DisplayName("Should handle very long strings")
    void testLongStrings() {
        // Arrange
        BundleSelection bundle = new BundleSelection();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("LONG-VALUE-");
        }
        String longValue = sb.toString();

        // Act
        bundle.setClientId(longValue);

        // Assert
        assertEquals(longValue, bundle.getClientId());
    }
}
