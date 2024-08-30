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
import com.example.esporte.model.Esporte;
import com.example.esporte.view.PessoasActivity;


import java.util.ArrayList;

public class EsporteAdapter extends RecyclerView.Adapter<EsporteAdapter.ViewHolder> {

    private ArrayList<Esporte> arrayEsporte;
    private Context context;

    public EsporteAdapter(ArrayList<Esporte> arrayEsporte, Context context) {
        this.arrayEsporte = arrayEsporte;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listar_esportes,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(arrayEsporte.get(position).getImage())
                .into(holder.imgEsporte);

        holder.nomeEsporte.setText(arrayEsporte.get(position).getNome());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Clicou em " + holder.nomeEsporte.getText(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, PessoasActivity.class);
                intent.putExtra("nome", holder.nomeEsporte.getText());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayEsporte.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgEsporte;
        private TextView nomeEsporte;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEsporte = itemView.findViewById(R.id.imageEsporte);
            nomeEsporte = itemView.findViewById(R.id.textEsporte);
            cardView = itemView.findViewById(R.id.idCard);
        }

    }
}
