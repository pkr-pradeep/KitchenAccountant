package com.example.kitchenaccountant.amazonaws.services.s3.sample;

import static com.example.kitchenaccountant.utilities.Constants.*;

import com.example.kitchenaccountant.amazonaws.services.s3.sample.auth.AWS4SignerBase;
import com.example.kitchenaccountant.amazonaws.services.s3.sample.auth.AWS4SignerForAuthorizationHeader;
import com.example.kitchenaccountant.amazonaws.services.s3.sample.util.HttpUtils;
import com.example.kitchenaccountant.utilities.CommonUtilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Samples showing how to GET an object from Amazon S3 using Signature V4
 * authorization.
 */
public class GetS3ObjectSample {

    /**
     * Request the content of the object '/ExampleObject.txt' from the given
     * bucket in the given region using virtual hosted-style object addressing.
     */
    public static void getS3Object(String bucketName, String regionName, String awsAccessKey, String awsSecretKey) {
        System.out.println("*******************************************************");
        System.out.println("*  Executing sample 'GetObjectUsingHostedAddressing'  *");
        System.out.println("*******************************************************");

        URL endpointUrl;
        try {
            String fileNamePrefix = CommonUtilities.getDateStamp("MMMM_yyyy", "Asia/Kolkata");
            endpointUrl = new URL("https://5d01feafecb2aabc05a201911480947e.r2.cloudflarestorage.com" +
                    FORWARD_SLASH + bucketName+ FORWARD_SLASH + fileNamePrefix + JSON_EXTENSION);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        /*// the region-specific endpoint to the target object expressed in path style
        URL endpointUrl;
        try {
            endpointUrl = new URL("https://" + bucketName + ".s3." + regionName + ".amazonaws.com/ExampleObject.txt");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }*/
        
        // for a simple GET, we have no body so supply the precomputed 'empty' hash
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(X_AMZ_CONTENT_SHA_256, AWS4SignerBase.EMPTY_BODY_SHA256);
        
        AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(
                endpointUrl, "GET", SERVICE_NAME, AWS_REGION);
        String authorization = signer.computeSignature(headers, 
                                                       null, // no query parameters
                                                       AWS4SignerBase.EMPTY_BODY_SHA256, 
                                                       awsAccessKey, 
                                                       awsSecretKey);
                
        // place the computed signature into a formatted 'Authorization' header
        // and call S3
        headers.put("Authorization", authorization);
        String response = HttpUtils.invokeHttpRequest(endpointUrl, "GET", headers, null);
        System.out.println("--------- Response content ---------");
        System.out.println(response);
        System.out.println("------------------------------------");
    }
}
