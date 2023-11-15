package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/clear")
public class ClearServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws  ServletException, IOException {
		
		request.setAttribute("result", "");
		
		String view = "/WEB-INF/views/index.jsp";
		request.getRequestDispatcher(view).forward(request, response);
	}
}