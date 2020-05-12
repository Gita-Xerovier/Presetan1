package id.ac.umn.presetan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    public EditText etSignPassword, etEmail;
    public Button btnSignUp;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.etEmail);
        etSignPassword = findViewById(R.id.etSignPassword);
        btnSignUp = findViewById(R.id.btnSignUp);


        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

//        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), SignUp.class));
//            finish();
//        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = etEmail.getText().toString().trim();
                String password = etSignPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    etEmail.setError("Email is Required");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    etSignPassword.setError("Password is Required");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if(password.length() < 6){
                    etSignPassword.setError("Password Must be >= 6 characters");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }


                //register user ke firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();
//                            Intent passUser = new Intent(getApplicationContext(), UserActivity.class);
//                            passUser.putExtra("username", etSignUsername.getText());
//                            startActivity(passUser);
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                            finish();
                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUp.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }

    public void cancel(View view) {
        startActivity(new Intent(getApplicationContext(), GuestProfile.class));
        finish();
    }
}
