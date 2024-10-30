package com.spring.boot.identity_service.util;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonParserUtils {
    // Hàm trợ giúp để lấy giá trị chuỗi từ JsonNode
    public static String getJsonStringValue(JsonNode jsonNode, String fieldName) {
        JsonNode node = jsonNode.get(fieldName);
        return (node != null) ? node.asText() : "null";
    }

    // Hàm trợ giúp để lấy giá trị số nguyên từ JsonNode
    public static int getJsonIntValue(JsonNode jsonNode, String fieldName) {
        JsonNode node = jsonNode.get(fieldName);
        return (node != null) ? node.asInt() : 0; // Giá trị mặc định là 0 nếu trường đó không tồn tại hoặc có giá trị null
    }
}
