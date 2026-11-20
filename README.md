# Calculator

Java（Servlet / JSP）で作った電卓アプリです。Tomcat 上で動かします。

## 必要なもの

- JDK 17 以上
- Tomcat 10.1 または 11（zip を展開しておく）
  - `jakarta.servlet` を使っているため、**Tomcat 9 以前では動きません**
- VS Code 拡張機能「Community Server Connector」（Red Hat）

## 起動手順

### 1. ソースをコンパイルする

リポジトリには `.class` ファイルが含まれていないため、各自でコンパイルが必要です。
クローンしたフォルダで次を実行します（`<Tomcatのフォルダ>` は自分の環境のパスに置き換える）。

```powershell
javac -encoding UTF-8 -cp "<Tomcatのフォルダ>\lib\servlet-api.jar" -d Calculator\src\main\webapp\WEB-INF\classes (Get-ChildItem -Recurse Calculator\src\main\java -Filter *.java).FullName
```

Java のソースを変更したら、毎回このコマンドを実行し直してください。

### 2. VS Code にサーバー（Tomcat）を登録する

1. VS Code 左下の **Servers** で `Community Server Connector` を右クリック → **Create New Server**
2. 「Download server?」には **No, use server on disk** と答え、展開した Tomcat のフォルダを指定する

### 3. アプリを登録して起動する

1. 作られたサーバー（Tomcat）を右クリック → **Add Deployment** → `Calculator\src\main\webapp` フォルダを指定
2. サーバーを右クリック → **Start Server**

### 4. ブラウザで開く

http://localhost:8080/webapp/calculator

- 公開されるときの名前がフォルダ名の `webapp` になるので、URL も `/webapp/` になります
- `index.jsp` を直接開いても表示されません。必ず `/calculator` から開いてください

## うまくいかないとき

| 症状 | 確認すること |
|---|---|
| 404 になる | 手順1のコンパイルをしたか、URL が `/webapp/calculator` になっているか |
| 起動しない / 「Started (Unknown)」のまま | 手順2で指定した Tomcat のパスが実在するか、8080 番ポートを他のものが使っていないか |
| Problems に `jakarta` のエラーが出る | Eclipse 用の設定が残っているために出る表示で、起動には関係ないので無視してよい |
