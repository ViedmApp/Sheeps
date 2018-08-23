package com.example.viedmapp.ocr;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PantallaPrincipal extends AppCompatActivity implements AsyncResponse {

    static private String datos;
    static private String cantCol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Button btn_ingresar = (Button) findViewById(R.id.btn_leer);
        Button btn_descargar = (Button) findViewById(R.id.btn_descargar);


    }

    @Override
    public void processFinish(JSONObject jsonObject) {
        try
        {
            datos = jsonObject.getString("dato");
            cantCol = jsonObject.getString("nCol");
            File file = new File(Environment.getExternalStorageDirectory(), "database.txt");
            file.createNewFile();
            OutputStreamWriter fout1 = new OutputStreamWriter(new FileOutputStream(file));
            fout1.write(cantCol+"\n" +datos);
            fout1.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

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
            DataRequest dataRequest = new DataRequest(this);
            dataRequest.delegate = this;
            dataRequest.execute("https://script.google.com/macros/s/AKfycbx_3i4ladoSuoap8_1DIWGfA7JmuQoLAQqs3krc-5Gi6wnVkgY/exec?idSheet=16kIxdrDC4or1rDjsH8iLRTKWmJcTnKJvSsggLSHzWwA");

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
