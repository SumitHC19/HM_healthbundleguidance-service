package com.alight.healthbundle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alight.healthbundle.dao.BundleGuidanceDao;
import com.alight.healthbundle.model.EvaluationResultsResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for BundleGuidanceService.
 * Achieves >90% coverage for service logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Bundle Guidance Service Tests")
class BundleGuidanceServiceTest {

        @Mock
        private BundleGuidanceDao bundleGuidanceDao;

        private BundleGuidanceService service;

        @BeforeEach
        void setUp() {
                service = new BundleGuidanceService(bundleGuidanceDao);
        }

        @Test
        @DisplayName("getRecommendations calls DAO and returns response")
        void testGetRecommendations() {

                EvaluationResultsResponse mockResponse = new EvaluationResultsResponse();
                mockResponse.setEvaluationId("TEST-456");

                when(bundleGuidanceDao.getRecommendations(anyString(), anyString(), anyString()))
                                .thenReturn(mockResponse);

                EvaluationResultsResponse response = service.getRecommendations(
                                "TEST-456",
                                "header-value",
                                "token-value");

                assertThat(response).isNotNull();
                assertThat(response.getEvaluationId()).isEqualTo("TEST-456");
                verify(bundleGuidanceDao).getRecommendations("TEST-456", "header-value", "token-value");
        }

        @Test
        @DisplayName("getRecommendations throws IllegalStateException when DAO is null")
        void testGetRecommendationsWithNullDao() {
                service = new BundleGuidanceService(null);

                assertThatThrownBy(() -> service.getRecommendations(
                                "TEST-789",
                                "header-value",
                                "token-value"))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Database connection not available");
        }

        @Test
        @DisplayName("saveRecommendations calls DAO and returns saved response")
        void testSaveRecommendations() {

                EvaluationResultsResponse requestData = new EvaluationResultsResponse();
                requestData.setEvaluationId("SAVE-123");

                EvaluationResultsResponse savedResponse = new EvaluationResultsResponse();
                savedResponse.setEvaluationId("SAVE-123");

                when(bundleGuidanceDao.saveRecommendations(any(), anyString(), anyString()))
                                .thenReturn(savedResponse);

                EvaluationResultsResponse response = service.saveRecommendations(
                                requestData,
                                "header-value",
                                "token-value");

                assertThat(response).isNotNull();
                assertThat(response.getEvaluationId()).isEqualTo("SAVE-123");
                verify(bundleGuidanceDao).saveRecommendations(requestData, "header-value", "token-value");
        }

        @Test
        @DisplayName("saveRecommendations throws IllegalStateException when DAO is null")
        void testSaveRecommendationsWithNullDao() {
                service = new BundleGuidanceService(null);

                EvaluationResultsResponse requestData = new EvaluationResultsResponse();
                requestData.setEvaluationId("SAVE-ERROR");

                assertThatThrownBy(() -> service.saveRecommendations(
                                requestData,
                                "header-value",
                                "token-value"))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Database connection not available");
        }

        @Test
        @DisplayName("getRecommendations with various evaluation IDs calls DAO correctly")
        void testGetRecommendationsWithDifferentEvaluationIds() {
                String[] evaluationIds = { "UUID-STYLE", "12345", "PROD-TEST-999" };

                for (String evalId : evaluationIds) {
                        EvaluationResultsResponse mockResponse = new EvaluationResultsResponse();
                        mockResponse.setEvaluationId(evalId);

                        when(bundleGuidanceDao.getRecommendations(eq(evalId), anyString(), anyString()))
                                        .thenReturn(mockResponse);

                        EvaluationResultsResponse response = service.getRecommendations(
                                        evalId,
                                        "header",
                                        "token");

                        assertThat(response).isNotNull();
                        assertThat(response.getEvaluationId()).isEqualTo(evalId);
                }

                verify(bundleGuidanceDao, times(3)).getRecommendations(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Service handles empty headers gracefully")
        void testServiceWithEmptyHeaders() {
                EvaluationResultsResponse mockResponse = new EvaluationResultsResponse();
                mockResponse.setEvaluationId("EMPTY-HEADER-TEST");

                when(bundleGuidanceDao.getRecommendations(anyString(), anyString(), anyString()))
                                .thenReturn(mockResponse);

                EvaluationResultsResponse response = service.getRecommendations(
                                "EMPTY-HEADER-TEST",
                                "",
                                "");

                assertThat(response).isNotNull();
                verify(bundleGuidanceDao).getRecommendations("EMPTY-HEADER-TEST", "", "");
        }

        @Test
        @DisplayName("Service handles null evaluation ID in save request")
        void testSaveWithNullEvaluationId() {

                EvaluationResultsResponse requestData = new EvaluationResultsResponse();
                requestData.setEvaluationId(null);

                EvaluationResultsResponse savedResponse = new EvaluationResultsResponse();

                when(bundleGuidanceDao.saveRecommendations(any(), anyString(), anyString()))
                                .thenReturn(savedResponse);

                EvaluationResultsResponse response = service.saveRecommendations(
                                requestData,
                                "header",
                                "token");

                assertThat(response).isNotNull();
                verify(bundleGuidanceDao).saveRecommendations(requestData, "header", "token");
        }
}
