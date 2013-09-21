package com.imageservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.util.ServiceException;
import com.imageservice.app.service.PicasaClient;

public class HomeController extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            message;
    
    private PicasaClient googleClient;

    public void init() throws ServletException {
        googleClient = PicasaClient.getInstance();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accessToken = (String)request.getSession().getAttribute("G_ACCESS_TOKEN");
        
        if(accessToken != null) {
            try {
              List<List<String>> images =  googleClient.getImages(accessToken, "zigride");
              request.setAttribute("images", images);
            } catch (ServiceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 
        
        request.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(request, response);  
        
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println("<h1>" + message + "</h1>");
    }
}