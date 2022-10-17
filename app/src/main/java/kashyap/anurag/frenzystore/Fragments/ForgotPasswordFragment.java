package kashyap.anurag.frenzystore.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.FragmentForgotPasswordBinding;

import android.transition.TransitionManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private FrameLayout parentFrameLayout;
    private FirebaseAuth firebaseAuth;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(getLayoutInflater());
        parentFrameLayout = getActivity().findViewById(R.id.frameLayoutRegister);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });

       return binding.getRoot();
    }

    private void setFragment(SignInFragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_from_rignt);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void validateData() {
        String email = binding.emailEt.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getContext(), "Enter Valid Email!!", Toast.LENGTH_SHORT).show();
        }else {
            sendVerificationEmail(email);
        }
    }

    private void sendVerificationEmail(String email) {
        binding.resetPassBtn.setEnabled(false);

        TransitionManager.beginDelayedTransition(binding.ll);
        binding.ll.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            binding.errorTv.setVisibility(View.VISIBLE);
                            binding.errorTv.setText("Recovery email sent successfully! Check your Inbox");
                            binding.errorTv.setTextColor(getResources().getColor(R.color.successGreen));
                            TransitionManager.beginDelayedTransition(binding.ll);

                        }else {

                            binding.errorTv.setText(task.getException().getMessage());
                            binding.errorTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                            TransitionManager.beginDelayedTransition(binding.ll);
                            binding.resetPassBtn.setEnabled(true);

                        }
                        binding.progressBar.setVisibility(View.GONE);

                    }
                });
    }
}