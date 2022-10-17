package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.Adapters.AdapterMyQuestion;
import kashyap.anurag.frenzystore.Models.ModelQuestion;
import kashyap.anurag.frenzystore.databinding.ActivityMyQuestionsBinding;

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

public class MyQuestionsActivity extends AppCompatActivity {
    private ActivityMyQuestionsBinding binding;
    private AdapterMyQuestion adapterMyQuestions;
    private ArrayList<ModelQuestion> questionArrayList;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Questions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        loadMyQuestions();
    }

    private void loadMyQuestions() {
        binding.progressBar.setVisibility(View.VISIBLE);
        questionArrayList =new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid()).collection("MyQuestions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelQuestion modelQuestion = document.toObject(ModelQuestion.class);
                                questionArrayList.add(modelQuestion);
                            }
                            Collections.sort(questionArrayList, new Comparator<ModelQuestion>() {
                                @Override
                                public int compare(ModelQuestion t1, ModelQuestion t2) {
                                    return t1.getQuestionId().compareToIgnoreCase(t2.getQuestionId());
                                }
                            });
                            Collections.reverse(questionArrayList);

                            adapterMyQuestions = new AdapterMyQuestion(MyQuestionsActivity.this, questionArrayList);
                            binding.myQuestionsRv.setAdapter(adapterMyQuestions);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}