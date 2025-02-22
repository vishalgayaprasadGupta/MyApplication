package com.example.myapplication.ManageUser;

import android.app.AlertDialog;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.UserDetails;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;


public class UpdateUser extends Fragment {

    FirebaseFirestore firestore;
    TextInputEditText name, contact, email, college,gender;
    RadioGroup radioGroup;
    Button update;
    ProgressBar updateProgressbar;
    RadioButton selectedRadioButton;
    View view;
    public UpdateUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_update_user, container, false);


        firestore = FirebaseFirestore.getInstance();

        name=view.findViewById(R.id.editName);
        contact=view.findViewById(R.id.editPhone);
        email=view.findViewById(R.id.editEmailAddress);
        college=view.findViewById(R.id.editCollege);
        update=view.findViewById(R.id.update);
        updateProgressbar=view.findViewById(R.id.updateProgressbar);
        updateProgressbar.setVisibility(View.INVISIBLE);
        radioGroup=view.findViewById(R.id.radioGroupGender);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        if (getArguments() != null) {
            update.setEnabled(true);
            String uid = getArguments().getString("uid");
            Log.d("UpdateUser", "User found from previous page! UID: " + uid);
            fetchUserDetails(uid);
            update.setOnClickListener(v -> {
                update.setEnabled(false);
                update.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                updateProgressbar.setVisibility(View.VISIBLE);
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Changes")
                        .setMessage("Are you sure you want to Update user data?")
                        .setPositiveButton("Yes", (dialog1, which) -> {
                            updateUserDetails(uid);
                        })
                        .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss()) // Dismiss dialog
                        .setCancelable(true) // Optional: Allow dismissing with the back button
                        .create();

                dialog.setCanceledOnTouchOutside(false); // Allow dismissing by touching outside
                dialog.show();            });
        }else{
            update.setEnabled(false);
            Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}");
    }
    private void fetchUserDetails(String uid) {
        Log.d("UpdateUser", "User UID: " + uid);

        firestore.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String Name = documentSnapshot.getString("name");
                String Gender = documentSnapshot.getString("gender");
                String Contact = documentSnapshot.getString("contact");
                String Email = documentSnapshot.getString("email");
                String College = documentSnapshot.getString("college");

                if(Gender!=null) {
                    switch (Gender) {
                        case "Male":
                            radioGroup.check(R.id.radioMale);
                            break;
                        case "Female":
                            radioGroup.check(R.id.radioFemale);
                            break;
                        default:
                            radioGroup.clearCheck();
                            Toast.makeText(getActivity()   , "Select Your gender", Toast.LENGTH_SHORT).show();
                    }
                }

                name.setText(Name);
                email.setText(Email);
                contact.setText(Contact);
                college.setText(College);
            } else {
                Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUserDetails(String userId) {
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        if(radioButtonId==-1){
            Toast.makeText(getActivity(), "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedRadioButton =view.findViewById(radioButtonId);

        String Name = name.getText().toString().trim();
        String Gender=selectedRadioButton.getText().toString();
        String Contact = contact.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String College = college.getText().toString().trim();

        if (Name.isEmpty() || Gender.isEmpty() || Contact.isEmpty() || Email.isEmpty() || College.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValidPhoneNumber(Contact)){
            Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDetails userDetails = new UserDetails(Name, Gender, Contact, Email, College);
        firestore.collection("User").document(userId).update(
                        "name", userDetails.getName(),
                        "contact", userDetails.getContact(),
                        "email", userDetails.getEmail(),
                        "college", userDetails.getCollege(),
                        "gender", userDetails.getGender()
                ).addOnSuccessListener(aVoid ->{
                    Toast.makeText(getActivity(), "Details updated successfully!", Toast.LENGTH_LONG).show();
                    name.setText("");
                    email.setText("");
                    contact.setText("");
                    college.setText("");
                    radioGroup.clearCheck();
                    update.setEnabled(true);
                    update.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF018786")));
                    updateProgressbar.setVisibility(View.INVISIBLE);
                    getActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}