package com.businessLogic;

import java.util.Date;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.postgres.PostgresService;
import com.postgres.models.Account;
import com.topics.LoginRequest;
import com.topics.LoginResponse;
import com.topics.NewAccountRequest;
import com.topics.NewAccountResponse;

@Service
public class BusinessLogic {
    @Autowired
    AsyncLogic asyncLogic;
    
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);
    public final PostgresService postgresService;

    // REST Clients to communicate with other microservices
    private RestClient notificationServiceClient = RestClient.create();
    private RestClient sessionManagerClient = RestClient.create();
    private String session = "http://session-manager:8090/api/v1/login";

    private HashMap<String, RestClient> restRouter = new HashMap<>();
    private HashMap<RestClient, String> restEndpoints = new HashMap<>();

    public BusinessLogic(PostgresService postgresService) {
        this.postgresService = postgresService;
        mapTopicsToClient();
    }

    /*
     * Method to map topics to their respective microservices and endpoints
     * # api-gateway:8081
     * # movie-service:8082
     * # notification-service:8083
     * # payment-service:8084
     * # seating-service:8085
     * # user-management-service:8086
     * # gui-service:8087
     */
    public void mapTopicsToClient() {
        restRouter.put("SeatResponse", notificationServiceClient);
        restEndpoints.put(notificationServiceClient, "http://notification-service:8083/api/v1/processTopic");
        restEndpoints.put(sessionManagerClient, "http://session-manager:8083/api/v1/login");
    }

    public ResponseEntity<String> processLoginRequest(LoginRequest loginRequest) {
        LOG.info("Processing the LoginRequest...");
        Account loginAttempt = postgresService.findByEmail(loginRequest.getEmail());
        if(loginAttempt != null && loginAttempt.getPassword().equals(loginRequest.getPassword())) {
            LOG.info("Login successful for email: " + loginRequest.getEmail());
            
            // notify the session manager of the successful login and set username as active session
            LOG.info("Updating the session manager with the active user: " + loginAttempt.getUsername());
            sessionManagerClient.post()
                .uri(session)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginAttempt.getUsername())
                .retrieve()
                .toEntity(String.class);

            // async notify the notification service of the successful login
            LoginResponse rsp = createLoginResponse(loginRequest, "SUCCESSFUL");
            asyncLogic.handleNotifications(rsp);
            return ResponseEntity.ok("Login successful!");
        } else {
            LOG.info("Login failed for email: " + loginRequest.getEmail());

            // async notify the notification service of the failed login
            LoginResponse rsp = createLoginResponse(loginRequest, "FAILED");
            asyncLogic.handleNotifications(rsp);
            return ResponseEntity.status(401).body("Login failed: Invalid email or password.");
        }
    }

    public ResponseEntity<String> processNewAccountRequest(NewAccountRequest newAccountRequest) {
        LOG.info("Processing the NewAccountRequest...");
        // give a default value for easier debugging
        String status = "NO STATUS";
        String statusMsg = "NO STATUS";
        Account newAccount = new Account();
                newAccount.setName(newAccountRequest.getName());
                newAccount.setEmail(newAccountRequest.getEmail());
                newAccount.setUsername(newAccountRequest.getUsername());
                newAccount.setPassword(newAccountRequest.getPassword());
                newAccount.setRewardPoints(50); // new accounts start with 50 reward points
                newAccount.setCreditCard(newAccountRequest.getCreditCard());
                newAccount.setCvc(newAccountRequest.getCvc());

        if(postgresService.findByEmail(newAccountRequest.getEmail()) != null) {
            LOG.info("Failed to create a new account... Email already in use" + newAccountRequest.getEmail());
            status = "FAILED";
            statusMsg = "Email already in use";
            return ResponseEntity.status(409).body("Account creation failed: Email already in use.");
        }

        if(postgresService.findByUsername(newAccountRequest.getUsername()) != null) {
            LOG.info("Failed to create a new account... Username already in use" + newAccountRequest.getUsername());
            status = "FAILED";
            statusMsg = "Username already in use";
            return ResponseEntity.status(409).body("Account creation failed: Username already in use.");
        }

        Account savedAccount = postgresService.save(newAccount);
        LOG.info("New account with the following details...");
        LOG.info("Name: " + savedAccount.getName());
        LOG.info("Email: " + savedAccount.getEmail());
        LOG.info("Username: " + savedAccount.getUsername());
        LOG.info("Reward Points: " + savedAccount.getRewardPoints());

        if(savedAccount.getCreditCard() != null && !savedAccount.getCreditCard().isEmpty())
        {
            String cc = savedAccount.getCreditCard();
            String lastFour = cc.substring(cc.length() - 4);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < cc.length() - 4; i++) {
                sb.append("*");
                if(i % 4 == 0 && i != 0) {
                    sb.append(" ");
                }
            }
            sb.append(lastFour);
            LOG.info("Credit Card: " + sb.toString());
        }

        if(savedAccount.getId() != null) {
            status = "SUCCESSFUL";
            statusMsg = "Account created successfully";

            // async notify the notification service of the failed account creation
            NewAccountResponse rsp = createNewAccountResponse(newAccountRequest, status, statusMsg);
            asyncLogic.handleNotifications(rsp);

            return ResponseEntity.ok("New account created successfully!");
        } else {
            // if the status/statusMsg is still the default then something went wrong with postgres
            status = status.isEmpty() ? "FAILED" : status;
            statusMsg = statusMsg.isEmpty() ? "Internal Error: Failed to create new account" : statusMsg;

            // async notify the notification service of the failed account creation
            NewAccountResponse rsp = createNewAccountResponse(newAccountRequest, status, statusMsg);
            asyncLogic.handleNotifications(rsp);

            return ResponseEntity.status(500).body("Internal Error: Failed to create new account.");
        }
    }

    private LoginResponse createLoginResponse(LoginRequest loginRequest, String status) {
        LOG.info("Creating a LoginResponse... with status: " + status);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setTopicName("LoginResponse");
        loginResponse.setCorrelatorId(loginRequest.getCorrelatorId());
        loginResponse.setStatus(LoginResponse.Status.valueOf(status));
        loginResponse.setTimestamp(new Date());
        return loginResponse;
    }

    private NewAccountResponse createNewAccountResponse(NewAccountRequest newAccountRequest, String status, String stsMsg) {
        LOG.info("Creating a NewAccountResponse... with status: " + status);
        NewAccountResponse newAccountResponse = new NewAccountResponse();
        newAccountResponse.setTopicName("NewAccountResponse");
        newAccountResponse.setCorrelatorId(newAccountRequest.getCorrelatorId());
        newAccountResponse.setStatus(NewAccountResponse.Status.valueOf(status));
        newAccountResponse.setStatusMessage(stsMsg);
        return newAccountResponse;
    }
}
