package com.example.esporte.config;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.esporte.model.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static FirebaseAuth mAuth;
    private static DatabaseReference reference;
    private static StorageReference storage;

    private static Usuarios usuarioLogado = new Usuarios();
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
   public static FirebaseUser getUsuarioLogado(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getAutenticacao();
        return usuario.getCurrentUser();
   }
   public static String IDUsuarioLogado(){
        FirebaseAuth auth = ConfiguracaoFirebase.getAutenticacao();
        String email = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificar(email);
        return idUsuario;
   }

}

