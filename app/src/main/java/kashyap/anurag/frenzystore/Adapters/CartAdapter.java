package kashyap.anurag.frenzystore.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import kashyap.anurag.frenzystore.Fragments.CartFragment;
import kashyap.anurag.frenzystore.Models.CartItemModel;
import kashyap.anurag.frenzystore.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.cartHolder> {

    private Context context;
    private ArrayList<CartItemModel> cartItemModelArrayList;
    private FirebaseAuth firebaseAuth;
    private int quantity = 1;
    private int totalProductPrice = 0;
    private int totalCuttedPrice = 0;

    public CartAdapter(Context context, ArrayList<CartItemModel> cartItemModelArrayList) {
        this.context = context;
        this.cartItemModelArrayList = cartItemModelArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public cartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        return new cartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull cartHolder holder, int position) {
        final CartItemModel cartItemModel = cartItemModelArrayList.get(position);
        String productId = cartItemModel.getProductId();

       loadProductDetails(productId, holder);

       holder.removeFromCartBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Dialog cartRemoveDialog = new Dialog(context);
               cartRemoveDialog.setContentView(R.layout.cod_dialog);
               cartRemoveDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               TextView yesBtn = cartRemoveDialog.findViewById(R.id.yesBtn);
               TextView noBtn = cartRemoveDialog.findViewById(R.id.noBtn);
               TextView textView4 = cartRemoveDialog.findViewById(R.id.textView4);

               textView4.setText("Are you sure to Remove from cart");

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
                       removeProductFromCart(productId);
                   }
               });
               cartRemoveDialog.show();
           }
       });
       holder.saveForLaterBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               addToSaveForLater(productId);
           }
       });
        holder.incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                holder.quantityTv.setText(""+quantity);

            }
        });
        holder.decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity>1){
                    quantity--;
                    holder.quantityTv.setText(""+quantity);

                }
            }
        });
    }

    private void addToSaveForLater(String productId) {

        long timestamp = System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("productId", productId);
        hashMap.put("timestamp", ""+timestamp);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("saveForLater").document(productId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Product added to save for later successfully!!!!1", Toast.LENGTH_SHORT).show();
                            removeProductFromCart(productId);
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

    private void removeProductFromCart(String productId) {
        FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid())
                .collection("myCart").document(productId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Product Removed From MyCart!!!", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProductDetails(String productId, cartHolder holder) {
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

                holder.productTitle.setText(productTitle);
                holder.productDescription.setText(productDescription);
                holder.deliveryWithinTv.setText("Delivery within "+deliveryWithin+ " days");

                quantity = Integer.parseInt(holder.quantityTv.getText().toString().trim());

                totalCuttedPrice = Integer.parseInt(cuttedPrice1) * quantity;
                holder.cuttedPrice.setText("Rs."+totalCuttedPrice);

                totalProductPrice = Integer.parseInt(productPrice1) * quantity;
                holder.productPrice.setText("Rs."+totalProductPrice);

                if (deliveryFee.equals("FREE Delivery")){
                    holder.freeDeliveryTv.setVisibility(View.VISIBLE);
                    holder.deliveryAmount.setPaintFlags(holder.deliveryAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else {
                    holder.deliveryAmount.setText("Rs."+deliveryFee);
                    holder.freeDeliveryTv.setVisibility(View.GONE);
                }

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
                holder.buyNowBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, DeliveryActivity.class);
                        intent.putExtra("productId", productId);
                        intent.putExtra("productPrice", ""+productPrice1);
                        intent.putExtra("productTitle", productTitle);
                        context.startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemModelArrayList.size();
    }

    public class cartHolder extends RecyclerView.ViewHolder {

        private TextView productTitle, productPrice, cuttedPrice;
        private ImageView productImage;
        private TextView removeFromCartBtn, buyNowBtn, productDescription, deliveryAmount, deliveryWithinTv,
                freeDeliveryTv, discountPercent, saveForLaterBtn, quantityTv;
        private RatingBar ratingBar;
        private ImageButton decrementBtn, incrementBtn;

        public cartHolder(@NonNull View itemView) {
            super(itemView);

            productTitle = itemView.findViewById(R.id.productTitleTv);
            productImage = itemView.findViewById(R.id.productImage);
            removeFromCartBtn = itemView.findViewById(R.id.removeFromCartBtn);
            productPrice = itemView.findViewById(R.id.productPrice);
            cuttedPrice = itemView.findViewById(R.id.cuttedPrice);
            buyNowBtn = itemView.findViewById(R.id.buyNowBtn);
            productDescription = itemView.findViewById(R.id.productDescription);
            deliveryAmount = itemView.findViewById(R.id.deliveryAmount);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            deliveryWithinTv = itemView.findViewById(R.id.deliveryWithinTv);
            freeDeliveryTv = itemView.findViewById(R.id.freeDeliveryTv);
            discountPercent = itemView.findViewById(R.id.discountPercent);
            saveForLaterBtn = itemView.findViewById(R.id.saveForLaterBtn);
            decrementBtn = itemView.findViewById(R.id.decrementBtn);
            incrementBtn = itemView.findViewById(R.id.incrementBtn);
            quantityTv = itemView.findViewById(R.id.quantityTv);
        }
    }
}
