package com.serverless;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	@Override
	public ApiGatewayResponse handleRequest(
			Map<String, Object> request,
			Context context
	) {
		Response responseBody = new Response(
				"Go Serverless v1.x! Your function executed successfully!",
				request
		);

		return ApiGatewayResponse
				.builder()
				.setStatusCode(200)
				.setObjectBody(responseBody)
				.setHeaders(Collections.singletonMap("Content-Type", "application/json"))
				.build();
	}
}
