package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/calculator")
public class CalculatorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = "/WEB-INF/views/index.jsp";
        request.getRequestDispatcher(view).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String number1 = request.getParameter("num1");
        String number2 = request.getParameter("num2");
        String operator = request.getParameter("operator");

        double num1 = 0;
        double num2 = 0;
        double result = 0;

        if (number1 != null && !number1.isEmpty() && number2 != null && !number2.isEmpty() && operator != null) {
            try {
                num1 = Double.parseDouble(number1);
                num2 = Double.parseDouble(number2);

                switch (operator) {
                    case "+":
                        result = num1 + num2;
                        break;
                    case "-":
                        result = num1 - num2;
                        break;
                    case "×":
                        result = num1 * num2;
                        break;
                    case "÷":
                        if (num2 != 0) {
                            result = num1 / num2;
                        } else {
                            request.setAttribute("result", "0で割ることはできません");
                        }
                        break;
                    default:
                        request.setAttribute("result", "演算子が無効です");
                        break;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("result", "入力値が無効です");
            }
        } else {
            request.setAttribute("result", "数値を入力してください");
        }

        request.setAttribute("result", result);

        String view = "/WEB-INF/views/index.jsp";
        request.getRequestDispatcher(view).forward(request, response);
    }
}
