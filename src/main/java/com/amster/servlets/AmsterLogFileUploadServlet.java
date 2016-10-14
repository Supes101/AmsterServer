package com.amster.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import org.hibernate.Session;

import com.amster.db.hibernate.TableLogFile;
import com.amster.db.persistence.HibernateUtil;
import com.amster.logparser.ClfyLogParser;
import com.amster.poc.TestProperties;
import com.amster.servlet.utils.UploadProgressListener;

@WebServlet("/uploadlog")
@MultipartConfig(fileSizeThreshold=1024*1024*0, // 0MB
                 maxFileSize=1024*1024*1000,      // 1GB
                 maxRequestSize=1024*1024*1000)   // 1GB
public class AmsterLogFileUploadServlet extends HttpServlet  {

	private static final long serialVersionUID = 1L;
	
	// location to store file uploaded
    private static  String UPLOAD_DIRECTORY = "\\uploaded_logs";
 
    // upload settings
    private static  int MEMORY_THRESHOLD   = 1024 * 1024 * 0;  // 0MB
    private static long MAX_FILE_SIZE      = AmsterLogFileUploadServlet.class.getAnnotation(MultipartConfig.class).maxFileSize(); 
    private static long MAX_REQUEST_SIZE   = AmsterLogFileUploadServlet.class.getAnnotation(MultipartConfig.class).maxRequestSize();
  

  
	public AmsterLogFileUploadServlet(){
		
		Properties prop = new Properties();
		InputStream input = null;


		try {
			input = TestProperties.class.getClassLoader().getResourceAsStream("amster.properties");
			
			
			if(input!=null){
				// load a properties file
				prop.load(input);
				
				String propMemThresh =  prop.getProperty("memory_threshold");
				if(propMemThresh!=null){
					MEMORY_THRESHOLD = Integer.valueOf(propMemThresh);
				}
				System.out.println("**AMSTER - Memory Threshold: " +MEMORY_THRESHOLD);
				
				String propUploadDir =  prop.getProperty("upload_location");
				if(propUploadDir!=null){
					UPLOAD_DIRECTORY = propUploadDir;
				}
				System.out.println("**AMSTER - Upload directory: " +UPLOAD_DIRECTORY);
				
				String propMaxFile =  prop.getProperty("max_file_size");
				if(propMaxFile!=null){
					MAX_FILE_SIZE = Long.valueOf(propMaxFile);
				}
				System.out.println("**AMSTER - Max File Size: " +MAX_FILE_SIZE);
				
				String propReqSize =  prop.getProperty("max_request_size");
				if(propReqSize!=null){
					MAX_REQUEST_SIZE = Long.valueOf(propReqSize);
				}
				System.out.println("**AMSTER - Max Request Size: " +MAX_REQUEST_SIZE );
				
				
				//Change the annotation class so that it uses the values from the properties file /flex
				final MultipartConfig classAnnotation = AmsterLogFileUploadServlet.class.getAnnotation(MultipartConfig.class);
				changeAnnotationValue(classAnnotation, "maxFileSize", MAX_FILE_SIZE);
				changeAnnotationValue(classAnnotation, "maxRequestSize", MAX_REQUEST_SIZE);
			}
			

		} catch (Exception e){
			e.printStackTrace();
		}

		
	}
	
    @SuppressWarnings("unchecked")
    private static Object changeAnnotationValue(Annotation annotation, String key, Object newValue){
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        f.setAccessible(true);
        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) f.get(handler);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        Object oldValue = memberValues.get(key);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put(key,newValue);
        return oldValue;
    }
    
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (session == null) {
			out.print("{\"error\" : \"null sesh\"}");
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
 
        
        // creates the directory if it does not exist
        File uploadDir = new File(UPLOAD_DIRECTORY);
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
                		}
                	}
                	
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        fileName = new File(item.getName()).getName();
                        System.out.println("**AMSTER Uploading file: "+fileName);
                        String filePath = UPLOAD_DIRECTORY + File.separator + fileName;

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

            hib_LogFile.setRealFileName(fileName);
            hib_LogFile.setShortName(shortName);
            hib_LogFile.setDescription(description);
            hib_LogFile.setCreateDate(Calendar.getInstance().getTime());
            
            
            hib_Session.save(hib_LogFile);
            hib_Session.getTransaction().commit();
            
            hib_Session.close();
            
            //Go parse the file
            ClfyLogParser clp = new ClfyLogParser(hib_LogFile,uploadProgressListener,fileName,String.valueOf(hib_LogFile.getId()),UPLOAD_DIRECTORY,stackB,ignoreList );
            clp.processFile();
            
            System.out.println("**AMSTER Finished uploading a file");
            response.sendRedirect("uploaded.jsp");
            
        } catch (Exception ex) {
        	ex.printStackTrace();
        	String message = ex.toString();
        	response.sendRedirect("error.jsp?message="+URLEncoder.encode(message,"UTF-8"));

        }
        	
    }
	
	
}
