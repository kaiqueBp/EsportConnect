package com.example.esporte.config;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.model.Conversa;
import com.example.esporte.model.Usuarios;

import java.util.ArrayList;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.ViewHolder>{
    private ArrayList<Conversa> conversas;
    private ArrayList<Usuarios> usuariosSelecionados = new ArrayList<>();
    private Context context;
    private GrupoSelecionadoAdapter selecionados;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public GrupoAdapter(ArrayList<Conversa> usuarios, Context context, OnItemClickListener listener) {
        this.conversas = usuarios;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GrupoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listar_pessoas, parent, false);
        return new GrupoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoAdapter.ViewHolder holder, int position) {
        Conversa conversa = conversas.get(position);
        Usuarios usuario = conversa.getUsuarioExibicao();
        if (usuario.getFoto() == null || usuario.getFoto() == ""){
            holder.img.setImageResource(R.drawable.usuario_padrao);
        }else{
            Glide.with(context).load(usuario.getFoto()).into(holder.img);
        }
        holder.nome.setText(usuario.getNome());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView nome;
        private CardView pessoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgPessoa);
            nome = itemView.findViewById(R.id.nomePessoa);
            pessoa = itemView.findViewById(R.id.idCardPessos);
        }
    }
}
