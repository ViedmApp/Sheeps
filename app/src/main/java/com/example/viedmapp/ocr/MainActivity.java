package com.example.viedmapp.ocr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    SurfaceView camara;
    TextView mostrar;
    CameraSource cameraSource;
    EditText editar;
    TextView cancelar;
    Button enviar;
    EditText codEnviado;
    Switch switchC;
    ConstraintLayout bg;
    final int RequestCameraPermissionID = 1001;

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
    private void mostrarVentanaRevision(final String result){
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.ventana_mensaje, (ConstraintLayout) findViewById(R.id.layout_mensaje));

        editar =  dialogLayout.findViewById(R.id.edit_text);
        cancelar = dialogLayout.findViewById(R.id.button_cancel);
        enviar = dialogLayout.findViewById(R.id.button_enviar);
        editar.setText(result);
        editar.setSelection(editar.getText().length());

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
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.button_enviar){
                    /*try {
                        File file2;
                        file2 = new File(Environment.getExternalStorageDirectory(), "database.txt");
                        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));

                        fin.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }*/
                    //cerrar teclado
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editar.getWindowToken(), 0);
                    //abrir ventana con los resultados
                    mostrarResultado(editar.getText().toString());
                }
            }
        });
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }

    private void mostrarResultado(String result) {
        Intent intent2 = new Intent(MainActivity.this, ResultadosObtenidos.class);
        intent2.putExtra("tittle", result);
        intent2.putExtra("d0", edad);
        intent2.putExtra("d1", madre);
        intent2.putExtra("d2", padre);

        startActivity(intent2);
        finish();
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
                //cerrar teclado
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(codEnviado.getWindowToken(), 0);
                mostrarResultado(codEnviado.getText().toString());
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
