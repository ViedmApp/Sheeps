package com.example.viedmapp.ocr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
    SurfaceView camara;
    TextView mostrar;
    CameraSource cameraSource;
    EditText editar;
    ImageView cancelar;

    EditText codEnviado;
    Switch switchC;
    ConstraintLayout bg;

    ImageView enviarGen;
    ImageView enviarProd;
    ImageView enviarRepro;

    private static String modo = "gen";

    final int RequestCameraPermissionID = 1001;

    private static String hojas1;
    private static String hojas2;
    private static String hojas3;

    Context context = this;


    //datos a enviar:
    String edad = "2";
    String madre = "2121";
    String padre = "5154";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(camara.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obteniendo el modo en que se ingreso (reproductivo, genealogico, productivo)


        switchC = (Switch) findViewById(R.id.switchOff);

        camara = findViewById(R.id.surface_view);
        mostrar =  findViewById(R.id.text_view);
        codEnviado = findViewById(R.id.editText);

        TextRecognizer textoReconocido = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textoReconocido.isOperational()) {
            Log.w("MainActivity", "Las dependencias del detector aún no están disponibles");
        } else {

            cameraSource = new CameraSource.Builder(getApplicationContext(), textoReconocido)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(0.00005f)
                    .setAutoFocusEnabled(true)
                    .build();
            camara.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(camara.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

            textoReconocido.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(final Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    System.out.println(items.size());

                    if (items.size() != 0 && detections.detectorIsOperational()) {

                        mostrar.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @SuppressLint("NewApi")
                            @Override
                            public void run() {
                                cameraSource.stop();
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); ++i) {
                                    TextBlock item = items.valueAt(0);
                                    stringBuilder.append(item.getValue());
                                    System.out.println(stringBuilder.toString());
                                }

                                mostrar.setText(stringBuilder.toString());
                                mostrarVentanaRevision(stringBuilder.toString());
                            }
                        });

                    }

                }
            });
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void mostrarVentanaRevision(String result){
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.ventana_mensaje, (ConstraintLayout) findViewById(R.id.layout_mensaje));

        editar =  dialogLayout.findViewById(R.id.edit_text);
        cancelar = dialogLayout.findViewById(R.id.button_cancel);

        enviarGen = dialogLayout.findViewById(R.id.btngen);
        enviarProd = dialogLayout.findViewById(R.id.btnprod);
        enviarRepro = dialogLayout.findViewById(R.id.btnrep);

        editar.setText(result);
        editar.setSelection(editar.getText().length());

        /*Bundle extras = getIntent().getExtras();
        hojas1 = extras.getString("hoja0");
        hojas2 = extras.getString("hoja1");
        hojas3 = extras.getString("hoja2");*/

        SharedPreferences sf = getSharedPreferences("credenciales",context.MODE_PRIVATE);
        String sheet1 = sf.getString("sheet1","");
        String sheet2 = sf.getString("sheet2", "");
        String sheet3 = sf.getString("sheet3", "");

        hojas1 = sheet1;
        hojas2 = sheet2;
        hojas3 = sheet3;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        //Botones Enviar y Cancelar

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.button_cancel){
                    reiniciarActividad(MainActivity.this);
                }
            }
        });

        enviarGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btngen){
                    try {
                        for(int i=0; i<3; i++){
                            if(hojas1.substring(0,3).equalsIgnoreCase("gen")){
                                modo = hojas1;
                            }
                            if(hojas2.substring(0,3).equalsIgnoreCase("gen")){
                                modo = hojas2;
                            }
                            if(hojas3.substring(0,3).equalsIgnoreCase("gen")){
                                modo = hojas3;
                            }
                        }

                        File file2;
                        file2 = new File(Environment.getExternalStorageDirectory(), modo+".txt");
                        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
                        String busco = editar.getText().toString();
                        busqueda(busco, fin);
                        fin.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    //cerrar teclado
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editar.getWindowToken(), 0);
                    //abrir ventana con los resultados

                }
            }
        });

        enviarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btnprod){
                    try {
                        for(int i=0; i<3; i++){
                            if(hojas1.substring(0,3).equalsIgnoreCase("pro")){
                                modo = hojas1;
                            }
                            if(hojas2.substring(0,3).equalsIgnoreCase("pro")){
                                modo = hojas2;
                            }
                            if(hojas3.substring(0,3).equalsIgnoreCase("pro")){
                                modo = hojas3;
                            }
                        }

                        File file2;
                        file2 = new File(Environment.getExternalStorageDirectory(), modo+".txt");
                        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
                        String busco = editar.getText().toString();
                        busqueda(busco, fin);
                        fin.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editar.getWindowToken(), 0);
                }
            }
        });
        enviarRepro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btnrep){
                    try {
                        for(int i=0; i<3; i++){
                            if(hojas1.substring(0,3).equalsIgnoreCase("rep")){
                                modo = hojas1;
                            }
                            if(hojas2.substring(0,3).equalsIgnoreCase("rep")){
                                modo = hojas2;
                            }
                            if(hojas3.substring(0,3).equalsIgnoreCase("rep")){
                                modo = hojas3;
                            }
                        }

                        File file2;
                        file2 = new File(Environment.getExternalStorageDirectory(), modo+".txt");
                        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
                        String busco = editar.getText().toString();
                        busqueda(busco, fin);
                        fin.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editar.getWindowToken(), 0);
                }
            }
        });
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }

    private void busqueda(String busco, BufferedReader fin) throws IOException {
        int salto = Integer.parseInt(fin.readLine());
        String[] datos = fin.readLine().trim().split("¡");
        for (int i = 0; i < datos.length; i++)
        {
            System.out.println(datos[i]);
        }
        int i = salto;
        while(i < datos.length && !(datos[i].equals(busco))){
            i = i+salto;
        }
        if(i >= datos.length) {
            Toast.makeText(MainActivity.this, "No se encuentra registrado en la base de datos", Toast.LENGTH_SHORT).show();
        }
        else if(datos[i].equals(busco)){
            mostrarResultado(busco, datos, i, salto);
        }


    }

    private void mostrarResultado(String result, String[] datos, int i, int salto ) {

        String[] resultados = new String[salto];
        String[] titulos = new String[salto];
        Intent intent2 = new Intent(MainActivity.this, ResultadosObtenidos.class);
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
        intent2.putExtra("modo", modo);

        startActivity(intent2);

    }


    static void reiniciarActividad(Activity actividad){
        Intent intent = new Intent();
        intent.setClass(actividad, actividad.getClass());
        actividad.startActivity(intent);
        actividad.finish();
    }


    public void onClick(View view) {
        if(view.getId() == R.id.btn_enviartext){
            if (!codEnviado.getText().toString().isEmpty()) {
                cameraSource.stop();
                System.out.println(codEnviado.getText().toString());
                mostrarVentanaRevision(codEnviado.getText().toString());
                //cerrar teclado
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(codEnviado.getWindowToken(), 0);
                //abrir ventana con los resultados
                codEnviado.setText("");

            }
            else{
                Toast.makeText(MainActivity.this,
                        "No ha ingresado nada",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void OffCam(View view){
        if(view.getId() == R.id.switchOff){
            if(switchC.isChecked()){
                cameraSource.stop();
                camara = findViewById(R.id.surface_view);
                camara.setBackground(getDrawable(R.drawable.bgcam));
            }else{
                reiniciarActividad(MainActivity.this);
            }

        }
    }
}
