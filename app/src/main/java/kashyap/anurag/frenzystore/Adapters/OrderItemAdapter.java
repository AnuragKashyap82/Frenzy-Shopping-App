package kashyap.anurag.frenzystore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import kashyap.anurag.frenzystore.Activities.OrderDetailsActivity;
import kashyap.anurag.frenzystore.Models.OrderItemModel;
import kashyap.anurag.frenzystore.R;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemHolder> {

    private Context context;
    private ArrayList<OrderItemModel> orderItemModelArrayList;

    public OrderItemAdapter(Context context, ArrayList<OrderItemModel> orderItemModelArrayList) {
        this.context = context;
        this.orderItemModelArrayList = orderItemModelArrayList;
    }

    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        return new OrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder holder, int position) {
        final  OrderItemModel orderItemModel = orderItemModelArrayList.get(position);
        String productId = orderItemModel.getProductId();
        String orderId = orderItemModel.getOrderId();
        String orderDate = orderItemModel.getOrderDate();
        String orderTime = orderItemModel.getOrderTime();

        loadProductDetails(productId, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderId", ""+orderId);
                context.startActivity(intent);
            }
        });

    }

    private void loadProductDetails(String productId, OrderItemHolder holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String productTitle = snapshot.get("productTitle").toString();
                String productImage = snapshot.get("productImage").toString();

                holder.productTitle.setText(productTitle);
                try {
                    Picasso.get().load(productImage).fit().centerCrop().placeholder(R.drawable.ic_cart_black).into(holder.productImage);
                } catch (Exception e) {
                    holder.productImage.setImageResource(R.drawable.ic_cart_black);
                }
            }

        });
    }

    private void setRating(int starPosition, OrderItemHolder holder) {
        for(int x = 0; x< holder.rateNowContainer.getChildCount(); x++){
            ImageView starBtn = (ImageView) holder.rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
            if (x <= starPosition){
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#CE0000")));
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderItemModelArrayList.size();
    }

    public class OrderItemHolder extends RecyclerView.ViewHolder {

        private TextView productTitle, orderDeliveredDate;
        private LinearLayout rateNowContainer;
        private ImageView productImage, orderIndicator;

        public OrderItemHolder(@NonNull View itemView) {
            super(itemView);

            productTitle = itemView.findViewById(R.id.productTitle);
            rateNowContainer = itemView.findViewById(R.id.rateNowContainer);
            productImage = itemView.findViewById(R.id.productImage);
            orderDeliveredDate = itemView.findViewById(R.id.orderDeliveredDate);
            orderIndicator = itemView.findViewById(R.id.orderIndicator);
        }
    }
}
