package com.example.logisticsoptimization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder> {

    private Context context;
    private List<Driver> driverList;
    private int selectedPosition = -1;
    private OnDriverSelectedListener listener;

    public interface OnDriverSelectedListener {
        void onDriverSelected(Driver driver);
    }

    public DriverAdapter(Context context, List<Driver> driverList, OnDriverSelectedListener listener) {
        this.context = context;
        this.driverList = driverList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_driver, parent, false);
        return new DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        Driver driver = driverList.get(position);
        holder.tvDriverName.setText(driver.getName());
        holder.tvDriverStatus.setText(driver.getUsername());

        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(0x220066FF);
        } else {
            holder.itemView.setBackgroundColor(0x00000000);
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_ID) {
                selectedPosition = pos;
                notifyDataSetChanged();
                listener.onDriverSelected(driverList.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() { return driverList.size(); }

    public static class DriverViewHolder extends RecyclerView.ViewHolder {
        TextView tvDriverName;
        TextView tvDriverStatus;
        MaterialButton btnDriverDetails;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvDriverStatus = itemView.findViewById(R.id.tvDriverStatus);
            btnDriverDetails = itemView.findViewById(R.id.btnDriverDetails);
        }
    }
}