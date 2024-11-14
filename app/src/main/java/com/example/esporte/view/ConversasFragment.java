package com.example.esporte.view;

import static android.content.Intent.getIntent;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.esporte.R;
import com.example.esporte.config.Base64Custom;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.config.ListarConversaAdapter;
import com.example.esporte.model.Conversa;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class ConversasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Conversa> Listaconversa = new ArrayList<>();
    private ArrayList<Conversa> apenasConversas = new ArrayList<>();
    private ListarConversaAdapter adapter;
    private DatabaseReference databaseRef;
    private DatabaseReference database;
    private ChildEventListener childEventListener;
    private Toolbar toolbarConversa;
    private View criarGrupo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        criarGrupo = view.findViewById(R.id.idCriarGrupo);
        toolbarConversa = view.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbarConversa);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            activity.getSupportActionBar().setTitle("Conversas");
            toolbarConversa.setTitleTextColor(getResources().getColor(R.color.white));
        }

        adapter = new ListarConversaAdapter(Listaconversa, getActivity());
        recyclerView = view.findViewById(R.id.idListarConversa);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        databaseRef = ConfiguracaoFirebase.getFirebase();
        String identificarUsuario = Base64Custom.codificar(ConfiguracaoFirebase.getAutenticacao().getCurrentUser().getEmail());
        database = databaseRef.child("Conversas").child(identificarUsuario);

        criarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CriarGrupoActivity.class);
                intent.putExtra("lista", apenasConversas);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("canal_id", "Nome do Canal", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        return view;
    }

    private void enviarNotificacao(String mensagem) {
        Intent intent = new Intent(getActivity(), Teste.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(requireContext(), "canal_id")
                .setSmallIcon(R.drawable.baseline_chat_24)
                .setContentTitle("Nova Mensagem")
                .setContentText(mensagem)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Listaconversa.clear();
        adapter.notifyDataSetChanged();
        RecuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        database.removeEventListener(childEventListener);
    }

    public void RecuperarConversas(){
        apenasConversas.clear();
        childEventListener = database.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);

                if (conversa != null && !Listaconversa.contains(conversa)) {
                    if (!conversa.getIsGroup().equals("true")) {
                        apenasConversas.add(conversa);
                    }
                    Listaconversa.add(conversa);
                    adapter.notifyItemInserted(Listaconversa.size() - 1);  // Notifica apenas o item adicionado
                    enviarNotificacao(conversa.getUltimaMensagem());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                if (conversa != null) {
                    int position = Listaconversa.indexOf(conversa);
                    if (position != -1) {
                        Listaconversa.set(position, conversa);
                        adapter.notifyItemChanged(position);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                if (conversa != null) {
                    int position = Listaconversa.indexOf(conversa);
                    if (position != -1) {
                        Listaconversa.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                }
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