package com.example.spotifywrapped;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpotifyAuth {
    private static String authorizationCode;
    private static String codeVerifier;
    private static String accessToken;
    private static int expiresIn;
    private static Map<String, Object> accessTokenResponseJSON;
    
    private static final String codeChallengeMethod = "S256";
    private static final String redirectURI = "https://spotifywrappedapp-819f6.firebaseapp.com/app-data";
    private static final String clientID = "5f164b1b815e411298a2df84bae6ddbb";

    private static final String authCodeURL = "https://accounts.spotify.com/authorize";
    private static final String accessTokenURL = "https://accounts.spotify.com/api/token";

    @NonNull
    public static Intent getAuthorizationIntent() {
        final String responseType = "code";
        String scope = "user-top-read";
        String codeChallenge = bytesToHex(genHash());

        String authCodeEncodedURL = authCodeURL
                .concat("?response_type=" + responseType)
                .concat("&client_id=" + clientID)
                .concat("&redirect_uri=" + redirectURI)
                .concat("&scope=" + scope)
                .concat("&code_challenge_method=" + codeChallengeMethod)
                .concat("&code_challenge=" + codeChallenge);

        return new Intent(Intent.ACTION_VIEW, Uri.parse(authCodeEncodedURL));
    }

    public static void parseAuthorizationResponse(Uri response) {
        String[] params = response.toString().split("&");
        for (int i = 1; i < params.length; i++) {
            if (params[i].contains("code")) {
                authorizationCode = params[i].substring(params[i].indexOf("=") + 1);
            } else if (params[i].contains("error")) {
                throw new RuntimeException("Auth Failed");
            }
        }
        requestAccessToken();
    }

    private static void requestAccessToken() {
        final String grantType = "authorization_code";

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(accessTokenURL);

        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("grant_type", grantType));
        params.add(new BasicNameValuePair("code", authorizationCode));
        params.add(new BasicNameValuePair("redirect_uri", redirectURI));
        params.add(new BasicNameValuePair("client_id", clientID));
        params.add(new BasicNameValuePair("code_verifier", codeVerifier));

        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("SpotifyAuth -- UTF-8 charset not supported");
        }

        try {
            HttpResponse response = client.execute(post);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                // EntityUtils to get the response content
                String content =  EntityUtils.toString(respEntity);
                parseAccessTokenResponse(content);
            }

        } catch (ClientProtocolException e) {
            throw new RuntimeException("SpotifyAuth -- error in http protocol");
        } catch (IOException e) {
            throw new RuntimeException("SpotifyAuth -- error in internet connection");
        }
    }

    private static void parseAccessTokenResponse(String content) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> jsonMap = gson.fromJson(content, type);

        accessToken = (String) jsonMap.get("access_token");
        accessTokenResponseJSON = jsonMap;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    private static byte[] genHash() {
        Random random = new Random();
        codeVerifier = random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(64)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(
                    codeVerifier.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SpotifyAuth -- SHA-256 not available");
        }
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

