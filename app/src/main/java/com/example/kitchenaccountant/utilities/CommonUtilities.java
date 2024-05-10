package com.example.kitchenaccountant.utilities;

import static com.example.kitchenaccountant.utilities.Constants.*;

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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CommonUtilities {

    public static String generateAmzCredential() {
        String dateStamp = getDateStamp("yyyyMMdd", "UTC");
        String scope = String.format("%s/%s/%s/aws4_request", dateStamp, AWS_REGION, SERVICE_NAME);
        return String.format("%s/%s", AWS_ACCESS_KEY_ID, scope);
    }

    public static String getDateStamp(String format, String timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
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


    public static String generateCanonicalRequest(String method, String path, String queryString, String payload, String dateTimeStamp) {
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

    public static String hashPayload(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateStringToSign(String dateTimeStamp, String canonicalRequest) {
        String hashedCanonicalRequest = hashCanonicalRequest(canonicalRequest);
        return "AWS4-HMAC-SHA256\n" +
                dateTimeStamp + "\n" +
                dateTimeStamp.substring(0, 8) + "/" + AWS_REGION + "/" + SERVICE_NAME + "/aws4_request\n" +
                hashedCanonicalRequest;
    }

    public static String hashCanonicalRequest(String canonicalRequest) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonicalRequest.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] deriveSigningKey(String secretKey, String region, String dateStamp) {
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

    public static String generateSignature(byte[] signingKey, String stringToSign) {
        try {
            byte[] signature = hmacSHA256(signingKey, stringToSign);
            return bytesToHex(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] hmacSHA256(byte[] key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKey);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String bytesToHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
