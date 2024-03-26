package com.example.spotifywrapped;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Authorization {
    static String accessToken;
    @NonNull
    public static Intent getAuthorizeIntent() throws NoSuchAlgorithmException, MalformedURLException {
        String response_type = "code";
        String client_ID = "5f164b1b815e411298a2df84bae6ddbb";
        String redirect_uri = "SPOTIFY-SDK://auth";
        String scope = "user-top-read";
        String code_challenge_method = "S256";
        String code_challenge = bytesToHex(genHash());
        URL url = new URL("https://accounts.spotify.com/authorize");

        String authURL = url.toString()
                .concat("?response_type=" + response_type)
                .concat("&client_id=" + client_ID)
                .concat("&redirect_uri=" + redirect_uri)
                .concat("&scope=" + scope)
                .concat("&code_challenge_method=" + code_challenge_method)
                .concat("&code_challenge=" + code_challenge);

        // Move the following to a separate method (or merge into setAccessToken idk)
//        //IDK the web interface but assume retURL is the URL that we're redirected to after login
//        String retURL = "";
//        String[] params = retURL.split("&");
//        for (int i = 1; i < params.length; i++) {
//            if (params[i].contains("code")) {
//                accessToken = params[i].substring(params[i].indexOf("=") + 1);
//            } else if (params[i].contains("error")) {
//                throw new RuntimeException("Auth Failed");
//            }
//        }
//        accessToken = "";

        return new Intent(Intent.ACTION_VIEW, Uri.parse(authURL));
    }

    public static void setAccessToken(String accessToken) {
        Authorization.accessToken = accessToken;
    }

    private static byte[] genHash() throws NoSuchAlgorithmException {
        Random random = new Random();
        String generatedString = random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(64)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(
                generatedString.getBytes(StandardCharsets.UTF_8));
        return encodedhash;
    }
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

