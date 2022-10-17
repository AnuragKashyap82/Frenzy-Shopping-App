package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.Adapters.AdapterRating;
import kashyap.anurag.frenzystore.Models.ModelRating;
import kashyap.anurag.frenzystore.databinding.ActivityAllReviewsBinding;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class AllReviewsActivity extends AppCompatActivity {
    private ActivityAllReviewsBinding binding;
    private String productId;
    private float avgRating;

    private AdapterRating adapterRating;
    private ArrayList<ModelRating> ratingArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add Or Manage Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productId = getIntent().getStringExtra("productId");
        loadAllRatings(productId);

    }
    private float ratingSum = 0;
    private void loadAllRatings(String productId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ratingArrayList =new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ratings").child(productId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ratingArrayList.clear();
                    ratingSum = 0;
                    for (DataSnapshot ds: snapshot.getChildren()){
                        float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                        ratingSum = ratingSum + rating;

                        ModelRating modelRating = ds.getValue(ModelRating.class);
                        ratingArrayList.add(modelRating);
                        String noOfRating = String.valueOf(ratingArrayList.size());
                        binding.ratingCountTv.setText(noOfRating +" ratings \n and reviews");

                    }

                    adapterRating = new AdapterRating(AllReviewsActivity.this, ratingArrayList, "ALL");
                    binding.ratingsRv.setAdapter(adapterRating);

                    long numberOfReviews = snapshot.getChildrenCount();
                    avgRating = ratingSum/numberOfReviews;

                    binding.ratingBar.setRating(avgRating);

                    updateAvgRating(avgRating);
                    binding.progressBar.setVisibility(View.GONE);
                }else {
                    binding.ratingCountTv.setText(0 +" ratings \n and reviews");
                    binding.progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateAvgRating(float avgRating) {

        HashMap<String, Object> hashMap  = new HashMap<>();
        hashMap.put("avgRating", ""+avgRating);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AllReviewsActivity.this, "Avg updated Successfully!!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AllReviewsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AllReviewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}