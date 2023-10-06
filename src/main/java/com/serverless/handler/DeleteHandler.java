package com.serverless.handler;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.serverless.ApiGatewayResponse;
import com.serverless.dto.response.CommonResponseDTO;

import java.util.*;

public class DeleteHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private final String AUTHOR_DB_TABLE = System.getenv("AUTHOR_TABLE");
    private final Regions REGION = Regions.fromName(System.getenv("REGION"));

    private AmazonDynamoDB amazonDynamoDB;

    @Override
    public ApiGatewayResponse handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context
    ) {
        initDynamoDBClient();

        Map<String, AttributeValue> keyMap = new HashMap<>();
        keyMap.put("id", new AttributeValue(request.getPathParameters().get("authorId")));

        DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
                .withTableName(AUTHOR_DB_TABLE)
                .withKey(keyMap);

        amazonDynamoDB.deleteItem(deleteItemRequest);

        return ApiGatewayResponse
                .builder()
                .setStatusCode(200)
                .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .setObjectBody(new CommonResponseDTO("Author deleted successfully."))
                .build();
    }

    private void initDynamoDBClient() {
        amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(REGION)
                .build();
    }

}
