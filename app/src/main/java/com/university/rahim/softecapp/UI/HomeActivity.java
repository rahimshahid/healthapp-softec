package com.university.rahim.softecapp.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.university.rahim.softecapp.Services.*;

import com.university.rahim.softecapp.R;
import com.university.rahim.softecapp.Utils.CalorieBurnedCalculator;
import com.university.rahim.softecapp.Utils.DistanceCalculator;
import com.university.rahim.softecapp.Utils.FeedReaderDbHelper;
import com.university.rahim.softecapp.Utils.LocalStore;
import com.university.rahim.softecapp.Utils.RunningDbHelper;

import java.util.Date;

import static android.os.SystemClock.elapsedRealtime;

public class HomeActivity extends AppCompatActivity {
    private static String TAG = "dbg_HomeAvticity";
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private TextView tv_steps,tv_cals,tv_distance,tv_speed,tv_run;
    private GraphView graph;
    private int steps,cals,speed;
    private double distance;
    private long last_step_taken,last_plot_update_time;
    private FeedReaderDbHelper mDbHelper;
    private DistanceCalculator D;
    private CalorieBurnedCalculator C;
    private int height,weight;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LocalStore.setUserHeight(this, 72);
        LocalStore.setUserWeight(this, 72);

        init_Views();
        init_Data();
        plotGraph();

        //Start Step Service
        Intent mServiceIntent = new Intent(this, StepCountService.class);
        this.startService(mServiceIntent);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Steps"));
        startTimerThreadToUpdateTextBoxSpeed();
    }

    private void init_Views(){
        tv_steps=(TextView)findViewById(R.id.tv_steps);
        tv_speed=(TextView)findViewById(R.id.tv_speed);
        tv_cals=(TextView)findViewById(R.id.tv_cal);
        tv_distance=(TextView)findViewById(R.id.tv_distance);
        tv_run=(TextView)findViewById(R.id.tv_run);

        //GRAPH VIEW
        graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(24);
        graph.getViewport().setYAxisBoundsManual(true);

    }
    private void init_Data(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        weight=prefs.getInt("wight",0);
        height=prefs.getInt("height",0);

        last_plot_update_time=0;
        D=new DistanceCalculator(height); //Give user height in cm here
        C=new CalorieBurnedCalculator(weight,height); //weight kg and height cm
        mDbHelper = new FeedReaderDbHelper(this);

        //SQLiteDatabase t=mDbHelper.getWritableDatabase();
        //t.execSQL("delete from STEPS where time like '2016/11/18%'");
        //t.execSQL("delete from STEPS");
        RunningDbHelper tmpdb=new RunningDbHelper(this);
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        SQLiteDatabase db2=tmpdb.getReadableDatabase();

        String[] a = new String[1]; // steps of today
        a[0]=new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
        a[0] +="%";
        Cursor c=db.rawQuery("select value from STEPS where time like ?",a);


        if(c==null){
            tv_steps.setText(""+0+" Steps");
            tv_distance.setText("" + 0 + " Km");
            tv_cals.setText("" + 0+" Cal");
            return;
        }
        while(c.moveToNext())
        {
            if(c.isLast()){
                steps=c.getInt(0);
                tv_steps.setText(""+steps+" Steps");

                distance=D.distanceWalkedinMeters(steps);
                distance=distance/1000;
                distance=Math.round(distance*10);
                distance=distance/10;
                tv_distance.setText("" + distance + " Km");

                cals=(int)C.caloriesBurned(steps);
                tv_cals.setText("" + cals+" Cal");
            }
        }

        Cursor cx=db2.rawQuery("select seconds from RUN where day like ?",a);
        if(cx.getCount()==0){
            tv_run.setText("data not found");
        }
        while(cx.moveToNext())
        {
            if(cx.isLast()){
                int time=cx.getInt(0);
                if(time < 60)
                    tv_run.setText(cx.getInt(0)+" sec");
                if(time >= 60)
                    tv_run.setText(cx.getInt(0)/60+" min");
            }
        }
    }
    private void plotGraph(){
        BarGraphSeries<DataPoint> series=new BarGraphSeries<>();
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        int last_hour_steps=0,max=0;

        for(int i=0;i<24;i++) {
            String[] a = new String[1]; // steps of today
            a[0] = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date()); //get current date
            if(i<=7)
                a[0] += "_0"+i;

            else if(i==8)
                a[0] += "_08";
            else if(i==9)
                a[0] += "_09";

            else if(i>=10)
                a[0] += "_" + i;

            a[0] += "%";
            Cursor c = db.rawQuery("select * from STEPS where time like ?", a);

            if (c.getCount()>0) {
                while (c.moveToNext()) {
                    if (c.isLast()) {
                        int StepDelta=c.getInt(1)-last_hour_steps;
                        if(StepDelta>max)
                            max=StepDelta;
                        series.appendData(new DataPoint(i, StepDelta), true, 24);
                        last_hour_steps=c.getInt(1);
                    }
                }
            }
            else {
                series.appendData(new DataPoint(i, 0), true, 24);
            }
        }
        //series.appendData(new DataPoint(12,50),true,24);
        series.setSpacing(10);
        graph.addSeries(series);
        graph.setTitle("Hourly Steps");
        graph.getViewport().setMaxY(max+(0.1*max));
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setNumHorizontalLabels(24);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    if(value==22){
                        return super.formatLabel(11, isValueX)+" pm";
                    }
                    if(value>=12) {
                        if(value%6!=0||value==24)
                            return "";
                        value=value%12;
                        if(value==0)
                            value=12;
                        return super.formatLabel(value, isValueX)+" pm";
                    }
                    else{
                        if(value%6!=0)
                            return "";
                        if(value==0)
                            value=12;
                        return super.formatLabel(value, isValueX)+" am";
                    }
                } else {
                    // show normal Y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 0),new DataPoint(5, 0),new DataPoint(10, 0),new DataPoint(15, 0),new DataPoint(20, 0),
                new DataPoint(1, 0),new DataPoint(6, 0),new DataPoint(11, 0),new DataPoint(16, 0),new DataPoint(21, 0),
                new DataPoint(2, 0),new DataPoint(7, 0),new DataPoint(12, 0),new DataPoint(17, 0),new DataPoint(22, 0),
                new DataPoint(3, 0),new DataPoint(8, 0),new DataPoint(13, 0),new DataPoint(18, 0),new DataPoint(23, 0),
                new DataPoint(4, 0),new DataPoint(9, 0),new DataPoint(14, 0),new DataPoint(19, 0),
        });
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int densityDpi = (int)(metrics.density * 160f);

        series2.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(densityDpi/28);
                canvas.drawPoint(x, y, paint);
            }
        });
        series2.setColor(Color.GRAY);
        graph.addSeries(series2);

        PointsGraphSeries<DataPoint> series3 = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 0),new DataPoint(6, 0),new DataPoint(12, 0),new DataPoint(18, 0),new DataPoint(23, 0)
        });
        series3.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(densityDpi/22);
                canvas.drawPoint(x, y, paint);
            }
        });
        series3.setColor(Color.BLACK);
        graph.addSeries(series3);

    }

    private void startTimerThreadToUpdateTextBoxSpeed() {
        Thread th = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    if(elapsedRealtime()-last_step_taken>1500) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_speed.setText("Speed: 0 Km/h");
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        th.start();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getIntExtra("stepCount", 0));

            last_step_taken = elapsedRealtime();

            steps = intent.getIntExtra("stepCount", 0);
            tv_steps.setText(""+steps+" Steps");

            distance=intent.getDoubleExtra("distanceWalked", 0.0);
            distance=distance/1000;
            distance=Math.round(distance * 10);
            distance=distance/10;
            tv_distance.setText("" + distance + " Km");
            int time=intent.getIntExtra("ranFor",0);
            if(time < 60)
                tv_run.setText(time+" sec");
            if(time >= 60)
                tv_run.setText(time/60+" min");

            tv_cals.setText("" + (int)intent.getDoubleExtra("caloriesBurned", 0.0)+" Cal");

            double speed = intent.getDoubleExtra("speed", 0);
            if (speed >0) {
                speed = Math.round(speed * 100);
                speed = speed / 100;
                if(speed>0)
                    tv_speed.setText("Speed: " + speed + " Km/h");
            }

        }
    };
}
