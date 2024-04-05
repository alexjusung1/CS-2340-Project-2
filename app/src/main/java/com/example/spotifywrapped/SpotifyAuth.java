package com.example.spotifywrapped;

import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpotifyAuth {
    private static String authorizationCode;
    private static String accessToken;
    private static int expiresIn;
    private static Map<String, Object> accessTokenResponseJSON;
    
    private static final String codeChallengeMethod = "S256";
    private static String codeVerifier;
    private static final String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";

    private static final String redirectURI = "https://spotifywrappedapp-819f6.firebaseapp.com/app-data";
    private static final String clientID = "5f164b1b815e411298a2df84bae6ddbb";

    private static final OkHttpClient authClient;

    static {
        // AuthClient setup
        authClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private static final String authCodeURI = "https://accounts.spotify.com/authorize";
    private static final String accessTokenURI = "https://accounts.spotify.com/api/token";

    private static final String TAG = "SpotifyAuth";

    @NonNull
    public static Intent getAuthorizationIntent() {
        final String responseType = "code";
        String scope = "user-top-read";

        codeVerifier = genCodeVerifier();
        Log.d(TAG, "Generated code verifier: " + codeVerifier);

        final String codeChallenge = getCodeChallenge(codeVerifier);
        Log.d(TAG, "Code challenge: " + codeChallenge);

        String authCodeEncodedURL = authCodeURI
                .concat("?response_type=" + responseType)
                .concat("&client_id=" + clientID)
                .concat("&redirect_uri=" + redirectURI)
                .concat("&scope=" + scope)
                .concat("&code_challenge_method=" + codeChallengeMethod)
                .concat("&code_challenge=" + codeChallenge);

        return new Intent(Intent.ACTION_VIEW, Uri.parse(authCodeEncodedURL));
    }

    public static void parseAuthorizationResponse(Uri response) {
        authorizationCode = response.getQueryParameter("code");
        Log.d(TAG, "Authorization Code: " + authorizationCode);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                requestAccessTokenNew();
            } catch (IOException e) {
                throw new RuntimeException("SpotifyAuth -- auth token failed");
            }
        });
    }

    private static void requestAccessTokenNew() throws IOException {
        // https://scrapeops.io/java-web-scraping-playbook/java-okhttp-post-requests/
        String formData = "grant_type=authorization_code"
            .concat("&code=" + authorizationCode)
            .concat("&redirect_uri=" + redirectURI)
            .concat("&client_id=" + clientID)
            .concat("&code_verifier=" + codeVerifier);

        MediaType contentType = MediaType.get("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(formData, contentType);

        Request request = new Request.Builder()
            .url(accessTokenURI)
            .post(body)
            .build();

        Response response = authClient.newCall(request).execute();

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
        // TODO: check if expired and start background thread to refresh token
        return accessToken;
    }

    private static String genCodeVerifier() {
        SecureRandom rng = new SecureRandom();

        return rng.ints(0, allowedChars.length())
                .limit(64)
                .mapToObj(allowedChars::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private static String getCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.encodeToString(hash, Base64.URL_SAFE | Base64.NO_PADDING);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SpotifyAuth -- SHA-256 not available");
        }
    }

//    private static byte[] genHash() {
//        Random random = new Random();
//        codeVerifier = random.ints(48, 123)
//                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
//                .limit(64)
//                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString();
//        Log.w("FFFFFF", codeVerifier);
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            return digest.digest(
//                    codeVerifier.getBytes(StandardCharsets.UTF_8));
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("SpotifyAuth -- SHA-256 not available");
//        }
//    }
//    private static String bytesToHex(byte[] hash) {
//        return Base64.encodeToString(hash, Base64.DEFAULT);
//        StringBuilder hexString = new StringBuilder(2 * hash.length);
//        for (byte b : hash) {
//            String hex = Integer.toHexString(0xff & b);
//            if (hex.length() == 1) {
//                hexString.append('0');
//            }
//            hexString.append(hex);
//        }
//        Log.w("asdflkasjdlfja", hexString.toString());
//        return hexString.toString();
//    }
}

