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

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private Context context;
    private List<Vehicle> vehicleList;
    private int selectedPosition = -1;
    private OnVehicleSelectedListener listener;

    public interface OnVehicleSelectedListener {
        void onVehicleSelected(Vehicle vehicle);
    }

    public VehicleAdapter(Context context, List<Vehicle> vehicleList, OnVehicleSelectedListener listener) {
        this.context = context;
        this.vehicleList = vehicleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.tvVehicleName.setText(vehicle.getModel());
        holder.tvVehiclePlate.setText("Plate: " + vehicle.getPlateNumber());

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
                listener.onVehicleSelected(vehicleList.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() { return vehicleList.size(); }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView tvVehicleName;
        TextView tvVehiclePlate;
        MaterialButton btnVehicleDetails;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvVehiclePlate = itemView.findViewById(R.id.tvVehiclePlate);
            btnVehicleDetails = itemView.findViewById(R.id.btnVehicleDetails);
        }
    }
}