package deemiensa.com.learnhub.Activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import deemiensa.com.learnhub.R;

import static deemiensa.com.learnhub.Utils.SharedPref.saveDept;

public class Departments extends AppCompatActivity {

    private RelativeLayout mCOSrel, mBioSciRel, mPhySciRel;
    private LinearLayout CosFaculties, mBioSciDepts, mPhySciDepts;
    private ImageView mCosArrow, mBioSciArrow, mPhySciArrow;
    private TextView mBioChem, mEnvSci, mFoodSci, mOptometry, mBiology, mChem, mCompSci, mMaths, mPhy, mStats, mActSci;
    private String dept_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departments);

        //colleges
        mCOSrel = findViewById(R.id.cos_rel);

        //faculties
        mBioSciRel = findViewById(R.id.bio_sci_rel);
        mPhySciRel = findViewById(R.id.phy_sci_rel);
        CosFaculties = findViewById(R.id.cos_faculties);

        //Departments layouts
        mBioSciDepts = findViewById(R.id.bio_sci_dept);
        mPhySciDepts = findViewById(R.id.phy_sci_dept);

        //arrows
        mCosArrow = findViewById(R.id.cos_arrow);
        mBioSciArrow = findViewById(R.id.bio_sci_arrow);
        mPhySciArrow = findViewById(R.id.phy_sci_arrow);

        //Depts textviews
        mBioChem = findViewById(R.id.bio_chem);
        mEnvSci = findViewById(R.id.env_sci);
        mFoodSci = findViewById(R.id.food_sci);
        mOptometry =findViewById(R.id.optometry);
        mBiology = findViewById(R.id.app_bio);
        mChem = findViewById(R.id.chem);
        mCompSci = findViewById(R.id.comp_sci);
        mMaths = findViewById(R.id.maths);
        mPhy = findViewById(R.id.physics);
        mStats = findViewById(R.id.stats);
        mActSci = findViewById(R.id.act_sci);

        mCOSrel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("Button Click", MODE_PRIVATE);
                boolean activated = pref.getBoolean("clicked", false);
                if(!activated) {
                    // User hasn't clicked the button yet
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("clicked", true);
                    editor.apply();

                    CosFaculties.setVisibility(View.VISIBLE);
                    mCosArrow.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                } else {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("clicked", false);
                    editor.apply();

                    CosFaculties.setVisibility(View.GONE);
                    mCosArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                }
            }
        });

        mBioSciRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("Button Click", MODE_PRIVATE);
                boolean activated = pref.getBoolean("Bio Sci clicked", false);
                if(!activated) {
                    // User hasn't clicked the button yet
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("Bio Sci clicked", true);
                    editor.apply();

                    mBioSciDepts.setVisibility(View.VISIBLE);
                    mBioSciArrow.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                } else {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("Bio Sci clicked", false);
                    editor.apply();

                    mBioSciDepts.setVisibility(View.GONE);
                    mBioSciArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                }
            }
        });

        mPhySciRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("Button Click", MODE_PRIVATE);
                boolean activated = pref.getBoolean("Phy Sci clicked", false);
                if(!activated) {
                    // User hasn't clicked the button yet
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("Phy Sci clicked", true);
                    editor.apply();

                    mPhySciDepts.setVisibility(View.VISIBLE);
                    mPhySciArrow.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                } else {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("Phy Sci clicked", false);
                    editor.apply();

                    mPhySciDepts.setVisibility(View.GONE);
                    mPhySciArrow.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                }
            }
        });

        mBioChem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mBioChem.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mEnvSci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mEnvSci.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mFoodSci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mFoodSci.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mOptometry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mOptometry.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mBiology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mBiology.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mChem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mChem.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mCompSci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mCompSci.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mMaths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mMaths.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mPhy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mPhy.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mStats.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });

        mActSci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dept_string = mActSci.getText().toString();
                saveDept(dept_string);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dept, menu);
        return true; //super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {

        }

        return super.onOptionsItemSelected(item);
    }
}
