package com.amster.logparser;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ThreadWriter {
	
	private int m_LineCount ;
	private BufferedWriter m_Writer;
	private String m_Path;
	private String m_FileName;
	private int m_FileCount;
	private int m_MaxLines = 0; //0 is switched off
	private String m_FileType=".clog";
	
	public ThreadWriter(String name, String path){
		
		m_FileName = name;
		m_FileCount =1;
		m_LineCount=0;
		m_Path=path;
		
	}
	
	public ThreadWriter(String name, String path, int max_lines){
		
		m_FileName = name;
		m_FileCount =1;
		m_LineCount=0;
		m_Path=path;
		m_MaxLines=max_lines;
		
	}
	
	//Hook method for file header if required
	public void startFile(){	
	}
	
	//Hook method for file footer if required
	public void endFile(){
	}
	
	public void close(){
		try {
			m_Writer.close();
		} catch (IOException e) {
		
		}
	}
	
	public String getFileName(){
		return m_FileName+"_"+String.valueOf(m_FileCount)+m_FileType;
	}
	
	public void write(String line){
		write(line,1);
	}
	
	public void write(String line, int lines){
		m_LineCount = m_LineCount+1;
		
		if(m_Writer==null){
		
			try {
				m_Writer= Files.newBufferedWriter(
						FileSystems.getDefault().getPath(m_Path, m_FileName+"_"+String.valueOf(m_FileCount)+m_FileType), 
						StandardCharsets.UTF_8, 
					    StandardOpenOption.CREATE);
				startFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			m_Writer.write(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Close the file and open a new one if we care about
		//Max Lines
		if(m_MaxLines>0 && m_LineCount>m_MaxLines){
			try {
				endFile();
				m_Writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_Writer =null;
			m_FileCount++;
			m_LineCount=0;
		}
		
		
	}

}