
// このヘッダファイルはWindowsのAPIでJavaライクなインターフェースを実現するためのものです。
// (まだ未完成です)

// 開発･テスト環境… Borland C++ 5.5.1 for Win32(フリー版) Windows95

// 特徴
//　･単独で起動できる(特定のDLLやVMに依存しない)
//　･リソースファイルをほとんど使用していない(コンポーネントは全てソースで定義している)
//  ･かなり原始的(Javaのレイアウトマネージャーやイベントリスナーに相当するものは存在していません)

//
//　使用例(Javaを知っている人は多分何をしているか見当がつくと思います)
//#include "Ekagen.h"
//
//  class TestWin :public EWindow{
//    public:
//    void EWindowClosing(){ //イベントリスナーとイベントソースとは同一のオブジェクト
//      System::exit(0);
//    }
//  };
//
//  class Button1 : public EButton{
//    public:
//    Button1( char* txt ){
//      setText( txt );
//    }
//
//    void actionPerformed(){
//      setText("ボタンが押されました");
//    }
//  };
//
//  TestWin* win;
//  ELabel*  lbl;
//  Button1* btn;
//
//  void Emain(){            //main関数に相当する
//    win = new TestWin();
//    win->setTitle("TEST");
//    win->setBounds( 0, 0, 300, 180 ); 
//    lbl = new ELabel("Test Program");
//    win->add( lbl );
//    lbl->setBounds( 10, 10, 200, 40 );
//    btn = new Button1( "ここを押すと表示が変化します" );
//    win->add( btn );
//    btn->setBounds( 10, 70, 250, 40 );
//    win->setVisible( 1 );
//  }
//
//まだ未完成なのであえて詳細なコメントはつけていません
//もっとよく知りたい方は以下のソースをご覧ください


#include <windows.h>
#include <vector>

namespace System{
  void exit( int code ){
    PostQuitMessage( code );
  }
}

using namespace std;

HINSTANCE   CurrentProcess;
HINSTANCE   PrevProcess;
LPSTR       CommandLine;
int         WindowState;
MSG         Message;
BOOL        MouseIsCaptured = 0;
BOOL        ClassIsValid = 0;


//プロトタイプ宣言
LRESULT CALLBACK WindowProc(HWND,UINT,WPARAM,LPARAM);
void Emain();

//  Windows アプリケーションエントリー
int WINAPI WinMain(HINSTANCE    hInstance,
                   HINSTANCE    hPrevInstance,
                   LPSTR        CmdLine,
                   int          CmdShow)
{
    CurrentProcess = hInstance;
    PrevProcess = hPrevInstance;
    CommandLine = CmdLine;
    WindowState = CmdShow;

    Emain();

//  メッセージループ
    while( GetMessage( &Message, NULL, 0, 0 ) ){
      TranslateMessage( &Message );
      DispatchMessage( &Message );
    }
    return  Message.wParam;
}


// コンポーネントの基底クラス 
class EWindow{

public:
    vector< EWindow*, allocator<EWindow*> > child;
    EWindow* parent;
    HWND     hwnd;   
    int      index_void;
    int      child_count;
    char*    title;
    RECT     bounds;
//  COLOR    fcolor;
//  COLOR    bcolor;
//  FONT     font;
    EWindow* emenubar;


EWindow(){
  child.clear();
  index_void = 0;
  child_count =0; 
  hwnd = (HWND)NULL;
  parent = ( EWindow* )NULL;
  title = "";
  emenubar = NULL;
  if( !ClassIsValid ) Register();
}

EWindow( char *ti ){
  child.clear();
  index_void = 0;
  child_count =0; 
  hwnd = (HWND)NULL;
  parent = ( EWindow* )NULL;
  title = ti;
  emenubar = NULL;
  if( !ClassIsValid ) Register();
}

~EWindow(){
  for( int i = child.size(); i >= 0; i-- ){
     child[i]->~EWindow();
  }
  if( emenubar != NULL) emenubar->~EWindow();
  if( hwnd != NULL ) DestroyWindow( hwnd );
}

virtual BOOL isVisible(){
  checkStatus();
  return IsWindowVisible( hwnd );
}

virtual void setVisible( int b ){
  checkStatus();
  ShowWindow( hwnd, b );
  setTitle( title );
  setBounds( bounds );
  repaint();
}

virtual RECT getBounds(){
  if( hwnd == NULL ) return bounds;
  GetWindowRect( hwnd, &bounds );
  return( bounds );
}

virtual void  setBounds( RECT r ){  
  bounds = r;
  if( hwnd != NULL ) setBounds( bounds.left, bounds.top, bounds.right-bounds.left, bounds.bottom-bounds.top );
}
  
virtual void  setBounds( int x0, int y0, int width, int height ){
  bounds.left = x0;
  bounds.top = y0;
  bounds.right = x0 + width;
  bounds.bottom = y0 + height; 
  if( hwnd == NULL ) return;
  MoveWindow( hwnd, (WORD)x0, (WORD)y0, (WORD)width, (WORD)height, TRUE );
  EWindowResized();
}

virtual POINT getLocation(){
  POINT point;
  if( hwnd != NULL )  GetWindowRect( hwnd, &bounds );
  point.x = bounds.left;
  point.y = bounds.top;
  return( point );
}

virtual void setLocation( POINT p ){
  setLocation( p.x, p.y );
}

virtual void setLocation( int x0, int y0 ){
  int width;
  int height;
  if( hwnd != NULL ) GetWindowRect( hwnd, &bounds );
  width = bounds.right - bounds.left;
  height = bounds.bottom - bounds.top;
  bounds.top = y0;
  bounds.left = x0;
  bounds.bottom = y0 + height;
  bounds.right = x0 + width;
  if( hwnd != NULL ) MoveWindow( hwnd, (WORD)x0, (WORD)y0, (WORD)width, (WORD)height, TRUE );
  EWindowMoved();
}

// virtual COLOR   getForeground(){}
// virtual void    setForeground( COLOR c ){}
// virtual COLOR   getBackground(){}
// virtual void    setBackground( COLOR c ){}
// virtual FONT    getFont(){}
// virtual void    setFont( FONT f ){}

virtual EWindow* getParent(){
  return parent;
}

virtual void add( EWindow* e ){
  checkStatus();
  int childID;
  int mx = child.size();
  if( e == (EWindow*)NULL ) return;
  for( int i = 0; i < mx; i++ ){
    if( child[i] == e ) return;
  }
  if( index_void >= mx ){
    child.push_back( e );
    childID = index_void = ++mx;
  }
  else{
    child[ index_void ] = e;
    childID = ++index_void;
    while( ( index_void < mx ) && ( child[index_void] != (EWindow*)NULL ) ) index_void++;
  }
  e->join( this, childID );
  child_count++;
  repaint();
  EWindowAdded( e );
}

virtual void remove( int n ){
  checkStatus();
  int mx = child.size()-1;
  int k = 0;
  for( int i = 0; i <= mx; i++ ){
    if( child[i] != (EWindow*)NULL ){
      if( k == n ){
        EWindow* e = child[i];
        EWindowRemoved( e );
        e->~EWindow();
        if( i == mx ){
          do{
            child.pop_back();
          } while( ( --i >= 0 ) && ( child[i] == (EWindow*)NULL ) );
          if( ++i < index_void ) index_void = i;
          child_count--;
          return;
        }
        else child[i] = (EWindow*)NULL;
        if( i < index_void ) index_void = i;
        child_count--; 
        return;
      }
      k++;
    }
  }    
}
 
virtual void remove( EWindow* e ){
  checkStatus();
  int mx = child.size()-1;
  for( int i = 0; i <= mx; i++ ){
    if( child[i] == e ){
      EWindowRemoved( e );
      e->~EWindow();
      if( i == mx ){
        do{
          child.pop_back();
        } while( ( --i >= 0 ) && ( child[i] == (EWindow*)NULL ) );
        if( ++i < index_void ) index_void = i;
        child_count--;
        return;
      }
      else child[i] = (EWindow*)NULL;
      if( i < index_void ) index_void = i;
      child_count--;
      return;
    }
  }
}
 
virtual EWindow* getComponent( int n ){
  checkStatus();
  int mx = child.size()-1;
  int k = 0;
  for( int i = 0; i <= mx; i++ ){
    if( child[i] != (EWindow*)NULL ){
      if( k == n ) return child[i];
      k++;
    }
  }    
  child_count = k;
  return (EWindow*)NULL;
}

virtual int getComponentCount(){
  EWindow* e = getComponent(-1);
  return child_count;
}
  
virtual char* getText(){
  if( hwnd == NULL ) return title;
  int len = GetWindowTextLength( hwnd );
  char* s = (char*)malloc( len+1 );
  GetWindowText( hwnd, s, len+1 );
  return title = s;
}

virtual void setText( char* s ){
  title = s;
  if( hwnd != NULL ) SetWindowText( hwnd, s );
}

virtual void append( char* s2 ){
  char* s1 = getText();
  char* s0 = (char*)malloc( strlen( s1 ) + strlen( s2 ) + 1 );
  strcpy( s0, s1 );
  free( s1 );
  strcat( s0, s2 );
  setText( s0 );
}

virtual char* getTitle(){
  return getText();
}

virtual void setTitle( char* s ){
  setText( s );
}  

virtual void setEMenuBar( EWindow* menu ){
  checkStatus();
  emenubar = menu;
  emenubar->setOwnerWindow(this);
}

virtual EWindow* getEMenuBar(){
  checkStatus();
  return emenubar;
}

virtual void repaint(){
  checkStatus(); 
  InvalidateRect( hwnd, NULL, TRUE );
}

virtual void join( EWindow* prnt, int insID ){
  HWND hd;
  parent = prnt;
  if( parent == (EWindow*)NULL ) hd = (HWND)NULL; else hd = parent->hwnd;
  hwnd = CreateEWindow( hd, title, insID );               // この２行のコードはクリティカルセクションで囲む必要があるため、 
  if( hwnd != NULL ) SetWindowLong( hwnd, 0, (LONG)this );// Windowsのバージョンによっては動作しない可能性がある
  if( parent != (EWindow*)NULL ) parent->repaint();
}

virtual HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "EWindow",  
                  title,
                  WS_OVERLAPPEDWINDOW,
                  CW_USEDEFAULT,
                  CW_USEDEFAULT,
                  CW_USEDEFAULT,
                  CW_USEDEFAULT,
                  hd,
                  (HMENU)insID,
                  CurrentProcess,
                  NULL);
}

virtual void Register(){
  WNDCLASS           wcls;     
  wcls.style         = CS_HREDRAW|CS_VREDRAW;
  wcls.lpfnWndProc   = WindowProc;
  wcls.cbClsExtra    = 0;
  wcls.cbWndExtra    = sizeof( EWindow* );
  wcls.hInstance     = CurrentProcess;
  wcls.hIcon         = LoadIcon(NULL,IDI_APPLICATION);
  wcls.hCursor       = LoadCursor(NULL,IDC_ARROW);
  wcls.hbrBackground = (HBRUSH)GetStockObject(LTGRAY_BRUSH);
  wcls.lpszMenuName  = NULL;
  wcls.lpszClassName = "EWindow";
  BOOL b = RegisterClass(&wcls);
  ClassIsValid = 1; 
}

void checkStatus(){
  if( ( parent == (EWindow*)NULL ) && ( hwnd == (HWND)NULL ) ){
    join( NULL, 0 );
    setTitle( title );
    setBounds( bounds );
  }
}

virtual void setOwnerWindow(EWindow* e){}
virtual EWindow* getOwnerWindow(){ return NULL; }

// イベント関数

virtual void EWindowClosing(){}
virtual void EWindowMoved(){}
virtual void EWindowResized(){}
virtual void EWindowAdded( EWindow* e ){}
virtual void EWindowRemoved( EWindow* e ){}
virtual void mousePressed( int x, int y ){}
virtual void mouseReleased( int x, int y ){}
virtual void mouseMoved( int x, int y ){}
virtual void mouseDragged( int x, int y ){}
virtual void paintEWindow( HWND hwnd ){}
virtual int  CommandProc( int NotifyCode ){ return  0;} 
virtual void HScrollProc( int ScrollCode, int Position ){}
virtual void VScrollProc( int ScrollCode, int Position ){}

};

// コントロールの基底クラス 
class EControl : public EWindow{
public:

void Register(){}

void join( EWindow* prnt, int insID ){
  HWND hd;
  parent = prnt;
  if( parent == (EWindow*)NULL ) hd = (HWND)NULL; else hd = parent->hwnd;
  hwnd = CreateEWindow( hd, title, insID );
  if( parent != (EWindow*)NULL ) parent->repaint();
}

virtual HWND CreateEWindow( HWND hd, char* title, int insID ) = 0;

};

class ELabel : public EControl{

public:

ELabel(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

ELabel( char *ti ){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = ti;
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "STATIC",  
                      title,
                      WS_CHILD|WS_VISIBLE|SS_LEFT,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);

}

};


class EButton : public EControl{

public:

EButton(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

EButton( char *ti ){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = ti;
}

virtual BOOL isSelected(){
  return  ( BST_CHECKED == SendMessage( hwnd, BM_GETCHECK , 0 , 0) );
}

virtual void setSelected( BOOL b ){
  if( b ) SendMessage( hwnd, BM_SETCHECK , BST_CHECKED , 0 );
  else    SendMessage( hwnd, BM_SETCHECK , BST_UNCHECKED , 0 );
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "BUTTON",  
                      title,
                      WS_CHILD|WS_VISIBLE|BS_PUSHBUTTON,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);

}

//イベント関数
int CommandProc( int NotifyCode ){
  if( NotifyCode == BN_CLICKED ){
    actionPerformed();
  }
  return 1;
}

virtual void actionPerformed(){}

};

class ECheckBox : public EButton{

public:

ECheckBox(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "BUTTON",  
                      title,
                      WS_CHILD|WS_VISIBLE|BS_AUTOCHECKBOX,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);

}

};

class ERadioButton : public EButton{

public:

ERadioButton(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "BUTTON",  
                      title,
                      WS_CHILD|WS_VISIBLE|BS_AUTORADIOBUTTON,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);

}

};

class EGroupBox : public EButton{

public:

EGroupBox(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

EGroupBox( char* ti){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = ti;
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "BUTTON",  
                      title,
                      WS_CHILD|WS_VISIBLE|WS_GROUP|BS_GROUPBOX,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);

}

};

class ETextField : public EControl{

public:

ETextField(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

ETextField( char *ti ){
  index_void = 0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = ti;
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "EDIT",  
                      title,
                      WS_CHILD|WS_VISIBLE|ES_NOHIDESEL|ES_AUTOHSCROLL|WS_BORDER,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);
}

};

class EPasswordField : public ETextField{

public:

EPasswordField(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

EPasswordField( char *ti ){
  index_void = 0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = ti;
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "EDIT",  
                      title,
                      WS_CHILD|WS_VISIBLE|ES_NOHIDESEL|ES_AUTOHSCROLL|ES_PASSWORD | WS_BORDER,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);
}

};

class ETextArea : public EControl{

public:

ETextArea(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

ETextArea( char *ti ){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = ti;
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "EDIT",  
                      title,
                      WS_CHILD|WS_VISIBLE|WS_HSCROLL|WS_VSCROLL|ES_MULTILINE|ES_NOHIDESEL|WS_BORDER,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);
}


};


class EList : public EControl{

public:

EList(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

void add( char* str ){
  SendMessage( hwnd, LB_ADDSTRING, 0, (LPARAM)str );
}

void add( char* str, int index ){
  SendMessage( hwnd, LB_INSERTSTRING, (LONG)index , (LPARAM)str );
}

void remove( int index ){
  SendMessage( hwnd, LB_DELETESTRING, (LONG)index, 0 );
}

void remove( char* str  ){
  if( SendMessage( hwnd, LB_SELECTSTRING, -1, (LPARAM)str ) == LB_ERR ) return;
  remove( getSelectedIndex() );
}

void removeAll(){
  SendMessage( hwnd, LB_RESETCONTENT, 0, 0 );
}
 
char* getSelectedItem(){
  char*   item;
  LONG    index;
  LONG    length;

  index = (LONG)getSelectedIndex();
  if( index == LB_ERR ) return  NULL;
  length = SendMessage( hwnd, LB_GETTEXTLEN, (WPARAM)index, 0 );
  item = (char*)malloc( length + 1 );
  SendMessage( hwnd, LB_GETTEXT, index, (LPARAM)item );
  return item;
}

int getSelectedIndex(){
  return (int)SendMessage( hwnd , LB_GETCURSEL , 0 , 0 );
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "LISTBOX",  
                      title,
                      WS_CHILD|WS_VISIBLE|LBS_STANDARD,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);

}

//イベント関数
int CommandProc( int NotifyCode ){
  char* item;

  if( NotifyCode == LBN_DBLCLK ){
    if( ( item = getSelectedItem() ) != (char*)NULL ) valueChanged( item );
  }
  return 1;
}

virtual void valueChanged( char* Item ){}

};

class EComboBox : public EControl{

public:

EComboBox(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
}

void addItem( char* str ){
  SendMessage( hwnd, CB_ADDSTRING, 0, (LPARAM)str );
}

void insertItemAt( char* str, int index ){
  SendMessage( hwnd, CB_INSERTSTRING, (LONG)index , (LPARAM)str );
}

void removeItemAt( int index ){
  SendMessage( hwnd, CB_DELETESTRING, (LONG)index, 0 );
}

void removeItem( char* str  ){
  if( SendMessage( hwnd, CB_SELECTSTRING, -1, (LPARAM)str ) == CB_ERR ) return;
  removeItemAt( getSelectedIndex() );
}

void removeAllItems(){
  SendMessage( hwnd, CB_RESETCONTENT, 0, 0 );
}
 
char* getSelectedItem(){
  char*   item;
  LONG    index;
  LONG    length;

  index = (LONG)getSelectedIndex();
  if( index == CB_ERR ) return  NULL;
  length = SendMessage( hwnd, CB_GETLBTEXTLEN, (WPARAM)index, 0 );
  item = (char*)malloc( length + 1 );
  SendMessage( hwnd, CB_GETLBTEXT, index, (LPARAM)item );
  return item;
}

int getSelectedIndex(){
  return (int)SendMessage( hwnd , CB_GETCURSEL , 0 , 0 );
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "COMBOBOX",  
                      title,
                      WS_CHILD|WS_VISIBLE|CBS_SIMPLE,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);

}

//イベント関数
int CommandProc( int NotifyCode ){
  char* item;

  if( NotifyCode == CBN_DBLCLK ){
    if( ( item = getSelectedItem() ) != (char*)NULL ) actionPerformed( item );
  }
  return 1;
}

virtual void actionPerformed( char* Item ){}


};


class EScrollBar : public EControl{

public:

int minimum;
int maximum;
int value;
int unitincrement;
int blockincrement;

virtual int getValue(){
  return value;
}

virtual int getMaximum(){
  return maximum;
}

virtual int getMinimum(){
  return minimum;
}

virtual int getUnitIncrement(){
  return unitincrement;
}

virtual int getBlockIncrement(){
  return blockincrement;
}

virtual void setValue( int val ){
  value = val;
  if( value < minimum ) value = minimum;
  SetScrollPos( hwnd, SB_CTL, value, TRUE );
}

virtual void setMaximum( int max ){
  maximum = max;
  if( maximum <= minimum ) maximum = minimum + 1;
}

virtual void setMinimum( int min ){
  minimum = min;
  if( minimum >= maximum ) minimum = maximum - 1;
}

virtual void setUnitIncrement( int inc ){
  unitincrement = inc;
  if( unitincrement < 1 ) unitincrement = 1;
  if( unitincrement > maximum - minimum ) unitincrement = maximum - minimum;
}

virtual void setBlockIncrement( int inc ){
  blockincrement = inc;
  if( blockincrement < 1 ) blockincrement = 1;
  if( blockincrement > maximum - minimum ) blockincrement = maximum - minimum;
}

HWND CreateEWindow( HWND hd, char* title, int insID ) = 0;

//イベント関数
void HScrollProc( int ScrollCode, int Position ){
  ScrollProc( ScrollCode, Position );
}

void VScrollProc( int ScrollCode, int Position ){
  ScrollProc( ScrollCode, Position );
}

virtual void ScrollProc( int ScrollCode, int Position ){
  SetScrollRange( hwnd , SB_CTL, minimum, maximum, TRUE );
  switch( ScrollCode )
  {
     case    SB_LINEDOWN:
             value += unitincrement;
             if( value > maximum ) value = maximum;
             break;

     case    SB_LINEUP:
             value -= unitincrement;
             if( value < minimum )  value = minimum;
             break;

     case    SB_PAGEUP:
             value -= blockincrement;
             if( value < minimum )  value = minimum;
             break;

     case    SB_PAGEDOWN:
             value += blockincrement;
             if( value > maximum )  value = maximum;
             break;

     case    SB_THUMBPOSITION:
             value = (int)Position;
             break;

  }
  SetScrollPos( hwnd, SB_CTL, value, TRUE );
  stateChanged( value ); 
}

virtual void stateChanged( int value ){}

};

class EHScrollBar : public EScrollBar{

public:

EHScrollBar(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
  minimum = 0;
  maximum = 100;
  value = 0;
  unitincrement = 1;
  blockincrement = 10;
  SetScrollRange( hwnd , SB_CTL, minimum, maximum, TRUE);
  SetScrollPos( hwnd, SB_CTL, value, TRUE );
}

EHScrollBar( int max ){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
  minimum = 0;
  maximum = max;
  value = 0;
  unitincrement = 1;
  blockincrement = 10;
  SetScrollRange( hwnd , SB_CTL, minimum, maximum, TRUE);
  SetScrollPos( hwnd, SB_CTL, value, TRUE );
}

EHScrollBar( int min, int max, int val ){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
  minimum = min;
  maximum = max;
  value = val;
  unitincrement = 1;
  blockincrement = 10;
  SetScrollRange( hwnd , SB_CTL, minimum, maximum, TRUE);
  SetScrollPos( hwnd, SB_CTL, value, TRUE );
}


HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "SCROLLBAR",  
                      title,
                      WS_CHILD|WS_VISIBLE|SBS_HORZ,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);

}

};

class EVScrollBar : public EScrollBar{

public:

EVScrollBar(){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
  minimum = 0;
  maximum = 100;
  value = 0;
  unitincrement = 1;
  blockincrement = 10;
  SetScrollRange( hwnd , SB_CTL, minimum, maximum, TRUE);
  SetScrollPos( hwnd, SB_CTL, value, TRUE );
}

EVScrollBar( int max ){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
  minimum = 0;
  maximum = max;
  value = 0;
  unitincrement = 1;
  blockincrement = 10;
  SetScrollRange( hwnd , SB_CTL, minimum, maximum, TRUE);
  SetScrollPos( hwnd, SB_CTL, value, TRUE );
}

EVScrollBar( int min, int max, int val ){
  index_void =0;
  child_count = 0;
  child.clear();
  hwnd = (HWND)NULL;
  parent = (EWindow*)NULL;
  title = "";
  minimum = min;
  maximum = max;
  value = val;
  unitincrement = 1;
  blockincrement = 10;
  SetScrollRange( hwnd , SB_CTL, minimum, maximum, TRUE);
  SetScrollPos( hwnd, SB_CTL, value, TRUE );
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return CreateWindow(  "SCROLLBAR",  
                      title,
                      WS_CHILD|WS_VISIBLE|SBS_VERT,
                      0,
                      0,
                      10,
                      10,
                      hd,
                      (HMENU)insID,
                      CurrentProcess,
                      NULL);

}

};


class EMenuBar : public EWindow{

public:

EWindow* ownerwindow;
HMENU hmenu;
unsigned int menuID;

EMenuBar(){
  child.clear();
  index_void = 0;
  child_count =0; 
  hwnd = (HWND)NULL;
  parent = ( EWindow* )NULL;
  title = "";
  ownerwindow = NULL;
  hmenu = CreateMenu();
}

virtual EWindow* getOwnerWindow(){ return ownerwindow; }

virtual void setOwnerWindow( EWindow* e ){
  if( ( ( e->hwnd ) != NULL) && ( ownerwindow == NULL ) ){
    ownerwindow = e;
    SetMenu( e->hwnd, hmenu );
    DrawMenuBar( e->hwnd );
    for( unsigned int i = 0; i < child.size() ; i++ ){
      ( (EMenuBar*)child[i] )->joinMenu( this );
    }
  }
}

virtual void join( EWindow* prnt, int insID ){
  ownerwindow = prnt;
  menuID = insID;
}

virtual void joinMenu( EMenuBar* prnt ){
  parent = prnt;
  ownerwindow = ((EMenuBar*)parent)->ownerwindow;
  if( ownerwindow == NULL ) return;
  ownerwindow->add(this);
  AppendMenu( ( (EMenuBar*)parent)->hmenu, MF_POPUP, (unsigned int)hmenu, title );
  DrawMenuBar( ownerwindow->hwnd );
  for( unsigned int i = 0; i < child.size() ; i++ ){
    ( (EMenuBar*)child[i] )->joinMenu( this );
  }
}

virtual void add( EWindow* e ){
  child.push_back( e );
  child_count++; 
  ((EMenuBar*)e)->joinMenu( this );
}

char* getText(){
  return title;
}

void setText( char* s ){
  title = s;
}

// イベント関数
int  CommandProc( int NotifyCode ){
  actionPerformed();
  return 1;
}

void repaint(){
  if( ownerwindow != NULL){
    DrawMenuBar( ownerwindow->hwnd );
    ownerwindow->repaint();
  }
}

virtual void actionPerformed(){}

//　未実装の関数
EWindow* getParent(){
  return parent;
}
EWindow* getComponent( int n ){
 return NULL;
}

int getComponentCount(){
  return child_count;
}

BOOL isVisible(){
  return( hmenu!=NULL );
}

void setVisible( int b ){}

RECT getBounds(){
  RECT r;
  return r;
}

void  setBounds( RECT r ){}
void  setBounds( int x0, int y0, int width, int height ){}
POINT getLocation(){
  POINT x;
  return x;
}

void setLocation( POINT p ){}
void setLocation( int x0, int y0 ){}
// virtual COLOR   getForeground(){}
// virtual void    setForeground( COLOR c ){}
// virtual COLOR   getBackground(){}
// virtual void    setBackground( COLOR c ){}
// virtual FONT    getFont(){}
// virtual void    setFont( FONT f ){}
void remove( int n ){}
void remove( EWindow* e ){}
void append( char* s2 ){}
char* getTitle(){
  return NULL;
}

void setTitle( char* s ){}
void setEMenuBar( EWindow* menu ){}
EMenuBar*  getMenu(){
  return NULL;
}

HWND CreateEWindow( HWND hd, char* title, int insID ){
  return NULL;
}

void Register(){}
void checkStatus(){}
};

class EMenuItem :public EMenuBar{

public:

EMenuItem(){
  child.clear();
  index_void = 0;
  child_count =0; 
  hwnd = (HWND)NULL;
  parent = ( EWindow* )NULL;
  title = "";
  hmenu = NULL;
  ownerwindow = NULL;
}

EMenuItem( char* ti ){
  child.clear();
  index_void = 0;
  child_count =0; 
  hwnd = (HWND)NULL;
  parent = ( EWindow* )NULL;
  title = "";
  ownerwindow = NULL;
  hmenu = NULL;
  setText( ti );
}

void setOwnerWindow( EWindow* e ){}

void joinMenu( EMenuBar* prnt ){
  parent = prnt;    
  ownerwindow = ((EMenuBar*)parent)->ownerwindow;
  if( ownerwindow == NULL ) return;
  ownerwindow->add(this);
  AppendMenu( ((EMenuBar*)parent)->hmenu, MF_STRING, menuID, title );
  DrawMenuBar( ownerwindow->hwnd );
}

void add( EWindow* e ){}

};

class ESeparator :public EMenuItem{
public:
void joinMenu( EMenuBar* prnt ){
  parent = prnt;    
  ownerwindow = ((EMenuBar*)parent)->ownerwindow;
  if( ownerwindow == NULL ) return;
  ownerwindow->add(this);
  AppendMenu( ((EMenuBar*)parent)->hmenu, MF_SEPARATOR, ((EMenuBar*)parent)->menuID, title );
  DrawMenuBar( ownerwindow->hwnd );
}

};

class EMenu : public EMenuBar{
public:
EMenu(){}
EMenu( char* ti ){
  setText( ti );
}

void setOwnerWindow( EWindow* e ){}

virtual void addSeparator(){
 add( new ESeparator() );
}

};

//  ウインドウプロシージャ
LRESULT CALLBACK WindowProc(HWND hwnd,UINT message,WPARAM wparam,LPARAM lparam)
{

    WORD code;
    HWND control;
    WORD posit;

    EWindow* e = (EWindow*)GetWindowLong( hwnd, 0 );
    switch(message)
    {
        case WM_QUIT:
          return 0;

        case WM_CREATE:
          return 0;

        case WM_INITMENU:
          return 0;

        case WM_DESTROY:
          e->EWindowClosing();
          return  0;

        case WM_SIZE:
          e->EWindowResized();
          return  0;

        case    WM_LBUTTONDOWN:
          SetCapture(hwnd);
          MouseIsCaptured = 1;
          e->mousePressed( (int)LOWORD(lparam),(int)HIWORD(lparam));
          return 0;

        case    WM_LBUTTONUP:
          if(GetCapture()==hwnd){
            e->mouseReleased( (int)LOWORD(lparam),(int)HIWORD(lparam));
            ReleaseCapture();
            MouseIsCaptured = 0;
          }
          return 0;

        case    WM_MOUSEMOVE:       //  マウスが動かされた
          if( MouseIsCaptured ) e->mouseDragged( (int)LOWORD(lparam), (int)HIWORD(lparam) );
          else e->mouseMoved( (int)LOWORD(lparam), (int)HIWORD(lparam));
          return 0;

        case    WM_PAINT:           //  再描画 
          e->paintEWindow( hwnd );
          return  DefWindowProc( hwnd, message, wparam, lparam );

        case    WM_HSCROLL:
          if( lparam == (LPARAM)NULL )  e->HScrollProc( (int)LOWORD(wparam), (int)HIWORD(wparam) );
          else e->child[(int)GetWindowLong( (HWND)lparam, GWL_ID )-1]->HScrollProc( (int)LOWORD(wparam), (int)HIWORD(wparam) );
          return 0;

        case    WM_VSCROLL:
          if( lparam == (LPARAM)NULL ) e->VScrollProc( (int)LOWORD(wparam), (int)HIWORD(wparam) );
          else e->child[(int)GetWindowLong( (HWND)lparam, GWL_ID )-1]->VScrollProc( (int)LOWORD(wparam), (int)HIWORD(wparam) );
          return 0;

        case    WM_COMMAND:         //  コマンドコード
          int NotifyCode = (int)HIWORD(wparam); 
          int insID = (int)LOWORD(wparam);
          if( ( insID > 0 ) && ( e->child[insID-1]->CommandProc( NotifyCode ) != 0 ) ) return 0;

    }
    return  DefWindowProc( hwnd, message, wparam, lparam );
}
