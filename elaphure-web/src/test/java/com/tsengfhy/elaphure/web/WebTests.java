package com.tsengfhy.elaphure.web;

import com.tsengfhy.elaphure.env.WebProperties;
import com.tsengfhy.elaphure.utils.JsonUtils;
import com.tsengfhy.elaphure.utils.XssUtils;
import com.tsengfhy.entry.Application;
import com.tsengfhy.entry.controller.TestController;
import com.tsengfhy.entry.domain.Domain;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class WebTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCors() throws Exception {
        String origin = "https://www.google.com";
        mockMvc.perform(MockMvcRequestBuilders.get(TestController.PATH).header(HttpHeaders.ORIGIN, origin))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").hasJsonPath());

        origin = "https://www.elaphure.com";
        mockMvc.perform(MockMvcRequestBuilders.get(TestController.PATH).header(HttpHeaders.ORIGIN, origin))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.header().stringValues(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
    }

    @Autowired
    private WebProperties webProperties;

    @Test
    void testXss() throws Exception {
        final String xssValue = "hello<script>alert(1)</script>";
        final String cleanValue = "hello";
        final Domain domain = new Domain().setValue(xssValue);
        final XssUtils.ProcessStrategy processStrategy = webProperties.getXss().getProcessStrategy();

        XssUtils.setDefaultProcessStrategy(XssUtils.ProcessStrategy.FILTER);
        mockMvc.perform(MockMvcRequestBuilders.get(TestController.PATH).param("value", xssValue))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(cleanValue));
        mockMvc.perform(MockMvcRequestBuilders.post(TestController.PATH).content(JsonUtils.toJson(domain)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(cleanValue));

        XssUtils.setDefaultProcessStrategy(XssUtils.ProcessStrategy.REJECT);
        mockMvc.perform(MockMvcRequestBuilders.get(TestController.PATH).param("value", xssValue))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));

        XssUtils.setDefaultProcessStrategy(XssUtils.ProcessStrategy.ENCODE);
        mockMvc.perform(MockMvcRequestBuilders.get(TestController.PATH).param("value", xssValue))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(StringEscapeUtils.escapeHtml4(xssValue)));
        mockMvc.perform(MockMvcRequestBuilders.post(TestController.PATH).content(JsonUtils.toJson(domain)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(StringEscapeUtils.escapeHtml4(xssValue)));

        XssUtils.setDefaultProcessStrategy(processStrategy);
    }

    @Test
    void testOpenAPI() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }
}
