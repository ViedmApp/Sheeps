package com.example.viedmapp.ocr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class PantallaPrincipal extends AppCompatActivity {

    static private String datos;
    static private int cantCol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Button btn_ingresar = (Button) findViewById(R.id.btn_leer);
        Button btn_descargar = (Button) findViewById(R.id.btn_descargar);


    }

    public void onClick(View view) {
        if(view.getId() == R.id.btn_leer){
            Intent myintent = new Intent(PantallaPrincipal.this, MainActivity.class);
            startActivity(myintent);
            finish();
        }
    }

    public void onclick(View view) {
        if(view.getId() == R.id.btn_descargar){


        }
    }


   /* public void onClick(View view) {
        if(view.getId() == R.id.btn_enter){
            Intent myintent = new Intent (PantallaPrincipal.this, MainActivity.class);
            startActivity(myintent);
            finish();
        }
    }*/
}
