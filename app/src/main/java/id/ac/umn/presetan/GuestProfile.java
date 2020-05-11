package id.ac.umn.presetan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GuestProfile extends AppCompatActivity {
    public TextView click;
    public Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent signUp = new Intent(GuestProfile.this, SignUp.class);
                startActivity(signUp);
            }
        });

        click = findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent signIn = new Intent(GuestProfile.this, Login.class);
                startActivity(signIn);
            }
        });
    }
}
