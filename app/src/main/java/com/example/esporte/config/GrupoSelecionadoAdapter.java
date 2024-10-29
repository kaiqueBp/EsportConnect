package com.example.esporte.config;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.model.Usuarios;

import java.util.ArrayList;

public class GrupoSelecionadoAdapter extends RecyclerView.Adapter<GrupoSelecionadoAdapter.ViewHolder>{
    private ArrayList<Usuarios> usuariosSelecionados;
    private Context context;
    private GrupoSelecionadoAdapter selecionados;
    private GrupoSelecionadoAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public GrupoSelecionadoAdapter(ArrayList<Usuarios> usuariosSelecionados, Context context, OnItemClickListener listener) {
        this.usuariosSelecionados = usuariosSelecionados;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GrupoSelecionadoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grupo_selecionado, parent, false);
        return new GrupoSelecionadoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoSelecionadoAdapter.ViewHolder holder, int position) {
        holder.txt.setText(usuariosSelecionados.get(position).getNome());
        Glide.with(context).load(usuariosSelecionados.get(position).getFoto()).into(holder.img);
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
        return usuariosSelecionados.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private TextView txt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgGrupoSelect);
            txt = itemView.findViewById(R.id.textGrupoSelect);
        }
    }
}
