package com.amster.logparser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiagnosticInfo {
	
	public static final String DATE_FORMAT =  "HH:mm:ss";
	
	public static final String DIAGNOSTIC_INFO="*****AmdocsClfy-Message*****Diagnostic Info: ";
	
	private HashMap<String,Diagnostic> m_Diagnostics = new HashMap<String, Diagnostic>();
	

	public void showContents(){
		for(Diagnostic diag: m_Diagnostics.values()){
			System.out.println(diag.getName()+" "+diag.getHighWatermark());
		}
	}
	
	public Collection<Diagnostic> getDiagnostics(){
		return m_Diagnostics.values();
	}
	
	
	public void addDiagnostic(String line){
		
		line=line.substring(0,line.length()-2);
		
		//Remove and weird 'Process Times' messages - we don't need them
		line=line.replaceAll("Process times \\d+.\\d+ Secs CpuUtil\\(\\%\\)=\\d+(\\,|)", "");
		//Remove whitespaces
		line.replaceAll("/^\\s+|\\s+$/g", "");

		
		Date d;
		try {
			
	
			d = new SimpleDateFormat(DATE_FORMAT).parse("00:00:00");
			

			

			Pattern pattern = Pattern.compile("\\d+:\\d+:\\d+");
			Matcher matcher = pattern.matcher(line);
			
			if (matcher.find())
			{
				d = new SimpleDateFormat(DATE_FORMAT).parse(matcher.group());
			}
			
			int i = line.indexOf(DIAGNOSTIC_INFO);
			line=line.substring(i+DIAGNOSTIC_INFO.length());
			
			String[] counters = line.split("\\,");
			
			for(String counter: counters){
				String[] countVal = counter.split("\\=");

			
				Diagnostic dig = m_Diagnostics.get(countVal[0]);
				if(dig==null){
					dig =  new Diagnostic(countVal[0]);
					dig.addValue(Integer.parseInt(countVal[1]), d);
					m_Diagnostics.put(countVal[0], dig);
				}else{
					
					dig.addValue(Integer.parseInt(countVal[1]), d);
				}
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
