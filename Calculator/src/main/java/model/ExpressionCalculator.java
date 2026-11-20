package model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 電卓の計算式を評価する Model。
 *
 * 計算式は文字列（例: "12+3*4"）で受け取り、数値と演算子に分解してから計算する。
 * 演算子は + - * / の4種類。× と ÷ を + と − より先に計算する。
 */
public final class ExpressionCalculator {

    /** 設計書 6章: 入力と計算結果の桁数の上限 */
    public static final int MAX_DIGITS = 15;

    /** 受け付ける計算式の最大文字数 */
    private static final int MAX_EXPRESSION_LENGTH = 1024;

    /** 使用できる演算子 */
    private static final String OPERATORS = "+-*/";

    /** インスタンスを作らせない（static メソッドだけで使う） */
    private ExpressionCalculator() {
    }

    /**
     * 計算式を計算し、画面に表示する文字列を返す。
     *
     * @param expression 計算式の文字列（例: "12+3*4"）
     * @return 計算結果の文字列（例: "24"）
     * @throws InvalidExpressionException 式が不正な場合
     * @throws ArithmeticException        0で割った場合
     * @throws ResultOverflowException    結果の整数部が15桁を超える場合
     */
    public static String evaluate(String expression) {
        if (expression == null || expression.isEmpty() || expression.length() > MAX_EXPRESSION_LENGTH) {
            throw new InvalidExpressionException();
        }
        List<String> tokens = tokenize(expression);
        return format(calculate(tokens));
    }

    /**
     * 計算式の文字列を、数値と演算子が交互に並んだリストに分解する。
     * 例: "12+3*-4" → ["12", "+", "3", "*", "-4"]
     *
     * 式の先頭、または演算子の直後にある「-」は、負の数の符号として数値に含める。
     *
     * @param expression 計算式の文字列
     * @return 数値と演算子のリスト
     * @throws InvalidExpressionException 式の並びが不正な場合
     */
    static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        int position = 0;
        while (position < expression.length()) {
            boolean expectsNumber = tokens.size() % 2 == 0;
            if (expectsNumber) {
                int end = findNumberEnd(expression, position);
                tokens.add(expression.substring(position, end));
                position = end;
            } else {
                char operator = expression.charAt(position);
                if (OPERATORS.indexOf(operator) < 0) {
                    throw new InvalidExpressionException();
                }
                tokens.add(String.valueOf(operator));
                position++;
            }
        }
        // 「数値, 演算子, 数値」の形で、数値で終わっている必要がある
        if (tokens.size() % 2 == 0) {
            throw new InvalidExpressionException();
        }
        return tokens;
    }

    /**
     * position から始まる数値（符号「-」と小数点を含む）が、どこで終わるかを返す。
     *
     * @param expression 計算式の文字列
     * @param start      数値の開始位置
     * @return 数値の直後の位置
     * @throws InvalidExpressionException 数値として読めない場合、または15桁を超える場合
     */
    private static int findNumberEnd(String expression, int start) {
        int position = start;
        if (position < expression.length() && expression.charAt(position) == '-') {
            position++;
        }
        int digits = 0;
        boolean hasDot = false;
        while (position < expression.length()) {
            char c = expression.charAt(position);
            if (Character.isDigit(c)) {
                digits++;
            } else if (c == '.' && !hasDot && digits > 0) {
                hasDot = true;
            } else {
                break;
            }
            position++;
        }
        // 数字がない・15桁を超える・「5.」のように「.」で終わる場合は不正とする
        if (digits == 0 || digits > MAX_DIGITS || expression.charAt(position - 1) == '.') {
            throw new InvalidExpressionException();
        }
        return position;
    }

    /**
     * 数値と演算子のリストを計算する。× と ÷ を先に計算する。
     *
     * × ÷ は直前の項にその場で掛け割りし、+ − が来たら、その項を合計に足し込む。
     * 例: 12 + 3 × 4 → 合計 12、項 3 → 項 3×4=12 → 最後に 12+12=24
     *
     * @param tokens 数値と演算子が交互に並んだリスト
     * @return 計算結果
     * @throws ArithmeticException 0で割った場合
     */
    private static BigDecimal calculate(List<String> tokens) {
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
                case "/" -> {
                    if (next.signum() == 0) {
                        throw new ArithmeticException("0で割ることはできません");
                    }
                    term = term.divide(next, MathContext.DECIMAL128);
                }
                default -> throw new InvalidExpressionException();
            }
        }
        return total.add(term);
    }

    /**
     * 計算結果を、整数部と小数部を合わせて15桁に収めた文字列にする。
     *
     * @param value 計算結果
     * @return 画面表示用の文字列（末尾の余分な0は取り除く）
     * @throws ResultOverflowException 整数部だけで15桁を超える場合
     */
    private static String format(BigDecimal value) {
        int digits = integerDigits(value);
        if (digits > MAX_DIGITS) {
            throw new ResultOverflowException();
        }
        BigDecimal rounded = value.setScale(MAX_DIGITS - digits, RoundingMode.HALF_UP);
        // 四捨五入で桁が繰り上がる場合(例: 999...9.6)があるので、もう一度確認する
        if (integerDigits(rounded) > MAX_DIGITS) {
            throw new ResultOverflowException();
        }
        if (rounded.signum() == 0) {
            return "0";
        }
        return rounded.stripTrailingZeros().toPlainString();
    }

    /**
     * 整数部の桁数を返す。整数部が0の場合は1桁とする。
     *
     * @param value 対象の数値
     * @return 整数部の桁数
     */
    private static int integerDigits(BigDecimal value) {
        BigDecimal integerPart = value.abs().setScale(0, RoundingMode.DOWN);
        return integerPart.signum() == 0 ? 1 : integerPart.precision();
    }

    /** 計算式が不正な場合の例外 */
    public static final class InvalidExpressionException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    /** 計算結果が15桁を超えた場合の例外 */
    public static final class ResultOverflowException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
