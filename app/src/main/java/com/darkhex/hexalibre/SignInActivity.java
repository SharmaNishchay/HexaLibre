package com.darkhex.hexalibre;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignInActivity extends AppCompatActivity {

    private EditText editTextRollNo, editTextBranch, editTextSemester;
    private TextView Name , Email;
    private Button buttonSubmit;
    private Intent intent;
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        editTextRollNo = findViewById(R.id.editTextRollNo);
        editTextBranch = findViewById(R.id.editTextBranch);
        editTextSemester = findViewById(R.id.editTextSemester);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        Name=findViewById(R.id.student_name);
        Email=findViewById(R.id.st_email);
        intent = getIntent();
        Name.append(intent.getStringExtra("Name"));
        Email.append(intent.getStringExtra("Email"));

        buttonSubmit.setOnClickListener(v -> {
            String rollNo = editTextRollNo.getText().toString().trim();
            String branch = editTextBranch.getText().toString().trim();
            String semester = editTextSemester.getText().toString().trim();
            String uid=intent.getStringExtra("c_id")+"@"+rollNo;

            if (rollNo.isEmpty() || branch.isEmpty() || semester.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Handle form submission
                user(rollNo,branch,semester,uid);

            }
        });
    }
    public void user(String rollNo,String branch, String semester,String uid){
        User_search userSearch = new User_search();
        userSearch.searchUserByEmail(rollNo,"rollNo", new UserSearchCallback() {
            @Override
            public void onUserFound(String uid) {
                // Do something with the UID
                Toast.makeText(SignInActivity.this, "Roll No. Already exists", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserNotFound() {
                // Handle case where user is not found
                AddUser(rollNo,branch,semester,uid);
            }
        });
    }

    public void AddUser(String rollNo,String branch, String semester,String uid){
        db=FirebaseDatabase.getInstance();
        reference= db.getReference("Users");
        String name=intent.getStringExtra("Name");
        String Email=intent.getStringExtra("Email");
        UserProfile users=new UserProfile(name,Email,rollNo,branch,semester,uid);
        assert name != null;
        reference.child(uid).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("LoginActivity","User Added");
                Intent intent1=new Intent(SignInActivity.this,MainActivity.class);
                startActivity(intent1);
            }
        });
    }
}
