# ObjectEditor  
  
起動画面  
<img src="image/kidou.png?raw=true" width="100%">  

## 最初に  
実行に際して最低限必要となるのはJavaの開発環境です、
  
OSはlinuxを想定しています。  
ダウンロード後、  
  
$ cd ObjectEditor  
$ ./objedit  
  
と入力するとプログラムが起動します。  
もし起動時にエラーが出るようでしたら  
  
$ ./make_objedit  
  
と入力して再コンパイルして下さい。  
  
  
## 概要  
### 接続図(回路図)  

皆さんはどのようにしてプログラムを作っていますか？  
必要に応じて場当たり的にプログラムを作るのもひとつの方法ですが、大きなプログラムになるとそうはいかなくなってきます。  
Javaに限らず、プログラムはテキストの形で記述するのが普通で、ひとめ見ただけでプログラムの各部分の依存関係を把握することは、たとえ小さなプログラムでも容易でない場合があります。  
そこでプログラムを機能や構造によりいくつかの部分(オブジェクト)にわけてそれらの関連を図で表すと比較的容易に把握することができます。  

オブジェクトを図形で表現
<img src="image/obj-expressin.png?raw=true" width="100%">  

しかし大きなプログラムになると数多くのオブジェクトが存在し、それらの相互のデータの流れをひとつずつ書いていたら、やはり全体の把握が難しくなってきます。  

たくさんのオブジェクト
<img src="image/MANY-OBJ.png?raw=true" width="100%">  

そこでピンという概念を導入して、互いに関連する数個のオブジェクトをまとめて１つの親オブジェクトを作り、親オブジェクトの外にアクセスするためにピンを経由するようにするとオブジェクトの間の記述を簡単にできます。(いわゆる構造化分析に近い考え方です)  

見やすくしたオブジェクト  
<img src="image/easy-see-obj.png?raw=true" width="100%">  

またメソッドの呼び出しのなかには例えば"close()"や"repaint()"のようにメソッド呼び出しだけが存在し、実質的なデータを持たないものも数多くあります。そのようなメソッド呼び出しはデータの流れだけでは表現できないので、  

信号 = メソッド呼び出し + データ  

という概念を導入してオブジェクト間の関連をより明確に表現できるようにしました。  

※この接続図は構造化分析におけるデータフロー図と多少差異はありますが、同じような考えかたでシステムを分析・設計することができます。  
これからJavaなどオブジェクト指向プログラミングを始められる方はその前に(ほんの少しだけ)構造化分析・設計の本を読むことをお勧めします。多分役に立つと思いますので．．．  

### 状態図
接続図の説明で大きなオブジェクトはより小さなオブジェクトに分割できることを示しましたが、プログラムの大きさが有限である以上、この分割には限りがあり、いつかは"最小のオブジェクト"に到達します。  
このようなオブジェクトの働きを表現するにはどうすればよいでしょうか？  
皆さんもご存知のように、この世に存在するものすべては、なんらかの"状態"にあります。  
この"状態"に着目して物(オブジェクト)の振る舞いを記述する方法のひとつに状態図があります。  
状態遷移図の例(水の三態)を下図に示します。  

水の三態  
<img src="image/mass-state-model.png?raw=true" width="100%">  

この状態遷移図に上で述べたピンと信号の概念を適用すると次のようになります。  

ある状態から別の状態への遷移はピンからの信号によって引き起こされます。また遷移が完了するとピンに信号を出力します。遷移の種類は次の５種類に分類されます。  

遷移の種類  
<img src="image/transition-type.png?raw=true" width="100%">  

(1) ピンからの信号によって遷移して完了したときピンに信号を出力する  
(2)ピンからの信号によって遷移して完了しても信号を出力しない  
(3)信号なし(自動的)に遷移して完了したときピンに信号を出力する  
(4)信号なし(自動的)に遷移して完了しても信号を出力しない  
(5)複数のピンから信号を受け取り、遷移後に複数のピンに信号を出力する  


## 接続図エディタ  

接続図エディタの画面（赤い文字は説明です）  
<img src="image/OBJEDIT.png?raw=true" width="100%">  

### xobject( compleX object )  

xobjectはプログラムやデータの集まりを一つのコンポーネントで表現したものです  
名前が書いてあるボタンをクリックするとオブジェクトを記述するための接続図エディタが起動します。  
xobject  
<img src="image/XOBJECT.png?raw=true" width="100%">  

### aobject( Atomic object )  

aobjectもxobjectと同様にプログラムやデータの集まりですが、ボタンをクリックしたとき状態図エディタが起動するところが違います。  
aobject  
<img src="image/AOBJECT.png?raw=true" width="100%">  

### pin  

オブジェクトが他のオブジェクトとデータをやり取りするときに使います。pinの表示はJavaのメソッド呼出しのフォーマットに準じます。  
またpinを作成するとき、同時にエディタで編集しているオブジェクト上にもpinが作られます。この２つのpinは同一のものとして取り扱われます。  
pinの例  
<img src="image/PIN.png?raw=true" width="100%">  


### 信号線  

信号線はpinの間のデータの流れ※を表現します。  


信号線の例  
<img src="image/SIGNAL.png?raw=true" width="100%">  

※信号線の中には(1)のようにデータを含まないものがあるので、以後は"データの流れ"と呼ばずに"信号"と呼ぶことにします。  

### 信号の伝わる順序  

信号線のつけかたによって信号の伝わりかたが異なってくる場合があります。  


信号の伝わる順序  
<img src="image/signal-order.png?raw=true" width="100%">  

信号の伝わる順序が重要な場合は(1)のような使い方は避けた法がよいでしょう。  

### uobject( Uni-functional object )  

uobjectもxobjectやaobjectと同じくプログラムやデータの集まりですが、このオブジェクトでは、直接Javaソースコードを記述します。  
またuobjectのpinの数は、入力用および出力用がそれぞれ最大で１つに制限されており、その動作は入力用のpinに信号が到達すると記述されているJavaのコードを実行し、必要に応じて出力用のpinに信号を出すという、単一の関数によく似た振る舞いをします。  


uobject  
<img src="image/UOBJECT.png?raw=true" width="100%">  

 またuobjectはxobjectに変換することができます。この機能はuobjectを機能拡張するときに便利です。  
まず、編集メニューの"Xオブジェクトに変換"を選択してから、変換したいuobjectをクリックするとxobjectに変換されます。  
<img src="image/uobjtox.png?raw=true" width="100%">  


### codeclip  

codeclipはソースコードの断片で直接Javaソースプログラムに挿入されます。  
これはプロパティ変数や関数の定義など、接続図エディタや状態遷移図エディタで表現できない部分を記述するため設けられました。  


codeclip  
<img src="image/CODECLIP.png?raw=true" width="100%">  

codeclipはソースコードの一部となるので基本的にはJavaプログラムのすべての表現が使えます。  

### コンポーネントのグループ  

コンポーネントの集合を表すコンポーネントです。単独で使うのはあまり意味がありません。ツールメニューの"グループ"を選択して編集画面をクリックすると貼り付けられます。  
移動や大きさを変えたりする方法は他の種類のコンポーネントと同じです。  


グループの例  
<img src="image/GROUP.png?raw=true" width="100%">  

グループはxobjectに変換することができます。編集メニューの"Xオブジェクトに変換"を選択してグループをクリックするとxobjectに変換され、信号の出入り口には自動的にpinが設置されます。  


#### グループをxobjectに変換する  
<img src="image/GROUPTOX.png?raw=true" width="100%">  

逆にxobjectをグループに変換することもできます。編集メニューの"グループに変換"を選択してxobjectをクリックするとグループに変換されます。


#### xobjectをグループに変換する  
<img src="image/XTOGROUP.png?raw=true" width="100%">  

上で説明したグループを扱う操作を組み合わせると、オブジェクトを自在にばらしたり、組み合わせたりすることができるので、プログラムの開発･デバッグがより簡単になります。  

## 状態図エディタ  

aobjectの名前の書いてあるボタンをクリックすると状態図エディタがオブジェクトの情報を読み込んで起動します。  

状態図エディタ
<img src="image/stateeditor.png?raw=true" width="100%">  

### 状態  

名前の通りaobjectの状態を表現します。単独で使うことはあまりありません。後述の遷移とともに使います。  


状態  
<img src="image/STATE.png?raw=true" width="100%">  

### 遷移  

遷移はオブジェクトのある状態から別の状態への移り変わりを表現します。その動作はuobjectとよくにていますが、実行の流れが状態によって決められているところが異なります。  


遷移  
<img src="image/transition.png?raw=true" width="100%">  

また遷移についている赤紫色の矢印をクリックして付け替えたい状態をクリックすると付け替えることができます。  


状態を付け替える
<img src="image/transition2.png?raw=true" width="100%">  

### 部品棚

部品棚ではuobjectと遷移は同じ種類のコンポーネントとして取り扱われます。これにより、uobjectを遷移として使ったり、その逆の使い方をすることができます。  


部品棚  
<img src="image/parts-table.png?raw=true" width="100%">  

### テキストエディタ  


テキストエディタ
<img src="image/texteditor.png?raw=true" width="100%">  

### メッセージウィンドウ  


コンパイル・実行結果の表示  
<img src="image/message-window.png?raw=true" width="100%">  

### プロパティの設定ウィンドウ  

プロパティには大きく分けて"デフォルトのプロパティ"と"プロジェクトのプロパティ"の２つがあります。  
"デフォルトのプロパティ"はプロジェクトを新規作成するときにセットされるプロパティですべてのプロジェクトで共通です。  
それに対して"プロジェクトのプロパティ"はプロジェクトファイルを開いたときにセットされ、その内容はインポートするライブラリやコンパイル方法など基本的にはプロジェクトごとに異なるものです。  

設定ボタンをクリックするとプロジェクトのプロパティの設定ウィンドウが開きます。  
現在の設定内容をプロジェクトのプロパティに反映させたければ"OK"を、取り消したいときは"キャンセル"を  
設定内容をデフォルトのプロパティに設定したいときは"デフォルトに設定"を、設定内容を新規作成の状態に戻したければ"デフォルトに戻す"をそれぞれクリックして下さい。  
設定ウィンドウ   
<img src="image/SETTING.png?raw=true" width="100%">  


## GUIデザイナー  

GUI-Designer  
<img src="image/guidesigner.png?raw=true" width="100%">  

### メニューエディタ


メニューエディタ  
<img src="image/menueditor.png?raw=true" width="100%">  

## 例題  

### 1.Hello World(コンソールアプリケーション)  

最初はコンソールに"こんにちは"と表示するプログラムをつくります。  

(1) まずuobjectを貼り付けます。  

(2) 次にオブジェクトの青いところをクリックしてテキストエディタ を起動し、Javaプログラムを入力します。Javaについて分からないところがあれば、エディタの"？"がついているボタンをクリックすると、Javaのヘルプが表示されます。  

(4) 最後に"Start()"と表示されているpinとuobjectを信号線で結ぶとプログラムが完成します。このとき信号線の向きに注意して下さい。  
これの意味するところは、アプリケーションプログラムは始動するとまず"Start()"とかいてあるpinに信号が出力されるように設定されているので、この信号でuobjectに動作の指示を出しているわけです。  

できあがったプログラムのコンパイル･実行の様子を下に示します。  

例題１  
<img src="image/SAMPLE1.png?raw=true" width="100%">  

### 2.Hello World(GUIアプリケーション/ラベルを使う)  

次はGUIプログラミング( Graphical User Interface:早い話がウィンドウを使ったプログラム）に挑戦してみましょう。
ここではラベルに"こんにちは"と表示するプログラムを作ってみます。ObjectEditorを起動後、最初にGUIデザイナーを起動してウィンドウのレイアウトをデザインします。  

ＧＵＩをデザインする  
<img src="image/sample2-1.png?raw=true" width="100%">  

デザインしたら"変換"ボタンをクリックしてGUIオブジェクトを作成してからGUIデザイナーを終了します。  
あとはStartピンとGUIオブジェクトのStartピンを信号線で結ぶとプログラムは完成します。  

実行のようす  
<img src="image/sample2-2.png?raw=true" width="100%">  

GUIオブジェクトのレイアウトを変更したいときは、GUIオブジェクトの"GUI"と書いてあるボタンをクリックしてGUIオブジェクトを開いてから、GUIデザイナーの起動ボタンをクリックして下さい。  

### 3.ボタンを使う  

今度はボタンを使うプログラムをつくります。２つのボタンをクリックするたびにラベルにメッセージを表示します。  
#### レイアウト  

例題３のレイアウト  
<img src="image/sample3-1.png?raw=true" width="100%">  

#### 接続図  

例題３の接続図  
<img src="image/sample3-2.png?raw=true" width="100%">  

 








  

