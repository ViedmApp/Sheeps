package com.example.viedmapp.ocr;

import android.content.Intent;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.viedmapp.ocr.Adapter.AdapterRecyclerView;
import com.example.viedmapp.ocr.Model.Picture;
import com.google.android.gms.vision.text.Line;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class ResultadosObtenidos extends AppCompatActivity {

    RatingBar puntuacion;
    TextView mostrarId;

    String idMadre;
    String idPadre;
    static private int cant;
    static private String[] dataBase;

    RecyclerView picturesRecycler;
    ArrayList<Picture> pictures = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_obtenidos);


        picturesRecycler = (RecyclerView) findViewById(R.id.datos_recycler);
        picturesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));




        recibirDatos();
        AdapterRecyclerView adapterRecyclerView = new AdapterRecyclerView(pictures);

        picturesRecycler.setAdapter(adapterRecyclerView);





    }


    private void recibirDatos(){

        puntuacion = findViewById(R.id.ratingBar);
        mostrarId = findViewById(R.id.text_titulo);


        Bundle extras = getIntent().getExtras();
        cant = extras.getInt("valores");
        dataBase = extras.getStringArray("database");

        String datakey = "";
        String tittlekey = "";

        for(int i=0; i<cant; i++){
            tittlekey = tittlekey.concat("t");
            datakey = datakey.concat("d");
            String info = extras.getString(datakey);
            String tittle = extras.getString(tittlekey);

            if(!(tittle.equalsIgnoreCase("rating")) && !(tittle.equalsIgnoreCase("id"))){
                pictures.add(new Picture(tittle, info));
                if(tittle.equalsIgnoreCase("madre")){
                    idMadre = info;
                }else if(tittle.equalsIgnoreCase("padre")){
                    idPadre = info;
                }
            }else{
                if(tittle.equalsIgnoreCase("rating")){
                    puntuacion.setProgress(Integer.valueOf(info));
                }else if(tittle.equalsIgnoreCase("id")){
                    mostrarId.append(info);
                }
            }

        }

    }


    public void onClick(View view) {
        if(view.getId() == R.id.button_back){
            Intent intent2 = new Intent(ResultadosObtenidos.this, MainActivity.class);
            startActivity(intent2);
            finish();
        }if(view.getId() == R.id.btn_busMadre){
            busqueda(idMadre, dataBase, cant);
        }if(view.getId() == R.id.btn_busPadre){
            busqueda(idPadre, dataBase, cant);
        }
    }

    private void busqueda(String busco, String[] datos, int salto) {
        int i = salto;

        while (i < datos.length && !(datos[i].equals(busco))) {
            i = i + salto;
        }
        if (i >= datos.length) {
            Toast.makeText(ResultadosObtenidos.this, "No se encuentra registrado en la base de datos", Toast.LENGTH_SHORT).show();
        } else if (datos[i].equals(busco)) {
            mostrarResultado(busco, datos, i, salto);
        }
    }

    private void mostrarResultado(String result, String[] datos, int i, int salto ) {
        String[] resultados = new String[salto];
        String[] titulos = new String[salto];
        Intent intent2 = new Intent(ResultadosObtenidos.this, ResultadosObtenidos.class);
        String datakey = "";
        for(int j = 0; j<salto; j++){
            datakey = datakey.concat("d");
            resultados[j] = datos[i];
            intent2.putExtra(datakey, resultados[j]);
            i++;
        }

        String tittlekey = "";
        for (int k = 0; k < salto; k++)
        {
            tittlekey = tittlekey.concat("t");
            titulos[k] = datos[k];
            intent2.putExtra(tittlekey,datos[k]);
        }
        System.out.println(salto);

        intent2.putExtra("valores", salto);
        intent2.putExtra("database", datos);
        intent2.putExtra("salto",salto);

        startActivity(intent2);
        finish();
    }


    public void onBackPressed(){
        Intent intent2 = new Intent(ResultadosObtenidos.this, MainActivity.class);
        startActivity(intent2);
        finish();
    }
}
