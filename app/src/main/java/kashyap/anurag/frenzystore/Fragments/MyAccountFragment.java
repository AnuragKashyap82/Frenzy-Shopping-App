package kashyap.anurag.frenzystore.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.SimpleItemAnimator;
import kashyap.anurag.frenzystore.Activities.DeliveryActivity;
import kashyap.anurag.frenzystore.Activities.MyAddressActivity;
import kashyap.anurag.frenzystore.Activities.MyQuestionsActivity;
import kashyap.anurag.frenzystore.Activities.MyReviewsActivity;
import kashyap.anurag.frenzystore.Activities.ProfileActivity;
import kashyap.anurag.frenzystore.Activities.RegisterActivity;
import kashyap.anurag.frenzystore.Adapters.AddressAdapter;
import kashyap.anurag.frenzystore.Models.AddressModel;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.FragmentMyAccountBinding;


public class MyAccountFragment extends Fragment {
    private FragmentMyAccountBinding binding;
    public  static final int MANAGE_ADDRESS = 1;
    private FirebaseAuth firebaseAuth;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyAccountBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();

        binding.viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyAddressActivity.class);
                intent.putExtra("MODE", MANAGE_ADDRESS);
                startActivity(intent);
            }
        });
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        binding.myReviewsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyReviewsActivity.class));
            }
        });
        binding.myQuestionsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyQuestionsActivity.class));
            }
        });
        binding.ordersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.wishlistTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.couponsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.helpCenterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        return binding.getRoot();
    }

    private void loadMyInfo() {
        binding.progressBar.setVisibility(View.VISIBLE);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()){
                    String name = snapshot.get("name").toString();
                    String email = snapshot.get("email").toString();
                    String phoneNo = snapshot.get("phoneNo").toString();
                    String profileImage = snapshot.get("profileImage").toString();

                    binding.nameTv.setText("Hey! "+name);


                    binding.progressBar.setVisibility(View.GONE);
                }else {

                }
            }
        });
    }
    private void showLogoutDialog() {
        Dialog logoutDialog = new Dialog(getActivity());
        logoutDialog.setContentView(R.layout.logout_dialog);
        logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView cancelBtn = logoutDialog.findViewById(R.id.cancelBtn);
        TextView logoutBtn = logoutDialog.findViewById(R.id.logoutBtn);
        logoutDialog.setCancelable(true);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog.dismiss();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog.dismiss();
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), RegisterActivity.class));
                getActivity().finishAffinity();
            }
        });
        logoutDialog.show();
    }
}