package com.example.kitchenaccountant.amazonaws.services.s3.sample;

import static com.example.kitchenaccountant.utilities.Constants.APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8;
import static com.example.kitchenaccountant.utilities.Constants.CLOUDFLARE_HOST;
import static com.example.kitchenaccountant.utilities.Constants.CONTENT_LENGTH;
import static com.example.kitchenaccountant.utilities.Constants.CONTENT_TYPE;
import static com.example.kitchenaccountant.utilities.Constants.EXPENSE;
import static com.example.kitchenaccountant.utilities.Constants.FORWARD_SLASH;
import static com.example.kitchenaccountant.utilities.Constants.HTTPS_PROTOCOL;
import static com.example.kitchenaccountant.utilities.Constants.INCOME;
import static com.example.kitchenaccountant.utilities.Constants.JSON_EXTENSION;
import static com.example.kitchenaccountant.utilities.Constants.SERVICE_NAME;
import static com.example.kitchenaccountant.utilities.Constants.X_AMZ_CONTENT_SHA_256;

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
    public static String putS3Object(String transactionType, String objectContent, String bucketName, String regionName, String awsAccessKey, String awsSecretKey) throws IOException {
        String existingIncomeData;
        String existingExpenseData;
        String objectContentModified = null;
        URL endpointUrl;
        try {
            String fileNamePrefix = transactionType.concat(CommonUtilities
                    .getDateStamp("MMMM_yyyy", "Asia/Kolkata"));
            endpointUrl = new URL(HTTPS_PROTOCOL + CLOUDFLARE_HOST +
                    FORWARD_SLASH + bucketName + FORWARD_SLASH + fileNamePrefix + JSON_EXTENSION);
            if (INCOME.equals(transactionType)) {
                existingIncomeData = GetS3ObjectIntoCloudFlare.getS3Object(fileNamePrefix, bucketName, regionName, awsAccessKey, awsSecretKey);
                objectContentModified = CommonUtilities.mergeIncomeJsonIntoAnother(existingIncomeData, objectContent);
            } else if (EXPENSE.equals(transactionType)) {
                existingExpenseData = GetS3ObjectIntoCloudFlare.getS3Object(fileNamePrefix, bucketName, regionName, awsAccessKey, awsSecretKey);
                objectContentModified = CommonUtilities.mergeExpenseJsonIntoAnother(existingExpenseData, objectContent);
            }
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
