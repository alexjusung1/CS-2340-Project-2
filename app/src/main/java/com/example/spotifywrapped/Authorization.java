package com.example.spotifywrapped;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Authorization {
    private static String authorizationCode;
    private static String accessToken;

    @NonNull
    public static Intent getAuthorizationIntent() throws NoSuchAlgorithmException, MalformedURLException {
        String response_type = "code";
        String client_ID = "5f164b1b815e411298a2df84bae6ddbb";
        String redirect_uri = "https://spotifywrappedapp-819f6.firebaseapp.com/app-data";
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

        return new Intent(Intent.ACTION_VIEW, Uri.parse(authURL));
    }

    public static void parseAuthorizationResponse(Uri response) {
        String[] params = response.toString().split("&");
        for (int i = 1; i < params.length; i++) {
            if (params[i].contains("code")) {
                authorizationCode = params[i].substring(params[i].indexOf("=") + 1);
                Log.i("Test", "")
            } else if (params[i].contains("error")) {
                throw new RuntimeException("Auth Failed");
            }
        }
    }

    public static void requestAccessToken() throws MalformedURLException {
        URL url = new URL("https://accounts.spotify.com/api/token");
        // Implement connection using "https://developer.spotify.com/documentation/web-api/tutorials/code-pkce-flow"
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

