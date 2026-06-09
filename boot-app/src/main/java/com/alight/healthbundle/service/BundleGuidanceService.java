package com.alight.healthbundle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.alight.healthbundle.dao.BundleGuidanceDao;
import com.alight.healthbundle.model.EvaluationResultsResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class BundleGuidanceService {

    private final BundleGuidanceDao bundleGuidanceDao;

    public EvaluationResultsResponse getRecommendations(
            String evalId,
            String alightRequestHeader,
            String alightPersonSessionToken) {

        if (bundleGuidanceDao == null) {
            throw new IllegalStateException("Database connection not available");
        }

        return bundleGuidanceDao.getRecommendations(evalId, alightRequestHeader, alightPersonSessionToken);
    }

    public EvaluationResultsResponse saveRecommendations(
            EvaluationResultsResponse evaluationResults,
            String alightRequestHeader,
            String alightPersonSessionToken) {

        if (bundleGuidanceDao == null) {
            throw new IllegalStateException("Database connection not available");
        }

        return bundleGuidanceDao.saveRecommendations(evaluationResults, alightRequestHeader, alightPersonSessionToken);
    }
}
