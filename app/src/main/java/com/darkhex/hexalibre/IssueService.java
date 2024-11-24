package com.darkhex.hexalibre;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IssueService {
    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    public void Issue(String c_id,String uid,History history){
        getHistoryValues(uid, c_id, new HistoryCallback() {
            @Override
            public void onHistoryFetched(List<History> histories) {
                String h_no="H"+(histories.size()+1);
                DatabaseReference reference= db.getReference("Colleges").child("College"+c_id)
                        .child("Users").child(uid).child("History");
                reference.child(h_no).setValue(history).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("History","Database updated uid"+uid+" c_id"+c_id);
                    }
                });
            }

            @Override
            public void onHistory_notFetched() {

            }
        });

    }

    public void getHistoryValues(String uid, String c_id,HistoryCallback callback) {
        Log.d("NULL","Uid"+uid);
        DatabaseReference reference = db.getReference("Colleges").child("College" + c_id)
                .child("Users").child(uid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if(snapshot.child("History").exists()) {
                        List<History> historyList = new ArrayList<>();

                        for (DataSnapshot childSnapshot : snapshot.child("History").getChildren()) {
                            History history = childSnapshot.getValue(History.class);
                            if (history != null) {
                                historyList.add(history);
                            }
                        }
                        callback.onHistoryFetched(historyList);
                    }else {
                        callback.onHistoryFetched(new ArrayList<>()); // Pass an empty list
                    }
                } else {
                    callback.onHistoryFetched(new ArrayList<>()); // Pass an empty list
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error: " + error.getMessage());
                callback.onHistory_notFetched();
            }
        });
    }
}
