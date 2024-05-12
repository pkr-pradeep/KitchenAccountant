package com.example.kitchenaccountant.amazonaws.services.s3.sample;

import static com.example.kitchenaccountant.utilities.Constants.*;

import com.example.kitchenaccountant.amazonaws.services.s3.sample.auth.AWS4SignerBase;
import com.example.kitchenaccountant.amazonaws.services.s3.sample.auth.AWS4SignerForAuthorizationHeader;
import com.example.kitchenaccountant.utilities.CommonUtilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Samples showing how to GET an object from Amazon S3 using Signature V4
 * authorization.
 */
public class GetS3ObjectIntoCloudFlare {

    /**
     * Request the content of the object '/ExampleObject.txt' from the given
     * bucket in the given region using virtual hosted-style object addressing.
     *
     * @return
     */
    public static String getS3Object(String bucketName, String regionName, String awsAccessKey, String awsSecretKey) {
        System.out.println("*******************************************************");
        System.out.println("*  Executing sample 'GetObjectUsingHostedAddressing'  *");
        System.out.println("*******************************************************");

        // the region-specific endpoint to the target object expressed in path style
        URL endpointUrl;
        try {
            String fileNamePrefix = CommonUtilities.getDateStamp("MMMM_yyyy", "Asia/Kolkata");
            endpointUrl = new URL(HTTPS_PROTOCOL + CLOUDFLARE_HOST +
                    FORWARD_SLASH + bucketName + FORWARD_SLASH + fileNamePrefix + JSON_EXTENSION);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }

        // for a simple GET, we have no body so supply the precomputed 'empty' hash
        Map<String, String> headers = new HashMap<>();

        AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(
                endpointUrl, GET, SERVICE_NAME, regionName);
        String authorization = signer.computeSignature(headers,
                null, // no query parameters
                AWS4SignerBase.EMPTY_BODY_SHA256,
                awsAccessKey,
                awsSecretKey);
        headers.put("Authorization", authorization);
        try {
            return getJsonFromCloudFlare(endpointUrl.toString(), headers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getJsonFromCloudFlare(String url, Map<String, String> headers) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("X-Amz-Content-Sha256", AWS4SignerBase.EMPTY_BODY_SHA256)
                .addHeader("X-Amz-Date", headers.get("x-amz-date"))
                .addHeader("Authorization", headers.get("Authorization"))
                .build();
        Response response = client.newCall(request).execute();
        String responseBodyJson = response.body().string();
        System.out.println("--------- Response content ---------");
        System.out.println(responseBodyJson);
        System.out.println("------------------------------------");
        return responseBodyJson;
    }
}
