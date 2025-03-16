package minor.Project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.parse.ParseUser;

public class EditDetails extends AppCompatActivity {

     EditText userName,email,address,phone;
     Button Edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_details);

        userName=findViewById(R.id.editTextUsername);
        email=findViewById(R.id.editTextEmail);
        address=findViewById(R.id.editTextAddress);
        phone=findViewById(R.id.editTextPhone);
        Edit=findViewById(R.id.btnEdit);

        fetchCurrentUserData();

        Edit.setOnClickListener(v->saveUserDetails());


    }

    private void fetchCurrentUserData(){
        ParseUser currentUser=ParseUser.getCurrentUser();

        if(currentUser!=null){
            userName.setText(currentUser.getUsername());
            email.setText(currentUser.getEmail());
            phone.setText(currentUser.getString("phone")); // Custom field: Phone
            address.setText(currentUser.getString("address")); // Custom field: Address
        } else {
            // Handle the case where no user is logged in
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        }
    }

    private void saveUserDetails() {
        // Get the current user
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            // Update only the fields that are not empty
            if (!userName.getText().toString().isEmpty()) {
                currentUser.setUsername(userName.getText().toString());
            }
            if (!email.getText().toString().isEmpty()) {
                currentUser.setEmail(email.getText().toString());
            }
            if (!phone.getText().toString().isEmpty()) {
                currentUser.put("phone", phone.getText().toString()); // Custom field: Phone
            }
            if (!address.getText().toString().isEmpty()) {
                currentUser.put("address", address.getText().toString()); // Custom field: Address
            }

            // Save the updated user details to Back4App
            currentUser.saveInBackground(e -> {
                if (e == null) {
                    // Successfully updated
                    Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity and return to the previous screen
                } else {
                    // Failed to update
                    Toast.makeText(this, "Failed to update details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where no user is logged in
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
