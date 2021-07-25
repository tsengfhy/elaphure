package com.tsengfhy.elaphure.web.servlet.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tsengfhy.elaphure.utils.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParameterMappingRequestWrapper extends HttpServletRequestWrapper {

    private final Function<String, String> mapper;
    private byte[] body;

    public ParameterMappingRequestWrapper(HttpServletRequest request, Function<String, String> mapper) {
        super(request);
        Assert.notNull(mapper, "Mapper must not be null");
        this.mapper = mapper;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return super.getParameterMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> this.mapParameterValues(entry.getValue())));
    }

    @Override
    public String[] getParameterValues(String name) {
        return Optional.ofNullable(super.getParameterValues(name))
                .map(this::mapParameterValues)
                .orElse(null);
    }

    @Override
    public String getParameter(String name) {
        return Optional.ofNullable(super.getParameter(name))
                .map(this::mapParameterValue)
                .orElse(null);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (shouldParseBody()) {
            if (body == null) {
                body = handleParseBody(IOUtils.toByteArray(super.getInputStream()));
            }
            return new DelegatingServletInputStream(new ByteArrayInputStream(body));
        }

        return super.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), this.getCharacterEncoding()));
    }

    protected boolean shouldParseBody() {
        return StringUtils.contains(this.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    }

    protected byte[] handleParseBody(byte[] source) {
        return JsonUtils.toJson(mapParameterBody(JsonUtils.fromJson(new String(source), JsonNode.class))).getBytes();
    }

    private JsonNode mapParameterBody(JsonNode json) {
        if (Objects.isNull(json) || json.isNull()) {
            return json;
        } else if (json.isValueNode()) {
            return JsonNodeFactory.instance.textNode(mapParameterValue(json.asText()));
        } else if (json.isArray()) {
            ArrayNode array = JsonNodeFactory.instance.arrayNode();
            for (JsonNode node : json) {
                array.add(mapParameterBody(node));
            }
            return array;
        } else if (json.isObject()) {
            ObjectNode object = JsonNodeFactory.instance.objectNode();
            Iterator<Map.Entry<String, JsonNode>> it = json.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                object.set(entry.getKey(), mapParameterBody(entry.getValue()));
            }
            return object;
        }
        return json;
    }

    private String[] mapParameterValues(String[] values) {
        return Arrays.stream(values).map(this::mapParameterValue).toArray(String[]::new);
    }

    protected String mapParameterValue(String value) {
        return mapper.apply(value);
    }

    /**
     * Copy from {@link org.springframework.mock.web.DelegatingServletInputStream}
     */
    private static class DelegatingServletInputStream extends ServletInputStream {

        private final InputStream is;
        private boolean finished = false;

        public DelegatingServletInputStream(InputStream is) {
            Assert.notNull(is, "Source InputStream must not be null");
            this.is = is;
        }

        @Override
        public int read() throws IOException {
            int data = this.is.read();
            if (data == -1) {
                this.finished = true;
            }
            return data;
        }

        @Override
        public int available() throws IOException {
            return this.is.available();
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.is.close();
        }

        @Override
        public boolean isFinished() {
            return this.finished;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }
}
