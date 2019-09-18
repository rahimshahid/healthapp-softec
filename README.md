# healthapp-softec
This application was developed in a 36 Hours continuos Code Marathon during SOFTEC 2018.

# Features
Automatic monitoring of your physical activity like running, walking, calories burned throughout the day and some nice analytics.
The application provides core features to keep up your body fit and healthy. It will record and analyze your daily activities 
and habits to help maintain successful diet and lead healthy lifestyle. It includes suggestions for various types of exercises
depending on your bmi score and daily activity. Also find analytics for you activiy across months, weeks and years.

# Implementation
Application makes use of the following major components

1 - StepCounterService
    Includes logic to track number of steps taken. Uses phone's pedometer by default. Falls back to accelerometer in case pedometer
    is not available in the application. Includes custom step detection logic to be used with accelerometer sensor.

2-  UI
    Custom Slider View to track current day steps. Graphs and charts to show trends. Its just so awsome.
    
3-  Firebase Backend
    To provide authentication and database functionality.


