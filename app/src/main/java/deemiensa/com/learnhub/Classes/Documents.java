package deemiensa.com.learnhub.Classes;

public class Documents {
    private String Title, Name, Time, DownloadUrl, UserID, PostTime, Mimetype;

    public Documents(){
        // empty public constructor
    }

    public Documents(String title, String name, String time, String downloadUrl, String userID, String postTime, String mimetype){
        this.Title = title;
        this.Name = name;
        this.Time = time;
        this.DownloadUrl = downloadUrl;
        this.UserID = userID;
        this.PostTime = postTime;
        this.Mimetype = mimetype;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPostTime() {
        return PostTime;
    }

    public void setPostTime(String postTime) {
        PostTime = postTime;
    }

    public String getMimetype() {
        return Mimetype;
    }

    public void setMimetype(String mimetype) {
        Mimetype = mimetype;
    }
}
