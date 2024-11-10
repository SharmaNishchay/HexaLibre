package com.darkhex.hexalibre;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Get_paths {
    private boolean data=false;
    FirebaseDatabase db;
    DatabaseReference reference;
    public void getPath(CollegeFetchCallback callback){
        db=FirebaseDatabase.getInstance();
        reference=db.getReference("Colleges");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String>paths=new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    paths.add(childSnapshot.getKey());
                }
                callback.onCollegeFetched(paths);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
