package kashyap.anurag.frenzystore.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kashyap.anurag.frenzystore.Adapters.RewardsAdapter;
import kashyap.anurag.frenzystore.Adapters.WishListAdapter;
import kashyap.anurag.frenzystore.Models.RewardModel;
import kashyap.anurag.frenzystore.Models.WishListModel;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.FragmentMyRewardsBinding;


public class MyRewardsFragment extends Fragment {
    private FragmentMyRewardsBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private RewardsAdapter rewardsAdapter;
    private ArrayList<RewardModel> rewardModelArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyRewardsBinding.inflate(getLayoutInflater());

        firebaseFirestore = FirebaseFirestore.getInstance();
        loadAllMyRewards();

        return binding.getRoot();
    }

    private void loadAllMyRewards() {
        rewardModelArrayList = new ArrayList<>();

        firebaseFirestore.collection("myRewards")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RewardModel rewardModel = document.toObject(RewardModel.class);
                                rewardModelArrayList.add(rewardModel);
                            }
                            rewardsAdapter = new RewardsAdapter(getActivity(), rewardModelArrayList);
                            binding.myRewardsRv.setAdapter(rewardsAdapter);
                            rewardsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}