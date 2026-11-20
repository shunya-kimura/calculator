<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Calculator</title>
<style>
  body {
    display: flex;
    justify-content: center;
    padding-top: 50px;
    font-family: sans-serif;
    background-color: #f4f5f7;
  }
  
  .calculator {
    width: 320px;
    background-color: #e5e5e5;
    padding: 10px;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  }

  .display {
    background-color: #f0f4e6;
    padding: 10px;
    border-radius: 5px;
    text-align: right;
    margin-bottom: 10px;
    border: 1px solid #ccc;
  }
  
  .history {
    font-size: 16px;
    color: #333;
    min-height: 20px;
  }
  
  .result-display {
    font-size: 40px;
    font-weight: bold;
    color: #000;
    margin: 0;
  }

  .keypad {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 6px;
  }
  
  button {
    padding: 15px 5px;
    font-size: 18px;
    border: 1px solid #999;
    border-radius: 6px;
    cursor: pointer;
    font-weight: bold;
  }
  
  button:active {
    opacity: 0.8;
  }

  .span-2 {
    grid-column: span 2;
  }

  .btn-red { background-color: #b51c1c; color: white; border-color: #8b0000; }
  .btn-dark { background-color: #555555; color: white; border-color: #333333; font-size: 15px; }
  .btn-green { background-color: #8da56b; color: white; border-color: #6b854a; font-size: 15px; }
  .btn-blue { background-color: #4b6a9b; color: white; border-color: #2b4d82; font-size: 24px; }
  .btn-white { background-color: #ffffff; color: #000000; font-size: 22px; }
</style>
</head>
<body>

<div class="calculator">
  <div class="display">
    <div class="history"></div>
    <div class="result-display">
      <% 
        Object result = request.getAttribute("result");
        out.print(result != null ? result : "0");
      %>
    </div>
  </div>

  <form action="calculator" method="post" id="calcForm">
    <div class="keypad">
      <button type="submit" formaction="clear" class="btn-red span-2">全消去 (AC)</button>
      <button type="button" class="btn-dark span-2">⌫ 1文字削除</button>

      <button type="button" class="btn-green span-2">税込 (10%)</button>
      <button type="button" class="btn-green span-2">税抜 (10%)</button>

      <button type="button" class="btn-blue">÷</button>
      <button type="button" class="btn-white">7</button>
      <button type="button" class="btn-white">8</button>
      <button type="button" class="btn-white">9</button>

      <button type="button" class="btn-blue">×</button>
      <button type="button" class="btn-white">4</button>
      <button type="button" class="btn-white">5</button>
      <button type="button" class="btn-white">6</button>

      <button type="button" class="btn-blue">-</button>
      <button type="button" class="btn-white">1</button>
      <button type="button" class="btn-white">2</button>
      <button type="button" class="btn-white">3</button>

      <button type="button" class="btn-blue">+</button>
      <button type="button" class="btn-white">0</button>
      <button type="button" class="btn-white">.</button>
      <button type="submit" class="btn-dark">=</button>
    </div>
    
    <input type="hidden" name="num1" id="num1" value="">
    <input type="hidden" name="operator" id="operator" value="">
    <input type="hidden" name="num2" id="num2" value="">
  </form>
</div>

</body>
</html>