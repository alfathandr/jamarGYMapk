package com.example.jamargym;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.jamargym.api.ApiClient;
import com.example.jamargym.api.ApiService;
import com.example.jamargym.response.UserResponse;
import com.google.common.util.concurrent.ListenableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRCodeActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private ImageView qrCodeImageView;
    private String qrCodeUrl;  // Tambahkan variabel instance untuk menyimpan URL QR Code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek token sebelum setContentView
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", "");

        // Redirect ke halaman login jika token tidak tersedia
        if (authToken.isEmpty()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_qrcode);

        // Inisialisasi UI
        qrCodeImageView = findViewById(R.id.idQRCode);
        Button buttonQRcode = findViewById(R.id.buttonQRcode);

        // Mengatur Edge-to-Edge layout
        setUpEdgeToEdge();

        // Mengambil QR Code pengguna dengan token autentikasi
        fetchUserQRCode(authToken);

        // Menangani klik tombol untuk mengunduh QR Code
        buttonQRcode.setOnClickListener(v -> requestStoragePermission());
    }

    private void redirectToLogin() {
        Intent intent = new Intent(QRCodeActivity.this, MasukActivity.class);
        startActivity(intent);
        finish();
    }

    private void setUpEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchUserQRCode(String authToken) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Panggil API dengan menyertakan token dalam header
        Call<UserResponse> call = apiService.getUserData("Bearer " + authToken);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse.User user = response.body().getData();
                    loadQRCodeImage(user.getQRCode());
                } else {
                    Log.e("QRCodeActivity", "Gagal mendapatkan data pengguna: " + response.message());
                    Toast.makeText(QRCodeActivity.this, "Gagal mendapatkan data pengguna", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("QRCodeActivity", "Error fetching user data", t);
            }
        });
    }

    private void loadQRCodeImage(String qrCode) {
        if (qrCode != null && !qrCode.isEmpty()) {
            String baseUrl = "https://jamargym.digi4our.com/storage/";
            qrCodeUrl = baseUrl + qrCode;  // Simpan URL QR Code ke variabel instance

            Glide.with(this)
                    .load(qrCodeUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("QRCodeActivity", "Error loading QR Code: ", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(qrCodeImageView);
        } else {
            Toast.makeText(this, "QR Code tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            downloadQRCode();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, unduh QR Code
                downloadQRCode();
            } else {
                Toast.makeText(this, "Izin penyimpanan diperlukan untuk mengunduh QR Code", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadQRCode() {
        if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(qrCodeUrl));
            request.setTitle("Unduh QR Code");
            request.setDescription("Mengunduh QR Code Anda...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "QRCode.png");

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                downloadManager.enqueue(request);
                Toast.makeText(this, "QR Code sedang diunduh...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal memulai unduhan", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "QR Code tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }
}
