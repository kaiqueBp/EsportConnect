package com.example.esporte.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.esporte.R;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.GrupoSelecionadoAdapter;
import com.example.esporte.model.Conversa;
import com.example.esporte.model.Grupo;
import com.example.esporte.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CadastroGrupoActivity extends AppCompatActivity {
    private ArrayList<Usuarios> usuarios = new ArrayList<>();
    private TextView totalParticipantes;
    private ImageView imgGrupo;
    private EditText nomeGrupo;
    private RecyclerView recyclerView;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private static final int REQUEST_IMAGE_SELECT = 1;
    private StorageReference storage;
    private Grupo grupo;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo2);
        totalParticipantes = findViewById(R.id.idTotalMembros);
        imgGrupo = findViewById(R.id.idImgGrupo);
        nomeGrupo = findViewById(R.id.idNomeGrupo);
        recyclerView = findViewById(R.id.idMembros);
        fab = findViewById(R.id.idFloat);
        storage = ConfiguracaoFirebase.getFirestore();
        Intent intent = getIntent();
        usuarios = (ArrayList<Usuarios>) intent.getSerializableExtra("usuariosSelecionados");
        totalParticipantes.setText("Participantes: " + usuarios.size());
        grupo = new Grupo();
        imgGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_SELECT);

            }
        });
        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(usuarios, this, new GrupoSelecionadoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(grupoSelecionadoAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = nomeGrupo.getText().toString();
                usuarios.add(ConfiguracaoFirebase.UsuarioLogado());
                grupo.setMembros(usuarios);
                grupo.setNome(nome);
                grupo.Salvar();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK && data != null){
            Bitmap imagem = null;
            try {
                Uri imagemSelecionada = data.getData();
                imagem = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);

                if(imagem != null){
                    imgGrupo.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[] dadosImagem = baos.toByteArray();

                   final StorageReference imagemRef = storage.child("imagens").child("grupos").child(grupo.getId()+ ".jpeg");
                    UploadTask up = imagemRef.putBytes(dadosImagem);
                    up.addOnFailureListener(CadastroGrupoActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroGrupoActivity.this,"Erro ao salvar a Imagem",Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(CadastroGrupoActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = task.getResult().toString();
                                    grupo.setFoto(url);
                                    Toast.makeText(CadastroGrupoActivity.this,url.toString(),Toast.LENGTH_LONG).show();

                                }
                            });
                            Toast.makeText(CadastroGrupoActivity.this,"Imagem Salva com Sucesso",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}