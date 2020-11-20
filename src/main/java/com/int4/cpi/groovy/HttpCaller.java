package com.int4.cpi.groovy;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class HttpCaller {
    private static OkHttpClient client;
    private String username;
    private String pass;

    public HttpCaller(String username, String pass) {
        this.username = username;
        this.pass = pass;
    }

    public Authenticator authenticator() {
        return new Authenticator() {
            @NotNull
            @Override
            public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
                return response.request().newBuilder()
                        .header("Authorization", Credentials.basic(username, pass))
                        .build();
            }
        };
    }

    public CookieJar cookieJar() {
        return new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };
    }

    public InputStream call(String url) {
        try {
            return client()
                    .newCall(new Request.Builder().url(url).build())
                    .execute()
                    .body()
                    .byteStream();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private OkHttpClient client() {
        if (client != null) {
            client = new OkHttpClient().newBuilder()
                    .cookieJar(cookieJar())
                    .authenticator(authenticator())
                    .build();
        }
        return client;
    }
}
