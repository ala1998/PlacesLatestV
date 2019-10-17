package com.example.yarab;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyIntentService extends IntentService {


    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    private Location triggerLocation;
    private int id=-1;
    public MyIntentService() {
        super("MyIntentService");
    }

    // Handle errors
    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // Retrieve the Geofencing intent
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // handling errors
        if (geofencingEvent.hasError()) {

            String errorMsg = getErrorString(geofencingEvent.getErrorCode());
            Log.e("MyIntentService", errorMsg);
            return;
        }

        // Retrieve GeofenceTrasition
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofence that were triggered
             //triggerLocation= geofencingEvent.getTriggeringLocation();
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Create a detail message with Geofences received
            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences);
            // Send notification details as a String
            sendNotification(geofenceTransitionDetails);
            Toast.makeText(getApplicationContext(),"BBBBBBB",Toast.LENGTH_LONG).show();

        }
    }

    // Create a detail message with Geofences received
    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
           triggeringGeofencesList.add(geofence.getRequestId());
       }
    for(int i=0;i<triggeringGeofencesList.size();i++)
        {
            if(triggeringGeofences.get(i).getRequestId().equals("BLUE"))
               id=R.raw.blue;
            else
            if(triggeringGeofences.get(i).getRequestId().equals("RAMALLAH"))
                id=R.raw.ramallah;
            else
            if(triggeringGeofences.get(i).getRequestId().equals("RAFEDIA"))
                id=R.raw.rafedia;

        }
        String status = null;
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            status = "Entering ";
        else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            status = "Exiting ";
        Toast.makeText(getApplicationContext(),"CCCCCCC",Toast.LENGTH_LONG).show();

        return status + TextUtils.join(", ", triggeringGeofencesList);
    }

    // Send a notification
    private void sendNotification(String msg) {
        Log.i("MyIntentService", "sendNotification: " + msg);

        // Intent to start the main Activity
        Intent notificationIntent = MapsActivity.makeNotificationIntent(getApplicationContext(), msg);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MapsActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));
        Toast.makeText(getApplicationContext(),"DDDDDDDD",Toast.LENGTH_LONG).show();

        playMusic();
    }


    // Create a notification
   private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_my_location)
                .setColor(Color.rgb(135,39,154))
                .setContentTitle("Visit Paletine")
                .setContentText("There's a historical Place here!")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }

    private void playMusic(){
       int audio=-1;
        if(id==0)
             audio=R.raw.blue;
        else
        if(id==1)
            audio=R.raw.ramallah;
        else
        if(id==2)
            audio=R.raw.rafedia;
        if(id!=-1) {
            MediaPlayer player = MediaPlayer.create(this, audio);
            player.setLooping(false);
            player.start();
        }
    }
}