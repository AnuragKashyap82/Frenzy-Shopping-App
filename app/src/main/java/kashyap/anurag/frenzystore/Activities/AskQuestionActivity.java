package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.databinding.ActivityAskQuestionBinding;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AskQuestionActivity extends AppCompatActivity {
    private ActivityAskQuestionBinding binding;
    private String question;
    private String productId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAskQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        productId = getIntent().getStringExtra("productId");

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.submitQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        question = binding.questionEt.getText().toString().trim();

        if (question.length()<10){
            Toast.makeText(this, "Question must of of 10 words", Toast.LENGTH_SHORT).show();
        }else {
            submitQuestions();
        }
    }

    private void submitQuestions() {
        long timestamp  = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("question", ""+question);
        hashMap.put("answer", "Not yet answered!!!!");
        hashMap.put("questionId", ""+timestamp);
        hashMap.put("productId", productId);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Questions");
        databaseReference.child(productId).child(""+timestamp)
                .setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AskQuestionActivity.this, "Question Uploaded Successfully!!!!", Toast.LENGTH_SHORT).show();
                            aadToMyQuestion(question, timestamp, productId);
                        }else {
                            Toast.makeText(AskQuestionActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AskQuestionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void aadToMyQuestion(String question, long timestamp, String productId) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("question", ""+question);
        hashMap.put("answer", "Not yet answered!!!!");
        hashMap.put("questionId", ""+timestamp);
        hashMap.put("productId", productId);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("MyQuestions").document(""+timestamp)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AskQuestionActivity.this, "Adder to my Questions!!!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AskQuestionActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AskQuestionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}