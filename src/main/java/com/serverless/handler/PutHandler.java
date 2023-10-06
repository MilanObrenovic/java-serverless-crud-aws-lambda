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

        RequestConversionUtil requestConversionUtil = new RequestConversionUtil();
        AuthorDTO authorDTO = requestConversionUtil.parseRequestBody(
                request.getBody(),
                AuthorDTO.class
        );

        AttributeValue authorIdAttributeValue = new AttributeValue(
                request.getPathParameters().get("authorId")
        );

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(
                        AUTHOR_DB_TABLE
                )
                .addKeyEntry(
                        "id",
                        authorIdAttributeValue
                )
                .addAttributeUpdatesEntry(
                        "first_name",
                        updateField(authorDTO.getFirstName())
                )
                .addAttributeUpdatesEntry(
                        "last_name",
                        updateField(authorDTO.getLastName())
                )
                .addAttributeUpdatesEntry(
                        "email",
                        updateField(authorDTO.getEmail())
                )
                .addAttributeUpdatesEntry(
                        "identification_number",
                        updateField(authorDTO.getIdentificationNumber())
                );

        amazonDynamoDB.updateItem(updateItemRequest);

        return ApiGatewayResponse
                .builder()
                .setStatusCode(200)
                .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .setObjectBody(new CommonResponseDTO("Author updated successfully."))
                .build();
    }

    private AttributeValueUpdate updateField(String value) {
        AttributeValue attributeValue = new AttributeValue(
                value
        );
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
