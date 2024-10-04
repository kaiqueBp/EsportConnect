package com.example.esporte.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.MenssagensAdapter;
import com.example.esporte.model.Conversa;
import com.example.esporte.model.Mensagem;
import com.example.esporte.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends BaseBotton {
    private Toolbar toolbar;
    private Usuarios pessoa;
    private ImageView foto;
    private TextView nome, mensagem;
    private String idRemetente, idDestinatario;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private MenssagensAdapter adapter;
    private List<Mensagem> menssagens = new ArrayList<>();
    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference mensagemRef;
    private ChildEventListener childEventListener;
    private ImageView imagemCamera;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private Mensagem ultimaMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        foto = findViewById(R.id.idFt);
        nome = findViewById(R.id.idNm);
        mensagem = findViewById(R.id.editMenssagem);
        imagemCamera = findViewById(R.id.imgCamera);
        recyclerView = findViewById(R.id.recyclerMenssagem);
        auth = ConfiguracaoFirebase.getAutenticacao();
        idRemetente = Base64Custom.codificar(auth.getCurrentUser().getEmail());
        toolbar = findViewById(R.id.idToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new MenssagensAdapter(menssagens,getApplicationContext());

          RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);



        Intent intent = getIntent();
        pessoa = (Usuarios) intent.getSerializableExtra("usuarioCLicado");
        getSupportActionBar().setTitle("");
        nome.setText(pessoa.getNome());
        Glide.with(this).load(pessoa.getFoto()).into(foto);
        idDestinatario = pessoa.getIdUsuario();
        database = ConfiguracaoFirebase.getFirebase();
        storage = ConfiguracaoFirebase.getFirestore();
        mensagemRef = database.child("Mensagens")
                .child(idRemetente)
                .child(idDestinatario);


        imagemCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 100);
                }

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                }

                // Abrir um diálogo para selecionar Câmera ou Galeria
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Escolher Imagem")
                        .setItems(new CharSequence[]{"Tirar Foto", "Escolher da Galeria"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: // Câmera
                                        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (intentCamera.resolveActivity(getPackageManager()) != null) {
                                            startActivityForResult(intentCamera, SELECAO_CAMERA);
                                        }
                                        break;
                                    case 1: // Galeria
                                        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intentGaleria.setType("image/*");
                                        startActivityForResult(intentGaleria, SELECAO_GALERIA);
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {
                if (requestCode == SELECAO_CAMERA) {
                    // Imagem capturada pela câmera
                    imagem = (Bitmap) data.getExtras().get("data");
                    imagemCamera.setImageBitmap(imagem);

                } else if (requestCode == SELECAO_GALERIA) {
                    // Imagem selecionada da galeria
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        // Converte o Uri da galeria em Bitmap
                        imagem = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        imagemCamera.setImageBitmap(imagem);
                    }
                }

                if(imagem != null){
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,100,byteArray);
                    byte[] imagemByte = byteArray.toByteArray();

                    String nomeImagem = UUID.randomUUID().toString();
                    final StorageReference imagemRef = storage.child("imagens")
                            .child("fotos")
                            .child(idRemetente)
                            .child(nomeImagem);
                    UploadTask uploadTask = imagemRef.putBytes(imagemByte);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro","Erro ao fazer upload da imagem");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = task.getResult().toString();

                                    Mensagem msg = new Mensagem();
                                    msg.setIdUsuario(idRemetente);
                                    msg.setMensagem("imagem.jpeg");
                                    msg.setImagem(url);

                                    Salvar(idRemetente,idDestinatario,msg);
                                    Salvar(idDestinatario,idRemetente,msg);
                                }
                            });
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void enviarMensagem(View view){
        if(!mensagem.getText().toString().isEmpty()){
            Mensagem msg = new Mensagem();
            msg.setIdUsuario(idRemetente);
            msg.setMensagem(mensagem.getText().toString());

            Salvar(idRemetente, idDestinatario, msg);
            Salvar(idDestinatario, idRemetente, msg);
            SalvarConversa(msg);
        }else {
            //mensagem.setError("Digite uma mensagem");
        }
    }
    private void SalvarConversa(Mensagem mensagem){
        Conversa conversaRem = new Conversa();
        conversaRem.setIdRemetente(idRemetente);
        conversaRem.setIdDestinatario(idDestinatario);
        conversaRem.setUltimaMensagem(mensagem.getMensagem());
        conversaRem.setUsuarioExibicao(pessoa);
        conversaRem.Salvar();

    }
    private void Salvar(String idRemetente, String idDestinatario, Mensagem m){
        DatabaseReference database = ConfiguracaoFirebase.getFirebase();;
        DatabaseReference mensagemRef = database.child("Mensagens");
        mensagemRef.child(idRemetente).child(idDestinatario).push().setValue(m);
        mensagem.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecuperarMenssagem();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagemRef.removeEventListener(childEventListener);
    }

    private void RecuperarMenssagem(){
        childEventListener = mensagemRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem msg = snapshot.getValue(Mensagem.class);
                menssagens.add(msg);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}