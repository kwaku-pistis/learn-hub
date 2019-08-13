package deemiensa.com.learnhub.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private static final String MY_PREFS = "myPrefs" ;
    private static SharedPref mInstance;
    private static Context mCtx;

    private SharedPref(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPref getmInstance(Context context) {
        if(mInstance == null){
            mInstance = new SharedPref(context);
        }
        return mInstance;
    }

    public boolean updateFirstLogIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("isLoggedIn",String.valueOf(1));
        editor.apply();
        return true;
    }

    //To check if user logs in first time after installation
    public String isFirstLogIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        return sharedPreferences.getString("isLoggedIn", String.valueOf(0));
    }

    public static void clearSession() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static void saveProfile(String name, String profile_pic, String programme, String email, String ref){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("profile pic", profile_pic);
        editor.putString("programme", programme);
        editor.putString("email", email);
        editor.putString("ref", ref);
        editor.apply();
    }

    public static void saveLecturerProfile(String name, String profile_pic, String department, String specialty){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("profile pic", profile_pic);
        editor.putString("dept", department);
        editor.putString("specialty", specialty);
        editor.putString("isLecturer", "yes");
        editor.apply();
    }

    public static void saveDept(String selected_dept){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dept", selected_dept);
        editor.apply();
    }

    public static Boolean isLecturer(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        String out = sharedPreferences.getString("isLecturer", null);
        if (out == "yes"){
            return true;
        } else {
            return false;
        }
    }

    public static String getProfileName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", null);
    }

    public static String getProfilePic(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("profile pic", null);
    }

    public static String getProgramme(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("programme", null);
    }

    public static String getEmail(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", null);
    }

    public static String getRef(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("ref", null);
    }

    public static String getSpeciality(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("specialty", null);
    }

    public static String getSavedDept(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("dept", null);
    }

    public static void saveThumbnail(String thumb){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("thumbnail", thumb);
        editor.apply();
    }

    public static String getThumbnail(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("thumbnail", null);
    }
}
