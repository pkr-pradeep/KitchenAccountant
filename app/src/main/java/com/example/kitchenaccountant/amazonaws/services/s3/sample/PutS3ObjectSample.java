package com.example.kitchenaccountant.amazonaws.services.s3.sample;

import static com.example.kitchenaccountant.utilities.Constants.FORWARD_SLASH;
import static com.example.kitchenaccountant.utilities.Constants.JSON_EXTENSION;

import com.example.kitchenaccountant.amazonaws.services.s3.sample.auth.AWS4SignerBase;
import com.example.kitchenaccountant.amazonaws.services.s3.sample.auth.AWS4SignerForAuthorizationHeader;
import com.example.kitchenaccountant.amazonaws.services.s3.sample.util.BinaryUtils;
import com.example.kitchenaccountant.amazonaws.services.s3.sample.util.HttpUtils;
import com.example.kitchenaccountant.utilities.CommonUtilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PutS3ObjectSample {
    
    /**
     * Uploads content to an Amazon S3 object in a single call using Signature V4 authorization.
     */
    public static String putS3Object(String objectContent, String bucketName, String regionName, String awsAccessKey, String awsSecretKey) {
        System.out.println("************************************************");
        System.out.println("*        Executing sample 'PutS3Object'        *");
        System.out.println("************************************************");

        String getResponse = GetS3ObjectSample.getS3Object(bucketName, regionName, awsAccessKey, awsSecretKey);

        URL endpointUrl;
        try {
            String fileNamePrefix = CommonUtilities.getDateStamp("MMMM_yyyy", "Asia/Kolkata");
            endpointUrl = new URL("https://5d01feafecb2aabc05a201911480947e.r2.cloudflarestorage.com" +
                    FORWARD_SLASH + bucketName+ FORWARD_SLASH + fileNamePrefix + JSON_EXTENSION);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        
        // precompute hash of the body content
        byte[] contentHash = AWS4SignerBase.hash(objectContent);
        String contentHashString = BinaryUtils.toHex(contentHash);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("x-amz-content-sha256", contentHashString);
        headers.put("content-length", "" + objectContent.length());

        AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(
                endpointUrl, "PUT", "s3", regionName);
        String authorization = signer.computeSignature(headers, 
                                                       null, // no query parameters
                                                       contentHashString, 
                                                       awsAccessKey, 
                                                       awsSecretKey);
                
        // express authorization for this as a header
        headers.put("Authorization", authorization);

        // make the call to Amazon S3
        String response = HttpUtils.invokeHttpRequest(endpointUrl, "PUT", headers, objectContent);
        System.out.println("--------- Response content ---------");
        System.out.println(response);
        System.out.println("------------------------------------");
        return response;
    }
}
