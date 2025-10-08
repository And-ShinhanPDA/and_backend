package com.example.alert_module.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Map;

public class JwtSimpleParser {

    /**
     * JWT에서 userId(sub 또는 userId 필드)를 추출합니다.
     * (서명 검증은 하지 않으며, payload만 Base64 디코딩합니다)
     */
    public static Long extractUserId(String token) {
        try {
            // "Bearer " 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // header.payload.signature 구조 분리
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("잘못된 JWT 형식입니다.");
            }

            // payload Base64 디코딩
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);

            // sub 또는 userId 필드에서 userId 추출
            if (payload.containsKey("sub")) {
                return Long.valueOf(payload.get("sub").toString());
            } else if (payload.containsKey("userId")) {
                return Long.valueOf(payload.get("userId").toString());
            } else {
                throw new IllegalArgumentException("JWT에 userId(sub) 정보가 없습니다.");
            }

        } catch (Exception e) {
            throw new RuntimeException("JWT 파싱 실패: " + e.getMessage());
        }
    }
}
