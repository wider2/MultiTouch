package twister.multitouch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName();
    MultitouchView multiTouch;
    TextView tv_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_output = (TextView) findViewById(R.id.tv_output);
        multiTouch = (MultitouchView) findViewById(R.id.multiTouch);
        multiTouch.setListener(twistertListener);
    }

    OnTwisterClickedListener twistertListener = new OnTwisterClickedListener() {
        @Override
        public void onGameRequestClicked(int clicksNumber) {
            Log.wtf(TAG, "Profile clicked: " + clicksNumber);
            tv_output.setText("Game finished.");
            multiTouch.setStopEvent(true);
        }
    };

}
