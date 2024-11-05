package com.example.esporte.view;

import static com.example.esporte.view.EditarActivity.usuario;

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
    public static ArrayList<String> selectedEsportes;
    //private Usuarios usuario = new Usuarios();

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
        //esport = new Esporte();
        //Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        //usuario = (Usuarios) getIntent().getSerializableExtra("esportes");
        //selectedEsportes = new ArrayList<>();
        pegarEsportes();

//        if(usuario != null){
//            pegarEportesSelecionados();
//            for (int i = 0; i < lista.size(); i++) {
//                String esporteAtual = lista.get(i);
//                if (selectedEsportes.contains(esporteAtual)) {
//                    // Adicionar a posição na lista de itens selecionados
//                    selectedItems.add(i);
//                    // Mudar a cor de fundo do item para mostrar que está selecionado
//                    View item = esporte.getChildAt(i);
//                    if (item != null) {
//                        item.setBackgroundColor(Color.GREEN);
//                    }
//                }
//            }
//
//            esporte.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String esporteSelecionado = lista.get(position);
//
//                    if (selectedItems.contains(position)) {
//                        // Remover o item da lista de selecionados
//                        selectedItems.remove((Integer) position);
//                        view.setBackgroundColor(Color.TRANSPARENT);
//                        usuario.getEsportes().remove(esporteSelecionado);
//                    } else {
//                        // Adicionar o item à lista de selecionados
//                        selectedItems.add(position);
//                        view.setBackgroundColor(Color.GREEN);
//                        usuario.getEsportes().add(esporteSelecionado);
//                    }
//                    // Atualiza a lista de esportes do usuário conforme a seleção
//                }
//            });
//        }
        if(usuario.getEsportes().size() > 0){
            //selectedEsportes.addAll(usuario.getEsportes()); // Adiciona os esportes já selecionados
            pegarEportesSelecionados();
            esporte.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String esporteSelecionado = lista.get(position);

                    if (selectedItems.contains(position)) {
                        // Remover o item da lista de selecionados
                        selectedItems.remove((Integer) position);
                        view.setBackgroundColor(Color.TRANSPARENT);
                        selectedEsportes.remove(esporteSelecionado);
                        usuario.setEsportes(selectedEsportes);
                    } else {
                        // Adicionar o item à lista de selecionados
                        selectedItems.add(position);
                        view.setBackgroundColor(Color.GREEN);
                        if (!selectedEsportes.contains(esporteSelecionado)) {
                            selectedEsportes.add(esporteSelecionado);
                            usuario.setEsportes(selectedEsportes);
                        }
                    }
                }
            });

        }else{
            for (int i = 0; i < lista.size(); i++) {
                String esporteAtual = lista.get(i);
                if (selectedEsportes.contains(esporteAtual)) {
                    // Adicionar a posição na lista de itens selecionados
                    selectedItems.add(i);
                    // Mudar a cor de fundo do item para mostrar que está selecionado
                    View item = esporte.getChildAt(i);
                    if (item != null) {
                        item.setBackgroundColor(Color.GREEN);
                    }
                }
            }
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
                        usuario.setEsportes(selectedEsportes);
                    } else {
                        // Adicionar o item à lista de selecionados
                        selectedItems.add(position);
                        // Muda a cor do item para a cor de seleção
                        view.setBackgroundColor(Color.GREEN);
                        //usuario.adicionarEsporte(lista.get(position));
                        selectedEsportes.add(lista.get(position));
                        usuario.setEsportes(selectedEsportes);
                    }
                    String esporteSelecionado = lista.get(position);
                    //a.setText(esporteSelecionado);
                    // Faça algo com o esporte selecionado
                }
            });
        }


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
    public void pegarEportesSelecionados() {
        firebase.child("Esportes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lista.clear(); // Limpe a lista antes de adicionar novos dados
                for (DataSnapshot esporteSnapshot : dataSnapshot.getChildren()) {
                    String nomeEsporte = esporteSnapshot.child("nome").getValue(String.class);
                    lista.add(nomeEsporte);
                }
                // Crie o adapter apenas após ter carregado os dados
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListarEsporteActivity.this, android.R.layout.simple_list_item_1, lista) {
                    @Override
                    public View getView(int position, View convertView, android.view.ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        // Verifica se o esporte na posição atual está na lista de esportes do usuário
                        String esporteAtual = lista.get(position);
                        if (usuario.getEsportes().contains(esporteAtual)) {
                            // Se o esporte já estiver selecionado, marque a posição e mude a cor de fundo
                            selectedItems.add(position);
                            view.setBackgroundColor(Color.GREEN);
                        } else {
                            // Caso contrário, mantenha a cor de fundo padrão
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        return view;
                    }
                };

                esporte.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.e("Esportes", "Erro ao recuperar esportes: " + databaseError.getMessage());
            }
        });
    }
}