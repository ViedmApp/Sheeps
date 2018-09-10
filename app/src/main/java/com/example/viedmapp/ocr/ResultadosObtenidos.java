package com.example.viedmapp.ocr;

import android.content.Intent;
import android.media.Rating;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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

    TextView mostrarId;
    RatingBar puntuacion;
    LinearLayout infoCheck;
    ConstraintLayout layoutBotones;
    ImageView checkImage;

    String idMadre;
    String idPadre;
    static private int cant;
    static private String[] dataBase;
    static private String modo;

    RecyclerView picturesRecycler;
    ArrayList<Picture> pictures = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_obtenidos);

        picturesRecycler = (RecyclerView) findViewById(R.id.datos_recycler);
        picturesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        picturesRecycler.setVerticalScrollBarEnabled(true);

        recibirDatos();

        AdapterRecyclerView adapterRecyclerView = new AdapterRecyclerView(pictures);

        picturesRecycler.setAdapter(adapterRecyclerView);
    }


    private void recibirDatos(){

        puntuacion = findViewById(R.id.ratingBar);
        checkImage = findViewById(R.id.imgCheck);
        mostrarId = findViewById(R.id.text_titulo);
        infoCheck =  findViewById(R.id.linearCheck);
        layoutBotones = (ConstraintLayout) findViewById(R.id.layout_botones);


        Bundle extras = getIntent().getExtras();
        cant = extras.getInt("valores");
        dataBase = extras.getStringArray("database");
        modo = extras.getString("modo");

        String datakey = "";
        String tittlekey = "";

        puntuacion.setVisibility(View.INVISIBLE);
        infoCheck.setVisibility(View.INVISIBLE);
        layoutBotones.setVisibility(View.INVISIBLE);


        for(int i=0; i<cant; i++){
            tittlekey = tittlekey.concat("t");
            datakey = datakey.concat("d");
            String info = extras.getString(datakey);
            String tittle = extras.getString(tittlekey);
            String lac = tittle.substring(0,2);

            if(!(tittle.equalsIgnoreCase("rating")) && !(tittle.equalsIgnoreCase("id")) && !(lac.equalsIgnoreCase("la"))){
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
                }else if(lac.equalsIgnoreCase("la")){
                    if(info.substring(0,1).equalsIgnoreCase("s")){
                        checkImage.setImageResource(R.drawable.ic_check_box_black_24dp);
                    }
                }
            }

        }

        if(modo.equalsIgnoreCase("p")){
            infoCheck.setVisibility(View.VISIBLE);
        }
        if(modo.equalsIgnoreCase("g")) {
            puntuacion.setVisibility(View.VISIBLE);
            layoutBotones.setVisibility(View.VISIBLE);
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

        intent2.putExtra("valores", salto);
        intent2.putExtra("database", datos);
        intent2.putExtra("salto",salto);
        intent2.putExtra("modo", modo);

        startActivity(intent2);


    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_back:
                onBackPressed();
                finish();
                /*Intent intent2 = new Intent(ResultadosObtenidos.this, ResultadosObtenidos.class);
                startActivity(intent2);
                finish();*/
                break;
            case R.id.btn_busMadre:
                System.out.println("data1:"+ dataBase[0]);
                busqueda(idMadre, dataBase, cant);
                System.out.println("data2:"+ dataBase[0]);
                break;
            case R.id.btn_busPadre:
                busqueda(idPadre, dataBase, cant);
                break;
        }

    }


    public void goScan(View v){
        Intent myintent = new Intent(ResultadosObtenidos.this, MainActivity.class);
        startActivity(new Intent(getBaseContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }


    /*public void onBackPressed(){
        Intent intent2 = new Intent(ResultadosObtenidos.this, ResultadosObtenidos.class);
        startActivity(intent2);
    }*/
}
