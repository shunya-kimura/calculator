package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExpressionCalculator;

/**
 * 電卓の画面表示と計算の受け付けを行う Servlet（Controller）。
 */
@WebServlet("/calculator")
public class CalculatorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * 電卓の画面（index.jsp）を表示する。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }

    /**
     * 計算式の文字列（パラメータ expression。例: "12+3*4"）を受け取り、
     * Model で計算した結果を JSON で返す。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String expression = request.getParameter("expression");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(calculate(expression));
    }

    /**
     * Model を呼び出して計算し、結果またはエラーの種類を JSON 文字列にする。
     *
     * @param expression 計算式の文字列
     * @return 画面（JS）に返す JSON 文字列
     */
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
