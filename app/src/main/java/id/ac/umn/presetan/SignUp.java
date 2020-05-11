package id.ac.umn.presetan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    public EditText etSignUsername, etSignPassword, etEmail;
    public Button btnSignUp;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etSignUsername = findViewById(R.id.etSignUsername);
        etSignPassword = findViewById(R.id.etSignPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        etEmail = findViewById(R.id.etEmail);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), ChooseActivity.class));
            finish();
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etSignPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    etEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    etSignPassword.setError("Password is Required");
                    return;
                }

                if(password.length() < 6){
                    etSignPassword.setError("Password Must be >= 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //register user ke firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();
                           // startActivity(new Intent(getApplicationContext(), ChooseActivity.class));
                        }
                        else{
                            Toast.makeText(SignUp.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }
}
