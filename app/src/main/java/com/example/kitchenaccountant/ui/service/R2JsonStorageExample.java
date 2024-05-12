package com.example.kitchenaccountant.ui.service;

import static com.example.kitchenaccountant.utilities.Constants.AWS_ACCESS_KEY_ID;
import static com.example.kitchenaccountant.utilities.Constants.AWS_REGION;
import static com.example.kitchenaccountant.utilities.Constants.AWS_SECRET_ACCESS_KEY;
import static com.example.kitchenaccountant.utilities.Constants.ISKCON_KITCHEN_MGT_BUCKET;

import android.os.Build;

import com.example.kitchenaccountant.amazonaws.services.s3.sample.PutObjectIntoCloudFlare;

import java.util.concurrent.CompletableFuture;

public class R2JsonStorageExample {
    public static void saveObjectIntoR2UsingCompletableFuture(String formDataString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture.runAsync(() -> {
                try {
                    PutObjectIntoCloudFlare.putS3Object(formDataString, ISKCON_KITCHEN_MGT_BUCKET, AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
