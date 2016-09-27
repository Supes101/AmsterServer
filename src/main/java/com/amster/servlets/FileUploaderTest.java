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
 
@WebServlet("/uploadservlettest")
@MultipartConfig(fileSizeThreshold=1024*1024*4, // 4MB
                 maxFileSize=1024*1024*1000,      // 1000MB
                 maxRequestSize=1024*1024*1000)   // 1000MB
public class FileUploaderTest extends HttpServlet {
 
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
 
 
        
        response.getWriter().println("Hello there hibernate boy.");
        
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