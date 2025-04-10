package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ManageEvents.Activity;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private List<Activity> activityList;
    private ActivityAdapter.OnItemClickListener listener;
    public ActivityAdapter(List<Activity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_events, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activityList.get(position);

        holder.activityName.setText(activity.getActivtiyName());
        holder.activityDescription.setText(activity.getActivtiyDescription());
        holder.activityDate.setText(activity.getActivityDate());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(activity.getActivityId()); // Pass document ID on click
            }
        });
    }


    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public void setOnItemClickListener(ActivityAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityName, activityDescription, activityDate, activityVenue, activityRules,availability;

        public ActivityViewHolder(View itemView) {
            super(itemView);

            activityName = itemView.findViewById(R.id.activityName);
            activityDescription = itemView.findViewById(R.id.activityDescription);
            activityDate = itemView.findViewById(R.id.activityDate);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(String eventId);
    }
}
