package com.darkhex.hexalibre;

public interface BookSearchCallback {
    void onBookFound(String uid);
    void onBookNotFound();
}
