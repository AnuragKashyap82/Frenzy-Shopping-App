package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Activities.CategoryActivity;
import kashyap.anurag.frenzystore.Models.ModelCategory;
import kashyap.anurag.frenzystore.R;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.holderCategory> {

    private Context context;
    private ArrayList<ModelCategory> categoryArrayList;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public holderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_category_item, parent, false);
        return new holderCategory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderCategory holder, int position) {
        final ModelCategory modelCategory = categoryArrayList.get(position);
        String categoryIcon = modelCategory.getCategoryIcon();
        String categoryName = modelCategory.getCategoryName();

        holder.categoryName.setText(categoryName);
        try {
            Picasso.get().load(categoryIcon).placeholder(R.drawable.ic_cart_black).into(holder.categoryIcon);
        } catch (Exception e) {
            holder.categoryIcon.setImageResource(R.drawable.ic_home_black);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("categoryName", categoryName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public class holderCategory extends RecyclerView.ViewHolder {

        private ImageView categoryIcon;
        private TextView categoryName;

        public holderCategory(@NonNull View itemView) {
            super(itemView);

            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}
