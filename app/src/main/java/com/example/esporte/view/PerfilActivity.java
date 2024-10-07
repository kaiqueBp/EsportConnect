package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.esporte.model.Endereco;
import com.example.esporte.model.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PerfilActivity extends BaseBotton {
    private ImageView imageView;
    private ListView listaEsportes;
    private TextView nome,email,sexo,cidade;
    private FirebaseAuth auth;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios");
    private Usuarios usuario = new Usuarios();
    private Endereco endereco;
    private Button editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        imageView = findViewById(R.id.idFt);
        listaEsportes = findViewById(R.id.pEsportes);
        nome = findViewById(R.id.pNome);
        email = findViewById(R.id.pEmail);
        sexo = findViewById(R.id.pSexo);
        cidade = findViewById(R.id.pCidade);
        editar = findViewById(R.id.btEdit);

        auth = ConfiguracaoFirebase.getAutenticacao();
        email.setText(auth.getCurrentUser().getEmail());

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, EditarActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });

        carregarPerfil(new PerfilActivity.Callback() {
            @Override
            public void onDataLoaded() {

                nome.setText(usuario.getNome());;
                sexo.setText(usuario.getSexo());
                Glide.with(PerfilActivity.this).load(usuario.getFoto()).into(imageView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_list_item_1, usuario.getEsportes());
                listaEsportes.setAdapter(adapter);
                //cidade.setText(usuario.getEndereco().getLocalidade() + " - " + usuario.getEndereco().getUf());
            }

            @Override
            public void onError() {
                // Tratar erro
            }
        });
    }
    private void carregarPerfil(final Callback callback){
        DatabaseReference usuarioRef = ref.child(Base64Custom.codificar(auth.getCurrentUser().getEmail()));
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //usuario = new Usuarios();
                    endereco = new Endereco();
                    //  usuario = snapshot.getValue(Usuario.class);
                    usuario.setNome(snapshot.child("nome").getValue(String.class));
                    usuario.setFoto(snapshot.child("foto").getValue(String.class));
                    usuario.setSexo(snapshot.child("sexo").getValue(String.class));
                    usuario.setEmail(snapshot.child("email").getValue(String.class));
                    ArrayList<String> esportes = new ArrayList<>();
                    for (DataSnapshot esporteSnapshot : snapshot.child("esportes").getChildren()) {
                        String esporte = esporteSnapshot.getValue(String.class);
                        esportes.add(esporte);
                    }
                    usuario.setEsportes(esportes);
                    endereco.setUf(snapshot.child("endereco").child("uf").getValue(String.class));
                    endereco.setLocalidade(snapshot.child("endereco").child("localidade").getValue(String.class));
                    usuario.setEndereco(endereco);
                    callback.onDataLoaded();
                } else {
                    Toast.makeText(PerfilActivity.this, "Perfil não encontrado", Toast.LENGTH_SHORT).show();
                    callback.onError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PerfilActivity.this, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show();
                callback.onError();
            }
        });
    }
    public interface Callback {
        void onDataLoaded();
        void onError();
    }

    @Override
    protected void onStart() {
        super.onStart();
        carregarPerfil(new PerfilActivity.Callback() {
            @Override
            public void onDataLoaded() {

                nome.setText(usuario.getNome());;
                sexo.setText(usuario.getSexo());
                Glide.with(PerfilActivity.this).load(usuario.getFoto()).into(imageView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_list_item_1, usuario.getEsportes());
                listaEsportes.setAdapter(adapter);
                cidade.setText(usuario.getEndereco().getLocalidade() + " - " + usuario.getEndereco().getUf());
            }

            @Override
            public void onError() {
                // Tratar erro
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        carregarPerfil(new PerfilActivity.Callback() {
            @Override
            public void onDataLoaded() {

                nome.setText(usuario.getNome());;
                sexo.setText(usuario.getSexo());
                Glide.with(PerfilActivity.this).load(usuario.getFoto()).into(imageView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_list_item_1, usuario.getEsportes());
                listaEsportes.setAdapter(adapter);
                cidade.setText(usuario.getEndereco().getLocalidade() + " - " + usuario.getEndereco().getUf());
            }

            @Override
            public void onError() {
                // Tratar erro
            }
        });
    }
    public void Sair(View view){
        auth.signOut();
        Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}