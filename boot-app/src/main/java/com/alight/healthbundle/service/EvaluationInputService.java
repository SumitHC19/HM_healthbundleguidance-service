package com.alight.healthbundle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.alight.healthbundle.dao.EvaluationInputDao;
import com.alight.healthbundle.exceptions.BadRequestException;
import com.alight.healthbundle.model.EvaluationInputRequest;
import com.alight.healthbundle.util.RequestContext;
import com.alight.healthbundle.util.RequestContextExtractor;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationInputService {

    private final EvaluationInputDao dao;
    private final RequestContextExtractor requestContextExtractor;

    public EvaluationInputRequest getEvaluationInput(
            String token,
            String requestHeader,
            String businessProcessReferenceId,
            String evaluationId,
            String platformInternalIdHeader) {

        if (businessProcessReferenceId == null || businessProcessReferenceId.isBlank()) {
            throw new BadRequestException("Business process reference ID is required");
        }

        RequestContext context = requestContextExtractor.extractRequestContext(
                token, requestHeader, platformInternalIdHeader);

        try {
            return dao.getEvaluationInputByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId(
                    context.getClientId(), context.getPlatformInternalId(), businessProcessReferenceId, evaluationId);
        } catch (Exception e) {
            log.error("Error retrieving evaluation input: {}", e.getMessage());
            throw e;
        }
    }

    public EvaluationInputRequest saveEvaluationInput(
            EvaluationInputRequest request,
            String token,
            String requestHeader,
            String platformInternalIdHeader) {

        RequestContext context = requestContextExtractor.extractRequestContext(
                token, requestHeader, platformInternalIdHeader);

        if (request.getBusinessProcessReferenceId() == null || request.getBusinessProcessReferenceId().isBlank()) {
            throw new BadRequestException("businessProcessReferenceId is required in request body");
        }

        if (request.getSavviRequest() == null) {
            throw new BadRequestException("savviRequest is required in request body");
        }

        request.setPlatformInternalId(context.getPlatformInternalId());
        request.setClientId(context.getClientId());

        try {
            return dao.saveEvaluationInput(request, context.getPlatformInternalId());
        } catch (BadRequestException e) {
            log.warn("Validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error saving evaluation input: {}", e.getMessage(), e);
            throw e;
        }
    }
}
