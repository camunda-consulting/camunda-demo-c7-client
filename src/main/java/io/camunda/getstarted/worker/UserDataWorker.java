package io.camunda.getstarted.worker;

import io.camunda.getstarted.service.email.EmailServiceImpl;
import org.apache.http.client.fluent.Request;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
@ExternalTaskSubscription("get-user")
public class UserDataWorker implements ExternalTaskHandler {
    private static final Logger LOGGER = Logger.getLogger(Class.class.getName());

    @Value("${data.api.uri}")
    private String dataApiUri;

    private EmailServiceImpl emailService;

    public UserDataWorker() {}

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        LOGGER.info("\n\n Get business data from business API");

        // get variables
        final String searchTerm = (String) externalTask.getVariable("searchTerm");
        final String emailAddress = (String) externalTask.getVariable("email");

        String dataURI = dataApiUri + "/" + searchTerm + emailAddress;
        LOGGER.info(" \n\n ====>> Data URI " + dataURI + "\n");

        // Use fluent HTTP api to execute request
        String content = null;
        try {
            content = Request.Get(dataURI).execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info(" \n\n ====>> Response Body " + content + "\n");

        final String emailBody = (String) externalTask.getVariable("message");

        VariableMap variables = Variables.createVariables();
        variables.put("user", content);
        // complete the external task
        externalTaskService.complete(externalTask, variables);

    }
}
