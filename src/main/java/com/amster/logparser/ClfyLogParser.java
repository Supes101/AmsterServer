package com.amster.logparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import com.amster.db.hibernate.TableDiag;
import com.amster.db.hibernate.TableDiagInfo;
import com.amster.db.hibernate.TableException;
import com.amster.db.hibernate.TableExceptionTimes;
import com.amster.db.hibernate.TableLogFile;
import com.amster.db.hibernate.TableThreadFile;
import com.amster.db.hibernate.TableTimeLog;
import com.amster.db.hibernate.TableTimeline;
import com.amster.db.hibernate.TableTopQueries;
import com.amster.db.persistence.HibernateUtil;
import com.amster.servlet.utils.UploadProgressListener;
import com.amster.utils.AmsterUtils;


public class ClfyLogParser {
	
	public static final String LOG_FILE_STARTED = "Log file started at";
	public static final String START_TIME_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";
	public static final String LOG_TIME_PATTERN =  "[0-9]{4}\\:[0-9]{2}\\:[0-9]{2}\\.[0-9]{3}\\ [0-9]{1,9}|[0-9]{4}\\:[0-9]{2}\\:[0-9]{2}\\.[0-9]{3}\\ -[0-9]{1,9}";
	public static final String EXCEPTION_PATTERN="\\(.+\\.java\\:\\d+\\)|\\(Unknown Source\\)|\\(Native Method\\)";
	public static final String DATE_FORMAT =  "HH:mm:ss";
	public static final String SQL_BATCH_START =  "SQL Batch started";
	public static final String BIND_VARIABLES =  "----BindVariables--";
	public static final String SQL_ELAPSED_TIME =  "Elapsed time of";
	public static final String DIAGNOSTIC_INFO="*****AmdocsClfy-Message*****Diagnostic Info:";
	
	
	private String m_InputFileName="";
	private String m_OutputFilePrefix="";
	private String m_Path = "";
	
	private Pattern m_StartTimePattern = Pattern.compile(START_TIME_PATTERN);
	private Pattern m_LogTimePattern = Pattern.compile(LOG_TIME_PATTERN);
	private Pattern m_ExceptionPattern = Pattern.compile(EXCEPTION_PATTERN);
	
	DateFormat m_DateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private boolean m_FoundStartTime = false;
	private Date m_LogStart;
	private Date m_CurrentTime;
	private String m_CurrentThreadID ="";
	
	private String m_SQLBatch="";
	private String m_bindVarSection = "";
	private int m_SQLLineCount=0;
	private Boolean m_SQLMode = false;
	private Boolean m_bindVarMode=false;
	private List<String> m_bindVars = new ArrayList<String>();
	
	private Boolean m_ExceptionMode = false;
	private String m_CurrentException = "";
	private String m_StackTrace = "";
	
	private boolean m_RemoveStacks = false;
	private String m_ExceptionIgnoreList = "";
	
	private ThreadWriters m_Files;
	private ExceptionLogs m_Errors;
	private TopQueries m_Queries;
	private AmsterTimeline m_TimeLine;
	private DiagnosticInfo m_DiagInfo = new DiagnosticInfo();
	
	private TableLogFile m_HibLogFile; 
	
	private List<String> m_ThreadList = new ArrayList<String>();
	UploadProgressListener m_UploadProgressListener;

	
	public ClfyLogParser(TableLogFile hib_logfile,UploadProgressListener uploadProgressListener, String inputfile, String outputfile_prefix, String path, boolean remove_all_stacks, String exception_ignorelist){
		m_InputFileName=inputfile;
		m_OutputFilePrefix=outputfile_prefix;
		m_Path = path;
		m_UploadProgressListener = uploadProgressListener;
		
		m_HibLogFile=hib_logfile;

        //Start with midnight until the real time is found in the logs
		try {
			m_LogStart = new SimpleDateFormat(DATE_FORMAT).parse("00:00:00");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		m_Files = new ThreadWriters(m_Path);
		m_Errors = new ExceptionLogs();
		m_Queries = new TopQueries(20);
		m_TimeLine = new AmsterTimeline();
		
		m_RemoveStacks=remove_all_stacks;
		m_ExceptionIgnoreList=exception_ignorelist;
    
	}
	
	
	
	//Does the current line mean we've hit a SQL batch
	private void isLineSQL(String line){
		
	   	if (line.contains(SQL_BATCH_START)){
			m_SQLMode=true;
			m_bindVarMode=false;
			m_SQLBatch=line;
			m_bindVarSection="";
			m_bindVars = new ArrayList<>();
			m_SQLLineCount=0;
    	}
			
	}
	
	//Does the current line indicate an exception
	private void isLineException(String line, String previous){
		Matcher m = m_ExceptionPattern.matcher(line);
    	
        if(m.find()){
        	//stackTrace = previousLine+line;
        	m_ExceptionMode=true;
        	m_CurrentException = previous;
        	m_StackTrace = previous+line;
 
        }


	}
	
	private void processDiagnostics(String line){
		if(line.contains(DIAGNOSTIC_INFO)){
			m_DiagInfo.addDiagnostic(line);
		}
	}
	
	private void getLogFileStartTime(String line){
		
		if(line.contains(LOG_FILE_STARTED)){
	   		Matcher m = m_StartTimePattern.matcher(line);
   		 
			if (m.find()){ 
    		    try {
					m_LogStart = new SimpleDateFormat(DATE_FORMAT).parse(m.group());
					m_FoundStartTime=true;
					m_TimeLine.setLogStartTime(m_LogStart);
					System.out.println(LOG_FILE_STARTED+m_LogStart);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		   
			}
   	 	}
	}
	
	private String getTimeAndThreadInfo(String line){
		
		Matcher m = m_LogTimePattern.matcher(line);		            
    	
		if (m.find()) {
        	// Found a elapsed time and thread id, i.e. 0004:34:34.953 2516
    		String[] threadInfo =m.group().split("\\s+");
    		m_CurrentThreadID = threadInfo[1].replaceAll("-", "");
    		
    		
    		//Maintain a list of threads
    		if(!m_CurrentThreadID.equals("") && !m_ThreadList.contains(m_CurrentThreadID)){
    			m_ThreadList.add(m_CurrentThreadID);
    		}
    		
        	//Convert elapsed time to local time
    		Calendar cal = Calendar.getInstance();
    		String[] timeStamp = threadInfo[0].replace(":", ".").split("\\.");
    
			cal.setTime(m_LogStart); 
			cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeStamp[0])); 
			cal.add(Calendar.MINUTE, Integer.parseInt(timeStamp[1])); 
			cal.add(Calendar.SECOND, Integer.parseInt(timeStamp[2]));
			
			m_CurrentTime=cal.getTime();
		
			line = new  SimpleDateFormat(DATE_FORMAT).format(cal.getTime())+"."+timeStamp[3]+" "+line;	
			
		}
		
		return line;
	}
	
	public void processException(String line){

		String resultString = line.replaceAll("\\p{C}", "");

		if(resultString.equals("")){
			m_ExceptionMode=false;
			m_Errors.logError(m_StackTrace,m_CurrentException, m_CurrentTime);
		}else{
			m_StackTrace=m_StackTrace+line;
		}
	}
	
	public void createBindVarArray(){
		
		//System.out.println(m_bindVarSection);
		
		//Build up an array of the bind variables
		String[] bindvs = m_bindVarSection.split(",");
		

		for ( String bv: bindvs ){
			//System.out.println(bv);
			int eq = bv.indexOf("=");
			if(eq>0){
				
				String bindVariable = "";
				if(bv.length()>eq){
					
					bindVariable=bv.substring(eq+1);
					//System.out.println("Variable: "+bindVariable);
				}
				//This is the bind variable type
				String bvType = bv.substring(eq-2,eq-1);
				
				if(bvType.equals("7") || bvType.equals("8")){
					//Wrap varchars in quotes
					m_bindVars.add("'"+bindVariable+"'");
				}else{
					m_bindVars.add(bindVariable);
				}
			}

			
		}
		
	}
	
	public void processSQL(String line){
		
		
		m_SQLLineCount++;
		
		
		if(!m_bindVarMode){ 
			//Build up the SQL String
			m_SQLBatch=m_SQLBatch+line;
		}else{
			
			if(!line.contains("JDBC") && line.contains(SQL_ELAPSED_TIME) ){
				
				m_SQLMode=false; //This is the end of the SQL batch
    			m_bindVarMode=false; 
    			
    			float elTime = Float.valueOf(line.substring(SQL_ELAPSED_TIME.length()));
    			
    			createBindVarArray();
    			
    			for (String bindvar : m_bindVars){

    				m_SQLBatch = m_SQLBatch.replaceFirst("[?]", Matcher.quoteReplacement(bindvar));
    				
    			}
    			
    			m_SQLBatch=m_SQLBatch+m_bindVarSection;

	        	m_Files.writeToFile(m_OutputFilePrefix+"_main",m_SQLBatch,m_SQLLineCount);
	        	if(!m_CurrentThreadID.equals("")){
	        		m_Files.writeToFile(m_OutputFilePrefix+"_thread_"+m_CurrentThreadID, m_SQLBatch,m_SQLLineCount);
	        	}
	        	
	        	m_SQLBatch=m_SQLBatch+m_bindVarSection+line;
	        	
	        	m_Queries.AddQuery(m_SQLBatch, elTime, m_CurrentTime);
	        	
			}else{
				
				m_bindVarSection=m_bindVarSection+line;
	    		
				
			}
			
		}
		
		if(line.contains(BIND_VARIABLES)){
			m_bindVarMode=true;
		}
		

	}
	
	public boolean canOutputStack(){
		
		//If an exception wasn't found, then of course we can output
		if(!m_ExceptionMode){
			return true;
		}
		
		//If remove stacks is switched on, don't output stacks
		if(m_RemoveStacks){
			return false;
		}
		
		//If the ignore list is empty, output the stacks
		if(m_ExceptionIgnoreList.equals("")){
			return true;
		}
		
		Pattern eRegPat = Pattern.compile(m_ExceptionIgnoreList);
		Matcher m = eRegPat.matcher(m_CurrentException);	
		
		if( m.find()){
			return false;
		}
		
		return true;
		
	}
		

	
	public void processFile(){
		
		Charset chars;
			
		try {
			
			chars = AmsterUtils.getFileCharEncoding(m_Path,m_InputFileName); 
			
			BufferedReader reader = 
					Files.newBufferedReader(
					FileSystems.getDefault().getPath(m_Path, m_InputFileName), chars);
			
			String line;
			String previousLine="";
			Long cnt = 0l;
	
	        System.out.println("Started");
	        while ( (line = reader.readLine()) != null ){
	        	cnt++;
	        	//System.out.println(cnt);
	        	m_UploadProgressListener.updateParse(line.getBytes(chars).length);
	        	
	        	line=line+System.getProperty("line.separator");
	
	        	
	       		//Check the start of the log file to get the local time
        		if(!m_FoundStartTime){
	        		getLogFileStartTime(line);
	        	}
	        		        	
	        	if(m_SQLMode){
	        		processSQL(line);
	        	}else{
	        		if(m_ExceptionMode){
	        			processException(line);
	        		}else{
	        			//Check to see if this is the start of a SQL Batch
		        		isLineSQL(line);
		        		isLineException(line,previousLine);
		           		//If this line contains a time stamp, convert it to local time
			        	line = getTimeAndThreadInfo(line);
			        	processDiagnostics(line);
	        		}
	        	}
	        	
	        	
	        	
	        	//Write the the line to file
	        	if(!m_SQLMode && canOutputStack()){
		        	m_Files.writeToFile(m_OutputFilePrefix+"_main",line);
		        	if(!m_CurrentThreadID.equals("")){
		        		m_Files.writeToFile(m_OutputFilePrefix+"_thread_"+m_CurrentThreadID, line);
		        	
		        	}
	        	}

	        	previousLine=line;
	        
	        }

	        m_Files.close();
	        reader.close();
	        
	        //m_Errors.showContents();
	       // m_Queries.showContents();
	        //m_DiagInfo.showContents();
	        
	        
	        //Loop the the Exception list and update the timetime
	        for(ExceptionLog el : m_Errors.getErrors()){
	        	for(Date d: el.getExceptionTimes()){
	        		m_TimeLine.addError(d);
	        	}
	        }
	        
	        //Loop though the Diagnostic list and update the timeline
	        for(Diagnostic diag: m_DiagInfo.getDiagnostics() ){
	        	if(diag.getHighWatermark()>0){
	        			m_TimeLine.addTimeLineEntry("High water mark for diagnostic - "+diag.getName()+" (reached : "+String.valueOf(diag.getHighWatermark())+")","" , diag.getHighWatermarkTime(),"blue","green");	
	        	}
	        	
	        }
	        
	        
	        //Loop through the slow query list and update the timeline
	        for(int i =0; i< m_Queries.getList().size();i++){
	        	ClfyQuery q = m_Queries.getList().get(i);
	        	m_TimeLine.addTimeLineEntry("The #"+Integer.toString(i+1)+" slowest query occurred with an elapsed time of "+Float.toString(q.getElapsedTime()), q.getSQL(), q.getTime(), "purple", "blue");
	        }
	        
	       // m_TimeLine.showContents();
	        
	        m_TimeLine.addErrorCountToTimeLine();
	        
	        Session hib_Session = HibernateUtil.getSessionFactory().openSession();

            hib_Session.beginTransaction();
            m_HibLogFile.setOutputFileName(m_Files.getFileName(m_OutputFilePrefix+"_main"));
            hib_Session.update(m_HibLogFile);
            
            //Create DB entries for each thread
            for(String thread: m_ThreadList){
            	TableThreadFile tf = new TableThreadFile(thread,m_Files.getFileName(m_OutputFilePrefix+"_thread_"+thread),m_HibLogFile);
            	hib_Session.save(tf);
            }
            
  
            //Create DB entries for the dodgy SQL
            int q_order=0;
            for(ClfyQuery q: m_Queries.getList()){
            	q_order++;
            	TableTopQueries tq = new TableTopQueries(AmsterUtils.truncateForText(q.getSQL(),65000),q.getElapsedTime(), m_DateFormat.format(q.getTime()),q_order,m_HibLogFile);
            	hib_Session.save(tq);
            }
            
            //Add the exceptions to the DB too
            for(ExceptionLog el: m_Errors.getErrors()){
            	TableException ex = new TableException(el.getException(),el.getStackTrace(),el.getCount(),m_HibLogFile);
            	hib_Session.save(ex);
            	for(Date d: el.getExceptionTimes()){
            		TableExceptionTimes et = new TableExceptionTimes(m_DateFormat.format(d),ex);
            		hib_Session.save(et);
            	}
            }
            
            //Dont forget the diagnostics
            for(Diagnostic dig : m_DiagInfo.getDiagnostics()){
     	
            	TableDiagInfo dInf = new TableDiagInfo(dig.getName(),dig.getHighWatermark(),m_DateFormat.format(dig.getHighWatermarkTime()),m_HibLogFile);
            	hib_Session.save(dInf);
            	
            	for(DiagCounter dc: dig.getDiags()){
            		TableDiag diag = new TableDiag(dc.getValue(),m_DateFormat.format(dc.getTime()),dInf);
            		hib_Session.save(diag);
            	}
            }
            
            //Finally, add the timeline
            for(Map.Entry<Date,ArrayList<AmsterTimeLog>> entry : this.m_TimeLine.getTimeLogs().entrySet()) {
            	  Date timelineDate = entry.getKey();
            	  ArrayList<AmsterTimeLog> logs = entry.getValue();
            	  for(AmsterTimeLog atl: logs){
            		  TableTimeLog ttl = new TableTimeLog(atl.getHeading(),atl.getDescription(),atl.getDate(),atl.getTime(),atl.getHeadingColour(), atl.getIconColour(),m_HibLogFile);
            		  hib_Session.save(ttl);
            	  }
            }
            
            
            hib_Session.getTransaction().commit();
	        
            hib_Session.close();
            
	        
		} catch (IOException ioe) {
			ioe.printStackTrace();
	    }       

	}




}
