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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class SpotifyAuth {
    private static String authorizationCode;
    private static String codeVerifier;
    private static String accessToken;
    private static int expiresIn;
    private static Map<String, Object> accessTokenResponseJSON;
    
    private static final String code_challenge_method = "S256";
    private static final String redirect_uri = "https://spotifywrappedapp-819f6.firebaseapp.com/app-data";
    private static final String client_id = "5f164b1b815e411298a2df84bae6ddbb";

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
            } else if (params[i].contains("error")) {
                throw new RuntimeException("Auth Failed");
            }
        }
    }

    public static void requestAccessToken() throws MalformedURLException {
        URL url = new URL("https://accounts.spotify.com/api/token");

        Intent authIntent = getAuthorizationIntent();
        startActivity(authIntent);
        // Implement connection using "https://developer.spotify.com/documentation/web-api/tutorials/code-pkce-flow"

        String grant_type = "authorization_code";
        //code = autorization_code (field)
        //redirect_uri in fields
        //client_id in fields
        //code_verifier in fields

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url.toString());

        List<NameValuePair> params = new ArrayList<>();

        //here we add all the parameters
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code", authorizationCode));
        params.add(new BasicNameValuePair("redirect_uri", redirectUri));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("code_verifier", codeVerifier));

        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (Exception e) {
            Log.e("SpotifyAuth", "Error in requestAccessToken - setEntity for HTTP POST.");
        }

        try {
            HttpResponse response = client.execute(httpPost);

            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                // EntityUtils to get the response content
                String content =  EntityUtils.toString(respEntity);
                parseAccessTokenResponse(content);
            }

            
        } catch (ClientProtocolException e) {
            Log.e("SpotifyAuth", "ClientProtocolException in http response.");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Spotify Auth", "IOException in http response");
            e.printStackTrace();
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

    private static byte[] genHash() throws NoSuchAlgorithmException {
        Random random = new Random();
        codeVerifier = random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(64)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(
                codeVerifier.getBytes(StandardCharsets.UTF_8));
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

