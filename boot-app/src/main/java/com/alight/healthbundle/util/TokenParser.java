package com.alight.healthbundle.util;

import com.alight.asg.model.header.v1_0.RequestHeader;
import com.alight.asg.model.token.v1_0.IdMapping;
import com.alight.asg.model.token.v1_0.PersonSessionToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenParser {

    private final ObjectMapper objectMapper;

    public String extractPlatformInternalIdFromToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String decodedToken = decodeTokenIfNeeded(token);

        try {
            PersonSessionToken personSessionToken = PersonSessionToken.parse(decodedToken);

            if (personSessionToken.getIdMapping() != null) {
                return personSessionToken.getIdMapping().stream()
                        .map(IdMapping::getPlatformInternalId)
                        .filter(id -> id != null && !id.isBlank())
                        .findFirst()
                        .orElse(null);
            }

            return null;

        } catch (Exception e) {
            log.error("Failed to parse alightPersonSessionToken: {}", e.getMessage());
            return null;
        }
    }

    private String decodeTokenIfNeeded(String token) {
        if (token == null || token.isBlank() || token.trim().startsWith("{")) {
            return token;
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String decoded = new String(decodedBytes, StandardCharsets.UTF_8);

            if (decoded.length() > 0 && !decoded.trim().startsWith("{")) {
                boolean hasNonPrintable = decoded.chars()
                        .limit(100)
                        .anyMatch(c -> c < 32 && c != 9 && c != 10 && c != 13);

                if (hasNonPrintable) {
                    log.error("Token appears to be encrypted. Service requires decrypted JSON token.");
                    return token;
                }
            }

            return decoded;
        } catch (IllegalArgumentException e) {
            return token;
        }
    }

    public boolean isTokenParsable(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        String decodedToken = decodeTokenIfNeeded(token);

        try {
            PersonSessionToken.parse(decodedToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractClientIdFromRequestHeader(String requestHeader) {
        if (requestHeader == null || requestHeader.isBlank()) {
            return null;
        }

        try {
            RequestHeader header = RequestHeader.parse(requestHeader);
            String clientId = header.getClientId();
            return (clientId != null && !clientId.isBlank()) ? clientId : null;
        } catch (Exception e) {
            log.error("Failed to parse alightRequestHeader: {}", e.getMessage());
            return null;
        }
    }

    public String extractPlatformInternalIdFromRequestHeader(String requestHeader) {
        if (requestHeader == null || requestHeader.isBlank()) {
            return null;
        }

        try {
            RequestHeader header = RequestHeader.parse(requestHeader);
            String subjectId = header.getSubjectId();
            return (subjectId != null && !subjectId.isBlank()) ? subjectId : null;
        } catch (Exception e) {
            String trimmed = requestHeader.trim();
            return trimmed.isEmpty() ? null : trimmed;
        }
    }
}
