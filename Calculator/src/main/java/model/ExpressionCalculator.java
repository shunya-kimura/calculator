package model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 電卓の計算式を計算する Model。
 * 演算子は + - * / の4種類。× と ÷ を + と − より先に計算する。
 */
public class ExpressionCalculator {

    /**
     * 計算式を計算して結果を返す。
     *
     * @param expression 計算式の文字列（例: "12+3*4"）
     * @return 計算結果（例: 24）
     */
    public static BigDecimal evaluate(String expression) {
        List<String> tokens = tokenize(expression);
        return calculate(tokens);
    }

    /**
     * 計算式の文字列を、数値と演算子のリストに分解する。
     * 例: "12+3*-4" → ["12", "+", "3", "*", "-4"]
     *
     * 数値の始まりにある「-」は、引き算ではなく負の符号として数値に含める。
     *
     * @param expression 計算式の文字列
     * @return 数値と演算子が交互に並んだリスト
     */
    static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        for (char c : expression.toCharArray()) {
            boolean isSign = c == '-' && number.length() == 0;
            if (Character.isDigit(c) || c == '.' || isSign) {
                number.append(c);
            } else {
                // 演算子が来たら、それまでの数値を確定して演算子と一緒に追加する
                tokens.add(number.toString());
                tokens.add(String.valueOf(c));
                number.setLength(0);
            }
        }
        tokens.add(number.toString());
        return tokens;
    }

    /**
     * 数値と演算子のリストを計算する。× と ÷ を先に計算する。
     *
     * × ÷ は直前の項にその場で掛け割りし、+ − が来たら、その項を合計に足し込む。
     * 例: 12 + 3 × 4 → 合計 12、項 3 → 項 3×4=12 → 最後に 12+12=24
     *
     * @param tokens 数値と演算子が交互に並んだリスト
     * @return 計算結果
     */
    static BigDecimal calculate(List<String> tokens) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal term = new BigDecimal(tokens.get(0));
        for (int i = 1; i < tokens.size(); i += 2) {
            String operator = tokens.get(i);
            BigDecimal next = new BigDecimal(tokens.get(i + 1));
            switch (operator) {
                case "+" -> {
                    total = total.add(term);
                    term = next;
                }
                case "-" -> {
                    total = total.add(term);
                    term = next.negate();
                }
                case "*" -> term = term.multiply(next);
                case "/" -> term = term.divide(next, MathContext.DECIMAL64);
            }
        }
        return total.add(term);
    }
}
