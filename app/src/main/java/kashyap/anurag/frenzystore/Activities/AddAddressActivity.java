package kashyap.anurag.frenzystore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.databinding.ActivityAddAddressBinding;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddAddressActivity extends AppCompatActivity {
    private ActivityAddAddressBinding binding;
    private String city, area, pinCode, state, landmark, name, phoneNo, alternatePhoneNo;
    private int TRACKER;
    private FirebaseAuth firebaseAuth;
    private boolean selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCategory.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add Or Manage Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        TRACKER = getIntent().getIntExtra("TRACKER", -1);
        if (TRACKER == 1){
            binding.tracker.setVisibility(View.GONE);
        }else {
            binding.tracker.setVisibility(View.VISIBLE);
        }
    }

    private void validateData() {
        city = binding.cityEt.getText().toString().trim();
        area = binding.areaEt.getText().toString().trim();
        pinCode = binding.pincodeEt.getText().toString().trim();
        state = binding.stateEt.getText().toString().trim();
        landmark = binding.landmarkEt.getText().toString().trim();
        name = binding.nameEt.getText().toString().trim();
        phoneNo = binding.mobileNoEt.getText().toString().trim();
        alternatePhoneNo = binding.alternateMobileNoEt.getText().toString().trim();

        if (city.isEmpty()) {
            Toast.makeText(this, "Enter your City name!!!", Toast.LENGTH_SHORT).show();
        } else if (area.isEmpty()) {
            Toast.makeText(this, "Enter your area name!!!", Toast.LENGTH_SHORT).show();
        } else if (pinCode.isEmpty()) {
            Toast.makeText(this, "Enter your PinCode!!!", Toast.LENGTH_SHORT).show();
        } else if (state.isEmpty()) {
            Toast.makeText(this, "Enter your State name!!!", Toast.LENGTH_SHORT).show();
        } else if (landmark.isEmpty()) {
            Toast.makeText(this, "Enter your Landmark name!!!", Toast.LENGTH_SHORT).show();
        } else if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name!!!", Toast.LENGTH_SHORT).show();
        } else if (phoneNo.isEmpty()) {
            Toast.makeText(this, "Enter your phone No!!!", Toast.LENGTH_SHORT).show();
        } else {
            saveAddress();
        }
    }

    private void saveAddress() {

//        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
//        reference.collection("myAddress").document("defaultAddress")

        //to be modified later!!!!!!!!!

        binding.progressBar.setVisibility(View.VISIBLE);

        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("addressId", "" + timestamp);
        hashMap.put("city", "" + city);
        hashMap.put("area", "" + area);
        hashMap.put("pinCode", "" + pinCode);
        hashMap.put("state", "" + state);
        hashMap.put("landmark", "" + landmark);
        hashMap.put("name", "" + name);
        hashMap.put("selected", selected);
        hashMap.put("phoneNo", "" + phoneNo);
        hashMap.put("alternatePhoneNo", "" + alternatePhoneNo);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.collection("myAddress").document("default" )
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddAddressActivity.this, "Address Saved!!!", Toast.LENGTH_SHORT).show();
                            clearText();
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddAddressActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddAddressActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearText() {
        binding.cityEt.setText("");
        binding.areaEt.setText("");
        binding.pincodeEt.setText("");
        binding.landmarkEt.setText("");
        binding.stateEt.setText("");
        binding.nameEt.setText("");
        binding.mobileNoEt.setText("");
        binding.alternateMobileNoEt.setText("");
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