package com.tunm.sleeptimer.data;

public class QuoteModel {
    private String content;
    private String author;

    public QuoteModel() {
    }

    public QuoteModel(String content, String author) {
        this.content = content;
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
