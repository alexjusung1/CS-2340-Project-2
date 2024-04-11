package com.example.spotifywrapped.utils;

import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.spotifywrapped.data.TimeRange;
import com.example.spotifywrapped.data.ArtistData;
import com.example.spotifywrapped.data.TrackData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyAPI {
    private static final OkHttpClient reqClient;
    static {
        // AuthClient setup
        reqClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    private static final String topArtistsURL = "https://api.spotify.com/v1/me/top/artists";
    private static final String topTracksURL = "https://api.spotify.com/v1/me/top/tracks";
    private static final String selfURI = "https://api.spotify.com/v1/me";

    private static final String TAG = "SpotifyAPI";

    public static CompletableFuture<List<ArtistData>> getTopArtists(TimeRange range, int count) {
        CompletableFuture<String> tokenFuture = CompletableFuture.supplyAsync(SpotifyAuth::returnAccessTokenAsync);
        return tokenFuture.thenApply(accessToken -> {
            if (accessToken == null) return null;

            String reqURL = topArtistsURL
                    .concat("?time_range=" + range.getValue())
                    .concat("&limit" + count)
                    .concat("&offset=" + "0");

            Request request = new Request.Builder()
                    .url(reqURL)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            try (Response response = reqClient.newCall(request).execute()) {
                return parseArtists(response.body().charStream());
            } catch (IOException e) {
                Log.e(TAG, "Error while getting top artists");
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<List<TrackData>> getTopTracks(TimeRange range, int count) {
        CompletableFuture<String> tokenFuture = CompletableFuture.supplyAsync(SpotifyAuth::returnAccessTokenAsync);
        return tokenFuture.thenApply(accessToken -> {
            String query = topTracksURL
                .concat("?time_range=" + range.getValue())
                .concat("&limit" + count)
                .concat("&offset=" + "0");

            Request request = new Request.Builder()
                .url(query)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

            try (Response response = reqClient.newCall(request).execute()) {
                return parseTracksAndRun(response.body().charStream());
            } catch (IOException e) {
                Log.e(TAG, "Error while getting top tracks");
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<String> getUserData() {
        CompletableFuture<String> tokenFuture = CompletableFuture.supplyAsync(SpotifyAuth::returnAccessTokenAsync);
        return tokenFuture.thenApply(accessToken -> {
            Request request = new Request.Builder()
                    .url(selfURI)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            try (Response response = reqClient.newCall(request).execute()) {
                JsonObject info = JsonParser.parseReader(response.body().charStream())
                        .getAsJsonObject();

                return info.get("display_name").getAsString();
            } catch (IOException e) {
                Log.e(TAG, "Error while getting user data");
                throw new RuntimeException(e);
            }
        });
    }

    public static void fetchImageFromURL(FetchImageAction action, URL url) {
        Request request = new Request.Builder()
                    .url(url)
                    .build();

        reqClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("SpotifyAPI", "Failed to fetch image from URL");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream imageStream = response.body().byteStream();
                action.performAction(BitmapFactory.decodeStream(imageStream));
            }
        });
    }

    private static List<ArtistData> parseArtists(Reader jsonReader) {
        List<ArtistData> topArtists = new ArrayList<>();
        JsonObject artistBody = JsonParser.parseReader(jsonReader)
                .getAsJsonObject();

        JsonArray artistJsons = artistBody.get("items").getAsJsonArray();
        for (int i = 0; i < artistJsons.size(); i++) {
            topArtists.add(new ArtistData(artistJsons.get(i).getAsJsonObject()));
        }
        return topArtists;
    }

    private static List<TrackData> parseTracksAndRun(Reader jsonReader) {
        List<TrackData> topTracks = new ArrayList<>();

        JsonObject body = JsonParser.parseReader(jsonReader).getAsJsonObject();

        JsonArray items = body.get("items").getAsJsonArray();

        for (int i = 0; i < items.size(); i++) {
            topTracks.add(new TrackData(items.get(i).getAsJsonObject()));
        }
        return topTracks;
    }
}
