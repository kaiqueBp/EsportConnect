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
//        if (bottomNavigationView == null) {
//            Log.e("BaseBotton", "BottomNavigationView não foi inflada corretamente");
//        } else {
//            Log.d("BaseBotton", "BottomNavigationView foi inflada corretamente");
//        }
//
//
//        // Listener para o BottomNavigationView
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int itemId = item.getItemId();
//
//                if (itemId == R.id.navHome) {
//                    // Navegação para a PrincipalActivity
//                    startActivity(new Intent(getApplicationContext(), PrincipalActivity.class));
//                    overridePendingTransition(0, 0);  // Sem animação de transição
//                    return true;
//
//                } else if (itemId == R.id.navPerfil) {
//                    // Navegação para a PerfilActivity
//                    startActivity(new Intent(getApplicationContext(), PerfilActivity.class));
//                    overridePendingTransition(0, 0);  // Sem animação de transição
//                    return true;
//
//                } else if (itemId == R.id.navConversa) {
//                    // Navegação para a ListarConversasActivity
//                    startActivity(new Intent(getApplicationContext(), ListarConversasActivity.class));
//                    overridePendingTransition(0, 0);  // Sem animação de transição
//                    return true;
//
//                } else {
//                    return false;
//                }
//            }
//        });
   }
}
