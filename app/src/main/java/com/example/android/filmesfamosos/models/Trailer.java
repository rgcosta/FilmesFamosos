package com.example.android.filmesfamosos.models;

import com.google.gson.annotations.Expose;

public class Trailer {

    private static final String YOUTUBE_BASE_URL_IMG = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_COVER_IMG = "/0.jpg";

    @Expose
    private String id;
    @Expose
    private String key;
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYoutubeCoverImgFullUrl(){
        return YOUTUBE_BASE_URL_IMG + getKey() + YOUTUBE_COVER_IMG;
    }
}
