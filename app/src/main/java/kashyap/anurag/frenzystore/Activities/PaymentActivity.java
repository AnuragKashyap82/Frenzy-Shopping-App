package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.ActivityPaymentBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private String productId, productTitle;
    private RelativeLayout paymentBtn;
    private TextView totalAmount, savedAmount, totalPrice, deliveryPrice, totalItemsPrice, discountPrice, priceTv;
    private boolean isClicked = false;
    private FirebaseAuth firebaseAuth;
    private String orderDate, orderTime;
    private String name, landmark, city, area, pinCode, state, phoneNo, shopId;
    boolean isCancelled;
    private int quantity;

    private int totalProductPrice = 0;
    private int totalCuttedPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Payment Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        productId = getIntent().getStringExtra("productId");
        productTitle = getIntent().getStringExtra("productTitle");
        quantity = getIntent().getIntExtra("quantity", -1);

        Toast.makeText(this, ""+quantity, Toast.LENGTH_SHORT).show();

        loadProductDetails(productId);
        loadDefaultAddress();
        loadInStock(productId);

        totalItemsPrice = findViewById(R.id.totalItemsPrice);
        deliveryPrice = findViewById(R.id.deliveryPrice);
        totalPrice = findViewById(R.id.totalPrice);
        savedAmount = findViewById(R.id.savedAmount);
        totalAmount = findViewById(R.id.totalAmount);
        paymentBtn = findViewById(R.id.paymentBtn);
        discountPrice = findViewById(R.id.discountPrice);
        priceTv = findViewById(R.id.priceTv);

        binding.walletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PaymentActivity.this, "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.netBankingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PaymentActivity.this, "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.upiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PaymentActivity.this, "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.creditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PaymentActivity.this, "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.codBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClicked){
                    isClicked = false;
                    binding.codBtn.setBackgroundColor(getResources().getColor(R.color.white));
                }else {
                    isClicked = true;
                    binding.codBtn.setBackgroundColor(getResources().getColor(R.color.gray01));
                }

            }
        });
        binding.paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClicked){
                    addToMyOrder(productId);
                }else {
                    Toast.makeText(PaymentActivity.this, "Select Payment Method!!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadDefaultAddress() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users")
                .document(firebaseAuth.getUid()).collection("myAddress").document("default");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()) {
                     name = snapshot.get("name").toString();
                     area = snapshot.get("area").toString();
                     city = snapshot.get("city").toString();
                     landmark = snapshot.get("landmark").toString();
                     phoneNo = snapshot.get("phoneNo").toString();
                     pinCode = snapshot.get("pinCode").toString();
                     state = snapshot.get("state").toString();

//                    nameTv.setText(name);
//                    addressTv.setText(city + " " + area + "\n" + landmark + ", " + state + ", " + pinCode);
//                    phoneNoTv.setText(phoneNo);

                } else {
                    Toast.makeText(PaymentActivity.this, "No Default Address!!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void loadInStock(String productId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseReference.child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String noOfQuantity = ""+snapshot.child("noOfQuantity").getValue();
                        if (noOfQuantity.equals("0")){
                            binding.addToCartBuyNowLl.setVisibility(View.GONE);
                            binding.outOfStockLl.setVisibility(View.VISIBLE);
                            changeInStock(productId);
                        }else if (Integer.parseInt(noOfQuantity) < 0){
                            binding.addToCartBuyNowLl.setVisibility(View.GONE);
                            binding.outOfStockLl.setVisibility(View.VISIBLE);
                            changeInStock(productId);
                        }else {
                            binding.addToCartBuyNowLl.setVisibility(View.VISIBLE);
                            binding.outOfStockLl.setVisibility(View.GONE);
                            changeInStockTrue(productId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
    private void changeInStock(String productId) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("inStock", false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId);
        databaseReference.updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PaymentActivity.this, "InStock Updated false!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void changeInStockTrue(String productId) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("inStock", true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId);
        databaseReference.updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PaymentActivity.this, "InStock Updated true!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToMyOrder(String productId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        long orderId = System.currentTimeMillis();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        orderDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        orderTime = currentTimeFormat.format(calForTime.getTime());

        String originalPrice = totalItemsPrice.getText().toString();
        String productPrice = priceTv.getText().toString();
        String deliveryFee = deliveryPrice.getText().toString();
        String discount = discountPrice.getText().toString();
        String purchasedPrice = totalPrice.getText().toString();
        String savedPrice = discountPrice.getText().toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderId", ""+orderId);
        hashMap.put("orderDate", ""+orderDate);
        hashMap.put("orderTime", ""+orderTime);
        hashMap.put("paymentMethod", "COD");
        hashMap.put("quantity",""+quantity);
        hashMap.put("purchasedPrice",""+purchasedPrice);
        hashMap.put("originalPrice",""+originalPrice);
        hashMap.put("productPrice",""+productPrice);
        hashMap.put("deliveryFee",""+deliveryFee);
        hashMap.put("discount",""+discount);
        hashMap.put("savedPrice",""+savedPrice);
        hashMap.put("orderStatus","ordered");
        hashMap.put("isCancelled", false);
        hashMap.put("productTitle",productTitle);
        hashMap.put("name",name);
        hashMap.put("phoneNo",phoneNo);
        hashMap.put("shippingAddress",city + " " + area + "\n" + landmark + ", " + state + ", " + pinCode);
        hashMap.put("productId", ""+productId);
        hashMap.put("orderFrom", ""+shopId);
        hashMap.put("orderBy", ""+firebaseAuth.getUid());

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(""+orderId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(PaymentActivity.this, "Ordered placed Successfully!!!!!", Toast.LENGTH_SHORT).show();
                            removeProductFromMyCart(productId,orderId);
                            addToShopOrders(shopId, orderId, productId);

                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(PaymentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToShopOrders(String shopId, long orderId, String productId) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("shopId", ""+shopId);
        hashMap.put("orderId", ""+orderId);
        hashMap.put("orderBy", ""+firebaseAuth.getUid());
        hashMap.put("productId", ""+productId);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Sellers").document(shopId);
        documentReference.collection("orders").document(""+orderId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                        }else {
                            Toast.makeText(PaymentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateProductQuantityCount(long newQuantityCount, String productId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("noOfQuantity", newQuantityCount);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products").child(this.productId);
        databaseReference.updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PaymentActivity.this, "Quantity count decreased by 1!!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeProductFromMyCart(String productId, long orderId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid())
                .collection("myCart").document(productId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(PaymentActivity.this, OrderConfirmedActivity.class);
                            intent.putExtra("orderId", ""+orderId);
                            startActivity(intent);
                            decreaseProductQuantity(productId);
                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(PaymentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void decreaseProductQuantity(String productId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String productQuantityCount = ""+snapshot.child("noOfQuantity").getValue();

                if (productQuantityCount.equals("") || productQuantityCount.equals("null")) {
                    productQuantityCount = "0";
                }

                long newQuantityCount = Long.parseLong(productQuantityCount) - quantity;
                updateProductQuantityCount(newQuantityCount, productId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadProductDetails(String productId) {

        binding.progressBar.setVisibility(View.VISIBLE);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {

                if (snapshot.exists()) {

                    String productTitle1 = snapshot.get("productTitle").toString();
                    String productImage1 = snapshot.get("productImage").toString();
                    String productPrice1 = snapshot.get("productPrice").toString();
                    String deliveryPrice1 = snapshot.get("deliveryFee").toString();
                    shopId = snapshot.get("shopId").toString();
                    String avgRating = snapshot.get("avgRating").toString();
                    String productCuttedPrice1 = snapshot.get("productCuttedPrice").toString();

                    boolean isCod = (boolean) snapshot.get("isCod");

                    if (isCod) {

                    } else {

                    }
                    totalCuttedPrice = Integer.parseInt(productCuttedPrice1) * quantity;
                    totalItemsPrice.setText("Rs."+totalCuttedPrice);

                    totalProductPrice = Integer.parseInt(productPrice1) * quantity;
                    priceTv.setText("Rs."+totalProductPrice);



                    if (deliveryPrice1.equals("FREE Delivery")){
                        deliveryPrice.setText("Rs.0");
                        int savedPrice = totalCuttedPrice - totalProductPrice;
                        savedAmount.setText("You saved Rs."+savedPrice+" on this Order");
                        totalPrice.setText("Rs."+totalProductPrice);
                        totalAmount.setText("Rs."+totalProductPrice);
                    }else {
                        deliveryPrice.setText("Rs."+deliveryPrice1);
                        int savedPrice = totalCuttedPrice - totalProductPrice - Integer.parseInt(deliveryPrice1);
                        savedAmount.setText("You saved Rs."+savedPrice+" on this Order");
                        int price = totalProductPrice + Integer.parseInt(deliveryPrice1);
                        totalPrice.setText("Rs."+price);
                        totalAmount.setText("Rs."+price);
                    }

                    int discount = totalCuttedPrice - totalProductPrice;
                    discountPrice.setText("Rs."+discount);

                    binding.progressBar.setVisibility(View.GONE);

                } else {

                }
            }
        });

    }
}