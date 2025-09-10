package com.businessLogic;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.topics.LoginResponse;
import com.topics.NewAccountResponse;


@Service
public class AsyncLogic {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);

    // REST Clients to communicate with other microservices
    private RestClient notificationServiceClient = RestClient.create();

    private HashMap<String, RestClient> restRouter = new HashMap<>();
    private HashMap<RestClient, String> restEndpoints = new HashMap<>();

    public AsyncLogic() {
        mapTopicsToClient();
    }

    /* Method to map topics to their respective microservices and endpoints
    * # api-gateway:8081
     * # movie-service:8082
     * # notification-service:8083
     * # payment-service:8084
     * # seating-service:8085
     * # user-management-service:8086
     * # gui-service:8087
     * # ticketing-manager:8088
     * # service-orchestrator:8089
     * # session-manager:8090   
    */
    public void mapTopicsToClient() {
        restRouter.put("LoginResponse", notificationServiceClient);
        restRouter.put("NewAccountResponse", notificationServiceClient);
        restEndpoints.put(notificationServiceClient, "http://notification-service:8083/api/v1/processTopic");

        LOG.info("Sucessfully mapped the topics to their respective microservices...");
    }

    // send reward responses and account login responses asynchronously
    // to the notification service
    @Async 
    public void handleNotifications(LoginResponse loginResponse) {
        LOG.info("Asynchronously sending the LoginResponse to the notification service...");
        notificationServiceClient.post()
            .uri(restEndpoints.get(notificationServiceClient))
            .body(loginResponse)
            .retrieve()
            .toEntity(String.class);
        
    }

    @Async 
    public void handleNotifications(NewAccountResponse newAccountResponse) {
        LOG.info("Asynchronously sending the NewAccountResponse to the notification service...");
        notificationServiceClient.post()
            .uri(restEndpoints.get(notificationServiceClient))
            .body(newAccountResponse)
            .retrieve()
            .toEntity(String.class);
        
    }
}
