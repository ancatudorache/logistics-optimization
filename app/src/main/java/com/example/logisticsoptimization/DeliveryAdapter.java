package com.example.logisticsoptimization;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {

    private Context context;
    private List<Delivery> deliveryList;
    private OnDeliveryClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public DeliveryAdapter(Context context, List<Delivery> deliveryList, OnDeliveryClickListener listener) {
        this.context = context;
        this.deliveryList = deliveryList;
        this.listener = listener;
    }

    public interface OnDeliveryClickListener {
        void onDeliveryClick(Delivery delivery);
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        Delivery delivery = deliveryList.get(position);
        holder.tvRoute.setText(delivery.getPickupAddress() + " → " + delivery.getDeliveryAddress());
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(delivery.getDeadline());
            holder.tvDeadline.setText("Deadline: " + outputFormat.format(date));
        } catch (Exception e) {
            holder.tvDeadline.setText("Deadline: " + delivery.getDeadline());
        }

        // Evidențiere vizuală pentru cardul selectat
        if (selectedPosition == position) {
            holder.cardView.setStrokeWidth(4);
            holder.cardView.setStrokeColor(context.getColor(R.color.primary));
        } else {
            holder.cardView.setStrokeWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            
            // Notificăm schimbările pentru a updata bordura
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onDeliveryClick(delivery);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute;
        TextView tvDeadline;
        MaterialButton btnViewDetails;
        MaterialCardView cardView;

        public DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            cardView = (MaterialCardView) itemView;
        }
    }
}