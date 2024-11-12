package com.darkhex.hexalibre;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class BookSearch {

    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    public BookSearch() {
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Books");
    }

    public void searchUserByEmail(String s, BookSearchCallback callback) {
        // Reference to the path "Books/s" directly
        DatabaseReference bookRef = usersRef.child(s);

        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Path with name `s` exists, retrieve the Name or any other required field
                    String bookName = Objects.toString(dataSnapshot.child("Name").getValue(), "Unknown");
                    Log.d("FirebaseSearch", "Book found with name: " + bookName);
                    callback.onBookFound(bookName);
                } else {
                    Log.d("FirebaseSearch", "No book found with the specified path.");
                    callback.onBookNotFound(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseSearch", "Error: " + databaseError.getMessage());
                callback.onBookNotFound("Error");
            }
        });
    }

}
