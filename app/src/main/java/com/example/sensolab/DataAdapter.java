package com.example.sensolab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private final List<SensorData> dataList;

    public DataAdapter(List<SensorData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SensorData data = dataList.get(position);

        String time = String.valueOf(data.getTime());
        String value = data.getValue();

        String timeText = holder.itemView.getContext().getString(R.string.time, time);
        String valueText = holder.itemView.getContext().getString(R.string.value, value);

        holder.timeText.setText(timeText);
        holder.valueText.setText(valueText);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView valueText, timeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            valueText = itemView.findViewById(R.id.valueText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }
}
