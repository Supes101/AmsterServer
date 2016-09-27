package com.amster.servlets;
 
import java.io.File;
import java.io.IOException;
 
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.hibernate.Session;

import com.amster.db.hibernate.TableLogFile;
import com.amster.db.persistence.HibernateUtil;
 
@WebServlet("/uploadservlet")
@MultipartConfig(fileSizeThreshold=1024*1024*4, // 4MB
                 maxFileSize=1024*1024*1000,      // 1000MB
                 maxRequestSize=1024*1024*1000)   // 1000MB
public class FileUploader extends HttpServlet {
 
    /**
     * Name of the directory where uploaded files will be saved, relative to
     * the web application directory.
     */
    private static final String SAVE_DIR = "uploadFiles";
     
    /**
     * handles file upload
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	

        String shortName=request.getParameter("short_name");
        String description = request.getParameter("description");
        
    	
    	// gets absolute path of the web application
        String appPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        String savePath = appPath + File.separator + SAVE_DIR;
         
        // creates the save directory if it does not exists
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }
         
       
        String fileName="";
        for (Part part : request.getParts()) {
            fileName = extractFileName(part);
            part.write(savePath + File.separator + fileName);
        }
 
        
    	
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();
        TableLogFile hib_LogFile = new TableLogFile();

        //hib_LogFile.setOutputFileName("TEST");
        hib_LogFile.setRealFileName(fileName);
        hib_LogFile.setShortName(shortName);
        hib_LogFile.setDescription(description);
        hib_LogFile.setCreateDate(Calendar.getInstance().getTime());
        
        
        session.save(hib_LogFile);
        session.getTransaction().commit();
        
        response.getWriter().println("Hello there hibernate boy. Your new ID is" +String.valueOf(hib_LogFile.getId()));
        
       // request.setAttribute("message", "Upload has been done successfully!");
        //getServletContext().getRequestDispatcher("/message.jsp").forward(
         //       request, response);
        
        
    }
 
    /**
     * Extracts file name from HTTP header content-disposition
     */
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
            	System.out.println(s);
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }
}