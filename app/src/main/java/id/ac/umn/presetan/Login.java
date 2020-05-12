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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
//    public TextView tvLogin;
    public EditText etEmail, etPassword;
    public Button btnLogin;
    FirebaseAuth fAuth;
    ProgressBar progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        fAuth = FirebaseAuth.getInstance();
        progressBar2 = findViewById(R.id.progressBar);
        progressBar2.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar2.setVisibility(View.VISIBLE);
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    etEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    etPassword.setError("Password is Required");
                    return;
                }

                if(password.length() < 6){
                    etPassword.setError("Password Must be >= 6 characters");
                    return;
                }

                //progressBar2.setVisibility(View.VISIBLE);

                //autentikasi user

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar2.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                            Intent passUser = new Intent(getApplicationContext(), UserActivity.class);
//                            passUser.putExtra("username", )
                            Global.login = 1;
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                            finish();
                        }
                        else{
                            progressBar2.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
