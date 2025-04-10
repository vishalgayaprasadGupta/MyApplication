package com.example.myapplication.eventOrganiser.ManageOrganiser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.ManageUser.DeActivateUserAdapter;
import com.example.myapplication.ManageUser.manageUser;
import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.sendAccountDeActivatedEmail;
import com.example.myapplication.User;
import com.example.myapplication.eventOrganiser.EventOrganiser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class RestrictOrganiserList extends Fragment {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private RestrictOrganiserAdapter adapter;
    private List<EventOrganiser> organiserList;
    sendAccountDeActivatedEmail sendEmail=new sendAccountDeActivatedEmail();
    public RestrictOrganiserList() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_restrict_organiser_list, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.restrictRecyclerView);
        organiserList = new ArrayList<>();
        adapter = new RestrictOrganiserAdapter(organiserList);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        fetchActiveUsers();

        adapter.setOnItemClickListener((uid, position) -> {
            deActivateEventOrganiser(uid, position);
        });

        return view;
    }
    private void fetchActiveUsers() {
        firestore.collection("User")
                .whereEqualTo("role", "Event Organiser")
                .whereEqualTo("status", "Active")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<EventOrganiser> organisers = queryDocumentSnapshots.toObjects(EventOrganiser.class);
                        organiserList.clear();
                        organiserList.addAll(organisers);
                        adapter.notifyDataSetChanged();
                    }else{
                        showNoUserDialog();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load users", Toast.LENGTH_SHORT).show();
                });
    }

    private void deActivateEventOrganiser(String uid, int position) {
        firestore.collection("User").document(uid)
                .update("status", "Pending")
                .addOnSuccessListener(aVoid -> {
                    sendEmail.sendDeactivatedEmail(organiserList.get(position).getEmail(),organiserList.get(position).getName());
                    Toast.makeText(getActivity(), "Event Organiser DeActivated successfully!", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);
                    getActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to deactivate event organiser", Toast.LENGTH_SHORT).show();
                });
    }

    private void showNoUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No User");
        builder.setMessage("No Active event organiser is found");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void redirectToFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}