package com.serverless.handler;

import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.serverless.ApiGatewayResponse;
import com.serverless.dto.AuthorDTO;
import com.serverless.dto.response.CommonResponseDTO;
import com.serverless.util.DynamoDBClientUtil;

import java.util.*;

import static com.serverless.constant.AwsConstants.AUTHOR_DB_TABLE;

public class GetHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    @Override
    public ApiGatewayResponse handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context
    ) {
        Map<String, String> queryParams = request.getQueryStringParameters();

        if (queryParams != null &&
                queryParams.containsKey("find-all") &&
                Boolean.parseBoolean(queryParams.get("find-all"))) {

            // Find all
            Map<String, AttributeValue> lastKeyEvaluated = null;
            List<AuthorDTO> authorDTOS = new ArrayList<>();

            do {
                ScanRequest scanRequest = new ScanRequest()
                        .withTableName(AUTHOR_DB_TABLE)
                        .withLimit(10)
                        .withExclusiveStartKey(lastKeyEvaluated);

                ScanResult scanResult = DynamoDBClientUtil
                        .amazonDynamoDB()
                        .scan(scanRequest);

                for (Map<String, AttributeValue> item : scanResult.getItems()) {
                    authorDTOS.add(mapToDTO(item));
                }

                lastKeyEvaluated = scanResult.getLastEvaluatedKey();
            } while (lastKeyEvaluated != null);

            return ApiGatewayResponse
                    .builder()
                    .setStatusCode(200)
                    .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                    .setObjectBody(authorDTOS)
                    .build();
        } else if (queryParams != null &&
                queryParams.containsKey("id") &&
                queryParams.get("id") != null) {

            // Find by ID
            Map<String, AttributeValue> attributesMap = new HashMap<>();
            attributesMap.put("id", new AttributeValue(queryParams.get("id")));

            GetItemRequest itemRequest = new GetItemRequest()
                    .withTableName(AUTHOR_DB_TABLE)
                    .withKey(attributesMap);

            GetItemResult itemResult = DynamoDBClientUtil
                    .amazonDynamoDB()
                    .getItem(itemRequest);

            if (!itemResult.getItem().isEmpty()) {
                return ApiGatewayResponse
                        .builder()
                        .setStatusCode(200)
                        .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                        .setObjectBody(mapToDTO(itemResult.getItem()))
                        .build();
            }
        }

        return ApiGatewayResponse
                .builder()
                .setStatusCode(404)
                .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .setObjectBody(new CommonResponseDTO("Author not found."))
                .build();
    }

    private AuthorDTO mapToDTO(Map<String, AttributeValue> item) {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(item.get("id").getS());
        authorDTO.setEmail(item.get("email").getS());
        authorDTO.setFirstName(item.get("first_name").getS());
        authorDTO.setLastName(item.get("last_name").getS());
        authorDTO.setIdentificationNumber(item.get("identification_number").getS());
        return authorDTO;
    }

}
