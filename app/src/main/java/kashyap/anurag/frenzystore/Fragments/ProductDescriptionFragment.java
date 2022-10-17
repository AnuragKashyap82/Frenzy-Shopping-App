package kashyap.anurag.frenzystore.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kashyap.anurag.frenzystore.R;

import static kashyap.anurag.frenzystore.Activities.ProductDetailsActivity.productDescription;
import static kashyap.anurag.frenzystore.Activities.ProductDetailsActivity.productOtherDetails;

public class ProductDescriptionFragment extends Fragment {

    public ProductDescriptionFragment() {
        // Required empty public constructor
    }

    private TextView productDescriptionTv;
    public String body;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view =  inflater.inflate(R.layout.fragment_product_description, container, false);
        productDescriptionTv = view.findViewById(R.id.productDescriptionTv);


        productDescriptionTv.setText(body);


        return view;
    }

}