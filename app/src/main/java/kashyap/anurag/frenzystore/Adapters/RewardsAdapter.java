package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Models.RewardModel;
import kashyap.anurag.frenzystore.R;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.HolderRewards> {

    private Context context;
    private ArrayList<RewardModel> rewardModelArrayList;

    public RewardsAdapter(Context context, ArrayList<RewardModel> rewardModelArrayList) {
        this.context = context;
        this.rewardModelArrayList = rewardModelArrayList;
    }

    @NonNull
    @Override
    public HolderRewards onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rewards_item_layout, parent, false);
        return new HolderRewards(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRewards holder, int position) {
        final  RewardModel rewardModel  = rewardModelArrayList.get(position);
        String validDate = rewardModel.getValidDate();

        holder.validDate.setText(validDate);
    }

    @Override
    public int getItemCount() {
        return rewardModelArrayList.size();
    }

    public class HolderRewards extends RecyclerView.ViewHolder {

        private TextView validDate;

        public HolderRewards(@NonNull View itemView) {
            super(itemView);

            validDate = itemView.findViewById(R.id.validDate);
        }
    }
}
