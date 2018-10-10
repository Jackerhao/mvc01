package com.home.mvcapp.servlet;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.home.mvcapp.dao.factory.CustomerDAOFactory;



//,load-on-startup=1
@WebServlet(name="InitServlet",urlPatterns="/InitServlet",
       displayName="InitServlet",
      description="" ,loadOnStartup=1)
public class InitServlet extends HttpServlet {
  
	private static final long serialVersionUID = 1L;
	
    public void init() throws ServletException {
    	CustomerDAOFactory.getInstance().setType("jdbc");
    	
    	InputStream in=
    	getServletContext().getResourceAsStream("/WEB-INF/classes/switch.properties");
    	Properties properties = new Properties();
    	
    	try {
		 properties.load(in);
		 String type = properties.getProperty("type");
		 CustomerDAOFactory.getInstance().setType(type);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
  
}
