package ftc.nova.ftc_2015_2016;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private Button tournamentModeBtn, testAutonomousBtn, driveRobotBtn, moveMotorsBtn, sensorViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tournamentModeBtn = (Button) findViewById(R.id.tournament_mode_btn);
        testAutonomousBtn = (Button) findViewById(R.id.test_autonomous_btn);
        driveRobotBtn = (Button) findViewById(R.id.drive_robot_btn);
        moveMotorsBtn = (Button) findViewById(R.id.move_motors_btn);
        sensorViewBtn = (Button) findViewById(R.id.sensor_view_btn);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void noAssignmentNotice(View view) {
        Toast.makeText(this, "This function has no assignment yet...", Toast.LENGTH_SHORT).show();
    }
}