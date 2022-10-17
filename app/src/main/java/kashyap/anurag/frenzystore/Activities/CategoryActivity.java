package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Adapters.AdapterGrid;
import kashyap.anurag.frenzystore.Adapters.TrendingProductAdapter;
import kashyap.anurag.frenzystore.Models.GridModel;
import kashyap.anurag.frenzystore.Models.TrendingProductModel;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.ActivityCategoryBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private ActivityCategoryBinding binding;
    private String categoryName;
    private FirebaseFirestore firebaseFirestore;

    private TrendingProductAdapter trendingProductAdapter;
    private ArrayList<TrendingProductModel> trendingProductModelArrayList;

    private AdapterGrid adapterGrid;
    private ArrayList<GridModel> gridModelArrayList;

    ImageSlider bannerSlider;
    private RecyclerView categoryRv;

    private ImageView stripImage;
    private MaterialCardView stripAdContainer;

    private TextView horizantalLayoutTitle;
    private Button horizantalLayoutViewAllBtn;
    private RecyclerView trendingRv;

    private TextView gridProductTitle;
    private Button gridProductViewAllBtn;
    private RecyclerView gridProductRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        categoryName = getIntent().getStringExtra("categoryName");

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bannerSlider = findViewById(R.id.bannerSlider);
        categoryRv = findViewById(R.id.categoryRv);

        stripImage = findViewById(R.id.strip_ad_image);
        stripAdContainer = findViewById(R.id.strip_ad_container);

        horizantalLayoutTitle = findViewById(R.id.trendingTitle);
        horizantalLayoutViewAllBtn = findViewById(R.id.seeAllBtn);
        trendingRv = findViewById(R.id.trendingRv);

        gridProductTitle = findViewById(R.id.gridProductTitle);
        gridProductViewAllBtn = findViewById(R.id.gridProductViewAllBtn);
        gridProductRv = findViewById(R.id.gridProductRv);

        loadBannerSlider();
        loadStripImages();
        loadTrendingProducts(categoryName);
        loadAllGridProducts(categoryName);

        String head = horizantalLayoutTitle.getText().toString().trim();
        horizantalLayoutViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, ViewAllProductActivity.class);
                intent.putExtra("CODE",0);
                intent.putExtra("head",""+head);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.searchIcon){
            Toast.makeText(this, "Search!!!!", Toast.LENGTH_SHORT).show();
            return true;
        }else if (id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void loadBannerSlider() {
        final List<SlideModel> bannerSliderModel = new ArrayList<>();
        firebaseFirestore.collection("BannerSlider")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bannerSliderModel.add(new SlideModel(document.getString("banner"), ScaleTypes.CENTER_CROP));

                                bannerSlider.setImageList(bannerSliderModel, ScaleTypes.CENTER_CROP);
                                bannerSlider.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onItemSelected(int i) {
                                        Toast.makeText(CategoryActivity.this, "Banner Slider!!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }else {
                            Toast.makeText(CategoryActivity.this, "not Found Banner!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void loadStripImages() {
        FirebaseFirestore.getInstance().collection("StripImages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String image = document.getString("categoryIcon");
                                try {
                                    Picasso.get().load(image).placeholder(R.drawable.ic_person_black).into(stripImage);
                                } catch (Exception e) {
                                    stripImage.setImageResource(R.drawable.ic_person_black);
                                }
                            }

                        }
                    }
                });
    }
    private void loadAllGridProducts(String categoryName) {
        gridModelArrayList =new ArrayList<>();

        firebaseFirestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GridModel gridModel = document.toObject(GridModel.class);
                                checkIsActive(gridModel, document);

                            }


                        }
                    }
                });
    }

    private void checkIsActive(GridModel gridModel, QueryDocumentSnapshot document) {
        String productId = gridModel.getProductId();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                boolean isActive = (boolean) snapshot.get("active");
                if (isActive) {
                    String currentCategoryName = gridModel.getCategory();
                    if (currentCategoryName.equals(categoryName)){
                        gridModelArrayList.add(gridModel);
                        checkForGridViewAllBtn(gridModelArrayList, document);

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(CategoryActivity.this, 2);
                        gridProductRv.setLayoutManager(gridLayoutManager);

                        adapterGrid = new AdapterGrid(CategoryActivity.this, gridModelArrayList, "HOME");
                        gridProductRv.setAdapter(adapterGrid);
                    }
                } else {

                }
            }
        });
    }

    private void loadTrendingProducts(String categoryName) {
        trendingProductModelArrayList =new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trendingProductModelArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            TrendingProductModel trendingProductModel = ds.getValue(TrendingProductModel.class);
                            String productId = trendingProductModel.getProductId();
                            loadTrendingProductDetails(productId, categoryName, trendingProductModel);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadTrendingProductDetails(String productId, String categoryName, TrendingProductModel trendingProductModel) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()){
                    String category = snapshot.get("category").toString();
                    if (category.equals(categoryName)){
                        boolean isActive = (boolean) snapshot.get("active");
                        if (isActive){
                            trendingProductModelArrayList.add(trendingProductModel);
                            Collections.sort(trendingProductModelArrayList, new Comparator<TrendingProductModel>() {
                                @Override
                                public int compare(TrendingProductModel t1, TrendingProductModel t2) {
                                    return t1.getTrendingCount().compareToIgnoreCase(t2.getTrendingCount());
                                }
                            });
                            Collections.reverse(trendingProductModelArrayList);

                            trendingProductAdapter = new TrendingProductAdapter(CategoryActivity.this, trendingProductModelArrayList, "HOME");
                            trendingRv.setAdapter(trendingProductAdapter);
                        }else {

                        }

                    }else {
                        Toast.makeText(CategoryActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkForGridViewAllBtn(ArrayList<GridModel> gridModelArrayList, QueryDocumentSnapshot document) {
        if (gridModelArrayList.size() < 9){
            gridProductViewAllBtn.setVisibility(View.INVISIBLE);
            gridProductViewAllBtn.setEnabled(false);
        }else {
            gridProductViewAllBtn.setVisibility(View.VISIBLE);
            gridProductViewAllBtn.setEnabled(true);
        }
    }

    private void checkForViewAllBtn(ArrayList<TrendingProductModel> trendingProductModelArrayList, DataSnapshot document) {
        if (trendingProductModelArrayList.size() < 9){
            horizantalLayoutViewAllBtn.setVisibility(View.INVISIBLE);
            horizantalLayoutViewAllBtn.setEnabled(false);
        }else {
            horizantalLayoutViewAllBtn.setVisibility(View.VISIBLE);
            horizantalLayoutViewAllBtn.setEnabled(true);
        }
    }
}
