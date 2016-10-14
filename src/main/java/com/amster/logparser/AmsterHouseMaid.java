package com.amster.logparser;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.amster.db.hibernate.TableLogFile;
import com.amster.db.hibernate.TableThreadFile;
import com.amster.db.hibernate.ViewLogFile;
import com.amster.db.hibernate.ViewThreadFile;
import com.amster.db.persistence.HibernateUtil;

public class AmsterHouseMaid {


	public static void cleanUpTheHouse(String fileLocation, int retention_days){
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        Calendar c = Calendar.getInstance();
	        c.add(Calendar.DATE, -retention_days);
	        
	        //Get Log files older than x days
	        query = hib_Session.createQuery("FROM ViewLogFile WHERE createDate  <:cdate");
	        query.setCalendarDate("cdate", c);
	
	        hib_Session.getTransaction().commit();
	                
	        
	        List<ViewLogFile> list = query.list();
	        
	        for(ViewLogFile vlf : list){
	        	
	        	//Delete the log files
	        	deleteFile(fileLocation+vlf.getRealFileName());
	        	deleteFile(fileLocation+vlf.getOutputFileName());
	        	
	            hib_Session.beginTransaction();
	        	//Get thread files
		        query = hib_Session.createQuery("FROM ViewThreadFile WHERE logFile =:id");
		        query.setParameter("id", vlf.getId());
		        hib_Session.getTransaction().commit();
		                
		        List<ViewThreadFile> thread_list = query.list();
		        
		        for(ViewThreadFile vtf : thread_list){
		        	//Delete the thread files
		        	deleteFile(fileLocation+vtf.getThreadFile());
		        }

	        }
	      
	        
	        hib_Session.beginTransaction();
	      
	        
	        //Delete the whole lot of them 
	        query = hib_Session.createQuery("DELETE FROM TableLogFile WHERE createDate  <:cdate");
	        query.setCalendarDate("cdate", c);
	
	        System.out.println("**AMSTER Cleaned up "+query.executeUpdate()+" logs");
	        
	        hib_Session.getTransaction().commit();
	        
	        hib_Session.close();
	        
	        
	}
	
	private static void deleteFile(String file_name){
    	System.out.println(file_name);
		
		try{

    		File file = new File(file_name);

    		if(file.delete()){
    			//System.out.println(file.getName() + " is deleted!");
    		}else{
    			//System.out.println("Delete operation failed.");
    		}

    	}catch(Exception e){

    		e.printStackTrace();

    	}
	}
	
	public static void main(String [] args)
	{

		AmsterHouseMaid.cleanUpTheHouse("C:\\Tomcat8\\webapps\\ROOT\\uploaded_logs\\",14);
	}
	
}
