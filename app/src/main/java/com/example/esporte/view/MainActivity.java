package com.example.esporte.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esporte.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Kaique");
        user.put("state", "MS");
        user.put("country", "Brasil");

        // Adicione o usuário à coleção "Usuarios"
        db.collection("Usuarios")
                .add(user)
                .addOnSuccessListener(documentReference -> System.out.println("Usuário adicionado com ID: " + documentReference.getId()))
                .addOnFailureListener(e -> System.err.println("Erro ao adicionar usuário: " + e));
    }
}