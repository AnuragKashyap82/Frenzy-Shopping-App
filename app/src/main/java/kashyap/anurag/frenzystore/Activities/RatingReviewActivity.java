package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.ActivityRatingReviewBinding;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RatingReviewActivity extends AppCompatActivity {
    private ActivityRatingReviewBinding binding;
    private String ratings, reviews;
    private FirebaseAuth firebaseAuth;
    private String orderId, productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRatingReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Review Products");

        orderId = getIntent().getStringExtra("orderId");
        firebaseAuth = FirebaseAuth.getInstance();

        loadOrderDetails(orderId);

        binding.addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void loadOrderDetails(String orderId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {

                            productId = snapshot.get("productId").toString();
                            String productName = snapshot.get("productTitle").toString();

                            binding.productNameTv.setText(productName);
                            loadProductDetails(productId);
                            loadMyRatings(productId);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void loadProductDetails(String productId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()) {
                    String productImage1 = snapshot.get("productImage").toString();

                    try {
                        Picasso.get().load(productImage1).fit().centerCrop().placeholder(R.drawable.ic_cart_black).into(binding.productImageIv);
                    } catch (Exception e) {
                        binding.productImageIv.setImageResource(R.drawable.ic_cart_black);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void validateData() {
        ratings = "" + binding.ratingBar.getRating();
        reviews = binding.reviewEt.getText().toString().trim();

        if (ratings.isEmpty()) {
            Toast.makeText(this, "Rate the product!!!", Toast.LENGTH_SHORT).show();
        } else if (reviews.isEmpty()) {
            Toast.makeText(this, "Write your reviews!!!!", Toast.LENGTH_SHORT).show();
        } else {
            updateProductRating();
        }
    }

    private void updateProductRating() {

        binding.progressBar.setVisibility(View.VISIBLE);

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String date = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        String time = currentTimeFormat.format(calForTime.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ratings", "" + ratings);
        hashMap.put("reviews", "" + reviews);
        hashMap.put("productId", "" + productId);
        hashMap.put("reviewTime", "" + time);
        hashMap.put("reviewDate", "" + date);
        hashMap.put("orderId", "" + orderId);
        hashMap.put("uid", firebaseAuth.getUid());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ratings");
        databaseReference.child(productId).child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RatingReviewActivity.this, "Ratings Updated!!!!", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                        addToMyReviews(ratings, reviews, productId, time, date);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RatingReviewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void addToMyReviews(String ratings, String reviews, String productId, String time, String date) {

        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ratings", "" + ratings);
        hashMap.put("reviews", "" + reviews);
        hashMap.put("productId", "" + productId);
        hashMap.put("reviewTime", "" + time);
        hashMap.put("reviewDate", "" + date);
        hashMap.put("reviewId", "" + timestamp);
        hashMap.put("orderId", "" + orderId);
        hashMap.put("uid", firebaseAuth.getUid());

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("MyReviews").document(productId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RatingReviewActivity.this, "Added to my Reviews!!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(RatingReviewActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RatingReviewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMyRatings(String productId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ratings");
        ref.child(productId).child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String uid = "" + snapshot.child("uid").getValue();
                            String reviews = "" + snapshot.child("reviews").getValue();
                            float ratings = Float.parseFloat("" + snapshot.child("ratings").getValue());

                            binding.ratingBar.setRating(ratings);
                            binding.reviewEt.setText(reviews);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}