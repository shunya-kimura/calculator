package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExpressionCalculator;

@WebServlet("/calculator")
public class CalculatorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }

    /** 計算式を受け取り、結果をJSONで返す。 */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(calculate(request.getParameter("expression")));
    }

    private String calculate(String expression) {
        try {
            return "{\"status\":\"ok\",\"result\":\"" + ExpressionCalculator.evaluate(expression) + "\"}";
        } catch (ArithmeticException e) {
            return "{\"status\":\"divideByZero\"}";
        } catch (ExpressionCalculator.ResultOverflowException e) {
            return "{\"status\":\"overflow\"}";
        } catch (ExpressionCalculator.InvalidExpressionException e) {
            return "{\"status\":\"invalid\"}";
        }
    }
}
