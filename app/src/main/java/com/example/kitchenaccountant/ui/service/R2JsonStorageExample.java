package com.example.kitchenaccountant.ui.service;

import static com.example.kitchenaccountant.utilities.Constants.ISKCON_KITCHEN_MGT_BUCKET;

import android.os.Build;

import com.example.kitchenaccountant.amazonaws.services.s3.sample.PutS3ObjectSample;

import java.util.concurrent.CompletableFuture;

public class R2JsonStorageExample {
    private static final String AWS_ACCESS_KEY_ID = "c6da6d0a4c9788582335aca93b9ab610";
    private static final String AWS_SECRET_ACCESS_KEY = "abe4007ffa243a6c685e6209b81417904eabca58cb78b5d927c0206d22ac1d71";
    private static final String AWS_REGION = "auto"; // Change to your AWS region
    public static void saveObjectIntoR2UsingCompletableFuture(String formDataString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture.runAsync(() -> {
                try {
                    PutS3ObjectSample.putS3Object(formDataString, ISKCON_KITCHEN_MGT_BUCKET, AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


}
