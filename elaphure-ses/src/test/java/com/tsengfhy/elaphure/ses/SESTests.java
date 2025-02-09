package com.tsengfhy.elaphure.ses;

import com.tsengfhy.entry.Application;
import org.junit.jupiter.api.Test;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import jakarta.activation.FileDataSource;
import java.io.IOException;

@SpringBootTest(classes = Application.class)
@EnabledIf(expression = "#{!'${spring.mail.username}'.isBlank() && !'${spring.mail.password}'.isBlank()}", loadContext = true)
class SESTests {

    @Autowired
    private Mailer mailer;

    private static final String CONFIGURATION_SET = "";

    @Test
    void testSendMail() throws IOException {
        Email email = EmailBuilder.startingBlank()
                .withSubject("Test Mail")
                .withHTMLText(new ClassPathResource("/static/index.html").getFile())
                .withEmbeddedImage("avatar.jpg", new FileDataSource(new ClassPathResource("/static/img/avatar.jpg").getFile()))
                .withAttachment("attachment.txt", new FileDataSource(new ClassPathResource("/static/asset/attachment.txt").getFile()))
                .withHeader("X-SES-CONFIGURATION-SET", CONFIGURATION_SET)
                .from("no-reply@tsengfhy.com")
                .to("Tsengfhy", "tsengfhy@gmail.com")
                .buildEmail();

        mailer.sendMail(email);
    }
}