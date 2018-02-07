package app.kbuild.com.mediaapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String TAG =MainActivity.class.getSimpleName() ;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView=(RecyclerView)findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference= FirebaseDatabase.getInstance().getReference().child("MediaApps");
        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Toast.makeText(getApplicationContext(),user.getDisplayName(),Toast.LENGTH_SHORT).show();

                if(user==null){
                    Intent loginIntent=new Intent(MainActivity.this,RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    Toast.makeText(getApplicationContext(),user.getDisplayName(),Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onAuthStateChanged:signed_out:" );
                    finish();
                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_in"+user.getUid());
                }

            }
        };




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Insta,InstaViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Insta, InstaViewHolder>(
                Insta.class,
                R.layout.app_row,
                InstaViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(InstaViewHolder viewHolder, Insta model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public  static class InstaViewHolder extends  RecyclerView.ViewHolder{
        View view=null;
        public InstaViewHolder(View itemView) {
            super(itemView);
             view=itemView;
        }

        public  void setTitle(String title){
            TextView post_title=(TextView)view.findViewById(R.id.textTitle);
            post_title.setText(title);
        }

        public  void setDesc(String desc){
            TextView  post_desc=(TextView)view.findViewById(R.id.textDesc);
            post_desc.setText(desc);
        }

        public void setImage(Context context, String image){
            ImageView imageView=(ImageView) view.findViewById(R.id.post_image);
            Picasso.with(context).load(image).into(imageView);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        else  if(id==R.id.addIcon){
            Intent intent= new Intent(MainActivity.this,PostActivity.class);
            startActivity(intent);
        }
        else  if(id==R.id.logout){
            firebaseAuth.signOut();
            Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
