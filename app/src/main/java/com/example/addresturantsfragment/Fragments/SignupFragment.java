package com.example.addresturantsfragment.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.addresturantsfragment.Activities.MainActivity;
import com.example.addresturantsfragment.DataBase.FirebaseServices;
import com.example.addresturantsfragment.DataBase.User;
import com.example.addresturantsfragment.R;
import com.example.addresturantsfragment.Utilites.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 123;
    private EditText etUsername,etPassword, etConfirmPassword,
            etFirstname, etLastname, etPhone;
    ImageView ivUserPhoto;
    private Button btnSignup,btnBack;
    private BottomNavigationView bottomNavigationView;
    private FirebaseServices fbs;
    private Utils msg;

    // TODO:signUp check (Image)
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
        // connecting components
        fbs=FirebaseServices.getInstance();
        btnBack=getView().findViewById(R.id.back);
        etUsername=getView().findViewById(R.id.etUsernameSignup);
        etPassword=getView().findViewById(R.id.etPasswordSignup);
        etFirstname = getView().findViewById(R.id.etFirstnameSignupFragment);
        etLastname = getView().findViewById(R.id.etLastnameSignupFragment);
        etConfirmPassword = getView().findViewById(R.id.etConfirmPasswordSignupFragment);
        etPhone = getView().findViewById(R.id.etPhoneSignupFragment);
        btnSignup=getView().findViewById(R.id.btnSignupSignup);
        ivUserPhoto = getView().findViewById(R.id.ivPhotoSignupFragment);
        msg = Utils.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gotoLoginFragment();
                setNavigationBarGone();
            }
        });

        ivUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Data validation
                String email=etUsername.getText().toString();
                String password=etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String firstname = etFirstname.getText().toString();
                String lastname = etLastname.getText().toString();
                String phone = etPhone.getText().toString();


                if (email.trim().isEmpty() || password.trim().isEmpty() || firstname.trim().isEmpty() ||
                        lastname.trim().isEmpty() || confirmPassword.trim().isEmpty() || phone.trim().isEmpty())

                {
                    Toast.makeText(getActivity(), "some fields are empty", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (!password.equals(confirmPassword)) {
                    msg.showMessageDialog(getActivity(), "Password are not identical!");
                    return;
                }

//                if(fbs.getSelectedImageURL() == null){
//                    msg.showMessageDialog(getActivity(), "Image are Empty !");
//
//                }


//                if (fbs.getSelectedImageURL() == null)
//                {
//                    User user = new User(firstname, lastname, username, phone, address, "");
//                }
//                else {
//                    User user = new User(firstname, lastname, username, phone, address, fbs.getSelectedImageURL().toString());
//                }
                //Signup procedure
                Uri selectedImageUri = fbs.getSelectedImageURL();
                String imageURL = "";
                if (selectedImageUri != null) {
                    imageURL = selectedImageUri.toString();
                }
                User user = new User(firstname, lastname,  phone,  imageURL,password,email);

                fbs.getAuth().createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful())
                                {
                                    fbs.getFire().collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            fbs= FirebaseServices.reloadInstance();
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


            }
        });
        ((MainActivity)getActivity()).pushFragment(new SignupFragment());
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }
    private void setNavigationBarGone() {
        ((MainActivity)getActivity()).getBottomNavigationView().setVisibility(View.GONE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            ivUserPhoto.setImageURI(selectedImageUri);
            Utils.getInstance().uploadImage(getActivity(), selectedImageUri);
        }
    }

    public void gotoHotelList()
    {
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,new AllHotelsFragment());
        ft.commit();
        setNavigationBarVisible();
    }
    public void  gotoLoginFragment()
    {
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,new LoginFragment());
        ft.commit();
        setNavigationBarVisible();
    }

    private void setNavigationBarVisible() {

        ((MainActivity)getActivity()).getBottomNavigationView().setVisibility(View.VISIBLE);

        if(!fbs.getAuth().getCurrentUser().getEmail().equals("hamoudy1221h@gmail.com")){

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
}

