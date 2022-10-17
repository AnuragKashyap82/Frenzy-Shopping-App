package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import kashyap.anurag.frenzystore.Activities.OrderDetailsActivity;
import kashyap.anurag.frenzystore.Activities.RatingReviewActivity;
import kashyap.anurag.frenzystore.Models.ModelMyReviews;
import kashyap.anurag.frenzystore.R;

public class AdapterMyReviews extends RecyclerView.Adapter<AdapterMyReviews.HolderMyReviews> {

    private Context context;
    private ArrayList<ModelMyReviews> modelMyReviewsArrayList;
    private FirebaseAuth firebaseAuth;

    public AdapterMyReviews(Context context, ArrayList<ModelMyReviews> modelMyReviewsArrayList) {
        this.context = context;
        this.modelMyReviewsArrayList = modelMyReviewsArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderMyReviews onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_my_reviews_items, parent, false);
        return new HolderMyReviews(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMyReviews holder, int position) {
        ModelMyReviews modelMyReviews = modelMyReviewsArrayList.get(position);
        String productId = modelMyReviews.getProductId();
        String ratings = modelMyReviews.getRatings();
        String reviews = modelMyReviews.getReviews();
        String reviewDate = modelMyReviews.getReviewDate();
        String reviewTime = modelMyReviews.getReviewTime();
        String orderId = modelMyReviews.getOrderId();
        String reviewId = modelMyReviews.getReviewId();

        holder.ratingBar.setRating(Float.parseFloat(ratings));
        holder.reviewTv.setText(reviews);
        holder.dateTv.setText(reviewDate+" - "+reviewTime);

        loadProductDetails(productId, holder);
        holder.deleteReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteReview(reviewId, productId);
            }
        });
        holder.editReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RatingReviewActivity.class);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });
    }

    private void deleteReview(String reviewId, String productId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("MyReviews").document(productId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            deleteFromAllReviews(productId);
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

    private void deleteFromAllReviews(String productId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ratings").child(productId).child(firebaseAuth.getUid());
        databaseReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Reviews Deleted!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProductDetails(String productId, HolderMyReviews holder) {
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
            }

        });
    }
    @Override
    public int getItemCount() {
        return modelMyReviewsArrayList.size();
    }

    public class HolderMyReviews extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle, dateTv, reviewTv, deleteReviewBtn, editReviewBtn;
        private RatingBar ratingBar;

        public HolderMyReviews(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            deleteReviewBtn = itemView.findViewById(R.id.deleteReviewBtn);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            editReviewBtn = itemView.findViewById(R.id.editReviewBtn);
        }
    }
}
