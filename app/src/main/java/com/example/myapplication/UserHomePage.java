package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.RegisteredEvents.RegisteredEventsList;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.fragements.AboutUsPage;
import com.example.myapplication.fragements.Announcement;
import com.example.myapplication.fragements.SettingPage;
import com.example.myapplication.fragements.Support;
import com.example.myapplication.fragements.UserHome;
import com.example.myapplication.ManageUser.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserHomePage extends AppCompatActivity implements Announcement.NotificationListener {
    DrawerLayout drawerLayout;
    ImageButton DrawerButtonToggle;
    TextView userName,userEmail,welcomeName,Date;
    FirebaseUser user;
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    View notificationDot;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home_page);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressButton();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.navigation_View);
        View headerview=navigationView.getHeaderView(0);
        userName=headerview.findViewById(R.id.name);
        userEmail=headerview.findViewById(R.id.email);
        welcomeName=findViewById(R.id.welcome);
        Date=findViewById(R.id.date);
        notificationDot = findViewById(R.id.notification_dot);


        DrawerButtonToggle=findViewById(R.id.DrawerButtonToggle);
        DrawerButtonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("DrawerClick", "Item Clicked: " + item.getTitle());

                int id=item.getItemId();
                if(id==R.id.logout){
                    AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UserHomePage.this);
                    builder.setTitle("Logout");
                    builder.setMessage(" Are you sure you want to Logout ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            Toast.makeText(UserHomePage.this, "Logout Succesfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(UserHomePage.this,LoginPage.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                }else if(id==R.id.setting){
                    Toast.makeText(UserHomePage.this, "Settng Page ", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getFragment(new SettingPage());
                }else if(id==R.id.registerEvents){
                    Toast.makeText(UserHomePage.this, "Register Events Page ", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getFragment( new RegisteredEventsList());
                }else if(id==R.id.support){
                    Toast.makeText(UserHomePage.this, "Support Page ", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getFragment(new Support());
                }else if(id==R.id.info){
                    Toast.makeText(UserHomePage.this, "Info Page ", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getFragment(new AboutUsPage());
                }
                return true;
            }
        });
        setDrawerProfile();

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            getFragment(new UserHome());
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();

                if(id==R.id.Home){
                    getFragment(new UserHome());
                    return true;
                }else if(id==R.id.User){
                    getFragment(new UserProfile());
                    return true;
                }else if(id==R.id.Announcement){
                    getFragment(new Announcement());
                    return true;
                }
                return true;
            }
        });
        checkForNotifications();
    }

    private void checkForNotifications() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Notifications")
                .whereEqualTo("seen", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        notificationDot.setVisibility(View.VISIBLE);
                    } else {
                        notificationDot.setVisibility(View.GONE);
                    }
                });
    }
    @Override
    public void markNotificationsAsRead() {
        if (notificationDot != null) {
            notificationDot.setVisibility(View.GONE);
        }
    }
    public void onBackPressButton() {
        if (bottomNavigationView.getSelectedItemId() == R.id.Home) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        finish();
                    })
                    .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss())
                    .setCancelable(true)
                    .create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.Home);
        }
    }


    public void setDrawerProfile(){
        user=mAuth.getCurrentUser();
        if (user!=null){
            String name=user.getDisplayName();
            String email=user.getEmail();

            if(name!=null){
                userName.setText(name);
                welcomeName.setText("Welcome , "+name);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");
                String currentDate = sdf.format(Calendar.getInstance().getTime());
                Date.setText(currentDate);
            }else{
                Toast.makeText(this, "Update your profile!", Toast.LENGTH_SHORT).show();
            }
            if(email!=null){
                userEmail.setText(email);
            }
        }else{
            Toast.makeText(this, "UserProfile not Authenticiated", Toast.LENGTH_SHORT).show();
        }

    }

    public void getFragment(Fragment fragment) {
        if (fragment instanceof AdminHome) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_layout, fragment)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}