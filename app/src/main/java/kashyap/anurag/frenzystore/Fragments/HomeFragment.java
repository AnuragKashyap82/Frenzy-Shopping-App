package kashyap.anurag.frenzystore.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.frenzystore.Activities.AddCategoryActivity;
import kashyap.anurag.frenzystore.Activities.ViewAllProductActivity;
import kashyap.anurag.frenzystore.Adapters.AdapterAllProducts;
import kashyap.anurag.frenzystore.Adapters.AdapterCategory;
import kashyap.anurag.frenzystore.Adapters.AdapterGrid;
import kashyap.anurag.frenzystore.Adapters.AdapterMostSoldProducts;
import kashyap.anurag.frenzystore.Adapters.AdapterRecentProduct;
import kashyap.anurag.frenzystore.Adapters.TrendingProductAdapter;
import kashyap.anurag.frenzystore.Models.GridModel;
import kashyap.anurag.frenzystore.Models.ModelMostSoldProduct;
import kashyap.anurag.frenzystore.Models.TrendingProductModel;
import kashyap.anurag.frenzystore.Models.ModelAllProduct;
import kashyap.anurag.frenzystore.Models.ModelCategory;
import kashyap.anurag.frenzystore.Models.ModelRecentProduct;
import kashyap.anurag.frenzystore.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private AdapterCategory adapterCategory;
    private ArrayList<ModelCategory> categoryArrayList;

    private TrendingProductAdapter trendingProductAdapter;
    private ArrayList<TrendingProductModel> trendingProductModelArrayList;

    private AdapterMostSoldProducts adapterMostSoldProducts;
    private ArrayList<ModelMostSoldProduct> mostSoldProductArrayList;

    private AdapterGrid adapterGrid;
    private ArrayList<GridModel> gridModelArrayList;

    private AdapterAllProducts adapterAllProducts;
    private ArrayList<ModelAllProduct> allProductArrayList;

    private AdapterRecentProduct adapterRecentProduct;
    private ArrayList<ModelRecentProduct> modelRecentProductArrayList;

    ImageSlider bannerSlider;
    private RecyclerView categoryRv;

    private ImageView stripImage;
    private MaterialCardView stripAdContainer;

    private TextView horizantalLayoutTitle, recentlyViewedTitle, allProductTitle;
    private Button horizantalLayoutViewAllBtn;
    private RecyclerView trendingRv, allProductRv;

    private TextView gridProductTitle;
    private Button gridProductViewAllBtn, allProductViewAllBtn;
    private RecyclerView gridProductRv, recentlyViewedRv, mostSoldRv;

    private FloatingActionButton addCategoriesBtn;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = view.findViewById(R.id.progressBar);

        loadAllCategory();
        loadBannerSlider();
        loadStripImages();
        loadTrendingProducts();
        loadMostSoldProducts();
        loadAllGridProducts();
        loadRecentProducts();
        loadAllProducts();


        bannerSlider = view.findViewById(R.id.bannerSlider);
        categoryRv = view.findViewById(R.id.categoryRv);

        stripImage = view.findViewById(R.id.strip_ad_image);
        stripAdContainer = view.findViewById(R.id.strip_ad_container);

        horizantalLayoutTitle = view.findViewById(R.id.trendingTitle);
        horizantalLayoutViewAllBtn = view.findViewById(R.id.seeAllBtn);
        trendingRv = view.findViewById(R.id.trendingRv);

        gridProductTitle = view.findViewById(R.id.gridProductTitle);
        gridProductViewAllBtn = view.findViewById(R.id.gridProductViewAllBtn);
        gridProductRv = view.findViewById(R.id.gridProductRv);

        recentlyViewedTitle = view.findViewById(R.id.recentlyViewedTitle);
        recentlyViewedRv = view.findViewById(R.id.recentlyViewedRv);

        allProductViewAllBtn = view.findViewById(R.id.allProductViewAllBtn);
        allProductRv = view.findViewById(R.id.allProductRv);
        allProductTitle = view.findViewById(R.id.allProductTitle);

        addCategoriesBtn = view.findViewById(R.id.addCategoriesBtn);

        mostSoldRv = view.findViewById(R.id.mostSoldRv);


        String head = horizantalLayoutTitle.getText().toString().trim();

        horizantalLayoutViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewAllProductActivity.class);
                intent.putExtra("CODE", 0);
                intent.putExtra("head", "" + head);
                startActivity(intent);
            }
        });
        String gridHead = gridProductTitle.getText().toString().trim();
        gridProductViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewAllProductActivity.class);
                intent.putExtra("CODE", 1);
                intent.putExtra("gridHead", "" + gridHead);
                startActivity(intent);
            }
        });
        String allProductHead = allProductTitle.getText().toString().trim();
        allProductViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewAllProductActivity.class);
                intent.putExtra("CODE", 2);
                intent.putExtra("allProductHead", "" + allProductHead);
                startActivity(intent);
            }
        });
        addCategoriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddCategoryActivity.class));
            }
        });

        return view;
    }

    private void loadAllCategory() {
        progressBar.setVisibility(View.VISIBLE);
        categoryArrayList = new ArrayList<>();

        firebaseFirestore.collection("Categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelCategory modelCategory = document.toObject(ModelCategory.class);
                                categoryArrayList.add(modelCategory);
                            }

                            adapterCategory = new AdapterCategory(getActivity(), categoryArrayList);
                            categoryRv.setAdapter(adapterCategory);
                            adapterCategory.notifyDataSetChanged();

                        } else {

                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadBannerSlider() {
        progressBar.setVisibility(View.VISIBLE);
        final List<SlideModel> bannerSliderModel = new ArrayList<>();
        firebaseFirestore.collection("BannerSlider")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bannerSliderModel.add(new SlideModel(document.getString("banner"), ScaleTypes.CENTER_CROP));


                                bannerSlider.setImageList(bannerSliderModel, ScaleTypes.CENTER_CROP);
                                bannerSlider.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onItemSelected(int i) {
                                        Toast.makeText(getContext(), "Banner Slider!!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } else {

                            Toast.makeText(getActivity(), "not Found Banner!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadStripImages() {
        progressBar.setVisibility(View.VISIBLE);
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

    private void loadTrendingProducts() {
        progressBar.setVisibility(View.VISIBLE);
        trendingProductModelArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            trendingProductModelArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                TrendingProductModel trendingProductModel = ds.getValue(TrendingProductModel.class);
                                checkIsActiveTrendingProduct(trendingProductModel, snapshot);

                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkIsActiveTrendingProduct(TrendingProductModel trendingProductModel, DataSnapshot s) {
        String productId = trendingProductModel.getProductId();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()){
                    boolean isActive = (boolean) snapshot.get("active");
                    if (isActive) {
                        trendingProductModelArrayList.add(trendingProductModel);
                        checkForViewAllBtn(trendingProductModelArrayList, s);

                        Collections.sort(trendingProductModelArrayList, new Comparator<TrendingProductModel>() {
                            @Override
                            public int compare(TrendingProductModel t1, TrendingProductModel t2) {
                                return t1.getTrendingCount().compareToIgnoreCase(t2.getTrendingCount());
                            }
                        });
                        Collections.reverse(trendingProductModelArrayList);

                        trendingProductAdapter = new TrendingProductAdapter(getActivity(), trendingProductModelArrayList, "HOME");
                        trendingRv.setAdapter(trendingProductAdapter);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void loadMostSoldProducts() {
        progressBar.setVisibility(View.VISIBLE);
        mostSoldProductArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            mostSoldProductArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelMostSoldProduct modelMostSoldProduct = ds.getValue(ModelMostSoldProduct.class);
                                checkMostSoldProductActive(modelMostSoldProduct);

                            }

                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkMostSoldProductActive(ModelMostSoldProduct modelMostSoldProduct) {
        String productId = modelMostSoldProduct.getProductId();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()) {
                    boolean isActive = (boolean) snapshot.get("active");
                    if (isActive) {
                        mostSoldProductArrayList.add(modelMostSoldProduct);
                        Collections.sort(mostSoldProductArrayList, new Comparator<ModelMostSoldProduct>() {
                            @Override
                            public int compare(ModelMostSoldProduct t1, ModelMostSoldProduct t2) {
                                return t1.getSellingCount().compareToIgnoreCase(t2.getSellingCount());
                            }
                        });
                        Collections.reverse(mostSoldProductArrayList);

                        adapterMostSoldProducts = new AdapterMostSoldProducts(getActivity(), mostSoldProductArrayList);
                        mostSoldRv.setAdapter(adapterMostSoldProducts);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void loadRecentProducts() {
        progressBar.setVisibility(View.VISIBLE);
        modelRecentProductArrayList = new ArrayList<>();

        firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("recentProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelRecentProduct modelRecentProduct = document.toObject(ModelRecentProduct.class);
                                modelRecentProductArrayList.add(modelRecentProduct);
                            }
                            Collections.sort(modelRecentProductArrayList, new Comparator<ModelRecentProduct>() {
                                @Override
                                public int compare(ModelRecentProduct t1, ModelRecentProduct t2) {
                                    return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                                }
                            });
                            Collections.reverse(modelRecentProductArrayList);

                            adapterRecentProduct = new AdapterRecentProduct(getActivity(), modelRecentProductArrayList);
                            recentlyViewedRv.setAdapter(adapterRecentProduct);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void loadAllGridProducts() {
        progressBar.setVisibility(View.VISIBLE);
        gridModelArrayList = new ArrayList<>();

        firebaseFirestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GridModel gridModel = document.toObject(GridModel.class);
                                checkGridActive(gridModel, document);

                            }

                        }
                    }
                });
    }

    private void checkGridActive(GridModel gridModel, QueryDocumentSnapshot document) {
        String productId = gridModel.getProductId();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()) {
                    boolean isActive = (boolean) snapshot.get("active");
                    if (isActive) {
                        gridModelArrayList.add(gridModel);
                        checkForGridViewAllBtn(gridModelArrayList, document);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                        gridProductRv.setLayoutManager(gridLayoutManager);

                        adapterGrid = new AdapterGrid(getActivity(), gridModelArrayList, "HOME");
                        gridProductRv.setAdapter(adapterGrid);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void loadAllProducts() {
        progressBar.setVisibility(View.VISIBLE);
        allProductArrayList = new ArrayList<>();
        firebaseFirestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelAllProduct modelAllProduct = document.toObject(ModelAllProduct.class);
                                checkAllProductIsActive(modelAllProduct);

                            }

                        }
                    }
                });

    }

    private void checkAllProductIsActive(ModelAllProduct modelAllProduct) {
        String productId = modelAllProduct.getProductId();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()) {
                    boolean isActive = (boolean) snapshot.get("active");
                    if (isActive) {
                        allProductArrayList.add(modelAllProduct);
                        checkForAllProductViewAllBtn(modelAllProduct, allProductArrayList);

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                        allProductRv.setLayoutManager(gridLayoutManager);

                        adapterAllProducts = new AdapterAllProducts(getActivity(), allProductArrayList, "HOME");
                        allProductRv.setAdapter(adapterAllProducts);

                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void checkForAllProductViewAllBtn(ModelAllProduct modelAllProduct, ArrayList<ModelAllProduct> allProductArrayList) {
        if (allProductArrayList.size() < 9) {
            allProductViewAllBtn.setVisibility(View.INVISIBLE);
            allProductViewAllBtn.setEnabled(false);
        } else {
            allProductViewAllBtn.setVisibility(View.VISIBLE);
            allProductViewAllBtn.setEnabled(true);
        }
    }

    private void checkForGridViewAllBtn(ArrayList<GridModel> gridModelArrayList, QueryDocumentSnapshot document) {
        if (gridModelArrayList.size() < 9) {
            gridProductViewAllBtn.setVisibility(View.INVISIBLE);
            gridProductViewAllBtn.setEnabled(false);
        } else {
            gridProductViewAllBtn.setVisibility(View.VISIBLE);
            gridProductViewAllBtn.setEnabled(true);
        }
    }

    private void checkForViewAllBtn(ArrayList<TrendingProductModel> trendingProductModelArrayList, DataSnapshot snapshot) {
        if (trendingProductModelArrayList.size() < 9) {
            horizantalLayoutViewAllBtn.setVisibility(View.INVISIBLE);
            horizantalLayoutViewAllBtn.setEnabled(false);
        } else {
            horizantalLayoutViewAllBtn.setVisibility(View.VISIBLE);
            horizantalLayoutViewAllBtn.setEnabled(true);
        }
    }


}


