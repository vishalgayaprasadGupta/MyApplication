package com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ManageEvents.Activity;

import java.util.List;

public class CollegeActivityListAdapter extends RecyclerView.Adapter<CollegeActivityListAdapter.ActivityViewHolder> {
    private List<Activity> activityList;
    private CollegeActivityListAdapter.OnItemClickListener listener; // Declare the listener
    public CollegeActivityListAdapter(List<Activity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_events, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        // Get the current activity
        Activity activity = activityList.get(position);

        // Bind data to the views
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
        return activityList.size(); // Return the size of the activity list
    }

    public void setOnItemClickListener(CollegeActivityListAdapter.OnItemClickListener listener) {
        this.listener = listener; // Set the listener
    }

    // Define your own ActivityViewHolder inside ActivityAdapter
    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityName, activityDescription, activityDate, activityVenue, activityRules,availability;

        public ActivityViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            activityName = itemView.findViewById(R.id.activityName);
            activityDescription = itemView.findViewById(R.id.activityDescription);
            activityDate = itemView.findViewById(R.id.activityDate);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(String activityId); // Pass eventId when item is clicked
    }
}
