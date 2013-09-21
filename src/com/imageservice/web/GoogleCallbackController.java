package com.imageservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imageservice.app.service.PicasaClient;

public class GoogleCallbackController extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            message;
    private PicasaClient googleClient;

    public void init() throws ServletException {
        // Do required initialization
        message = "Hello World";
        googleClient = PicasaClient.getInstance();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String code = request.getParameter("code");
        Map<String, Object> tokenMap = googleClient.getAccessTokenMap(code, "http://localhost:8080/gcallback");
        String accessToken = (String)tokenMap.get("access_token");
        request.getSession().setAttribute("G_ACCESS_TOKEN", accessToken);
        
        Map<String, Object> user = googleClient.getUserProfile(accessToken);
        String email = (String)user.get("email");
        request.getSession().setAttribute("GMAIL_ID", email);
        
        request.getRequestDispatcher("/WEB-INF/jsp/imageupload.jsp").forward(request, response);        
        
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println("<h1>" + message + "</h1>");
    }
}