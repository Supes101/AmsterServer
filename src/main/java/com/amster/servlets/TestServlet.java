package com.amster.servlets;


	
import java.io.IOException;
 


import java.util.Calendar;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.amster.db.persistence.HibernateUtil;
import com.amster.db.hibernate.*;
 
@WebServlet("/processForm")
public class TestServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	
    	
    	
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();
        TableLogFile hib_LogFile = new TableLogFile();

        hib_LogFile.setOutputFileName("TEST");
        hib_LogFile.setRealFileName("RAR");
        hib_LogFile.setShortName("Whoop");
        hib_LogFile.setCreateDate(Calendar.getInstance().getTime());
        

        
        session.save(hib_LogFile);
        session.getTransaction().commit();
        
        response.getWriter().println("Hello there hibernate boy. Your new ID is" +String.valueOf(hib_LogFile.getId()));
    }
}