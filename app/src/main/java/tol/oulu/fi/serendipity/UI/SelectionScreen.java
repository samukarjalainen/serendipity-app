package tol.oulu.fi.serendipity.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.R;
import tol.oulu.fi.serendipity.SerendipityService;
import tol.oulu.fi.serendipity.Server.Login;
import tol.oulu.fi.serendipity.Server.SoundDownloader;

/**
 * Created by IoLiving on 04/04/2016.
 */
public class SelectionScreen extends Activity {
    private static String TAG = "Serendipity-SelectionScreen";
    private Button record;
    private Button locate;
    private DataHandler mDataHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_screen);
        mDataHandler = DataHandler.getInstance(this);

        locate =(Button)findViewById(R.id.button);
        record =(Button)findViewById(R.id.button2);
       locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentForService = new Intent(SelectionScreen.this, SerendipityService.class);
                intentForService.setAction(Intent.ACTION_ANSWER);
                startService(intentForService);
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent recordIntent = new Intent(SelectionScreen.this, RecordScreen.class);
                SelectionScreen.this.startActivity(recordIntent);
            }
        });



    }



}
