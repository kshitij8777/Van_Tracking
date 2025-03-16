package minor.Project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class Resident_login extends AppCompatActivity {
    EditText username,password;
    Button Login,SignUp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resident_login);

        username=findViewById(R.id.resident_username_login);
        password=findViewById(R.id.resident_password);

        Login=findViewById(R.id.user_login);
        SignUp=findViewById(R.id.user_signup);
        if (username == null) Log.e("LoginError", "Username EditText is null");
        if (password == null) Log.e("LoginError", "Password EditText is null");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Resident_login.this,Resident_signup.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUser(){
        String usernameText=username.getText().toString().trim();
        String passwordText=password.getText().toString().trim();

        if (usernameText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        ParseUser.logInInBackground(usernameText, passwordText, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    // Login successful
                    Toast.makeText(Resident_login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Resident_login.this, User_Dashboard.class);
                    startActivity(intent);
                    finish(); // Close login screen
                } else {
                    // Login failed
                    Toast.makeText(Resident_login.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    }
