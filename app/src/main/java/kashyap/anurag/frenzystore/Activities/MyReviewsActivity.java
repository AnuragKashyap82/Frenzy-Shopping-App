package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.Adapters.AdapterMyReviews;
import kashyap.anurag.frenzystore.Models.ModelMyReviews;
import kashyap.anurag.frenzystore.databinding.ActivityMyReviewsBinding;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyReviewsActivity extends AppCompatActivity {
    private ActivityMyReviewsBinding binding;
    private FirebaseAuth firebaseAuth;
    private AdapterMyReviews adapterMyReviews;
    private ArrayList<ModelMyReviews> modelMyReviewsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadMyReviews();

    }

    private void loadMyReviews() {
        binding.progressBar.setVisibility(View.VISIBLE);
        modelMyReviewsArrayList =new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid()).collection("MyReviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelMyReviews modelMyReviews = document.toObject(ModelMyReviews.class);
                                modelMyReviewsArrayList.add(modelMyReviews);
                            }
                            Collections.sort(modelMyReviewsArrayList, new Comparator<ModelMyReviews>() {
                                @Override
                                public int compare(ModelMyReviews t1, ModelMyReviews t2) {
                                    return t1.getReviewId().compareToIgnoreCase(t2.getReviewId());
                                }
                            });
                            Collections.reverse(modelMyReviewsArrayList);

                            adapterMyReviews = new AdapterMyReviews(MyReviewsActivity.this, modelMyReviewsArrayList);
                            binding.myReviewsRv.setAdapter(adapterMyReviews);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}