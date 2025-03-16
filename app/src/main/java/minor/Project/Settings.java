package minor.Project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import minor.Project.databinding.ActivitySettingsBinding;

public class Settings extends AppCompatActivity {

    Button ChangePassword,EditDetails;
    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ChangePassword=findViewById(R.id.btnChangePassword);
        EditDetails=findViewById(R.id.btnEditDetails);

        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG","Change password Clicked Button");
                Intent intent=new Intent(Settings.this,ChangePassword.class);
                startActivity(intent);
                Log.d("DEBUG","Intent Triggered");
                finish();
            }
        });
        binding.btnEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DetailIntent=new Intent(Settings.this,EditDetails.class);
                startActivity(DetailIntent);
                finish();
            }
        });


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                Intent homeIntent = new Intent(Settings.this, User_Dashboard.class);
                startActivity(homeIntent);
                finish(); // Close current activity to avoid back stack clutter
            } else if (itemId == R.id.profile) {
                Intent profileIntent = new Intent(Settings.this, User_Profile.class);
                startActivity(profileIntent);
                finish();
            } else if (itemId == R.id.Notification) {
                Intent notificationIntent = new Intent(Settings.this, Notification.class);
                startActivity(notificationIntent);
                finish();
            } else if (itemId == R.id.settings) {

            }

            return true;
        });


        binding.bottomNavigationView.setSelectedItemId(R.id.settings);
    }
}
