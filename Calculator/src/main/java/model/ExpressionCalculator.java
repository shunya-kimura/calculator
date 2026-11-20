package model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 電卓の計算式を評価する。
 *
 * 対応する書式: 数値、+ - * /、単項マイナス、数値直後の %。
 * × と ÷ を + と − より先に計算する。
 */
public final class ExpressionCalculator {

    /** 設計書 6章: 入力と計算結果の桁数の上限 */
    public static final int MAX_DIGITS = 15;

    private static final int MAX_EXPRESSION_LENGTH = 1024;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final String expression;
    private int position;

    private ExpressionCalculator(String expression) {
        this.expression = expression;
    }

    /**
     * 計算式を評価し、画面に表示する文字列を返す。
     *
     * @throws InvalidExpressionException 式が不正な場合
     * @throws ArithmeticException        0で割った場合
     * @throws ResultOverflowException    結果の整数部が15桁を超える場合
     */
    public static String evaluate(String expression) {
        if (expression == null || expression.isEmpty() || expression.length() > MAX_EXPRESSION_LENGTH) {
            throw new InvalidExpressionException();
        }
        ExpressionCalculator calculator = new ExpressionCalculator(expression);
        BigDecimal value = calculator.parseExpression();
        if (calculator.position != expression.length()) {
            throw new InvalidExpressionException();
        }
        return format(value);
    }

    private BigDecimal parseExpression() {
        BigDecimal total = parseTerm(null);
        while (peek() == '+' || peek() == '-') {
            char operator = expression.charAt(position++);
            BigDecimal term = parseTerm(total);
            total = operator == '+' ? total.add(term) : total.subtract(term);
        }
        return total;
    }

    /**
     * 項を評価する。
     *
     * 項が「b%」だけで、+ または − の右側にある場合は、それまでの値に対する割合とする。
     * 例: 200+10% = 200 + (200の10%) = 220
     *
     * @param base 割合計算の基準値。式の先頭の項では null
     */
    private BigDecimal parseTerm(BigDecimal base) {
        Factor first = parseFactor();
        boolean hasMultiplication = peek() == '*' || peek() == '/';
        BigDecimal value = first.value;
        if (first.percent && base != null && !hasMultiplication) {
            return base.multiply(first.value, MathContext.DECIMAL128);
        }
        while (peek() == '*' || peek() == '/') {
            char operator = expression.charAt(position++);
            BigDecimal right = parseFactor().value;
            if (operator == '*') {
                value = value.multiply(right);
            } else {
                value = value.divide(right, MathContext.DECIMAL128);
            }
        }
        return value;
    }

    private Factor parseFactor() {
        boolean negative = false;
        if (peek() == '-') {
            negative = true;
            position++;
        }
        BigDecimal number = parseNumber();
        boolean percent = peek() == '%';
        if (percent) {
            position++;
            number = number.divide(HUNDRED, MathContext.DECIMAL128);
        }
        return new Factor(negative ? number.negate() : number, percent);
    }

    private BigDecimal parseNumber() {
        int start = position;
        int digits = 0;
        boolean hasDot = false;
        while (position < expression.length()) {
            char c = expression.charAt(position);
            if (c >= '0' && c <= '9') {
                digits++;
            } else if (c == '.' && !hasDot) {
                hasDot = true;
            } else {
                break;
            }
            position++;
        }
        if (digits == 0 || digits > MAX_DIGITS || expression.charAt(start) == '.') {
            throw new InvalidExpressionException();
        }
        return new BigDecimal(expression.substring(start, position));
    }

    private char peek() {
        return position < expression.length() ? expression.charAt(position) : '\0';
    }

    /** 整数部と小数部を合わせて15桁に収める。整数部だけで15桁を超える場合はエラーとする。 */
    private static String format(BigDecimal value) {
        BigDecimal integerPart = value.abs().setScale(0, RoundingMode.DOWN);
        int integerDigits = integerPart.signum() == 0 ? 1 : integerPart.precision();
        if (integerDigits > MAX_DIGITS) {
            throw new ResultOverflowException();
        }
        BigDecimal rounded = value.setScale(MAX_DIGITS - integerDigits, RoundingMode.HALF_UP);
        BigDecimal roundedInteger = rounded.abs().setScale(0, RoundingMode.DOWN);
        if (roundedInteger.signum() != 0 && roundedInteger.precision() > MAX_DIGITS) {
            throw new ResultOverflowException();
        }
        if (rounded.signum() == 0) {
            return "0";
        }
        return rounded.stripTrailingZeros().toPlainString();
    }

    private static final class Factor {
        final BigDecimal value;
        final boolean percent;

        Factor(BigDecimal value, boolean percent) {
            this.value = value;
            this.percent = percent;
        }
    }

    public static final class InvalidExpressionException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public static final class ResultOverflowException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
