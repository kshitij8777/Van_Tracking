package minor.Project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class DriverDashboard extends AppCompatActivity {
    Button wet,dry;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_dashboard);

        wet=findViewById(R.id.wetButton);
        dry=findViewById(R.id.dryWaste);

        wet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWasteType("Wet Waste");
                Intent wetIntent=new Intent(DriverDashboard.this,startScreen.class);
                startActivity(wetIntent);
                finish();
            }
        });

        dry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWasteType("Dry Waste");
                Intent dryIntent=new Intent(DriverDashboard.this,startScreen.class);
                startActivity(dryIntent);
                finish();
            }
        });

    }

    private void saveWasteType(String wasteType){
        ParseUser currentUser=ParseUser.getCurrentUser();
        if(currentUser!=null){
            String driverId=currentUser.getObjectId();
            String driverName=currentUser.getString("username");

            ParseObject waste=new ParseObject("WasteType");
            waste.put("type",wasteType);
            waste.put("driverId",driverId);
            waste.put("driverName",driverName);

            waste.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        Toast.makeText(DriverDashboard.this, "WasteType Saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DriverDashboard.this,"Failed to save:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(DriverDashboard.this,"No Driver is Logged in",Toast.LENGTH_SHORT).show();
        }
    }
}