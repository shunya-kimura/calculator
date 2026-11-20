<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Calculator</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="calculator">

    <!-- 履歴 -->
    <div class="top-bar">
        <button type="button" class="history-icon" aria-label="履歴を表示">🕘</button>
    </div>

    <!-- 表示欄 -->
    <%
        Object result = request.getAttribute("result");
    %>
    <input type="text" id="display" class="display" value="<%= result != null ? result : "" %>" readonly aria-label="入力値と計算結果">

    <form action="calculator" method="post" id="calcForm">

        <!-- 電卓ボタン -->
        <div class="buttons">

            <!-- 1段目 -->
            <button class="function" type="submit" formaction="clear">AC</button>
            <button class="function" type="button" aria-label="1文字削除">×</button>
            <button class="function" type="button">%</button>
            <button class="operator" type="button">÷</button>

            <!-- 2段目 -->
            <button class="number" type="button">7</button>
            <button class="number" type="button">8</button>
            <button class="number" type="button">9</button>
            <button class="operator" type="button" aria-label="掛ける">×</button>

            <!-- 3段目 -->
            <button class="number" type="button">4</button>
            <button class="number" type="button">5</button>
            <button class="number" type="button">6</button>
            <button class="operator" type="button">−</button>

            <!-- 4段目 -->
            <button class="number" type="button">1</button>
            <button class="number" type="button">2</button>
            <button class="number" type="button">3</button>
            <button class="operator" type="button">＋</button>

            <!-- 5段目 -->
            <button class="sign" type="button">+/−</button>
            <button class="number" type="button">0</button>
            <button class="number" type="button">.</button>
            <button class="operator" type="submit">=</button>

        </div>

        <input type="hidden" name="num1" id="num1" value="">
        <input type="hidden" name="operator" id="operator" value="">
        <input type="hidden" name="num2" id="num2" value="">
    </form>

    <!-- メッセージ -->
    <div class="status" id="status" role="status"></div>

    <!-- 履歴表示エリア -->
    <div id="historyArea" class="history-area" style="display: none;">
        <div class="history-header">
            <span>履歴</span>
            <button type="button" class="history-close">閉じる</button>
        </div>
        <div id="historyList" class="history-list"></div>
    </div>

</div>

</body>
</html>
