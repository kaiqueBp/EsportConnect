package com.example.esporte.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.GrupoAdapter;
import com.example.esporte.config.ListarConversaAdapter;
import com.example.esporte.config.ListarPessoasGrupoAdapter;
import com.example.esporte.model.Grupo;
import com.example.esporte.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PerfilGrupoActivity extends AppCompatActivity {
    private Grupo grupo;
    private TextView nomeGrupo, adicionar,titulo;
    private ImageView img;
    private ListarPessoasGrupoAdapter adapter;
    private RecyclerView recyclerView;
    private static final int REQUEST_IMAGE_SELECT = 1;
    private StorageReference storage;
    private Uri selectedImageUri;
    private String urlImagem;
    private Toolbar too;
    //private ArrayList<Usuarios> listaUsuarios = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_grupo);
        nomeGrupo = findViewById(R.id.idPNomeGrop);
        titulo = findViewById(R.id.tituloGrup);
        adicionar = findViewById(R.id.idAdd);
        img = findViewById(R.id.idImgPGrupo);
        Bundle extras = getIntent().getExtras();
        grupo = (Grupo) extras.getSerializable("perfilGrupo");
        too = findViewById(R.id.idToolbarP);
        setSupportActionBar(too);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Grupo");
        titulo.setText(grupo.getNome());

        titulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = new EditText(PerfilGrupoActivity.this);
                editText.setText(titulo.getText().toString());

                // Construindo o AlertDialog
                new AlertDialog.Builder(PerfilGrupoActivity.this)
                        .setTitle("Editar Texto")
                        .setView(editText)
                        .setPositiveButton("Salvar", (dialog, which) -> {
                            // Atualizando o TextView com o novo texto
                            titulo.setText(editText.getText().toString());
                            grupo.setNome(titulo.getText().toString());
                            Atualizar();
                            finish();

                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                        .show();

            }
        });
        adapter = new ListarPessoasGrupoAdapter(grupo.getMembros(), PerfilGrupoActivity.this, new GrupoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Usuarios usuario = grupo.getMembros().get(position);
                String id = usuario.getIdUsuario();
                if(!usuario.getIdUsuario().equals(ConfiguracaoFirebase.IDUsuarioLogado())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PerfilGrupoActivity.this);
                    builder.setMessage("Excluir membro do grupo?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            deletarUsuario(id,grupo.getId(), new Callback() {
//                                @Override
//                                public void onDataLoaded() {
//                                    finish();
//                                    startActivity(getIntent());
//                                }
//
//                                @Override
//                                public void onError() {
//
//                                }
//                            });
                            deletarConversa(id, new Callback() {
                                @Override
                                public void onDataLoaded() {
                                    deletarUsuario(id, grupo.getId(), new Callback() {
                                        @Override
                                        public void onDataLoaded() {
                                            finish();
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });

                                }

                                @Override
                                public void onError() {

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Fechar opção de excluir conversa
                        }
                    });
                    builder.show();
                }
            }
        });
        recyclerView = findViewById(R.id.idPesGrupo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        recyclerView.setAdapter(adapter);

        nomeGrupo.setText("Membros: "+grupo.getMembros().size());

        if(grupo.getFoto() != null || grupo.getFoto() == ""){
            Glide.with(this).load(grupo.getFoto()).into(img);
        }else
            img.setImageResource(R.drawable.grupo_padrao);


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(grupo.getFoto() != null || grupo.getFoto() == ""){
                    DeletarFoto();
                }
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_SELECT);

            }
        });

        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PerfilGrupoActivity.this, CriarGrupoActivity.class);
                intent.putExtra("adicionar", grupo);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DEBUG", "onActivityResult called");
        if (resultCode == RESULT_OK && data != null) {
            img.setImageDrawable(null);
            try {
                Bitmap imagem = null;
                Uri selected = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selected);
                img.setImageBitmap(imagem);
                SalvarFoto();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void deletarUsuario(String idPessoa, String idGrupo, final Callback callback) {
        DatabaseReference mensagensRef = FirebaseDatabase.getInstance().getReference("grupos");
        mensagensRef.child(idGrupo)
                .child("membros")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            //DataSnapshot membrosSnapshot = dataSnapshot.child("membros");
                            List<Usuarios> membros = new ArrayList<>();
                            for (DataSnapshot membro : dataSnapshot.getChildren()) {
                                String idMembro = membro.child("idUsuario").getValue(String.class);
                                // Comparar o ID do membro com o ID que você deseja remover
                                if (!idMembro.equals(idPessoa)) {
                                    // Remover o membro
                                    membros.add(membro.getValue(Usuarios.class));
                                }
                            }
                            dataSnapshot.getRef().setValue(membros);
                            callback.onDataLoaded();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("ExcluirMensagem", "Erro ao excluir mensagem", databaseError.toException());
                        callback.onError();
                    }
                });
    }
    private void deletarConversa(String idPessoa, final Callback callback){
        DatabaseReference conversaRef = ConfiguracaoFirebase.getFirebase().child("Conversas");
        conversaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot membro : snapshot.getChildren()){
                    if(membro.getKey().equals(idPessoa)){
                        DataSnapshot snap = membro.child(grupo.getId());
                        snap.getRef().removeValue();
                    }
                    if(membro.getKey().equals(ConfiguracaoFirebase.IDUsuarioLogado())){
                        DataSnapshot membrosSnapshot = membro.child(grupo.getId()).child("grupo").child("membros");
                        List<Usuarios> membros = new ArrayList<>();
                        for (DataSnapshot m : membrosSnapshot.getChildren()){
                            String idMembro = m.child("idUsuario").getValue(String.class);
                            if (!idMembro.equals(idPessoa)) {
                                // Remover o membro
                                membros.add(m.getValue(Usuarios.class));
                            }
                        }
                        membrosSnapshot.getRef().setValue(membros);
                    }
                }
                callback.onDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void DeletarFoto(){
        StorageReference imagemRef = FirebaseStorage.getInstance().getReference();
        StorageReference image = imagemRef.child("imagens");
        StorageReference imgRef = image.child(grupo.getNome());
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Toast.makeText(EditarActivity.this,"Imagem deletada com sucesso",Toast.LENGTH_LONG).show();
                Log.d("DEBUG", "Imagem deletada com sucesso");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(EditarActivity.this,"Erro ao deletar a imagem",Toast.LENGTH_LONG).show();
                Log.d("DEBUG", "Erro ao deletar a imagem");
            }
        });
    }

    public void SalvarFoto(){
        if(img.getDrawable() == null){

        }else{
            img.setDrawingCacheEnabled(true);
            img.buildDrawingCache();
            Bitmap bitmap = img.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] dadosImagem = baos.toByteArray();
            // Salvar a imagem no Firebase Storage
            StorageReference imagemRef = FirebaseStorage.getInstance().getReference();
            StorageReference image = imagemRef.child("imagens/grupos");
            StorageReference imgRef = image.child(grupo.getId());

            UploadTask up = imgRef.putBytes(dadosImagem);
            up.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    PegarImagem(new ListarConversaAdapter.Callback() {
                        @Override
                        public void onDataLoaded() {
                            grupo.setFoto(urlImagem);
                            Atualizar();
                            finish();
                        }

                        @Override
                        public void onError() {

                        }
                    });

                }
            });
        }

    }
    public void PegarImagem(final ListarConversaAdapter.Callback callback){
        String caminhoImagem = "imagens/grupos/"+grupo.getId();
        StorageReference imagemRef = ConfiguracaoFirebase.getFirestore();
        StorageReference ref = imagemRef.child(caminhoImagem);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                urlImagem = uri.toString();
                callback.onDataLoaded();
            }
        });
    }
    private void Atualizar(){
        DatabaseReference grupoRef = ConfiguracaoFirebase.getFirebase().child("grupos").child(grupo.getId());
        grupoRef.setValue(grupo);
        DatabaseReference conversaRef = ConfiguracaoFirebase.getFirebase().child("Conversas");
        for (Usuarios usuario:grupo.getMembros()) {
            conversaRef.child(usuario.getIdUsuario()).child(grupo.getId()).child("grupo").setValue(grupo);
        }
    }

    public interface Callback {
        void onDataLoaded();
        void onError();
    }
}