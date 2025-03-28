package com.example.myapplication.eventOrganiser.ManageOrganiser;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.eventOrganiser.PendingOrganisersRequest;
import com.example.myapplication.eventOrganiser.addEventOrganiser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ManageEventOrganiser extends Fragment {
    View view;
    CardView AddEventOrganiser,VerifyRequest,UpdateDetails,DeleteEventOrganiser,ExportOrganiser;
    TextView addOrganiser,exportOrganiser,pendingRequest,restrictOrganiser,updateOrganiserDetails;
    PdfExporter pdfExporter = new PdfExporter(getContext());
    public ManageEventOrganiser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_manage_event_organiser, container, false);

        AddEventOrganiser=view.findViewById(R.id.AddEventOrganiser);
        VerifyRequest=view.findViewById(R.id.VerifyRequest);
        UpdateDetails=view.findViewById(R.id.UpdateDetails);
        DeleteEventOrganiser=view.findViewById(R.id.DeleteEventOrganiser);
        ExportOrganiser=view.findViewById(R.id.ExportOrganiser);

        exportOrganiser=view.findViewById(R.id.exportOrganiserDetails);
        restrictOrganiser=view.findViewById(R.id.restrictOrganiser);
        updateOrganiserDetails=view.findViewById(R.id.updateOrganiserDetails);

        animateCardView(AddEventOrganiser,400);
        animateCardView(UpdateDetails,800);
        animateCardView(VerifyRequest,1200);
        animateCardView(DeleteEventOrganiser,1600);
        animateCardView(ExportOrganiser,2000);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        addOrganiser=view.findViewById(R.id.addOrganiser);
        addOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new addEventOrganiser());
            }
        });
        pendingRequest=view.findViewById(R.id.pendingRequest);
        pendingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new PendingOrganisersRequest());
            }
        });

        exportOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownloadDialog();
            }
        });

        restrictOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new RestrictOrganiserList());
            }
        });

        updateOrganiserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new UpdateOrganiserList());
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
                .setMessage("Do you want to download the Event Organiser Details as a PDF?")
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

        db.collection("User").whereEqualTo("role", "Event Organiser").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Map<String, Object>> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userList.add(document.getData());
                }
                if (!userList.isEmpty()) {
                    Log.d("Firestore", "Final user list: " + userList.toString());
                    pdfExporter.generateOrganiserDetails(getContext(), userList);
                }else{
                    Toast.makeText(requireContext(), "No users found with role='Event Organiser'", Toast.LENGTH_SHORT).show();
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