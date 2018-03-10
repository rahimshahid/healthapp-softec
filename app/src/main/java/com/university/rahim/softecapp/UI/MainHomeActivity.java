package com.university.rahim.softecapp.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.university.rahim.softecapp.R;
import com.university.rahim.softecapp.Services.StepCountService;
import com.university.rahim.softecapp.Utils.LocalStore;

import at.grabner.circleprogress.CircleProgressView;

import static android.os.SystemClock.elapsedRealtime;

public class MainHomeActivity extends AppCompatActivity {
    private Drawer drawer;
    CircleProgressView mCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        CreateView();

        StepCountService.Start(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Steps"));
    }

    @Override
    protected void onResume() {
        if(drawer!=null && drawer.isDrawerOpen()) {
            drawer.setSelectionAtPosition(1);
        }
        super.onResume();
    }

    void CreateView(){
        NavDrawerInit();

        this.findViewById(R.id.hamIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer();
            }
        });

        ProgressBar p = ((ProgressBar)this.findViewById(R.id.workoutProgressbar));
        p.setScaleY(4f);
        p.setProgress(80);
        p.setMax(100);

        setupCircleView();

        findViewById(R.id.resumeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO:
            }
        });
    }


    private void NavDrawerInit() {
        new DrawerBuilder().withActivity(this).build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("HOME");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Profile");
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(2).withName("Statistics");
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(2).withName("Logout");

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawerback)
                .addProfiles(
                        new ProfileDrawerItem().withName("Rahim Shahid")
                                .withEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                .withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withSelectedItem(1)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        item3,
                        item4
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position){
                            case 1:
                                MainHomeActivity.this.drawer.closeDrawer();
                                break;
                            case 2:
                                break;
                            case 3:
                                Intent i = new Intent(MainHomeActivity.this, ProfileActivity.class);
                                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                break;
                            case 4:
                                Intent i2 = new Intent(MainHomeActivity.this, HomeActivity.class);
                                //i2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i2);
                                break;
                            case 5:
                                FirebaseAuth.getInstance().signOut();
                                break;
                        }

                        return true;
                    }
                })
                .build();

        drawer = result;
    }

    private void setupCircleView(){
        mCircleView = this.findViewById(R.id.stepProgress);
        mCircleView.setRimWidth(70);
        mCircleView.setMaxValue(6000);
        mCircleView.setValue(0);
        mCircleView.setValueAnimated(LocalStore.getSteps(this));


        //show unit
        mCircleView.setUnit("%");
        mCircleView.setUnitVisible(true);

        //text sizes
        mCircleView.setTextSize(50); // text size set, auto text size off
        mCircleView.setUnitSize(40); // if i set the text size i also have to set the unit size
        mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        //if you want the calculated text sizes to be bigger/smaller you can do so via
        mCircleView.setUnitScale(0.9f);
        mCircleView.setTextScale(0.9f);

        mCircleView.setBarColor(getResources().getColor(R.color.primary), getResources().getColor(R.color.accent));
        updateViews(LocalStore.getSteps(this),(int)LocalStore.getCals(this),0);
    }

    private void updateViews(int steps, int cals, int runTime){
         mCircleView.setValue(steps);
        ((TextView)this.findViewById(R.id.stepsWalked)).setText(steps + " Steps");
        ((TextView)this.findViewById(R.id.calburned)).setText((int)LocalStore.getCals(this) + " Calories Burned");
        ((TextView)this.findViewById(R.id.distwalked)).setText((int)LocalStore.getDistance(this) + " KM Walked");

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int steps = intent.getIntExtra("stepCount", 0);
            double distance=intent.getDoubleExtra("distanceWalked", 0.0);
            distance=distance/1000;
            distance=Math.round(distance * 10);
            distance=distance/10;

            int time=intent.getIntExtra("ranFor",0);

            if(time < 60)
                time = time;
            if(time >= 60)
                time = time/60;

            int cals = (int)intent.getDoubleExtra("caloriesBurned", 0.0);
            updateViews(steps, cals, time);
        }
    };
}
