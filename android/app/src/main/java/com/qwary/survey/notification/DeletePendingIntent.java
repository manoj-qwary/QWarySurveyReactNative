package com.qwary.survey.notification;

import static com.qwary.survey.notification.Notification.NotificationEntry.NOTIFICATION_TIME;
import static com.qwary.survey.notification.Notification.NotificationEntry.TABLE_NAME;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;

import java.util.Calendar;

public class DeletePendingIntent extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationId = intent.getStringExtra("_id");
        String rrule = intent.getStringExtra("rrule");
        long dstart = intent.getLongExtra("dstart",Calendar.getInstance().getTimeInMillis());
        DeleteNotification(context,notificationId,rrule,dstart);
    }

    public static void DeleteNotification(Context context,String notificationId,String rrule,long dstart){
        try {
            RecurrenceRule r = new RecurrenceRule(rrule);
            RecurrenceRuleIterator it = r.iterator(new DateTime(dstart));
            it.fastForward(DateTime.now());
            long next_time = it.nextMillis();
            Log.e("time",String.valueOf(next_time));
            Notification.NotificationDBHelper mDbHelper = new Notification.NotificationDBHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues args = new ContentValues();
            args.put(NOTIFICATION_TIME,next_time);
            db.update(TABLE_NAME,args,Notification.NotificationEntry._ID+" = "+notificationId,null);
            db.close();
            NotifyMe.scheduleNotification(context,notificationId,next_time);
        }catch (Exception e)
        {
            Log.e("error",e.getMessage());
            Notification.NotificationDBHelper mDbHelper = new Notification.NotificationDBHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(TABLE_NAME, Notification.NotificationEntry._ID+" = "+notificationId,null);
            db.close();
        }

    }
}
