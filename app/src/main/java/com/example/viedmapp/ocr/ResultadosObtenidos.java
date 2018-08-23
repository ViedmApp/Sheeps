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
        edad = (TextView) findViewById(R.id.edad);
        padre = (TextView) findViewById(R.id.padre);
        madre = (TextView) findViewById(R.id.madre);
        titulo = (TextView) findViewById(R.id.titulo);
        pantalla = (LinearLayout) findViewById(R.id.layout_resultados);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_obtenidos);


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
