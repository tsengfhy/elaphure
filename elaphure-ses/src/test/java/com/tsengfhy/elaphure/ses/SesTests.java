package com.tsengfhy.elaphure.ses;

import org.junit.jupiter.api.*;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import javax.activation.FileDataSource;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SesTests {

    @Autowired(required = false)
    private Mailer mailer;

    private Email email;

    @BeforeAll
    void setUp() throws Exception {
        email = EmailBuilder.startingBlank()
                .to("Tsengfhy", "tsengfhy@gmail.com")
                .withSubject("Test Mail")
                .withHTMLText(new ClassPathResource("/static/index.html").getFile())
                .withAttachment("attachment.txt", new FileDataSource(new ClassPathResource("/static/asset/attachment.txt").getFile()))
                .buildEmail();
    }

    @Test
    void testAutoConfiguration() {
        Assertions.assertNotNull(mailer);
    }

    @Test
    void testClassPathAttachment() {
        Assertions.assertFalse(email.getAttachments().isEmpty());
    }

    @Test
    @Disabled("Only for manual testing")
    void testSendMail() {
        mailer.sendMail(email);
    }
}
