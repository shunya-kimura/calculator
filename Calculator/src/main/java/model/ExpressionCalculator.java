package model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 電卓の計算式を評価する。
 *
 * 計算式は「数値, 演算子, 数値, 演算子, 数値 ...」の順に並んだ配列で受け取る。
 * 演算子は + - * / の4種類。× と ÷ を + と − より先に計算する。
 */
public final class ExpressionCalculator {

    /** 設計書 6章: 入力と計算結果の桁数の上限 */
    public static final int MAX_DIGITS = 15;

    private static final int MAX_TOKENS = 1023;
    private static final Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");

    private ExpressionCalculator() {
    }

    /**
     * 計算式を評価し、画面に表示する文字列を返す。
     *
     * @throws InvalidExpressionException 式が不正な場合
     * @throws ArithmeticException        0で割った場合
     * @throws ResultOverflowException    結果の整数部が15桁を超える場合
     */
    public static String evaluate(List<String> tokens) {
        if (tokens.isEmpty() || tokens.size() % 2 == 0 || tokens.size() > MAX_TOKENS) {
            throw new InvalidExpressionException();
        }

        // × ÷ は直前の項にその場で掛け割りし、+ − が来たら項を合計に足し込む
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal term = toNumber(tokens.get(0));
        for (int i = 1; i < tokens.size(); i += 2) {
            String operator = tokens.get(i);
            BigDecimal next = toNumber(tokens.get(i + 1));
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
        return format(total.add(term));
    }

    private static BigDecimal toNumber(String token) {
        if (token == null || !NUMBER.matcher(token).matches()
                || token.replaceAll("[-.]", "").length() > MAX_DIGITS) {
            throw new InvalidExpressionException();
        }
        return new BigDecimal(token);
    }

    /** 整数部と小数部を合わせて15桁に収める。整数部だけで15桁を超える場合はエラーとする。 */
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

    /** 整数部の桁数を返す。整数部が0の場合は1桁とする */
    private static int integerDigits(BigDecimal value) {
        BigDecimal integerPart = value.abs().setScale(0, RoundingMode.DOWN);
        return integerPart.signum() == 0 ? 1 : integerPart.precision();
    }

    public static final class InvalidExpressionException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public static final class ResultOverflowException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
