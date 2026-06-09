package com.alight;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.alight.healthbundle.document.EvaluationDocument;
import com.alight.healthbundle.helper.EvaluationMongoHelper;
import com.alight.healthbundle.model.EvaluationResultsResponse;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration tests for Bundle Guidance API endpoints.
 * Tests successful scenarios and parameter validation.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Bundle Guidance API Tests")
class BundleGuidanceTest {

    @MockitoBean
    private EvaluationMongoHelper evaluationMongoHelper;

    private static final String REQUEST_HEADER = "alightRequestHeader";
    private static final String SESSION_TOKEN_HEADER = "alightPersonSessionToken";
    private static final String TEST_REQUEST_HEADER = "test-request-header";
    private static final String TEST_SESSION_TOKEN = "test-session-token";
    private static final String TEST_EVALUATION_ID = "TEST-EVAL-ID-12345"; // Test evaluation ID
    private static final String RECOMMENDATIONS_PATH = "/api/bundle/recommendations/";
    private static final String SAVE_RECOMMENDATIONS_PATH = "/api/bundle/recommendations";

    @Autowired
    private MockMvc mockMvc;

    /**
     * Helper method to perform GET request with standard headers
     */
    private ResultActions performGetRequest(String evaluationId) throws Exception {
        return mockMvc.perform(get(RECOMMENDATIONS_PATH + evaluationId)
                .header(REQUEST_HEADER, TEST_REQUEST_HEADER)
                .header(SESSION_TOKEN_HEADER, TEST_SESSION_TOKEN));
    }

    /**
     * Helper method to perform PUT request with standard headers
     */
    private ResultActions performPutRequest(String jsonBody) throws Exception {
        return mockMvc.perform(put(SAVE_RECOMMENDATIONS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header(REQUEST_HEADER, TEST_REQUEST_HEADER)
                .header(SESSION_TOKEN_HEADER, TEST_SESSION_TOKEN));
    }

    @Test
    @DisplayName("Should return 204 NoContent for valid evaluation ID that doesn't exist")
    void testBasicRecommendationRequest() throws Exception {
        // Mock repository to return empty result (no data found)
        when(evaluationMongoHelper.findByEvaluationId(anyString())).thenReturn(Optional.empty());

        // Returns 204 since evaluation ID doesn't exist
        performGetRequest(TEST_EVALUATION_ID)
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @ValueSource(strings = { "null", " " })
    @DisplayName("Should return 400 Bad Request for invalid evaluation IDs")
    void testInvalidEvaluationIds(String invalidId) throws Exception {
        // Note: Empty string "" is not tested as it doesn't match the URL pattern
        performGetRequest(invalidId)
                .andExpect(status().isBadRequest());
    }

    // =============== PUT Endpoint Tests ===============

    @Test
    @DisplayName("Should return 200 OK when saving valid evaluation data")
    void testSaveRecommendations() throws Exception {
        // Prepare test data with at least one bundle
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "TEST-SAVE-123",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success",
                    "evaluationOutput": {
                        "bundles": [
                            {
                                "bundleRank": 1,
                                "bundleId": "bundle-123",
                                "bundleName": "Test Bundle"
                            }
                        ]
                    }
                }
                """;

        // Mock the save operation
        EvaluationDocument savedDocument = new EvaluationDocument();
        savedDocument.setId("mongo-generated-id");
        savedDocument.setEvaluationId("TEST-SAVE-123");
        savedDocument.setEventType("evaluation_result");
        savedDocument.setEvaluationType("selectsmart:bundles");
        savedDocument.setEvaluationStatus("success");

        when(evaluationMongoHelper.save(any(EvaluationDocument.class))).thenReturn(savedDocument);

        // Perform PUT request and verify response
        performPutRequest(requestBody)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationId").value("TEST-SAVE-123"))
                .andExpect(jsonPath("$.eventType").value("evaluation_result"))
                .andExpect(jsonPath("$.evaluationType").value("selectsmart:bundles"))
                .andExpect(jsonPath("$.evaluationStatus").value("success"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when evaluationId is null")
    void testSaveRecommendationsWithNullEvaluationId() throws Exception {
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": null,
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success"
                }
                """;

        performPutRequest(requestBody)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when evaluationId is empty string")
    void testSaveRecommendationsWithEmptyEvaluationId() throws Exception {
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success"
                }
                """;

        performPutRequest(requestBody)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when evaluationId is blank string")
    void testSaveRecommendationsWithBlankEvaluationId() throws Exception {
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "   ",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success"
                }
                """;

        performPutRequest(requestBody)
                .andExpect(status().isBadRequest());
    }

    @Test

    @DisplayName("Should save complete evaluation data with all fields")
    void testSaveRecommendationsWithCompleteData() throws Exception {
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "COMPLETE-TEST-456",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success",
                    "evaluationOutput": {
                        "bundles": [
                            {
                                "bundleRank": 1,
                                "bundleId": "bundle-123",
                                "bundleName": "Premium Plan"
                            }
                        ],
                        "accounts": {
                            "hsa": {
                                "accountType": "HSA",
                                "amount": 5000
                            }
                        }
                    }
                }
                """;

        EvaluationDocument savedDocument = new EvaluationDocument();
        savedDocument.setId("mongo-id-complete");
        savedDocument.setEvaluationId("COMPLETE-TEST-456");
        savedDocument.setEventType("evaluation_result");
        savedDocument.setEvaluationType("selectsmart:bundles");
        savedDocument.setEvaluationStatus("success");

        when(evaluationMongoHelper.save(any(EvaluationDocument.class))).thenReturn(savedDocument);

        performPutRequest(requestBody)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationId").value("COMPLETE-TEST-456"))
                .andExpect(jsonPath("$.eventType").value("evaluation_result"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when evaluationOutput is null")
    void testSaveRecommendationsWithNullEvaluationOutput() throws Exception {
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "TEST-NULL-OUTPUT",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success",
                    "evaluationOutput": null
                }
                """;

        performPutRequest(requestBody)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when bundles array is empty")
    void testSaveRecommendationsWithEmptyBundles() throws Exception {
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "TEST-EMPTY-BUNDLES",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success",
                    "evaluationOutput": {
                        "bundles": []
                    }
                }
                """;

        performPutRequest(requestBody)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully retrieve saved bundle guidance data")
    void testRetrieveSavedData() throws Exception {
        // Create mock saved data
        EvaluationResultsResponse savedData = new EvaluationResultsResponse();
        savedData.setEventType("evaluation_result");
        savedData.setEvaluationId("SAVED-123");
        savedData.setEvaluationType("selectsmart:bundles");
        savedData.setEvaluationStatus("success");

        when(evaluationMongoHelper.findByEvaluationId("SAVED-123"))
                .thenReturn(Optional.of(createMockDocument("SAVED-123")));

        performGetRequest("SAVED-123")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationId").value("SAVED-123"));
    }

    @Test
    @DisplayName("Should save data with minimum required fields")
    void testSaveWithMinimumFields() throws Exception {
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "MIN-FIELDS-123",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success",
                    "evaluationOutput": {
                        "bundles": [{"bundleRank": 1}]
                    }
                }
                """;

        EvaluationDocument savedDocument = new EvaluationDocument();
        savedDocument.setId("min-id");
        savedDocument.setEvaluationId("MIN-FIELDS-123");
        when(evaluationMongoHelper.save(any(EvaluationDocument.class))).thenReturn(savedDocument);

        performPutRequest(requestBody)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should successfully handle different eventType values")
    void testDifferentEventTypes() throws Exception {
        String requestBody = """
                {
                    "eventType": "custom_event_type",
                    "evaluationId": "EVENT-TYPE-TEST",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success",
                    "evaluationOutput": {
                        "bundles": [{"bundleRank": 1}]
                    }
                }
                """;

        EvaluationDocument savedDocument = new EvaluationDocument();
        savedDocument.setId("event-type-id");
        savedDocument.setEvaluationId("EVENT-TYPE-TEST");
        when(evaluationMongoHelper.save(any(EvaluationDocument.class))).thenReturn(savedDocument);

        performPutRequest(requestBody)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should successfully handle different evaluationType values")
    void testDifferentEvaluationTypes() throws Exception {
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "EVAL-TYPE-TEST",
                    "evaluationType": "custom:type",
                    "evaluationStatus": "success",
                    "evaluationOutput": {
                        "bundles": [{"bundleRank": 1}]
                    }
                }
                """;

        EvaluationDocument savedDocument = new EvaluationDocument();
        savedDocument.setId("eval-type-id");
        savedDocument.setEvaluationId("EVAL-TYPE-TEST");
        when(evaluationMongoHelper.save(any(EvaluationDocument.class))).thenReturn(savedDocument);

        performPutRequest(requestBody)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle large evaluation IDs")
    void testLargeEvaluationId() throws Exception {
        String largeId = "A".repeat(255);
        String requestBody = String.format("""
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "%s",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success",
                    "evaluationOutput": {
                        "bundles": [{"bundleRank": 1}]
                    }
                }
                """, largeId);

        EvaluationDocument savedDocument = new EvaluationDocument();
        savedDocument.setId("large-id");
        savedDocument.setEvaluationId(largeId);
        when(evaluationMongoHelper.save(any(EvaluationDocument.class))).thenReturn(savedDocument);

        performPutRequest(requestBody)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle evaluationId with special characters")
    void testSpecialCharactersInEvaluationId() throws Exception {
        String requestBody = """
                {
                    "eventType": "evaluation_result",
                    "evaluationId": "TEST-ID_123.456@SPECIAL",
                    "evaluationType": "selectsmart:bundles",
                    "evaluationStatus": "success",
                    "evaluationOutput": {
                        "bundles": [{"bundleRank": 1}]
                    }
                }
                """;

        EvaluationDocument savedDocument = new EvaluationDocument();
        savedDocument.setId("special-id");
        savedDocument.setEvaluationId("TEST-ID_123.456@SPECIAL");
        when(evaluationMongoHelper.save(any(EvaluationDocument.class))).thenReturn(savedDocument);

        performPutRequest(requestBody)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should retrieve data with different evaluation IDs")
    void testGetWithVariousEvaluationIds() throws Exception {
        String[] testIds = { "TEST-1", "TEST-2", "TEST-3", "ANOTHER-ID" };

        for (String testId : testIds) {
            when(evaluationMongoHelper.findByEvaluationId(testId))
                    .thenReturn(Optional.of(createMockDocument(testId)));

            performGetRequest(testId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.evaluationId").value(testId));
        }
    }

    @Test
    @DisplayName("Should handle multiple sequential save operations")
    void testMultipleSaveOperations() throws Exception {
        for (int i = 1; i <= 3; i++) {
            String requestBody = String.format("""
                    {
                        "eventType": "evaluation_result",
                        "evaluationId": "MULTI-SAVE-%d",
                        "evaluationType": "selectsmart:bundles",
                        "evaluationStatus": "success",
                        "evaluationOutput": {
                            "bundles": [{"bundleRank": %d}]
                        }
                    }
                    """, i, i);

            EvaluationDocument savedDocument = new EvaluationDocument();
            savedDocument.setId("multi-id-" + i);
            savedDocument.setEvaluationId("MULTI-SAVE-" + i);
            when(evaluationMongoHelper.save(any(EvaluationDocument.class))).thenReturn(savedDocument);

            performPutRequest(requestBody)
                    .andExpect(status().isOk());
        }
    }

    private EvaluationDocument createMockDocument(String evaluationId) {
        EvaluationDocument doc = new EvaluationDocument();
        doc.setId("mongo-id");
        doc.setEvaluationId(evaluationId);
        doc.setEventType("evaluation_result");
        doc.setEvaluationType("selectsmart:bundles");
        doc.setEvaluationStatus("success");
        return doc;
    }
}
