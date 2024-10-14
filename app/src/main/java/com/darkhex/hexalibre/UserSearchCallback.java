package com.darkhex.hexalibre;

public interface UserSearchCallback {
    void onUserFound(String uid);
    void onUserNotFound();
}
