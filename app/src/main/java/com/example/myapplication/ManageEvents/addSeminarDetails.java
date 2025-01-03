package com.example.myapplication.ManageEvents;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.databinding.FragmentAddSeminarDetailsBinding;
import com.example.myapplication.fragements.Seminar;
import com.example.myapplication.manageEvents;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class addSeminarDetails extends Fragment {
     View view;
     FirebaseFirestore firestore;
     TextView seminarTitle,seminarDescription,seminarDate,seminarVenue,seminarDuration,speakerName,speakerBio,registrationFeeSeminar,seminarAgenda,requirments,availability;
     Button addEventButton;
     ProgressBar addEventDetails;
    public addSeminarDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_add_seminar_details, container, false);

        firestore = FirebaseFirestore.getInstance();

        seminarTitle=view.findViewById(R.id.seminarTitle);
        seminarDescription=view.findViewById(R.id.seminarDescription);
        seminarDate=view.findViewById(R.id.seminarDate);
        seminarVenue=view.findViewById(R.id.seminarVenue);
        seminarDuration=view.findViewById(R.id.seminarDuration);
        speakerName=view.findViewById(R.id.speakerName);
        speakerBio=view.findViewById(R.id.speakerBio);
        registrationFeeSeminar=view.findViewById(R.id.registrationFees);
        seminarAgenda=view.findViewById(R.id.seminarAgenda);
        requirments=view.findViewById(R.id.requirement);
        availability=view.findViewById(R.id.availability);
        addEventDetails=view.findViewById(R.id.seminarProgressbar);
        addEventDetails.setVisibility(View.INVISIBLE);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getFragment(new manageEvents());
                    }
                });

        addEventButton =view.findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v -> {
            addEventDetails.setVisibility(View.VISIBLE);
            addEventButton.setEnabled(false);
            addEventDetails.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
            addDetails();
        });

        seminarDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        return view;
    }
    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    String selectedDateString = formatDate(selectedDay, selectedMonth + 1, selectedYear);
                    seminarDate.setText(selectedDateString);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, 2);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private String formatDate(int day, int month, int year) {
        return String.format("%02d/%02d/%d", day, month,year);
    }
    private void addDetails() {
        String eventId = "";
        String eventType = "";
        if (getArguments() != null) {
            eventId = getArguments().getString("documentId"); // Retrieve eventId passed from previous fragment
            Log.d("addEvent", "Event ID (Passed): " + eventId);
            eventType = getArguments().getString("eventType");
        }

        // Retrieve input fields
        String name = seminarTitle.getText().toString();
        String description = seminarDescription.getText().toString();
        String date = seminarDate.getText().toString();
        String venue = seminarVenue.getText().toString();
        String Duration=seminarDuration.getText().toString();
        String speaker=speakerName.getText().toString();
        String bio=speakerBio.getText().toString();
        String agenda = seminarAgenda.getText().toString();
        String availability=this.availability.getText().toString();
        String requirement=requirments.getText().toString();
        String registrationFee = registrationFeeSeminar.getText().toString();
        
        if (name.isEmpty() || description.isEmpty() || date.isEmpty() ||
                venue.isEmpty() || Duration.isEmpty() || speaker.isEmpty() || bio.isEmpty() ||
                agenda.isEmpty() || availability.isEmpty() || requirement.isEmpty() || registrationFee.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }

        // Log the retrieved values
        Log.d("addEvent", "Event ID (Passed): " + eventId);

        Seminar activity = new Seminar(name, description, date, venue, Duration,speaker,bio,registrationFee,agenda,eventId,eventType,requirement,availability);

        firestore.collection("EventActivities")
                .add(activity)
                .addOnSuccessListener(documentReference -> {
                    String activityId = documentReference.getId();
                    activity.setActivityId(activityId);

                    documentReference.update("activityId", activityId)
                            .addOnSuccessListener(aVoid -> {
                                addEventDetails.setVisibility(View.INVISIBLE);
                                addEventButton.setEnabled(true);
                                addEventDetails.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E3C72")));
                                Log.d("addEvent", "Activity ID added to document: " + activityId);
                                getFragment(new AdminHome());
                            })
                            .addOnFailureListener(e -> {
                                Log.d("addEvent", "Error updating activityId in Firestore");
                            });
                    Toast.makeText(getActivity(), "Activity Added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error adding activity", Toast.LENGTH_SHORT).show();
                });
    }

    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}