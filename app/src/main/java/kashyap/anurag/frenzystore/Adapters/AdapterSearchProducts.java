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
import kashyap.anurag.frenzystore.Models.ModelSearchProduct;
import kashyap.anurag.frenzystore.R;

public class AdapterSearchProducts extends RecyclerView.Adapter<AdapterSearchProducts.HolderSearchProducts> implements Filterable {

    private Context context;
    public ArrayList<ModelSearchProduct> searchProductArrayList, filterList;
    private FirebaseAuth firebaseAuth;
    private FilterProducts filter;


    public AdapterSearchProducts(Context context, ArrayList<ModelSearchProduct> searchProductArrayList) {
        this.context = context;
        this.searchProductArrayList = searchProductArrayList;
        this.filterList = searchProductArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderSearchProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_search_item, parent, false);
        return new HolderSearchProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSearchProducts holder, int position) {
        final ModelSearchProduct modelSearchProduct = searchProductArrayList.get(position);
        String productId = modelSearchProduct.getProductId();
        String productImage = modelSearchProduct.getProductImage();
        String productTitle = modelSearchProduct.getProductTitle();
        String category = modelSearchProduct.getCategory();

        holder.productCategory.setText("in "+category);
        holder.productTitle.setText(productTitle);
        try {
            Picasso.get().load(productImage).placeholder(R.drawable.ic_cart_black).into(holder.productImage);
        } catch (Exception e) {
            holder.productImage.setImageResource(R.drawable.ic_home_black);
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
        return searchProductArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProducts(filterList, this);

        }
        return filter;
    }

    public class HolderSearchProducts extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle, productCategory;

        public HolderSearchProducts(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productCategory = itemView.findViewById(R.id.productCategory);
        }
    }
}
