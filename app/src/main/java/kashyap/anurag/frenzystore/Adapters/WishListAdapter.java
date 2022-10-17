package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Activities.ProductDetailsActivity;
import kashyap.anurag.frenzystore.Models.WishListModel;
import kashyap.anurag.frenzystore.R;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.HolderWishList> {

    private Context context;
    private ArrayList<WishListModel> wishListModelArrayList;
    private boolean DELETE_BTN;
    private FirebaseAuth firebaseAuth;

    public WishListAdapter(Context context, ArrayList<WishListModel> wishListModelArrayList, boolean DELETE_BTN) {
        this.context = context;
        this.wishListModelArrayList = wishListModelArrayList;
        this.DELETE_BTN = DELETE_BTN;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderWishList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wishlist_item_layout, parent, false);
        return new HolderWishList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderWishList holder, int position) {
        final WishListModel wishListModel = wishListModelArrayList.get(position);
        String productId = wishListModel.getProductId();
        String productTitle = wishListModel.getProductTitle();

        holder.quantityLl.setVisibility(View.GONE);

        loadProductDetails(productId, holder);

        if (DELETE_BTN){
            holder.deleteWishListBtn.setVisibility(View.VISIBLE);
        }else {
            holder.deleteWishListBtn.setVisibility(View.GONE);
        }

        holder.deleteWishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFromWishlist(productId);
            }
        });

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

    private void deleteFromWishlist(String productId) {
        FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid())
                .collection("myWishlist").document(productId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Producet Removed from myWishlist!!!!", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProductDetails(String productId, HolderWishList holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String productTitle = snapshot.get("productTitle").toString();
                String productImage = snapshot.get("productImage").toString();
                String productPrice = snapshot.get("productPrice").toString();
                String productDescription = snapshot.get("productDescription").toString();
                String cuttedPrice = snapshot.get("productCuttedPrice").toString();
                String avgRating = snapshot.get("avgRating").toString();
                String deliveryPrice1 = snapshot.get("deliveryFee").toString();

                boolean isCod = Boolean.parseBoolean(snapshot.get("isCod").toString());

                if (isCod){
                    holder.codAvailableTv.setText("Cash On Delivery Available");
                }else {
                    holder.codAvailableTv.setText("Cash on delivery Not Available");
                }

                if (deliveryPrice1.equals("FREE Delivery")){
                    holder.freeDeliveryTv.setVisibility(View.VISIBLE);
                    holder.deliveryAmountTv.setPaintFlags(holder.deliveryAmountTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                }else {
                    holder.freeDeliveryTv.setVisibility(View.GONE);
                    holder.deliveryAmountTv.setText("Delivery charges Rs."+deliveryPrice1);

                }
                int percentDiscount = ((Integer.parseInt(cuttedPrice) - Integer.parseInt(productPrice)) * 100)/Integer.parseInt(cuttedPrice);
                holder.percentDiscountTv.setText(percentDiscount+"% OFF");

                holder.productTitle.setText(productTitle);
                holder.cuttedPrice.setText("Rs."+cuttedPrice);
                holder.productDescriptionTv.setText(productDescription);
                holder.cuttedPrice.setPaintFlags(holder.cuttedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.productPrice.setText("Rs."+productPrice);
                holder.productRateMiniView.setText(avgRating);

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
        return wishListModelArrayList.size();
    }

    public class HolderWishList extends RecyclerView.ViewHolder {

        private TextView productTitle, cuttedPrice, productPrice, codAvailableTv, deliveryAmountTv,
                productRateMiniView, freeDeliveryTv, percentDiscountTv, productDescriptionTv;
        private ImageView deleteWishListBtn, productImage;
        private LinearLayout quantityLl;

        public HolderWishList(@NonNull View itemView) {
            super(itemView);

            productTitle  = itemView.findViewById(R.id.productTitle);
            deleteWishListBtn  = itemView.findViewById(R.id.deleteWishListBtn);
            productImage  = itemView.findViewById(R.id.productImage);
            cuttedPrice  = itemView.findViewById(R.id.cuttedPrice);
            productPrice  = itemView.findViewById(R.id.productPrice);
            codAvailableTv  = itemView.findViewById(R.id.codAvailableTv);
            deliveryAmountTv  = itemView.findViewById(R.id.deliveryAmountTv);
            productRateMiniView  = itemView.findViewById(R.id.productRateMiniView);
            freeDeliveryTv  = itemView.findViewById(R.id.freeDeliveryTv);
            percentDiscountTv  = itemView.findViewById(R.id.percentDiscountTv);
            productDescriptionTv  = itemView.findViewById(R.id.productDescriptionTv);
            quantityLl  = itemView.findViewById(R.id.quantityLl);
        }
    }
}
