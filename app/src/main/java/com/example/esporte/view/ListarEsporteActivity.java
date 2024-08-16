package com.example.esporte.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esporte.R;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.model.Esporte;
import com.example.esporte.model.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListarEsporteActivity extends AppCompatActivity {
    private FirebaseAuth auth = ConfiguracaoFirebase.getAutenticacao();
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
    private List<String> lista = new ArrayList<>(); // Inicialize a lista
    List<Integer> selectedItems = new ArrayList<>();
    private ListView esporte;
    private Button salvar;
    private Esporte esport ;
    public static List<String> selectedEsportes;

    @Override
    protected void onStart() {
        super.onStart();
        //selectedEsportes.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_esporte);
        esporte = findViewById(R.id.idLista);
        salvar = findViewById(R.id.btSalvarEsporte);
        esport = new Esporte();
        selectedEsportes = new ArrayList<>();
        pegarEsportes();

        //usuario = new Usuarios();

        // Crie o adapter apenas após ter carregado os dados
        esporte.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedItems.contains(position)) {
                    // Remover o item da lista de selecionados
                    selectedItems.remove((Integer) position);
                    // Muda a cor do item para a cor original
                    view.setBackgroundColor(Color.TRANSPARENT);
                   // usuario.removerEsporte(lista.get(position));
                    selectedEsportes.remove(lista.get(position));
                } else {
                    // Adicionar o item à lista de selecionados
                    selectedItems.add(position);
                    // Muda a cor do item para a cor de seleção
                    view.setBackgroundColor(Color.GREEN);
                    //usuario.adicionarEsporte(lista.get(position));
                    selectedEsportes.add(lista.get(position));
                }
                String esporteSelecionado = lista.get(position);
                //a.setText(esporteSelecionado);
                // Faça algo com o esporte selecionado
            }
        });
        
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public  void  Encerrar(View view){
        finish();
    }
    public void pegarEsportes() {
        firebase.child("Esportes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lista.clear(); // Limpe a lista antes de adicionar novos dados
                for (DataSnapshot esporteSnapshot : dataSnapshot.getChildren()) {
                    String nomeEsporte = esporteSnapshot.child("nome").getValue(String.class);
                    lista.add(nomeEsporte);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ListarEsporteActivity.this, android.R.layout.simple_list_item_1, lista);
                esporte.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.e("Esportes", "Erro ao recuperar esportes: " + databaseError.getMessage());
            }
        });
    }
}