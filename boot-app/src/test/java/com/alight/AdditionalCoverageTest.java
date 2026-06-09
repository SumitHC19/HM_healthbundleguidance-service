package com.alight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.alight.healthbundle.document.EvaluationDocument;
import com.alight.healthbundle.helper.EvaluationMongoHelper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Additional tests to achieve 90%+ code coverage.
 * Focuses on remaining untested code paths and branches.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Additional Coverage Tests")
class AdditionalCoverageTest {

        private static final String REQUEST_HEADER = "alightRequestHeader";
        private static final String SESSION_TOKEN_HEADER = "alightPersonSessionToken";
        private static final String VALID_HEADER = "valid-header-1234567890";
        private static final String VALID_TOKEN = "valid-token-0987654321";
        private static final String RECOMMENDATIONS_PATH = "/api/bundle/recommendations/";
        private static final String SAVE_PATH = "/api/bundle/recommendations";

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private EvaluationMongoHelper evaluationMongoHelper;

        @Test
        @DisplayName("GET with data found in database")
        void testGetRealDataFound() throws Exception {
                EvaluationDocument doc = createDoc("REAL-DATA-1");
                when(evaluationMongoHelper.findByEvaluationId("REAL-DATA-1"))
                                .thenReturn(Optional.of(doc));

                mockMvc.perform(get(RECOMMENDATIONS_PATH + "REAL-DATA-1")
                                .header(REQUEST_HEADER, VALID_HEADER)
                                .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET request retrieves data from database")
        void testGetFromDatabase() throws Exception {
                EvaluationDocument doc = createDoc("DEFAULT-TEST");
                when(evaluationMongoHelper.findByEvaluationId("DEFAULT-TEST"))
                                .thenReturn(Optional.of(doc));

                mockMvc.perform(get(RECOMMENDATIONS_PATH + "DEFAULT-TEST")
                                .header(REQUEST_HEADER, VALID_HEADER)
                                .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("PUT with complex bundle structure")
        void testPutComplexBundles() throws Exception {
                String request = """
                                {
                                    "eventType": "evaluation_result",
                                    "evaluationId": "COMPLEX-123",
                                    "evaluationType": "selectsmart:bundles",
                                    "evaluationStatus": "success",
                                    "evaluationOutput": {
                                        "bundles": [
                                            {"bundleRank": 1, "bundleId": "b1", "bundleName": "Bundle One"},
                                            {"bundleRank": 2, "bundleId": "b2", "bundleName": "Bundle Two"},
                                            {"bundleRank": 3, "bundleId": "b3", "bundleName": "Bundle Three"}
                                        ]
                                    }
                                }
                                """;

                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenReturn(createDoc("COMPLEX-123"));

                mockMvc.perform(put(SAVE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                                .header(REQUEST_HEADER, VALID_HEADER)
                                .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Multiple GET operations in sequence")
        void testMultipleGetOperations() throws Exception {
                for (int i = 1; i <= 5; i++) {
                        String evalId = "SEQ-GET-" + i;
                        when(evaluationMongoHelper.findByEvaluationId(evalId))
                                        .thenReturn(Optional.of(createDoc(evalId)));

                        mockMvc.perform(get(RECOMMENDATIONS_PATH + evalId)
                                        .header(REQUEST_HEADER, VALID_HEADER)
                                        .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                        .andExpect(status().isOk());
                }
        }

        @Test
        @DisplayName("Multiple PUT operations in sequence")
        void testMultiplePutOperations() throws Exception {
                for (int i = 1; i <= 5; i++) {
                        String evalId = "SEQ-PUT-" + i;
                        String request = String.format("""
                                        {
                                            "eventType": "evaluation_result",
                                            "evaluationId": "%s",
                                            "evaluationType": "selectsmart:bundles",
                                            "evaluationStatus": "success",
                                            "evaluationOutput": {
                                                "bundles": [{"bundleRank": %d}]
                                            }
                                        }
                                        """, evalId, i);

                        when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                        .thenReturn(createDoc(evalId));

                        mockMvc.perform(put(SAVE_PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(request)
                                        .header(REQUEST_HEADER, VALID_HEADER)
                                        .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                        .andExpect(status().isOk());
                }
        }

        @Test
        @DisplayName("GET with multiple different evaluation IDs")
        void testMultipleDifferentIds() throws Exception {
                String[] ids = { "DATA-A", "DATA-B", "DATA-C", "DATA-D", "DATA-E" };

                for (String id : ids) {
                        EvaluationDocument doc = createDoc(id);
                        when(evaluationMongoHelper.findByEvaluationId(id))
                                        .thenReturn(Optional.of(doc));

                        mockMvc.perform(get(RECOMMENDATIONS_PATH + id)
                                        .header(REQUEST_HEADER, VALID_HEADER)
                                        .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.evaluationId").value(id));
                }
        }

        @Test
        @DisplayName("PUT with various eventType values")
        void testPutDifferentEventTypes() throws Exception {
                String[] eventTypes = { "evaluation_result", "custom_event", "test_event" };

                for (int i = 0; i < eventTypes.length; i++) {
                        String request = String.format("""
                                        {
                                            "eventType": "%s",
                                            "evaluationId": "EVENT-%d",
                                            "evaluationType": "selectsmart:bundles",
                                            "evaluationStatus": "success",
                                            "evaluationOutput": {
                                                "bundles": [{"bundleRank": 1}]
                                            }
                                        }
                                        """, eventTypes[i], i);

                        when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                        .thenReturn(createDoc("EVENT-" + i));

                        mockMvc.perform(put(SAVE_PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(request)
                                        .header(REQUEST_HEADER, VALID_HEADER)
                                        .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                        .andExpect(status().isOk());
                }
        }

        @Test
        @DisplayName("PUT with various evaluationType values")
        void testPutDifferentEvaluationTypes() throws Exception {
                String[] evalTypes = { "selectsmart:bundles", "custom:type", "test:type" };

                for (int i = 0; i < evalTypes.length; i++) {
                        String request = String.format("""
                                        {
                                            "eventType": "evaluation_result",
                                            "evaluationId": "TYPE-%d",
                                            "evaluationType": "%s",
                                            "evaluationStatus": "success",
                                            "evaluationOutput": {
                                                "bundles": [{"bundleRank": 1}]
                                            }
                                        }
                                        """, i, evalTypes[i]);

                        when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                        .thenReturn(createDoc("TYPE-" + i));

                        mockMvc.perform(put(SAVE_PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(request)
                                        .header(REQUEST_HEADER, VALID_HEADER)
                                        .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                        .andExpect(status().isOk());
                }
        }

        @Test
        @DisplayName("GET and PUT cycle for same evaluation ID")
        void testGetPutCycle() throws Exception {
                String evalId = "CYCLE-TEST";

                // PUT first
                String request = String.format("""
                                {
                                    "eventType": "evaluation_result",
                                    "evaluationId": "%s",
                                    "evaluationType": "selectsmart:bundles",
                                    "evaluationStatus": "success",
                                    "evaluationOutput": {
                                        "bundles": [{"bundleRank": 1}]
                                    }
                                }
                                """, evalId);

                EvaluationDocument doc = createDoc(evalId);
                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenReturn(doc);

                mockMvc.perform(put(SAVE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                                .header(REQUEST_HEADER, VALID_HEADER)
                                .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                .andExpect(status().isOk());

                // Then GET
                when(evaluationMongoHelper.findByEvaluationId(evalId))
                                .thenReturn(Optional.of(doc));

                mockMvc.perform(get(RECOMMENDATIONS_PATH + evalId)
                                .header(REQUEST_HEADER, VALID_HEADER)
                                .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.evaluationId").value(evalId));
        }

        @Test
        @DisplayName("PUT with long evaluation ID")
        void testPutLongEvaluationId() throws Exception {
                String longId = "LONG-ID-" + "X".repeat(200);
                String request = String.format("""
                                {
                                    "eventType": "evaluation_result",
                                    "evaluationId": "%s",
                                    "evaluationType": "selectsmart:bundles",
                                    "evaluationStatus": "success",
                                    "evaluationOutput": {
                                        "bundles": [{"bundleRank": 1}]
                                    }
                                }
                                """, longId);

                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenReturn(createDoc(longId));

                mockMvc.perform(put(SAVE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                                .header(REQUEST_HEADER, VALID_HEADER)
                                .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET with numeric evaluation ID")
        void testGetNumericId() throws Exception {
                String numericId = "1234567890";
                when(evaluationMongoHelper.findByEvaluationId(numericId))
                                .thenReturn(Optional.of(createDoc(numericId)));

                mockMvc.perform(get(RECOMMENDATIONS_PATH + numericId)
                                .header(REQUEST_HEADER, VALID_HEADER)
                                .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("PUT with numeric evaluation ID")
        void testPutNumericId() throws Exception {
                String numericId = "9876543210";
                String request = String.format("""
                                {
                                    "eventType": "evaluation_result",
                                    "evaluationId": "%s",
                                    "evaluationType": "selectsmart:bundles",
                                    "evaluationStatus": "success",
                                    "evaluationOutput": {
                                        "bundles": [{"bundleRank": 1}]
                                    }
                                }
                                """, numericId);

                when(evaluationMongoHelper.save(any(EvaluationDocument.class)))
                                .thenReturn(createDoc(numericId));

                mockMvc.perform(put(SAVE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                                .header(REQUEST_HEADER, VALID_HEADER)
                                .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET with UUID-like evaluation ID")
        void testGetUuidId() throws Exception {
                String uuidId = "550e8400-e29b-41d4-a716-446655440000";
                when(evaluationMongoHelper.findByEvaluationId(uuidId))
                                .thenReturn(Optional.of(createDoc(uuidId)));

                mockMvc.perform(get(RECOMMENDATIONS_PATH + uuidId)
                                .header(REQUEST_HEADER, VALID_HEADER)
                                .header(SESSION_TOKEN_HEADER, VALID_TOKEN))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET with various valid header combinations")
        void testGetDifferentHeaders() throws Exception {
                String[][] headers = {
                                { "header-value-1234567890", "token-value-0987654321" },
                                { "UPPERCASE-HEADER-12345", "lowercase-token-67890" },
                                { "MixedCase-Header-ABC", "MixedCase-Token-XYZ" }
                };

                for (String[] headerPair : headers) {
                        when(evaluationMongoHelper.findByEvaluationId("HEADER-TEST"))
                                        .thenReturn(Optional.of(createDoc("HEADER-TEST")));

                        mockMvc.perform(get(RECOMMENDATIONS_PATH + "HEADER-TEST")
                                        .header(REQUEST_HEADER, headerPair[0])
                                        .header(SESSION_TOKEN_HEADER, headerPair[1]))
                                        .andExpect(status().isOk());
                }
        }

        private EvaluationDocument createDoc(String evalId) {
                EvaluationDocument doc = new EvaluationDocument();
                doc.setId("mongo-" + evalId);
                doc.setEvaluationId(evalId);
                doc.setEventType("evaluation_result");
                doc.setEvaluationType("selectsmart:bundles");
                doc.setEvaluationStatus("success");
                return doc;
        }
}
