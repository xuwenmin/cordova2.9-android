package org.apache.cordova;

import java.util.Date;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.LOG;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog.Calls;
import android.telephony.PhoneNumberUtils;

public class Phone extends CordovaPlugin {
	
	private static final int PHONE_CALL = 0;     // 拨打电话
    private static final int PHONE_ABORT = 1;     // 挂断电话
    private Date start_time;
    private CallbackContext callbackContext;   
    private String phonenumber;
    
	@Override
	public boolean  execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		try{

		this.callbackContext = callbackContext;
		LOG.d("phone", action);
		if ("call".equals(action)) {
			this.phonenumber=args.getString(0);
			this.call(args.getString(0),callbackContext);
	        return true;
	    }
	    if ("abort".equals(action)) {
	       this.abort(callbackContext);
	        return true;
	    }
	    return false;
		}
		catch(Exception e){
			callbackContext.error(e.getMessage());
		}
		return false;
	}
	//拨打电话
	private  void call(String phonenumber, CallbackContext callbackContext) {

        if (phonenumber != null && phonenumber.length() > 0) { 
        	LOG.d("phone", phonenumber);
    	    if(PhoneNumberUtils.isGlobalPhoneNumber(phonenumber)){
    	     Intent i = new Intent();  
    	     i.setAction(Intent.ACTION_DIAL); 
    	     i.setData(Uri.parse("tel:"+phonenumber));   
    	     start_time=new Date();
    	     this.cordova.startActivityForResult(this, i,PHONE_CALL);
    	     
    	    }
    	    else{
    	    	callbackContext.error(phonenumber+"不是有效的电话号码。");
    	    }  
        } else {
            callbackContext.error("电话号码不能为空.");
        }
    }
	//中断电话
	private void abort(CallbackContext callbackContext) {

    }
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Date end_time=new Date();
		if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PHONE_CALL) {

            	this.callbackContext.error("未知状态");
            } 
		}
		else if (resultCode == Activity.RESULT_CANCELED) {
			try{
				if (requestCode == PHONE_CALL) {
				long duration=GetLastCallDuration();
					
		            	PhoneResult result=new PhoneResult();

		            	result.setNumber(Phone.this.phonenumber);
		            	result.setStartTime(Phone.this.start_time);
		            	result.setEndTime(end_time);
		            	result.setDuration(duration);
		                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result.toJSONObject()));
		                
		            } 
			}
			catch(Exception e){
				this.callbackContext.error(e.getMessage());
			}
    }

    else {
    	this.callbackContext.error("其他错误！");
    	 
    }
	}
	long delayTime=0;
	long timeOut=2000;
	long GetLastCallDuration() throws InterruptedException{
		Activity activity = this.cordova.getActivity();
		Cursor cursor = activity.getContentResolver().query(Calls.CONTENT_URI, 
				new String[] {Calls.NUMBER,Calls.DATE, Calls.DURATION, Calls.TYPE, Calls.DATE }, 
				"number=?and type=?", 
				new String[]{this.phonenumber,"2"}, 
				Calls.DEFAULT_SORT_ORDER);
		activity.startManagingCursor(cursor);
		boolean hasRecord = cursor.moveToFirst();
		if (hasRecord) { 
			long endTime=cursor.getLong(cursor.getColumnIndex(Calls.DATE));
			Date date = new Date(endTime);
			long duration = cursor.getLong(cursor.getColumnIndex(Calls.DURATION));
			long dif=this.start_time.getTime()-date.getTime();
			long second=dif/1000;
			if(second<2&&second>-2){
				return duration;
			}else{
				if(delayTime>=timeOut){
					return 0;
				}
				Thread.sleep(200);
				delayTime+=200;
				return GetLastCallDuration();
			}
		}else{
			if(delayTime>=timeOut){
				return 0;
			}
			Thread.sleep(200);
			delayTime+=200;
			return GetLastCallDuration();
		}
	}

}
