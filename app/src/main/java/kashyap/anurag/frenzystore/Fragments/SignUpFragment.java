package kashyap.anurag.frenzystore.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import kashyap.anurag.frenzystore.Activities.MainActivity;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.FragmentSignUpBinding;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private FrameLayout parentFrameLayout;
    private String email, name, password, cPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(getLayoutInflater());
        parentFrameLayout = getActivity().findViewById(R.id.frameLayoutRegister);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.alreadyAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });




        return  binding.getRoot();
    }

    private void validateData() {
         email = binding.emailEt.getText().toString().trim();
         name = binding.nameEt.getText().toString().trim();
         password = binding.passwordEt.getText().toString().trim();
         cPassword = binding.cPasswordEt.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getActivity(), "Enter Valid Email!!!", Toast.LENGTH_SHORT).show();
        }else if (name.isEmpty()){
            Toast.makeText(getActivity(), "Enter Your Name!!!!", Toast.LENGTH_SHORT).show();
        }else if (password.length()<8){
            Toast.makeText(getActivity(), "Enter your Password of length must be 8 characters!!!!", Toast.LENGTH_SHORT).show();
        }else if (!cPassword.equals(password)){
            Toast.makeText(getActivity(), "Password doesn't matches!!!!", Toast.LENGTH_SHORT).show();
        }else {

            createAccountWithEmailAndPassword(email, password);
        }
    }

    private void setFragment(SignInFragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_from_rignt);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
    private void createAccountWithEmailAndPassword(String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getActivity(), "Account Created Successfully!!!", Toast.LENGTH_SHORT).show();
                        saveUserDataToDb();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataToDb() {

        long timestamp = System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", ""+firebaseAuth.getCurrentUser().getUid());
        hashMap.put("email", ""+email);
        hashMap.put("name", ""+name);
        hashMap.put("phoneNo", "");
        hashMap.put("profileImage", "");
        hashMap.put("userType", "users");
        hashMap.put("timestamp", ""+timestamp);

        DocumentReference documentReference = firebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "data Added to database!!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finishAffinity();

                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Failed adding data to database!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}