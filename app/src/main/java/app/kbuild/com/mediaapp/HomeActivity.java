package app.kbuild.com.mediaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import app.kbuild.com.mediaapp.services.MyLocationService;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE = 200;
    private DatabaseReference databaseReference;
    private  DatabaseReference userDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CircleImageView profile_image;
    private FirebaseUser user;
    FloatingActionButton floatingActionButton;
    Double latitude,longitude;
    String address,area,locality;
    Geocoder geocoder;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();



        fn_permission();


        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab_btn);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (boolean_permission) {

                    if (mPref.getString("service", "").matches("")) {
                        medit.putString("service", "service").commit();

                        Intent intent = new Intent(getApplicationContext(), MyLocationService.class);
                        startService(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();

                        try{
                            unregisterReceiver(broadcastReceiver);
                            Intent intent = new Intent(getApplicationContext(), MyLocationService.class);
                            startService(intent);
                        }catch (Exception x){

                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
                }


            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        firebaseAuth=FirebaseAuth.getInstance();
         user = firebaseAuth.getCurrentUser();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("MediaApps");
        userDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());


        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                View view = navigationView.getHeaderView(0);
                // find MenuItem you want to change
                TextView t1 =(TextView) view.findViewById(R.id.text_user_name);
                TextView t2 =(TextView) view.findViewById(R.id.text_user_email);
                profile_image =(CircleImageView) view.findViewById(R.id.profile_image);


                DatabaseReference userdb=databaseReference.child(user.getUid());
                String firstname=dataSnapshot.child("firstname").getValue().toString();
                String lastname=dataSnapshot.child("lastname").getValue().toString();
                t1.setText(firstname+" "+lastname);
                t2.setText(user.getEmail());
                Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getValue().toString()).into(profile_image);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









        authStateListener=new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Toast.makeText(getApplicationContext(),user.getDisplayName(),Toast.LENGTH_SHORT).show();

                if(user==null){
                    Intent loginIntent=new Intent(HomeActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    Toast.makeText(getApplicationContext(),user.getDisplayName(),Toast.LENGTH_SHORT).show();

                    finish();
                }
                else{

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    // get menu from navigationView
                    View view = navigationView.getHeaderView(1);
                    // find MenuItem you want to change
                    TextView t1 =(TextView) view.findViewById(R.id.text_user_name);

                  t1.setText("test");

                }

            }
        };
    }


    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;
        }
    }






    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else  if(id==R.id.logout){
            firebaseAuth.signOut();
            Intent loginIntent=new Intent(HomeActivity.this,LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                    Toast.makeText(getApplicationContext(), "Permitted",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          //  Toast.makeText(getApplicationContext(), "On received method",Toast.LENGTH_SHORT).show();

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude,2);
                String cityName = addresses.get(1).getAddressLine(0);
                String stateName = addresses.get(1).getAddressLine(1);
                String countryName = addresses.get(1).getAddressLine(2);

                area=addresses.get(1).getAdminArea();
                locality=cityName;
                address=stateName;
                StringBuilder builder=new StringBuilder();
                builder.append("Latitude:"+latitude);
                builder.append("Longitude:"+longitude);

                builder.append(locality);

                Toast.makeText(getApplicationContext(),builder.toString(),Toast.LENGTH_LONG).show();



            } catch (IOException e1) {
                e1.printStackTrace();
                Toast.makeText(getApplicationContext(), e1.getMessage(),Toast.LENGTH_SHORT).show();
            }





        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        try{
            registerReceiver(broadcastReceiver, new IntentFilter(MyLocationService.str_receiver));
        }catch (Exception x){

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            unregisterReceiver(broadcastReceiver);
        }catch (Exception x){

        }


    }
}
