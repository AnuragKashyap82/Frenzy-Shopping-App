package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.SimpleItemAnimator;
import kashyap.anurag.frenzystore.Adapters.AddressAdapter;
import kashyap.anurag.frenzystore.Models.AddressModel;
import kashyap.anurag.frenzystore.databinding.ActivityMyAddressBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static kashyap.anurag.frenzystore.Activities.DeliveryActivity.SELECT_ADDRESS;

public class MyAddressActivity extends AppCompatActivity {
    private ActivityMyAddressBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private static AddressAdapter addressAdapter;
    private ArrayList<AddressModel> addressModelArrayList;
    private int MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        binding.addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAddressActivity.this, AddAddressActivity.class);
                intent.putExtra("TRACKER",1);
                startActivity(intent);
            }
        });

        MODE = getIntent().getIntExtra("MODE", -1);

        loadAllAddresses();

        if (MODE == SELECT_ADDRESS){
            binding.deliverHereBtn.setVisibility(View.VISIBLE);
        }else {
            binding.deliverHereBtn.setVisibility(View.GONE);
        }
    }

    private void loadAllAddresses() {
        addressModelArrayList =new ArrayList<>();

        firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("myAddress")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AddressModel addressModel = document.toObject(AddressModel.class);
                                addressModelArrayList.add(addressModel);
                                binding.noAddressSaved.setText(addressModelArrayList.size() + " Saved Addresses ");
                            }
                            addressAdapter = new AddressAdapter(MyAddressActivity.this, addressModelArrayList, MODE);
                            binding.myAddressesRv.setAdapter(addressAdapter);
                            ((SimpleItemAnimator)binding.myAddressesRv.getItemAnimator()).setSupportsChangeAnimations(false);
                            addressAdapter.notifyDataSetChanged();

                        }
                    }
                });
    }
    public static void refreshItem(int deselect, int select){
        addressAdapter.notifyItemChanged(deselect);
        addressAdapter.notifyItemChanged(select);
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
}