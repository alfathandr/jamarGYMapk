package com.example.jamargym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jamargym.api.ApiClient;
import com.example.jamargym.api.ApiService;
import com.example.jamargym.response.BrandResponse;
import com.example.jamargym.response.OrderResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPaketActivity extends AppCompatActivity {

    private static final int MAX_IMAGE_SIZE_MB = 4;
    private TextView textNamaAplikasi;
    private TextView textViewNamaPaket, textViewDeskripsiPaket, textViewHargaPaket;
    private ImageView imageViewBuktiPembayaran;
    private Button buttonOrder;
    private Uri imageUri;
    private String authToken;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_paket);

        // Check authentication token
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        authToken = sharedPreferences.getString("auth_token", "");

        if (authToken.isEmpty()) {
            startActivity(new Intent(this, MasukActivity.class));
            finish();
            return;
        }

        setupUI();
        retrievePackageData();
        initializeImagePicker();
        fetchBrandData();
    }

    private void setupUI() {
        textNamaAplikasi = findViewById(R.id.textNamaAplikasi);
        textViewNamaPaket = findViewById(R.id.textViewNamaPaket);
        textViewDeskripsiPaket = findViewById(R.id.textViewDeskripsiPaket);
        textViewHargaPaket = findViewById(R.id.textViewHargaPaket);
        imageViewBuktiPembayaran = findViewById(R.id.imageViewBuktiPembayaran);
        buttonOrder = findViewById(R.id.buttonOrder);

        buttonOrder.setOnClickListener(view -> placeOrder());
        imageViewBuktiPembayaran.setOnClickListener(v -> openImagePicker());
    }

    private void retrievePackageData() {
        Intent intent = getIntent();

        String packageName = intent.getStringExtra("packageName");
        String packageDescription = intent.getStringExtra("packageDescription");
        String packagePrice = intent.getStringExtra("packagePrice");
        int packageDuration = intent.getIntExtra("packageDuration", 0);
        int packageId = intent.getIntExtra("packageId", 0);

        displayPackageDetails(packageId, packageName, packageDescription, packagePrice, packageDuration);
    }

    private void displayPackageDetails(int packageId, String packageName, String packageDescription, String packagePrice, int packageDuration) {
        textViewNamaPaket.setText(packageName);
        textViewDeskripsiPaket.setText(packageDescription);
        textViewHargaPaket.setText(formatCurrency(packagePrice) + " / " + packageDuration + " Hari");

        TextView textViewKonfirmasiNamaPaket = findViewById(R.id.textPaketName);
        TextView textViewKonfirmasiDeskripsiPaket = findViewById(R.id.textPaketDescription);
        TextView textViewKonfirmasiHargaPaket = findViewById(R.id.textPaketPrice);
        TextView textViewKonfirmasiDurasiPaket = findViewById(R.id.textPaketDuration);

        textViewKonfirmasiNamaPaket.setText("Nama paket: " + packageName + " (ID: " + packageId + ")");
        textViewKonfirmasiDeskripsiPaket.setText("Deskripsi paket: " + packageDescription);
        textViewKonfirmasiHargaPaket.setText("Harga paket: " + formatCurrency(packagePrice));
        textViewKonfirmasiDurasiPaket.setText("Durasi paket: " + packageDuration + " Hari");
    }

    private void initializeImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        handleImageSelection(result.getData().getData());
                    }
                }
        );
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleImageSelection(Uri uri) {
        if (uri != null) {
            try {
                if (isFileSizeWithinLimit(uri, MAX_IMAGE_SIZE_MB)) {
                    imageUri = uri;
                    imageViewBuktiPembayaran.setImageURI(imageUri);
                } else {
                    Toast.makeText(this, "Ukuran gambar tidak boleh lebih dari " + MAX_IMAGE_SIZE_MB + " MB", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("DetailPaketActivity", "Error loading image: " + e.getMessage());
            }
        }
    }

    private boolean isFileSizeWithinLimit(Uri uri, int maxSizeMB) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);

                long fileSizeInBytes = options.outWidth * options.outHeight * 4;
                long fileSizeInMB = fileSizeInBytes / (4024 * 4024);

                return fileSizeInMB <= maxSizeMB;
            }
        } catch (IOException e) {
            Log.e("DetailPaketActivity", "Error checking file size: " + e.getMessage());
        }
        return false;
    }

    private void placeOrder() {
        if (imageUri != null) {
            File imageFile = new File(getRealPathFromURI(imageUri));
            int packageId = getIntent().getIntExtra("packageId", 0);

            // Create RequestBody for package ID
            RequestBody requestBodyPackageId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(packageId));

            // Create MultipartBody.Part for the image
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

            // Call the API
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            apiService.placeOrder("Bearer " + authToken, requestBodyPackageId, body)
                    .enqueue(new Callback<OrderResponse>() {
                        @Override
                        public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                // Handle successful response
                                Log.d("DetailPaketActivity", "Order placed successfully: " + response.body().getMessage());

                                // Show success message
                                Toast.makeText(DetailPaketActivity.this, "Order berhasil, admin akan konfirmasi pesanan anda", Toast.LENGTH_LONG).show();

                                // Open HomeActivity after successful order
                                Intent intent = new Intent(DetailPaketActivity.this, BerandaActivity.class);
                                startActivity(intent);
                            } else {
                                // Handle unsuccessful response
                                Log.d("DetailPaketActivity", "Order placement failed: " + response.message());
                                Toast.makeText(DetailPaketActivity.this, "Pemesanan gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderResponse> call, Throwable t) {
                            Log.e("DetailPaketActivity", "Error: " + t.getMessage());
                            Toast.makeText(DetailPaketActivity.this, "Kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Silakan pilih gambar bukti pembayaran.", Toast.LENGTH_SHORT).show();
        }
    }



    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        }
        return null;
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
                    Log.e("DetailPaketActivity", "Failed to fetch brands: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                Log.e("DetailPaketActivity", "Error fetching brands: " + t.getMessage());
            }
        });
    }

    private String formatCurrency(String price) {
        double priceValue = Double.parseDouble(price);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(priceValue);
    }
}
