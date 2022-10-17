package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import kashyap.anurag.frenzystore.Models.ModelQuestion;
import kashyap.anurag.frenzystore.R;

public class AdapterMyQuestion extends RecyclerView.Adapter<AdapterMyQuestion.HolderMyQuestions> {

    private Context context;
    private ArrayList<ModelQuestion> questionArrayList;

    public AdapterMyQuestion(Context context, ArrayList<ModelQuestion> questionArrayList) {
        this.context = context;
        this.questionArrayList = questionArrayList;
    }

    @NonNull
    @Override
    public HolderMyQuestions onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.row_my_questions_items, parent, false);
        return new HolderMyQuestions(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMyQuestions holder, int position) {
        final ModelQuestion modelQuestion = questionArrayList.get(position);
        String productId = modelQuestion.getProductId();
        String question = modelQuestion.getQuestion();
        String answer = modelQuestion.getAnswer();

        holder.questionTv.setText("Q: "+question);
        holder.answerTv.setText("A: "+answer);

        loadProductDetails(productId, holder);
    }
    private void loadProductDetails(String productId, HolderMyQuestions holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()){
                    String productTitle = snapshot.get("productTitle").toString();
                    String productImage = snapshot.get("productImage").toString();

                    holder.productTitle.setText(productTitle);
                    try {
                        Picasso.get().load(productImage).fit().centerCrop().placeholder(R.drawable.ic_cart_black).into(holder.productImage);
                    } catch (Exception e) {
                        holder.productImage.setImageResource(R.drawable.ic_cart_black);
                    }
                }
               else {
                    Toast.makeText(context, "Not Found!!!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return questionArrayList.size();
    }

    public class HolderMyQuestions extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle, questionTv, answerTv;

        public HolderMyQuestions(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            questionTv = itemView.findViewById(R.id.questionTv);
            productTitle = itemView.findViewById(R.id.productTitle);
            answerTv = itemView.findViewById(R.id.answerTv);
        }
    }
}
