package com.example.fithub.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fithub.R;
import com.example.fithub.databinding.ActivityMainBinding;
import com.example.fithub.fragment.ChatFragment;
import com.example.fithub.fragment.FeedFragment;
import com.example.fithub.fragment.HomeFragment;
import com.example.fithub.fragment.ProfileFragment;
import com.example.fithub.viewmodel.UserViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Initialisation de View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.open_nav,
                R.string.close_nav
        );

        binding.drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment())
                    .commit();
            binding.navView.setCheckedItem(R.id.nav_home);
        }

        binding.navView.bringToFront();

        binding.navView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            // Marquer l’item comme sélectionné
            item.setChecked(true);

            // Fermer le drawer
            binding.drawerLayout.closeDrawers();

            // Changer de fragment
            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());

            } else if (id == R.id.nav_settings) {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_share) {
                Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_about) {
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_notifications) {
                Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId(); // Récupérer l'ID de l'élément cliqué une seule fois

            // REMPLACER LE SWITCH PAR DES IF/ELSE IF
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());

            } else if (itemId == R.id.feed) {
                replaceFragment(new FeedFragment());

            } else if (itemId == R.id.chat) {
                replaceFragment(new ChatFragment());

            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());

            }
            // Si vous aviez l'item vide (<item android:title=""/>) dans le menu,
            // son ID sera 0, vous n'avez pas besoin de le gérer ici.

            return true;
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }

    // Outside onCreate method
    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = dialog.findViewById(R.id.layoutShorts);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Upload a Video is clicked",Toast.LENGTH_SHORT).show();

            }
        });

        shortsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Create a short is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}