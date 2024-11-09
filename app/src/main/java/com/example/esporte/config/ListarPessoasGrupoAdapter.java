package com.example.esporte.config;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.esporte.R;
import com.example.esporte.model.Mensagem;
import com.example.esporte.model.Usuarios;

import java.util.List;

public class ListarPessoasGrupoAdapter extends RecyclerView.Adapter<ListarPessoasGrupoAdapter.MyViewHolder>{
    private List<Usuarios> usuarios;
    private Context context;
    private GrupoAdapter.OnItemClickListener listener;

    public ListarPessoasGrupoAdapter(List<Usuarios> usuarios, Context context, GrupoAdapter.OnItemClickListener listener) {
        this.usuarios = usuarios;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListarPessoasGrupoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_perfil_listagem, parent, false);
        return new ListarPessoasGrupoAdapter.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ListarPessoasGrupoAdapter.MyViewHolder holder, int position) {
        if(usuarios.size() > 0){
            if(usuarios.get(position).getFoto() != null){
                Glide.with(context).load(usuarios.get(position).getFoto()).into(holder.foto);
            }else
                holder.foto.setImageResource(R.drawable.usuario_padrao);

            if(usuarios.get(position).getIdUsuario().equals(ConfiguracaoFirebase.IDUsuarioLogado()))
                holder.nome.setText("Você");
            else
                holder.nome.setText(usuarios.get(position).getNome());
        }

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
        return usuarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView foto;
        private TextView nome;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.perfilListImg);
            nome = itemView.findViewById(R.id.perfilListNome);
        }
    }
}
