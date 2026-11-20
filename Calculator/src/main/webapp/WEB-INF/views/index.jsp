<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Calculator</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body data-calculate-url="${pageContext.request.contextPath}/calculator">

<div class="calculator">

    <!-- 履歴 -->
    <div class="top-bar">
        <button type="button" class="history-icon" onclick="toggleHistory()" aria-label="履歴を表示">🕘</button>
    </div>

    <!-- 表示欄 -->
    <input type="text" id="display" class="display" value="" readonly aria-label="入力値と計算結果">

    <!-- 電卓ボタン -->
    <div class="buttons">

        <!-- 1段目 -->
        <button class="function" type="button" onclick="clearDisplay()">AC</button>
        <button class="function" type="button" onclick="deleteLast()" aria-label="1文字削除">×</button>
        <button class="function" type="button" onclick="percentage()">%</button>
        <button class="operator" type="button" onclick="chooseOperator('/')">÷</button>

        <!-- 2段目 -->
        <button class="number" type="button" onclick="inputNumber('7')">7</button>
        <button class="number" type="button" onclick="inputNumber('8')">8</button>
        <button class="number" type="button" onclick="inputNumber('9')">9</button>
        <button class="operator" type="button" onclick="chooseOperator('*')" aria-label="掛ける">×</button>

        <!-- 3段目 -->
        <button class="number" type="button" onclick="inputNumber('4')">4</button>
        <button class="number" type="button" onclick="inputNumber('5')">5</button>
        <button class="number" type="button" onclick="inputNumber('6')">6</button>
        <button class="operator" type="button" onclick="chooseOperator('-')">−</button>

        <!-- 4段目 -->
        <button class="number" type="button" onclick="inputNumber('1')">1</button>
        <button class="number" type="button" onclick="inputNumber('2')">2</button>
        <button class="number" type="button" onclick="inputNumber('3')">3</button>
        <button class="operator" type="button" onclick="chooseOperator('+')">＋</button>

        <!-- 5段目 -->
        <button class="sign" type="button" onclick="toggleSign()">+/−</button>
        <button class="number" type="button" onclick="inputNumber('0')">0</button>
        <button class="number" type="button" onclick="inputDecimal()">.</button>
        <button class="operator" type="button" onclick="calculate()">=</button>

    </div>

    <!-- メッセージ -->
    <div class="status" id="status" role="status"></div>

    <!-- 履歴表示エリア -->
    <div id="historyArea" class="history-area" style="display: none;">
        <div class="history-header">
            <span>履歴</span>
            <button type="button" class="history-close" onclick="toggleHistory()">閉じる</button>
        </div>
        <div id="historyList" class="history-list"></div>
    </div>

</div>

<script src="${pageContext.request.contextPath}/js/calculator.js"></script>
</body>
</html>
