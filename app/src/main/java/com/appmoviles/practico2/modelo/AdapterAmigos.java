package com.appmoviles.practico2.modelo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appmoviles.practico2.R;

import java.util.ArrayList;

public class AdapterAmigos extends RecyclerView.Adapter<AdapterAmigos.CustomViewHolder> {

    ArrayList<Amigo> data;

    public void showAllAmigos(ArrayList<Amigo> allAmigos) {
        for (int i = 0; i < allAmigos.size(); i++) {
            if (!data.contains(allAmigos.get(i))) data.add(allAmigos.get(i));
        }
        notifyDataSetChanged();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;

        public CustomViewHolder(LinearLayout v) {
            super(v);
            root = v;
        }
    }

    public AdapterAmigos() {
        data = new ArrayList<>();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.renglon_amigos, parent, false);
        CustomViewHolder vh = new CustomViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        ((TextView) holder.root.findViewById(R.id.renglon_amigo_nombre)).setText(data.get(position).getNombre());
        ((TextView) holder.root.findViewById(R.id.renglon_amigo_telefono)).setText(data.get(position).getTelefono());
        holder.root.findViewById(R.id.renglon_amigo_btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(data.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    // Observer
    public interface OnItemClickListener{
        void onItemClick(Amigo amigo);
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
