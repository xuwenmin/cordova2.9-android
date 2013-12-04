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
import android.net.Uri;
import android.telephony.PhoneNumberUtils;

public class SMS extends CordovaPlugin{
	

	private CallbackContext callbackContext;   
	@Override
	public boolean  execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		try{
			this.callbackContext = callbackContext;
			LOG.d("sms", action);
			if ("send".equals(action)) {
				this.send(args.getString(0),callbackContext);
		        return true;
		    }
			return false;
		}
		catch(Exception e){
			callbackContext.error(e.getMessage());
		}
		return false;
	}
	//发送短信
		private  void send(String phonenumber, CallbackContext callbackContext) {

	        if (phonenumber != null && phonenumber.length() > 0) { 
	    	    if(PhoneNumberUtils.isGlobalPhoneNumber(phonenumber)){
	    	    	 LOG.d("sms", phonenumber);
		    	     Intent i = new Intent();  
		    	     i.setAction(Intent.ACTION_VIEW); 
		    	     i.setData(Uri.parse("sms:"+phonenumber));   
		    	     this.cordova.startActivityForResult(this, i,1);
	    	    }
	    	    else{
	    	    	callbackContext.error(phonenumber+"不是有效的电话号码。");
	    	    }  
	        } else {
	            callbackContext.error("电话号码不能为空.");
	        }
	    }
		public void onActivityResult(int requestCode, int resultCode, Intent intent) {
			Date end_time=new Date();
			if (resultCode == Activity.RESULT_OK) {

	            if (requestCode == 1) {

	            	this.callbackContext.error("未知状态");
	            } 
			}
			else if (resultCode == Activity.RESULT_CANCELED) {
				try{
					if (requestCode == 1) {
						
			            	PhoneResult result=new PhoneResult();
			            	result.setEndTime(end_time);
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
}
