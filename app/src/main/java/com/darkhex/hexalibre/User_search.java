package com.darkhex.hexalibre;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class User_search {

    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    public User_search() {
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
    }

    public void searchUserByEmail(String s,String target, UserSearchCallback callback) {
        // Query to search for users by email
        Query emailQuery = usersRef.orderByChild(target).equalTo(s);
        emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate through the results and get the user details
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String uid = Objects.requireNonNull(userSnapshot.child("uid").getValue()).toString();
                        Log.d("FirebaseSearch", "UID: " + uid);
                        callback.onUserFound(uid);
                        return; // We found the user, exit the loop and callback
                    }
                } else {
                    Log.d("FirebaseSearch", "No user found with the "+target+": " + s);
                    callback.onUserNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseSearch", "Error: " + databaseError.getMessage());
                callback.onUserNotFound();
            }
        });
    }
}
