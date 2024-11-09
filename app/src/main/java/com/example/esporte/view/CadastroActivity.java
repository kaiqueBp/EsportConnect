package com.example.esporte.view;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.NominatimApi;
import com.example.esporte.config.ValidateCityCallback;
import com.example.esporte.model.Endereco;
import com.example.esporte.model.Esporte;
import com.example.esporte.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CadastroActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button btCadastrar;
    private Button btFoto;
    private RadioButton sexoMasculino;
    private RadioButton sexoFeminino;
    private RadioButton sexoOutros;
    private Usuarios usuario;
    private Esporte esporte;
    private FirebaseAuth auth;
    private EditText nome, email, senha;
    private static final int REQUEST_IMAGE_SELECT = 1;
    private Uri selectedImageUri;
    private ImageView img;
    private String imgUrl;
    private String state;
    private Button bt;
    private EditText text;
    private Spinner spinner;
    private Endereco endereco = new Endereco();
    //private  List<String> selectedEsportes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Button btListar = findViewById(R.id.idListaEsporte);
         sexoMasculino = findViewById(R.id.radioButtonM);
         sexoFeminino = findViewById(R.id.radioButtonF);
         sexoOutros = findViewById(R.id.radioButtonO);
         btCadastrar = findViewById(R.id.idBotaoCadastrar);
         nome = findViewById(R.id.idNome);
         email = findViewById(R.id.idEmail);
         senha = findViewById(R.id.idSenha);
         btFoto = findViewById(R.id.btAdicionarFt);
        img = findViewById(R.id.idImg);
        img.setImageDrawable(getResources().getDrawable(R.drawable.baseline_person_24));
        text = findViewById(R.id.idCidade);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(CadastroActivity.this);
        String[] estados = getResources().getStringArray(R.array.Estados);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
         btFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_SELECT);
            }
        });

         btCadastrar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String sNome = nome.getText().toString();
                 String sEmail = email.getText().toString().toLowerCase();
                 String sSenha = senha.getText().toString();

                 //boolean sSexo = RadionButton();
                 if(!sNome.isEmpty() && !sEmail.isEmpty() && !sSenha.isEmpty() && RadionButton() != ""){

                     usuario = new Usuarios();

                     usuario.setNome(sNome);

                     usuario.setEmail(sEmail);
                     usuario.setSenha(sSenha);
                     usuario.setSexo(RadionButton());
                     //usuario.setEsportes(selectedEsportes);
                     validar();

                 }else{
                     Toast.makeText(CadastroActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                 }
             }
         });

         btListar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(CadastroActivity.this, ListarEsporteActivity.class));
             }
         });
    }


    public String RadionButton(){
        if(sexoMasculino.isChecked()){
            return "M";
        }else if(sexoFeminino.isChecked()){
            return "F";
        }else if(sexoOutros.isChecked()){
            return "O";
        }else return "";
    }
    public void cadastrarUsuario(){

        auth = ConfiguracaoFirebase.getAutenticacao();
        auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            String idUsuario = Base64Custom.codificar(usuario.getEmail());
                            usuario.setIdUsuario(idUsuario);
                            usuario.setFoto(imgUrl);
                            //usuario.salvar();

                            Toast.makeText(CadastroActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            abrirTelaPrincipal();
                        }else{
                            String erro="";
                            try {
                                throw  task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erro = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erro = "Digite um e-mail válido!";
                            }catch (FirebaseAuthUserCollisionException e){
                                erro = "Essa conta já foi cadastrada!";
                            }catch(Exception e){
                                erro = "Erro ao cadastrar usuário!"+ e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastroActivity.this, erro, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DEBUG", "onActivityResult called");
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK && data != null) {
            // Obter a URI da imagem selecionada
            selectedImageUri = data.getData();
            // Mostrar a imagem selecionada no ImageView
            img = findViewById(R.id.idImg);
            img.setImageURI(selectedImageUri);
        }
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
            up.addOnFailureListener(CadastroActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CadastroActivity.this,"Erro ao salvar a Imagem",Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(CadastroActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri url = task.getResult();
                            usuario.setFoto(url.toString());
                            FirebaseUser user = auth.getCurrentUser();
                            user.updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(url).build());
                            usuario.salvar();
                            Toast.makeText(CadastroActivity.this,url.toString(),Toast.LENGTH_LONG).show();

                        }
                    });
                    Toast.makeText(CadastroActivity.this,"Imagem Salva com Sucesso",Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void validar(){
        state = "";
        String city = padronizarTitulo(text.getText().toString());;
        state = spinner.getSelectedItem().toString().toLowerCase();
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
                        // Cidade válida, prosseguir com o cadastro do usuário
                        usuario.setEndereco(endereco);
                        cadastrarUsuario();
                    } else {
                        // Cidade inválida, mostrar mensagem de erro

                        Toast.makeText(CadastroActivity.this, "Cidade inválida ou Estado inválido", Toast.LENGTH_SHORT).show();

                    }
                }
            });
            task.execute(city, state);
        }
    }

    public static String padronizarTitulo(String str) {
//        String[] words = str.split(" ");
//        StringBuilder titleCase = new StringBuilder();
//
//        for (String word : words) {
//            titleCase.append(word.substring(0, 1).toUpperCase());
//            titleCase.append(word.substring(1).toLowerCase());
//            titleCase.append(" ");
//        }
        str = str.toLowerCase();
        return str.toString().replaceAll("[áàâãä]", "a")
                .replaceAll("[éèêë]", "e")
                .replaceAll("[íìîï]", "i")
                .replaceAll("[óòôõö]", "o")
                .replaceAll("[úùûü]", "u")
                .trim();
    }


    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public class ValidateCityTask extends AsyncTask<String, Void, String> {
        private CadastroActivity mActivity;
        private ValidateCityCallback callback;

        public ValidateCityTask(CadastroActivity activity, ValidateCityCallback callback) {
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
            int j =0;
            Log.d("ValidateCityTask", "Name: " + response);
            if (response != null) {
                Log.d("ValidateCityTask", "Resposta: " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String cidadePadronizado = padronizarTitulo(text.getText().toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nome = jsonObject.getString("nome");
                        nome = padronizarTitulo(nome);
                        if (nome.equals(cidadePadronizado)) {
                            j =1;
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "Erro ao parsear JSON", Toast.LENGTH_SHORT).show();
                    // Handle JSON parsing error
                }
                if (j > 0 ) {
                    SalvarFoto();
                    endereco.setLocalidade(text.getText().toString().toUpperCase());
                    endereco.setUf(spinner.getSelectedItem().toString());
                    callback.onCityValidated(true, endereco);
                }
                if(j == 0){
                    Toast.makeText(CadastroActivity.this, "Cidade inválida ou Estado inválido", Toast.LENGTH_SHORT).show();
                }
                j=0;
            }
        }
    }

    public void restartActivity() {
        finish();
        startActivity(getIntent());
    }

}