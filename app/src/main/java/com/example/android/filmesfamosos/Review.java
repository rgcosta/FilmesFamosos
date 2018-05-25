package com.example.android.filmesfamosos;

import com.google.gson.annotations.Expose;

public class Review {

    @Expose
    private String id;
    @Expose
    private String author;
    @Expose
    private String content;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
