package com.university.rahim.softecapp.Services;

/**
 * Created by RAHIM on 3/10/2018.
 */

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.university.rahim.softecapp.Utils.CalorieBurnedCalculator;
import com.university.rahim.softecapp.Utils.DistanceCalculator;
import com.university.rahim.softecapp.Utils.FeedReaderDbHelper;
import com.university.rahim.softecapp.Utils.LocalStore;
import com.university.rahim.softecapp.Utils.RunningDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.SystemClock.elapsedRealtime;

public class StepCountService extends Service implements SensorEventListener {
    private static String TAG = "dbg_StepCountService: ";

    PowerManager.WakeLock wakeLock;
    private SensorManager sensorManager;
    private boolean StepCounterFound;
    private Sensor mSensorAccelerometer;
    private Sensor mStepCounterSensor;
    private int stepCount;
    private int stepCount_moment_ago;
    private boolean high_G,low_G;
    private boolean firstRun;
    private int prev;
    private int initial;
    private long prevTime;
    private long prev_db_writing_time;
    private long prev_speed_update_time=0;
    private long prev_location_update_time=0;
    private int last_StepValue_written_to_DB=0;
    private DistanceCalculator D;
    private CalorieBurnedCalculator C;
    private int today;
    private static final double  maxG= 1.5;   //Increasing this will decrease sensitivity
    FeedReaderDbHelper mDbHelper;
    RunningDbHelper mRunningDBHelper;
    private boolean running=false;
    private int seconds_ran=0;
    private int chance_finish=0;
    private int chance_start=0;
    private double curr_speed=0;
    private int last_step_count_while_running=0;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
        wakeLock.acquire();

        high_G=false;
        low_G=false;
        prev=0;
        firstRun=true;
        prev_db_writing_time=0;
        prev_location_update_time=0;
        prev_speed_update_time=0;
        String x=new java.text.SimpleDateFormat("dd").format(new Date());
        today=Integer.parseInt(x);

        //Check for Sensor availability Use Pedometer if available, Accelerometer otherwise
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
        {
            StepCounterFound=true;
            mStepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
        {
            StepCounterFound=false;
            mSensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else{
            Log.d(TAG, "onCreate: Sensor Unavailable");
            return;
        }

        int weight = LocalStore.getUserWeight(this);
        int height = LocalStore.getUserHeight(this);

        Log.d(TAG, "onCreate: " + weight + " " + height);

        D=new DistanceCalculator(height); //Give user height in cm here
        C=new CalorieBurnedCalculator(weight,height);

        //Open/Create DB
        mDbHelper = new FeedReaderDbHelper(this);
        mRunningDBHelper=new RunningDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteDatabase db2=mRunningDBHelper.getReadableDatabase();

        String[] a = new String[1]; // steps of today
        a[0]=new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
        a[0] +="%";
        Cursor c=db.rawQuery("select value from STEPS where time like ?",a);

        if(c==null){
            last_StepValue_written_to_DB=initial=stepCount=stepCount_moment_ago=0;
        }
        while(c.moveToNext())
        {
            if(c.isLast()){
                last_StepValue_written_to_DB=initial=stepCount=stepCount_moment_ago=c.getInt(0);
            }
        }


        String[] b = new String[1]; // steps of today
        b[0]=new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());

        Cursor cx=db2.rawQuery("select seconds from RUN where day like ?",a);
        if(cx.getCount()==0){
            seconds_ran=0;
        }
        while(cx.moveToNext())
        {
            if(cx.isLast()){
                seconds_ran=cx.getInt(0);
            }
        }

        Timer timer = new Timer();
        timer.schedule(new UpdateDB(),0, 5000); //Update DB every 5 secs if step value changes
        timer.schedule(new checkRunning(),0, 1000);
    }

    private void resetAll(){
        high_G=false;
        low_G=false;
        prev=0;
        firstRun=true;
        prev_db_writing_time=0;
        prev_location_update_time=0;
        prev_speed_update_time=0;
        String x=new java.text.SimpleDateFormat("dd").format(new Date());
        today=Integer.parseInt(x);

        last_StepValue_written_to_DB=initial=stepCount=stepCount_moment_ago=0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Release Wakelock
        wakeLock.release();
        //Unregister Sensors
        if(StepCounterFound)
            sensorManager.unregisterListener(this);
        else
            sensorManager.unregisterListener(this);

        //Then Destroy
        super.onDestroy();
    }

    private double getSpeed(long time){
        if(time==prev_speed_update_time)
            return -1;
        int stepDelta=stepCount-stepCount_moment_ago;
        if(stepDelta>=2){
            double x=D.distanceWalkedinMeters(stepDelta)/(time-prev_speed_update_time)*1000;
            x=(x/1000)*3600;
            prev_speed_update_time=time;
            stepCount_moment_ago=stepCount;
            return x;
        }
        else
            return -1;
    }

    class UpdateDB extends TimerTask {
        public void run()
        {
            DB_writing_thread();
        }
    }

    class checkRunning extends TimerTask {
        public void run()
        {
            if(last_step_count_while_running!=stepCount) {
                Thread th = new Thread(new Runnable() {
                    public void run(){
                        if (curr_speed >= 7 && running == false)
                            running = true;
                        else if (curr_speed < 7 && running == true) {
                            chance_finish++;
                            if (chance_finish == 3) {
                                running = false;
                                chance_finish = 0;
                            }
                        }
                        if (running)
                            seconds_ran += 1;
                        last_step_count_while_running=stepCount;

                    }
                });
                th.start();
            }
        }
    }


    private void DB_writing_thread() {
        if(last_StepValue_written_to_DB==stepCount)
            return;
        Thread th = new Thread(new Runnable() {
            public void run() {
                //Delete old value of same hour
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                String tmp=new java.text.SimpleDateFormat("yyyy/MM/dd_HH").format(new Date());
                tmp+="%";
                db.execSQL("delete from STEPS where time like '"+tmp+"'");
                //Write new steps value for that hour
                ContentValues v = new ContentValues();
                v.put("time",new SimpleDateFormat("yyyy/MM/dd_HH:mm").format(new Date()));
                v.put("value", stepCount);
                db.insert("STEPS", null, v);
                last_StepValue_written_to_DB=stepCount;

                SQLiteDatabase db2=mRunningDBHelper.getWritableDatabase();
                String day=new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
                db2.execSQL("delete from RUN where day ='"+day+"';");
                ContentValues w = new ContentValues();
                w.put("day", day);
                w.put("seconds",seconds_ran);
                db2.insert("RUN", null, w);
            }
        });
        th.start();
    }

    private void newDayOperations(){
        String temp=new java.text.SimpleDateFormat("dd").format(new Date());
        int veryAccurateToday=Integer.parseInt(temp);
        if(veryAccurateToday!=today){
            resetAll();
            today=veryAccurateToday;
        }
    }


    private void onStepTaken(long time,String sensorUsed){
        //Broadcast that a step is taken
        Intent intent = new Intent("Steps");
        intent.putExtra("stepCount",stepCount );
        intent.putExtra("sensor",sensorUsed);
        intent.putExtra("distanceWalked",D.distanceWalkedinMeters(stepCount));
        intent.putExtra("caloriesBurned",C.caloriesBurned(stepCount));
        double speed=getSpeed(time);
        curr_speed=speed;
        intent.putExtra("speed",speed);


        intent.putExtra("ranFor",seconds_ran);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


        //Broadcast Location update request every X milli seconds while walking
        if(time-prev_location_update_time>=50000){
            prev_location_update_time=time;
            Intent i = new Intent("LocationUpdate");
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        newDayOperations();
        long time=elapsedRealtime();
        if (StepCounterFound) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                float[] values = event.values;

                if(firstRun){
                    if (values.length > 0) {
                        prev = (int) values[0];
                        prev_speed_update_time=time;
                        firstRun=false;
                    }
                }

                if (values.length > 0) {
                    stepCount = (int) values[0];
                    stepCount=stepCount-prev+initial;
                    if(stepCount>0){
                        onStepTaken(time,"Pedometer");
                    }
                    // prev=temp;
                }
            }
        }
        else {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float[] values = event.values;
                float x=values[0];
                float y=values[1];
                float z=values[2];

                double g = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
                if(firstRun) {
                    prevTime = elapsedRealtime();
                    prev_speed_update_time=time;
                    firstRun=false;
                }

                if (g>maxG && high_G && low_G) {  //threshold
                    high_G=false;
                    low_G=false;
                    if(time-prevTime>1100) {   //At least x ms for g to complete its cycle
                        stepCount++;
                        onStepTaken(time,"Accelerometer");
                        prev=stepCount;
                    }
                }

                if(g>=maxG)
                    high_G=true;
                if(g<maxG&&high_G)
                    low_G=true;

            }
            else
                return;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do Nothing
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
