package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Models.ProductSpecificationModel;
import kashyap.anurag.frenzystore.R;

public class ProductSpecificationAdapter extends RecyclerView.Adapter<ProductSpecificationAdapter.productSpecificationHolder> {

    private Context context;
    private List<ProductSpecificationModel> productSpecificationModelArrayList;

    public ProductSpecificationAdapter(Context context, List<ProductSpecificationModel> productSpecificationModelArrayList) {
        this.context = context;
        this.productSpecificationModelArrayList = productSpecificationModelArrayList;
    }

    @NonNull
    @Override
    public productSpecificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_specification_item, parent, false);
        return new productSpecificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull productSpecificationHolder holder, int position) {
        final ProductSpecificationModel productSpecificationModel = productSpecificationModelArrayList.get(position);
        String featureName = productSpecificationModel.getFeatureName();
        String featureValue = productSpecificationModel.getFeatureValue();

        holder.featureName.setText(featureName);
        holder.featureValue.setText(featureValue);
    }

    @Override
    public int getItemCount() {
        return productSpecificationModelArrayList.size();
    }

    public class productSpecificationHolder extends RecyclerView.ViewHolder {

        private TextView featureName, featureValue;

        public productSpecificationHolder(@NonNull View itemView) {
            super(itemView);

            featureName = itemView.findViewById(R.id.featureName);
            featureValue = itemView.findViewById(R.id.featureValue);
        }
    }
}
