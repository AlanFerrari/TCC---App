package br.com.etecia.meus_direitos;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

import br.com.etecia.meus_direitos.objetos.Advogados;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Advogados> mCartao;

    public RecyclerAdapter(Context mContext, List<Advogados> mCartao) {
        this.mContext = mContext;
        this.mCartao = mCartao;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_lista_advogado, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(mContext).load(mCartao.get(position).getImagem()).into(holder.imagemPerfil);
        holder.nomeAdvogado.setText(mCartao.get(position).getNomeAdvogado());
        holder.estado.setText(mCartao.get(position).getEstado());
        holder.cidade.setText(mCartao.get(position).getCidade());
        holder.area_atuacao.setText(mCartao.get(position).getArea_atuacao());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Perfil_Advogados_Cli.class);

                intent.putExtra("imagem", mCartao.get(position).getImagem());
                intent.putExtra("nome", mCartao.get(position).getNomeAdvogado());
                intent.putExtra("estado", mCartao.get(position).getEstado());
                intent.putExtra("cidade", mCartao.get(position).getCidade());
                intent.putExtra("area_atuacao", mCartao.get(position).getArea_atuacao());

                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartao.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemPerfil;
        TextView nomeAdvogado;
        TextView estado;
        TextView cidade;
        TextView area_atuacao;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagemPerfil = itemView.findViewById(R.id.imagem_perfil);
            nomeAdvogado = itemView.findViewById(R.id.nomeAdvogado);
            estado = itemView.findViewById(R.id.estado);
            cidade = itemView.findViewById(R.id.cidade);
            area_atuacao = itemView.findViewById(R.id.nome_area_atuacao);
            cardView = itemView.findViewById(R.id.cardViewAdvogados);
        }
    }
}