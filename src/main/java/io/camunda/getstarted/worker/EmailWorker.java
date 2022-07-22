package io.camunda.getstarted.worker;

import io.camunda.getstarted.service.email.EmailServiceImpl;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@ExternalTaskSubscription("email")
public class EmailWorker implements ExternalTaskHandler {
    private static final Logger LOGGER = Logger.getLogger(Class.class.getName());

    private EmailServiceImpl emailService;

    public EmailWorker() {}

    @Autowired
    public EmailWorker(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        // get variables
        final String emailSubject = externalTask.getVariable("subject");
        final String emailBody = externalTask.getVariable("message");
        final String emailTo = externalTask.getVariable("email");

        // we could call an external service to create the loan documents here
        LOGGER.info("\n\n Sending email with message content: "+ emailBody +"\n\n");
        emailService.sendSimpleMessage(emailTo, emailSubject, emailBody);

        // complete the external task
        externalTaskService.complete(externalTask);

    }
}