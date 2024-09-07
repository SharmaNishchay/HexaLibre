package com.darkhex.hexalibre;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class LibraryApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner setup for college selection
        Spinner collegeSpinner = findViewById(R.id.spinner_colleges);

        // List of colleges (can be dynamically generated)
        List<String> collegeList = Arrays.asList(
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
    }
}
