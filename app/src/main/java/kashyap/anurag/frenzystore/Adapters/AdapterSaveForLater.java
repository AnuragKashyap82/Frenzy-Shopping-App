package kashyap.anurag.frenzystore.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import kashyap.anurag.frenzystore.Activities.DeliveryActivity;
import kashyap.anurag.frenzystore.Activities.ProductDetailsActivity;
import kashyap.anurag.frenzystore.Models.ModelSaveForLater;
import kashyap.anurag.frenzystore.R;

public class AdapterSaveForLater extends RecyclerView.Adapter<AdapterSaveForLater.HolderSaveForLater> {

    private Context context;
    private ArrayList<ModelSaveForLater> saveForLaterArrayList;
    private FirebaseAuth firebaseAuth;

    public AdapterSaveForLater(Context context, ArrayList<ModelSaveForLater> saveForLaterArrayList) {
        this.context = context;
        this.saveForLaterArrayList = saveForLaterArrayList;
        firebaseAuth  = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderSaveForLater onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_save_for_later, parent, false);
        return new HolderSaveForLater(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSaveForLater holder, int position) {
        final ModelSaveForLater modelSaveForLater = saveForLaterArrayList.get(position);
        String productId = modelSaveForLater.getProductId();

        loadProductDetails(productId, holder);
        holder.removeFromSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog cartRemoveDialog = new Dialog(context);
                cartRemoveDialog.setContentView(R.layout.cod_dialog);
                cartRemoveDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView yesBtn = cartRemoveDialog.findViewById(R.id.yesBtn);
                TextView noBtn = cartRemoveDialog.findViewById(R.id.noBtn);
                TextView textView4 = cartRemoveDialog.findViewById(R.id.textView4);

                textView4.setText("Are you sure to Remove from save For Later");

                cartRemoveDialog.setCancelable(false);

                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartRemoveDialog.dismiss();
                    }
                });
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeFromSaveForLater(productId);
                        cartRemoveDialog.dismiss();
                    }
                });
                cartRemoveDialog.show();
            }
        });
        holder.moveToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToMyCart(productId);
            }
        });
    }

    private void addProductToMyCart(String productId) {
        long timestamp = System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("productId", productId);
        hashMap.put("timestamp", ""+timestamp);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myCart").document(productId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Product Added To Cart!!!!", Toast.LENGTH_SHORT).show();
                            removeFromSaveForLater(productId);
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

    private void removeFromSaveForLater(String productId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("saveForLater").document(productId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Product removed from save for later!!!", Toast.LENGTH_SHORT).show();
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

    private void loadProductDetails(String productId, HolderSaveForLater holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String productTitle = snapshot.get("productTitle").toString();
                String productImage = snapshot.get("productImage").toString();
                String productPrice1 = snapshot.get("productPrice").toString();
                String cuttedPrice1 = snapshot.get("productCuttedPrice").toString();
                String productDescription = snapshot.get("productDescription").toString();
                String avgRating = snapshot.get("avgRating").toString();

                holder.productTitle.setText(productTitle);
                holder.cuttedPrice.setText("Rs."+cuttedPrice1);
                holder.productPrice.setText("Rs."+productPrice1);
                holder.productDescription.setText(productDescription);

                if (avgRating == null){
                    holder.ratingBar.setRating(Float.parseFloat(avgRating));
                }

                holder.cuttedPrice.setPaintFlags(holder.cuttedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                int percentDiscount = ((Integer.parseInt(cuttedPrice1) - Integer.parseInt(productPrice1)) * 100)/Integer.parseInt(cuttedPrice1);
                holder.discountPercent.setText(percentDiscount+"% OFF");

                try {
                    Picasso.get().load(productImage).fit().centerCrop().placeholder(R.drawable.ic_cart_black).into(holder.productImage);
                } catch (Exception e) {
                    holder.productImage.setImageResource(R.drawable.ic_cart_black);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return saveForLaterArrayList.size();
    }

    public class HolderSaveForLater extends RecyclerView.ViewHolder {

        private TextView productTitle, productQuantity, productPrice, cuttedPrice;
        private ImageView productImage;
        private TextView productDescription, deliveryAmount, deliveryWithinTv, freeDeliveryTv, discountPercent, moveToCartBtn, removeFromSaveBtn;
        private RatingBar ratingBar;

        public HolderSaveForLater(@NonNull View itemView) {
            super(itemView);

            productTitle = itemView.findViewById(R.id.productTitleTv);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productImage = itemView.findViewById(R.id.productImage);
            productPrice = itemView.findViewById(R.id.productPrice);
            cuttedPrice = itemView.findViewById(R.id.cuttedPrice);
            productDescription = itemView.findViewById(R.id.productDescription);
            deliveryAmount = itemView.findViewById(R.id.deliveryAmount);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            deliveryWithinTv = itemView.findViewById(R.id.deliveryWithinTv);
            freeDeliveryTv = itemView.findViewById(R.id.freeDeliveryTv);
            discountPercent = itemView.findViewById(R.id.discountPercent);
            moveToCartBtn = itemView.findViewById(R.id.moveToCartBtn);
            removeFromSaveBtn = itemView.findViewById(R.id.removeFromSaveBtn);
        }
    }
}


