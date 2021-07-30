package com.tsengfhy.elaphure.web;

import com.tsengfhy.elaphure.env.WebProperties;
import com.tsengfhy.elaphure.utils.JsonUtils;
import com.tsengfhy.elaphure.utils.MessageUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.context.request.ServletWebRequest;

@SpringBootTest
@AutoConfigureMockMvc
class WebTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/unknown").header(HttpHeaders.ORIGIN, "www.google.com"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").hasJsonPath());

        mockMvc.perform(MockMvcRequestBuilders.get("/unknown"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Autowired
    WebProperties webProperties;

    @Test
    void testXss() throws Exception {
        final String path = "/test";
        final String xssValue = "hello<script>alert(1)</script>";
        final String cleanValue = "hello";
        final TestController.TestDTO dto = new TestController.TestDTO().setValue(xssValue);
        final WebProperties.Xss.ProcessStrategy processStrategy = webProperties.getXss().getProcessStrategy();

        webProperties.getXss().setProcessStrategy(WebProperties.Xss.ProcessStrategy.FILTER);
        mockMvc.perform(MockMvcRequestBuilders.get(path).param("value", xssValue))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(cleanValue));
        mockMvc.perform(MockMvcRequestBuilders.post(path).content(JsonUtils.toJson(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(cleanValue));

        webProperties.getXss().setProcessStrategy(WebProperties.Xss.ProcessStrategy.REJECT);
        mockMvc.perform(MockMvcRequestBuilders.get(path).param("value", xssValue))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));

        webProperties.getXss().setProcessStrategy(WebProperties.Xss.ProcessStrategy.ENCODE);
        mockMvc.perform(MockMvcRequestBuilders.get(path).param("value", xssValue))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(StringEscapeUtils.escapeHtml4(xssValue)));
        mockMvc.perform(MockMvcRequestBuilders.post(path).content(JsonUtils.toJson(dto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(StringEscapeUtils.escapeHtml4(xssValue)));

        webProperties.getXss().setProcessStrategy(processStrategy);
    }

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @Test
    void testI18nForValidation() {
        TestController.TestDTO testDto = new TestController.TestDTO();
        BindingResult bindingResult = new BindException(testDto, "testDto");
        validator.validate(testDto, bindingResult);
        Assertions.assertEquals(
                bindingResult.getFieldErrors().get(0).getDefaultMessage(),
                MessageUtils.getMessage("test.value")
        );
    }

    @Autowired
    private DefaultErrorAttributes errorAttributes;

    @Test
    void testI18nForException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        errorAttributes.resolveException(request, response, null, new TestController.TestException());
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE);
        Assertions.assertEquals(
                errorAttributes.getErrorAttributes(new ServletWebRequest(request), options).get("message"),
                MessageUtils.getMessage("test.error")
        );
    }
}
