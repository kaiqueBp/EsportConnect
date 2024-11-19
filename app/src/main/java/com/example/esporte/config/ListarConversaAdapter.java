package com.example.esporte.config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.model.Conversa;
import com.example.esporte.model.Grupo;
import com.example.esporte.model.Mensagem;
import com.example.esporte.model.Usuarios;
import com.example.esporte.view.ChatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ListarConversaAdapter extends RecyclerView.Adapter<ListarConversaAdapter.MyViewHolder> {
    private List<Conversa> conversas;
    private Context context;
    private  Grupo grupo;

    public ListarConversaAdapter(List<Conversa> conversas, Context context) {
        this.conversas = conversas;
        this.context = context;
    }

    @NonNull
    @Override
    public ListarConversaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listar_conversa, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ListarConversaAdapter.MyViewHolder holder, int position) {
        Conversa conversa = conversas.get(holder.getAdapterPosition());
//        CarregarGrupo(conversa.getGrupo().getId(), new Callback() {
//            @Override
//            public void onDataLoaded() {
//
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });

        if(conversa.getIsGroup().equals("true")){

            Grupo grupo = conversa.getGrupo();
            holder.nome.setText(grupo.getNome());
            if(grupo.getFoto() != null){
                Glide.with(context).load(grupo.getFoto()).into(holder.img);
            }else{
                holder.img.setImageResource(R.drawable.grupo_padrao);
            }
            holder.msg.setText(conversa.getUltimaMensagem());
        }else{
            Usuarios usuario = conversa.getUsuarioExibicao();
            holder.nome.setText(usuario.getNome());
            holder.msg.setText(conversa.getUltimaMensagem());
            if(usuario.getFoto() != null){
                Glide.with(context).load(usuario.getFoto()).into(holder.img);
            }else{
                holder.img.setImageResource(R.drawable.usuario_padrao);
            }
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Conversa conversa = conversas.get(holder.getAdapterPosition());
                if(conversa.getIsGroup().equals("true")){
                    //Grupo grupo = conversa.getGrupo();
                    PegarGrupo(conversa.getGrupo().getId(), new Callback() {
                        @Override
                        public void onDataLoaded() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
                }else{
                    int posicao = holder.getAdapterPosition();
                    Usuarios usuarioCLicado = conversas.get(posicao).getUsuarioExibicao();
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("usuarioCLicado", usuarioCLicado);
                    context.startActivity(intent);
                }

            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int posicao = holder.getAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Excluir conversa?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String idRem;
                        String idDest;
                        if(conversa.getIsGroup().equals("true")){
                            Grupo g = conversa.getGrupo();
                            for (Usuarios usuario:g.getMembros()) {
                                if(usuario.getIdUsuario().equals(ConfiguracaoFirebase.IDUsuarioLogado())){
                                    g.getMembros().remove(usuario);
                                }
                            }
                            DatabaseReference grupoRef = ConfiguracaoFirebase.getFirebase().child("grupos").child(g.getId());
                            grupoRef.setValue(g);
                            DatabaseReference conversaRef = ConfiguracaoFirebase.getFirebase().child("Conversas");
                            for (Usuarios usuario:g.getMembros()) {
                                conversaRef.child(usuario.getIdUsuario()).child(g.getId()).child("grupo").setValue(g);
                            }
                            conversaRef.child(ConfiguracaoFirebase.IDUsuarioLogado()).child(g.getId()).child("grupo").setValue(g);
                        }
                        if(conversa.getUsuarioExibicao() == null){
                            idRem = conversa.getIdRemetente();
                            idDest = conversa.getIdDestinatario();
                            notifyItemRemoved(holder.getAdapterPosition());
                        }else{
                            Usuarios usuarioCLicado = conversas.get(posicao).getUsuarioExibicao();

                            FirebaseAuth auth = ConfiguracaoFirebase.getAutenticacao();
                            String usuarioPessaoal = auth.getCurrentUser().getEmail();
                            idDest = usuarioCLicado.getIdUsuario();
                            idRem = Base64Custom.codificar(usuarioPessaoal);
                            notifyItemRemoved(holder.getAdapterPosition());
                        }


                        deletarConversa(idDest,idRem, new Callback() {
                            @Override
                            public void onDataLoaded() {
                                deleteMessages(idDest,idRem, new Callback() {
                                    @Override
                                    public void onDataLoaded() {
                                        ((Activity) context).recreate();
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Fechar opção de excluir conversa
                    }
                });
                builder.show();

                return false;

            }
        });

    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView nome, msg;
        private CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgConversa);
            nome = itemView.findViewById(R.id.nomeConversa);
            msg = itemView.findViewById(R.id.msgConversa);
            cardView = itemView.findViewById(R.id.cardConversa);
        }
    }
    private void deleteMessages(String idDestinatarioBase64, String idUsuarioBase64, final Callback callback) {
        // Consultar mensagens com IDs de remetente e destinatário
        DatabaseReference mensagensRef = FirebaseDatabase.getInstance().getReference("Mensagens");
        mensagensRef.child(idUsuarioBase64)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot mensagemSnapshot : dataSnapshot.getChildren()) {
                            String idDestinatario = mensagemSnapshot.getKey();

                            // Comparar os IDs
                            if (idDestinatarioBase64.equals(idDestinatario)) {
                                // Excluir a mensagem
                                mensagensRef.child(idUsuarioBase64).child(idDestinatario).removeValue();
                                callback.onDataLoaded();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("ExcluirMensagem", "Erro ao excluir mensagem", databaseError.toException());
                        callback.onError();
                    }
                });
    }
    private void deletarConversa(String idDestinatarioBase64, String idUsuarioBase64, final Callback callback) {
        DatabaseReference mensagensRef = FirebaseDatabase.getInstance().getReference("Conversas");
        mensagensRef.child(idUsuarioBase64)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot mensagemSnapshot : dataSnapshot.getChildren()) {
                            String idDestinatario = mensagemSnapshot.getKey();

                            // Comparar os IDs
                            if (idDestinatarioBase64.equals(idDestinatario)) {
                                // Excluir a mensagem
                                mensagensRef.child(idUsuarioBase64).child(idDestinatario).removeValue();
                                callback.onDataLoaded();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("ExcluirMensagem", "Erro ao excluir mensagem", databaseError.toException());
                        callback.onError();
                    }
                });
    }
    private  void PegarGrupo(String idGrupo, final Callback callback){
        DatabaseReference mensagensRef = FirebaseDatabase.getInstance().getReference("grupos");
        mensagensRef.child(idGrupo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Grupo grupo = snapshot.getValue(Grupo.class);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("grupoClicado", grupo);
                context.startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void CarregarGrupo(String idGrupo, final Callback callback){
        DatabaseReference mensagensRef = FirebaseDatabase.getInstance().getReference("grupos");
        mensagensRef.child(idGrupo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                grupo = snapshot.getValue(Grupo.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    public interface Callback {
        void onDataLoaded();
        void onError();
    }

}
