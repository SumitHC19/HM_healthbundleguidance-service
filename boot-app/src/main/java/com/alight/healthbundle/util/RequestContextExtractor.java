package com.alight.healthbundle.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.alight.healthbundle.exceptions.BadRequestException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestContextExtractor {

    private final TokenParser tokenParser;

    public RequestContext extractRequestContext(
            String token,
            String requestHeader,
            String platformInternalIdHeader) {

        String clientId = tokenParser.extractClientIdFromRequestHeader(requestHeader);

        if (clientId == null || clientId.isBlank()) {
            log.warn("Client ID is required but missing from request header");
            throw new BadRequestException("Client ID is required in request header");
        }

        String platformInternalId = null;

        if (tokenParser.isTokenParsable(token)) {
            platformInternalId = tokenParser.extractPlatformInternalIdFromToken(token);
        }

        if (platformInternalId == null || platformInternalId.isBlank()) {
            platformInternalId = platformInternalIdHeader;

            if (platformInternalId == null || platformInternalId.isBlank()) {
                log.warn("Platform internal ID is required but missing from both token and header");
                throw new BadRequestException("Platform internal ID is required in token or request header");
            }
        }

        return new RequestContext(clientId, platformInternalId);
    }
}
