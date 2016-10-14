package com.amster.controller;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.servlet.annotation.MultipartConfig;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amster.db.hibernate.TableLogFile;
import com.amster.db.persistence.HibernateUtil;
import com.amster.logparser.AmsterHouseMaid;
import com.amster.poc.TestProperties;
import com.amster.servlets.AmsterLogFileUploadServlet;

@RestController
@RequestMapping("/rest")
public class AmsterController {

	
    private static  String UPLOAD_DIRECTORY = "\\uploaded_logs";
    private static int RETENTION_DAYS=14;
    
    public AmsterController(){
		Properties prop = new Properties();
		InputStream input = null;


		try {
			input = TestProperties.class.getClassLoader().getResourceAsStream("amster.properties");
			
			
			if(input!=null){
				// load a properties file
				prop.load(input);
				
				
				String propUploadDir =  prop.getProperty("upload_location");
				if(propUploadDir!=null){
					UPLOAD_DIRECTORY = propUploadDir;
				}
				System.out.println("**AMSTER - Upload directory: " +UPLOAD_DIRECTORY);
				
				String propRetDays =  prop.getProperty("retention_days");
				if(propRetDays!=null){
					RETENTION_DAYS = Integer.valueOf(propRetDays);
				}
				System.out.println("**AMSTER - File retention days: " +RETENTION_DAYS);
			
			}
			

		} catch (Exception e){
			e.printStackTrace();
		}

    }
	
	
	@RequestMapping("/logfile-detail")
	public List<?> getLogFileDetails(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM TableLogFile WHERE id = :id");
		    query.setParameter("id", Integer.valueOf(id));
	        
	        hib_Session.getTransaction().commit();
	                
	        return query.list();
	        
		
	}

	
	@RequestMapping("/logfile")
	public List<?> getLogFile(@RequestParam(value = "id",required = false,
	                                                    defaultValue = "0") String id) {
	
		//Before we get the logs, let's check to see if we can clear any old ones out
		AmsterHouseMaid.cleanUpTheHouse(UPLOAD_DIRECTORY, RETENTION_DAYS);
			
		Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	    Query query;
	       
	    hib_Session.beginTransaction();
	        
	    if(id.equals("0")){
	    	query = hib_Session.createQuery("FROM ViewLogFile");
	    }else{
	    	query = hib_Session.createQuery("FROM ViewLogFile WHERE id = :id");
		    query.setParameter("id", Integer.valueOf(id));
	    }

	    hib_Session.getTransaction().commit();
	        
	    return query.list();
		
	}
	
	@RequestMapping("/timeline")
	public List<?> getTimeLine(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM ViewTimeLog where logFile = :id");
		    query.setParameter("id", Integer.valueOf(id));
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
	@RequestMapping("/threads")
	public List<?> getTimeThreads(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM ViewThreadFile where logFile = :id");
		    query.setParameter("id", Integer.valueOf(id));
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
	@RequestMapping("/top-queries")
	public List<?> getTopQueries(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM ViewTopQueries where logFile = :id");
		    query.setParameter("id", Integer.valueOf(id));
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
	@RequestMapping("/diagnostics")
	public List<?> getDiagnostics(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM TableDiagnostic where logFile = :id");
		    query.setParameter("id", Integer.valueOf(id));
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
	@RequestMapping("/exceptions")
	public List<?> getExceptions(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM TableExceptionDetail where logFile = :id order by exceptionCount desc");
		    query.setParameter("id", Integer.valueOf(id));
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
	
}