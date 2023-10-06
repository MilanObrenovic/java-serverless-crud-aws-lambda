package com.serverless.handler;

import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.serverless.ApiGatewayResponse;
import com.serverless.dto.response.CommonResponseDTO;
import com.serverless.util.DynamoDBClientUtil;

import java.util.*;

import static com.serverless.constant.AwsConstants.AUTHOR_DB_TABLE;

public class DeleteHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    @Override
    public ApiGatewayResponse handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context
    ) {
        Map<String, AttributeValue> keyMap = new HashMap<>();
        keyMap.put("id", new AttributeValue(request.getPathParameters().get("authorId")));

        DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
                .withTableName(AUTHOR_DB_TABLE)
                .withKey(keyMap);

        DynamoDBClientUtil
                .amazonDynamoDB()
                .deleteItem(deleteItemRequest);

        return ApiGatewayResponse
                .builder()
                .setStatusCode(200)
                .setHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .setObjectBody(new CommonResponseDTO("Author deleted successfully."))
                .build();
    }

}
