package com.amster.logparser;


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;


public class ExceptionLogs {

	private HashMap<String, ExceptionLog> errors = new HashMap<String,ExceptionLog>();
	
	public void logError(String stacktrace, String error, Date time){
		
		ExceptionLog el = errors.get(stacktrace);

		if(el==null){
			el = new ExceptionLog(stacktrace,error,time);
			errors.put(stacktrace, el);
		}else{
			el.addTime(time);
		}
	}
	
	public Collection<ExceptionLog> getErrors(){
		return errors.values();
	}
	
	public void showContents(){
		for (ExceptionLog el : errors.values()) {
			System.out.println("Exception: "+el.getException()+" | Occurrences:  "+Long.toString(el.getCount()));
		}
		
	}
	
	
}
