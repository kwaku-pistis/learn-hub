package deemiensa.com.learnhub.Activities;

import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.EditText;

import deemiensa.com.learnhub.R;

public class Feedback extends AppCompatActivity {

    CardView send;
    EditText feedback;
    EditText report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        {

            send = (CardView) findViewById(R.id.Button1);
            report = (EditText) findViewById(R.id.EditText01);

            send.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"app.learnhub@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report a problem: LearnHub app - version 1.0.1");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, report.getText());
                    //  Feedback.this.startActivity(Intent.createChooser(emailIntent, "Send email using"));
                    try {
                        Feedback.this.startActivity(Intent.createChooser(emailIntent, "Send email using"));

                    } catch (android.content.ActivityNotFoundException ex) {
                        Snackbar.make(v, "No email clients available.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }


                }

            });
        }

        {

            send = (CardView) findViewById(R.id.Button2);
            feedback = (EditText) findViewById(R.id.editText3);

            send.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"app.learnhub@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: LearnHub app - version 1.0.1");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, feedback.getText());
                    //  Feedback.this.startActivity(Intent.createChooser(emailIntent, "Send email using"));
                    try {
                        Feedback.this.startActivity(Intent.createChooser(emailIntent, "Send email using"));

                    } catch (android.content.ActivityNotFoundException ex) {
                        Snackbar.make(v, "No email clients available.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }


                }

            });
        }
    }
}
