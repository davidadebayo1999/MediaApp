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
    private Button sign_in_button;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

       setUI();

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");


        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent= new Intent(RegisterActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }


    public void setUI(){




        firstnameTextField=(EditText)findViewById(R.id.firstnameTextField);
        lastnameTextField=(EditText)findViewById(R.id.lastnameTextField);
        phoneTextField=(EditText)findViewById(R.id.phoneTextField);
        emailTextField=(EditText)findViewById(R.id.emailTextField);
        passwordTextField=(EditText)findViewById(R.id.passwordTextField);

        sign_up_button=(Button)findViewById(R.id.sign_up_button);
        sign_in_button=(Button)findViewById(R.id.sign_in_button);



        final TextInputLayout firstnameTextInputLayout=(TextInputLayout)findViewById(R.id.firstnameTextInputLayout);
        final TextInputLayout lastnameTextInputLayout=(TextInputLayout)findViewById(R.id.lastnameTextInputLayout);
        final TextInputLayout phoneTextInputLayout=(TextInputLayout)findViewById(R.id.phoneTextInputLayout);
        final TextInputLayout emailTextInputLayout=(TextInputLayout)findViewById(R.id.emailTextInputLayout);
        final TextInputLayout passwordTextInputLayout=(TextInputLayout)findViewById(R.id.passwordTextInputLayout);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);



        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class ));
            }
        });




        //Email

        emailTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(emailTextField.getText().toString().isEmpty()){
                    emailTextInputLayout.setErrorEnabled(true);
                    emailTextInputLayout.setError("Email cannot be empty!");
                }else{
                    emailTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

       emailTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(emailTextField.getText().toString().isEmpty()){
                    emailTextInputLayout.setErrorEnabled(true);
                    emailTextInputLayout.setError("Email cannot be empty!");
                }else{
                    emailTextInputLayout.setErrorEnabled(false);
                }
            }
        });


        //Password

        passwordTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordTextField.getText().toString().isEmpty()){
                    passwordTextInputLayout.setErrorEnabled(true);
                    passwordTextInputLayout.setError("Password cannot be empty!");
                }else{
                    passwordTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

      passwordTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(passwordTextField.getText().toString().isEmpty()){
                    passwordTextInputLayout.setErrorEnabled(true);
                    passwordTextInputLayout.setError("Password cannot be empty!");
                }else{
                    passwordTextInputLayout.setErrorEnabled(false);
                }
            }
        });


        passwordTextInputLayout.setPasswordVisibilityToggleEnabled(true);
        passwordTextInputLayout.setCounterEnabled(true);
        passwordTextInputLayout.setCounterMaxLength(8);




    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }


    public void registerButtonClicked(View view){


        final String email=emailTextField.getText().toString();
        String password=passwordTextField.getText().toString();


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
                     current_user_db.child("image").setValue("avatar");
                     current_user_db.child("firstname").setValue("default");
                     current_user_db.child("lastname").setValue("default");
                     current_user_db.child("phone").setValue("default");
                     progressBar.setVisibility(View.GONE);

                     Intent intent=new Intent(RegisterActivity.this,SetupActivity.class);
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
