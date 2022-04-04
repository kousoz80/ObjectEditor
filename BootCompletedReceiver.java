package com.example.test;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
public class BootCompletedReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      Intent intent_service = new Intent(); 
      intent_service.setClassName( "com.example.test", "com.example.test.HelloWorld" ); 
      context.startService( intent_service );
    }
  }
}
