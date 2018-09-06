package com.example.viedmapp.ocr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PantallaPrincipal extends AppCompatActivity implements AsyncResponse{

    static private String datos;
    static private String cantCol;
    static private String[] datos2;
    static private String name;
    //static private int cantCol2;
    Spinner listaCampos;
    ArrayList<String> campos = new ArrayList<>();
    Context context = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);


        SharedPreferences sharprefS = getSharedPreferences("sf",context.MODE_PRIVATE);


        listaCampos = findViewById(R.id.isSpiner);

        campos.add("Seleccionar hoja");

        if(isOnline(getApplicationContext())){
            DataRequest02 dataRequest = new DataRequest02(this);
            dataRequest.delegate = this;
            dataRequest.execute("https://script.google.com/macros/s/AKfycbxh_lDr1HRXLfqZtrncHCpWQTFEtJg1szav00vTr4cQDpvqYkM/exec");

            ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,campos);
            listaCampos.setAdapter(adap);

        }else{
            SharedPreferences sharpref = getPreferences(context.MODE_PRIVATE);
            String hojaGuardada = sharpref.getString("hojaGuardada","");
            Toast.makeText(getApplicationContext(),"No existe conexión a internet. Estas en "+hojaGuardada, Toast.LENGTH_SHORT).show();
        }

        Button btn_ingresar = (Button) findViewById(R.id.btn_leer);
        Button btn_descargar = (Button) findViewById(R.id.btn_descargar);



        listaCampos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    String campSelect = listaCampos.getSelectedItem().toString();
                    System.out.println("seleccion: " + campSelect);
                    name = campSelect;
                    Toast.makeText(PantallaPrincipal.this,name+" seleccionado", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
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

    @Override
    public void processFinish02(JSONObject js) {
        try
        {

            datos2 = js.getString("dato").split("¡");
            for(int i= 0; i<datos2.length; i++){
                campos.add(datos2[i]);
            }
        }catch (Exception e){
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
            Toast.makeText(this, "Descargando "+name, Toast.LENGTH_SHORT).show();
            DataRequest dataRequest = new DataRequest(this);
            dataRequest.delegate = this;
            dataRequest.execute("https://script.google.com/macros/s/AKfycbx_3i4ladoSuoap8_1DIWGfA7JmuQoLAQqs3krc-5Gi6wnVkgY/exec?name="+name);
            SharedPreferences sharpref = getPreferences(context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharpref.edit();
            editor.putString("hojaGuardada",name);
            editor.commit();

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
