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

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Models.TrendingProductModel;
import kashyap.anurag.frenzystore.Activities.ProductDetailsActivity;
import kashyap.anurag.frenzystore.R;

public class TrendingProductAdapter extends RecyclerView.Adapter<TrendingProductAdapter.ViewHolder> {


    private Context context;
    private List<TrendingProductModel> trendingProductModelList;
    private String LAYOUT_CODE;
    private FirebaseAuth firebaseAuth;

    public TrendingProductAdapter(Context context, List<TrendingProductModel> trendingProductModelList, String LAYOUT_CODE) {
        this.context = context;
        this.trendingProductModelList = trendingProductModelList;
        this.LAYOUT_CODE = LAYOUT_CODE;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_horizantal_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final TrendingProductModel trendingProductModel = trendingProductModelList.get(position);

        String productId = trendingProductModel.getProductId();
        String trendingCount = trendingProductModel.getTrendingCount();

        loadProductDetails(productId, holder);


    }

    private void loadProductDetails(String productId, ViewHolder holder) {
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
        if (trendingProductModelList.size() > 9) {
            if (LAYOUT_CODE == "HOME") {
                return 8;
            } else {
                return trendingProductModelList.size();
            }
        }else {
            return trendingProductModelList.size();
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView productTitleHS, productDescriptionHS, productPriceHS;
        private ImageView productImageHS;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productTitleHS = itemView.findViewById(R.id.productTitleHS);
            productDescriptionHS = itemView.findViewById(R.id.productDescriptionHS);
            productPriceHS = itemView.findViewById(R.id.productPriceHS);
            productImageHS = itemView.findViewById(R.id.productImageHS);
        }
    }
}
