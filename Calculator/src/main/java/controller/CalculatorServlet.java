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
     * Model で計算した結果を文字列（例: "24"）で返す。
     * 計算できなかった場合は、ステータスコード 400 を返す。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String expression = request.getParameter("expression");
        response.setContentType("text/plain; charset=UTF-8");

        try {
            BigDecimal result = ExpressionCalculator.evaluate(expression);
            response.getWriter().write(result.stripTrailingZeros().toPlainString());
        } catch (RuntimeException e) {
            // 0で割った場合や、式が不正な場合
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
