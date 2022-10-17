package kashyap.anurag.frenzystore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import kashyap.anurag.frenzystore.Fragments.SignInFragment;
import kashyap.anurag.frenzystore.Fragments.SignUpFragment;
import kashyap.anurag.frenzystore.databinding.ActivityRegisterBinding;

import android.os.Bundle;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    public static boolean setSignUpFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (setSignUpFragment){
            setSignUpFragment = false;
            setFragment(new SignUpFragment());
        }else {
            setFragment(new SignInFragment());
        }


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.frameLayoutRegister.getId(), fragment);
        fragmentTransaction.commit();
    }
}