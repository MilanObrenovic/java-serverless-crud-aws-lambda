package com.serverless.util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import static com.serverless.constant.AwsConstants.REGION;

public class DynamoDBClientUtil {

    public static AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(REGION)
                .build();
    }

}
