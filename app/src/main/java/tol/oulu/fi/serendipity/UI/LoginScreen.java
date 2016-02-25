package tol.oulu.fi.serendipity.UI;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import tol.oulu.fi.serendipity.Data.DataHandler;
import tol.oulu.fi.serendipity.R;
import tol.oulu.fi.serendipity.Server.Login;

/**
 * Created by ashrafuzzaman on 10/02/2016.
 */
public class LoginScreen  extends Activity  {

    private String user= "";
    private String pass = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        final DataHandler mDataHandler = DataHandler.getInstance(this);
        final EditText username = (EditText)findViewById(R.id.editText);
        final EditText password = (EditText)findViewById(R.id.editText2);

        final Button b1=(Button)findViewById(R.id.button1);
        final URL[] requestURL = {null};
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = password.getText().toString();
                user= username.getText().toString();
              //  mDataHandler.updateLoginCredentials(user, pass);
                try {
                    requestURL[0] = new URL("http://46.101.104.38:3000/login");
                } catch (MalformedURLException e) {
                    Log.e("LOG", "failed");
                }
                Login serverSync = new Login(LoginScreen.this);
                serverSync.execute(requestURL[0]);

//change
            }
        });

    }



}
