package com.darkhex.hexalibre;

import java.util.List;

public interface oneBookCallback {
    void onBooksFetched(String title,String url);
    void onBook_notFetched();
}
