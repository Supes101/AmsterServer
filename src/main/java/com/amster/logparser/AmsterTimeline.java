package com.amster.logparser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class AmsterTimeline {
	
	public static final String DATE_FORMAT =  "HH:mm:ss";
	
	private Date m_LogStart;
	private List<Date> m_TimeRange; 
	private int[] m_ErrorCount;
	private TreeMap<Date, ArrayList<AmsterTimeLog>> m_TimeLogs = new TreeMap<Date,ArrayList<AmsterTimeLog>>();
	
	
	public AmsterTimeline(){
		try {
			m_LogStart = new SimpleDateFormat(DATE_FORMAT).parse("00:00:00");
			setTimeRange();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
	public TreeMap<Date, ArrayList<AmsterTimeLog>>  getTimeLogs(){
		return m_TimeLogs;
	}
	
	public void setLogStartTime(Date log_start){
		m_LogStart = log_start;
		setTimeRange();
	}
	
	private void setTimeRange(){
		
		//This function creates 24 hours of times starting from the log_file start
		//and spaced out every 15 minutes
		m_TimeRange =  new ArrayList<Date>();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(m_LogStart);
		
		m_TimeRange.add(cal.getTime());
		
		for(int i=0; i<96;i++){
			cal.add(Calendar.MINUTE, 15);
			m_TimeRange.add(cal.getTime());
			//System.out.println(cal.getTime());
		}
		
		
		m_ErrorCount = new int[96];
	}

	private boolean isWithinRange(Date check, Date start, Date end){
		

		return !(check.before(start) || check.after(end));
	}
	
	private Date getLogKey(Date check_date){
		

		for(int i=0;i<m_TimeRange.size();i++){
			Date s = m_TimeRange.get(i);
			Calendar c = Calendar.getInstance();
			c.setTime(s);
			c.add(Calendar.MINUTE, 15);
			c.add(Calendar.MILLISECOND, -1);
			Date e =c.getTime();
			if(isWithinRange(check_date,s,e)){
				return s;
			}
		}
		
		return m_LogStart;
	}
	
	private int getDateIndex(Date check_date){
		

		for(int i=0;i<m_TimeRange.size();i++){
			Date s = m_TimeRange.get(i);
			Calendar c = Calendar.getInstance();
			c.setTime(s);
			c.add(Calendar.MINUTE, 15);
			c.add(Calendar.MILLISECOND, -1);
			Date e =c.getTime();
			if(isWithinRange(check_date,s,e)){
				return i;
			}
		}
		
		return 0;
	}
	
	public void addTimeLineEntry(String heading, String desc, Date time, String heading_colour, String icon_colour){
		
		AmsterTimeLog logEntry =  new AmsterTimeLog(heading,desc,time, heading_colour, icon_colour);
		
		Date timeline = getLogKey(time);
		
		ArrayList<AmsterTimeLog> atl = m_TimeLogs.get(timeline);
		
		if(atl==null){
			atl = new ArrayList<AmsterTimeLog>();
			atl.add(logEntry);
			m_TimeLogs.put(timeline, atl);
		}else{
			atl.add(logEntry);
		}
		
	}
	
	public void addErrorCountToTimeLine(){
		for(int i=0;i<m_ErrorCount.length;i++){
			if(m_ErrorCount[i]>0){
				addTimeLineEntry(Integer.toString(m_ErrorCount[i])+" errors recorded","",m_TimeRange.get(i), "red", "red");
			}
		}
	}
	
	public void addError(Date time){
		m_ErrorCount[getDateIndex(time)]++;
	}
	
	public void showContents(){
		
		addErrorCountToTimeLine();
		
		for(Date d : m_TimeLogs.keySet()){
			System.out.println(d);
			ArrayList<AmsterTimeLog> logs = m_TimeLogs.get(d);
			for(AmsterTimeLog log: logs){
				System.out.println("    "+log.getHeading());
			}
		}
	}
	

}
