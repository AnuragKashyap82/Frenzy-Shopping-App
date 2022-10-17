package kashyap.anurag.frenzystore.Fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.util.Collections;
import java.util.Comparator;

import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Activities.AddAddressActivity;
import kashyap.anurag.frenzystore.Adapters.AdapterSaveForLater;
import kashyap.anurag.frenzystore.Adapters.CartAdapter;
import kashyap.anurag.frenzystore.Models.CartItemModel;
import kashyap.anurag.frenzystore.Models.ModelSaveForLater;
import kashyap.anurag.frenzystore.R;


public class CartFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private CartAdapter cartAdapter;
    public static ArrayList<CartItemModel> cartItemModelArrayList;

    private AdapterSaveForLater adapterSaveForLater;
    private ArrayList<ModelSaveForLater> saveForLaterArrayList;

    private ProgressBar progressBar;
    private RecyclerView cartItemsRv, saveForLaterRv;

    private TextView totalItemsPrice, cartContinueBtn, priceTv, deliveryPrice,
            discountPrice, totalPrice, totalCartAmount, savedAmount;

    private int totalOriginalPrice = 0;
    private int totalCostPrice = 0;
    private int totalDeliveryPrice = 0;
    private int totalDiscount = 0;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        cartContinueBtn = view.findViewById(R.id.cartContinueBtn);
        progressBar = view.findViewById(R.id.progressBar);
        totalItemsPrice = view.findViewById(R.id.totalItemsPrice);
        cartItemsRv = view.findViewById(R.id.cartItemsRv);
        saveForLaterRv = view.findViewById(R.id.saveForLaterRv);
        priceTv = view.findViewById(R.id.priceTv);
        deliveryPrice = view.findViewById(R.id.deliveryPrice);
        discountPrice = view.findViewById(R.id.discountPrice);
        totalPrice = view.findViewById(R.id.totalPrice);
        totalCartAmount = view.findViewById(R.id.totalCartAmount);
        savedAmount = view.findViewById(R.id.savedAmount);

        loadAllCartItems();
        loadAllSaveForLaterItems();

        cartContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddAddressActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadAllCartItems() {
        progressBar.setVisibility(View.VISIBLE);
        cartItemModelArrayList =new ArrayList<>();

        firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("myCart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CartItemModel cartItemModel = document.toObject(CartItemModel.class);
                                cartItemModelArrayList.add(cartItemModel);

                                loadProductDetails(cartItemModel);
                            }

                            Collections.sort(cartItemModelArrayList, new Comparator<CartItemModel>() {
                                @Override
                                public int compare(CartItemModel t1, CartItemModel t2) {
                                    return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                                }
                            });
                            Collections.reverse(cartItemModelArrayList);

                            cartAdapter = new CartAdapter(getActivity(), cartItemModelArrayList);
                            cartItemsRv.setAdapter(cartAdapter);
                            cartAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void loadProductDetails(CartItemModel cartItemModel) {

        String productId = cartItemModel.getProductId();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String productTitle = snapshot.get("productTitle").toString();
                String productImage = snapshot.get("productImage").toString();
                String productPrice1 = snapshot.get("productPrice").toString();
                String cuttedPrice1 = snapshot.get("productCuttedPrice").toString();
                String productDescription = snapshot.get("productDescription").toString();
                String deliveryFee = snapshot.get("deliveryFee").toString();
                String avgRating = snapshot.get("avgRating").toString();
                String deliveryWithin = snapshot.get("deliveryWithin").toString();

               int oneTypeCuttedPrice = Integer.parseInt(cuttedPrice1);
               int oneTypeCostPrice = Integer.parseInt(productPrice1);

                totalOriginalPrice = oneTypeCuttedPrice + totalOriginalPrice;
                totalCostPrice = oneTypeCostPrice + totalCostPrice;

                totalItemsPrice.setText("Rs."+totalOriginalPrice);
                priceTv.setText("Rs."+totalCostPrice);

                if (deliveryFee.equals("FREE Delivery")){
                    deliveryFee = "0";
                    int oneTypeDeliveryFee = Integer.parseInt(deliveryFee);
                    totalDeliveryPrice = totalDeliveryPrice + oneTypeDeliveryFee;
                    deliveryPrice.setText("Rs."+totalDeliveryPrice);
                }else {
                    int oneTypeDeliveryFee  = Integer.parseInt(deliveryFee);
                    totalDeliveryPrice = totalDeliveryPrice + oneTypeDeliveryFee;
                    deliveryPrice.setText("Rs."+totalDeliveryPrice);
                }
                int discount = Integer.parseInt(cuttedPrice1) - Integer.parseInt(productPrice1);
                totalDiscount = totalDiscount + discount;
                discountPrice.setText("Rs."+totalDiscount);

                int totalAmount = totalCostPrice + totalDeliveryPrice;
                totalPrice.setText("Rs."+ totalAmount);
                totalCartAmount.setText("Rs."+ totalAmount);

                savedAmount.setText("You Saved Rs."+ totalDiscount+" on this Order");
            }
        });
    }

    private void loadAllSaveForLaterItems(){
        progressBar.setVisibility(View.VISIBLE);
        saveForLaterArrayList =new ArrayList<>();

        firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("saveForLater")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelSaveForLater modelSaveForLater = document.toObject(ModelSaveForLater.class);
                                saveForLaterArrayList.add(modelSaveForLater);
                            }
                            Collections.sort(saveForLaterArrayList, new Comparator<ModelSaveForLater>() {
                                @Override
                                public int compare(ModelSaveForLater t1, ModelSaveForLater t2) {
                                    return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                                }
                            });
                            Collections.reverse(saveForLaterArrayList);

                            adapterSaveForLater = new AdapterSaveForLater(getActivity(), saveForLaterArrayList);
                            saveForLaterRv.setAdapter(adapterSaveForLater);
                            adapterSaveForLater.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}