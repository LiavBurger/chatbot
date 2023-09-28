package com.handson.chatbot.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
public class ChuckNorrisJokesService {

    OkHttpClient client;
    ObjectMapper om;

    public ChuckNorrisJokesService(@Autowired ObjectMapper om) {
        client = new OkHttpClient().newBuilder().build();
        this.om = om;
    }

    public String getChuckNorrisJoke(String keyword) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.chucknorris.io/jokes/search?query=" + keyword)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        ChuckNorrisJokeResponse res = om.readValue(response.body().string(), ChuckNorrisJokeResponse.class);
        if (res.getResult() != null && !res.getResult().isEmpty()) {
            int randomIndex = new Random().nextInt(res.result.size());
            return res.result.get(randomIndex).getJoke();
        } else {
            return "No jokes found for the provided keyword!";
        }
    }

    private static class ChuckNorrisJokeResponse {
        List<ChuckNorrisJokeObject> result;

        public List<ChuckNorrisJokeObject> getResult() {
            return result;
        }
    }

    private static class ChuckNorrisJokeObject {
        @JsonProperty("value")
        String joke;

        public String getJoke() {
            return joke;
        }
    }
}
