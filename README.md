# Currency-Detection
Android currency detection

--------- For Training ------------
In Android.Manifest 
--- Change  this 
            <activity
            android:name=".SplashActivity"
            android:theme="@style/AppTheme.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".ReaderMainActivity"
            android:theme="@style/AppTheme.NoActionBar"/>
        <activity android:name=".KeypointsActivity">




        </activity>


----to this 

<activity
            android:name=".SplashActivity"
            android:theme="@style/AppTheme.NoActionBar">
           
        </activity>
        <activity android:name=".ReaderMainActivity"
            android:theme="@style/AppTheme.NoActionBar"/>
        <activity android:name=".KeypointsActivity">

           <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>


        </activity>
        
        
 ------After training is completed
 -------- Again Change back to 
 
 
  <activity
            android:name=".SplashActivity"
            android:theme="@style/AppTheme.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".ReaderMainActivity"
            android:theme="@style/AppTheme.NoActionBar"/>
        <activity android:name=".KeypointsActivity">




        </activity>
 
 
