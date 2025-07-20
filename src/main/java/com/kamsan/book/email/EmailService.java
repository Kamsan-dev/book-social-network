package com.kamsan.book.email;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.kamsan.book.sharedkernel.exception.ApiException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;

	@Async
	public void sendEmail(String to, String username, EmailTemplateName emailTemplate, String confirmationUrl,
			String activationCode, String subject) {

		String templateName;
		if (emailTemplate == null)
			templateName = "confirm-email";
		else
			templateName = emailTemplate.getName();

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(
					mimeMessage, 
					MimeMessageHelper.MULTIPART_MODE_MIXED,
					UTF_8.name());
			
			Map<String, Object> properties = new HashMap<>();
			properties.put("username", username);
			properties.put("confirmationUrl", confirmationUrl);
			properties.put("activation_code", activationCode);
			
			Context context = new Context();
			context.setVariables(properties);
			helper.setFrom("contact@kamsan-dev.com");
			helper.setTo(to);
			helper.setSubject(subject);
			
			String template = templateEngine.process(templateName, context);
			helper.setText(template, true);
			mailSender.send(mimeMessage);
			
			
		} catch (MessagingException e) {
			throw new ApiException(String.format("Unable to send an email. %s", e.getMessage()));
		}
	}

}
