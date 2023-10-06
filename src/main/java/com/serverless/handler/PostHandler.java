package com.serverless.handler;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.ApiGatewayResponse;
import com.serverless.dto.AuthorDTO;
import com.serverless.dto.response.CommonResponseDTO;
import com.serverless.util.DynamoDBClientUtil;
import com.serverless.util.RequestConversionUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.serverless.constant.AwsConstants.AUTHOR_DB_TABLE;

public class PostHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    @Override
    public ApiGatewayResponse handleRequest(
            Map<String, Object> request,
            Context context
    ) {
        RequestConversionUtil requestConversionUtil = new RequestConversionUtil();
        AuthorDTO authorDTO = requestConversionUtil.parseRequestBody(
                request.get("body").toString(),
                AuthorDTO.class
        );

        persistData(authorDTO);

        return ApiGatewayResponse
                .builder()
                .setStatusCode(201)
                .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .setObjectBody(new CommonResponseDTO("Author registration successfully completed."))
                .build();
    }

    private void persistData(
            AuthorDTO authorDTO
    ) throws ConditionalCheckFailedException {
        String userId = UUID.randomUUID().toString();
        Map<String, AttributeValue> attributesMap = new HashMap<>();
        attributesMap.put("id", new AttributeValue(userId));
        attributesMap.put("first_name", new AttributeValue(authorDTO.getFirstName()));
        attributesMap.put("last_name", new AttributeValue(authorDTO.getLastName()));
        attributesMap.put("email", new AttributeValue(authorDTO.getEmail()));
        attributesMap.put("identification_number", new AttributeValue(authorDTO.getIdentificationNumber()));

        DynamoDBClientUtil
                .amazonDynamoDB()
                .putItem(AUTHOR_DB_TABLE, attributesMap);
    }

}
