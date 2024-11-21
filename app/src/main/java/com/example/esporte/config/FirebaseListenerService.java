package com.example.esporte.config;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.esporte.R;
import com.example.esporte.config.ConfiguracaoFirebase;
import com.example.esporte.model.Grupo;
import com.example.esporte.model.Usuarios;
import com.example.esporte.view.Teste;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseListenerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("canal_id", "Nome do Canal", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            NotificationChannel canalGrupo = new NotificationChannel("canal_idGrupo", "Nome do Canal", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager managerGrupo = getSystemService(NotificationManager.class);
            managerGrupo.createNotificationChannel(canalGrupo);
        }

        DatabaseReference mensagensRef = FirebaseDatabase.getInstance().getReference("Conversas").child(ConfiguracaoFirebase.IDUsuarioLogado());
        mensagensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot mensagemSnapshot : dataSnapshot.getChildren()) {
                    String mensagem = mensagemSnapshot.child("ultimaMensagem").getValue(String.class);
                    String valiGrup = mensagemSnapshot.child("isGroup").getValue(String.class);
                    String valiVisualiza = mensagemSnapshot.child("visualiza").getValue(String.class);
                    if(valiGrup.equals("false")){

                        if(valiVisualiza.equals("false")){
                            Usuarios usuario = mensagemSnapshot.child("usuarioExibicao").getValue(Usuarios.class);
                            enviarNotificacao(mensagem, usuario.getNome());
                        }
                    }
                    else{
                        if(valiVisualiza.equals("false")){
                            Grupo g = mensagemSnapshot.child("grupo").getValue(Grupo.class);
                            enviarNotificacaoGrupo(mensagem, g.getNome());
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void enviarNotificacao(String mensagem, String nome) {
        Intent intent = new Intent(this, Teste.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "canal_id")
                .setSmallIcon(R.drawable.baseline_chat_24)
                .setContentTitle(nome)
                .setContentText(mensagem)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
    private void enviarNotificacaoGrupo(String mensagem, String nome) {
        Intent intent = new Intent(this, Teste.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "canal_idGrupo")
                .setSmallIcon(R.drawable.baseline_chat_24)
                .setContentTitle(nome)
                .setContentText(mensagem)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
