package com.tsengfhy.elaphure.web.cors;

import com.tsengfhy.elaphure.utils.JsonUtils;
import com.tsengfhy.elaphure.utils.MessageUtils;
import com.tsengfhy.elaphure.utils.ResponseUtils;
import com.tsengfhy.elaphure.web.WebMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.cors.DefaultCorsProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RestCorsProcessor extends DefaultCorsProcessor {

    @Override
    protected void rejectRequest(ServerHttpResponse response) throws IOException {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getBody().write(JsonUtils.toJson(ResponseUtils.failure(HttpStatus.FORBIDDEN, MessageUtils.getMessage(WebMessages.CORS_FORBIDDEN.getKey(), WebMessages.CORS_FORBIDDEN.getMessage()))).getBytes(StandardCharsets.UTF_8));
        response.flush();
    }
}
