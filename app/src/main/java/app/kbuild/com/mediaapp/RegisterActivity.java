package app.kbuild.com.mediaapp;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

import static android.R.attr.typeface;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstnameTextField;
    private EditText lastnameTextField;
    private EditText phoneTextField;
    private EditText emailTextField;
    private  EditText passwordTextField;

    private Button sign_up_button;
    private Button btn_reset_password;
    private Button sign_in_button;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private  Typeface typeface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

       setUI();

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");


        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent= new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


    public void setUI(){

        AssetManager am = getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am,String.format(Locale.US, "fonts/%s", "hv.ttf"));


        firstnameTextField=(EditText)findViewById(R.id.firstnameTextField);
        firstnameTextField.setTypeface(typeface);
        lastnameTextField=(EditText)findViewById(R.id.lastnameTextField);
        lastnameTextField.setTypeface(typeface);
        phoneTextField=(EditText)findViewById(R.id.phoneTextField);
        phoneTextField.setTypeface(typeface);
        emailTextField=(EditText)findViewById(R.id.emailTextField);
        emailTextField.setTypeface(typeface);
        passwordTextField=(EditText)findViewById(R.id.passwordTextField);
        passwordTextField.setTypeface(typeface);

        sign_up_button=(Button)findViewById(R.id.sign_up_button);
        sign_up_button.setTypeface(typeface);
        btn_reset_password=(Button)findViewById(R.id.btn_reset_password);
        btn_reset_password.setTypeface(typeface);
        sign_in_button=(Button)findViewById(R.id.sign_in_button);
        sign_in_button.setTypeface(typeface);

        final TextInputLayout firstnameTextInputLayout=(TextInputLayout)findViewById(R.id.firstnameTextInputLayout);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, ResetPasswordActivity.class));
            }
        });

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class ));
            }
        });

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


    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }


    public void registerButtonClicked(View view){

       final String firstname=firstnameTextField.getText().toString();
        final String lastname=lastnameTextField.getText().toString();
        final String phone=phoneTextField.getText().toString();
        final String email=emailTextField.getText().toString();
        String password=passwordTextField.getText().toString();

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

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


          firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){

                     String user_id= firebaseAuth.getCurrentUser().getUid();
                     DatabaseReference current_user_db= databaseReference.child(user_id);
                     current_user_db.child("firstname").setValue(firstname);
                     current_user_db.child("lastname").setValue(lastname);
                     current_user_db.child("phone").setValue(phone);
                     current_user_db.child("image").setValue("default");
                     progressBar.setVisibility(View.GONE);

                     Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);
                     finish();

                 }else{
                     task.addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             progressBar.setVisibility(View.GONE);
                             Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                         }
                     });

                 }

              }
          });



    }




}
