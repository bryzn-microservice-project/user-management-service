package com.businessLogic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgres.PostgresService;
import com.postgres.models.Account;
import com.topics.AccountInfoRequest;
import com.topics.AccountInfoResponse;


@ExtendWith(MockitoExtension.class)
public class AccountInfoRequestTest {
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
	@DisplayName("[BUSINESS_LOGIC] Valid AccountInfoRequest (email)")
	public void AccountInfoRequest1(TestInfo testInfo) {
		System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
				{
					"topicName": "AccountInfoRequest",
					"correlatorId": 12345,
					"email": "bryzntest@gmail.com"
				}
				""";

		AccountInfoRequest request = null;
		try {
			request = objectMapper.readValue(JSON, AccountInfoRequest.class);
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

		ResponseEntity<String> httpResponse = businessLogic.processAccountInfoRequest(request);
		AccountInfoResponse response = null;
		try {
			@SuppressWarnings("null")
			String body = httpResponse.getBody().toString();
			if (isString(body)) {
				System.out.println("\n" + httpResponse.getBody());
			} else {
				response = objectMapper.readValue(body, AccountInfoResponse.class);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		assertNotNull(response);
		Assertions.assertEquals(500, response.getRewardPoints());
		Assertions.assertEquals("username1234", response.getUsername());
	}

	@Test
	@DisplayName("[BUSINESS_LOGIC] Valid AccountInfoRequest (username)")
	public void AccountInfoRequest2(TestInfo testInfo) {
		System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
				{
					"topicName": "AccountInfoRequest",
					"correlatorId": 12345,
					"username": "dummy123"
				}
				""";

		AccountInfoRequest request = null;
		try {
			request = objectMapper.readValue(JSON, AccountInfoRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Account account = new Account();
		account.setEmail("dummy@gmail.com");
		account.setName("Dummy Name");
		account.setRewardPoints(500);
		account.setUsername("dummy123");
		account.setPassword("pass123");

		Mockito.when(postgresService.findByUsername(any(String.class)))
			.thenReturn(account);

		ResponseEntity<String> httpResponse = businessLogic.processAccountInfoRequest(request);
		AccountInfoResponse response = null;
		try {
			@SuppressWarnings("null")
			String body = httpResponse.getBody().toString();
			if (isString(body)) {
				System.out.println("\n" + httpResponse.getBody());
			} else {
				response = objectMapper.readValue(body, AccountInfoResponse.class);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		assertNotNull(response);
		Assertions.assertEquals(500, response.getRewardPoints());
		Assertions.assertEquals("dummy@gmail.com", response.getEmail());
		Assertions.assertEquals("Dummy Name", response.getName());
	}

	@Test
	@DisplayName("[BUSINESS_LOGIC] Invalid AccountInfoRequest (no user)")
	public void BadAccountInfoRequest(TestInfo testInfo) {
		System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
				{
					"topicName": "AccountInfoRequest",
					"correlatorId": 12345,
					"username": "dummy123"
				}
				""";

		AccountInfoRequest request = null;
		try {
			request = objectMapper.readValue(JSON, AccountInfoRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ResponseEntity<String> httpResponse = businessLogic.processAccountInfoRequest(request);

		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, httpResponse.getStatusCode());
		Assertions.assertEquals("Internal Error: Could not grab user data.", httpResponse.getBody());
	}

	private boolean isString(String responseBody) {
		// Check if the response is a simple string (you may need more specific checks depending on
		// your use case)
		return responseBody != null && responseBody.length() > 0 && responseBody.charAt(0) != '{';
	}
}
