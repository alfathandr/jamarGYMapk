package com.example.jamargym;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.jamargym.api.ApiClient;
import com.example.jamargym.api.ApiService;
import com.example.jamargym.request.RegisterRequest;
import com.example.jamargym.response.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarActivity extends ComponentActivity {

    private TextInputEditText edtNama, edtEmail, edtPassword;
    private MaterialButton btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        // Initialize views
        edtNama = findViewById(R.id.textInputDaftarNama);
        edtEmail = findViewById(R.id.textInputDaftarEmail);
        edtPassword = findViewById(R.id.textInputDaftarPassword);
        btnDaftar = findViewById(R.id.btnDaftar);

        // Adjust padding for window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up button click listener
        btnDaftar.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String nama = edtNama.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Validate input fields
        if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the API service
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Create the register request
        RegisterRequest registerRequest = new RegisterRequest(nama, email, password);

        // Make API call
        Call<RegisterResponse> call = apiService.register(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    Toast.makeText(DaftarActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    // Handle success, e.g., navigate to login activity
                } else {
                    Toast.makeText(DaftarActivity.this, "Pendaftaran gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(DaftarActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
