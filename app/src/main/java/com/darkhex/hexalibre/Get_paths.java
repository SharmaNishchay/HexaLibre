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
import java.util.Objects;

public class Get_paths {
    private boolean data=false;
    FirebaseDatabase db;
    DatabaseReference reference;
    public void getPath(String path, PathFetchCallback callback){
        db=FirebaseDatabase.getInstance();
        if(!("Colleges".equals(path)))
            reference=db.getReference("Colleges").child(path).child("Books");
        else
            reference=db.getReference(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String>paths=new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    if("Colleges".equals(path))
                        paths.add(Objects.requireNonNull(childSnapshot.child("Name").getValue()).toString());
                    else
                        paths.add(Objects.requireNonNull(childSnapshot.getKey()));
                }
                callback.onPathFetched(paths);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
