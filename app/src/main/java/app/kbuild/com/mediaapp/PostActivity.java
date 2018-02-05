package app.kbuild.com.mediaapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final int REQUEST_CODE=100;
    private Uri uri=null;
    ImageButton imageButton;
    EditText fullnameEditText;
    EditText descriptionEditText;
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        fullnameEditText=(EditText)findViewById(R.id.fullname);
        descriptionEditText=(EditText)findViewById(R.id.description);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("MediaApps");
    }

    public void imageButtonClicked(View view){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE);
    }

    public void submitButtonClicked(View view){
      final String fullname=fullnameEditText.getText().toString();
        final String description=descriptionEditText.getText().toString();

        if(!TextUtils.isEmpty(fullname)  && !TextUtils.isEmpty(description)){
            Toast.makeText(getApplicationContext(),"Upload Started",Toast.LENGTH_SHORT).show();

            StorageReference filePath=mStorageRef.child("PostImage").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl=taskSnapshot.getDownloadUrl();
                   Toast.makeText(getApplicationContext(),"Upload complete",Toast.LENGTH_SHORT).show();

                    DatabaseReference newPost=databaseReference.push();
                    newPost.child("title").setValue(fullname);
                    newPost.child("desc").setValue(description);
                    newPost.child("image").setValue(downloadUrl.toString());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"Name or Description cannot be empty",Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            uri=data.getData();
            imageButton=(ImageButton)findViewById(R.id.imageButton);
            imageButton.setImageURI(uri);
        }
    }
}
