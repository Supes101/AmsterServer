package com.amster.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amster.servlet.utils.UploadProgressListener;

@WebServlet("/uploadprogress")
public class AmsterUploadProgressServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (session == null) {
			out.println( "{ \"total_bytes\" : \"0\", \"bytes_uploaded\" : \"0\", \"bytes_parsed\" : \"0\", \"upload_percent\" : \"0\", \"parse_percent\" : \"0\", \"message\" : \"Not started\"}");
			
			return;
		}

		UploadProgressListener upProgressListener = (UploadProgressListener) session.getAttribute("UploadProgressListener");
		if (upProgressListener == null) {
			out.println ("{ \"total_bytes\" : \"0\", \"bytes_uploaded\" : \"0\", \"bytes_parsed\" : \"0\", \"upload_percent\" : \"0\", \"parse_percent\" : \"0\", \"message\" : \"Not started\"}");
			return;
		}

		out.println(upProgressListener.getJSON());

	}
	

}
