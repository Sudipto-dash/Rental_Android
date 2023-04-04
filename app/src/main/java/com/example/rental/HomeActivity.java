package com.example.rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rental.fragments.AboutusFragment;
import com.example.rental.fragments.HomeFragment;
import com.example.rental.fragments.MyPostsFragment;
import com.example.rental.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private FirebaseAuth mFirebase;


    ImageView imageMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loadFragment(new HomeFragment());
        mFirebase = FirebaseAuth.getInstance();

        // Navagation Drawar------------------------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_View);
        imageMenu = findViewById(R.id.imageMenu);


        toggle = new ActionBarDrawerToggle(HomeActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawar click event
        // Drawer item Click event ------

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //Fragment fragment=null;
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.mProfile:
                        Toast.makeText(HomeActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        loadFragment(new ProfileFragment());
                        //drawerLayout.closeDrawers();
                        break;
                    case R.id.mMyposts:
                        Toast.makeText(HomeActivity.this, "Your Posts", Toast.LENGTH_SHORT).show();
                        loadFragment(new MyPostsFragment());
                        //drawerLayout.closeDrawers();
                        break;

                    case R.id.mHome:
                        Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        loadFragment(new HomeFragment());
                        //drawerLayout.closeDrawers();
                        break;
                    case R.id.mAboutUs:
                        Toast.makeText(HomeActivity.this, "About Us!", Toast.LENGTH_SHORT).show();
                        loadFragment(new AboutusFragment());
                        //drawerLayout.closeDrawers();
                        break;
                    case R.id.mRate:
                        Toast.makeText(HomeActivity.this, "Rate app!", Toast.LENGTH_SHORT).show();
                        //drawerLayout.closeDrawers();
                        break;
                    case R.id.mLogOut:
                        mFirebase.signOut();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                        Toast.makeText(HomeActivity.this, "Log out successful", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        return true;

                }

                return true;
            }


        });
        //------------------------------

        // ------------------------
        // App Bar Click Event
        imageMenu = findViewById(R.id.imageMenu);

        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code Here
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });




    } // OnCreate Method Close here ==============

    private void loadFragment(Fragment fragment){

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,fragment).commit();


    }
}