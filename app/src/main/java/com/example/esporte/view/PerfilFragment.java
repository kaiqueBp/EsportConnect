package com.example.esporte.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.model.Endereco;
import com.example.esporte.model.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PerfilFragment extends Fragment {

    private ImageView imageView;
    private ListView listaEsportes;
    private TextView nome,email,sexo,cidade;
    private FirebaseAuth auth;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios");
    private Usuarios usuario = new Usuarios();
    private Endereco endereco;
    private Button editar, sugestao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        imageView = view.findViewById(R.id.idFt);
        listaEsportes = view.findViewById(R.id.pEsportes);
        nome = view.findViewById(R.id.pNome);
        email = view.findViewById(R.id.pEmail);
        sexo = view.findViewById(R.id.pSexo);
        cidade = view.findViewById(R.id.pCidade);
        editar = view.findViewById(R.id.btEdit);
        sugestao = view.findViewById(R.id.IdSugestao);

        auth = ConfiguracaoFirebase.getAutenticacao();
        email.setText(auth.getCurrentUser().getEmail());

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditarActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });

        carregarPerfil(new PerfilActivity.Callback() {
            @Override
            public void onDataLoaded() {

                nome.setText(usuario.getNome());;
                sexo.setText(usuario.getSexo());
                Glide.with(getActivity()).load(usuario.getFoto()).into(imageView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, usuario.getEsportes());
                listaEsportes.setAdapter(adapter);
                //cidade.setText(usuario.getEndereco().getLocalidade() + " - " + usuario.getEndereco().getUf());
            }

            @Override
            public void onError() {
                // Tratar erro
            }
        });

        sugestao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });


        return view;
    }
    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Digite o nome do esporte");

        // Configurando o campo de entrada
        final EditText input = new EditText(getActivity());
        builder.setView(input);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String esporte = input.getText().toString();
                sendEmail(esporte);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void sendEmail(String esporte) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:kpbarros2@outlook.com")); // Substitua pelo seu e-mail
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Nome do Esporte");
        emailIntent.putExtra(Intent.EXTRA_TEXT, esporte);

        // Verifica se há um aplicativo que pode enviar o e-mail
        if (emailIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            Toast.makeText(getActivity(), "Nenhum aplicativo de e-mail encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarPerfil(final PerfilActivity.Callback callback){
        DatabaseReference usuarioRef = ref.child(Base64Custom.codificar(auth.getCurrentUser().getEmail()));
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //usuario = new Usuarios();
                    endereco = new Endereco();
                    //  usuario = snapshot.getValue(Usuario.class);
                    usuario.setNome(snapshot.child("nome").getValue(String.class));
                    usuario.setFoto(snapshot.child("foto").getValue(String.class));
                    usuario.setSexo(snapshot.child("sexo").getValue(String.class));
                    usuario.setEmail(snapshot.child("email").getValue(String.class));
                    ArrayList<String> esportes = new ArrayList<>();
                    for (DataSnapshot esporteSnapshot : snapshot.child("esportes").getChildren()) {
                        String esporte = esporteSnapshot.getValue(String.class);
                        esportes.add(esporte);
                    }
                    usuario.setEsportes(esportes);
                    endereco.setUf(snapshot.child("endereco").child("uf").getValue(String.class));
                    endereco.setLocalidade(snapshot.child("endereco").child("localidade").getValue(String.class));
                    usuario.setEndereco(endereco);
                    callback.onDataLoaded();
                } else {
                    Toast.makeText(getActivity(), "Perfil não encontrado", Toast.LENGTH_SHORT).show();
                    callback.onError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Erro ao carregar perfil", Toast.LENGTH_SHORT).show();
                callback.onError();
            }
        });
    }
    public interface Callback {
        void onDataLoaded();
        void onError();
    }

    @Override
    public void onStart() {
        super.onStart();
        carregarPerfil(new PerfilActivity.Callback() {
            @Override
            public void onDataLoaded() {

                nome.setText(usuario.getNome());;
                sexo.setText(usuario.getSexo());
                Glide.with(getActivity()).load(usuario.getFoto()).into(imageView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, usuario.getEsportes());
                listaEsportes.setAdapter(adapter);
                cidade.setText(usuario.getEndereco().getLocalidade() + " - " + usuario.getEndereco().getUf());
            }

            @Override
            public void onError() {
                // Tratar erro
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarPerfil(new PerfilActivity.Callback() {
            @Override
            public void onDataLoaded() {

                nome.setText(usuario.getNome());;
                sexo.setText(usuario.getSexo());
                Glide.with(getActivity()).load(usuario.getFoto()).into(imageView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, usuario.getEsportes());
                listaEsportes.setAdapter(adapter);
                cidade.setText(usuario.getEndereco().getLocalidade() + " - " + usuario.getEndereco().getUf());
            }

            @Override
            public void onError() {
                // Tratar erro
            }
        });
    }

//    @Override
//    public void onRestart() {
//        super.onRestart();
//        carregarPerfil(new PerfilActivity.Callback() {
//            @Override
//            public void onDataLoaded() {
//
//                nome.setText(usuario.getNome());;
//                sexo.setText(usuario.getSexo());
//                Glide.with(getActivity()).load(usuario.getFoto()).into(imageView);
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, usuario.getEsportes());
//                listaEsportes.setAdapter(adapter);
//                cidade.setText(usuario.getEndereco().getLocalidade() + " - " + usuario.getEndereco().getUf());
//            }
//
//            @Override
//            public void onError() {
//                // Tratar erro
//            }
//        });
//    }
    public void Sair(View view){
        auth.signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}