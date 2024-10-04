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
import com.example.esporte.model.Conversa;
import com.example.esporte.model.Usuarios;
import com.example.esporte.view.ChatActivity;

import java.util.List;

public class ListarConversaAdapter extends RecyclerView.Adapter<ListarConversaAdapter.MyViewHolder> {
    private List<Conversa> conversas;
    private Context context;

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
        Conversa conversa = conversas.get(position);
        Usuarios usuario = conversa.getUsuarioExibicao();

        holder.nome.setText(usuario.getNome());
        holder.msg.setText(conversa.getUltimaMensagem());
        if(usuario.getFoto() != null){
            Glide.with(context).load(usuario.getFoto()).into(holder.img);
        }else{
            holder.img.setImageResource(R.drawable.baseline_person_24);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicao = holder.getAdapterPosition();
                Usuarios usuarioCLicado = conversas.get(posicao).getUsuarioExibicao();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("usuarioCLicado", usuarioCLicado);
                context.startActivity(intent);
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
}
