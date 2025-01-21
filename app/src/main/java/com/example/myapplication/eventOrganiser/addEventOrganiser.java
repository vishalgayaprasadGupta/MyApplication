package com.example.myapplication.eventOrganiser;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.LoginPage;
import com.example.myapplication.R;
import com.example.myapplication.RegistrationPage;
import com.example.myapplication.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class addEventOrganiser extends Fragment {
    View view;
    private String  selectedStream, selectedDepartment,emailSend;
    private Spinner  departmentSpinner, streamSpinner;
    TextInputEditText Phone,EmailAddress,UserName,CollegeName,UserPassword,ConfirmPassword;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String Status,Role,Gender;
    Button Signup;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference userData;
    ProgressBar progressBar;
    User user;
    EventOrganiser organiser;
    String username,email,phone,college,password,confirmPassword;
    static final String USER = "User";
    static final String TAG="RegistrationPage";
    public addEventOrganiser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_add_event_organiser, container, false);

        Phone = view.findViewById(R.id.editPhone);
        EmailAddress=view.findViewById(R.id.editEmail);
        UserName = view.findViewById(R.id.editName);
        CollegeName = view.findViewById(R.id.editCollege);
        UserPassword = view.findViewById(R.id.editPassword);
        ConfirmPassword = view.findViewById(R.id.editConfirmPassword);
        Signup = view.findViewById(R.id.addEventOrganiser);
        radioGroup = view.findViewById(R.id.radioGroupGender);

        departmentSpinner = view.findViewById(R.id.departmentSpinner);
        streamSpinner = view.findViewById(R.id.streamSpinner);

        emailSend="null";

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getFragment(new ManageEventOrganiser());
                    }
                });

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        userData = db.collection("User");
        loadStreams();

        progressBar = view.findViewById(R.id.addOrganiserProgressbaar);
        progressBar.setVisibility(View.INVISIBLE);

        Signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "Signup button clicked");

                Role="Event Organiser";
                Status="Pending";

                if(validateUserInput()) {
                    if (isNetworkAvailable()) {
                        organiser = new EventOrganiser(Status, Role, username, Gender, email, phone, college, password,selectedStream,selectedDepartment,emailSend);
                        registerUser(email, password);
                    } else {
                        Toast.makeText(requireActivity(), "Network error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(requireActivity(), "Invalid input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    private boolean validateUserInput() {
         username = UserName.getText().toString().trim();
         email = EmailAddress.getText().toString().trim();
         phone = Phone.getText().toString().trim();
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        if (radioButtonId != -1) {
            selectedRadioButton = view.findViewById(radioButtonId);
            Gender = selectedRadioButton.getText().toString();
        } else {
            Toast.makeText(getActivity(), "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        }
         college= CollegeName.getText().toString().trim();
         password = UserPassword.getText().toString();
         confirmPassword = ConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)|| TextUtils.isEmpty(college)||
        TextUtils.isEmpty(Gender)) {
            Toast.makeText(requireActivity(), "All fields are mandatory!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedStream == null || selectedDepartment == null) {
            Toast.makeText(requireActivity(), "Please select stream and department.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!username.matches("^[a-zA-Z\\s'-]{2,50}$")){
            Toast.makeText(requireActivity(), "Invalid name !", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!college.matches("^[a-zA-Z\\s'-]{2,50}$")){
            Toast.makeText(requireActivity(), "Invalid college name !", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireActivity(), "Invalid email format!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("\\d{10}")) {
            Toast.makeText(requireActivity(), "Invalid phone number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(requireActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loadStreams() {
        List<String> streams = new ArrayList<>();
        db.collection("Departments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                streams.add("Select Stream"); // Default option
                for (DocumentSnapshot doc : task.getResult()) {
                    streams.add(doc.getId());
                }

                ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(requireActivity(),
                        android.R.layout.simple_spinner_item, streams);
                streamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                streamSpinner.setAdapter(streamAdapter);

                streamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedStream = streams.get(position);
                        if (!selectedStream.equals("Select Stream")) {
                            loadDepartments(selectedStream);
                        } else {
                            selectedStream = null;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });
            } else {
                Toast.makeText(requireActivity(), "Failed to load streams!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDepartments(String stream) {
        db.collection("Departments").document(stream).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String departmentField = stream+ "Department";
                    List<String> departments = (List<String>) doc.get(departmentField);
                    Log.d("Firestore", "Fetching field: " + departmentField);

                    if (departments != null) {
                        departments.add(0, "Select Department"); // Default option
                        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(requireActivity(),
                                android.R.layout.simple_spinner_item, departments);
                        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        departmentSpinner.setAdapter(departmentAdapter);

                        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedDepartment = departments.get(position);
                                if (selectedDepartment.equals("Select Department")) {
                                    selectedDepartment = null;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });
                    } else {
                        Toast.makeText(requireActivity(), "No departments selected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "Stream does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireActivity(), "Failed to load departments!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetStreamsAndDepartments() {
        streamSpinner.setAdapter(null);
        departmentSpinner.setAdapter(null);

        List<String> streams = new ArrayList<>();
        List<String> departments = new ArrayList<>();

        streams.add("Select Stream");
        departments.add("Select Department");

        ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, streams);
        streamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        streamSpinner.setAdapter(streamAdapter);

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);
    }

    public void registerUser(String EmailId,String Password) {
        progressBar.setVisibility(View.VISIBLE);
        Signup.setEnabled(false);
        Signup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        mAuth.createUserWithEmailAndPassword(EmailId, Password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Signup.setEnabled(true);
                        Signup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E3C72")));
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                            emailSend="Email Verification";
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                setDisplayName();
                                Log.d(TAG, "Register done ");
                                Toast.makeText(getActivity(), "Event Organiser added succesfully", Toast.LENGTH_SHORT).show();
                                if (isNetworkAvailable()) {
                                    updateUI(user);
                                } else {
                                    mAuth.getCurrentUser().delete();
                                }
                            }

                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                String errorMessage = exception.getMessage();
                                if (errorMessage != null && errorMessage.contains("The email address is already in use")) {
                                    Toast.makeText(getActivity(), "Email is already regisered,Try logging in!", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", exception);
                                    Toast.makeText(getActivity(), "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }
    public void setDisplayName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = UserName.getText().toString();
            if(!TextUtils.isEmpty(displayName)) {
                user.updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName).build());
            }else{
                Toast.makeText(getActivity(), "User not authenticated.", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(getActivity(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), " Activation link has been sent organiser email", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireActivity(), "Activation link has been sent organiser email", Toast.LENGTH_LONG).show();
                            Log.e("EmailVerification", "Error sending verification email: " + task.getException());
                        }
                    });
        }
    }
    public void updateUI(FirebaseUser user){
        if (user != null) {
            if(isNetworkAvailable()) {
                String uid = user.getUid();
                EventOrganiser userdata = new EventOrganiser(Status,Role, username, Gender, email, phone, college, password,selectedStream,selectedDepartment,emailSend);
                userData.document(uid).set(userdata).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                       getFragment(new ManageEventOrganiser());
                    } else {
                        Toast.makeText(getActivity(), "Error Saving User Data", Toast.LENGTH_SHORT).show();
                        requireActivity().finish();
                    }
                });
            }else {
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            if (mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().delete();
                requireActivity().finish();
            }
        }
    }
    public void getFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}