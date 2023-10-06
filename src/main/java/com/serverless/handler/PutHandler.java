package com.serverless.handler;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.serverless.ApiGatewayResponse;
import com.serverless.dto.AuthorDTO;
import com.serverless.dto.response.CommonResponseDTO;
import com.serverless.util.RequestConversionUtil;

import java.util.*;

public class PutHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private final String AUTHOR_DB_TABLE = System.getenv("AUTHOR_TABLE");
    private final Regions REGION = Regions.fromName(System.getenv("REGION"));

    private AmazonDynamoDB amazonDynamoDB;

    @Override
    public ApiGatewayResponse handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context
    ) {
        initDynamoDBClient();

        String authorId = request.getPathParameters().get("authorId");
        RequestConversionUtil requestConversionUtil = new RequestConversionUtil();
        AuthorDTO authorDTO = requestConversionUtil.parseRequestBody(
                request.getBody(),
                AuthorDTO.class
        );

        Map<String, AttributeValue> authorIdMap = new HashMap<>();
        authorIdMap.put("id", attributeValue(authorId));

        Map<String, AttributeValueUpdate> updateAuthorMap = new HashMap<>();
        updateAuthorMap.put("first_name", attributeValueUpdate(authorDTO.getFirstName()));
        updateAuthorMap.put("last_name", attributeValueUpdate(authorDTO.getLastName()));
        updateAuthorMap.put("email", attributeValueUpdate(authorDTO.getEmail()));
        updateAuthorMap.put("identification_number", attributeValueUpdate(authorDTO.getIdentificationNumber()));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(
                        AUTHOR_DB_TABLE
                )
                .withKey(
                        authorIdMap
                )
                .withAttributeUpdates(
                        updateAuthorMap
                );

        amazonDynamoDB.updateItem(updateItemRequest);

        return ApiGatewayResponse
                .builder()
                .setStatusCode(200)
                .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .setObjectBody(new CommonResponseDTO("Author updated successfully."))
                .build();
    }

    private AttributeValue attributeValue(String value) {
        return new AttributeValue(
                value
        );
    }

    private AttributeValueUpdate attributeValueUpdate(String value) {
        AttributeValue attributeValue = attributeValue(value);
        return new AttributeValueUpdate(
                attributeValue,
                AttributeAction.PUT
        );
    }

    private void initDynamoDBClient() {
        amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(REGION)
                .build();
    }

}
