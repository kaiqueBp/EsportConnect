package com.example.esporte.config;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.model.Mensagem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MenssagensAdapter extends RecyclerView.Adapter<MenssagensAdapter.MyViewHolder> {
    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;
    private FirebaseAuth auth;

    public MenssagensAdapter( List<Mensagem> m, Context c){
        this.mensagens = m;
        this.context = c;
    }

    @NonNull
    @Override
    public MenssagensAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;
        if(viewType == TIPO_REMETENTE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.menssagem_remetente, parent, false);
        }else if(viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.menssagem_destinatario, parent, false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MenssagensAdapter.MyViewHolder holder, int position) {
        Mensagem m = mensagens.get(position);
        String msg = m.getMensagem();
        String img = m.getImagem();

        if(img != null){
            Uri url = Uri.parse(img);
            Glide.with(context).load(url).into(holder.imagem);
            holder.mensagem.setVisibility(View.GONE);
        }else{
            holder.mensagem.setText(msg);
            holder.imagem.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        auth = ConfiguracaoFirebase.getAutenticacao();
        Mensagem m = mensagens.get(position);
        String idUsuario = Base64Custom.codificar(auth.getCurrentUser().getEmail());
        if(idUsuario.equals(m.getIdUsuario())){
            return TIPO_REMETENTE;
        }else{
            return TIPO_DESTINATARIO;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagem;
        private TextView mensagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.imgChat);
            mensagem = itemView.findViewById(R.id.textChat);
        }
    }
}
