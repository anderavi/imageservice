package com.imageservice.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.imageservice.app.service.PicasaClient;

public class ImageUploadController extends HttpServlet {

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
        String accessToken = (String)request.getSession().getAttribute("G_ACCESS_TOKEN");
        
        if(accessToken == null) {
            String googleAuthUrl = googleClient.getAuthorizeUrl("http://localhost:8080/gcallback", "/upload");
            response.sendRedirect(googleAuthUrl);
        } else {
            request.getRequestDispatcher("/WEB-INF/jsp/imageupload.jsp").forward(request, response);        
        }
        
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accessToken = (String)request.getSession().getAttribute("G_ACCESS_TOKEN");
        
        ServletFileUpload upload = new ServletFileUpload();
        upload.setSizeMax(500000);
        String contentType = null;
        InputStream imageStream = null;
        try {

            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    imageStream = stream;
                    contentType = item.getContentType();
                    break;
                }
            }
            
        }catch (Exception e) {
            // TODO: handle exception
        }   
        String imageUrl = googleClient.upload(accessToken, "", "zigride", imageStream, contentType);
        
        response.sendRedirect("/?imageUrl="+imageUrl);
    }
}