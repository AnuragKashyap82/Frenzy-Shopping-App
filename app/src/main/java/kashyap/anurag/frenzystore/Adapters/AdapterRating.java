package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Models.ModelRating;
import kashyap.anurag.frenzystore.R;

public class AdapterRating extends RecyclerView.Adapter<AdapterRating.HolderRating> {

    private Context context;
    private ArrayList<ModelRating> ratingArrayList;
    private String SHOW_ALL_CODE;

    public AdapterRating(Context context, ArrayList<ModelRating> ratingArrayList, String SHOW_ALL_CODE) {
        this.context = context;
        this.ratingArrayList = ratingArrayList;
        this.SHOW_ALL_CODE = SHOW_ALL_CODE;
    }

    @NonNull
    @Override
    public HolderRating onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.row_rating_review_items, parent, false);
        return  new HolderRating(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRating holder, int position) {
        final  ModelRating modelRating = ratingArrayList.get(position);
        String uid = modelRating.getUid();
        String ratings = modelRating.getRatings();
        String productId = modelRating.getProductId();
        String reviews = modelRating.getReviews();
        String date = modelRating.getReviewDate();
        String time = modelRating.getReviewTime();

        holder.ratingBar.setRating(Float.parseFloat(ratings));
        holder.reviewTv.setText(reviews);
        holder.dateTimeTv.setText(date +"  "+ time);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "moreBtn Clicked!!!", Toast.LENGTH_SHORT).show();
            }
        });
        loadSenderDetails(uid, holder);
    }

    private void loadSenderDetails(String uid, HolderRating holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()){
                    String name = snapshot.get("name").toString();
                    holder.nameTv.setText(name);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (ratingArrayList.size() > 3){
            if (SHOW_ALL_CODE == "ALL"){
                return ratingArrayList.size();
            }else{
                return 3;
            }

        }else {
           return ratingArrayList.size();
        }

    }

    public class HolderRating extends RecyclerView.ViewHolder {

        private TextView nameTv, dateTimeTv, reviewTv;
        private ImageView moreBtn;
        private RatingBar ratingBar;

        public HolderRating(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            dateTimeTv = itemView.findViewById(R.id.dateTimeTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            ratingBar = itemView.findViewById(R.id.ratingBar);

        }
    }
}
