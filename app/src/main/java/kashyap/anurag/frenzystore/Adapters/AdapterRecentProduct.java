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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import kashyap.anurag.frenzystore.Models.GridModel;
import kashyap.anurag.frenzystore.Models.ModelRecentProduct;
import kashyap.anurag.frenzystore.R;

public class AdapterRecentProduct extends RecyclerView.Adapter<AdapterRecentProduct.HolderRecentProduct> {

    private Context context;
    private ArrayList<ModelRecentProduct> modelRecentProductArrayList;

    public AdapterRecentProduct(Context context, ArrayList<ModelRecentProduct> modelRecentProductArrayList) {
        this.context = context;
        this.modelRecentProductArrayList = modelRecentProductArrayList;
    }

    @NonNull
    @Override
    public HolderRecentProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recent_items, parent, false);
        return new HolderRecentProduct(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecentProduct holder, int position) {
        final  ModelRecentProduct modelRecentProduct = modelRecentProductArrayList.get(position);
        String productId = modelRecentProduct.getProductId();

        loadProductDetails(productId, holder);

    }

    private void loadProductDetails(String productId, HolderRecentProduct holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String productTitle = snapshot.get("productTitle").toString();
                String productImage = snapshot.get("productImage").toString();

                holder.productTitle.setText(productTitle);
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

                    }
                });
            }

        });
    }

    @Override
    public int getItemCount() {
        return modelRecentProductArrayList.size();
    }

    public class HolderRecentProduct extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;

        public HolderRecentProduct(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
        }
    }
}
