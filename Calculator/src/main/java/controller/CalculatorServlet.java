package controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

    /**
     * 計算式を配列（同じ名前のパラメータ token の並び）で受け取り、結果をJSONで返す。
     * 例: token=12&token=+&token=3&token=*&token=4
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String[] tokens = request.getParameterValues("token");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(calculate(tokens == null ? List.of() : Arrays.asList(tokens)));
    }

    private String calculate(List<String> tokens) {
        try {
            return "{\"status\":\"ok\",\"result\":\"" + ExpressionCalculator.evaluate(tokens) + "\"}";
        } catch (ArithmeticException e) {
            return "{\"status\":\"divideByZero\"}";
        } catch (ExpressionCalculator.ResultOverflowException e) {
            return "{\"status\":\"overflow\"}";
        } catch (ExpressionCalculator.InvalidExpressionException e) {
            return "{\"status\":\"invalid\"}";
        }
    }
}
