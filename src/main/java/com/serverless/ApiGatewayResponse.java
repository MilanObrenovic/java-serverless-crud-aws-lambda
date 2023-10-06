package com.serverless;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiGatewayResponse {

	private int statusCode;
	private String body;
	private Map<String, String> headers;
	private boolean isBase64Encoded;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private static final Logger LOG = Logger.getLogger(ApiGatewayResponse.Builder.class);

		private static final ObjectMapper objectMapper = new ObjectMapper();

		private int statusCode = 200;
		private Map<String, String> headers = Collections.emptyMap();
		private String rawBody;
		private Object objectBody;
		private byte[] binaryBody;
		private boolean base64Encoded;

		public Builder setStatusCode(int statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public Builder setHeaders(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}

		/**
		 * Builds the {@link ApiGatewayResponse} using the passed raw body string.
		 */
		public Builder setRawBody(String rawBody) {
			this.rawBody = rawBody;
			return this;
		}

		/**
		 * Builds the {@link ApiGatewayResponse} using the passed object body
		 * converted to JSON.
		 */
		public Builder setObjectBody(Object objectBody) {
			this.objectBody = objectBody;
			return this;
		}

		/**
		 * Builds the {@link ApiGatewayResponse} using the passed binary body
		 * encoded as base64. {@link #setBase64Encoded(boolean)
		 * setBase64Encoded(true)} will be in invoked automatically.
		 */
		public Builder setBinaryBody(byte[] binaryBody) {
			this.binaryBody = binaryBody;
			setBase64Encoded(true);
			return this;
		}

		/**
		 * A binary or rather a base64encoded responses requires
		 * <ol>
		 * <li>"Binary Media Types" to be configured in API Gateway
		 * <li>a request with an "Accept" header set to one of the "Binary Media
		 * Types"
		 * </ol>
		 */
		public Builder setBase64Encoded(boolean base64Encoded) {
			this.base64Encoded = base64Encoded;
			return this;
		}

		public ApiGatewayResponse build() {
			String body = null;
			if (rawBody != null) {
				body = rawBody;
			} else if (objectBody != null) {
				try {
					body = objectMapper.writeValueAsString(objectBody);
				} catch (JsonProcessingException e) {
					LOG.error("failed to serialize object", e);
					throw new RuntimeException(e);
				}
			} else if (binaryBody != null) {
				body = new String(Base64.getEncoder().encode(binaryBody), StandardCharsets.UTF_8);
			}
			return new ApiGatewayResponse(statusCode, body, headers, base64Encoded);
		}
	}

}
