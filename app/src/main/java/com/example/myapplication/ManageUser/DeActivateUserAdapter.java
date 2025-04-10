package com.example.myapplication.ManageUser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.User;

import java.util.List;

public class DeActivateUserAdapter extends RecyclerView.Adapter<DeActivateUserAdapter.UserViewHolder> {
    private List<User> userList;
    private OnItemClickListener listener;

    public DeActivateUserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activate_user_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.status.setText(user.getStatus());

        if (user.getStatus().equals("Active")) {
            holder.activateButton.setText("DeActivate");
        } else {
            holder.activateButton.setText("DeActivated");
        }

        holder.activateButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(user.getUid(), position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email,status;
        Button activateButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            status=itemView.findViewById(R.id.status);
            activateButton = itemView.findViewById(R.id.activateButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String uid, int position);
    }
}
