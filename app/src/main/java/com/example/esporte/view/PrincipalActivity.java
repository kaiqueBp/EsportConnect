package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class PrincipalActivity extends AppCompatActivity {
    private ImageView img;
    private TextView txt;
    Usuarios usuarios;
    private FirebaseAuth auth;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        usuarios = new Usuarios();
        img = findViewById(R.id.idimage);
        //txt = findViewById(R.id.idnome);
        DatabaseReference referencia = ConfiguracaoFirebase.getFirebase();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String e = user.getEmail();

        referencia.child("Usuarios").child(Base64Custom.codificar(e)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot = task.getResult();
                    String nome = snapshot.child("nome").getValue(String.class);
                    url = snapshot.child("foto").getValue(String.class);

                    Glide.with(PrincipalActivity.this)
                    .load(url)
                    .into(img);
                }
            }
        });
    }
    public void sair(View view){
        auth = ConfiguracaoFirebase.getAutenticacao();
        auth.signOut();
        Intent intent = new Intent(PrincipalActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}