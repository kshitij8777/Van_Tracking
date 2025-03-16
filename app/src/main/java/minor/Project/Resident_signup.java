package minor.Project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Resident_signup extends AppCompatActivity {
    EditText username,email,phone,address,password;
    Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resident_signup);

        username=findViewById(R.id.resident_username);
        email=findViewById(R.id.resdientEmailAddress);
        phone=findViewById(R.id.residentPhone);
        address=findViewById(R.id.residentPostalAddress);
        password=findViewById(R.id.residentPassword);
        signupBtn=findViewById(R.id.residentSignUp);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupUser();
            }
        });

    }

    private void signupUser(){
        String usernameText=username.getText().toString().trim();
        String emailText=email.getText().toString().trim();
        String phoneText=phone.getText().toString().trim();
        String addressText=address.getText().toString().trim();
        String passwordText=password.getText().toString().trim();

        if (usernameText.isEmpty() || emailText.isEmpty() || phoneText.isEmpty() ||
                addressText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser user= new ParseUser();
        user.setUsername(usernameText);
        user.setPassword(passwordText);
        user.setEmail(emailText);

        user.put("phone",phoneText);
        user.put("address",addressText);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Toast.makeText(Resident_signup.this,"SignUP Successful",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(Resident_signup.this, "Signup Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}