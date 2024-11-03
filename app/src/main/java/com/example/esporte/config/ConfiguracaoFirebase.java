package com.example.esporte.config;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.esporte.model.Endereco;
import com.example.esporte.model.Usuarios;
import com.example.esporte.view.PerfilActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
   public static Usuarios UsuarioLogado(){
        FirebaseUser user = getUsuarioLogado();
        Usuarios usuario = new Usuarios();
        usuario.setEmail(user.getEmail());
        usuario.setIdUsuario(ConfiguracaoFirebase.IDUsuarioLogado());
        usuario.setNome(user.getDisplayName());
        if(user.getPhotoUrl() == null){
            usuario.setFoto("");
        }else usuario.setFoto(user.getPhotoUrl().toString());

        return usuario;
   }
    public static void carregarPerfil(final PerfilActivity.Callback callback){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios");
        FirebaseAuth auth = ConfiguracaoFirebase.getAutenticacao();
        DatabaseReference usuarioRef = ref.child(Base64Custom.codificar(auth.getCurrentUser().getEmail()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nome = (snapshot.child("nome").getValue(String.class));

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nome)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Perfil atualizado com sucesso
                                    Log.d("Firebase", "Perfil atualizado com sucesso.");
                                } else {
                                    // Tratamento de erro
                                    Log.e("Firebase", "Erro ao atualizar perfil: ", task.getException());
                                }
                            });
                    callback.onDataLoaded();
                } else {
                    callback.onError();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError();
            }
        });
    }
    public interface Callback {
        void onDataLoaded();
        void onError();
    }

}

