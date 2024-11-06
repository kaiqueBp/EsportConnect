package com.example.esporte.view;



import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import com.example.esporte.config.NominatimApi;
import com.example.esporte.config.ValidateCityCallback;
import com.example.esporte.model.Endereco;
import com.example.esporte.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EditarActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static Usuarios usuario = new Usuarios();
    private Endereco endereco = new Endereco();
    private ImageView img;
    private EditText nome,cidade;
    private Spinner estado;
    private RadioButton masculino, feminino, outros;
    private int posicao;
    private String state;
    private FirebaseAuth auth;
    private Button editar,ft;
    private static final int REQUEST_IMAGE_SELECT = 200;
    private Uri selectedImageUri;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        img = findViewById(R.id.editImg);
        nome = findViewById(R.id.editNome);
        cidade = findViewById(R.id.editCidade);
        estado = findViewById(R.id.editEstado);
        masculino = findViewById(R.id.editM);
        feminino = findViewById(R.id.editF);
        outros = findViewById(R.id.editO);
        editar = findViewById(R.id.btEditar);
        ft = findViewById(R.id.btEdiFoto);
        auth = ConfiguracaoFirebase.getAutenticacao();
        Intent intent = getIntent();
        usuario = (Usuarios) getIntent().getSerializableExtra("usuario");
        ft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentGaleria.setType("image/*");
                startActivityForResult(intentGaleria, REQUEST_IMAGE_SELECT);
            }
        });

        //usuario.setEndereco(endereco);
        estado.setOnItemSelectedListener(EditarActivity.this);
        String[] estados = getResources().getStringArray(R.array.Estados);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estado.setAdapter(adapter);
        posicao = adapter.getPosition(usuario.getEndereco().getUf());
        CarregarDados();
    }
    private void CarregarDados() {
        Glide.with(this).load(usuario.getFoto()).into(img);
        nome.setText(usuario.getNome());
        cidade.setText(usuario.getEndereco().getLocalidade());
        estado.setSelection(posicao);
        if (usuario.getSexo().toUpperCase().equals("M")) {
            masculino.setChecked(true);
        } else if (usuario.getSexo().equals("F")) {
            feminino.setChecked(true);
        } else {
            outros.setChecked(true);
        }
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
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //CarregarDados();
    }

    public void DeletarFoto(){
        StorageReference imagemRef = FirebaseStorage.getInstance().getReference();
        StorageReference image = imagemRef.child("imagens");
        StorageReference imgRef = image.child(usuario.getNome());
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
            StorageReference image = imagemRef.child("imagens");
            StorageReference imgRef = image.child(nome.getText().toString());

            UploadTask up = imgRef.putBytes(dadosImagem);
            up.addOnFailureListener(EditarActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditarActivity.this,"Erro ao salvar a Imagem",Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(EditarActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri url = task.getResult();
                            usuario.setFoto(url.toString());
                            usuario.salvar();
                            Intent intent = new Intent(EditarActivity.this, PerfilActivity.class);
                            finish();
                            Toast.makeText(EditarActivity.this,url.toString(),Toast.LENGTH_LONG).show();

                        }
                    });
                    Toast.makeText(EditarActivity.this,"Imagem Salva com Sucesso",Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public String RadionButton(){
        if(masculino.isChecked()){
            return "M";
        }else if(feminino.isChecked()){
            return "F";
        }else if(outros.isChecked()){
            return "O";
        }else return "";
    }
    public void EditarEsporte(View view){
        Intent intent = new Intent(this, ListarEsporteActivity.class);
        startActivity(intent);
    }
    public static String padronizarTitulo(String str) {
        str = str.toLowerCase();
        return str.toString().replaceAll("[áàâãä]", "a")
                .replaceAll("[éèêë]", "e")
                .replaceAll("[íìîï]", "i")
                .replaceAll("[óòôõö]", "o")
                .replaceAll("[úùûü]", "u")
                .trim();
    }


    public void atualizarUsuario(){
        usuario.setIdUsuario(Base64Custom.codificar(auth.getCurrentUser().getEmail()));
        //usuario.salvar();
    }

    public void validar(View view){
        state = "";
        String city = padronizarTitulo(cidade.getText().toString());;
        state = estado.getSelectedItem().toString().toLowerCase();
        //state = endereco.getEstado(state);
        Toast.makeText(this, "Estado selecionado: " + city, Toast.LENGTH_SHORT).show();
        if(city.isEmpty() || state == "Selecione um estado"){
            Toast.makeText(this, "Informe uma cidade e um estado", Toast.LENGTH_SHORT).show();
            return;
        }else {
            ValidateCityTask task = new ValidateCityTask(this, new ValidateCityCallback() {
                @Override
                public void onCityValidated(boolean isValid, Endereco endereco) {
                    if (isValid) {
                        //DeletarFoto();

                        usuario.setSexo(RadionButton());
                        usuario.setNome(nome.getText().toString());
                        usuario.setEndereco(endereco);
                        atualizarUsuario();

                    } else {
                        // Cidade inválida, mostrar mensagem de erro

                        Toast.makeText(EditarActivity.this, "Cidade inválida ou Estado inválido", Toast.LENGTH_SHORT).show();

                    }
                }
            });
            task.execute(city, state);
        }
    }


    public class ValidateCityTask extends AsyncTask<String, Void, String> {
        private EditarActivity mActivity;
        private ValidateCityCallback callback;

        public ValidateCityTask(EditarActivity activity, ValidateCityCallback callback) {
            mActivity = activity;
            this.callback = callback;
        }


        @Override
        protected String doInBackground(String... params) {
            String state = params[1]; // adicionado para receber a sigla do estado
            try {
                String response = NominatimApi.searchCity(state);
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            int j = 0;
            Log.d("ValidateCityTask", "Name: " + response);
            if (response != null) {
                Log.d("ValidateCityTask", "Resposta: " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String cidadeInputPadronizado = padronizarTitulo(cidade.getText().toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String cidadeInfo = jsonArray.getString(i);
                        int separatorIndex = cidadeInfo.indexOf(":");

                        // Extrair o nome da cidade após o separador ":"
                        String nomeCidade = cidadeInfo.substring(separatorIndex + 1);

                        if (padronizarTitulo(nomeCidade).equals(cidadeInputPadronizado)) {
                            j++;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "Erro ao parsear JSON", Toast.LENGTH_SHORT).show();
                    // Handle JSON parsing error
                }
                if (j > 0) {
                    DeletarFoto();
                    SalvarFoto();
                    endereco.setLocalidade(cidade.getText().toString());
                    endereco.setUf(estado.getSelectedItem().toString());
                    callback.onCityValidated(true, endereco);
                } else {
                    Toast.makeText(EditarActivity.this, "Cidade inválida ou Estado inválido", Toast.LENGTH_SHORT).show();
                    //CadastroActivity.this.restartActivity();
                }
                j = 0;
            }
        }
    }
}