package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.ActivityOrderTrackerBinding;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class OrderTrackerActivity extends AppCompatActivity {
    private ActivityOrderTrackerBinding binding;
    private String orderId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderTrackerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Tracker");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        orderId = getIntent().getStringExtra("orderId");

        checkOrderStatus(orderId);
    }
    private void checkOrderStatus(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        String orderStatus = snapshot.getString("orderStatus");
                        if (orderStatus.equals("cancelled")) {
                            loadOrderedDetails(orderId);

                            binding.packedIndicator.setVisibility(View.GONE);
                            binding.packedTv.setVisibility(View.GONE);
                            binding.packedDate.setVisibility(View.GONE);
                            binding.packedTextIndicator.setVisibility(View.GONE);
                            binding.packedShippingProgressBar.setVisibility(View.GONE);
                            binding.shippingIndicator.setVisibility(View.GONE);
                            binding.shippedDate.setVisibility(View.GONE);
                            binding.shippedTv.setVisibility(View.GONE);
                            binding.shippedTextIndicator.setVisibility(View.GONE);
                            binding.orderedPackedProgressBar.setVisibility(View.GONE);

                            binding.returnIndicator.setVisibility(View.GONE);
                            binding.returnProgressBar.setVisibility(View.GONE);
                            binding.refundProgressBar.setVisibility(View.GONE);
                            binding.refundIndicator.setVisibility(View.GONE);
                            binding.refundRl.setVisibility(View.GONE);
                            binding.returnRl.setVisibility(View.GONE);

                            binding.shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.deliveredIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            binding.deliveredTextIndicator.setText("Cancelled");
                            binding.deliveredTv.setText("Your Ordered has been Cancelled");
                            loadCancelledDetails(orderId);
                        }else if (orderStatus.equals("ordered")){
                            loadOrderedDetails(orderId);
                        } else if (orderStatus.equals("packed")){
                            loadOrderedDetails(orderId);

                            binding.returnIndicator.setVisibility(View.GONE);
                            binding.returnProgressBar.setVisibility(View.GONE);
                            binding.refundProgressBar.setVisibility(View.GONE);
                            binding.refundIndicator.setVisibility(View.GONE);
                            binding.refundRl.setVisibility(View.GONE);
                            binding.returnRl.setVisibility(View.GONE);

                            binding.packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                        }else if (orderStatus.equals("shipped")){
                            loadOrderedDetails(orderId);

                            binding.returnIndicator.setVisibility(View.GONE);
                            binding.returnProgressBar.setVisibility(View.GONE);
                            binding.refundProgressBar.setVisibility(View.GONE);
                            binding.refundIndicator.setVisibility(View.GONE);
                            binding.refundRl.setVisibility(View.GONE);
                            binding.returnRl.setVisibility(View.GONE);

                            binding.packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                            binding.shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.shippingIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.shippedTv.setText("Your order has been shipped");
                            loadShippedDetails(orderId);
                        }else if (orderStatus.equals("delivered")){
                            loadOrderedDetails(orderId);

                            binding.returnIndicator.setVisibility(View.GONE);
                            binding.returnProgressBar.setVisibility(View.GONE);
                            binding.refundProgressBar.setVisibility(View.GONE);
                            binding.refundIndicator.setVisibility(View.GONE);
                            binding.refundRl.setVisibility(View.GONE);
                            binding.returnRl.setVisibility(View.GONE);

                            binding.packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                            binding.shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.shippingIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.shippedTv.setText("Your order has been shipped");
                            loadShippedDetails(orderId);

                            binding. deliveredIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.deliveredTv.setText("Your order has been Delivered");
                            loadDeliveredDetails(orderId);
                        }else if (orderStatus.equals("returned")){
                            loadOrderedDetails(orderId);

                            binding.returnProgressBar.setVisibility(View.VISIBLE);
                            binding.refundProgressBar.setVisibility(View.VISIBLE);
                            binding.returnIndicator.setVisibility(View.VISIBLE);
                            binding.refundIndicator.setVisibility(View.VISIBLE);
                            binding.refundProgressBar.setVisibility(View.VISIBLE);
                            binding.returnRl.setVisibility(View.VISIBLE);
                            binding.refundRl.setVisibility(View.VISIBLE);

                            binding.packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                            binding.shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.shippingIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.shippedTv.setText("Your order has been shipped");
                            loadShippedDetails(orderId);

                            binding.deliveredIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.deliveredTv.setText("Your order has been Delivered");
                            loadDeliveredDetails(orderId);

                            binding.returnProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            binding.returnIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            binding.returnTv.setText("Your Order has been return");
                            loadReturnDetails(orderId);


                        }else if (orderStatus.equals("refunded")) {
                            loadOrderedDetails(orderId);

                            binding.returnProgressBar.setVisibility(View.VISIBLE);
                            binding.refundProgressBar.setVisibility(View.VISIBLE);
                            binding.returnIndicator.setVisibility(View.VISIBLE);
                            binding.refundIndicator.setVisibility(View.VISIBLE);
                            binding.refundProgressBar.setVisibility(View.VISIBLE);
                            binding.returnRl.setVisibility(View.VISIBLE);
                            binding.refundRl.setVisibility(View.VISIBLE);

                            binding.packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                            binding.shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.shippingIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.shippedTv.setText("Your order has been shipped");
                            loadShippedDetails(orderId);

                            binding.deliveredIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.deliveredTv.setText("Your order has been Delivered");
                            loadDeliveredDetails(orderId);

                            binding.returnProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            binding.returnIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            binding.returnTv.setText("Your Order has been return");
                            loadReturnDetails(orderId);

                            binding.refundProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.refundIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            binding.refundTv.setText("Your Amount has been Refunded");
                            loadRefundDetails(orderId);
                        }
                    }
                });
    }
    private void loadOrderedDetails(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {
                            String orderDate = snapshot.get("orderDate").toString();
                            String orderTime = snapshot.get("orderTime").toString();

                            binding.orderedDate.setText(orderDate + "   " + orderTime);

                        }
                    }
                });
    }
    private void loadCancelledDetails(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {

                            String orderCancelledDate = ""+snapshot.get("orderCancelledDate");
                            String orderCancelledTime = ""+snapshot.get("orderCancelledTime");

                            binding.deliveryDate.setText(orderCancelledDate + "  "+orderCancelledTime);
                        }
                    }
                });
    }
    private void loadPackedDetails(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {

                            String orderPackedDate = "" + snapshot.get("orderPackedDate");
                            String orderPackedTime = "" + snapshot.get("orderPackedTime");

                            binding.packedDate.setText(orderPackedDate + "  " + orderPackedTime);
                        }
                    }
                });
    }
    private void loadShippedDetails(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {

                            String orderShippedDate = "" + snapshot.get("orderShippedDate");
                            String orderShippedTime = "" + snapshot.get("orderShippedTime");

                            binding.shippedDate.setText(orderShippedDate + "  " + orderShippedTime);
                        }
                    }
                });
    }
    private void loadDeliveredDetails(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {

                            String orderDeliveredDate = "" + snapshot.get("orderDeliveredDate");
                            String orderDeliveredTime = "" + snapshot.get("orderDeliveredTime");

                            binding.deliveryDate.setText(orderDeliveredDate + "  " + orderDeliveredTime);
                        }
                    }
                });
    }
    private void loadReturnDetails(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {

                            String orderReturnDate = "" + snapshot.get("orderReturnDate");
                            String orderReturnTime = "" + snapshot.get("orderReturnTime");

                            binding.returnDate.setText(orderReturnDate + "  " + orderReturnTime);
                        }
                    }
                });
    }
    private void loadRefundDetails(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {

                            String orderRefundedDate = "" + snapshot.get("orderRefundedDate");
                            String orderRefundedTime = "" + snapshot.get("orderRefundedTime");

                            binding.refundDate.setText(orderRefundedDate + "  " + orderRefundedTime);
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}