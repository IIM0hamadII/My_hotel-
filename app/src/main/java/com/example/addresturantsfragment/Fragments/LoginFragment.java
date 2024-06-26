package com.example.addresturantsfragment.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addresturantsfragment.Activities.MainActivity;
import com.example.addresturantsfragment.DataBase.FirebaseServices;
import com.example.addresturantsfragment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private EditText etUsername,etPassword;
    private TextView tvSignupLink;
    private TextView tvForgotPassLink;
    private Button btnLogin;
    private FirebaseServices fsb;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onStart() {

        super.onStart();
        fsb= FirebaseServices.getInstance();
        etUsername=getView().findViewById(R.id.etUsernameLogin);
        etPassword=getView().findViewById(R.id.etPasswordLogin);
        tvSignupLink=getView().findViewById(R.id.tvSignupLinkLogin);
        tvForgotPassLink= getView().findViewById(R.id.tvForgotPasswordLinkLogin);
        btnLogin=getView().findViewById(R.id.etButtomLogin);


        tvForgotPassLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoForgotPasswordFragment();
            }
        });

        tvSignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSignupFragment();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (username.trim().isEmpty() || password.trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Some fields are empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                fsb.getAuth().signInWithEmailAndPassword(username,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            fsb = FirebaseServices.reloadInstance();

                            gotoHotelList();
                            Toast.makeText(getActivity(), "Welcome ", Toast.LENGTH_SHORT).show();
                            setNavigationBarVisible();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "failed to login! check user or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    private void setNavigationBarVisible() {

        ((MainActivity)getActivity()).getBottomNavigationView().setVisibility(View.VISIBLE);

        if(!fsb.getAuth().getCurrentUser().getEmail().equals("hamoudy1221h@gmail.com")){

            // Get the menu from the navigation view.
            Menu menu = ((MainActivity)getActivity()).getBottomNavigationView().getMenu();

            // Get the admin navigation item.
            menu.findItem(R.id.action_add).setVisible(false);
        } else{
            // Get the menu from the navigation view.
            Menu menu = ((MainActivity)getActivity()).getBottomNavigationView().getMenu();

            // Get the admin navigation item.
            menu.findItem(R.id.action_add).setVisible(true);
        }
    }
    private void gotoSignupFragment(){
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,new SignupFragment());
        ft.commit();
    }
    private void gotoForgotPasswordFragment(){
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,new ForgotPasswordFragment());
        ft.commit();
    }

    public void gotoHotelList()
    {
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,new AllHotelsFragment());
        ft.commit();

    }
}

