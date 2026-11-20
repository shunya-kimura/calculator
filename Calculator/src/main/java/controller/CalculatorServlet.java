package controller;

import java.io.IOException;
import java.math.BigDecimal;

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
        String expression = request.getParameter("expression");

        String json;
        try {
            BigDecimal result = ExpressionCalculator.evaluate(expression);
            json = "{\"status\":\"ok\",\"result\":\"" + result.stripTrailingZeros().toPlainString() + "\"}";
        } catch (RuntimeException e) {
            // 0で割った場合や、式が不正な場合はエラーとして返す
            json = "{\"status\":\"error\"}";
        }

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
}
