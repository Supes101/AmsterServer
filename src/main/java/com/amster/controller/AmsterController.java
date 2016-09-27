package com.amster.controller;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.amster.db.hibernate.TableLogFile;
import com.amster.db.persistence.HibernateUtil;

@RestController
@RequestMapping("/rest")
public class AmsterController {
	@Autowired

	
	@RequestMapping("/logfile-detail")
	public List<?> getLogFileDetails(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM TableLogFile WHERE id = :id");
		    query.setParameter("id", id);
	        
	        hib_Session.getTransaction().commit();
	                
	        return query.list();
		
	}

	
	@RequestMapping("/logfile")
	public List<?> getLogFile(@RequestParam(value = "id",required = false,
	                                                    defaultValue = "0") String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        if(id.equals("0")){
	        	 query = hib_Session.createQuery("FROM ViewLogFile");
	        }else{
		        query = hib_Session.createQuery("FROM ViewLogFile WHERE id = :id");
		        query.setParameter("id", id);
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
		    query.setParameter("id", id);
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
	@RequestMapping("/threads")
	public List<?> getTimeThreads(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM ViewThreadFile where logFile = :id");
		    query.setParameter("id", id);
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
	@RequestMapping("/top-queries")
	public List<?> getTopQueries(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM ViewTopQueries where logFile = :id");
		    query.setParameter("id", id);
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
	@RequestMapping("/diagnostics")
	public List<?> getDiagnostics(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM TableDiagnostic where logFile = :id");
		    query.setParameter("id", id);
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
	@RequestMapping("/exceptions")
	public List<?> getExceptions(@RequestParam(value = "id",required = true) String id) {
		
	       Session hib_Session = HibernateUtil.getSessionFactory().openSession();
	       Query query;
	       
	        hib_Session.beginTransaction();
	        
	        query = hib_Session.createQuery("FROM TableExceptionDetail where logFile = :id");
		    query.setParameter("id", id);
	     

	        hib_Session.getTransaction().commit();
	              	        
	        return query.list();
		
	}
	
}