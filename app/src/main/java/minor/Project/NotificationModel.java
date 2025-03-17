package minor.Project;

public class NotificationModel {
    private String title;
    private String details;
    private boolean expanded;

    public NotificationModel(String title, String details) {
        this.title = title;
        this.details = details;
        this.expanded = false;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
