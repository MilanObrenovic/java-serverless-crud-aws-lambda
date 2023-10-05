package com.serverless.handler;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.dto.AuthorDTO;
import com.serverless.dto.response.CommonResponseDTO;
import com.serverless.util.RequestConversionUtil;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class RegistrationHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(RegistrationHandler.class);
    private static final String AUTHOR_DB_TABLE = System.getenv("AUTHOR_TABLE");
    private static final Regions REGION = Regions.fromName(System.getenv("REGION"));

    private AmazonDynamoDB amazonDynamoDB;

    @Override
    public ApiGatewayResponse handleRequest(
            Map<String, Object> input,
            Context context
    ) {
        RequestConversionUtil requestConversionUtil = new RequestConversionUtil();

        AuthorDTO authorDTO = requestConversionUtil.parseRequestBody(
                input.get("body").toString(),
                AuthorDTO.class
        );

        LOG.info("Incoming author registration request " + authorDTO);

        initDynamoDBClient();
        persistData(authorDTO);

        return ApiGatewayResponse
                .builder()
                .setStatusCode(201)
                .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .setObjectBody(new CommonResponseDTO("Author registration successfully completed."))
                .build();
    }

    private String persistData(
            AuthorDTO authorDTO
    ) throws ConditionalCheckFailedException {
        String userId = UUID.randomUUID().toString();
        Map<String, AttributeValue> attributesMap = new HashMap<>();
        attributesMap.put("id", new AttributeValue(userId));
        attributesMap.put("first_name", new AttributeValue(authorDTO.getFirstName()));
        attributesMap.put("last_name", new AttributeValue(authorDTO.getLastName()));
        attributesMap.put("email", new AttributeValue(authorDTO.getEmail()));
        attributesMap.put("identification_number", new AttributeValue(authorDTO.getIdentificationNumber()));
        amazonDynamoDB.putItem(AUTHOR_DB_TABLE, attributesMap);
        return userId;
    }

    private void initDynamoDBClient() {
        amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(REGION)
                .build();
    }

}
