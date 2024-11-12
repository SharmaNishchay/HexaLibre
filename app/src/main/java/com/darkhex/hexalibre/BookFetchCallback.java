package com.darkhex.hexalibre;

import java.util.List;


public interface BookFetchCallback {
    void onBooksFetched(List<Book>books);
}

