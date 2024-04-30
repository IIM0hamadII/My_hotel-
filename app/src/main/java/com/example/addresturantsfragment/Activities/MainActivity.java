package com.example.addresturantsfragment.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.addresturantsfragment.Fragments.AddHotelFragment;
import com.example.addresturantsfragment.Fragments.AllHotelsFragment;
import com.example.addresturantsfragment.DataBase.FirebaseServices;
import com.example.addresturantsfragment.Fragments.ListFragmentType;
import com.example.addresturantsfragment.Fragments.LoginFragment;
import com.example.addresturantsfragment.Fragments.ProfileFragment;
import com.example.addresturantsfragment.R;
import com.example.addresturantsfragment.DataBase.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private FirebaseServices fbs;
    private BottomNavigationView bottomNavigationView;
    private ListFragmentType listType;
    private Stack<Fragment> fragmentStack = new Stack<>();
    private FrameLayout fragmentContainer;
    private User userData, user1;
    private ImageView imj;
    Intent btn;

    @Override
    public void onStart() {
        super.onStart();
     if(fbs.getCurrentUser()==null){
         gotoLoginFragment();
         bottomNavigationView.setVisibility(View.GONE);
     }
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {


          imj=findViewById(R.id.imageView4);
        fbs = FirebaseServices.getInstance();
        imj.setVisibility(View.VISIBLE);      //fbs.getAuth().signOut();
        listType = ListFragmentType.Regular;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.action_home) {


                    selectedFragment = new AllHotelsFragment();
                }
                 else if ( item.getItemId() == R.id.action_add  ) {
                     selectedFragment = new AddHotelFragment();

                     }
                 else if (item.getItemId() == R.id.action_profile ) {
                    selectedFragment = new ProfileFragment();
                   }

                else if (item.getItemId() == R.id.action_signout) {

                    signout();
                    bottomNavigationView.setVisibility(View.GONE);
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, selectedFragment).commit();

                }
                return true;
            }
        });
        fragmentContainer = findViewById(R.id.frameLayout);
        userData = getUserData();

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (fbs.getAuth().getCurrentUser() == null) {
            gotoLoginFragment();
            bottomNavigationView.setVisibility(View.GONE);

            pushFragment(new LoginFragment());
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
            // fbs.getCurrentObjectUser();
            gotoHotelList();
            pushFragment(new AllHotelsFragment());
        }
    }

    public User getUserDataObject() {
        return this.userData;
    }

    private void signout() {
        fbs.getAuth().signOut();
        bottomNavigationView.setVisibility(View.INVISIBLE);
        gotoLoginFragment();
    }


    private void gotoLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, new LoginFragment());
        ft.commit();
    }

    public void gotoHotelList() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, new AllHotelsFragment());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentStack.size() > 1) {
            fragmentStack.pop(); // Remove the current fragment from the stack
            Fragment previousFragment = fragmentStack.peek(); // Get the previous fragment

            // Replace the current fragment with the previous one
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, previousFragment)
                    .commit();
        } else {
            super.onBackPressed(); // If there's only one fragment left, exit the app
        }
    }

    // Method to add a new fragment to the stack
    public void pushFragment(Fragment fragment) {
        fragmentStack.push(fragment);
        /*
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit(); */
    }

    public User getUserData() {
        final User[] currentUser = {null};
        try {
            fbs.getFire().collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    User user = document.toObject(User.class);
                                    if (fbs.getAuth().getCurrentUser() != null ) {
                                        //if (fbs.getAuth().getCurrentUser().getEmail().equals(user.getUsername())) {
                                        currentUser[0] = document.toObject(User.class);
                                        fbs.setCurrentUser(currentUser[0]);
                                    }
                                }
                            } else {
                                Log.e("AllRestActivity: readData()", "Error getting documents.", task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error reading!" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return currentUser[0];


    }
}