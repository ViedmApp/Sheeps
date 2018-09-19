package com.example.viedmapp.ocr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

    static private String[] nombreHojas = new String[3];
    static private String cantCol,datos;
    static private String[] datos2;
    static private String name;
    static private int index = -1;
    static private String[] urls ={"1m9qVR6aTM3mOcwOGQKXEI9fBE9VrDOGOYViaQCNGIkQ","16kIxdrDC4or1rDjsH8iLRTKWmJcTnKJvSsggLSHzWwA","1IxejOVQWO5ucH-pFPAGi71Yoo3IjWjNCvxe4xPi5Xew"};
    //static private int cantCol2;
    Spinner listaCampos;
    ArrayList<String> campos = new ArrayList<>();
    Context context = this;
    final int REQUEST_ACCES_WRITE = 0;
    final int RequestCameraPermissionID = 1001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_ACCES_WRITE);


        nombreHojas[0] = "";
        nombreHojas[1] = "";
        nombreHojas[2] = "";
        SharedPreferences sharprefS = getSharedPreferences("sf",context.MODE_PRIVATE);


        listaCampos = findViewById(R.id.isSpiner);

        campos.add("Seleccionar predio");
        campos.add("Colico");
        campos.add("Chiriuco");
        campos.add("Quillayes");







        listaCampos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    index = position-1;
                    //Hacer cambio de background dependiendo la posicion;
                    String campSelect = listaCampos.getSelectedItem().toString();
                    System.out.println("seleccion: " + campSelect);
                    name = campSelect;
                    Toast.makeText(PantallaPrincipal.this,name+" seleccionado", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    index = position-1;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adap = new ArrayAdapter<>(this, R.layout.spinner_item_predio,campos);

        listaCampos.setAdapter(adap);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestCameraPermissionID: {

                break;
            }
            case REQUEST_ACCES_WRITE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permiso aceptado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
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
            nombreHojas = jsonObject.getString("nombreHojas").split("¡");

            for (int i = 0; i < 3; i++)
            {
                cantCol = jsonObject.getString(nombreHojas[i]+" cols");
                datos = jsonObject.getString(nombreHojas[i]+"");
                File file = new File(Environment.getExternalStorageDirectory(), nombreHojas[i].substring(0,1)+".txt");
                file.createNewFile();
                OutputStreamWriter fout1 = new OutputStreamWriter(new FileOutputStream(file));
                fout1.write(cantCol+"\n" +datos);
                fout1.close();
            }

            SharedPreferences sf = getSharedPreferences("credenciales",context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sf.edit();

            editor1.putString("sheet1", nombreHojas[0].substring(0,1));
            editor1.putString("sheet2", nombreHojas[1].substring(0,1));
            editor1.putString("sheet3", nombreHojas[2].substring(0,1));
            editor1.commit();


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

            SharedPreferences sf = getSharedPreferences("credenciales",context.MODE_PRIVATE);
            String sheet1 = sf.getString("sheet1","");
            String sheet2 = sf.getString("sheet2", "");
            String sheet3 = sf.getString("sheet3", "");


            startActivity(myintent);
            finish();
        }
    }

    public void onclick(View view) {
        if(view.getId() == R.id.btn_descargar){
            if(isOnline(getApplicationContext())){
                if (index!=-1)
                {
                    Toast.makeText(this, "Descargando " + name, Toast.LENGTH_SHORT).show();
                    DataRequest dataRequest = new DataRequest(this);
                    dataRequest.delegate = this;
                    dataRequest.execute("https://script.google.com/macros/s/AKfycbx_3i4ladoSuoap8_1DIWGfA7JmuQoLAQqs3krc-5Gi6wnVkgY/exec?sId=" + urls[index]);
                    SharedPreferences sharpref = getPreferences(context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharpref.edit();

                    editor.putString("hojaGuardada", name);

                    editor.commit();
                }
                else
                {
                    Toast.makeText(this,"Seleccione un predio para ser descargado",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"No existe conexión a internet, no puedes actualizar la base de datos", Toast.LENGTH_SHORT).show();
            }
        }
    }





}
