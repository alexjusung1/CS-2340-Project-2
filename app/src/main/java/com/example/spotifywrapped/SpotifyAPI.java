package com.example.spotifywrapped;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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

public class SpotifyAPI {
    private static final OkHttpClient reqClient;
    static {
        // AuthClient setup
        reqClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    private static final String topItemURL = "https://api.spotify.com/v1/me/top/artists";

    public static void getTopArtists(TopArtistsAction action, TimeRange range, int count) {
        new Thread(() -> SpotifyAuth.useAccessToken(accessToken -> {
            String offset = "0";

            String reqURL = topItemURL
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
                    Log.w("API Error", "Top Artists Call Failed");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String body = response.body().string();
                    Log.e("SpotifyAPI", body);
                    parseArtistsAndRun(new StringReader(body), action);
                    response.body().close();
                }
            });
        })).start();
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

    public static List<TrackData> getTopTracks(TimeRange range, int count) {
        return new ArrayList<>(count);
    }
}
