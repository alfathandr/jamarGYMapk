package com.example.jamargym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.jamargym.api.ApiClient;
import com.example.jamargym.api.ApiService;
import com.example.jamargym.request.UserUpdateRequest;
import com.example.jamargym.response.BrandResponse;
import com.example.jamargym.response.UserResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilActivity extends AppCompatActivity {

    private TextView textNamaAplikasi;
    private TextView textNameUser;
    private TextView textEmailUser;
    private TextInputEditText textInputNameUser;
    private TextInputEditText textInputEmailUser;
    private TextInputEditText textInputAddressUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek token terlebih dahulu sebelum setContentView
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", "");

        if (authToken.isEmpty()) {
            // Token tidak ada, arahkan ke halaman login
            Intent intent = new Intent(ProfilActivity.this, MasukActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_profil);

        // Set up window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textNamaAplikasi = findViewById(R.id.textNamaAplikasi);
        textNameUser = findViewById(R.id.textNameUser);
        textEmailUser = findViewById(R.id.textEmailUser);

        textInputNameUser = findViewById(R.id.textInputUpdateNameUser);
        textInputEmailUser = findViewById(R.id.textInputUpdateEmailUser);
        textInputAddressUser = findViewById(R.id.textInputUpdateAddressUser);

        Button buttonUpdateUser = findViewById(R.id.buttonUpdateUser);
        Button buttonKeluar = findViewById(R.id.buttonKeluar);

        buttonUpdateUser.setOnClickListener(v -> updateUser(authToken));
        buttonKeluar.setOnClickListener(v -> logout());

        // Fetch data from API
        fetchUserData(authToken);
        fetchBrandData();
    }

    private void updateUser(String authToken) {
        String name = textInputNameUser.getText().toString().trim();
        String email = textInputEmailUser.getText().toString().trim();
        String address = textInputAddressUser.getText().toString().trim();

        // Password bisa opsional
        String password = ""; // Jika password tidak perlu di-update, kirimkan null atau kosong

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(name, address, email, password.isEmpty() ? null : password);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserResponse> call = apiService.updateUser("Bearer " + authToken, userUpdateRequest);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update berhasil, fetch data pengguna lagi untuk memastikan UI diperbarui
                    fetchUserData(authToken);
                } else {
                    // Gagal update
                    Log.e("ProfilActivity", "Update failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Error
                Log.e("ProfilActivity", "Error updating user", t);
            }
        });
    }

    private void fetchUserData(String authToken) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserResponse> call = apiService.getUserData("Bearer " + authToken);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    UserResponse.User user = userResponse.getData();

                    // Update UI with user data
                    textNameUser.setText(user.getName());
                    textEmailUser.setText(user.getEmail());
                    textInputNameUser.setText(user.getName());
                    textInputEmailUser.setText(user.getEmail());
                    textInputAddressUser.setText(user.getAddress());
                } else {
                    Log.e("ProfilActivity", "Failed to get user data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("ProfilActivity", "Error fetching user data", t);
            }
        });
    }

    private void fetchBrandData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<BrandResponse> call = apiService.getBrandData();

        call.enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BrandResponse brandResponse = response.body();
                    List<BrandResponse.Brand> brands = brandResponse.getData().getBrands();

                    if (!brands.isEmpty()) {
                        // Display the first brand name on the TextView
                        BrandResponse.Brand brand = brands.get(0);
                        textNamaAplikasi.setText(brand.getName());
                    }
                } else {
                    Log.e("ProfilActivity", "Failed to get brand data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                Log.e("ProfilActivity", "Error fetching brand data", t);
            }
        });
    }

    private void logout() {
        // Clear the authentication token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("auth_token");
        editor.apply();

        // Redirect to the login activity
        Intent intent = new Intent(ProfilActivity.this, MasukActivity.class);
        startActivity(intent);
        finish();
    }
}
