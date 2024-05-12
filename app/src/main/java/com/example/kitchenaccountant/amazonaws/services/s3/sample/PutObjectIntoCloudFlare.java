package com.example.kitchenaccountant.amazonaws.services.s3.sample;

import static com.example.kitchenaccountant.utilities.Constants.*;

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

public class PutObjectIntoCloudFlare {

    /**
     * Uploads content to an Amazon S3 object in a single call using Signature V4 authorization.
     */
    public static String putS3Object(String objectContent, String bucketName, String regionName, String awsAccessKey, String awsSecretKey) throws IOException {
        String existingIncomeData = GetS3ObjectIntoCloudFlare.getS3Object(bucketName, regionName, awsAccessKey, awsSecretKey);
        String objectContentModified = CommonUtilities.mergeJsonIntoAnother(existingIncomeData, objectContent);
        URL endpointUrl;
        try {
            String fileNamePrefix = CommonUtilities.getDateStamp("MMMM_yyyy", "Asia/Kolkata");
            endpointUrl = new URL(HTTPS_PROTOCOL + CLOUDFLARE_HOST +
                    FORWARD_SLASH + bucketName + FORWARD_SLASH + fileNamePrefix + JSON_EXTENSION);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        byte[] contentHash = AWS4SignerBase.hash(objectContentModified);
        String contentHashString = BinaryUtils.toHex(contentHash);

        Map<String, String> headers = new HashMap<>();
        headers.put(X_AMZ_CONTENT_SHA_256, contentHashString);
        headers.put(CONTENT_LENGTH, "" + objectContentModified.length());
        headers.put(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);

        AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(
                endpointUrl, "PUT", SERVICE_NAME, regionName);
        String authorization = signer.computeSignature(headers,
                null, // no query parameters
                contentHashString,
                awsAccessKey,
                awsSecretKey);

        // express authorization for this as a header
        headers.put("Authorization", authorization);
        String response = HttpUtils.invokeHttpRequest(endpointUrl, "PUT", headers, objectContentModified);
        // make the call to Amazon S3
        System.out.println("--------- Response content ---------");
        System.out.println(response);
        System.out.println("------------------------------------");
        return response;
    }
}
