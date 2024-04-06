package com.example.spotifywrapped;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@FunctionalInterface
interface TopArtistsAction {
    void performAction(List<ArtistData> topArtists);
}

@FunctionalInterface
interface FetchImageAction {
    void performAction(Bitmap image);
}

@FunctionalInterface
interface TopTracksAction {
    void performAction(List<TrackData> topTracks);
}

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

    public static void getTopArtists(TopArtistsAction action, TimeRange range, int count) {
        SpotifyAuth.useAccessToken(accessToken -> {
            String offset = "0";

            String reqURL = topArtistsURL
                    .concat("?time_range=" + range.getValue())
                    .concat("&limit" + count)
                    .concat("&offset=" + offset);

            Request request = new Request.Builder()
                    .url(reqURL)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            reqClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("SpotifyAPI", "Top Artists Call Failed");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String body = response.body().string();
                    Log.d("SpotifyAPI", body);
                    parseArtistsAndRun(new StringReader(body), action);
                    response.body().close();
                }
            });
        });
    }

    private static void parseArtistsAndRun(Reader jsonReader, TopArtistsAction action) {
        List<ArtistData> topArtists = new ArrayList<>();
        JsonObject artistBody = JsonParser.parseReader(jsonReader)
                .getAsJsonObject();

        JsonArray artistJsons = artistBody.get("items").getAsJsonArray();
        for (int i = 0; i < artistJsons.size(); i++) {
            topArtists.add(new ArtistData(artistJsons.get(i).getAsJsonObject()));
        }
        action.performAction(topArtists);
    }

    public static void getTopTracks(TopTracksAction action, TimeRange range, int count) {
        SpotifyAuth.useAccessToken(accessToken -> {
            String query = topTracksURL
                .concat("?time_range=" + range.getValue())
                .concat("&limit" + count)
                .concat("&offset=" + "0");
            
            Request request = new Request.Builder()
                .url(query)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
            
            
            reqClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.w("API Error", "Top Tracks Call failed");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String body = response.body().string();
                    // Log.e("SpotifyAPI", body) // in case of error
                    parseTracksAndRun(new StringReader(body), action);
                    response.body().close();
                }
            });
        });
    }

    public static void parseTracksAndRun(Reader jsonReader, TopTracksAction action) {
        List<TrackData> topTracks = new ArrayList<>();

        JsonObject body = JsonParser.parseReader(jsonReader).getAsJsonObject();

        JsonArray items = body.get("items").getAsJsonArray();

        for (int i = 0; i < items.size(); i++) {
            topTracks.add(new TrackData(items.get(i).getAsJsonObject()));
        }

        action.performAction(topTracks);
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
}
