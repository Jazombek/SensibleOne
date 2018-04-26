package pl.edu.pwr.sensibleone;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;

public class ArcadeActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor gravitymeter,gyrometer, proximitymeter;
    private TextView gravText, gravTargetText, gyroText, gyroTargetText, proxiText, proxiTargetText, scoreText,timerText;
    private Boolean grav,gyro,proxi,lastProxi;
    private CountDownTimer timer;
    private int gravTarget=0,gyroTarget,proxiTarget,count;
    private static final int GRAV_THRESHOLD = 9,GYRO_THRESHOLD = 2, DIFFICULTY_RAMP=15,DONT_CARE=6;
    //private static final int GYRO_THRESHOLD = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcade);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initViews();
        //collectSensors();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assignSensors();
        registerSensors();
        //timerStart();
        gameStart();




    }

    public static void start(Context context,Boolean grav, Boolean gyro, Boolean proxi, Boolean atOnce) {
        Intent starter = new Intent(context, ArcadeActivity.class);
        starter.putExtra("grav", grav);
        starter.putExtra("gyro", gyro);
        starter.putExtra("proxi",proxi);
        starter.putExtra("atOnce", atOnce);
        context.startActivity(starter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        registerSensors();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onPause() {

        super.onPause();
        sensorManager.unregisterListener(this);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        timer.cancel();
    }


    private void assignSensors(){
        if(getIntent().getBooleanExtra("grav",false))
            gravitymeter = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if(getIntent().getBooleanExtra("gyro",false))
            gyrometer = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(getIntent().getBooleanExtra("proxi",false)) {
            proximitymeter = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        }

    }

    private void registerSensors(){
        if(getIntent().getBooleanExtra("grav",false))
            sensorManager.registerListener(this, gravitymeter, SensorManager.SENSOR_DELAY_GAME);
        if(getIntent().getBooleanExtra("gyro",false))
            sensorManager.registerListener(this, gyrometer, SensorManager.SENSOR_DELAY_GAME);
        if(getIntent().getBooleanExtra("proxi",false))
            sensorManager.registerListener(this, proximitymeter,SensorManager.SENSOR_DELAY_GAME);
    }

    private void timerStart(){

        timer=new CountDownTimer(300000/(9+count), 10) {


            public void onTick(long millisUntilFinished) {
                timerText.setText(getString(R.string.secondsRemaining) + millisUntilFinished / 1000);
                if(allSet()){
                    timer.cancel();
                    refresh();
                }
                paint();





            }

            public void onFinish() {
                timerText.setText(R.string.finished);
                Vibrator v =(Vibrator)getApplication().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(750);
                updateHighscores();


            }
        };

        timer.start();

    }

    private void gameStart(){
        count=0;
        grav=true;
        gyro=true;
        proxi=true;
        lastProxi=false;
        refresh();



        //gyroTargetText.setText(Integer.toString(count));
    }

    private void paint(){
        if(grav){
            gravTargetText.setTextColor(Color.GREEN);
        }else
            gravTargetText.setTextColor(Color.BLACK);
        if(gyro){
            gyroTargetText.setTextColor(Color.GREEN);
        }else
            gyroTargetText.setTextColor(Color.BLACK);
        if(proxi){
            proxiTargetText.setTextColor(Color.GREEN);
        }else
            proxiTargetText.setTextColor(Color.BLACK);
    }

    private void refresh(){
        Vibrator v =(Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(50);

        count++;
        scoreText.setText(Integer.toString(count));
        rollTarget();

        while(gravTarget==DONT_CARE&&gyroTarget==DONT_CARE){
            rollTarget();
        }
        timerStart();
    }

    private void rollTarget(){
        if(gravitymeter!=null){
            Random r = new Random();
            int i1 = r.nextInt(DIFFICULTY_RAMP ) ;
            if(i1>count){
                gravTarget=DONT_CARE;
                grav=true;
                gravTargetText.setText(R.string.DontCare);
            }else
            setGrav();
        }
        if(gyrometer!=null){
            Random r = new Random();
            int i1 = r.nextInt(DIFFICULTY_RAMP ) ;
            if(i1>count){
                gyroTarget=DONT_CARE;
                gyro=true;
               gyroTargetText.setText(R.string.DontCare);
            }else
            setGyro();
        }
        if(proximitymeter!=null){
            Random r = new Random();
            int i1 = r.nextInt(DIFFICULTY_RAMP ) ;
            if(i1+5>count){
                proxiTarget=DONT_CARE;
                proxi=true;
                proxiTargetText.setText(R.string.DontCare);
            }else
                setProxi();
        }
    }

    private void updateHighscores(){

    }


    private Boolean allSet(){
        return grav&&gyro&&proxi;
    }


    private void setGrav(){
        grav=false;

        Random r = new Random();
        int i1 = r.nextInt(6 ) ;
        switch (i1){case 0: gravTargetText.setText(R.string.xup);
            break;
            case 1: gravTargetText.setText(R.string.xdown);
                break;
            case 2: gravTargetText.setText(R.string.yup);
                break;
            case 3: gravTargetText.setText(R.string.ydown);
               break;
            case 4: gravTargetText.setText(R.string.zup);
                break;
            case 5: gravTargetText.setText(R.string.zdown);
               break;


        }
        gravTarget=i1;


    }

    private void setGyro(){
        gyro=false;

        Random r = new Random();
        int i1 = r.nextInt(6 ) ;
        switch (i1){case 0: gyroTargetText.setText(R.string.xco);
            break;
            case 1: gyroTargetText.setText(R.string.xcl);
                break;
            case 2: gyroTargetText.setText(R.string.yco);
                break;
            case 3: gyroTargetText.setText(R.string.ycl);
                break;
            case 4: gyroTargetText.setText(R.string.zco);
               break;
            case 5: gyroTargetText.setText(R.string.zcl);
                break;


        }
        gyroTarget=i1;

    }

    private void setProxi(){
        proxi=false;

        Random r = new Random();
        int i1 = r.nextInt(2 )+4 ;

        switch (i1){case 4: proxiTargetText.setText(R.string.proxi);
            if(lastProxi)proxi=true;
            break;
            case 5: proxiTargetText.setText(R.string.noproxi);
            if(!lastProxi)proxi=true;
                break;
            case 6: proxiTargetText.setText(R.string.DontCare);
            proxi=true;break;


        }
        proxiTarget=i1;

    }

    private void initViews(){
        gravText =findViewById(R.id.gravTextView);
        gravTargetText =findViewById(R.id.gravTargetTextView);
        gyroText =findViewById(R.id.gyroTextView);
        gyroTargetText =findViewById(R.id.gyroTargetTextView);
        proxiText =findViewById(R.id.proxiTextView);
        proxiTargetText =findViewById(R.id.proxiTargetTextView);
        scoreText =findViewById(R.id.scoreTextView);
        timerText=findViewById(R.id.timerTextView);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor==gravitymeter) {

            if(sensorEvent.values[0]>GRAV_THRESHOLD){
                gravText.setText(R.string.xup);
                if(gravTarget==0)grav=true;
            }

            else if(-sensorEvent.values[0]>GRAV_THRESHOLD){
                gravText.setText(R.string.xdown);
                if(gravTarget==1)grav=true;
            }
            else if(sensorEvent.values[1]>GRAV_THRESHOLD){
                gravText.setText(R.string.yup);
                if(gravTarget==2)grav=true;
            }

            else if(-sensorEvent.values[1]>GRAV_THRESHOLD){
                gravText.setText(R.string.ydown);
                if(gravTarget==3)grav=true;
            }


            else if(sensorEvent.values[2]>GRAV_THRESHOLD){
                gravText.setText(R.string.zup);
                if(gravTarget==4)grav=true;
            }

            else if(-sensorEvent.values[2]>GRAV_THRESHOLD){
                gravText.setText(R.string.zdown);
                if(gravTarget==5)grav=true;
            }

            else {
                gravText.setText("0");

                if (gravTarget == DONT_CARE) {
                    grav = true;
                }
                else
                if(getIntent().getBooleanExtra("atOnce",false))
                grav=false;

            }

        }
        if(sensorEvent.sensor== proximitymeter) {
                if (sensorEvent.values[0] == 0) {
                    proxiText.setText(R.string.proxi);
                    lastProxi = true;
                    if (proxiTarget == 4) {
                        proxi = true;
                    } else if(proxiTarget!=DONT_CARE&&getIntent().getBooleanExtra("atOnce",false))proxi = false;
                } else {
                    proxiText.setText(R.string.noproxi);
                    lastProxi = false;
                    if (proxiTarget == 5) {
                        proxi = true;
                    } else if(proxiTarget!=DONT_CARE&&getIntent().getBooleanExtra("atOnce",false))proxi = false;
                }
            }
        if(sensorEvent.sensor==gyrometer) {
            if(sensorEvent.values[0]>GYRO_THRESHOLD){
                gyroText.setText(R.string.xco);
                if(gyroTarget==0)gyro=true;
            }

            else if(-sensorEvent.values[0]>GYRO_THRESHOLD){
                gyroText.setText(R.string.xcl);
                if(gyroTarget==1)gyro=true;
            }
            else if(sensorEvent.values[1]>GYRO_THRESHOLD){
                gyroText.setText(R.string.yco);
                if(gyroTarget==2)gyro=true;
            }

            else if(-sensorEvent.values[1]>GYRO_THRESHOLD){
                gyroText.setText(R.string.ycl);
                if(gyroTarget==3)gyro=true;
            }


            else if(sensorEvent.values[2]>GYRO_THRESHOLD){
                gyroText.setText(R.string.zco);
                if(gyroTarget==4)gyro=true;
            }

            else if(-sensorEvent.values[2]>GYRO_THRESHOLD){
                gyroText.setText(R.string.zcl);
                if(gyroTarget==5)gyro=true;
            }
            else{
                gyroText.setText("0");
                if (gyroTarget == DONT_CARE) {
                    gyro = true;
                }
                else
                if(getIntent().getBooleanExtra("atOnce",false))
                    gyro=false;
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
