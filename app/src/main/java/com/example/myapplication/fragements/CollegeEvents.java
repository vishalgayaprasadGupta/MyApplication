package com.example.myapplication.fragements;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.Adapter.EventAdapter;
import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CollegeEvents extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private EventAdapter eventAdapter;
    String collegeName;
    public CollegeEvents() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_college_events, container, false);
        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        eventAdapter = new EventAdapter(new ArrayList<>());
        eventAdapter.setOnItemClickListener(this::onItemClick);
        recyclerView.setAdapter(eventAdapter);

        if(getArguments()!=null){
            collegeName=getArguments().getString("collegeName");
            Log.d("CollegeEvents", "College Name 4 : " + collegeName);
            fetchEvents(collegeName);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                });
        return view;
    }

    private void fetchEvents(String collegeName) {
        Log.d("CollegeEvents", "College Name 5 : " + collegeName);
        db.collection("College Events").addSnapshotListener((value, error) -> {
            if(collegeName==null){
                Toast.makeText(getActivity(), "No college name found", Toast.LENGTH_SHORT).show();
                return;
            }
            if (error != null) {
                Toast.makeText(getActivity(), "Error fetching events", Toast.LENGTH_SHORT).show();
                return;
            }
            if (value == null || value.isEmpty()) {
                showNoEventDialog();
                return;
            }

            List<Event> filteredEvents = new ArrayList<>();
            for (DocumentSnapshot doc : value.getDocuments()) {
                Event event = doc.toObject(Event.class);
                if (event.getCollege()!=null && event != null && !"Deleted".equals(event.getEventStatus())
                        && event.getCollege().equals(collegeName.trim())) {
                    filteredEvents.add(event);
                }
            }
            if (filteredEvents.isEmpty()) {
                showNoEventDialog();
            }else {
                eventAdapter = new EventAdapter(filteredEvents);
                eventAdapter.setOnItemClickListener(this::onItemClick);
                recyclerView.setAdapter(eventAdapter);
            }
        });
    }

    private void showNoEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("No Event");
        builder.setMessage("No Events are ongoing");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(getActivity()!=null){
                    getActivity().getSupportFragmentManager().popBackStack();
                }else{
                    getFragment(new ViewEventCategory());
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void getFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void onItemClick(String eventId,String Status) {
       if(Status.equals("Active")){
           CollegeEventActivities activitiesFragment = new CollegeEventActivities();
           Bundle bundle = new Bundle();
           bundle.putString("eventId", eventId);
           activitiesFragment.setArguments(bundle);
           getFragment(activitiesFragment);
       }else if(Status.equals("Cancel")){
           Toast.makeText(getActivity(), "Event has been Cancel", Toast.LENGTH_LONG).show();
       }else{
           Toast.makeText(getActivity(), "Event has been Closed", Toast.LENGTH_LONG).show();
       }

    }
}
