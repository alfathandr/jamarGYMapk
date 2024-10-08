package com.example.jamargym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jamargym.adapter.PaketLatihanAdapter;
import com.example.jamargym.api.ApiClient;
import com.example.jamargym.api.ApiService;
import com.example.jamargym.response.BrandResponse;
import com.example.jamargym.response.PackageResponse;
import com.example.jamargym.response.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class BerandaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PaketLatihanAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout mainLayout;
    private LinearLayout footerLayout;

    private TextView textNamaPengguna;
    private TextView textPaket;
    private TextView badgeStatus;
    private TextView textEndDate;
    private TextView textNamaAplikasi;

    private int dataLoadedCount = 0;
    private final int totalDataCalls = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check token first
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", "");

        if (authToken.isEmpty()) {
            startActivity(new Intent(BerandaActivity.this, MasukActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_beranda);

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initializeViews();

        // Show ProgressBar while loading data
        showLoading();

        // Fetch data
        fetchUserData(authToken);
        fetchPackages(authToken);
        fetchBrandData();
    }

    private void initializeViews() {
        ImageView imageButton = findViewById(R.id.imageButton);
        Button buttonQRcode = findViewById(R.id.buttonQRcode);
        progressBar = findViewById(R.id.progressBar);
        textNamaPengguna = findViewById(R.id.textNamaPengguna);
        textPaket = findViewById(R.id.textPaket);
        badgeStatus = findViewById(R.id.badgeStatus);
        textEndDate = findViewById(R.id.textEndDate);
        textNamaAplikasi = findViewById(R.id.textNamaAplikasi);
        recyclerView = findViewById(R.id.recyclerPaketLatihan);
        mainLayout = findViewById(R.id.mainLayout);
        footerLayout = findViewById(R.id.footerLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set click listeners
        imageButton.setOnClickListener(v -> startActivity(new Intent(BerandaActivity.this, ProfilActivity.class)));
        buttonQRcode.setOnClickListener(v -> startActivity(new Intent(BerandaActivity.this, QRCodeActivity.class)));
    }

    private void fetchBrandData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<BrandResponse> call = apiService.getBrandData();
        call.enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BrandResponse.Brand> brands = response.body().getData().getBrands();
                    if (!brands.isEmpty()) {
                        textNamaAplikasi.setText(brands.get(0).getName());
                    }
                } else {
                    Log.e("BerandaActivity", "Failed to get brand data: " + response.message());
                }
                onDataLoaded();
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                Log.e("BerandaActivity", "Error fetching brand data", t);
                onDataLoaded();
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
                    UserResponse.User user = response.body().getData();
                    textNamaPengguna.setText(user.getName());

                    if (user.getPackages() != null && !user.getPackages().isEmpty()) {
                        UserResponse.User.PackageData firstPackage = user.getPackages().get(0);
                        textPaket.setText(firstPackage.getPackageDetail() != null ? firstPackage.getPackageDetail().getName() : "Detail paket tidak tersedia");
                        badgeStatus.setText(firstPackage.getStatus());
                        textEndDate.setText("Berakhir Pada " + firstPackage.getEndDate());
                    }
                } else {
                    Log.e("BerandaActivity", "Failed to get user data: " + response.message());
                }
                onDataLoaded();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("BerandaActivity", "Error fetching user data", t);
                onDataLoaded();
            }
        });
    }

    private void fetchPackages(String authToken) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<PackageResponse> call = apiService.getPackages("Bearer " + authToken);
        call.enqueue(new Callback<PackageResponse>() {
            @Override
            public void onResponse(Call<PackageResponse> call, Response<PackageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PackageResponse packageResponse = response.body();
                    if (packageResponse.isSuccess()) {
                        List<PackageResponse.Package> packages = packageResponse.getData();
                        adapter = new PaketLatihanAdapter(BerandaActivity.this, packages);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.e("BerandaActivity", "Response indicates failure");
                    }
                } else {
                    Log.e("BerandaActivity", "Response is not successful: " + response.message());
                }
                onDataLoaded();
            }

            @Override
            public void onFailure(Call<PackageResponse> call, Throwable t) {
                Log.e("BerandaActivity", "Error fetching packages", t);
                onDataLoaded();
            }
        });
    }

    private void onDataLoaded() {
        dataLoadedCount++;
        if (dataLoadedCount >= totalDataCalls) {
            hideLoading();
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        footerLayout.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        footerLayout.setVisibility(View.VISIBLE);
    }
}
