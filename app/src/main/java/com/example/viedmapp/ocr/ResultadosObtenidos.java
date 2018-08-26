package com.example.viedmapp.ocr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultadosObtenidos extends AppCompatActivity {
    TextView edad;
    TextView padre;
    TextView madre;
    TextView titulo;
    LinearLayout pantalla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pantalla = findViewById(R.id.layout_resultados);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_obtenidos);
        recibirDatos();


    }
    private void recibirDatos(){
        edad =  findViewById(R.id.edad);
        padre =  findViewById(R.id.padre);
        madre =  findViewById(R.id.madre);
        titulo = findViewById(R.id.titulo);

        Bundle extras = getIntent().getExtras();
        String num = extras.getString("tittle");
        String setEdad = extras.getString("d0");
        String setMadre = extras.getString("d1");
        String setPadre = extras.getString("d2");

        titulo.append(num);
        edad.append(" " + setEdad);
        madre.append(" " + setMadre);
        padre.append(" " +setPadre);
    }

    public void onClick(View view) {
        if(view.getId() == R.id.button_back){
            Intent intent2 = new Intent(ResultadosObtenidos.this, MainActivity.class);
            startActivity(intent2);
            finish();
        }
    }
    public void onBackPressed() {
        Intent intent2 = new Intent(ResultadosObtenidos.this, MainActivity.class);
        startActivity(intent2);
        finish();
    }
}
