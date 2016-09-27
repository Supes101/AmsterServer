package com.amster.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.amster.db.hibernate.TableLogFile;
import com.amster.db.persistence.HibernateUtil;
import com.amster.logparser.ClfyLogParser;
import com.amster.servlet.utils.UploadProgressListener;


@WebServlet("/uploadlog")
@MultipartConfig(fileSizeThreshold=1024*1024*4, // 4MB
                 maxFileSize=1024*1024*1000,      // 1000MB
                 maxRequestSize=1024*1024*1000)   // 1000MB
public class AmsterLogFileUploadServlet extends HttpServlet  {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// location to store file uploaded
    private static final String UPLOAD_DIRECTORY = "uploaded_logs";
 
    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 0;  // 0MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 1500; // 1500MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 1500; // 1500MB
    

    
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (session == null) {
			out.print("{\"error\" : \"null sesh\"}");
			//out//.println( "{ \"total_bytes\" : \"0\", \"bytes_uploaded\" : \"0\", \"bytes_parsed\" : \"0\", \"upload_percent\" : \"0\", \"parse_percent\" : \"0\", \"message\" : \"Not started\"}");
			//out.println(uploadProgressListener.getJSON());
			return;
		}

		
		UploadProgressListener uploadProgressListener = (UploadProgressListener) session.getAttribute("UploadProgressListener");
		
		if (uploadProgressListener == null) {
		 	uploadProgressListener = new UploadProgressListener();
		 	session.setAttribute("UploadProgressListener", uploadProgressListener);
		}

		out.println(uploadProgressListener.getJSON());
	}
	
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("HERE");
		String fileName = "";
		String shortName="";
		String description = "";
		String allStack="";
		String ignoreList="";
		Boolean stackB = false;
		

		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		
		// checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            PrintWriter writer = response.getWriter();
            writer.println("Error: Form must has enctype=multipart/form-data.");
            writer.flush();
            return;
        }
 
        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
         
        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);
         
        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        // constructs the directory path to store upload file
        // this path is relative to application's directory
       // String uploadPath = getServletContext().getRealPath("")
       //         + File.separator + UPLOAD_DIRECTORY;
        
     
        String uploadPath = "C:\\Tomcat8\\webapps\\ROOT\\uploaded_logs\\";
        
        
        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        
    	HttpSession session = request.getSession();
    	
       // uploadProgressListener.clear();
		UploadProgressListener uploadProgressListener = (UploadProgressListener) session.getAttribute("UploadProgressListener");
		
		
		if (uploadProgressListener == null) {
		 	uploadProgressListener = new UploadProgressListener();
		 	session.setAttribute("UploadProgressListener", uploadProgressListener);
		}
        
		uploadProgressListener.clear();
		upload.setProgressListener(uploadProgressListener);

 
        try {
            // parses the request's content to extract file data
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);
 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                	
                	if(item.isFormField()){
                		if(item.getFieldName().equals("short_name")){
                			shortName=item.getString();
                		}
                		if(item.getFieldName().equals("description")){
                			description=item.getString();
                		}
                		
                		if(item.getFieldName().equals("allstack")){
                			allStack=item.getString();
                			if (allStack.equals("allStack")){
                				stackB=true;
                			}
                		}
                		
                		if(item.getFieldName().equals("ignorelist")){
                			ignoreList=item.getString();
                			System.out.println("igList "+ignoreList);
                		}
                	}
                	
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        fileName = new File(item.getName()).getName();
                        System.out.println("FILE NAME: "+fileName);
                        String filePath = uploadPath + File.separator + fileName;
                        System.out.println("FILE PATH :" +filePath);
                        File storeFile = new File(filePath);
 
                        // saves the file on disk
                        item.write(storeFile);
                       // out.println("{\"message\" : \"success\" }");
                    }
                }
            }
            
            Session hib_Session = HibernateUtil.getSessionFactory().openSession();

            hib_Session.beginTransaction();
            TableLogFile hib_LogFile = new TableLogFile();

            //hib_LogFile.setOutputFileName("TEST");
            hib_LogFile.setRealFileName(fileName);
            hib_LogFile.setShortName(shortName);
            hib_LogFile.setDescription(description);
            hib_LogFile.setCreateDate(Calendar.getInstance().getTime());
            
            
            hib_Session.save(hib_LogFile);
            hib_Session.getTransaction().commit();
            
            hib_Session.close();
            
            //Go parse the file
            ClfyLogParser clp = new ClfyLogParser(hib_LogFile,uploadProgressListener,fileName,String.valueOf(hib_LogFile.getId()),uploadPath,stackB,ignoreList );
            clp.processFile();
            
    		//out.println("{\"message\" : \"success\" }");
            System.out.println("Finished uploading");
            response.sendRedirect("uploaded.jsp");
            
        } catch (Exception ex) {
        	ex.printStackTrace();
        	String message = ex.toString();
        	response.sendRedirect("error.jsp?message="+URLEncoder.encode(message,"UTF-8"));
        	//out.println("{\"error\" : \""+ex.toString()+"\" }");

        }
        


		
    }
	

	
}
