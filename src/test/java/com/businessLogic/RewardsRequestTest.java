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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgres.PostgresService;
import com.postgres.models.Account;
import com.topics.RewardsRequest;
import com.topics.RewardsResponse;
import com.topics.RewardsResponse.Application;


@ExtendWith(MockitoExtension.class)
public class RewardsRequestTest {
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
	@DisplayName("[BUSINESS_LOGIC] Valid RewardsRequest")
	public void RewardsRequest(TestInfo testInfo) {
		System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
				{
					"topicName": "RewardsRequest",
					"correlatorId": 12345,
					"name": "John Doe",
					"email": "john.doe@example.com",
					"username": "johndoe123",
					"rewardPoints": 250,
					"application": "REWARD_POINTS_ADDED"
				}
				""";

		RewardsRequest request = null;
		try {
			request = objectMapper.readValue(JSON, RewardsRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Account account = new Account();
		account.setName("John Doe");
		account.setEmail("john.doe@example.com");
		account.setUsername("johndoe123");
		account.setPassword("superSecret1");
		account.setCreditCard("4111111111111111");
		account.setCvc("123");
		account.setId(Long.valueOf(1));
		account.setRewardPoints(250);

		Mockito.when(postgresService.findByUsername(any(String.class)))
			.thenReturn(account);

		Mockito.when(postgresService.updateRewardPoints(any(Long.class), any(Integer.class)))
			.thenReturn(account);

		ResponseEntity<String> httpResponse = businessLogic.processRewardsRequest(request);
		RewardsResponse response = null;
		try {
			@SuppressWarnings("null")
			String body = httpResponse.getBody().toString();
			if (isString(body)) {
				System.out.println("\n" + httpResponse.getBody());
			} else {
				response = objectMapper.readValue(body, RewardsResponse.class);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		assertNotNull(response);
		Assertions.assertEquals(250, response.getRewardPoints());
		Assertions.assertEquals(Application.SUCCESS, response.getApplication());
		Assertions.assertEquals("johndoe123", response.getUsername());
	}

	@Test
	@DisplayName("[BUSINESS_LOGIC] Valid RewardsRequest (no username)")
	public void BadRewardsRequest(TestInfo testInfo) {
		System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
				{
					"topicName": "RewardsRequest",
					"correlatorId": 12345,
					"name": "John Doe",
					"email": "john.doe@example.com",
					"username": "johndoe123",
					"rewardPoints": 250,
					"application": "REWARD_POINTS_ADDED"
				}
				""";

		RewardsRequest request = null;
		try {
			request = objectMapper.readValue(JSON, RewardsRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Mockito.when(postgresService.findByUsername(any(String.class)))
			.thenReturn(null);

		ResponseEntity<String> httpResponse = businessLogic.processRewardsRequest(request);
		String body = httpResponse.getBody().toString();
		Assertions.assertEquals("No account found for user: " + request.getUsername(), body);
	}

	private boolean isString(String responseBody) {
		// Check if the response is a simple string (you may need more specific checks depending on
		// your use case)
		return responseBody != null && responseBody.length() > 0 && responseBody.charAt(0) != '{';
	}
}
