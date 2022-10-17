package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import kashyap.anurag.frenzystore.Adapters.AdapterGrid;
import kashyap.anurag.frenzystore.Adapters.WishListAdapter;
import kashyap.anurag.frenzystore.Models.GridModel;
import kashyap.anurag.frenzystore.Models.WishListModel;
import kashyap.anurag.frenzystore.databinding.ActivityViewAllProductBinding;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewAllProductActivity extends AppCompatActivity {
    private ActivityViewAllProductBinding binding;

    private int CODE;
    private FirebaseFirestore firebaseFirestore;

    private AdapterGrid adapterGrid;
    private ArrayList<GridModel> gridModelArrayList;

    private WishListAdapter wishListAdapter;
    private ArrayList<WishListModel> wishListModelArrayList;
    private String head, gridHead, allProductHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore  = FirebaseFirestore.getInstance();

        head = getIntent().getStringExtra("head");
        gridHead = getIntent().getStringExtra("gridHead");
        allProductHead = getIntent().getStringExtra("allProductHead");


        CODE = getIntent().getIntExtra("CODE", -1);
        if (CODE == 0){
            binding.horizantalProductRv.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(head);
            loadHorizantalProduct();
        }else if (CODE == 1){
            binding.gridProductRv.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(gridHead);
            loadGridProduct();
        }else if (CODE == 2){
            binding.gridProductRv.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(allProductHead);
            loadAllProducts();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

       if (id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadHorizantalProduct() {
        binding.progressBar.setVisibility(View.VISIBLE);
        wishListModelArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            wishListModelArrayList.clear();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                WishListModel wishListModel = ds.getValue(WishListModel.class);
                                wishListModelArrayList.add(wishListModel);

                            }
                            Collections.sort(wishListModelArrayList, new Comparator<WishListModel>() {
                                @Override
                                public int compare(WishListModel t1, WishListModel t2) {
                                    return t1.getTrendingCount().compareToIgnoreCase(t2.getTrendingCount());
                                }
                            });
                            Collections.reverse(wishListModelArrayList);

                            wishListAdapter = new WishListAdapter(ViewAllProductActivity.this, wishListModelArrayList, false);
                            binding.horizantalProductRv.setAdapter(wishListAdapter);
                            wishListAdapter.notifyDataSetChanged();

                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadGridProduct() {
        binding.progressBar.setVisibility(View.VISIBLE);
        gridModelArrayList =new ArrayList<>();

        firebaseFirestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GridModel gridModel = document.toObject(GridModel.class);
                                gridModelArrayList.add(gridModel);

                            }
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewAllProductActivity.this, 2);
                            binding.gridProductRv.setLayoutManager(gridLayoutManager);

                            adapterGrid = new AdapterGrid(ViewAllProductActivity.this, gridModelArrayList, "VIEW_ALL");
                            binding.gridProductRv.setAdapter(adapterGrid);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void loadAllProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        gridModelArrayList =new ArrayList<>();

        firebaseFirestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GridModel gridModel = document.toObject(GridModel.class);
                                gridModelArrayList.add(gridModel);

                            }
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewAllProductActivity.this, 2);
                            binding.gridProductRv.setLayoutManager(gridLayoutManager);

                            adapterGrid = new AdapterGrid(ViewAllProductActivity.this, gridModelArrayList, "VIEW_ALL");
                            binding.gridProductRv.setAdapter(adapterGrid);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}