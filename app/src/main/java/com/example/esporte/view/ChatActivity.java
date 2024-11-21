package com.example.esporte.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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
import com.example.esporte.model.Endereco;
import com.example.esporte.model.Grupo;
import com.example.esporte.model.Mensagem;
import com.example.esporte.model.Usuarios;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends BaseBotton {
    private Toolbar toolbar;
    private Usuarios pessoa,usuarioLogado = new Usuarios();
    private Usuarios usuarioAux;
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
    private ImageView imagemCamera, imgLocalizar;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private Mensagem ultimaMensagem;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private Double longi;
    private Double lati;
    private Grupo grupo;

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
        imgLocalizar = findViewById(R.id.imgLoc);
        recyclerView = findViewById(R.id.recyclerMenssagem);
        auth = ConfiguracaoFirebase.getAutenticacao();
        idRemetente = Base64Custom.codificar(auth.getCurrentUser().getEmail());
        toolbar = findViewById(R.id.idToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new MenssagensAdapter(menssagens,this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        imgLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PegarLoc(new Callback() {
                    @Override
                    public void onDataLoaded() {
                        Mensagem msg = new Mensagem();
                        msg.setIdUsuario(idRemetente);
                        if(lati != null && longi != null){
                            msg.setLatitude(lati);
                            msg.setLongitude(longi);

                            if(pessoa != null){
                                msg.setIdUsuario(idRemetente);
                                msg.setMensagem("Localização");

                                Salvar(idRemetente, idDestinatario, msg);
                                Salvar(idDestinatario, idRemetente, msg);
                                SalvarConversa(idRemetente,idDestinatario,msg,pessoa,"true");
                                SalvarConversa(idDestinatario,idRemetente,msg,usuarioAux,"false");
                            }else {
                                for (Usuarios membros : grupo.getMembros()) {
                                    String idRem = Base64Custom.codificar(membros.getEmail());
                                    String idDes = ConfiguracaoFirebase.IDUsuarioLogado();

                                    msg.setIdUsuario(idDes);
                                    msg.setMensagem(mensagem.getText().toString());
                                    msg.setNome(usuarioLogado.getNome());
                                    Salvar(idRem, idDestinatario, msg);
                                    if(idRem.equals(idDes)){
                                        SalvarConversa(idRem, idDestinatario, msg, null,"true");
                                    }else{
                                        SalvarConversa(idRem, idDestinatario, msg, null,"false");
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey("grupoClicado")){
                grupo = (Grupo) extras.getSerializable("grupoClicado");
                getSupportActionBar().setTitle("");
                nome.setText(grupo.getNome());
                idDestinatario = grupo.getId();

                if(grupo.getFoto() != null)
                    Glide.with(this).load(grupo.getFoto()).into(foto);
                else{
                    foto.setImageResource(R.drawable.grupo_padrao);
                }
            }else{
                pessoa = (Usuarios) extras.getSerializable("usuarioCLicado");
                getSupportActionBar().setTitle("");
                nome.setText(pessoa.getNome());
                idDestinatario = pessoa.getIdUsuario();

                if(pessoa.getFoto() != null)
                    Glide.with(this).load(pessoa.getFoto()).into(foto);
                else
                    foto.setImageResource(R.drawable.usuario_padrao);
            }

        }


        if(grupo != null){
            nome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChatActivity.this, PerfilGrupoActivity.class);
                    intent.putExtra("perfilGrupo",grupo);
                    startActivity(intent);
                    finish();
                }
            });
        }

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
                    imagem = (Bitmap) data.getExtras().get("data");
                    //imagemCamera.setImageBitmap(imagem);

                } else if (requestCode == SELECAO_GALERIA) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        imagem = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                       // imagemCamera.setImageBitmap(imagem);
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

                                    if(pessoa != null){
                                        msg.setIdUsuario(idRemetente);
                                        msg.setMensagem(mensagem.getText().toString());

                                        Salvar(idRemetente, idDestinatario, msg);
                                        Salvar(idDestinatario, idRemetente, msg);

                                        SalvarConversa(idRemetente,idDestinatario,msg,pessoa,"true");
                                        SalvarConversa(idDestinatario,idRemetente,msg,usuarioAux,"false");
                                    }else {
                                        for (Usuarios membros : grupo.getMembros()) {
                                            String idRem = Base64Custom.codificar(membros.getEmail());
                                            String idDes = ConfiguracaoFirebase.IDUsuarioLogado();

                                            msg.setIdUsuario(idDes);
                                            msg.setMensagem(mensagem.getText().toString());
                                            msg.setNome(usuarioLogado.getNome());
                                            Salvar(idRem, idDestinatario, msg);
                                            if(idRem.equals(idDes)){
                                                SalvarConversa(idRem, idDestinatario, msg, null,"true");
                                            }else{
                                                SalvarConversa(idRem, idDestinatario, msg, null,"false");
                                            }
                                        }
                                    }
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
            if(pessoa != null){
                Mensagem msg = new Mensagem();
                msg.setIdUsuario(idRemetente);
                msg.setMensagem(mensagem.getText().toString());

                Salvar(idRemetente, idDestinatario, msg);
                Salvar(idDestinatario, idRemetente, msg);
                SalvarConversa(idRemetente,idDestinatario,msg,pessoa,"true");
                SalvarConversa(idDestinatario,idRemetente,msg,usuarioAux,"false");
            }else{
                for(Usuarios membros : grupo.getMembros()){
                    String idRem = Base64Custom.codificar(membros.getEmail());
                    String idDes = ConfiguracaoFirebase.IDUsuarioLogado();

                    Mensagem msg = new Mensagem();
                    msg.setIdUsuario(idDes);
                    msg.setMensagem(mensagem.getText().toString());
                    msg.setNome(usuarioLogado.getNome());
                    Salvar(idRem, idDestinatario, msg);
                    if(idRem.equals(idDes)){
                        SalvarConversa(idRem, idDestinatario, msg, null,"true");
                    }else{
                        SalvarConversa(idRem, idDestinatario, msg, null,"false");
                    }
                }
            }
            mensagem.setText("");

        }else {
            //mensagem.setError("Digite uma mensagem");
        }
    }
    private void SalvarConversa(String idR, String idD, Mensagem mensagem, Usuarios pessoa, String visualizacao){
        Conversa conversaRem = new Conversa();
        conversaRem.setIdRemetente(idR);
        conversaRem.setIdDestinatario(idD);
        conversaRem.setUltimaMensagem(mensagem.getMensagem());
        conversaRem.setVisualiza(visualizacao);
        if(pessoa != null){
            conversaRem.setUsuarioExibicao(pessoa);
        }else {
            conversaRem.setGrupo(grupo);
            conversaRem.setIsGroup("true");
        }
        conversaRem.Salvar();

    }
    private void Salvar(String idRemetente, String idDestinatario, Mensagem m){
        DatabaseReference database = ConfiguracaoFirebase.getFirebase();;
        DatabaseReference mensagemRef = database.child("Mensagens");
        mensagemRef.child(idRemetente).child(idDestinatario).push().setValue(m);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(usuarioAux == null){
            UsuarioLocal(new Callback() {
                @Override
                public void onDataLoaded() {
                    usuarioAux = usuarioLogado;
                }

                @Override
                public void onError() {

                }
            });
        }
        menssagens.clear();
        adapter.notifyDataSetChanged();
        RecuperarMenssagem();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        menssagens.clear();
        adapter.notifyDataSetChanged();
    //    RecuperarMenssagem();
        if(usuarioAux == null){
            UsuarioLocal(new Callback() {
                @Override
                public void onDataLoaded() {
                    usuarioAux = usuarioLogado;
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagemRef.removeEventListener(childEventListener);
    }

    private void RecuperarMenssagem(){
        menssagens.clear();
        childEventListener = mensagemRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem msg = snapshot.getValue(Mensagem.class);
                menssagens.add(msg);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child("Conversas").child(idRemetente).child(idDestinatario).child("visualiza").setValue("true");
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
    private void PegarLoc(final Callback callback) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Verificar permissão de localização
        boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!isLocationEnabled) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                lati = location.getLatitude();
                                longi = location.getLongitude();
                                callback.onDataLoaded();
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, iniciar solicitação única de localização
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    PegarLoc(new Callback() {
                        @Override
                        public void onDataLoaded() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            } else {
                // Permissão negada, notificar o usuário
                Toast.makeText(this, "Permissão de localização necessária", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void UsuarioLocal(final Callback callback){
        //Usuarios usuario = new Usuarios();
        FirebaseAuth auth = ConfiguracaoFirebase.getAutenticacao();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios");
        DatabaseReference usuarioRef = ref.child(Base64Custom.codificar(auth.getCurrentUser().getEmail()));
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //usuario = new Usuarios();
                    Endereco endereco = new Endereco();
                    //  usuario = snapshot.getValue(Usuario.class);
                    usuarioLogado.setIdUsuario(Base64Custom.codificar(auth.getCurrentUser().getEmail()));
                    usuarioLogado.setNome(snapshot.child("nome").getValue(String.class));
                    usuarioLogado.setFoto(snapshot.child("foto").getValue(String.class));
                    usuarioLogado.setSexo(snapshot.child("sexo").getValue(String.class));
                    usuarioLogado.setEmail(snapshot.child("email").getValue(String.class));
                    ArrayList<String> esportes = new ArrayList<>();
                    for (DataSnapshot esporteSnapshot : snapshot.child("esportes").getChildren()) {
                        String esporte = esporteSnapshot.getValue(String.class);
                        esportes.add(esporte);
                    }
                    usuarioLogado.setEsportes(esportes);
                    endereco.setUf(snapshot.child("endereco").child("uf").getValue(String.class));
                    endereco.setLocalidade(snapshot.child("endereco").child("localidade").getValue(String.class));
                    usuarioLogado.setEndereco(endereco);
                    callback.onDataLoaded();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface Callback {
        void onDataLoaded();
        void onError();
    }
}