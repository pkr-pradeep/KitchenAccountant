package com.example.kitchenaccountant.amazonaws.services.s3.sample;

import static com.example.kitchenaccountant.utilities.Constants.AWS_ACCESS_KEY_ID;
import static com.example.kitchenaccountant.utilities.Constants.FORWARD_SLASH;
import static com.example.kitchenaccountant.utilities.Constants.JSON_EXTENSION;

import com.example.kitchenaccountant.amazonaws.services.s3.sample.auth.AWS4SignerBase;
import com.example.kitchenaccountant.amazonaws.services.s3.sample.auth.AWS4SignerForAuthorizationHeader;
import com.example.kitchenaccountant.amazonaws.services.s3.sample.util.BinaryUtils;
import com.example.kitchenaccountant.amazonaws.services.s3.sample.util.HttpUtils;
import com.example.kitchenaccountant.utilities.CommonUtilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PutS3ObjectSample {

    /**
     * Uploads content to an Amazon S3 object in a single call using Signature V4 authorization.
     */
    public static String putS3Object(String objectContent, String bucketName, String regionName, String awsAccessKey, String awsSecretKey) throws IOException {
        System.out.println("************************************************");
        System.out.println("*        Executing sample 'PutS3Object'        *");
        System.out.println("************************************************");
        String objectContentModified = CommonUtilities.mergeJsonWithJackson(objectContent);
        String getResponse = GetS3ObjectSample.getS3Object(bucketName, regionName, awsAccessKey, awsSecretKey);
        URL endpointUrl;
        try {
            String fileNamePrefix = CommonUtilities.getDateStamp("MMMM_yyyy", "Asia/Kolkata");
            endpointUrl = new URL("https://5d01feafecb2aabc05a201911480947e.r2.cloudflarestorage.com" +
                    FORWARD_SLASH + bucketName + FORWARD_SLASH + fileNamePrefix + JSON_EXTENSION);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        byte[] contentHash = AWS4SignerBase.hash(objectContentModified);
        String contentHashString = BinaryUtils.toHex(contentHash);

        Map<String, String> headers = new HashMap<>();
        //headers.put("x-amz-content-sha256", AWS4SignerBase.UNSIGNED_PAYLOAD);
        headers.put("content-length", "" + objectContent.length());
        headers.put("Content-Type", "application/json");

        AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(
                endpointUrl, "PUT", "s3", regionName);
        String authorization = signer.computeSignature(headers,
                null, // no query parameters
                AWS4SignerBase.UNSIGNED_PAYLOAD,
                awsAccessKey,
                awsSecretKey);

        // express authorization for this as a header
        headers.put("Authorization", authorization);
        String response = putJsonIntoCloudFlare(headers, objectContentModified);
        // make the call to Amazon S3
        //String response = HttpUtils.invokeHttpRequest(endpointUrl, "PUT", headers, objectContent);
        System.out.println("--------- Response content ---------");
        System.out.println(response);
        System.out.println("------------------------------------");
        return response;
    }

    public static String putJsonIntoCloudFlare(Map<String, String> headers, String contentJson) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, contentJson);
        Request request = new Request.Builder()
                .url("https://5d01feafecb2aabc05a201911480947e.r2.cloudflarestorage.com/iskcon-kitchen-management/pkr1.json?" +
                        "X-Amz-Expires=86400&X-Amz-Date=".concat(headers.get("x-amz-date")) +
                        "&X-Amz-Algorithm=AWS4-HMAC-SHA256" +
                        "&X-Amz-Credential=".concat(AWS_ACCESS_KEY_ID).concat("%2F20240512%2Fauto%2Fs3%2Faws4_request") +
                        "&X-Amz-SignedHeaders=content-length%3Bcontent-type%3Bhost" +
                        "&X-Amz-Signature=".concat(headers.get("Authorization")))
                .method("PUT", body)
                .addHeader("x-amz-content-sha256", AWS4SignerBase.UNSIGNED_PAYLOAD)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String responseBodyJson = response.body().string();
        System.out.println("--------- Response content ---------");
        System.out.println(responseBodyJson);
        System.out.println("------------------------------------");
        return responseBodyJson;
    }
}
