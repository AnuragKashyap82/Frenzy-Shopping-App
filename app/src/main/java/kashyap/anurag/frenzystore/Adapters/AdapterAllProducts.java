package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Activities.ProductDetailsActivity;
import kashyap.anurag.frenzystore.Models.ModelAllProduct;
import kashyap.anurag.frenzystore.R;

public class AdapterAllProducts extends RecyclerView.Adapter<AdapterAllProducts.HolderAllProduct>{

    private Context context;
    public ArrayList<ModelAllProduct> allProductArrayList;
    private String LAYOUT_CODE;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public AdapterAllProducts(Context context, ArrayList<ModelAllProduct> allProductArrayList, String LAYOUT_CODE) {
        this.context = context;
        this.allProductArrayList = allProductArrayList;
        this.LAYOUT_CODE = LAYOUT_CODE;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public HolderAllProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_grid_item, parent, false);
        return new HolderAllProduct(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAllProduct holder, int position) {

        final ModelAllProduct modelAllProduct = allProductArrayList.get(position);
        String productTitle = modelAllProduct.getProductTitle();
        String productImage = modelAllProduct.getProductImage();
        String productDescription = modelAllProduct.getProductDescription();
        String productId = modelAllProduct.getProductId();
        String productPrice = modelAllProduct.getProductPrice();

        holder.productTitle.setText(productTitle);
        holder.productDescription.setText(productDescription);
        holder.productPrice.setText("Rs."+productPrice);

        try {
            Picasso.get().load(productImage).fit().centerCrop().placeholder(R.drawable.ic_cart_black).into(holder.productImage);
        } catch (Exception e) {
            holder.productImage.setImageResource(R.drawable.ic_cart_black);
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
        if (allProductArrayList.size() > 8) {
            if (LAYOUT_CODE == "HOME") {
                return 8;
            } else {
                return allProductArrayList.size();
            }
        }else {
            return allProductArrayList.size();
        }

    }

    public class HolderAllProduct extends RecyclerView.ViewHolder {

        private TextView productTitle, productDescription, productPrice;
        private ImageView productImage;

        public HolderAllProduct(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);

        }
    }
}
