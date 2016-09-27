package com.amster.logparser;

import java.util.HashMap;



public class ThreadWriters {
	
	//This hashmap contains references to all the current open files
	private HashMap<String, ThreadWriter> m_ThreadWriters = new HashMap<String,ThreadWriter>();
	private String m_Path;
	
	public ThreadWriters(String path){
		m_Path=path;
	}
	
	public void writeToFile(String id,String line){
		
		
		ThreadWriter tw = m_ThreadWriters.get(id);
		
		if(tw == null){
			tw  = new ThreadWriter(id,m_Path);
			m_ThreadWriters.put(id, tw);
		}
		
		tw.write(line);
		
	}
	
	public void writeToFile(String id,String line, int lines){
		
		
		ThreadWriter tw = m_ThreadWriters.get(id);
		
		if(tw == null){
			tw  = new ThreadWriter(id,m_Path);
			m_ThreadWriters.put(id, tw);
		}
		
		tw.write(line,lines);
		
	}
	
	public String getFileName(String id){
		ThreadWriter tw = m_ThreadWriters.get(id);
		
		if(tw == null){
			return "";
		}
		
		return tw.getFileName();
		
	}
	
	public void close(){
		for(ThreadWriter tw : m_ThreadWriters.values()){
			tw.close();
		}
	}
	
}