package com.example.myapplication.ManageUser;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.manageEvents;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddUser extends Fragment {

    TextView back;
    Button addUser;
    TextInputEditText name,college,email,phone,password;
    RadioGroup radioGroup;
    ProgressBar addUserProgressbar;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    static final String USER = "User";
    CollectionReference userData;
    User user;
    String uid,Gender,Role,Status,isVerficationEmailSend,isEmailVerified;
    RadioButton selectedRadioButton;
    public AddUser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_user, container, false);

        back=view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new manageUser());
            }});

        name=view.findViewById(R.id.editName);
        college=view.findViewById(R.id.editCollege);
        email=view.findViewById(R.id.editEmailAddress);
        phone=view.findViewById(R.id.editPhone);
        password=view.findViewById(R.id.editPassword);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        addUserProgressbar=view.findViewById(R.id.addUserProgressbar);
        addUserProgressbar.setVisibility(View.INVISIBLE);

        isVerficationEmailSend="false";
        isEmailVerified="false";
        radioGroup=view.findViewById(R.id.radioGroupGender);
        firestore= FirebaseFirestore.getInstance();
        userData = firestore.collection(USER);
        mAuth= FirebaseAuth.getInstance();

        addUser=view.findViewById(R.id.addUser);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();

                selectedRadioButton = view.findViewById(radioButtonId);
                Role="User";
                Status="Pending";
                String Username = name.getText().toString();
                try {
                    Gender = selectedRadioButton.getText().toString();
                }catch(Exception ex){
                    Toast.makeText(getActivity(), "error :  "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("error",ex.getMessage());
                    return;
                }
                String EmailId = email.getText().toString();
                String Contact = phone.getText().toString();
                String College = college.getText().toString();
                String Password = password.getText().toString();

                validateInput(Username,College,EmailId,Contact,Password);
            }
        });

        return view;
    }
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}");
    }
    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }

    public void validateInput(String Username,String College,String EmailId,String Contact,String Password){
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        if (radioButtonId == -1) {
            Toast.makeText(getActivity(), "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(Contact) || TextUtils.isEmpty(EmailId) || TextUtils.isEmpty(Username) || TextUtils.isEmpty(College) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(Gender)){
            Toast.makeText(getActivity(), "All fields are mandatory!", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isValidPhoneNumber(Contact)){
            Toast.makeText(getActivity(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isNetworkAvailable()) {
            user = new User(uid,Status,Role, Username, Gender, EmailId, Contact, College, Password,isVerficationEmailSend,isEmailVerified);
            registerUser(EmailId, Password);
        }else{
            Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void setDisplayName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = name.getText().toString();
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

    public void registerUser(String EmailId,String Password){
        addUserProgressbar.setVisibility(View.VISIBLE);
        addUserProgressbar.setEnabled(false);
        addUser.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        mAuth.createUserWithEmailAndPassword(EmailId, Password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        addUserProgressbar.setVisibility(View.INVISIBLE);
                        addUserProgressbar.setEnabled(true);
                        addUser.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E3C72")));
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null){
                                setDisplayName();
                                Log.d(TAG, "Register done ");
                                Toast.makeText(getActivity(), "User added succesfully", Toast.LENGTH_SHORT).show();
                                if(isNetworkAvailable()) {
                                    updateUI(user);
                                }else{
                                    mAuth.getCurrentUser().delete();
                                }
                            }

                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                String errorMessage = exception.getMessage();
                                if (errorMessage != null && errorMessage.contains("The email address is already in use")) {
                                    Toast.makeText(getActivity(), "User already exists", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", exception);
                                    Toast.makeText(getActivity(), "Email is already regisered,Try logging in!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), " Activation link has been sent to organiser email", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireActivity(), "Error sending verification email", Toast.LENGTH_LONG).show();
                            Log.e("EmailVerification", "Error sending verification email: " + task.getException());
                        }
                    });
        }
    }

    public void updateUI(FirebaseUser user){
        if (user != null) {
            if(isNetworkAvailable()) {
                uid = user.getUid();
                User userdata = new User(uid,Status,Role, name.getText().toString(), Gender, email.getText().toString(),
                        phone.getText().toString(), college.getText().toString(), password.getText().toString(),isVerficationEmailSend,isEmailVerified);
                userData.document(uid).set(userdata).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                      getFragment(new manageUser());
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
}