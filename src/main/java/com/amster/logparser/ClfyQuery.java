package com.amster.logparser;

import java.util.Date;


public class ClfyQuery  {
	
	String m_SQL="";
	float m_ElapsedTime =0;
	Date m_Time;
	
	public ClfyQuery(float elapsed_time, String SQL, Date time){
		m_SQL=SQL;
		m_ElapsedTime=elapsed_time;
		m_Time=time;
	}

	public float getElapsedTime(){
		return m_ElapsedTime;
	}
	
	public String getSQL(){
		return m_SQL;
	}
	
	public Date getTime(){
		return m_Time;
	}

}
