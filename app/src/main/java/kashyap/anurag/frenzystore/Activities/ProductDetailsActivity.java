package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import kashyap.anurag.frenzystore.Adapters.AdapterQuestions;
import kashyap.anurag.frenzystore.Adapters.AdapterRating;
import kashyap.anurag.frenzystore.Adapters.ProductDescriptionAdapter;
import kashyap.anurag.frenzystore.Fragments.CartFragment;
import kashyap.anurag.frenzystore.Models.CartItemModel;
import kashyap.anurag.frenzystore.Models.ModelQuestion;
import kashyap.anurag.frenzystore.Models.ModelRating;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.ActivityProductDetailsBinding;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    private ActivityProductDetailsBinding binding;

    private FloatingActionButton wishlistBtn;
    private boolean isWishlist;

    private LinearLayout productDetailsLl, addToCartBuyNowLl, outOfStockLl;
    private ConstraintLayout productDetailsTabContainer, rewardLayout;
    private RelativeLayout addToCartBtn, buyNowBtn, productDetailsRlLayout;
    private String productId, productHead, price;

    private ViewPager productDescriptionViewPager;
    private TabLayout productDescriptionTabLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView ratingsRv, questionsAnswersRv;

    private AdapterRating adapterRating;
    private ArrayList<ModelRating> ratingArrayList;

    private AdapterQuestions adapterQuestions;
    private ArrayList<ModelQuestion> questionArrayList;

    private TextView productTitle, productPrice, productDetailsTv, cuttedPrice,
            codIndicatorTv, rewardTitle, rewardBody, namePincodeTv, completeAddressTv, codTv,
            deliverTo, noRatingCount, ratingCountTv, rateNowBtn, productRateMiniView, ratingMiniView,
            deliveryWithinTv, deliveryFeeTv, percentDiscountTv, noQuestionCount, askQuestionsBtn;
    private ImageView codIndicator;

    public static String productDescription;
    public static String productOtherDetails;
    private ImageSlider bannerSlider;

    private TextView badgeCount;
    private RatingBar ratingBar;
    private float avgRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productId = getIntent().getStringExtra("productId");
        productHead = getIntent().getStringExtra("productTitle");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        wishlistBtn = findViewById(R.id.wishlistBtn);
        productDescriptionViewPager = findViewById(R.id.productDetailsViewPager);
        productDescriptionTabLayout = findViewById(R.id.productDetailsTabLayout);
        addToCartBtn = findViewById(R.id.addToCartBtn);
        buyNowBtn = findViewById(R.id.buyNowBtn);
        productTitle = findViewById(R.id.productTitle);
        productPrice = findViewById(R.id.productPrice);
        productDetailsTabContainer = findViewById(R.id.productDetailsTabContainer);
        productDetailsRlLayout = findViewById(R.id.productDetailsRlLayout);
        productDetailsTv = findViewById(R.id.productDetailsTv);
        productDetailsLl = findViewById(R.id.productDetailsLl);
        bannerSlider = findViewById(R.id.bannerSlider);
        cuttedPrice = findViewById(R.id.cuttedPrice);
        codIndicatorTv = findViewById(R.id.codIndicatorTv);
        codIndicator = findViewById(R.id.codIndicator);
        rewardLayout = findViewById(R.id.rewardLayout);
        rewardBody = findViewById(R.id.rewardBody);
        rewardTitle = findViewById(R.id.rewardTitle);
        addToCartBuyNowLl = findViewById(R.id.addToCartBuyNowLl);
        outOfStockLl = findViewById(R.id.outOfStockLl);
        completeAddressTv = findViewById(R.id.completeAddressTv);
        namePincodeTv = findViewById(R.id.namePincodeTv);
        codTv = findViewById(R.id.codTv);
        ratingsRv = findViewById(R.id.ratingsRv);
        deliverTo = findViewById(R.id.deliverTo);
        noRatingCount = findViewById(R.id.noRatingCount);
        ratingBar = findViewById(R.id.ratingBar);
        ratingCountTv = findViewById(R.id.ratingCountTv);
        rateNowBtn = findViewById(R.id.rateNowBtn);
        productRateMiniView = findViewById(R.id.productRateMiniView);
        ratingMiniView = findViewById(R.id.ratingMiniView);
        deliveryWithinTv = findViewById(R.id.deliveryWithinTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        percentDiscountTv = findViewById(R.id.percentDiscountTv);
        noQuestionCount = findViewById(R.id.noQuestionCount);
        askQuestionsBtn = findViewById(R.id.askQuestionsBtn);
        questionsAnswersRv = findViewById(R.id.questionsAnswersRv);

        binding.progressBar.setVisibility(View.VISIBLE);

        increaseTrendingCount(productId, productHead);
        loadProductDetails(productId);
        checkMyCart(productId);
        checkWishlist(productId);
        checkRewardAvailable(productId);
        loadMyAddress();
        loadAllRatings(productId);
        loadInStock(productId);
        loadAllQuestions(productId);
        loadBannerSlider();


        setSupportActionBar(binding.appBarProductDetails.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(productHead);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWishlist) {
                    isWishlist = false;
                    removeFromMyWishList(productId);
                } else {
                    isWishlist = true;
                    addToMyWishList(productId);
                }

            }
        });
        productDescriptionViewPager.setAdapter(new ProductDescriptionAdapter(getSupportFragmentManager(), productDescriptionTabLayout.getTabCount()));
        productDescriptionViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDescriptionTabLayout));
        productDescriptionTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                productDescriptionViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAddressSaved();
            }
        });
        binding.removeFromCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromMyCart(productId);
            }
        });
        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAddressSaved();
                binding.progressBar.setVisibility(View.VISIBLE);
            }
        });
        noRatingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductDetailsActivity.this, "to be integrated!!!!", Toast.LENGTH_SHORT).show();
            }
        });
        rateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkProductPurchased(productId);
            }
        });
        noRatingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, AllReviewsActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });
        noQuestionCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, AllQuestionsActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });
        askQuestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, AskQuestionActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
            }
        });
    }

    private void checkRewardAvailable(String productId) {
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

            }
        });
    }


    private void loadProductDetails(String productId) {

        List<String> productImages = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("products").document(productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();


                            productPrice.setText("Rs."+documentSnapshot.get("productPrice").toString());

                            price = documentSnapshot.get("productPrice").toString();
                            String deliveryWithin = documentSnapshot.get("deliveryWithin").toString();
                            String deliveryFee = documentSnapshot.get("deliveryFee").toString();

                            if (deliveryFee.equals("FREE Delivery")){
                                deliveryFeeTv.setText("FREE Delivery");
                            }else {
                                deliveryFeeTv.setText("Rs."+deliveryFee);
                            }

                            productPrice.setText("Rs."+price);
                            deliveryWithinTv.setText("Delivery within "+deliveryWithin+ " days");

                            String cuttedPrice1 = documentSnapshot.get("productCuttedPrice").toString();
                            cuttedPrice.setText("Rs."+cuttedPrice1);

                            productTitle.setText(documentSnapshot.get("productTitle").toString());
//                            rewardTitle.setText(documentSnapshot.get("rewardTitle").toString());
//                            rewardBody.setText(documentSnapshot.get("rewardBody").toString());
                            productDetailsTv.setText(documentSnapshot.get("allDetails").toString());

                            if ((boolean) documentSnapshot.get("isCod")){
                                codIndicator.setVisibility(View.VISIBLE);
                                codIndicatorTv.setVisibility(View.VISIBLE);
                                codTv.setText("Cash on delivery available");
                            }else {
                                codIndicator.setVisibility(View.GONE);
                                codIndicatorTv.setVisibility(View.GONE);
                                codTv.setText("Cash on delivery not available");
                            }
                            if ((boolean) documentSnapshot.get("isTabSelected")){
                                productDetailsRlLayout.setVisibility(View.GONE);
                                productDetailsTabContainer.setVisibility(View.VISIBLE);
                            }else {
                                productDetailsRlLayout.setVisibility(View.VISIBLE);
                                productDetailsTabContainer.setVisibility(View.GONE);
                            }

                            int percentDiscount = ((Integer.parseInt(cuttedPrice1) - Integer.parseInt(price)) * 100)/Integer.parseInt(cuttedPrice1);
                            percentDiscountTv.setText(percentDiscount+"% OFF");

                        } else {
                            Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                            addToCartBuyNowLl.setVisibility(View.GONE);
                            outOfStockLl.setVisibility(View.VISIBLE);
                            changeInStock(productId);
                        }else if (Integer.parseInt(noOfQuantity) < 0){
                            addToCartBuyNowLl.setVisibility(View.GONE);
                            outOfStockLl.setVisibility(View.VISIBLE);
                            changeInStock(productId);
                        }else {
                            addToCartBuyNowLl.setVisibility(View.VISIBLE);
                            outOfStockLl.setVisibility(View.GONE);
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
                        Toast.makeText(ProductDetailsActivity.this, "InStock Updated false!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProductDetailsActivity.this, "InStock Updated true!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMyAddress() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myAddress").document("default")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()) {
                            namePincodeTv.setVisibility(View.VISIBLE);
                            completeAddressTv.setVisibility(View.VISIBLE);
                            deliverTo.setVisibility(View.VISIBLE);

                            String name = snapshot.get("name").toString();
                            String area = snapshot.get("area").toString();
                            String city = snapshot.get("city").toString();
                            String landmark = snapshot.get("landmark").toString();
                            String phoneNo = snapshot.get("phoneNo").toString();
                            String pinCode = snapshot.get("pinCode").toString();
                            String state = snapshot.get("state").toString();

                            namePincodeTv.setText(name + ", "+pinCode);
                            completeAddressTv.setText(city + " " + area+"  " + landmark +", "+ state + ", " + pinCode);


                        } else {
                            Toast.makeText(ProductDetailsActivity.this, "No Default Address!!!!", Toast.LENGTH_SHORT).show();
                            namePincodeTv.setVisibility(View.GONE);
                            completeAddressTv.setVisibility(View.GONE);
                            deliverTo.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private float ratingSum = 0;
    private void loadAllRatings(String productId) {
        ratingArrayList =new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ratings").child(productId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ratingArrayList.clear();
                    ratingSum = 0;
                    for (DataSnapshot ds: snapshot.getChildren()){
                        float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                        ratingSum = ratingSum + rating;

                        ModelRating modelRating = ds.getValue(ModelRating.class);
                        ratingArrayList.add(modelRating);
                        String noOfRating = String.valueOf(ratingArrayList.size());
                        noRatingCount.setText("See all "+ noOfRating +" reviews");
                        ratingCountTv.setText(noOfRating +" ratings \n and reviews");

                        ratingMiniView.setText(noOfRating + "  Ratings");
                    }

                    adapterRating = new AdapterRating(ProductDetailsActivity.this, ratingArrayList, "THREE");
                    ratingsRv.setAdapter(adapterRating);

                    long numberOfReviews = snapshot.getChildrenCount();
                    avgRating = ratingSum/numberOfReviews;

                    productRateMiniView.setText(String.format("%.1f", avgRating));
                    ratingBar.setRating(avgRating);

                    updateAvgRating(avgRating);
                }else {
                    noRatingCount.setText("See all "+ 0 +" reviews");
                    ratingCountTv.setText(0 +" ratings \n and reviews");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateAvgRating(float avgRating) {

        HashMap<String, Object> hashMap  = new HashMap<>();
        hashMap.put("avgRating", ""+avgRating);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProductDetailsActivity.this, "Avg updated Successfully!!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkProductPurchased(String productId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myOrders").document(productId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Toast.makeText(ProductDetailsActivity.this, "To be integrated!!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadAllQuestions(String productId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        questionArrayList =new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Questions").child(productId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    questionArrayList.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        ModelQuestion modelQuestion = ds.getValue(ModelQuestion.class);
                        questionArrayList.add(modelQuestion);

                        String noOfQuestions = String.valueOf(questionArrayList.size());

                        noQuestionCount.setText("See all "+noOfQuestions+ " questions");
                    }

                    adapterQuestions = new AdapterQuestions(ProductDetailsActivity.this, questionArrayList, "THREE");
                    questionsAnswersRv.setAdapter(adapterQuestions);

                    binding.progressBar.setVisibility(View.GONE);
                }else {
                    noQuestionCount.setText("See all "+ 0 +" questions");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addProductToMyCart(String productId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        long timestamp = System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("productId", productId);
        hashMap.put("timestamp", ""+timestamp);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myCart").document(productId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProductDetailsActivity.this, "Product Added To Cart!!!!", Toast.LENGTH_SHORT).show();
                            invalidateOptionsMenu();
                            binding.progressBar.setVisibility(View.GONE);
                        }else {
                            Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkMyCart(String productId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myCart").document(productId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()){
                            binding.addToCartBtn.setVisibility(View.GONE);
                            binding.removeFromCartBtn.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.GONE);
                        }else {
                            binding.addToCartBtn.setVisibility(View.VISIBLE);
                            binding.removeFromCartBtn.setVisibility(View.GONE);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void removeFromMyCart(String productId) {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myCart").document(productId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProductDetailsActivity.this, "Product Removed From My Cart!!!!", Toast.LENGTH_SHORT).show();
                            invalidateOptionsMenu();
                        }else {
                            Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkWishlist(String productId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myWishlist").document(productId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()){

                            wishlistBtn.setImageResource(R.drawable.ic_favourite_black);
                            isWishlist = true;
                        }else {
                            wishlistBtn.setImageResource(R.drawable.ic_fav_border);
                            isWishlist = false;
                        }
                    }
                });
    }

    private void addToMyWishList(String productId) {
        long timestamp = System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("productId", productId);
        hashMap.put("timestamp", ""+timestamp);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myWishlist").document(productId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProductDetailsActivity.this, "Product Added To WishList!!!!", Toast.LENGTH_SHORT).show();
                            wishlistBtn.setImageResource(R.drawable.ic_favourite_black);
                        }else {
                            Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFromMyWishList(String productId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myWishlist").document(productId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProductDetailsActivity.this, "Product Removed From Wishlist!!!!", Toast.LENGTH_SHORT).show();
                            wishlistBtn.setImageResource(R.drawable.ic_fav_border);

                        }else {
                            Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAddressSaved() {
        binding.progressBar.setVisibility(View.VISIBLE);
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myAddress").document("default")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()){
                            addProductToMyCartFromBuyBtn(productId);
                            binding.progressBar.setVisibility(View.GONE);
                            addProductToMyCart(productId);
                        }else {
                            Intent intent = new Intent(ProductDetailsActivity.this, AddAddressActivity.class);
                            startActivity(intent);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void addProductToMyCartFromBuyBtn(String productId) {
        binding.progressBar.setVisibility(View.VISIBLE);

        long timestamp = System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("productId", productId);
        hashMap.put("timestamp", ""+timestamp);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myCart").document(productId)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            invalidateOptionsMenu();
                            showBuyDialog(productId, productHead);
                        }else {
                            Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBuyDialog(String productId, String productHead) {

        binding.progressBar.setVisibility(View.GONE);

        Dialog buyProductDialog = new Dialog(ProductDetailsActivity.this);
        buyProductDialog.setContentView(R.layout.buy_product_dialog);
        buyProductDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView productTitle = buyProductDialog.findViewById(R.id.productTitle);
        TextView closeDialogBtn = buyProductDialog.findViewById(R.id.closeDialogBtn);
        TextView continueBtn = buyProductDialog.findViewById(R.id.continueBtn);
        buyProductDialog.setCancelable(true);
        productTitle.setText(productHead);


        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyProductDialog.dismiss();
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("productPrice", ""+price);
                intent.putExtra("productTitle", productHead);
                startActivity(intent);
                buyProductDialog.dismiss();
            }
        });
        buyProductDialog.show();
    }

    private void increaseTrendingCount(String productId, String productHead) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");
        ref.child(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String trendingCount = "" + snapshot.child("trendingCount").getValue();
                        if (trendingCount.equals("") || trendingCount.equals("null")) {
                            trendingCount = "0";
                        }
                        long newTrendingCount = Long.parseLong(trendingCount) + 1;
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("trendingCount", ""+newTrendingCount);
                        hashMap.put("productTitle",""+ productHead);
                        hashMap.put("productId",""+ productId);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("products");
                        reference.child(productId)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ProductDetailsActivity.this, "Trending Count Updated Successfully!!!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icons, menu);

        loadAllCartItems();

        MenuItem cartItem = menu.findItem(R.id.cartIcon);
        cartItem.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badgeIcon);
        badgeIcon.setImageResource(R.drawable.ic_cart_black);
        badgeCount = cartItem.getActionView().findViewById(R.id.badgeCount);

        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductDetailsActivity.this, "Cart Btn Clicked!!!    ", Toast.LENGTH_SHORT).show();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.searchIcon) {
            Toast.makeText(this, "Search product Details!!!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void loadAllCartItems() {
        CartFragment.cartItemModelArrayList =new ArrayList<>();

        firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("myCart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CartItemModel cartItemModel = document.toObject(CartItemModel.class);
                                CartFragment.cartItemModelArrayList.add(cartItemModel);
                            }
                            if (CartFragment.cartItemModelArrayList.size() > 0){
                                if (CartFragment.cartItemModelArrayList.size() < 99) {
                                    badgeCount.setText(String.valueOf(CartFragment.cartItemModelArrayList.size()));
                                } else {
                                    badgeCount.setText("99+");
                                }
                            }else {

                            }

                        }
                    }
                });
    }
    private void loadBannerSlider() {
        final List<SlideModel> bannerSliderModel = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("productImages").child(productId).child("BannerImages");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot data : snapshot.getChildren())
                                bannerSliderModel.add(new SlideModel(data.child("productImage").getValue().toString(), ScaleTypes.FIT));

                            bannerSlider.setImageList(bannerSliderModel, ScaleTypes.CENTER_CROP);
                            bannerSlider.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onItemSelected(int i) {
                                    Toast.makeText(ProductDetailsActivity.this, "Banner Slider!!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(ProductDetailsActivity.this, "No ProductImages!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}