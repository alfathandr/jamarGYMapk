package com.example.jamargym.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;  // Tambahkan import ini untuk log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jamargym.DetailPaketActivity;
import com.example.jamargym.R;
import com.example.jamargym.response.PackageResponse;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PaketLatihanAdapter extends RecyclerView.Adapter<PaketLatihanAdapter.PaketViewHolder> {

    private List<PackageResponse.Package> packageList;
    private Context context;
    private boolean clickable = true;

    // Tambahkan tag untuk log
    private static final String TAG = "PaketLatihanAdapter";

    public PaketLatihanAdapter(Context context, List<PackageResponse.Package> packageList) {
        this.context = context;
        this.packageList = packageList != null ? packageList : List.of(); // Avoid null packageList
    }

    @NonNull
    @Override
    public PaketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paket, parent, false);
        return new PaketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaketViewHolder holder, int position) {
        PackageResponse.Package paket = packageList.get(position);

        // Set package name
        holder.textViewNamaPaket.setText(paket.getName());

        // Format price as "Rp. 150.000"
        String formattedPrice = formatCurrency(paket.getPrice());
        String priceAndDuration = formattedPrice + " / " + paket.getDuration() + " Hari";
        holder.textViewHargaPaket.setText(priceAndDuration);

        // Set description
        holder.textViewDeskripsiPaket.setText(paket.getDescription());

        // Set listener for RecyclerView item click to open DetailPaketActivity
        holder.itemView.setOnClickListener(v -> {
            if (clickable) { // Check if the item is clickable
                // Log the package ID
                Log.d(TAG, "Package ID: " + paket.getId());  // Tambahkan log ini untuk menampilkan ID paket

                Intent intent = new Intent(context, DetailPaketActivity.class);
                intent.putExtra("packageId", paket.getId());
                intent.putExtra("packageName", paket.getName());
                intent.putExtra("packageDescription", paket.getDescription());
                intent.putExtra("packagePrice", paket.getPrice());  // Use separate keys
                intent.putExtra("packageDuration", paket.getDuration());
                context.startActivity(intent);
            }
        });

        // Jika tidak clickable, nonaktifkan elemen
        holder.itemView.setEnabled(clickable);
        holder.itemView.setAlpha(clickable ? 1.0f : 0.5f); // Beri efek visual jika tidak bisa diklik
    }

    // Metode untuk mengatur status klik
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
        notifyDataSetChanged(); // Update tampilan
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    // Helper method to format price to "Rp. XXX.XXX"
    private String formatCurrency(String price) {
        try {
            double amount = Double.parseDouble(price);
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            return format.format(amount).replace("IDR", "Rp. ").replace(",00", ""); // Adjust the format
        } catch (NumberFormatException e) {
            return "Rp. 0"; // Return default value if price is not parsable
        }
    }

    public static class PaketViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNamaPaket, textViewHargaPaket, textViewDeskripsiPaket;

        public PaketViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNamaPaket = itemView.findViewById(R.id.textNamaPaket);
            textViewHargaPaket = itemView.findViewById(R.id.textHargaPaket);
            textViewDeskripsiPaket = itemView.findViewById(R.id.textDeskripsiPaket);
        }
    }
}
