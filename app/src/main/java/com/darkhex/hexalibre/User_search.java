package com.darkhex.hexalibre;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class User_search {


    public void searchUserByEmail(String college, String s,String target, UserSearchCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef;
        String data;
        if(target.equals("email")||target.equals("rollNo")) {
            usersRef = database.getReference("Colleges").child("College1").child("Users");
            data="uid";
        }
        else {
            usersRef = database.getReference("Colleges");
            data="c_id";
        }
        // Query to search for users by email
        Query emailQuery = usersRef.orderByChild(target).equalTo(s);
        emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate through the results and get the user details
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String uid = Objects.requireNonNull(userSnapshot.child(data).getValue()).toString();
                        callback.onUserFound(uid);
                        return; // We found the user, exit the loop and callback
                    }
                } else {
                    callback.onUserNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseSearch", "Error: " + databaseError.getMessage());
                callback.onUserNotFound();
            }
        });
    }
}
