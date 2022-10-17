package kashyap.anurag.frenzystore.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import kashyap.anurag.frenzystore.Adapters.ProductSpecificationAdapter;
import kashyap.anurag.frenzystore.Models.ProductSpecificationModel;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.FragmentProductSpecificationBinding;


public class ProductSpecificationFragment extends Fragment {

    private FragmentProductSpecificationBinding binding;
    private ProductSpecificationAdapter productSpecificationAdapter;
    public static List<ProductSpecificationModel> productSpecificationModelArrayList;

    public ProductSpecificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductSpecificationBinding.inflate(getLayoutInflater());


//        loadProductSpecification();

        return binding.getRoot();
    }

    private void loadProductSpecification() {


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.productSpecificationRv.setLayoutManager(linearLayoutManager);
        productSpecificationModelArrayList.add(new ProductSpecificationModel("Ram", "4GB"));

        ProductSpecificationAdapter productSpecificationAdapter = new ProductSpecificationAdapter(getContext(),productSpecificationModelArrayList);
        binding.productSpecificationRv.setAdapter(productSpecificationAdapter);
        productSpecificationAdapter.notifyDataSetChanged();
    }
}