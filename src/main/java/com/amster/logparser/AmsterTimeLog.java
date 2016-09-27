package com.amster.logparser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AmsterTimeLog {

	private String m_Heading;
	private String m_Description;
	private Date m_Date;
	private String m_HeadingColour;
	private String m_IconColour;
	
	
	
	DateFormat m_DateFormat = new SimpleDateFormat("HH:mm:ss");
	
	public AmsterTimeLog(String heading, String description,Date real_date, String heading_colour,String icon_colour){
		m_Heading=heading;
		m_Description = description;
		m_Date= real_date;
		m_HeadingColour = heading_colour;
		m_IconColour = icon_colour;
	}
	
	public String toString(){
		return m_Heading+" "+m_Description+" "+m_Date.toString();
	}
		
	public String getHeading(){
		return m_Heading;
	}
	
	public String getDescription(){
		return m_Description;
	}
	
	public Date getDate(){
		return m_Date;
	}
	
	public String getTime(){
		return m_DateFormat.format(m_Date);
	}
	
	public String getHeadingColour(){
		return m_HeadingColour;
	}
	
	public String getIconColour(){
		return m_IconColour;
	}
}
