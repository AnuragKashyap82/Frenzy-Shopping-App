package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Activities.ProductDetailsActivity;
import kashyap.anurag.frenzystore.Models.ModelMostSoldProduct;
import kashyap.anurag.frenzystore.Models.TrendingProductModel;
import kashyap.anurag.frenzystore.R;

public class AdapterMostSoldProducts extends RecyclerView.Adapter<AdapterMostSoldProducts.HolderMostSoldProducts> {

    private Context context;
    private ArrayList<ModelMostSoldProduct> mostSoldProductArrayList;
    private FirebaseAuth firebaseAuth;

    public AdapterMostSoldProducts(Context context, ArrayList<ModelMostSoldProduct> mostSoldProductArrayList) {
        this.context = context;
        this.mostSoldProductArrayList = mostSoldProductArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderMostSoldProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_horizantal_item_layout, parent, false);
        return new HolderMostSoldProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMostSoldProducts holder, int position) {
        final ModelMostSoldProduct modelMostSoldProduct = mostSoldProductArrayList.get(position);

        String productId = modelMostSoldProduct.getProductId();
        String trendingCount = modelMostSoldProduct.getTrendingCount();

        loadProductDetails(productId, holder);
    }
    private void loadProductDetails(String productId, HolderMostSoldProducts holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()){
                    String productTitle = snapshot.get("productTitle").toString();
                    String productImage = snapshot.get("productImage").toString();
                    String productPrice = snapshot.get("productPrice").toString();
                    String productDescription = snapshot.get("productDescription").toString();

                    holder.productTitleHS.setText(productTitle);
                    holder.productDescriptionHS.setText(productDescription);
                    holder.productPriceHS.setText("Rs."+productPrice);

                    try {
                        Picasso.get().load(productImage).fit().centerCrop().placeholder(R.drawable.ic_cart_black).into(holder.productImageHS);
                    } catch (Exception e) {
                        holder.productImageHS.setImageResource(R.drawable.ic_cart_black);
                    }
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ProductDetailsActivity.class);
                            intent.putExtra("productId", productId);
                            intent.putExtra("productTitle", productTitle);

                            context.startActivity(intent);
                            addToRecentProducts(productId);
                        }
                    });
                }
            }
        });
    }

    private void addToRecentProducts(String productId) {

        long timestamp = System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("productId", productId);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("uid", firebaseAuth.getUid());

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users")
                .document(firebaseAuth.getUid()).collection("recentProducts").document(productId);
        documentReference.set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Added to recently Viewed!!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mostSoldProductArrayList.size();
    }

    public class HolderMostSoldProducts extends RecyclerView.ViewHolder {

        private TextView productTitleHS, productDescriptionHS, productPriceHS;
        private ImageView productImageHS;

        public HolderMostSoldProducts(@NonNull View itemView) {
            super(itemView);

            productTitleHS = itemView.findViewById(R.id.productTitleHS);
            productDescriptionHS = itemView.findViewById(R.id.productDescriptionHS);
            productPriceHS = itemView.findViewById(R.id.productPriceHS);
            productImageHS = itemView.findViewById(R.id.productImageHS);
        }
    }
}
