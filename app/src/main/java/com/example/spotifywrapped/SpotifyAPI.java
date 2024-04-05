package com.example.spotifywrapped;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private static String topItemURL = "https://api.spotify.com/v1/me/top";
    private static List<ArtistData> topArtists;

    public static List<ArtistData> getTopArtists(TimeRange range, int count, String token) {
        String offset = "0";
        topArtists = new ArrayList<>();

        String reqURL = topItemURL
                .concat("?time_range=" + range.getValue())
                .concat("&limit" + count)
                .concat("&offset=" + offset);

        Request request = new Request.Builder()
                .url(reqURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        reqClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.w("API Error", "Top Artists Call Failed");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                parseArtists(response.body().charStream());
                response.body().close();
            }
        });

        return topArtists;
    }

    private static void parseArtists(Reader jsonReader) {
        JsonObject artistBody = JsonParser.parseReader(jsonReader)
                .getAsJsonObject();

        JsonArray artistJsons = artistBody.get("items").getAsJsonArray();
        for (int i = 0; i < artistJsons.size(); i++) {
            topArtists.add(new ArtistData(artistJsons.get(i).getAsJsonObject()));
        }

    }
}
