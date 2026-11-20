'use strict';

const MAX_DIGITS = 15;
const MAX_HISTORY = 10;
const HISTORY_KEY = 'calculatorHistory';
const DISPLAY_FONT_SIZE = 54;
const MIN_DISPLAY_FONT_SIZE = 18;

const MESSAGE_ENTER_VALUE = '値を入力してください。';
const MESSAGE_INVALID = '無効な式です。';
const MESSAGE_DIVIDE_BY_ZERO = '0で割ることはできません。';

// 内部の式は + - * / を使う。画面表示のときだけ記号を置き換える
let expression = '';
let showsError = false;
let justCalculated = false;
let calculating = false;
let history = loadHistory();

const displayElement = document.getElementById('display');
const statusElement = document.getElementById('status');
const historyAreaElement = document.getElementById('historyArea');
const historyListElement = document.getElementById('historyList');

function isOperator(character) {
    return '+-*/'.includes(character);
}

function lastCharacter() {
    return expression.charAt(expression.length - 1);
}

/** 末尾の - が、引き算ではなく負の符号かどうかを判定する */
function endsWithNegativeSign() {
    if (lastCharacter() !== '-') {
        return false;
    }
    return expression.length === 1 || isOperator(expression.charAt(expression.length - 2));
}

/** 入力中の数値(符号を除く)を返す。なければ空文字を返す */
function currentNumber() {
    const match = expression.match(/\d+\.?\d*%?$/);
    return match ? match[0] : '';
}

function setStatus(message) {
    statusElement.textContent = message;
}

function toDisplayText(text) {
    return text.replace(/\*/g, '×').replace(/\//g, '÷').replace(/-/g, '−');
}

function render() {
    displayElement.value = showsError ? 'Error' : toDisplayText(expression);
    fitDisplayText();
}

/** 長い式が表示欄に収まるように文字を小さくする */
function fitDisplayText() {
    let fontSize = DISPLAY_FONT_SIZE;
    displayElement.style.fontSize = fontSize + 'px';
    while (displayElement.scrollWidth > displayElement.clientWidth && fontSize > MIN_DISPLAY_FONT_SIZE) {
        fontSize -= 2;
        displayElement.style.fontSize = fontSize + 'px';
    }
    displayElement.scrollLeft = displayElement.scrollWidth;
}

function reset() {
    expression = '';
    showsError = false;
    justCalculated = false;
}

function inputNumber(digit) {
    setStatus('');
    if (justCalculated || showsError) {
        reset();
    }
    const number = currentNumber();
    if (number.endsWith('%')) {
        setStatus(MESSAGE_INVALID);
        return;
    }
    if (number === '0') {
        // 0の連続入力は00にせず、他の数字は0と置き換える
        expression = expression.slice(0, -1) + digit;
    } else if (number.replace('.', '').length < MAX_DIGITS) {
        expression += digit;
    }
    render();
}

function inputDecimal() {
    setStatus('');
    const number = currentNumber();
    if (number === '' || showsError) {
        setStatus(MESSAGE_ENTER_VALUE);
        return;
    }
    if (number.endsWith('%')) {
        setStatus(MESSAGE_INVALID);
        return;
    }
    if (!number.includes('.')) {
        expression += '.';
        justCalculated = false;
    }
    render();
}

function chooseOperator(operator) {
    setStatus('');
    if (expression === '') {
        if (operator === '-') {
            showsError = false;
            expression = '-';
            render();
        } else if (operator === '+') {
            setStatus(MESSAGE_ENTER_VALUE);
        } else {
            setStatus(MESSAGE_INVALID);
        }
        return;
    }
    if (endsWithNegativeSign()) {
        setStatus(MESSAGE_ENTER_VALUE);
        return;
    }
    if (isOperator(lastCharacter()) && operator !== '-') {
        setStatus(MESSAGE_ENTER_VALUE);
        return;
    }
    expression += operator;
    justCalculated = false;
    render();
}

function toggleSign() {
    setStatus('');
    const number = currentNumber();
    if (number === '') {
        setStatus(MESSAGE_ENTER_VALUE);
        return;
    }
    const before = expression.slice(0, -number.length);
    const isNegative = before.endsWith('-')
        && (before.length === 1 || isOperator(before.charAt(before.length - 2)));
    expression = (isNegative ? before.slice(0, -1) : before + '-') + number;
    render();
}

function percentage() {
    setStatus('');
    const number = currentNumber();
    if (number === '') {
        setStatus(MESSAGE_ENTER_VALUE);
        return;
    }
    if (!number.endsWith('%')) {
        expression += '%';
        justCalculated = false;
    }
    render();
}

function clearDisplay() {
    setStatus('');
    reset();
    render();
}

function deleteLast() {
    setStatus('');
    if (showsError) {
        reset();
    } else {
        expression = expression.slice(0, -1);
        justCalculated = false;
    }
    render();
}

/** 末尾の演算子を除いた、計算できる形の式を返す */
function completeExpression() {
    return expression.replace(/[-+*/]+$/, '');
}

async function calculate() {
    if (calculating) {
        return;
    }
    setStatus('');
    if (expression === '') {
        return;
    }
    const target = completeExpression();
    if (target === '') {
        setStatus(MESSAGE_ENTER_VALUE);
        return;
    }

    calculating = true;
    try {
        const response = await fetch(document.body.dataset.calculateUrl, {
            method: 'POST',
            body: new URLSearchParams({ expression: target })
        });
        if (!response.ok) {
            throw new Error('HTTP ' + response.status);
        }
        applyResult(target, await response.json());
    } catch (error) {
        console.error(error);
        setStatus('計算できませんでした。もう一度お試しください。');
    } finally {
        calculating = false;
    }
}

function applyResult(target, answer) {
    switch (answer.status) {
        case 'ok':
            addHistory(target, answer.result);
            expression = answer.result;
            justCalculated = true;
            break;
        case 'overflow':
            expression = '';
            showsError = true;
            justCalculated = false;
            break;
        case 'divideByZero':
            setStatus(MESSAGE_DIVIDE_BY_ZERO);
            break;
        default:
            setStatus(MESSAGE_INVALID);
            break;
    }
    render();
}

/* 履歴。タブを閉じると消えるようにブラウザのsessionStorageに保持し、サーバーのセッションは使わない */

function loadHistory() {
    try {
        const stored = JSON.parse(sessionStorage.getItem(HISTORY_KEY));
        return Array.isArray(stored) ? stored : [];
    } catch (error) {
        return [];
    }
}

function saveHistory() {
    try {
        sessionStorage.setItem(HISTORY_KEY, JSON.stringify(history));
    } catch (error) {
        console.warn('履歴を保存できませんでした。', error);
    }
}

function addHistory(target, result) {
    // 数字だけで=を押した場合は計算していないので、履歴に残さない
    if (!/[-+*/%]/.test(target.replace(/^-/, ''))) {
        return;
    }
    history.push(toDisplayText(target) + ' = ' + toDisplayText(result));
    if (history.length > MAX_HISTORY) {
        history.shift();
    }
    saveHistory();
    renderHistory();
}

function renderHistory() {
    historyListElement.replaceChildren();
    if (history.length === 0) {
        const empty = document.createElement('div');
        empty.className = 'history-empty';
        empty.textContent = '履歴はありません';
        historyListElement.append(empty);
        return;
    }
    for (const entry of [...history].reverse()) {
        const item = document.createElement('div');
        item.className = 'history-item';
        item.textContent = entry;
        historyListElement.append(item);
    }
}

function toggleHistory() {
    const isHidden = historyAreaElement.style.display === 'none';
    historyAreaElement.style.display = isHidden ? 'block' : 'none';
}

renderHistory();
render();
