package com.example.addresturantsfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    private EditText etUsername,etPassword,etName,etLastName,etPhone,etHobbies,etLivingPlace;
    private Button btnSignup,btnBack;
    private FirebaseServices fsb;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }
    @Override
    public void onStart() {

        super.onStart();
        fsb= FirebaseServices.getInstance();
        etUsername=getView().findViewById(R.id.etUsernameSignup);
        etPassword=getView().findViewById(R.id.etPasswordSignup);
        btnSignup=getView().findViewById(R.id.etButtonSignup);
        btnBack=getView().findViewById(R.id.back);
        etName=getView().findViewById(R.id.etName);
        etLastName=getView().findViewById(R.id.etLastname);
        etHobbies=getView().findViewById(R.id.ethobbies);
        etPhone=getView().findViewById(R.id.etPhone);
        etLivingPlace=getView().findViewById(R.id.etLivingPlace);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLoginFragment();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: get all strings from add fragment in addition to username and password
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String Name= etName.getText().toString();
                String LastName=etLastName.getText().toString();
                String Phone= etPhone.getText().toString();
                String Hobbies = etHobbies.getText().toString();
                String LivingPlace= etLivingPlace.getText().toString();

                // TODO: check all other fields too
                if (username.trim().isEmpty() && password.trim().isEmpty() && Name.trim().isEmpty()&& LastName.trim().isEmpty()&&Phone.trim().isEmpty()
                && Hobbies.trim().isEmpty()&& LivingPlace.trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Some fields are empty!", Toast.LENGTH_SHORT).show();
                    return;
                }


                User user = new User(Name, LastName, username,Phone, LivingPlace);

                fsb.getAuth().createUserWithEmailAndPassword(username,password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful())
                                {
                                    fsb.getFire().collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            gotoHotelList();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("SignupFragment: signupOnClick: ", e.getMessage());
                                        }
                                    });
                                    // String firstName, String lastName, String username, String phone, String address, String photo) {
                                    Toast.makeText(getActivity(), "you have succesfully signed up", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), "failed to sign up! check user or password", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
        };
      });

    }

    private void gotoMainFragment(){
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,new HotelsFragment());
        ft.commit();
    }
    private void gotoLoginFragment(){
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,new LoginFragment());
        ft.commit();
    }

    public void gotoHotelList()
    {
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,new AllHotelsFragment());
        ft.commit();

    }
}