package minor.Project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class Driver_login extends AppCompatActivity {

    private EditText driverUsername, driverPassword;
    private Button driverLogin, driverReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        // Initialize views
        driverUsername = findViewById(R.id.driverUsername);
        driverPassword = findViewById(R.id.driverPassword);
        driverLogin = findViewById(R.id.driverLogin);
        driverReport = findViewById(R.id.driverReport);


        driverLogin.setOnClickListener(v -> authenticateDriver());


        driverReport.setOnClickListener(v -> sendReport());
    }

    private void authenticateDriver() {
        String username = driverUsername.getText().toString();
        String password = driverPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Driver");
        query.whereEqualTo("username", username);
        query.whereEqualTo("password", password);

        query.getFirstInBackground((driver, e) -> {
            if (e == null && driver != null) {

                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(Driver_login.this, DriverDashboard.class);
                startActivity(intent);
                finish();
            } else {

                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendReport() {
        String username = driverUsername.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return;
        }


        ParseObject report = new ParseObject("Report");
        report.put("driverUsername", username);
        report.put("issueDescription", "Driver is facing issues logging in");
        report.put("status", "Pending");


        report.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    Toast.makeText(Driver_login.this, "Report sent to admin", Toast.LENGTH_SHORT).show();
                } else {
                    // Failed to save the report
                    Toast.makeText(Driver_login.this, "Failed to send report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}