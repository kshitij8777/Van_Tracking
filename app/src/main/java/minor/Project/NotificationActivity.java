package minor.Project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        notificationList = new ArrayList<>();
        notificationList.add(new NotificationModel("Driver Nearby", "The driver is within 500 meters of your home."));
        notificationList.add(new NotificationModel("Driver Arrived", "The driver has arrived at your location."));
        notificationList.add(new NotificationModel("Driver Leaving", "The driver is leaving the pickup point."));

        adapter = new NotificationAdapter(notificationList, this);
        recyclerView.setAdapter(adapter);
    }
}
