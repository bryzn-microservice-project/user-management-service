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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgres.PostgresService;
import com.postgres.models.Account;
import com.topics.NewAccountRequest;


@ExtendWith(MockitoExtension.class)
public class NewAccountRequestTest {
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
	@DisplayName("[BUSINESS_LOGIC] Valid NewAccountRequest")
	public void NewAccountRequest(TestInfo testInfo) {
		System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
				{
					"topicName": "NewAccountRequest",
					"correlatorId": 12345,
					"name": "John Doe",
					"email": "john.doe@example.com",
					"username": "johndoe123",
					"password": "superSecret1",
					"creditCard": "4111111111111111",
					"cvc": "123"
				}
				""";

		NewAccountRequest request = null;
		try {
			request = objectMapper.readValue(JSON, NewAccountRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Mockito.when(postgresService.findByEmail(any(String.class)))
			.thenReturn(null);

		Mockito.when(postgresService.findByUsername(any(String.class)))
			.thenReturn(null);

		Account account = new Account();
		account.setName("John Doe");
		account.setEmail("john.doe@example.com");
		account.setUsername("johndoe123");
		account.setPassword("superSecret1");
		account.setCreditCard("4111111111111111");
		account.setCvc("123");
		account.setId(Long.valueOf(1));

		Mockito.when(postgresService.save(any(Account.class)))
			.thenReturn(account);

		ResponseEntity<String> httpResponse = businessLogic.processNewAccountRequest(request);
		String body = httpResponse.getBody().toString();
		Assertions.assertEquals("New account created successfully!", body);
	}

	@Test
	@DisplayName("[BUSINESS_LOGIC] Invalid NewAccountRequest (non unique email)")
	public void BadNewAccountRequest1(TestInfo testInfo) {
		System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
				{
					"topicName": "NewAccountRequest",
					"correlatorId": 12345,
					"name": "John Doe",
					"email": "john.doe@example.com",
					"username": "johndoe123",
					"password": "superSecret1",
					"creditCard": "4111111111111111",
					"cvc": "123"
				}
				""";

		NewAccountRequest request = null;
		try {
			request = objectMapper.readValue(JSON, NewAccountRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Mockito.when(postgresService.findByEmail(any(String.class)))
			.thenReturn(new Account()); // mock a non null, empty but not null

		ResponseEntity<String> httpResponse = businessLogic.processNewAccountRequest(request);
		assertNotNull(httpResponse);
		String body = httpResponse.getBody().toString();
		Assertions.assertEquals("Account creation failed: Email already in use.", body);
	}

	@Test
	@DisplayName("[BUSINESS_LOGIC] Invalid NewAccountRequest (non unique username)")
	public void BadNewAccountRequest2(TestInfo testInfo) {
		System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
		String JSON = """
				{
					"topicName": "NewAccountRequest",
					"correlatorId": 12345,
					"name": "John Doe",
					"email": "john.doe@example.com",
					"username": "johndoe123",
					"password": "superSecret1",
					"creditCard": "4111111111111111",
					"cvc": "123"
				}
				""";

		NewAccountRequest request = null;
		try {
			request = objectMapper.readValue(JSON, NewAccountRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Mockito.when(postgresService.findByEmail(any(String.class)))
			.thenReturn(null);

		Mockito.when(postgresService.findByUsername(any(String.class)))
			.thenReturn(new Account()); // mock a non null, empty but not null

		ResponseEntity<String> httpResponse = businessLogic.processNewAccountRequest(request);
		assertNotNull(httpResponse);
		String body = httpResponse.getBody().toString();
		Assertions.assertEquals("Account creation failed: Username already in use.", body);
	}
}
