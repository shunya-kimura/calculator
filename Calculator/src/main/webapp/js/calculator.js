'use strict';

const MAX_DIGITS = 15;
const DISPLAY_FONT_SIZE = 54;
const MIN_DISPLAY_FONT_SIZE = 18;

const MESSAGE_ENTER_VALUE = '値を入力してください。';
const MESSAGE_INVALID = '無効な式です。';
const MESSAGE_DIVIDE_BY_ZERO = '0で割ることはできません。';
const MESSAGE_FAILED = '計算できませんでした。もう一度お試しください。';

const OPERATORS = ['+', '-', '*', '/'];

/*
 * 入力中の式は「確定した要素の配列」と「入力中の数値」で持つ。
 * 例: 12 + 3 × 4 を入力中 → tokens = ['12', '+', '3', '*'], current = '4'
 * = を押すと、tokens と current をまとめた配列をサーバーに送る。
 */
let tokens = [];
let current = '';
let justCalculated = false;
let showsError = false;
let calculating = false;

const displayElement = document.getElementById('display');
const statusElement = document.getElementById('status');

function setStatus(message) {
    statusElement.textContent = message;
}

function toDisplayText(token) {
    return token.replace('*', '×').replace('/', '÷').replace('-', '−');
}

function render() {
    const text = [...tokens, current].map(toDisplayText).join('');
    displayElement.value = showsError ? 'Error' : text;
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
    tokens = [];
    current = '';
    justCalculated = false;
    showsError = false;
}

/** 末尾の「.」を取り除いた数値を返す（例: '5.' → '5'） */
function normalizeNumber(number) {
    return number.endsWith('.') ? number.slice(0, -1) : number;
}

function inputNumber(digit) {
    setStatus('');
    if (justCalculated || showsError) {
        reset();
    }
    if (current === '0') {
        // 0の連続入力は00にせず、他の数字は0と置き換える
        current = digit;
    } else if (current.replace(/[-.]/g, '').length < MAX_DIGITS) {
        current += digit;
    }
    render();
}

function inputDecimal() {
    setStatus('');
    if (showsError || current === '') {
        setStatus(MESSAGE_ENTER_VALUE);
        return;
    }
    if (!current.includes('.')) {
        current += '.';
        justCalculated = false;
    }
    render();
}

function chooseOperator(operator) {
    setStatus('');
    if (showsError) {
        return;
    }
    if (current === '') {
        if (tokens.length === 0) {
            setStatus(operator === '*' || operator === '/' ? MESSAGE_INVALID : MESSAGE_ENTER_VALUE);
            return;
        }
        // 演算子が続いた場合は、後から押した演算子に置き換える
        tokens[tokens.length - 1] = operator;
    } else {
        tokens.push(normalizeNumber(current), operator);
        current = '';
    }
    justCalculated = false;
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
    } else if (current !== '') {
        current = current.slice(0, -1);
        if (current === '-') {
            current = '';
        }
    } else if (tokens.length > 0) {
        // 末尾の演算子を消し、その前の数値を再び入力中に戻す
        tokens.pop();
        current = tokens.pop();
    }
    justCalculated = false;
    render();
}

/** サーバーに送る配列を作る。末尾の演算子は取り除く */
function buildExpression() {
    const expression = [...tokens];
    if (current === '') {
        expression.pop();
    } else {
        expression.push(normalizeNumber(current));
    }
    return expression;
}

async function calculate() {
    if (calculating || showsError) {
        return;
    }
    setStatus('');
    const expression = buildExpression();
    if (expression.length < 3) {
        // 数値だけ、または未入力の場合は計算しない
        return;
    }

    const body = new URLSearchParams();
    expression.forEach((token) => body.append('token', token));

    calculating = true;
    try {
        const response = await fetch(document.body.dataset.calculateUrl, { method: 'POST', body });
        if (!response.ok) {
            throw new Error('HTTP ' + response.status);
        }
        applyResult(await response.json());
    } catch (error) {
        console.error(error);
        setStatus(MESSAGE_FAILED);
    } finally {
        calculating = false;
    }
}

function applyResult(answer) {
    switch (answer.status) {
        case 'ok':
            tokens = [];
            current = answer.result;
            justCalculated = true;
            break;
        case 'overflow':
            reset();
            showsError = true;
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

/* ボタンの data-action と処理の対応。data-value があれば引数として渡す */
const actions = {
    number: inputNumber,
    decimal: inputDecimal,
    operator: chooseOperator,
    clear: clearDisplay,
    delete: deleteLast,
    calculate: calculate
};

document.addEventListener('click', (event) => {
    const button = event.target.closest('button[data-action]');
    const action = button && actions[button.dataset.action];
    if (action) {
        action(button.dataset.value);
    }
});

render();
