package kashyap.anurag.frenzystore.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kashyap.anurag.frenzystore.Adapters.CartAdapter;
import kashyap.anurag.frenzystore.Adapters.OrderItemAdapter;
import kashyap.anurag.frenzystore.Models.CartItemModel;
import kashyap.anurag.frenzystore.Models.OrderItemModel;
import kashyap.anurag.frenzystore.Models.TrendingProductModel;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.FragmentMyOrdersBinding;


public class MyOrdersFragment extends Fragment {
    private FragmentMyOrdersBinding binding;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private OrderItemAdapter orderItemAdapter;
    private ArrayList<OrderItemModel> orderItemModelArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyOrdersBinding.inflate(getLayoutInflater());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        loadAllOrders();

        return binding.getRoot();
    }

    private void loadAllOrders() {
        binding.progressBar.setVisibility(View.VISIBLE);
        orderItemModelArrayList =new ArrayList<>();

        firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("myOrders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                OrderItemModel orderItemModel = document.toObject(OrderItemModel.class);
                                orderItemModelArrayList.add(orderItemModel);
                            }
                            Collections.sort(orderItemModelArrayList, new Comparator<OrderItemModel>() {
                                @Override
                                public int compare(OrderItemModel t1, OrderItemModel t2) {
                                    return t1.getOrderId().compareToIgnoreCase(t2.getOrderId());
                                }
                            });
                            Collections.reverse(orderItemModelArrayList);

                            orderItemAdapter = new OrderItemAdapter(getActivity(), orderItemModelArrayList);
                            binding.myOrdersRv.setAdapter(orderItemAdapter);
                            orderItemAdapter.notifyDataSetChanged();
                            binding.progressBar.setVisibility(View.GONE);
                        }else{
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Not Found!!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}