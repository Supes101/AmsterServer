package com.amster.logparser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Diagnostic {
	
	String m_DiagName;
	List<DiagCounter> m_DiagCounter = new ArrayList<DiagCounter>();
	int m_Highest = Integer.MIN_VALUE;
	Date m_HighestTime;

	public Diagnostic(String name){
		m_DiagName=name;
	}

	public void addValue(int value, Date time){

		if(value>m_Highest){
			m_Highest=value;
			m_HighestTime=time;
		}
		
	
		m_DiagCounter.add(new DiagCounter(value,time));
	}
	
	public String getName(){
		return m_DiagName;
	}
	
	public List<DiagCounter> getDiags(){
		return m_DiagCounter;
	}
	
	public int getHighWatermark(){
		return m_Highest;
	}
	
	public Date getHighWatermarkTime(){
		return m_HighestTime;
	}
	
}
