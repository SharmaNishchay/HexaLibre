package com.darkhex.hexalibre;

public class BookService {
    private String title;
    private String status;
    private String date;

    public BookService(String title, String status, String date) {
        this.title = title;
        this.status = status;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}

