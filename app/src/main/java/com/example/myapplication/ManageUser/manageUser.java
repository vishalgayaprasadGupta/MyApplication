package com.example.myapplication.ManageUser;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.PdfExporter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class manageUser extends Fragment {
    public manageUser() {
        // Required empty public constructor
    }
    TextView addUser,updateUser,deactivateUser,activate,export;
    CardView AddUser,UpdateUser,DeactivateUser,Activate,Export;
    PdfExporter pdfExporter = new PdfExporter(getContext());
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_user, container, false);

        AddUser=view.findViewById(R.id.AddUser);
        UpdateUser=view.findViewById(R.id.UpdateUser);
        DeactivateUser=view.findViewById(R.id.DeactivateUser);
        Activate=view.findViewById(R.id.ActivateUser);
        Export=view.findViewById(R.id.Export);

        animateCardView(AddUser,0);
        animateCardView(UpdateUser,200);
        animateCardView(DeactivateUser,400);
        animateCardView(Activate,600);
        animateCardView(Export,800);

        addUser=view.findViewById(R.id.addUser);
        updateUser=view.findViewById(R.id.updateUser);
        deactivateUser=view.findViewById(R.id.deactivateUser);
        activate=view.findViewById(R.id.activateUser);
        export=view.findViewById(R.id.export);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new AddUser());
            }
        });
        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new UpdateUserList());
            }
        });
        deactivateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new DeActivateUser());
            }
        });
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new ActivateUser());
            }
        });
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownloadDialog();
            }
        });
        return view;
    }

    private void animateCardView(final CardView cardView, long delay) {
        cardView.setVisibility(View.INVISIBLE);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(delay);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Optional: You can add additional behavior after the animation ends
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Not needed in this case
            }
        });

        cardView.startAnimation(fadeIn);
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Download PDF")
                .setMessage("Do you want to download the User Details as a PDF?")
                .setPositiveButton("Download", (dialog, which) -> {
                    fetchUserData();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void fetchUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User").whereEqualTo("role", "User").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Map<String, Object>> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userList.add(document.getData());
                }
                if (!userList.isEmpty()) {
                    Log.d("Firestore", "Final user list: " + userList.toString());
                    pdfExporter.generateUserDetails(getContext(), userList);
                } else {
                    Toast.makeText(requireContext(), "No users found with role='User'", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }
        });

    }

    public void getFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}