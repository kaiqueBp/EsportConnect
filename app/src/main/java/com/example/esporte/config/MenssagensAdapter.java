package com.example.esporte.config;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
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

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class MenssagensAdapter extends RecyclerView.Adapter<MenssagensAdapter.MyViewHolder> {
    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;
    private FirebaseAuth auth;
    private String loc = "";
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
        Double lati = m.getLatitude();
        Double longi = m.getLongitude();

        if(!m.getNome().isEmpty()){
            holder.nome.setText(m.getNome());
        }else holder.nome.setVisibility(View.GONE);

        Context ctx = context;
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        if(img != null){
            Uri url = Uri.parse(img);
            Glide.with(context).load(url).into(holder.imagem);
            holder.mensagem.setVisibility(View.GONE);
            holder.localizacao.setVisibility(View.GONE);
            holder.mapa.setVisibility(View.GONE);

            // Adiciona o clique para exibir a imagem em tela cheia
            holder.imagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Verifica se o contexto é uma Activity
                    if (context instanceof Activity) {
                        Dialog dialog = new Dialog((Activity) context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.layout_image);
                        ImageView fullImage = dialog.findViewById(R.id.imageFullScreen);

                        // Carregar a imagem no dialog
                        Glide.with(context).load(url).into(fullImage);

                        // Fechar o dialog ao clicar na imagem
                        fullImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    } else {
                        Log.e("DialogError", "Context is not an Activity");
                    }
                }
            });
        }
        if(msg != null && img == null && lati == null && longi == null) {
            holder.mensagem.setText(msg);
            holder.imagem.setVisibility(View.GONE);
            holder.localizacao.setVisibility(View.GONE);
            holder.mapa.setVisibility(View.GONE);
        }
        if(lati != null && longi != null){
            //Configuration.getInstance().setUserAgentValue(getPackageName());
            holder.mapa.setTileSource(TileSourceFactory.MAPNIK);
            Configuration.getInstance().setCacheMapTileCount((short) 9);  // Converter para short
            Configuration.getInstance().setCacheMapTileOvershoot((short) 2);  // Converter para short
            holder.mapa.setMultiTouchControls(true);
            IMapController mapController = holder.mapa.getController();
            mapController.setZoom(15.0);
            GeoPoint geoPoint = new GeoPoint(lati, longi);

            // Centralizar o mapa na localização
            mapController.setCenter(geoPoint);
            holder.mapa.getOverlays().clear();
            // Adicionar um marcador na posição atual
            Marker marker = new Marker(holder.mapa);
            marker.setPosition(geoPoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            holder.mapa.getOverlays().add(marker);
            holder.mapa.invalidate();
            loc = "https://www.google.com/maps/search/?api=1&query=" + lati.toString() + "," + longi.toString();
            holder.localizacao.setText(loc);
            holder.imagem.setVisibility(View.GONE);
            holder.mensagem.setVisibility(View.GONE);
        }
        holder.localizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(loc));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Log.e("IntentDebug", "No application can handle this intent");
                }
            }
        });
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
        private TextView mensagem, localizacao, nome;
        private MapView mapa;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.imgChat);
            mensagem = itemView.findViewById(R.id.textChat);
            localizacao = itemView.findViewById(R.id.textLoc);
            mapa = itemView.findViewById(R.id.mapView);
            nome = itemView.findViewById(R.id.idNomeExibir);

        }
    }

}
