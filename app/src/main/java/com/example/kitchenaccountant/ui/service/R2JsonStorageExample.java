package com.example.kitchenaccountant.ui.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.kitchenaccountant.amazonaws.services.s3.sample.PutS3ObjectSample;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class R2JsonStorageExample {

    private static final String AWS_ACCESS_KEY_ID = "c6da6d0a4c9788582335aca93b9ab610";
    private static final String AWS_SECRET_ACCESS_KEY = "abe4007ffa243a6c685e6209b81417904eabca58cb78b5d927c0206d22ac1d71";
    private static final String AWS_REGION = "auto"; // Change to your AWS region
    private static final String SERVICE_NAME = "s3";
    private static final String ACCOUNT_ID = "5d01feafecb2aabc05a201911480947e";
    public static void saveObjectIntoR2UsingCompletableFuture(String formDataString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture.runAsync(() -> {
                OkHttpClient client = new OkHttpClient();
                String bucketName = "iskcon-kitchen-management";
                /*MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, formDataString);
                String fileName = "temp.json";
                String bucketName = "iskcon-kitchen-management";
                String amzCredential = generateAmzCredential();
                String method = "PUT"; // HTTP method
                String path = "/".concat(bucketName).concat("/").concat(fileName); // URI path
                String queryString = "X-Amz-Expires=3600"; // Query string parameters
                String awsSignature = formAwSSignature(method, path, queryString, formDataString);*/
                /*HttpUrl url = HttpUrl.parse("https://"+ACCOUNT_ID+".r2.cloudflarestorage.com"
                        .concat(path))
                        .newBuilder()
                        .addQueryParameter("X-Amz-Credential", amzCredential)
                        .addQueryParameter("X-Amz-Date", getCurrentUTCDateTime())
                        .addQueryParameter("x-Amz-Signature", awsSignature)
                        .addQueryParameter("X-Amz-Algorithm", "AWS4-HMAC-SHA256")
                        .addQueryParameter("X-Amz-SignedHeaders", "content-length;content-type;host")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .put(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-amz-content-sha256", "UNSIGNED-PAYLOAD")
                        .addHeader("x-amz-user-agent", "your_user_agent") // Replace with your user agent
                        .addHeader("x-amz-sdk-invocation-id", UUID.randomUUID().toString())
                        .addHeader("x-amz-sdk-request-id", UUID.randomUUID().toString())
                        .build();*/

                try {
                    PutS3ObjectSample.putS3Object(formDataString, bucketName, AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);

                       /* if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);*/
                } /*catch (IOException e) {
                    throw new RuntimeException(e);
                } */catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static String generateAmzCredential() {
        String dateStamp = getDateStamp();
        String scope = String.format("%s/%s/%s/aws4_request", dateStamp, AWS_REGION, SERVICE_NAME);
        return String.format("%s/%s", AWS_ACCESS_KEY_ID, scope);
    }

    private static String getDateStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }

    public static String getCurrentUTCDateTime() {
        LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
            return currentDateTime.format(formatter);
        }
        return null;
    }

    public static String formAwSSignature(String method, String path, String queryString, String payload) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateTimeStamp = dateFormat.format(new Date());
        // Generate canonical request
        String canonicalRequest = generateCanonicalRequest(method, path, queryString, payload, dateTimeStamp);
        // Generate string to sign
        String stringToSign = generateStringToSign(dateTimeStamp, canonicalRequest);
        // Generate signing key
        byte[] signingKey = deriveSigningKey(AWS_SECRET_ACCESS_KEY, AWS_REGION, dateTimeStamp.substring(0, 8));
        // Generate signature
       return generateSignature(signingKey, stringToSign);
    }


    private static String generateCanonicalRequest(String method, String path, String queryString, String payload, String dateTimeStamp) {
        String hashedPayload = hashPayload(payload);
        return method + "\n" +
                path + "\n" +
                queryString + "\n" +
                "host:"+ACCOUNT_ID+".r2.cloudflarestorage.com\n" +
                "x-amz-content-sha256:" + hashedPayload + "\n" +
                "x-amz-date:" + dateTimeStamp + "\n" +
                "\n" +
                "content-length;content-type;host\n" +
                hashedPayload;
    }

    private static String hashPayload(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String generateStringToSign(String dateTimeStamp, String canonicalRequest) {
        String hashedCanonicalRequest = hashCanonicalRequest(canonicalRequest);
        return "AWS4-HMAC-SHA256\n" +
                dateTimeStamp + "\n" +
                dateTimeStamp.substring(0, 8) + "/" + AWS_REGION + "/" + SERVICE_NAME + "/aws4_request\n" +
                hashedCanonicalRequest;
    }

    private static String hashCanonicalRequest(String canonicalRequest) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonicalRequest.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] deriveSigningKey(String secretKey, String region, String dateStamp) {
        try {
            byte[] secretKeyBytes = ("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8);
            byte[] dateKey = hmacSHA256(secretKeyBytes, dateStamp);
            byte[] regionKey = hmacSHA256(dateKey, region);
            byte[] serviceKey = hmacSHA256(regionKey, SERVICE_NAME);
            byte[] signingKey = hmacSHA256(serviceKey, "aws4_request");
            return signingKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateSignature(byte[] signingKey, String stringToSign) {
        try {
            byte[] signature = hmacSHA256(signingKey, stringToSign);
            return bytesToHex(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] hmacSHA256(byte[] key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKey);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String bytesToHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
