package com.schemaValidator;

import java.io.InputStream;
import java.net.URL;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import com.SchemaService;
import com.schema.SchemaValidator;  

@SpringBootTest(classes = SchemaValidator.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class SchemaValidatorTest {
    @Autowired
    private ResourceLoader resourceLoader;

    private SchemaValidator schemaValidator;

    @BeforeEach
    void setup() {
        schemaValidator = new SchemaValidator(resourceLoader);
    }

    @Test
    @DisplayName("[SCHEMA] Valid NewAccountRequest")
    void testNewAccountRequest(TestInfo testInfo) throws Exception {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
        // JSON that satisfies the schema
        JSONObject validJson = new JSONObject("""
            {
                "topicName": "NewAccountRequest",
                "correlatorId": 12345,
                "name": "John Doe",
                "email": "john.doe@example.com",
                "username": "johndoe123",
                "password": "superSecret1",
                "rewardPoints": 250,
                "creditCard": "4111111111111111",
                "cvc": "123"
            }
        """);

        // Testing with a dynamically loaded schema from the SchemaService
        String topicName = "NewAccountRequest";  

        // Assert the validation result (assuming valid schema)
        Assertions.assertTrue(validate(topicName, validJson));
    }

    @Test
    @DisplayName("[SCHEMA] Invalid NewAccountRequest (no name)")
    void testBadNewAccountRequest(TestInfo testInfo) throws Exception {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
        // JSON that satisfies the schema
        JSONObject validJson = new JSONObject("""
            {
                "topicName": "NewAccountRequest",
                "correlatorId": 12345,
                "email": "john.doe@example.com",
                "username": "johndoe123",
                "password": "superSecret1",
                "creditCard": "4111111111111111",
                "cvc": "123"
            }
        """);

        // Testing with a dynamically loaded schema from the SchemaService
        String topicName = "NewAccountRequest";  

        // Assert the validation result (assuming valid schema)
        Assertions.assertFalse(validate(topicName, validJson));
    }

    @Test
    @DisplayName("[SCHEMA] Valid LoginRequest")
    void testLoginRequest(TestInfo testInfo) throws Exception {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
        // JSON that satisfies the schema
        JSONObject validJson = new JSONObject("""
            {
                "topicName": "LoginRequest",
                "correlatorId": 67890,
                "email": "john.doe@example.com",
                "password": "superSecret1"
            }
        """);

        // Testing with a dynamically loaded schema from the SchemaService
        String topicName = "LoginRequest";  

        // Assert the validation result (assuming valid schema)
        Assertions.assertTrue(validate(topicName, validJson));
    }

    @Test
    @DisplayName("[SCHEMA] Invalid LoginRequest (no password)")
    void testBadLoginRequest(TestInfo testInfo) throws Exception {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
        // JSON that satisfies the schema
        JSONObject validJson = new JSONObject("""
            {
                "topicName": "LoginRequest",
                "correlatorId": 67890,
                "email": "john.doe@example.com"            
            }
        """);

        // Testing with a dynamically loaded schema from the SchemaService
        String topicName = "LoginRequest";  

        // Assert the validation result (assuming valid schema)
        Assertions.assertFalse(validate(topicName, validJson));
    }

    @Test
    @DisplayName("[SCHEMA] Valid AccountInfoRequest (email)")
    void testAccountInfoRequest1(TestInfo testInfo) throws Exception {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
        // JSON that satisfies the schema
        JSONObject validJson = new JSONObject("""
            {
                "topicName": "AccountInfoRequest",
                "correlatorId": 12345,
                "email": "bryzntest@gmail.com"
            }
        """);

        // Testing with a dynamically loaded schema from the SchemaService
        String topicName = "AccountInfoRequest";  

        // Assert the validation result (assuming valid schema)
        Assertions.assertTrue(validate(topicName, validJson));
    }

    @Test
    @DisplayName("[SCHEMA] Invalid AccountInfoRequest (email)")
    void testBadAccountInfoRequest1(TestInfo testInfo) throws Exception {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
        // JSON that satisfies the schema
        JSONObject validJson = new JSONObject("""
            {
                "topicName": "AccountInfoRequest",
                "correlatorId": 12345,
                "email": "bryzntest1234"
            }
        """);

        // Testing with a dynamically loaded schema from the SchemaService
        String topicName = "AccountInfoRequest";  

        // Assert the validation result (assuming valid schema)
        Assertions.assertFalse(validate(topicName, validJson));
    }

    @Test
    @DisplayName("[SCHEMA] Valid AccountInfoRequest (username)")
    void testAccountInfoRequest2(TestInfo testInfo) throws Exception {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
        // JSON that satisfies the schema
        JSONObject validJson = new JSONObject("""
            {
                "topicName": "AccountInfoRequest",
                "correlatorId": 12345,
                "username": "dummy1"
            }
        """);

        // Testing with a dynamically loaded schema from the SchemaService
        String topicName = "AccountInfoRequest";  

        // Assert the validation result (assuming valid schema)
        Assertions.assertTrue(validate(topicName, validJson));
    }

    @Test
    @DisplayName("[SCHEMA] Invalid AccountInfoRequest (username)")
    void testBadAccountInfoRequest2(TestInfo testInfo) throws Exception {
        System.out.println("\n-----------Running: " + testInfo.getDisplayName() + "-----------");
        // JSON that satisfies the schema
        JSONObject validJson = new JSONObject("""
            {
                "topicName": "AccountInfoRequest",
                "correlatorId": 12345,
                "username": ""
            }
        """);

        // Testing with a dynamically loaded schema from the SchemaService
        String topicName = "AccountInfoRequest";  

        // Assert the validation result (assuming valid schema)
        Assertions.assertFalse(validate(topicName, validJson));
    }

    private boolean validate(String topicName, JSONObject validJson)
    {
        URL schemaUrl = getClass().getClassLoader().getResource(SchemaService.getPathFor(topicName));

        // Check if the schema is found
        if (schemaUrl == null) {
            throw new RuntimeException("Schema not found for topic: " + topicName);
        }

        // Load schema stream from resource
        InputStream schemaStream = schemaValidator.getSchemaStream(SchemaService.getPathFor(topicName));

        if (schemaStream == null) {
            System.out.println("No schema found for topic: " + topicName);
        }

        // Validate the JSON using the schema stream
        return schemaValidator.validateJson(schemaStream, validJson);
    }
}
