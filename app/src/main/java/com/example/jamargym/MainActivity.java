package com.example.jamargym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnMasuk;
    private Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);  // Enable edge-to-edge
        setContentView(R.layout.activity_main);

        // Referensi ke tombol
        btnMasuk = findViewById(R.id.btnLandingMasuk);
        btnDaftar = findViewById(R.id.btnLandingDaftar);

        // Menangani klik tombol "MASUK"
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BerandaActivity.class);
                startActivity(intent);
            }
        });

        // Menangani klik tombol "DAFTAR"
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DaftarActivity.class);
                startActivity(intent);
            }
        });

        // Atur padding untuk tampilan utama
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}