package com.example.jamargym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jamargym.api.ApiClient;
import com.example.jamargym.api.ApiService;
import com.example.jamargym.request.LoginRequest;
import com.example.jamargym.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasukActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ApiService apiService; // Use ApiService instead of AuthService

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek apakah token sudah disimpan
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);

        if (token != null) {
            // Jika token ada, langsung ke BerandaActivity
            Intent intent = new Intent(MasukActivity.this, BerandaActivity.class);
            startActivity(intent);
            finish(); // Tutup activity login
        } else {
            // Jika token tidak ada, tampilkan layar login
            setContentView(R.layout.activity_masuk);
            initViews();
        }
    }

    // Inisialisasi tampilan
    private void initViews() {
        emailEditText = findViewById(R.id.textInputLoginEmail);
        passwordEditText = findViewById(R.id.textInputLoginPassword);
        loginButton = findViewById(R.id.btnMasuk);

        // Initialize ApiService using ApiClient
        apiService = ApiClient.getClient().create(ApiService.class);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                login(email, password);
            } else {
                Toast.makeText(MasukActivity.this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode untuk menyimpan token di SharedPreferences
    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }

    // Metode untuk login
    private void login(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Periksa statusCode dari loginResponse
                    if (loginResponse.getStatusCode() == 200) {
                        String token = loginResponse.getData().getToken();
                        if (token != null) {
                            saveToken(token); // Simpan token
                            Toast.makeText(MasukActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                            // Arahkan ke BerandaActivity
                            Intent intent = new Intent(MasukActivity.this, BerandaActivity.class);
                            startActivity(intent);
                            finish(); // Menutup activity login agar tidak bisa kembali ke login
                        } else {
                            Toast.makeText(MasukActivity.this, "Login gagal: Token tidak valid", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MasukActivity.this, "Login gagal: " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 401) {
                    // Jika status kode 401, berarti email atau password salah
                    Toast.makeText(MasukActivity.this, "Email atau password salah", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MasukActivity.this, "Login gagal, coba lagi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MasukActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
