package com.gorun.kelompok7.runningapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gorun.kelompok7.runningapp.R;

public class DetailDiriActivity extends AppCompatActivity {
    EditText etBerat, etTinggi;
    Button buttonLanjut;
    Double berat, tinggi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_diri);

        etBerat = findViewById(R.id.berat);
        etTinggi = findViewById(R.id.tinggi);
        buttonLanjut = findViewById(R.id.buttonLanjut);

        buttonLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBerat.getText().toString().isEmpty()) {
                    etBerat.setError("Isi berat badan Anda");
                    return;
                } else berat = Double.parseDouble(etBerat.getText().toString());

                if (etTinggi.getText().toString().isEmpty()){
                    etTinggi.setError("Isi tinggi badan Anda");
                    return;
                } else tinggi = Double.parseDouble(etTinggi.getText().toString());

                Intent intent = new Intent(DetailDiriActivity.this, MainActivity.class);
                intent.putExtra("berat", berat);
                intent.putExtra("tinggi", tinggi);
                startActivity(intent);
            }
        });
    }
}
