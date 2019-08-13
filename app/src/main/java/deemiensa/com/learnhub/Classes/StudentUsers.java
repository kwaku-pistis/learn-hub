package deemiensa.com.learnhub.Classes;

public class StudentUsers
{
    public String Name;
    public String Programme;
    public String ProfileImageUrl;

    public StudentUsers() {}

    public StudentUsers(String name, String programme, String profileImageUrl)
    {
        this.Name = name;
        this.Programme = programme;
        this.ProfileImageUrl = profileImageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProgramme() {
        return Programme;
    }

    public void setProgramme(String programme) {
        Programme = programme;
    }

    public String getProfileImageUrl() {
        return ProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        ProfileImageUrl = profileImageUrl;
    }
}
