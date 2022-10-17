package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import kashyap.anurag.frenzystore.Adapters.AdapterSearchProducts;
import kashyap.anurag.frenzystore.Models.ModelSearchProduct;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.ActivitySearchProductBinding;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchProductActivity extends AppCompatActivity {
    private ActivitySearchProductBinding binding;
    private AdapterSearchProducts adapterSearchProducts;
    private ArrayList<ModelSearchProduct> searchProductArrayList;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(SearchProductActivity.this, R.color.white));
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        binding.searchEt.requestFocus();

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllProducts();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    adapterSearchProducts.getFilter().filter(charSequence);
                }
                catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadAllProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        searchProductArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelSearchProduct modelSearchProduct = document.toObject(ModelSearchProduct.class);
                                checkActiveProduct(modelSearchProduct);

                            }

                        }else {
                            Toast.makeText(SearchProductActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkActiveProduct(ModelSearchProduct modelSearchProduct) {
        String productId = modelSearchProduct.getProductId();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("products").document(productId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                boolean isActive = (boolean) snapshot.get("active");
                if (isActive){
                    searchProductArrayList.add(modelSearchProduct);
                    Collections.sort(searchProductArrayList, new Comparator<ModelSearchProduct>() {
                        @Override
                        public int compare(ModelSearchProduct t1, ModelSearchProduct t2) {
                            return t1.getProductId().compareToIgnoreCase(t2.getProductId());
                        }
                    });
                    Collections.reverse(searchProductArrayList);

                    adapterSearchProducts = new AdapterSearchProducts(SearchProductActivity.this, searchProductArrayList);
                    binding.searchRv.setAdapter(adapterSearchProducts);
                    binding.progressBar.setVisibility(View.GONE);
                }else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}