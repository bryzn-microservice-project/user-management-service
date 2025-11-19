package com.businessLogic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgres.PostgresService;
import com.postgres.models.Account;
import com.topics.LoginRequest;

@ExtendWith(MockitoExtension.class)
public class LoginRequestTest {
	@InjectMocks
	private BusinessLogic businessLogic;
	@Mock
	private PostgresService postgresService;
	@Mock
    private RestClient sessionManagerClient; 
	@Mock
	private AsyncLogic asyncLogic;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("[BUSINESS_LOGIC] Valid Login Request")
	public void LoginRequest(TestInfo testInfo) {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
			{
				"topicName": "LoginRequest",
				"correlatorId": 67890,
				"email": "bryzntest@gmail.com",
				"password": "pass123"
			}
			""";
		
		LoginRequest request = null;	
		try{
			request = objectMapper.readValue(JSON, LoginRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}


		Account account = new Account();
		account.setEmail("bryzntest@gmail.com");
		account.setName("Dummy Name");
		account.setRewardPoints(500);
		account.setUsername("username1234");
		account.setPassword("pass123");

		Mockito.when(postgresService.findByEmail(any(String.class)))
			.thenReturn(account);
		
		RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
		RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
		RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

		when(sessionManagerClient.post()).thenReturn(uriSpec);
		when(uriSpec.uri((String) isNull())).thenReturn(bodySpec);
		when(bodySpec.contentType(any(MediaType.class))).thenReturn(bodySpec);
		when(bodySpec.body(any(String.class))).thenReturn(bodySpec);
		when(bodySpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.toEntity(String.class))
    		.thenReturn(new ResponseEntity<>("Login successful", HttpStatus.OK));

		ResponseEntity<String> response = businessLogic.processLoginRequest(request);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertEquals("Session Manager was able to log the user in!", response.getBody());
	}

	@Test
	@DisplayName("[BUSINESS_LOGIC] Invalid Login Request (no user exist)")
	public void BadLoginRequest(TestInfo testInfo) {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
			{
				"topicName": "LoginRequest",
				"correlatorId": 67890,
				"email": "bryzntest@gmail.com",
				"password": "pass123"
			}
			""";
		
		LoginRequest request = null;	
		try{
			request = objectMapper.readValue(JSON, LoginRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Mockito.when(postgresService.findByEmail(any(String.class)))
			.thenReturn(null);
		
		ResponseEntity<String> response = businessLogic.processLoginRequest(request);
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		Assertions.assertEquals("Login failed: Invalid email or password.", response.getBody());
	}
}
