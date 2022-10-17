package kashyap.anurag.frenzystore.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.frenzystore.Fragments.CartFragment;
import kashyap.anurag.frenzystore.Fragments.HomeFragment;
import kashyap.anurag.frenzystore.Fragments.MyAccountFragment;
import kashyap.anurag.frenzystore.Fragments.MyOrdersFragment;
import kashyap.anurag.frenzystore.Fragments.MyRewardsFragment;
import kashyap.anurag.frenzystore.Fragments.MyWishlistFragment;
import kashyap.anurag.frenzystore.Models.CartItemModel;
import kashyap.anurag.frenzystore.R;
import kashyap.anurag.frenzystore.databinding.ActivityMainBinding;
import static kashyap.anurag.frenzystore.Activities.RegisterActivity.setSignUpFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    private FrameLayout frameLayout;
    private NavigationView navigationView;
    private RelativeLayout noDataRl;
    private FirebaseFirestore firebaseFirestore;
    private TextView badgeCount;
    private FirebaseAuth firebaseAuth;
    private TextView headerNameTv, headerEmailTv;
    private ImageView profilePic;


    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDER_FRAGMENT = 2;
    private static final int WISHLIST_FRAGMENT = 3;
    private static final int REWARD_FRAGMENT = 4;
    private static final int ACCOUNT_FRAGMENT = 5;
    private static int currentFragment = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.appBarMain.toolbar);

        frameLayout = findViewById(R.id.mainFrameLayout);

        noDataRl = findViewById(R.id.noDataRl);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {


            loadMyInfo();


            DrawerLayout drawer = binding.drawerLayout;
            navigationView = findViewById(R.id.nav_view);

            navigationView.setNavigationItemSelectedListener(MainActivity.this);
            navigationView.getMenu().getItem(0).setChecked(true);

            View headerView = navigationView.getHeaderView(0);
            headerNameTv = (TextView) headerView.findViewById(R.id.nameTv);
            headerEmailTv = (TextView) headerView.findViewById(R.id.emailTv);
            profilePic = (ImageView) headerView.findViewById(R.id.profileIv);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            setFragment(new HomeFragment(), HOME_FRAGMENT);
        } else {
            noDataRl.setVisibility(View.VISIBLE);
        }
    }

    private void loadMyInfo() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String name = snapshot.get("name").toString();
                String email = snapshot.get("email").toString();
                String profileImage = snapshot.get("profileImage").toString();


                headerNameTv.setText(name);
                headerEmailTv.setText(email);

                try {
                    Picasso.get().load(profileImage).fit().centerCrop().placeholder(R.drawable.ic_person_black).into(profilePic);
                } catch (Exception e) {
                    profilePic.setImageResource(R.drawable.ic_person_black);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) binding.drawerLayout;
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();
            } else {
                invalidateOptionsMenu();
                setFragment(new HomeFragment(), HOME_FRAGMENT);
                navigationView.getMenu().getItem(0).setChecked(true);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            loadAllCartItems();

            MenuItem cartItem = menu.findItem(R.id.cartIcon);
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badgeIcon);
            badgeIcon.setImageResource(R.drawable.ic_cart_black);
            badgeCount = cartItem.getActionView().findViewById(R.id.badgeCount);

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotToFragment("My Cart", new CartFragment(), CART_FRAGMENT);
                }
            });

        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.searchIcon) {

            Intent intent = new Intent(MainActivity.this, SearchProductActivity.class);
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
            startActivity(intent);

            return true;
        } else if (id == R.id.notificationIcon) {

            Dialog signInDialog = new Dialog(MainActivity.this);
            signInDialog.setContentView(R.layout.sign_in_dialog);
            signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView signInBtn = signInDialog.findViewById(R.id.signInBtn);
            TextView signUpBtn = signInDialog.findViewById(R.id.signUpBtn);
            signInDialog.setCancelable(true);

            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);


            signInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signInDialog.dismiss();
                    setSignUpFragment = false;
                    startActivity(intent);
                }
            });
            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signInDialog.dismiss();
                    setSignUpFragment = true;
                    startActivity(intent);
                }
            });
            signInDialog.show();

            return true;
        } else if (id == R.id.cartIcon) {
            gotToFragment("My Cart", new CartFragment(), CART_FRAGMENT);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navMyFrenzy) {
            invalidateOptionsMenu();
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            Toast.makeText(this, "Home!!!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.navOrders) {
            gotToFragment("My Orders", new MyOrdersFragment(), ORDER_FRAGMENT);
            Toast.makeText(this, "Orders!!!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.navRewards) {
            gotToFragment("My Cart", new MyRewardsFragment(), REWARD_FRAGMENT);
            Toast.makeText(this, "Rewards!!!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.navCart) {
            gotToFragment("My Cart", new CartFragment(), CART_FRAGMENT);
            Toast.makeText(this, "Carts!!!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.navWishlist) {
            gotToFragment("My Wishlist", new MyWishlistFragment(), WISHLIST_FRAGMENT);
            Toast.makeText(this, "Wishlist!!!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.navAccount) {
            gotToFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
        } else if (id == R.id.navLogout) {
            showLogoutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void gotToFragment(String title, Fragment fragment, int fragmentNo) {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment, fragmentNo);
        if (fragmentNo == CART_FRAGMENT) {
            navigationView.getMenu().getItem(3).setChecked(true);
        }
    }

    private void setFragment(Fragment fragment, int fragmentNo) {
        if (currentFragment != fragmentNo) {
            currentFragment = fragmentNo;
            invalidateOptionsMenu();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        } else {
            Toast.makeText(this, "On that fragment Only!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAllCartItems() {
        CartFragment.cartItemModelArrayList = new ArrayList<>();

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
                            if (CartFragment.cartItemModelArrayList.size() > 0) {
                                if (CartFragment.cartItemModelArrayList.size() < 99) {
                                    badgeCount.setText(String.valueOf(CartFragment.cartItemModelArrayList.size()));
                                } else {
                                    badgeCount.setText("99+");
                                }
                            } else {

                            }

                        }
                    }
                });
    }

    private void showLogoutDialog() {
        Dialog logoutDialog = new Dialog(MainActivity.this);
        logoutDialog.setContentView(R.layout.logout_dialog);
        logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView cancelBtn = logoutDialog.findViewById(R.id.cancelBtn);
        TextView logoutBtn = logoutDialog.findViewById(R.id.logoutBtn);
        logoutDialog.setCancelable(true);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog.dismiss();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog.dismiss();
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finishAffinity();
            }
        });
        logoutDialog.show();
    }
}