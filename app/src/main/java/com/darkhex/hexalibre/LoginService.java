package com.darkhex.hexalibre;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class LoginService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner setup for college selection
        Spinner collegeSpinner = findViewById(R.id.spinner_colleges);

        // List of colleges (can be dynamically generated)
        List<String> collegeList = Arrays.asList(
                "Select a College",
                "College of Engineering, Pune",
                "Indian Institute of Technology, Bombay",
                "Delhi Technological University",
                "Birla Institute of Technology, Pilani",
                "Vellore Institute of Technology"
        );

        // Create an ArrayAdapter to populate the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, collegeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        collegeSpinner.setAdapter(adapter);
        Button toLoginPage = findViewById(R.id.button_login_redirect);

        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(id != 0) {
                    // Get the selected item
                    toLoginPage.setVisibility(View.VISIBLE);
                    toLoginPage.setEnabled(true);
                }
                else{
                    toLoginPage.setVisibility(View.GONE);
                    toLoginPage.setEnabled(false);
                }

                String selectedCollege = parent.getItemAtPosition(position).toString();
                // Handle the selected college here
                // For example, show a Toast message
                // Toast.makeText(LoginActivity.this, "Selected College: " + selectedCollege, Toast.LENGTH_SHORT).show();
                // Or handle the selection as needed for your app
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toLoginPage.setVisibility(View.GONE);
                toLoginPage.setEnabled(false);
            }
        });

    }

}
