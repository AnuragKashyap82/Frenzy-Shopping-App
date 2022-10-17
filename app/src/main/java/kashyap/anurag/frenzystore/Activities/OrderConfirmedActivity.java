package kashyap.anurag.frenzystore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.databinding.ActivityOderedConfirmendBinding;

import android.os.Bundle;

public class OrderConfirmedActivity extends AppCompatActivity {
    private ActivityOderedConfirmendBinding binding;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOderedConfirmendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getStringExtra("orderId");
        binding.orderIdTv.setText(orderId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        finish();
    }
}