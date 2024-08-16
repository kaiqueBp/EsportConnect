package com.example.esporte.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static FirebaseAuth mAuth;
    private static DatabaseReference reference;
    public static DatabaseReference getFirebase(){
        if(reference == null){
          reference = FirebaseDatabase.getInstance().getReference();
        }
        return reference;
    }
    public static FirebaseAuth getAutenticacao(){
        if(mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }
}
