package minor.Project;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

import minor.Project.databinding.ActivityUserProfileBinding;

public class User_Profile extends AppCompatActivity {
    ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchUserData();


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                Intent homeIntent = new Intent(User_Profile.this, User_Dashboard.class);
                startActivity(homeIntent);
                finish(); // Close current activity to avoid back stack clutter
            } else if (itemId == R.id.profile) {
                // Already on User_Profile activity, no action needed
            } else if (itemId == R.id.notification) {
                Intent notificationIntent = new Intent(User_Profile.this, Notification.class);
                startActivity(notificationIntent);
                finish();
            } else if (itemId == R.id.settings) {
                Intent settingsIntent = new Intent(User_Profile.this, Settings.class);
                startActivity(settingsIntent);
                finish();
            }

            return true;
        });


        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
    }

    private void fetchUserData(){
        ParseUser currentUser=ParseUser.getCurrentUser();

        if(currentUser!=null){
            String username=currentUser.getUsername();
            String email=currentUser.getEmail();
            String phone=currentUser.getString("phone");
            String address=currentUser.getString("address");

            binding.usernameValue.setText(username);
            binding.emailValue.setText(email);
            binding.phoneValue.setText(phone);
            binding.addressValue.setText(address);
        }else{
            Intent loginIntent= new Intent(User_Profile.this,Resident_login.class);
            startActivity(loginIntent);
            finish();
        }
    }
}
