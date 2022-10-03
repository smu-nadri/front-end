package com.example.nadri4_edit1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HighlightAdapter extends RecyclerView.Adapter<HighlightAdapter.ViewHolder> {
    private List<String> Items;

    public HighlightAdapter(List<String> Items) {
        this.Items = Items;
    }

    @NonNull
    @Override
    public HighlightAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highlight_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HighlightAdapter.ViewHolder holder, int position) {
        holder.pagerText.setText(Items.get(position));
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pagerText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pagerText = itemView.findViewById(R.id.view_pager_text);
        }
    }

}
