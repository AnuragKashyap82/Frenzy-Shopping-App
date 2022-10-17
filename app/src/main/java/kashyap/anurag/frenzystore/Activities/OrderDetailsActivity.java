package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.ActivityOrderDetailsBinding;


import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class OrderDetailsActivity extends AppCompatActivity {
    private ActivityOrderDetailsBinding binding;
    private String orderId;
    private TextView productTitle, productPrice, productQuantity, orderedDate, packedDate,
            shippedDate, deliveryDate, packedTv, shippedTv, deliveredTv, nameTv, addressTv, phoneNoTv,
            totalItemsPrice, deliveryPrice, totalPrice, savedAmount, productDescriptionTv, cancelOrderBtn, priceTv, discountPrice,
            needHelpBtn, needHelpBtn2, packedTextIndicator, shippedTextIndicator, deliveredTextIndicator, orderIdTv, shopNameTv, returnOrderBtn
            ,returnDate, returnTv, refundTv, refundDate;
    private ImageView productImage, packedIndicator, shippingIndicator, deliveredIndicator, orderTrackerBtn, refundIndicator, returnIndicator;
    private View packedShippingProgressBar, shippedDeliveryProgressBar, orderedPackedProgressBar, returnProgressBar, refundProgressBar;
    private RatingBar ratingBar;
    private String productId;
    private TextView rateNowBtn;
    private RelativeLayout  returnRl, refundRl;

    private FirebaseAuth firebaseAuth;
    private LinearLayout cancelOrderLl, returnOrderLl;
    private boolean isCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Details");

        firebaseAuth = FirebaseAuth.getInstance();
        orderId = getIntent().getStringExtra("orderId");

        checkOrderStatus(orderId);
        loadOrderDetails(orderId);

        productTitle = findViewById(R.id.productTitle);
        productPrice = findViewById(R.id.productPrice);
        productQuantity = findViewById(R.id.productQuantity);
        orderedDate = findViewById(R.id.orderedDate);
        packedDate = findViewById(R.id.packedDate);
        shippedDate = findViewById(R.id.shippedDate);
        deliveryDate = findViewById(R.id.deliveryDate);
        packedTv = findViewById(R.id.packedTv);
        shippedTv = findViewById(R.id.shippedTv);
        deliveredTv = findViewById(R.id.deliveredTv);
        nameTv = findViewById(R.id.nameTv);
        addressTv = findViewById(R.id.addressTv);
        phoneNoTv = findViewById(R.id.phoneNoTv);
        totalItemsPrice = findViewById(R.id.totalItemsPrice);
        deliveryPrice = findViewById(R.id.deliveryPrice);
        totalPrice = findViewById(R.id.totalPrice);
        savedAmount = findViewById(R.id.savedAmount);
        productImage = findViewById(R.id.productImage);
        packedIndicator = findViewById(R.id.packedIndicator);
        shippingIndicator = findViewById(R.id.shippingIndicator);
        deliveredIndicator = findViewById(R.id.deliveredIndicator);
        packedShippingProgressBar = findViewById(R.id.packedShippingProgressBar);
        shippedDeliveryProgressBar = findViewById(R.id.shippedDeliveryProgressBar);
        ratingBar = findViewById(R.id.ratingBar);
        rateNowBtn = findViewById(R.id.rateNowBtn);
        productDescriptionTv = findViewById(R.id.productDescriptionTv);
        needHelpBtn = findViewById(R.id.needHelpBtn);
        packedTextIndicator = findViewById(R.id.packedTextIndicator);
        cancelOrderBtn = findViewById(R.id.cancelOrderBtn);
        shippedTextIndicator = findViewById(R.id.shippedTextIndicator);
        deliveredTextIndicator = findViewById(R.id.deliveredTextIndicator);
        orderedPackedProgressBar = findViewById(R.id.orderedPackedProgressBar);
        cancelOrderLl = findViewById(R.id.cancelOrderLl);
        priceTv = findViewById(R.id.priceTv);
        discountPrice = findViewById(R.id.discountPrice);
        orderIdTv = findViewById(R.id.orderIdTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        returnOrderLl = findViewById(R.id.returnOrderLl);
        needHelpBtn2 = findViewById(R.id.needHelpBtn2);
        returnOrderBtn = findViewById(R.id.returnOrderBtn);
        orderTrackerBtn = findViewById(R.id.orderTrackerBtn);
        returnProgressBar = findViewById(R.id.returnProgressBar);
        refundProgressBar = findViewById(R.id.refundProgressBar);
        refundIndicator = findViewById(R.id.refundIndicator);
        returnIndicator = findViewById(R.id.returnIndicator);
        returnDate = findViewById(R.id.returnDate);
        returnTv = findViewById(R.id.returnTv);
        returnRl = findViewById(R.id.returnRl);
        refundRl = findViewById(R.id.refundRl);
        refundTv = findViewById(R.id.refundTv);
        refundDate = findViewById(R.id.refundDate);

        orderIdTv.setText("Order ID: "+orderId);

        rateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailsActivity.this, RatingReviewActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            }
        });
        cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog logoutDialog = new Dialog(OrderDetailsActivity.this);
                logoutDialog.setContentView(R.layout.logout_dialog);
                logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView cancelBtn = logoutDialog.findViewById(R.id.cancelBtn);
                TextView logoutBtn = logoutDialog.findViewById(R.id.logoutBtn);
                TextView textView = logoutDialog.findViewById(R.id.textView4);
                logoutDialog.setCancelable(true);

                textView.setText("Are you sure want to cancel this order?");
                logoutBtn.setText("Leave");

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutDialog.dismiss();
                        cancelOrder(orderId);
                    }
                });
                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutDialog.dismiss();
                    }
                });
                logoutDialog.show();
            }
        });
        needHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        needHelpBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        orderTrackerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailsActivity.this, OrderTrackerActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            }
        });
        returnOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog logoutDialog = new Dialog(OrderDetailsActivity.this);
                logoutDialog.setContentView(R.layout.logout_dialog);
                logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView cancelBtn = logoutDialog.findViewById(R.id.cancelBtn);
                TextView logoutBtn = logoutDialog.findViewById(R.id.logoutBtn);
                TextView textView = logoutDialog.findViewById(R.id.textView4);
                logoutDialog.setCancelable(true);

                textView.setText("Are you sure want to return this order?");
                logoutBtn.setText("Return");

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutDialog.dismiss();

                    }
                });
                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutDialog.dismiss();
                        returnOrder(orderId);
                    }
                });
                logoutDialog.show();
            }
        });
    }

    private void checkOrderStatus(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        String orderStatus = snapshot.getString("orderStatus");
                        if (orderStatus.equals("cancelled")) {
                           packedIndicator.setVisibility(View.GONE);
                           packedTv.setVisibility(View.GONE);
                           packedDate.setVisibility(View.GONE);
                           packedTextIndicator.setVisibility(View.GONE);
                           packedShippingProgressBar.setVisibility(View.GONE);
                           shippingIndicator.setVisibility(View.GONE);
                           shippedDate.setVisibility(View.GONE);
                           shippedTv.setVisibility(View.GONE);
                           shippedTextIndicator.setVisibility(View.GONE);
                           orderedPackedProgressBar.setVisibility(View.GONE);

                           cancelOrderLl.setVisibility(View.GONE);

                           shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                           deliveredIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                           deliveredTextIndicator.setText("Cancelled");
                           deliveredTv.setText("Your Ordered has been Cancelled");
                           loadCancelledDetails(orderId);
                       }else if (orderStatus.equals("packed")){

                            packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                       }else if (orderStatus.equals("shipped")){

                            packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                            shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            shippingIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            shippedTv.setText("Your order has been shipped");
                            loadShippedDetails(orderId);
                        }else if (orderStatus.equals("delivered")){

                            cancelOrderLl.setVisibility(View.GONE);
                            returnOrderLl.setVisibility(View.VISIBLE);

                            packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                            shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            shippingIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            shippedTv.setText("Your order has been shipped");
                            loadShippedDetails(orderId);

                            deliveredIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            deliveredTv.setText("Your order has been Delivered");
                            loadDeliveredDetails(orderId);

                        }else if (orderStatus.equals("returned")){
                            cancelOrderLl.setVisibility(View.GONE);

                            returnProgressBar.setVisibility(View.VISIBLE);
                            refundProgressBar.setVisibility(View.VISIBLE);
                            returnIndicator.setVisibility(View.VISIBLE);
                            refundIndicator.setVisibility(View.VISIBLE);
                            refundProgressBar.setVisibility(View.VISIBLE);
                            returnRl.setVisibility(View.VISIBLE);
                            refundRl.setVisibility(View.VISIBLE);

                            packedIndicator.setVisibility(View.GONE);
                            packedTv.setVisibility(View.GONE);
                            packedDate.setVisibility(View.GONE);
                            packedTextIndicator.setVisibility(View.GONE);
                            packedShippingProgressBar.setVisibility(View.GONE);
                            shippingIndicator.setVisibility(View.GONE);
                            shippedDate.setVisibility(View.GONE);
                            shippedTv.setVisibility(View.GONE);
                            shippedTextIndicator.setVisibility(View.GONE);
                            orderedPackedProgressBar.setVisibility(View.GONE);

                            packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                            shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            shippingIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            shippedTv.setText("Your order has been shipped");
                            loadShippedDetails(orderId);

                            deliveredIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            deliveredTv.setText("Your order has been Delivered");
                            loadDeliveredDetails(orderId);

                            returnProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            returnIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            returnTv.setText("Your Order has been returned");
                            loadReturnDetails(orderId);
                        }else if (orderStatus.equals("refunded")) {

                            returnProgressBar.setVisibility(View.VISIBLE);
                            refundProgressBar.setVisibility(View.VISIBLE);
                            returnIndicator.setVisibility(View.VISIBLE);
                            refundIndicator.setVisibility(View.VISIBLE);
                            refundProgressBar.setVisibility(View.VISIBLE);
                            returnRl.setVisibility(View.VISIBLE);
                            refundRl.setVisibility(View.VISIBLE);

                            packedIndicator.setVisibility(View.GONE);
                            packedTv.setVisibility(View.GONE);
                            packedDate.setVisibility(View.GONE);
                            packedTextIndicator.setVisibility(View.GONE);
                            packedShippingProgressBar.setVisibility(View.GONE);
                            shippingIndicator.setVisibility(View.GONE);
                            shippedDate.setVisibility(View.GONE);
                            shippedTv.setVisibility(View.GONE);
                            shippedTextIndicator.setVisibility(View.GONE);
                            orderedPackedProgressBar.setVisibility(View.GONE);

                            packedShippingProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            packedTv.setText("Your order has been packed");
                            loadPackedDetails(orderId);

                            shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            shippingIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            shippedTv.setText("Your order has been shipped");
                            loadShippedDetails(orderId);

                            deliveredIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            deliveredTv.setText("Your order has been Delivered");
                            loadDeliveredDetails(orderId);

                            returnProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            returnIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            returnTv.setText("Your Order has been returned");
                            loadReturnDetails(orderId);

                            refundProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            refundIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            refundTv.setText("Your Amount has been Refunded");
                            loadRefundDetails(orderId);
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

                            deliveryDate.setText(orderCancelledDate + "  "+orderCancelledTime);
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

                            packedDate.setText(orderPackedDate + "  " + orderPackedTime);
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

                            shippedDate.setText(orderShippedDate + "  " + orderShippedTime);
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

                            deliveryDate.setText(orderDeliveredDate + "  " + orderDeliveredTime);
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

                            returnDate.setText(orderReturnDate + "  " + orderReturnTime);
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

                            refundDate.setText(orderRefundedDate + "  " + orderRefundedTime);
                        }
                    }
                });
    }

    private void loadOrderDetails(String orderId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {
                            String productTitle1 = snapshot.get("productTitle").toString();
                            String purchasedPrice = snapshot.get("purchasedPrice").toString();
                            String originalPrice = snapshot.get("originalPrice").toString();
                            String productPrice1 = snapshot.get("productPrice").toString();
                            String deliveryFee1 = snapshot.get("deliveryFee").toString();
                            String discount = snapshot.get("discount").toString();
                            String orderDate = snapshot.get("orderDate").toString();
                            String orderTime = snapshot.get("orderTime").toString();
                            String name = snapshot.get("name").toString();
                            String quantity = snapshot.get("quantity").toString();
                            String shippingAddress = snapshot.get("shippingAddress").toString();
                            String phoneNo = snapshot.get("phoneNo").toString();
                            String orderFrom = snapshot.get("orderFrom").toString();
                            productId = snapshot.get("productId").toString();

                            productTitle.setText(productTitle1);
                            productPrice.setText(purchasedPrice);
                            orderedDate.setText(orderDate + "   " + orderTime);
                            nameTv.setText(name);
                            addressTv.setText(shippingAddress);
                            phoneNoTv.setText(phoneNo);
                            totalItemsPrice.setText(originalPrice);
                            priceTv.setText(productPrice1);
                            totalPrice.setText(purchasedPrice);
                            deliveryPrice.setText(deliveryFee1);
                            discountPrice.setText(discount);
                            productQuantity.setText("Qty: " + quantity);
                            savedAmount.setText("You saved Rs."+discount+" on this Order");

                            binding.codAmountTv.setText("COD: "+purchasedPrice + " to be paid");

                            loadProductDetails(productId);
                            loadMyRatings(productId);
                            loadShopName(orderFrom);
                        }
                    }
                });
    }


    private void loadShopName(String orderFrom) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Sellers").document(orderFrom);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String shopName = snapshot.getString("shopName");

                shopNameTv.setText("Seller: "+shopName);
            }
        });
    }

    private void loadMyRatings(String productId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ratings");
        ref.child(productId).child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            rateNowBtn.setText("Edit Rating");

                            String uid = "" + snapshot.child("uid").getValue();
                            float ratings = Float.parseFloat("" + snapshot.child("ratings").getValue());

                            ratingBar.setRating(ratings);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadProductDetails(String productId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()) {
                    String productImage1 = snapshot.get("productImage").toString();
                    String productDescription = snapshot.get("productDescription").toString();

                    productDescriptionTv.setText(productDescription);

                    try {
                        Picasso.get().load(productImage1).fit().centerCrop().placeholder(R.drawable.ic_cart_black).into(productImage);
                    } catch (Exception e) {
                        productImage.setImageResource(R.drawable.ic_cart_black);
                    }
                }
            }
        });
    }
    private void cancelOrder(String orderId) {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String orderCancelledDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        String orderCancelledTime = currentTimeFormat.format(calForTime.getTime());

        isCancelled = true;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", "cancelled");
        hashMap.put("isCancelled", isCancelled);
        hashMap.put("orderCancelledDate", ""+ orderCancelledDate);
        hashMap.put("orderCancelledTime", ""+ orderCancelledTime);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(OrderDetailsActivity.this, "Order Cancelled Successfully!!!!!", Toast.LENGTH_SHORT).show();
                            packedIndicator.setVisibility(View.GONE);
                            packedTv.setVisibility(View.GONE);
                            packedDate.setVisibility(View.GONE);
                            packedTextIndicator.setVisibility(View.GONE);
                            packedShippingProgressBar.setVisibility(View.GONE);
                            shippingIndicator.setVisibility(View.GONE);
                            shippedDate.setVisibility(View.GONE);
                            shippedTv.setVisibility(View.GONE);
                            shippedTextIndicator.setVisibility(View.GONE);
                            orderedPackedProgressBar.setVisibility(View.GONE);

                            shippedDeliveryProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                            deliveredIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            deliveredTextIndicator.setText("Cancelled");
                            deliveredTv.setText("Your Ordered has been Cancelled");
                            deliveryDate.setText(orderCancelledDate + "  "+orderCancelledTime);
                        }else {
                            Toast.makeText(OrderDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void returnOrder(String orderId) {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String orderReturnDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        String orderReturnTime = currentTimeFormat.format(calForTime.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", "returned");
        hashMap.put("isReturned", true);
        hashMap.put("orderReturnDate", ""+ orderReturnDate);
        hashMap.put("orderReturnTime", ""+ orderReturnTime);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(orderId)
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(OrderDetailsActivity.this, "Order Returned Successfully!!!!!", Toast.LENGTH_SHORT).show();
                            packedIndicator.setVisibility(View.GONE);
                            packedTv.setVisibility(View.GONE);
                            packedDate.setVisibility(View.GONE);
                            packedTextIndicator.setVisibility(View.GONE);
                            packedShippingProgressBar.setVisibility(View.GONE);
                            shippingIndicator.setVisibility(View.GONE);
                            shippedDate.setVisibility(View.GONE);
                            shippedTv.setVisibility(View.GONE);
                            shippedTextIndicator.setVisibility(View.GONE);
                            orderedPackedProgressBar.setVisibility(View.GONE);

                            returnProgressBar.setVisibility(View.VISIBLE);
                            refundProgressBar.setVisibility(View.VISIBLE);
                            returnIndicator.setVisibility(View.VISIBLE);
                            refundIndicator.setVisibility(View.VISIBLE);
                            refundProgressBar.setVisibility(View.VISIBLE);
                            returnRl.setVisibility(View.VISIBLE);
                            refundRl.setVisibility(View.VISIBLE);

                            returnProgressBar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            returnIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            returnDate.setText(orderReturnDate+"  "+orderReturnTime);
                            returnTv.setText("Your Order has been returned");

                        }else {
                            Toast.makeText(OrderDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}