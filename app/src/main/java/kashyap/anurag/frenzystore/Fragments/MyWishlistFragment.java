package kashyap.anurag.frenzystore.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kashyap.anurag.frenzystore.Adapters.OrderItemAdapter;
import kashyap.anurag.frenzystore.Adapters.WishListAdapter;
import kashyap.anurag.frenzystore.Models.OrderItemModel;
import kashyap.anurag.frenzystore.Models.WishListModel;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.FragmentMyWishlistBinding;


public class MyWishlistFragment extends Fragment {
    private FragmentMyWishlistBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private WishListAdapter wishListAdapter;
    private ArrayList<WishListModel> wishListModelArrayList;

    public MyWishlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyWishlistBinding.inflate(getLayoutInflater());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        loadAllMyWishListItems();

        return binding.getRoot();
    }

    private void loadAllMyWishListItems() {
        binding.progressBar.setVisibility(View.VISIBLE);
        wishListModelArrayList = new ArrayList<>();
        firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("myWishlist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                WishListModel wishListModel = document.toObject(WishListModel.class);
                                wishListModelArrayList.add(wishListModel);
                            }
                            Collections.sort(wishListModelArrayList, new Comparator<WishListModel>() {
                                @Override
                                public int compare(WishListModel t1, WishListModel t2) {
                                    return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                                }
                            });
                            Collections.reverse(wishListModelArrayList);
                            wishListAdapter = new WishListAdapter(getActivity(), wishListModelArrayList, true);
                            binding.myWishlistRv.setAdapter(wishListAdapter);
                            wishListAdapter.notifyDataSetChanged();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}