'use strict';

const MAX_DIGITS = 15;
const DISPLAY_FONT_SIZE = 54;
const MIN_DISPLAY_FONT_SIZE = 18;

const MESSAGE_ENTER_VALUE = '値を入力してください。';
const MESSAGE_INVALID = '無効な式です。';
const MESSAGE_FAILED = '計算できませんでした。もう一度お試しください。';

/*
 * 入力中の式は「確定した部分」と「入力中の数値」に分けて持つ。
 * 例: 12 + 3 × 4 を入力中 → tokens = ['12', '+', '3', '*'], current = '4'
 * = を押すと、つなげた文字列（例: "12+3*4"）をサーバーに送り、
 * 数値と演算子への分解と計算はサーバー側（Model）で行う。
 */
let tokens = [];
let current = '';
let justCalculated = false;
let calculating = false;

const displayElement = document.getElementById('display');
const statusElement = document.getElementById('status');

/**
 * 表示欄の下にメッセージを表示する。空文字を渡すと消える。
 * @param {string} message 表示するメッセージ
 */
function setStatus(message) {
    statusElement.textContent = message;
}

/**
 * 内部で使う記号（* / -）を、画面表示用の記号（× ÷ −）に置き換える。
 * @param {string} token 数値または演算子
 * @returns {string} 画面表示用の文字列
 */
function toDisplayText(token) {
    return token.replace('*', '×').replace('/', '÷').replace('-', '−');
}

/**
 * 現在の入力内容を表示欄に反映する。
 */
function render() {
    displayElement.value = [...tokens, current].map(toDisplayText).join('');
    fitDisplayText();
}

/**
 * 長い式が表示欄に収まるように、文字の大きさを小さくする。
 */
function fitDisplayText() {
    let fontSize = DISPLAY_FONT_SIZE;
    displayElement.style.fontSize = fontSize + 'px';
    while (displayElement.scrollWidth > displayElement.clientWidth && fontSize > MIN_DISPLAY_FONT_SIZE) {
        fontSize -= 2;
        displayElement.style.fontSize = fontSize + 'px';
    }
    displayElement.scrollLeft = displayElement.scrollWidth;
}

/**
 * 入力内容と状態をすべて初期状態に戻す。
 */
function reset() {
    tokens = [];
    current = '';
    justCalculated = false;
}

/**
 * 末尾の「.」を取り除いた数値を返す（例: '5.' → '5'）。
 * @param {string} number 数値の文字列
 * @returns {string} 整えた数値の文字列
 */
function normalizeNumber(number) {
    return number.endsWith('.') ? number.slice(0, -1) : number;
}

/**
 * 数字ボタンの処理。入力中の数値の末尾に数字を追加する。
 * 0の連続入力は00にせず、15桁を超える入力は受け付けない。
 * @param {string} digit 押された数字（0〜9）
 */
function inputNumber(digit) {
    setStatus('');
    if (justCalculated) {
        reset();
    }
    if (current === '0') {
        current = digit;
    } else if (current.replace(/[-.]/g, '').length < MAX_DIGITS) {
        current += digit;
    }
    render();
}

/**
 * 小数点ボタンの処理。入力中の数値に「.」を1つだけ追加する。
 */
function inputDecimal() {
    setStatus('');
    if (current === '') {
        setStatus(MESSAGE_ENTER_VALUE);
        return;
    }
    if (!current.includes('.')) {
        current += '.';
        justCalculated = false;
    }
    render();
}

/**
 * 演算子ボタンの処理。入力中の数値を確定し、演算子を追加する。
 * 演算子が続いた場合は、後から押した演算子に置き換える。
 * @param {string} operator 押された演算子（+ - * /）
 */
function chooseOperator(operator) {
    setStatus('');
    if (current === '') {
        if (tokens.length === 0) {
            setStatus(operator === '*' || operator === '/' ? MESSAGE_INVALID : MESSAGE_ENTER_VALUE);
            return;
        }
        tokens[tokens.length - 1] = operator;
    } else {
        tokens.push(normalizeNumber(current), operator);
        current = '';
    }
    justCalculated = false;
    render();
}

/**
 * ACボタンの処理。入力内容をすべて消す。
 */
function clearDisplay() {
    setStatus('');
    reset();
    render();
}

/**
 * ×（1文字削除）ボタンの処理。最後に入力した1文字を消す。
 * 演算子を消した場合は、その前の数値を再び入力中に戻す。
 */
function deleteLast() {
    setStatus('');
    if (current !== '') {
        current = current.slice(0, -1);
        if (current === '-') {
            current = '';
        }
    } else if (tokens.length > 0) {
        tokens.pop();
        current = tokens.pop();
    }
    justCalculated = false;
    render();
}

/**
 * サーバーに送る計算式の文字列を作る。末尾の演算子は取り除く。
 * @returns {string} 計算式（例: "12+3*4"）。計算するものがない場合は空文字
 */
function buildExpression() {
    const parts = [...tokens];
    if (current === '') {
        parts.pop();
    } else {
        parts.push(normalizeNumber(current));
    }
    // 数値だけ、または未入力の場合は計算しない
    return parts.length < 3 ? '' : parts.join('');
}

/**
 * =ボタンの処理。計算式の文字列をサーバーに送り、結果を受け取る。
 */
async function calculate() {
    if (calculating) {
        return;
    }
    setStatus('');
    const expression = buildExpression();
    if (expression === '') {
        return;
    }

    calculating = true;
    try {
        const response = await fetch(document.body.dataset.calculateUrl, {
            method: 'POST',
            body: new URLSearchParams({ expression })
        });
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

/**
 * サーバーから返ってきた結果を画面に反映する。
 * 成功（ok）なら結果を表示し、それ以外はエラーメッセージを表示する。
 * @param {{status: string, result?: string}} answer サーバーの応答
 */
function applyResult(answer) {
    if (answer.status === 'ok') {
        tokens = [];
        current = answer.result;
        justCalculated = true;
    } else {
        setStatus(MESSAGE_FAILED);
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

/* ボタンが押されたら、data-action に対応する処理を呼び出す */
document.addEventListener('click', (event) => {
    const button = event.target.closest('button[data-action]');
    const action = button && actions[button.dataset.action];
    if (action) {
        action(button.dataset.value);
    }
});

render();
