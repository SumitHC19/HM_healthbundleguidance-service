package com.alight.healthbundle.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.alight.healthbundle.exceptions.BadRequestException;
import com.alight.healthbundle.exceptions.NoObjectFoundException;
import com.alight.healthbundle.model.BundleSelection;
import com.alight.healthbundle.model.enums.FeaturedAs;
import com.alight.healthbundle.service.BundleSelectionService;

@ExtendWith(MockitoExtension.class)
class BundleSelectionControllerTest {

        @Mock
        private BundleSelectionService bundleSelectionService;

        @InjectMocks
        private BundleSelectionController controller;

        private static final String REQUEST_HEADER = "{\"clientId\":\"19968\"}";
        private static final String TOKEN = "test-token";
        private static final String PLATFORM_ID_HEADER = "12853765";
        private static final String BUSINESS_PROCESS_REF_ID = "bp-123";
        private static final String EVALUATION_ID = "eval-456";

        @Test
        @DisplayName("GET bundle selection returns OK with bundle selection")
        void testGetBundleSelection_Success() {
                BundleSelection bundleSelection = createBundleSelection();
                when(bundleSelectionService.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID, PLATFORM_ID_HEADER))
                                .thenReturn(bundleSelection);

                ResponseEntity<?> response = controller.getBundleSelection(
                                REQUEST_HEADER, TOKEN, PLATFORM_ID_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(((BundleSelection) response.getBody()).getBusinessProcessReferenceId())
                                .isEqualTo(BUSINESS_PROCESS_REF_ID);
                verify(bundleSelectionService).getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID, PLATFORM_ID_HEADER);
        }

        @Test
        @DisplayName("GET bundle selection without evaluationId")
        void testGetBundleSelection_WithoutEvaluationId() {
                BundleSelection bundleSelection = createBundleSelection();
                when(bundleSelectionService.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, null, PLATFORM_ID_HEADER))
                                .thenReturn(bundleSelection);

                ResponseEntity<?> response = controller.getBundleSelection(
                                REQUEST_HEADER, TOKEN, PLATFORM_ID_HEADER, BUSINESS_PROCESS_REF_ID, null);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();
                verify(bundleSelectionService).getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, null, PLATFORM_ID_HEADER);
        }

        @Test
        @DisplayName("GET bundle selection returns NO_CONTENT when not found")
        void testGetBundleSelection_NOCONTENT() {
                when(bundleSelectionService.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID, PLATFORM_ID_HEADER))
                                .thenThrow(new NoObjectFoundException("Bundle selection not found"));

                ResponseEntity<?> response = controller.getBundleSelection(
                                REQUEST_HEADER, TOKEN, PLATFORM_ID_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody()).isInstanceOf(java.util.Map.class);
                java.util.Map<String, String> body = (java.util.Map<String, String>) response.getBody();
                assertThat(body.get("message")).isEqualTo("Bundle selection not found");
                verify(bundleSelectionService).getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID, PLATFORM_ID_HEADER);
        }

        @Test
        @DisplayName("GET bundle selection throws exception for unexpected errors")
        void testGetBundleSelection_UnexpectedError() {
                when(bundleSelectionService.getBundleSelection(
                                TOKEN, REQUEST_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID, PLATFORM_ID_HEADER))
                                .thenThrow(new RuntimeException("Database error"));

                assertThatThrownBy(() -> controller.getBundleSelection(
                                REQUEST_HEADER, TOKEN, PLATFORM_ID_HEADER, BUSINESS_PROCESS_REF_ID, EVALUATION_ID))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Database error");
        }

        @Test
        @DisplayName("PUT bundle selection returns OK with saved bundle selection")
        void testPutBundleSelection_Success() {
                BundleSelection bundleSelection = createBundleSelection();
                BundleSelection savedBundleSelection = createBundleSelection();
                savedBundleSelection.setLastModifiedTimeStamp(new Date().toString()); // use Date for realism

                when(bundleSelectionService.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .thenReturn(savedBundleSelection);

                ResponseEntity<BundleSelection> response = controller.putBundleSelection(
                                REQUEST_HEADER, TOKEN, PLATFORM_ID_HEADER, bundleSelection);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().getLastModifiedTimeStamp()).isNotNull();
                verify(bundleSelectionService).saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);
        }

        @Test
        @DisplayName("PUT bundle selection without platformInternalId header")
        void testPutBundleSelection_WithoutPlatformIdHeader() {
                BundleSelection bundleSelection = createBundleSelection();
                BundleSelection savedBundleSelection = createBundleSelection();

                when(bundleSelectionService.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, null))
                                .thenReturn(savedBundleSelection);

                ResponseEntity<BundleSelection> response = controller.putBundleSelection(
                                REQUEST_HEADER, TOKEN, null, bundleSelection);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                verify(bundleSelectionService).saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, null);
        }

        @Test
        @DisplayName("PUT bundle selection throws BadRequestException for invalid data (featuredAs null)")
        void testPutBundleSelection_BadRequest() {
                BundleSelection bundleSelection = createBundleSelection();
                bundleSelection.setFeaturedAs(null); // enum can be null in DTO

                when(bundleSelectionService.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .thenThrow(new BadRequestException("featuredAs is required"));

                assertThatThrownBy(() -> controller.putBundleSelection(
                                REQUEST_HEADER, TOKEN, PLATFORM_ID_HEADER, bundleSelection))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessageContaining("featuredAs is required");
        }

        @Test
        @DisplayName("PUT bundle selection throws exception for unexpected errors")
        void testPutBundleSelection_UnexpectedError() {
                BundleSelection bundleSelection = createBundleSelection();

                when(bundleSelectionService.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .thenThrow(new RuntimeException("Database error"));

                assertThatThrownBy(() -> controller.putBundleSelection(
                                REQUEST_HEADER, TOKEN, PLATFORM_ID_HEADER, bundleSelection))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Database error");
        }

        @Test
        @DisplayName("PUT bundle selection with none and returns OK with saved bundle selection")
        void testPutBundleSelectionNone_Success() {
                BundleSelection bundleSelection = createBundleSelection();
                bundleSelection.setFeaturedAs("none"); // set to none for this test case

                BundleSelection savedBundleSelection = createBundleSelection();
                savedBundleSelection.setLastModifiedTimeStamp(new Date().toString()); // use Date for realism

                when(bundleSelectionService.saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER))
                                .thenReturn(savedBundleSelection);

                ResponseEntity<BundleSelection> response = controller.putBundleSelection(
                                REQUEST_HEADER, TOKEN, PLATFORM_ID_HEADER, bundleSelection);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().getLastModifiedTimeStamp()).isNotNull();
                verify(bundleSelectionService).saveBundleSelection(
                                bundleSelection, TOKEN, REQUEST_HEADER, PLATFORM_ID_HEADER);
        }

        private BundleSelection createBundleSelection() {
                BundleSelection bundleSelection = new BundleSelection();
                bundleSelection.setBusinessProcessReferenceId(BUSINESS_PROCESS_REF_ID);
                bundleSelection.setEvaluationId(EVALUATION_ID);
                bundleSelection.setClientId("19968");
                bundleSelection.setPlatformInternalId("12853765");
                bundleSelection.setFeaturedAs("balanced"); // enum now
                return bundleSelection;
        }
}
