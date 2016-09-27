package com.amster.logparser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExceptionLog {

	private String m_Exception="";
	private String m_StackTrace="";
	private int count=0;
	private List <Date> times = new ArrayList<Date>();
	
	public ExceptionLog(String stacktrace, String exception, Date time){
		m_Exception=exception;
		m_StackTrace=stacktrace;
		count++;;
		times.add(time);
	}
	
	public void addTime(Date time){
		count++;
		times.add(time);
	}
	
	public String getException(){
		return m_Exception;
	}
	
	public int getCount(){
		return count;
	}
	
	public String getStackTrace(){
		return m_StackTrace;
	}
	
	public List<Date>getExceptionTimes(){
		return times;
	}
}
