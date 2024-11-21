package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.esporte.R;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.FirebaseListenerService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class Teste extends AppCompatActivity {
    public static BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);
        navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navPerfil, R.id.navHome, R.id.navConversa)
                .build();
        NavigationUI.setupWithNavController(navView, navController);

        FirebaseAuth auth = ConfiguracaoFirebase.getAutenticacao();
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            Intent serviceIntent = new Intent(this, FirebaseListenerService.class);
            startService(serviceIntent);
        }


        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navHome) {

                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

                        getSupportFragmentManager().popBackStack();
                    } else {

                        navController.navigate(R.id.navHome);
                    }
                    return true;
                } else {

                    navController.navigate(id);
                    return true;
                }
            }
        });
    }
}