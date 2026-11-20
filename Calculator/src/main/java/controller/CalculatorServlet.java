package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Calculator;

@WebServlet("/calculator")
public class CalculatorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = "/WEB-INF/views/index.jsp";
        request.getRequestDispatcher(view).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String result1 = request.getParameter("result");

        Double result2 = Calculator.evaluate("result1");

        String view = "/WEB-INF/views/index.jsp";
        request.getRequestDispatcher(view).forward(request, response);
    }
}
