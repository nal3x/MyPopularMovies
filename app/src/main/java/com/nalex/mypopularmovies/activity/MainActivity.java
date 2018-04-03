package com.nalex.mypopularmovies.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.nalex.mypopularmovies.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String myApiKey = getString(R.string.THEMOVIEDB_API_KEY);
        Toast.makeText(getApplicationContext(), myApiKey, Toast.LENGTH_LONG).show();
    }
}