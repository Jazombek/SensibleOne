package pl.edu.pwr.sensibleone;

import android.content.Context;
import android.hardware.Sensor;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


public class StartScreen extends AppCompatActivity{

    //private SensorManager sensorManager;

    //private Button startButton;
    private Boolean grav,gyro,proxi,atOnce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        initViews();

        detectSensors();
    }


    private void initViews(){
        Button startButton=findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb=findViewById(R.id.atOnceButton);
                atOnce=cb.isChecked();
                ArcadeActivity.start(getApplicationContext(), grav,gyro,proxi, atOnce);
            }
        });
    }
    private void detectSensors() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {

            grav=(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null);

            gyro=(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null);
            proxi=(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null);
        }
    }





}
