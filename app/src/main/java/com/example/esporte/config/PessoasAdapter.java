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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.model.Usuarios;
import com.example.esporte.view.ChatActivity;


import java.util.ArrayList;

public class PessoasAdapter extends RecyclerView.Adapter<PessoasAdapter.ViewHolder> {
    private ArrayList<Usuarios> usuarios;
    private Context context;

    public PessoasAdapter(ArrayList<Usuarios> usuarios, Context context) {
        this.usuarios = usuarios;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listar_pessoas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(usuarios.get(position).getFoto()).into(holder.img);
        holder.nome.setText(usuarios.get(position).getNome());
        holder.pessoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicao = holder.getAdapterPosition();
                Usuarios usuarioCLicado = usuarios.get(posicao);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("usuarioCLicado", usuarioCLicado);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
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
