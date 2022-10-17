package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.ActivityDeliveryBinding;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;

public class DeliveryActivity extends AppCompatActivity {
    private ActivityDeliveryBinding binding;
    private Button addAddressBtn;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private TextView nameTv, addressTv, phoneNoTv, cuttedPrice, productTitle, coupenTv,
            productPrice, codAvailableTv, totalItemsPrice, deliveryPrice, totalPrice,
            savedAmount, totalCartAmount, productRateMiniView, discountPrice, priceTv,
            percentDiscountTv, freeDeliveryTv, deliveryAmountTv, quantityTv;
    private ImageView productImage, deleteWishListBtn;

    private String productId, price, productHead;
    private RelativeLayout cartContinueBtn;
    private ImageButton decrementBtn, incrementBtn;

    private int totalProductPrice = 0;
    private int totalCuttedPrice = 0;

    private int quantity = 1;

    public static final int SELECT_ADDRESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productId = getIntent().getStringExtra("productId");
        price = getIntent().getStringExtra("productPrice");
        productHead = getIntent().getStringExtra("productTitle");


        addAddressBtn = findViewById(R.id.changeAddressBtn);
        cuttedPrice = findViewById(R.id.cuttedPrice);
        productImage = findViewById(R.id.productImage);
        productTitle = findViewById(R.id.productTitle);
        coupenTv = findViewById(R.id.coupenTv);
        productPrice = findViewById(R.id.productPrice);
        codAvailableTv = findViewById(R.id.codAvailableTv);
        deleteWishListBtn = findViewById(R.id.deleteWishListBtn);
        totalItemsPrice = findViewById(R.id.totalItemsPrice);
        deliveryPrice = findViewById(R.id.deliveryPrice);
        totalPrice = findViewById(R.id.totalPrice);
        savedAmount = findViewById(R.id.savedAmount);
        totalCartAmount = findViewById(R.id.totalCartAmount);
        cartContinueBtn = findViewById(R.id.cartContinueBtn);
        productRateMiniView = findViewById(R.id.productRateMiniView);
        discountPrice = findViewById(R.id.discountPrice);
        priceTv = findViewById(R.id.priceTv);
        percentDiscountTv = findViewById(R.id.percentDiscountTv);
        freeDeliveryTv = findViewById(R.id.freeDeliveryTv);
        deliveryAmountTv = findViewById(R.id.deliveryAmountTv);
        decrementBtn = findViewById(R.id.decrementBtn);
        incrementBtn = findViewById(R.id.incrementBtn);
        quantityTv = findViewById(R.id.quantityTv);

        cuttedPrice.setPaintFlags(cuttedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        deleteWishListBtn.setVisibility(View.GONE);

        nameTv = findViewById(R.id.nameTv);
        addressTv = findViewById(R.id.addressTv);
        phoneNoTv = findViewById(R.id.phoneNoTv);
        addAddressBtn.setVisibility(View.VISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

//        loadAllCartItems();
        loadProductDetails(productId);
        loadDefaultAddress();
        loadInStock(productId);

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliveryActivity.this, MyAddressActivity.class);
                intent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(intent);
            }
        });
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                quantityTv.setText(""+quantity);
                loadProductDetails(productId);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity>1){
                    quantity--;
                    quantityTv.setText(""+quantity);
                    loadProductDetails(productId);
                }
            }
        });
        cartContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkQuantityAvailable(productId, quantity);

            }
        });
    }

    private void checkQuantityAvailable(String productId, int quantity) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products").child(productId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String productQuantityCount = ""+snapshot.child("noOfQuantity").getValue();

                if (Integer.parseInt(productQuantityCount) < quantity){
                    Toast.makeText(DeliveryActivity.this, "Only "+productQuantityCount+" products are available for now", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(DeliveryActivity.this, PaymentActivity.class);
                    intent.putExtra("productId", productId);
                    intent.putExtra("productTitle", productHead);
                    intent.putExtra("quantity", quantity);
                    startActivity(intent);
                }

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

                String quantity = quantityTv.getText().toString().trim();

                if (snapshot.exists()) {
                    String productTitle1 = snapshot.get("productTitle").toString();
                    String productImage1 = snapshot.get("productImage").toString();
                    String productPrice1 = snapshot.get("productPrice").toString();
                    String deliveryPrice1 = snapshot.get("deliveryFee").toString();
                    String avgRating = snapshot.get("avgRating").toString();
                    String productCuttedPrice1 = snapshot.get("productCuttedPrice").toString();
                    boolean isCod = (boolean) snapshot.get("isCod");

                    if (isCod) {
                        codAvailableTv.setText("Cash On Delivery Available");
                        codAvailableTv.setTextColor(getResources().getColor(R.color.successGreen));
                    } else {
                        codAvailableTv.setText("COD Not Available");
                        codAvailableTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }

                    productTitle.setText(productTitle1);

                    totalProductPrice = Integer.parseInt(productPrice1) * Integer.parseInt(quantity);
                    productPrice.setText("Rs."+totalProductPrice);
                    priceTv.setText("Rs."+totalProductPrice);

                    totalCuttedPrice = Integer.parseInt(productCuttedPrice1) * Integer.parseInt(quantity);
                    cuttedPrice.setText("Rs."+totalCuttedPrice);
                    totalItemsPrice.setText("Rs."+totalCuttedPrice);

                    productRateMiniView.setText(avgRating);

                    int percentDiscount = ((Integer.parseInt(productCuttedPrice1) - Integer.parseInt(productPrice1)) * 100)/Integer.parseInt(productCuttedPrice1);
                    percentDiscountTv.setText(percentDiscount+"% OFF");

                    if (deliveryPrice1.equals("FREE Delivery")){
                        deliveryPrice.setText("Rs.0");
                        int savedPrice = totalCuttedPrice - totalProductPrice;
                        savedAmount.setText("You saved Rs."+savedPrice+" on this Order");
                        totalPrice.setText("Rs."+totalProductPrice);
                        totalCartAmount.setText("Rs."+totalProductPrice);
                        freeDeliveryTv.setVisibility(View.VISIBLE);
                        deliveryAmountTv.setPaintFlags(deliveryAmountTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }else {
                        deliveryPrice.setText("Rs."+deliveryPrice1);
                        int savedPrice = totalCuttedPrice - totalProductPrice - Integer.parseInt(deliveryPrice1);
                        savedAmount.setText("You saved Rs."+savedPrice+" on this Order");
                        int price = totalProductPrice + Integer.parseInt(deliveryPrice1);
                        totalPrice.setText("Rs."+price);
                        totalCartAmount.setText("Rs."+price);
                        freeDeliveryTv.setVisibility(View.GONE);
                        deliveryAmountTv.setText("Delivery Fees Rs."+deliveryPrice1);
                    }
                    int discount = totalCuttedPrice - totalProductPrice;
                    discountPrice.setText("Rs."+discount);



                    try {
                        Picasso.get().load(productImage1).fit().centerCrop().placeholder(R.drawable.ic_cart_black).into(productImage);
                    } catch (Exception e) {
                        productImage.setImageResource(R.drawable.ic_cart_black);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
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
                        Toast.makeText(DeliveryActivity.this, "InStock Updated false!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DeliveryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DeliveryActivity.this, "InStock Updated true!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DeliveryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    String name = snapshot.get("name").toString();
                    String area = snapshot.get("area").toString();
                    String city = snapshot.get("city").toString();
                    String landmark = snapshot.get("landmark").toString();
                    String phoneNo = snapshot.get("phoneNo").toString();
                    String pinCode = snapshot.get("pinCode").toString();
                    String state = snapshot.get("state").toString();

                    nameTv.setText(name);
                    addressTv.setText(city + " " + area + "\n" + landmark + ", " + state + ", " + pinCode);
                    phoneNoTv.setText(phoneNo);

                } else {
                    Toast.makeText(DeliveryActivity.this, "No Default Address!!!!", Toast.LENGTH_SHORT).show();
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