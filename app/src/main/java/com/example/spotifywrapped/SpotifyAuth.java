package com.example.spotifywrapped;

import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@FunctionalInterface
interface AccessTokenAction {
    void performAction(String accessToken);
}

public class SpotifyAuth {
    private static String authorizationCode;
    private static String codeVerifier;
    private static String accessToken;
    private static boolean accessTokenExpired;
    private static String refreshToken;

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
    private static final String tokenURI = "https://accounts.spotify.com/api/token";

    private static final String codeChallengeMethod = "S256";
    private static final String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";

    private static final PausableThreadPoolExecutor accessTokenExecutor = PausableThreadPoolExecutor.createDefaultInstance();
    private static final ScheduledExecutorService timeoutScheduler = Executors.newSingleThreadScheduledExecutor();

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

        requestAccessToken();
    }

    public static void useAccessToken(AccessTokenAction action) {
        if (authorizationCode == null) {
            return;
        }

        accessTokenExecutor.submit(() -> {
            action.performAction(accessToken);
        });
    }

    public static void debugForceRefresh() {
        accessTokenExpired = true;
    }

    private static void requestAccessToken() {
        accessTokenExecutor.pause();
        // https://scrapeops.io/java-web-scraping-playbook/java-okhttp-post-requests/
        String formData = "grant_type=authorization_code"
            .concat("&code=" + authorizationCode)
            .concat("&redirect_uri=" + redirectURI)
            .concat("&client_id=" + clientID)
            .concat("&code_verifier=" + codeVerifier);

        MediaType contentType = MediaType.get("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(formData, contentType);

        Request request = new Request.Builder()
            .url(tokenURI)
            .post(body)
            .build();

        authClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                parseTokenResponse(response.body().charStream());
            }
        });
    }

    private static synchronized void refreshAccessToken() {
        timeoutScheduler.shutdownNow();
        RequestBody formBody = new FormBody.Builder()
                .addEncoded("grant_type", "refresh_token")
                .addEncoded("refresh_token", refreshToken)
                .addEncoded("client_id", clientID)
                .build();

        Request request = new Request.Builder()
                .url(tokenURI)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();

        authClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.w("SDF", "asdfa");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                parseTokenResponse(response.body().charStream());
                response.body().close();
            }
        });
    }

    private static void parseTokenResponse(Reader jsonReader) {
        JsonObject tokenBody = JsonParser.parseReader(jsonReader)
                .getAsJsonObject();

        accessToken = tokenBody.get("access_token").getAsString();
        Log.d(TAG, "Access Token: " + accessToken);
        accessTokenExpired = false;

        refreshToken = tokenBody.get("refresh_token").getAsString();
        int timeout = tokenBody.get("expires_in").getAsInt();

        timeoutScheduler.schedule(() -> {
            accessTokenExpired = true;
            accessTokenExecutor.pause();
            refreshAccessToken();
        }, timeout, TimeUnit.SECONDS);

        accessTokenExecutor.resume();
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
}
