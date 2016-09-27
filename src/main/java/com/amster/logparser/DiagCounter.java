package com.amster.logparser;

import java.util.Date;

public class DiagCounter {
	
	private int m_Value;
	private Date m_Time;

	public DiagCounter (int value, Date time){
		m_Value=value;
		m_Time=time;
	}
	
	public int getValue(){
		return m_Value;
	}
	
	public Date getTime(){
		return m_Time;
	}
}
