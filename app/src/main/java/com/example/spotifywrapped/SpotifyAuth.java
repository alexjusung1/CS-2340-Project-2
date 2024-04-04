package com.example.spotifywrapped;

import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

// import org.apache.hc.client5.http.classic.methods.HttpPost;
// import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
// import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
// import org.apache.hc.client5.http.impl.classic.HttpClients;
// import org.apache.hc.core5.http.HttpEntity;
// import org.apache.hc.core5.http.NameValuePair;
// import org.apache.hc.core5.http.io.entity.EntityUtils;
// import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
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
        String strUrl = response.toString();
        int question = strUrl.indexOf("code");
        authorizationCode = strUrl.substring(question + 5);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                requestAccessTokenNew();
            } catch (IOException e) {
                throw new RuntimeException("asdf");
            }
        });
    }

//    private static void requestAccessTokenOld() {
//        final String grantType = "authorization_code";
//
//        try (CloseableHttpClient client = HttpClients.createDefault()) {
//            HttpPost post = new HttpPost(accessTokenURL);
//            List<NameValuePair> params = new ArrayList<>();
//
//            params.add(new BasicNameValuePair("grant_type", grantType));
//            params.add(new BasicNameValuePair("code", authorizationCode));
//            params.add(new BasicNameValuePair("redirect_uri", redirectURI));
//            params.add(new BasicNameValuePair("client_id", clientID));
//            params.add(new BasicNameValuePair("code_verifier", codeVerifier));
//
//            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
//
//            post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
//
//            client.execute(post, response -> {
//                HttpEntity respEntity = response.getEntity();
//
//                if (respEntity != null) {
//                    String content = EntityUtils.toString(respEntity);
//                    parseAccessTokenResponse(content);
//                }
//                return null;
//            });
//        } catch (IOException e) {
//            throw new RuntimeException("SpotifyAuth -- IOException during connection");
//        }
//
//
//    }

    private static void requestAccessTokenNew() throws IOException {

        // https://scrapeops.io/java-web-scraping-playbook/java-okhttp-post-requests/

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        
        String formData = "grant_type=authorization_code"
            .concat("&code=" + authorizationCode)
            .concat("&redirect_uri=" + redirectURI)
            .concat("&client_id=" + clientID)
            .concat("&code_verifier=" + codeVerifier);

        MediaType contentType = MediaType.get("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(formData, contentType);

        Request request = new Request.Builder()
            .url(accessTokenURL)
            .post(body)
            .build();

        Response response = client.newCall(request).execute();

        System.out.println("XXXXXX" + response.body().string());
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

    public static String _debugGetAuthorizationCode() { return authorizationCode; }

    private static byte[] genHash() {
        Random random = new Random();
        codeVerifier = random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(64)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        Log.w("FFFFFF", codeVerifier);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(
                    codeVerifier.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SpotifyAuth -- SHA-256 not available");
        }
    }
    private static String bytesToHex(byte[] hash) {
        return Base64.encodeToString(hash, Base64.DEFAULT);
        //Old
        /*
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        Log.w("asdflkasjdlfja", hexString.toString());
        return hexString.toString();*/
    }
}

