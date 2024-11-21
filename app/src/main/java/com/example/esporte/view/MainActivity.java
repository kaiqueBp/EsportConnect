package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esporte.R;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.FirebaseListenerService;
import com.example.esporte.model.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private Button btCadastrar;
    private Button btEntrar;
    private FirebaseAuth auth;
    private TextInputEditText email;
    private TextInputEditText senha;
    private Usuarios usuario;
    private TextView recuperar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        btCadastrar = findViewById(R.id.idCadastrar);
        btEntrar = findViewById(R.id.btEntrar);
        email = findViewById(R.id.idEmailLogin);
        senha = findViewById(R.id.idSenhaLogin);
        recuperar = findViewById(R.id.idRecupere);

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                if (!userEmail.isEmpty()) {
                    auth = ConfiguracaoFirebase.getAutenticacao();
                    auth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Email de Redefinição de senha enviado", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Erro ao enviar email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Digite seu email", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailDigitado = email.getText().toString();
                String senhaDigitada = senha.getText().toString();
                if(emailDigitado.isEmpty() || senhaDigitada.isEmpty()){
                    Toast.makeText(MainActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else{
                    usuario = new Usuarios();
                    usuario.setEmail(emailDigitado);
                    usuario.setSenha(senhaDigitada);
                    validarLogin();
                }
            }
        });
        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivities(new Intent[]{new Intent(MainActivity.this, CadastroActivity.class)});
            }
        });
    }
    public void validarLogin() {
        auth = ConfiguracaoFirebase.getAutenticacao();
        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            abrirTelaPrincipal();
                            Toast.makeText(MainActivity.this, "Bem - vindo!!", Toast.LENGTH_SHORT).show();
                        } else {
                            String erro = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                erro = "Email não cadastrado";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erro = "Email ou senha estão incorretas";
                            } catch (Exception e) {
                                erro = "Erro ao fazer login" + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(MainActivity.this, erro, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, Teste.class));
        finish();
    }
}