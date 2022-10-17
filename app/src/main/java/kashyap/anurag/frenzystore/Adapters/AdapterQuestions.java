package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Models.ModelQuestion;
import kashyap.anurag.frenzystore.R;

public class AdapterQuestions extends RecyclerView.Adapter<AdapterQuestions.HolderQuestionAnswer> {

    private Context context;
    private ArrayList<ModelQuestion> questionArrayList;
    private String LAYOUT_CODE;

    public AdapterQuestions(Context context, ArrayList<ModelQuestion> questionArrayList, String LAYOUT_CODE) {
        this.context = context;
        this.questionArrayList = questionArrayList;
        this.LAYOUT_CODE = LAYOUT_CODE;
    }

    @NonNull
    @Override
    public HolderQuestionAnswer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_question_answer_items, parent, false);
        return new HolderQuestionAnswer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderQuestionAnswer holder, int position) {
        final ModelQuestion modelQuestion = questionArrayList.get(position);
        String question = modelQuestion.getQuestion();
        String answer = modelQuestion.getAnswer();
        String productId = modelQuestion.getProductId();
        String questionId = modelQuestion.getQuestionId();
        String uid = modelQuestion.getUid();

        holder.questionTv.setText("Q: "+question);
        holder.answerTv.setText("A: "+answer);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (questionArrayList.size() > 3){
            if (LAYOUT_CODE == "ALL"){
                return questionArrayList.size();
            }else {
                return 3;
            }
        }else {
            return questionArrayList.size();
        }
    }

    public class HolderQuestionAnswer extends RecyclerView.ViewHolder {

        private TextView questionTv, answerTv, nameTv;
        private ImageView moreBtn;

        public HolderQuestionAnswer(@NonNull View itemView) {
            super(itemView);

            questionTv = itemView.findViewById(R.id.questionTv);
            answerTv = itemView.findViewById(R.id.answerTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }
    }
}
