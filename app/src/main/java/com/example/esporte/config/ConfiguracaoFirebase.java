package com.example.esporte.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static FirebaseAuth mAuth;
    private static DatabaseReference reference;
    private static StorageReference storage;

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
    public static StorageReference getFirestore(){
        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}

