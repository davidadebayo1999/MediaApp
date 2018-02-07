package app.kbuild.com.mediaapp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText firstnameTextField;
    private EditText lastnameTextField;
    private EditText phoneTextField;


    private Button update_profile;
    private ProgressBar progressBar;

    private static final int REQUEST_CODE = 200;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ImageView profile_img;
    private Uri imageUri=null;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference().child("profile_image");
        setUI();
    }

    public void onProfileImageClicked(View view){
        Intent galleryIntent= new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUEST_CODE);
    }

    public void registerButtonClicked(View view){

        final String firstname=firstnameTextField.getText().toString();
        final String lastname=lastnameTextField.getText().toString();
        final String phone=phoneTextField.getText().toString();



        if (TextUtils.isEmpty(firstname)) {
            Toast.makeText(getApplicationContext(), "Enter your firstname!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(lastname)) {
            Toast.makeText(getApplicationContext(), "Enter your lastname!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getApplicationContext(), "Enter your phone number!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(imageUri==null){
            Toast.makeText(getApplicationContext(), "Upload profile image", Toast.LENGTH_SHORT).show();
            return;
        }



        progressBar.setVisibility(View.VISIBLE);

        String user_id=firebaseAuth.getCurrentUser().getUid();


        final DatabaseReference current_user_db= databaseReference.child(user_id);
        StorageReference filepath=storageReference.child(imageUri.getLastPathSegment());
        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl=taskSnapshot.getDownloadUrl().toString();
                current_user_db.child("firstname").setValue(firstname);
                current_user_db.child("lastname").setValue(lastname);
                current_user_db.child("phone").setValue(phone);
                current_user_db.child("image").setValue(downloadUrl);

                progressBar.setVisibility(View.GONE);

                Intent intent=new Intent(SetupActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });





    }


    public void setUI(){

        firstnameTextField=(EditText)findViewById(R.id.firstnameTextField);
        lastnameTextField=(EditText)findViewById(R.id.lastnameTextField);
        phoneTextField=(EditText)findViewById(R.id.phoneTextField);

        update_profile=(Button)findViewById(R.id.update_profile);
        profile_img=(ImageView) findViewById(R.id.profile_img);

        final TextInputLayout firstnameTextInputLayout=(TextInputLayout)findViewById(R.id.firstnameTextInputLayout);
        final TextInputLayout lastnameTextInputLayout=(TextInputLayout)findViewById(R.id.lastnameTextInputLayout);
        final TextInputLayout phoneTextInputLayout=(TextInputLayout)findViewById(R.id.phoneTextInputLayout);


        progressBar=(ProgressBar)findViewById(R.id.progressBar200);




        ////  Firstname

        firstnameTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(firstnameTextField.getText().toString().isEmpty()){
                    firstnameTextInputLayout.setErrorEnabled(true);
                    firstnameTextInputLayout.setError("Firstname cannot be empty!");
                }else{
                    firstnameTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        firstnameTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(firstnameTextField.getText().toString().isEmpty()){
                    firstnameTextInputLayout.setErrorEnabled(true);
                    firstnameTextInputLayout.setError("Username cannot be empty!");
                }else{
                    firstnameTextInputLayout.setErrorEnabled(false);
                }
            }
        });


        //Lastname

        lastnameTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(lastnameTextField.getText().toString().isEmpty()){
                    lastnameTextInputLayout.setErrorEnabled(true);
                    lastnameTextInputLayout.setError("Lastname cannot be empty!");
                }else{
                    lastnameTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lastnameTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(lastnameTextField.getText().toString().isEmpty()){
                    lastnameTextInputLayout.setErrorEnabled(true);
                    lastnameTextInputLayout.setError("Lastname cannot be empty!");
                }else{
                    lastnameTextInputLayout.setErrorEnabled(false);
                }
            }
        });



        //Phone

        phoneTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(phoneTextField.getText().toString().isEmpty()){
                    phoneTextInputLayout.setErrorEnabled(true);
                    phoneTextInputLayout.setError("Phone cannot be empty!");
                }else{
                    phoneTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        phoneTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(phoneTextField.getText().toString().isEmpty()){
                    phoneTextInputLayout.setErrorEnabled(true);
                    phoneTextInputLayout.setError("Phone cannot be empty!");
                }else{
                    phoneTextInputLayout.setErrorEnabled(false);
                }
            }
        });










    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK) {



                Uri imageUri=data.getData();
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult  result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                imageUri=result.getUri();
                profile_img.setImageURI(imageUri);

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }
}


