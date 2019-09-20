package deemiensa.com.learnhub.Classes;

public class HomeVideo {
    private String Title, Desc, Category, VideoUrl, ProfileImage, Name, UserID, Thumbnail, Time, PostTime, Institution, PublishedDate;
    private int Duration;

    public HomeVideo(){
        //empty public constructor
    }

    public HomeVideo(String title, String desc, String category, String videoUrl, String profileImage, String name, String userID,
                     int duration, String thumbnail, String time, String postTime, String institution, String publishedDate){
        this.Title = title;
        this.Desc = desc;
        this.Category = category;
        this.VideoUrl = videoUrl;
        this.Name = name;
        this.ProfileImage = profileImage;
        this.UserID = userID;
        this.Duration = duration;
        this.Thumbnail = thumbnail;
        this.Time = time;
        this.PostTime = postTime;
        this.Institution = institution;
        this.PublishedDate = publishedDate;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPostTime() {
        return PostTime;
    }

    public void setPostTime(String postTime) {
        PostTime = postTime;
    }

    public String getInstitution() {
        return Institution;
    }

    public void setInstitution(String institution) {
        Institution = institution;
    }

    public String getPublishedDate() {
        return PublishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        PublishedDate = publishedDate;
    }
}
