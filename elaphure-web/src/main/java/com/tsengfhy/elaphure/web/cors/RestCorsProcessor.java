package com.tsengfhy.elaphure.web.cors;

import com.tsengfhy.elaphure.constants.WebMessages;
import com.tsengfhy.elaphure.utils.JsonUtils;
import com.tsengfhy.elaphure.utils.MessageUtils;
import com.tsengfhy.elaphure.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.cors.DefaultCorsProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RestCorsProcessor extends DefaultCorsProcessor {

    @Override
    protected void rejectRequest(ServerHttpResponse response) throws IOException {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getBody().write(
                JsonUtils.toJson(
                        ResponseUtils.failure(
                                HttpStatus.FORBIDDEN,
                                MessageUtils.getMessage(WebMessages.CORS_REJECT, WebMessages.CORS_REJECT)
                        )
                ).getBytes(StandardCharsets.UTF_8)
        );
        response.flush();
    }
}
