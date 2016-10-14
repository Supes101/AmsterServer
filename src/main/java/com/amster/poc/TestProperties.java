package com.amster.poc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Properties;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import com.amster.servlets.AmsterLogFileUploadServlet;


@MultipartConfig(fileSizeThreshold=1024*1024*0, // 0MB
                 maxFileSize=1024*1024*1000,      // 1GB
                 maxRequestSize=1024*1024*1000)   // 1GB
public class TestProperties {
	
    private static  String UPLOAD_DIRECTORY = "\\uploaded_logs";
    private static int MEMORY_THRESHOLD   = 1024 * 1024 * 0;  // 0MB
    private static long MAX_FILE_SIZE      = AmsterLogFileUploadServlet.class.getAnnotation(MultipartConfig.class).maxFileSize(); 
    private static long MAX_REQUEST_SIZE   = AmsterLogFileUploadServlet.class.getAnnotation(MultipartConfig.class).maxRequestSize();
    
    @SuppressWarnings("unchecked")
    public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue){
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
	
	public static void main(String [] args)
	{
		
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
				
				final MultipartConfig classAnnotation = AmsterLogFileUploadServlet.class.getAnnotation(MultipartConfig.class);
				changeAnnotationValue(classAnnotation, "maxFileSize", MAX_FILE_SIZE);
				changeAnnotationValue(classAnnotation, "maxRequestSize", MAX_REQUEST_SIZE);
			}
			

		} catch (Exception e){
			e.printStackTrace();
		}
		
		System.out.println("COMPLETE");
	}

}
