package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.Adapters.AdapterQuestions;
import kashyap.anurag.frenzystore.Models.ModelQuestion;
import kashyap.anurag.frenzystore.databinding.ActivityAllQuestionsBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllQuestionsActivity extends AppCompatActivity {
    private ActivityAllQuestionsBinding binding;
    private String productId;
    private AdapterQuestions adapterQuestions;
    private ArrayList<ModelQuestion> questionArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Questions & Answers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productId = getIntent().getStringExtra("productId");

        loadAllQuestions(productId);
        binding.askQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllQuestionsActivity.this, AskQuestionActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });
    }

    private void loadAllQuestions(String productId) {
            binding.progressBar.setVisibility(View.VISIBLE);
            questionArrayList =new ArrayList<>();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Questions").child(productId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        questionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelQuestion modelQuestion = ds.getValue(ModelQuestion.class);
                            questionArrayList.add(modelQuestion);

                        }

                        adapterQuestions = new AdapterQuestions(AllQuestionsActivity.this, questionArrayList, "ALL");
                        binding.questionAnswerRv.setAdapter(adapterQuestions);

                        binding.progressBar.setVisibility(View.GONE);
                    }else {
                        binding.progressBar.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
}