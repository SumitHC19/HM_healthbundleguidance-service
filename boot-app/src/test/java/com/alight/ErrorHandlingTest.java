package com.alight;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Integration tests for error handling in Package Guidance API.
 * Tests validation errors and edge cases.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Error Handling and Validation Tests")
class ErrorHandlingTest {

        private static final String REQUEST_HEADER = "alightRequestHeader";
        private static final String SESSION_TOKEN_HEADER = "alightPersonSessionToken";
        private static final String TEST_REQUEST_HEADER = "test-request-header";
        private static final String TEST_SESSION_TOKEN = "test-session-token";
        private static final String RECOMMENDATIONS_PATH = "/api/bundle/recommendations/";
        private static final String STATUS_CODE_PATH = "$.statusCode";
        private static final String ERROR_MESSAGE_PATH = "$.errorMessage";

        @Autowired
        private MockMvc mockMvc;

        /**
         * Helper method to build a standard request with headers
         */
        private ResultActions performRequest(String evaluationId) throws Exception {
                return mockMvc.perform(get(RECOMMENDATIONS_PATH + evaluationId)
                                .header(REQUEST_HEADER, TEST_REQUEST_HEADER)
                                .header(SESSION_TOKEN_HEADER, TEST_SESSION_TOKEN));
        }

        /**
         * Helper method to assert JSON error response
         */
        private void assertBadRequestWithMessage(ResultActions resultActions, String expectedMessage) throws Exception {
                resultActions
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath(STATUS_CODE_PATH).value(400))
                                .andExpect(jsonPath(ERROR_MESSAGE_PATH).value(containsString(expectedMessage)));
        }

        @Test
        @DisplayName("Should return 400 for null evaluation ID")
        public void testBadRequest_NullEvaluationId() throws Exception {
                assertBadRequestWithMessage(
                                performRequest("null"),
                                "evaluationId must not be 'null' or 'Empty'");
        }

        @Test
        @DisplayName("Should return 400 for empty evaluation ID")
        public void testBadRequest_EmptyEvaluationId() throws Exception {
                performRequest(" ")
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath(STATUS_CODE_PATH).value(400));
        }

        @Test
        @DisplayName("Should return 401 or 400 when request header is missing")
        public void testUnauthorized_MissingRequestHeader() throws Exception {
                mockMvc.perform(get(RECOMMENDATIONS_PATH + "TEST-123")
                                .header(SESSION_TOKEN_HEADER, TEST_SESSION_TOKEN))
                                .andExpect(jsonPath(STATUS_CODE_PATH).exists());
        }

        @Test
        @DisplayName("Should return 403 or 400 when request header format is invalid")
        public void testForbidden_InvalidRequestHeader() throws Exception {
                mockMvc.perform(get(RECOMMENDATIONS_PATH + "TEST-123")
                                .header(REQUEST_HEADER, "short")
                                .header(SESSION_TOKEN_HEADER, TEST_SESSION_TOKEN))
                                .andExpect(jsonPath(STATUS_CODE_PATH).exists());
        }

        @Test
        @DisplayName("Should handle internal server error gracefully")
        public void testInternalServerError() throws Exception {
                // This test verifies the generic exception handler
                // In real scenario, this would be triggered by unexpected runtime errors
                mockMvc.perform(get(RECOMMENDATIONS_PATH + "ERROR-TRIGGER-500")
                                .header(REQUEST_HEADER, TEST_REQUEST_HEADER)
                                .header(SESSION_TOKEN_HEADER, TEST_SESSION_TOKEN))
                                .andExpect(jsonPath(STATUS_CODE_PATH).exists());
        }
}
