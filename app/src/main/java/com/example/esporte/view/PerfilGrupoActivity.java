package com.example.esporte.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.esporte.R;
import com.example.esporte.model.Grupo;

public class PerfilGrupoActivity extends AppCompatActivity {
    private Grupo grupo;
    private TextView nomeGrupo, adicionar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_grupo);
        nomeGrupo = findViewById(R.id.idPNomeGrop);
        adicionar = findViewById(R.id.idAdd);

        Bundle extras = getIntent().getExtras();
        grupo = (Grupo) extras.getSerializable("perfilGrupo");

        nomeGrupo.setText("Membros: "+grupo.getMembros().size());

        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilGrupoActivity.this, CriarGrupoActivity.class);
                intent.putExtra("adicionar", grupo);
                startActivity(intent);
            }
        });
    }
}