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
        // Configure o canal de notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("canal_id", "Nome do Canal", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Adicione o ouvinte ao nó do Firebase
        DatabaseReference mensagensRef = FirebaseDatabase.getInstance().getReference("Conversas").child(ConfiguracaoFirebase.IDUsuarioLogado());
        mensagensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot mensagemSnapshot : dataSnapshot.getChildren()) {
                    String mensagem = mensagemSnapshot.child("ultimaMensagem").getValue(String.class);
                    enviarNotificacao(mensagem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void enviarNotificacao(String mensagem) {
        Intent intent = new Intent(this, Teste.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "canal_id")
                .setSmallIcon(R.drawable.baseline_chat_24)
                .setContentTitle("Nova Mensagem")
                .setContentText(mensagem)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
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
