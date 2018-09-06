package com.example.viedmapp.ocr.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.viedmapp.ocr.Model.Picture;
import com.example.viedmapp.ocr.R;

import java.util.ArrayList;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.PictureViewHolder> {
    private ArrayList<Picture> pictures;

    public AdapterRecyclerView(ArrayList<Picture> pictures){
        this.pictures = pictures;


    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_resultado,null,false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        holder.asignarDatos(pictures.get(position));

    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder{

        private TextView datoTexto;

        public PictureViewHolder(View itemView) {
            super(itemView);
            datoTexto = (TextView) itemView.findViewById(R.id.datoTexto);


        }

        public void asignarDatos(Picture picture) {
            datoTexto.setText(picture.getDatoTittle()+": "+picture.getDatoInfo());
        }
    }
}
