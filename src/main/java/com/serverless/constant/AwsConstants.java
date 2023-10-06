package com.serverless.constant;

import com.amazonaws.regions.Regions;

public class AwsConstants {

    public static final String AUTHOR_DB_TABLE = System.getenv("AUTHOR_TABLE");
    public static final Regions REGION = Regions.fromName(System.getenv("REGION"));

}
