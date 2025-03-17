package minor.Project;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import minor.Project.databinding.ActivityNotificationBinding;

public class Notification extends AppCompatActivity {
    ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                Intent homeintent = new Intent(Notification.this, User_Dashboard.class);
                startActivity(homeintent);
                finish();
            } else if (itemId == R.id.profile) {
                Intent profileIntent = new Intent(Notification.this, User_Profile.class);
                startActivity(profileIntent);
                finish();
            } else if (itemId == R.id.notification) {
            } else if (itemId == R.id.settings) {
                Intent settingsIntent = new Intent(Notification.this, Settings.class);
                startActivity(settingsIntent);
                finish();
            }

            return true;
        });
        binding.bottomNavigationView.setSelectedItemId(R.id.notification);

    }
    }
