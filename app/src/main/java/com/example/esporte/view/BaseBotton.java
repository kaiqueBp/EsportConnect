package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esporte.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseBotton extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_teste);

        // Configure a BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.idBotton);
        bottomNavigationView.setVisibility(View.VISIBLE);

   }
}
