package com.example.viedmapp.ocr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText codEnviado;

    Button enviarGen;
    Button enviarProd;
    Button enviarRepro;

    private static String modo = "g";

    private static String hojas1;
    private static String hojas2;
    private static String hojas3;

    Context context = this;


    //datos a enviar:


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obteniendo el modo en que se ingreso (reproductivo, genealogico, productivo)

        codEnviado = findViewById(R.id.editText);
        codEnviado.setText("");
        mostrarVentanaRevision();


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void mostrarVentanaRevision() {
        String result;
        enviarGen = findViewById(R.id.btngen);
        enviarProd = findViewById(R.id.btnprod);
        enviarRepro = findViewById(R.id.btnrep);

        result = codEnviado.getText().toString();

        codEnviado.setText(result);
        codEnviado.setSelection(codEnviado.getText().length());


        SharedPreferences sf = getSharedPreferences("credenciales", context.MODE_PRIVATE);
        String sheet1 = sf.getString("sheet1", "");
        String sheet2 = sf.getString("sheet2", "");
        String sheet3 = sf.getString("sheet3", "");

        hojas1 = sheet1;
        hojas2 = sheet2;
        hojas3 = sheet3;

    }

    private void busqueda(String busco, BufferedReader fin) throws IOException {
        int salto = Integer.parseInt(fin.readLine());
        String[] datos = fin.readLine().trim().split("¡");
        for (int i = 0; i < datos.length; i++) {
            System.out.println(datos[i]);
        }
        int i = salto;
        while (i < datos.length && !(datos[i].equals(busco))) {
            i = i + salto;
        }
        if (i >= datos.length) {
            Toast.makeText(MainActivity.this, "No se encuentra registrado en la base de datos", Toast.LENGTH_SHORT).show();
        } else if (datos[i].equals(busco)) {
            mostrarResultado(busco, datos, i, salto);
        }


    }

    private void mostrarResultado(String result, String[] datos, int i, int salto) {
        String[] resultados = new String[salto];
        String[] titulos = new String[salto];
        Intent intent2 = new Intent(MainActivity.this, ResultadosObtenidos.class);
        String datakey = "";
        for (int j = 0; j < salto; j++) {
            datakey = datakey.concat("d");
            resultados[j] = datos[i];
            intent2.putExtra(datakey, resultados[j]);
            i++;
        }

        String tittlekey = "";
        for (int k = 0; k < salto; k++) {
            tittlekey = tittlekey.concat("t");
            titulos[k] = datos[k];
            intent2.putExtra(tittlekey, datos[k]);
        }
        System.out.println(salto);

        intent2.putExtra("valores", salto);
        intent2.putExtra("database", datos);
        intent2.putExtra("salto", salto);
        intent2.putExtra("modo", modo);

        startActivity(intent2);

    }


    public void onClick(View view) {
        if (view.getId() == R.id.btngen) {
            try {
                for (int i = 0; i < 3; i++) {
                    if (hojas1.equalsIgnoreCase("g")) {
                        modo = hojas1;
                    }
                    if (hojas2.equalsIgnoreCase("g")) {
                        modo = hojas2;
                    }
                    if (hojas3.equalsIgnoreCase("g")) {
                        modo = hojas3;
                    }
                }

                File file2;
                file2 = new File(Environment.getExternalStorageDirectory(), modo + ".txt");
                BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
                String busco = codEnviado.getText().toString();
                busqueda(busco, fin);
                fin.close();
                //alertDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //cerrar teclado
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(codEnviado.getWindowToken(), 0);
            //abrir ventana con los resultados


        }

        if (view.getId() == R.id.btnprod) {
            try {
                for (int i = 0; i < 3; i++) {
                    if (hojas1.equalsIgnoreCase("p")) {
                        modo = hojas1;
                    }
                    if (hojas2.equalsIgnoreCase("p")) {
                        modo = hojas2;
                    }
                    if (hojas3.equalsIgnoreCase("p")) {
                        modo = hojas3;
                    }
                }

                File file2;
                file2 = new File(Environment.getExternalStorageDirectory(), modo + ".txt");
                BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
                String busco = codEnviado.getText().toString();
                busqueda(busco, fin);
                fin.close();
                //alertDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(codEnviado.getWindowToken(), 0);
        }

        if (view.getId() == R.id.btnrep) {
            try {
                for (int i = 0; i < 3; i++) {
                    if (hojas1.equalsIgnoreCase("r")) {
                        modo = hojas1;
                    }
                    if (hojas2.equalsIgnoreCase("r")) {
                        modo = hojas2;
                    }
                    if (hojas3.equalsIgnoreCase("r")) {
                        modo = hojas3;
                    }
                }

                File file2;
                file2 = new File(Environment.getExternalStorageDirectory(), modo + ".txt");
                BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
                String busco = codEnviado.getText().toString();
                busqueda(busco, fin);
                fin.close();
                //alertDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(codEnviado.getWindowToken(), 0);
        }



    }


   /* public void onClick(View view) {
        if (view.getId() == R.id.btn_enviartext) {
            if (!codEnviado.getText().toString().isEmpty()) {
                cameraSource.stop();
                System.out.println(codEnviado.getText().toString());
                mostrarVentanaRevision(codEnviado.getText().toString());
                //cerrar teclado
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(codEnviado.getWindowToken(), 0);
                //abrir ventana con los resultados
                codEnviado.setText("");

            } else {
                Toast.makeText(MainActivity.this, "No ha ingresado nada", Toast.LENGTH_SHORT).show();
            }

        }
    }*/


}