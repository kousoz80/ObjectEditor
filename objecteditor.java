//オブジェクトエディタver1.2.7
// 変更点：Javascriptに対応
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import javax.swing.filechooser.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


//アプリケーションのクラス
class App1{
  static final String VERSION_STRING =" ObjectEditor version 1.2.7";
      

// 主体オブジェクト
  ObjectEditor   objecteditor = null;
  StateEditor    stateeditor = null;
  TreeTool       treetool = null;
  MessageWindow  messagewindow = null;
  PropertyWindow propertywindow = null;
  FileWindow     filewindow = null;
  TextEditor     texteditor = null;
  InitialDialog  initialdialog = null;
  InputDialog    inputdialog = null;
  Dialog1        dialog1 = null;
  Dialog2        dialog2 = null;
  Dialog3        dialog3 = null;
  Nxml           xml = null;
  Object         project = null;
  Object         properties = null;
  Object         clipboad = null;
  boolean       compile_ready = true;

//アプリケーションのプロパティ

  // 固定プロパティ(仕様に関係する部分で実行中の変更は不可)
  Integer Xinteger;
  String  LabelHeader = "@";
  String  TmpTextFile = "tmp"+((int)(Math.random() * (double)0x7fffffff))+".txt";
  XFile  FTmpTextFile = new XFile(TmpTextFile);
  String  TmpImageFile = "tmp"+((int)(Math.random() * (double)0x7fffffff))+".jpg";
  XFile    FTmpImageFile = new XFile(TmpImageFile);
  XFile   SetupFile;
  XFile   ProjectFile;
                                                            // 設定ファイル
  String  TransientConditionPrefix;                         // 条件遷移にするための接頭文字列  
  String  FollowIsCodePrefix;                               // ソースコード記述のための接頭文字列  
  XFile[] ObjectLib;                                        // ライブラリのパス  
  XFile[] SourceFile;                                       // ソースファイル
  XFile   GUIDesignerWork;
  XFile   LayoutData;

  XFile   CurrentDir;
  XFile   ProjectDir;
  XFile   ImageDir;

  String[] ExectableFileMode  = {
                                   "実行ファイル(jar)/jar",
                                   "実行ファイル(jar)/jar",
                                   "実行ファイル(exe)/exe",
                                   "実行ファイル(exe)/exe", 
                                   "Apkファイル(apk)/apk", 
                                   "basファイル(bas)/bas", 
                                   "実行ファイル(exe)/exe", 
                                   "実行ファイル(efi)/efi", 
                                   "",
                                   "実行ファイル(html)/html", 
  };

  String   ProjectFileMode    =    "プロジェクトファイル(prj)/prj";

  String[] SelectedFile  = {
                                   "Test.jar",
                                   "Test.jar",
                                   "Test.exe",
                                   "Test.exe",
                                   "Test.apk",
                                   "Test.bas",
                                   "Test.exe",
                                   "Test.efi",
                                   "",
                                   "Test.html",
  };

  String[] AppTypes ={
                                   "(Javaアプリケーション)",
                                   "(Java アプレット)",
                                   "(C++ コンソールアプリケーション)",
                                   "(C++ Windowsアプリケーション)",
                                   "(androidアプリケーション)",
                                   "(Basicアプリケーション)",
                                   "(C言語 アプリケーション)",
                                   "(oregengo-R言語 アプリケーション)",
                                   "(マルチ言語アプリケーション)",
                                   "(Javascriptアプリケーション)",
  };


  // 可変プロパティ
  String SDKpath;
  String CompilerFileName;

  int MainWinx0, MainWiny0, MainWinWidth, MainWinHeight;  //メインウィンドウの位置と大きさ
  int MesgWinx0, MesgWiny0, MesgWinWidth, MesgWinHeight;  //メッセージウィンドウの位置と大きさ
  int FileWinx0, FileWiny0, FileWinWidth, FileWinHeight;  //fileWindowの位置と大きさ
  int EditWinx0, EditWiny0, EditWinWidth, EditWinHeight;  //editWindowの位置と大きさ
  int PropWinx0, PropWiny0, PropWinWidth, PropWinHeight;  //PropertyWindowの位置と大きさ
  int DividerLocation1, DividerLocation2;                 //画面分割位置

  int MoveStep;

  boolean ToolBarVisible;
  boolean ViewSourceAtCompile;  
  boolean OpenCompileDialog;
  boolean NoOptimizePin;

  DefaultMutableTreeNode mae_node; //ひとつ前のオブジェクトを示すノード

  String LookandFeel;
  String JavaEditCommand;
  String ScriptExecCommand;
  String ImageEditCommand;
  String JavaViewCommand;
  String HtmlEditCommand;
  String HelpCommand;

// ここより下はアプリケーションの種類( Javaアプリケーション or アプレット )ごとに存在する
  int ApplicationType = 0;   //アプリケーションの種類( 0:Javaアプリケーション / 1:アプレット/2: C++コンソール/3:C++ Windows /4:android/5:Basic/6:C言語/7:oregengo-R/8:マルチ言語環境/9: Javascript 

  String[] CompileCommand     = { "","","","","","","","","","" };
  String[] RunCommand         = { "","","","","","","","","","" };
  String[] GUIDesignerCommand = { "","","","","","","","","","" };
  String[] ImportFiles        = { "","","","","","","","","","" };
  String[] ProgramStartupCode = { "","","","","","","","","","" };
  String[] NativeHelpCommand  = { "","","","","","","","","","" };
  String[] IDF_LocalVariable  = { "","","","","","","","","","" };

  // Javascriptのコンパイル用変数
  String HtmlBuffer = "";


  // 共通メソッドを記述

// 新しいプロジェクトを作成する
  public void newProject(){
    Object prj = xml.子要素( xml.ルート要素(), "project" );
    if( prj != null ) xml.要素を削除( prj );
    project = xml.新しい要素( xml.ルート要素(), "xobject", "project" );
    xml.属性値をセット( project, "x0", "0" );
    xml.属性値をセット( project, "y0", "0" );
    xml.属性値をセット( project, "width", "200" );
    xml.属性値をセット( project, "height","100" );
    xml.属性値をセット( project, "ID_maker", "0" );
    xml.属性値をセット( project, "objectname", "NewApplication" );
    xml.属性値をセット( project, "description", "新規のアプリケーション" );

    int apptyp = ApplicationType;
    loadProperty();
    ApplicationType = apptyp;  // loadProperty()で書き換えられたApplicationTypeを元に戻す

    ImageDir = CurrentDir;

    if( ApplicationType < 2 || ApplicationType == 4 ){
      Object clip = xml.新しい要素( project, "codeclip", "_CSTART" );
      xml.属性値をセット( clip, "x0", "10" );
      xml.属性値をセット( clip, "y0", "10" );
      xml.属性値をセット( clip, "width", "80" );
      xml.属性値をセット( clip, "height","24" );
      xml.属性値をセット( clip, "codetext", ProgramStartupCode[ ApplicationType ] );
    }

    Object pin = xml.新しい要素( project, "pin", "_PSTART" );
    xml.属性値をセット( pin, "x0", "10" );
    xml.属性値をセット( pin, "y0", "40" );
    xml.属性値をセット( pin, "width", "55" );
    xml.属性値をセット( pin, "height","24" );
    xml.属性値をセット( pin, "text", "Start()" );
    xml.属性値をセット( pin, "px", "50" );
    xml.属性値をセット( pin, "py", "50" );

  }//newProject()

// ファイルからプロジェクトをロードする
  public void loadProject(){
    if( ( ProjectFile != null ) && ProjectFile.isFile() ){
      Object prj = xml.子要素( xml.ルート要素(), "project" );
      if( prj != null ) xml.要素を削除( prj );
      project = xml.新しい要素( xml.ルート要素(), ProjectFile, "project" );
      properties = xml.子要素( project, "properties" );
      if( properties == null ) loadProperty(); else syncProperty();
    }
    else newProject(); 
    ImageDir = CurrentDir;

  }//~loadProject()

//プロジェクトをセーブする   
  public void saveProject(){
    xml.要素を保存( project, ProjectFile );
  }//~saveProject()

// プロパティをロードする
  public void loadProperty(){

    Object prj = xml.子要素( xml.ルート要素(), "project" );
    if( prj != null ){
      if( SetupFile.isFile() ){
        Object pro = xml.子要素( prj, "properties" );
        if( pro != null ) xml.要素を削除( pro );
        properties = xml.新しい要素( prj, SetupFile, "properties" );
      }
      else{
        preloadProperty();
      }

      syncProperty();
    }
    else initProperty();

  }//~loadProperty()

// Configディレクトリの中のOSに応じた設定ファイルからプロパティをロードする
  public void preloadProperty(){
    XFile config = new XFile("Config");
    XFile target = new XFile( config, System.getProperty("os.name") );
    XFile source = new XFile( target, "ObjectEditor.xml");
    if( source.isFile() ){
      source.Xcopy( SetupFile );
      if( SetupFile.isFile() ) loadProperty();
      else{
        initProperty();
        properties = xml.新しい要素( project, "プロパティ", "properties" );
        restoreProperty();
      }
    }
    else{
      initProperty();
      properties = xml.新しい要素( project, "プロパティ", "properties" );
      restoreProperty();
    }

  }//~preloadProperty()

//プロパティを初期化する
  public void initProperty(){
    boolean b;
    
    ToolBarVisible = true;
    ViewSourceAtCompile = false;
    OpenCompileDialog = false;
    NoOptimizePin = false;
    LookandFeel =UIManager.getSystemLookAndFeelClassName();
  
    MainWinx0 = 0;
    MainWiny0 = 0;
    MainWinWidth = 640;
    MainWinHeight = 400;
    MesgWinx0 = 0;
    MesgWiny0 = 400;
    MesgWinWidth = 640;
    MesgWinHeight = 150;
    FileWinx0 = -1;
    FileWiny0 = -1;
    FileWinWidth = -1;
    FileWinHeight = -1;
    EditWinx0 = -1;
    EditWiny0 = -1;
    EditWinWidth = -1;
    EditWinHeight = -1;
    PropWinx0 = -1;
    PropWiny0 = -1;
    PropWinWidth = -1;
    PropWinHeight = -1;
    DividerLocation1 = 100;
    DividerLocation2 = 24;
    MoveStep = 32;
    JavaEditCommand = "";
    ScriptExecCommand = "";
    ImageEditCommand = "";
    JavaViewCommand = "";
    HtmlEditCommand = "notepad NewApplication.html";
    HelpCommand  = "cmd.exe /c  Help\\manual.html";
    ApplicationType = 0;

    CompileCommand[0] = "cmd.exe /c scripts\\compile.bat -java";
    RunCommand[0] = "cmd.exe /c scripts\\run0.bat -java";
    GUIDesignerCommand[0] = "javaw.exe -jar guidsin.jar -java";
    NativeHelpCommand[0]  = "";
    ImportFiles[0]        = " import java.awt.*;\n"
                          + " import java.awt.event.*;\n"
                          + " import javax.swing.*;\n"
                          + " import javax.swing.event.*;\n"
                          + " import javax.swing.tree.*;\n"
                          + " import java.io.*;\n"
                          + " import java.net.*;\n"
                          + " import java.util.*;\n"
                          + " class Starter{\n"
                          + "   public static void main( String[] args ){\n"
                          + "     %AppName% ap = new %AppName%();\n"
                          + "     ap.ARGS = args;\n"
                          + "     ap.Start();\n"
                          + "   }\n"
                          + " }\n";
    ProgramStartupCode[0] = " String[] ARGS;\n";

    CompileCommand[1] = "cmd.exe /c scripts\\compile.bat -java";
    RunCommand[1] = "cmd.exe /c scripts\\run1.bat -java";
    GUIDesignerCommand[1] = "javaw.exe -jar guidsin.jar -applet";
    NativeHelpCommand[1]  = "";
    ImportFiles[1]        = " import java.awt.*;\n"
                          + " import java.awt.event.*;\n"
                          + " import java.applet.*;\n"
                          + " import javax.swing.*;\n"
                          + " import javax.swing.event.*;\n"
                          + " import javax.swing.tree.*;\n"
                          + " import java.io.*;\n"
                          + " import java.net.*;\n"
                          + " import java.util.*;\n"
                          + " public class javatext extends Applet {\n"
                          + "   public void init() {\n"
                          + "     %AppName% ap = new %AppName%();\n"
                          + "     ap.APPLET = this;\n"
                          + "     ap.Start();\n"
                          + "   }\n"
                          + " }\n";
    ProgramStartupCode[1] = " Applet APPLET;\n";

    CompileCommand[2] = "cmd.exe /c scripts\\compileCC.bat ";
    RunCommand[2] = "NewApplication.exe";
    GUIDesignerCommand[2] = "";
    NativeHelpCommand[2]  = "";
    ImportFiles[2]        = "#include <stdio.h>\n"
                          + "void Startup();\n";
    ProgramStartupCode[2] = "int main(){\n"
                          + "  Startup();\n"
                          + "  %AppName%::Start();\n"
                          + "}\n";

    CompileCommand[3] = "cmd.exe /c scripts\\compileCW.bat ";
    RunCommand[3] = "NewApplication.exe";
    GUIDesignerCommand[3] = "javaw.exe -jar guidsin.jar -cpp";
    NativeHelpCommand[3]  = "";
    ImportFiles[3]        = "#define STRICT\n"
                          + "#include <stdio.h>\n"
                          + "#include <sys/stat.h>\n"
                          + "#include \"Ekagen.h\"\n"
                          + "void Startup();\n";
    ProgramStartupCode[3] = "void Emain(){\n"
                          + "  Startup();\n"
                          + "  %AppName%::Start();\n"
                          + "}\n";

    CompileCommand[4] = "cmd.exe /c scripts\\compile_and.bat -android";
    RunCommand[4] = "cmd.exe /c scripts\\run_and.bat -android";
    GUIDesignerCommand[4] = "javaw.exe -jar guidsin.jar -android";
    NativeHelpCommand[4]  = "";
    ImportFiles[4]        = "import android.app.Activity;\n"
                          + "import android.graphics.Color;\n"
                          + "import android.os.Bundle;\n"
                          + "import android.view.Window;\n"
                          + "import android.widget.*;\n"
                          + " public class MainActivity extends Activity {\n"
                          + "   public void onCreate(Bundle bundle) {\n"
                          + "     super.onCreate(bundle);\n"
                          + "     %AppName% ap = new %AppName%();\n"
                          + "     ap.ACTIVITY = this;\n"
                          + "     ap.Start();\n"
                          + "   }\n"
                          + " }\n";
    ProgramStartupCode[4] = " Activity ACTIVITY;\n";

    IDF_LocalVariable[5]="\\";
    CompileCommand[5] = "";
    RunCommand[5] = "";
    GUIDesignerCommand[5] = "";
    NativeHelpCommand[5]  = "";
    ImportFiles[5]        = "";
    ProgramStartupCode[5] = "gosub @_PSTART\nend";

    CompileCommand[6] = "cmd.exe /c scripts\\compileCC.bat ";
    RunCommand[6] = "NewApplication.exe";
    GUIDesignerCommand[6] = "";
    NativeHelpCommand[6]  = "";
    ImportFiles[6]        = "#include <stdio.h>\n";
    ProgramStartupCode[6] = "int main(){\n"
                          + "  _PSTART();\n"
                          + "}\n";

    IDF_LocalVariable[7]="\\";
    CompileCommand[7] = "";
    RunCommand[7] = "";
    GUIDesignerCommand[7] = "";
    NativeHelpCommand[7]  = "";
    ImportFiles[7]        = "";
    ProgramStartupCode[7] = " _PSTART\nend";

    RunCommand[8] = "";

    CompileCommand[9] = "";
    RunCommand[9] = "";
    GUIDesignerCommand[9] = "";
    NativeHelpCommand[9]  = "";
    ImportFiles[9]        = "";
    ProgramStartupCode[9] = "";

  }//~initProperty(){

//プロパティをセーブする   
  public void saveProperty(){
    xml.要素を保存( properties, SetupFile );
  }//~saveProperty()


  // プロパティを現在の状態に反映させる
  public void syncProperty(){
    String t;
    if( properties == null ) return;

    LookandFeel = xml.属性値( properties, "LookandFeel" );
    if( LookandFeel == null ) LookandFeel =UIManager.getSystemLookAndFeelClassName();

    MainWinx0 = parseInt( xml.属性値( properties, "MainWinx0" ) );
    MainWiny0 = parseInt( xml.属性値( properties, "MainWiny0" ) );
    MainWinWidth = parseInt( xml.属性値( properties, "MainWinWidth" ) );
    MainWinHeight = parseInt( xml.属性値( properties, "MainWinHeight" ) );
    MesgWinx0 = parseInt( xml.属性値( properties, "MesgWinx0" ) );
    MesgWiny0 = parseInt( xml.属性値( properties, "MesgWiny0" ) );
    MesgWinWidth = parseInt( xml.属性値( properties, "MesgWinWidth" ) );
    MesgWinHeight = parseInt( xml.属性値( properties, "MesgWinHeight" ) );
    FileWinx0 = parseInt( ((t=xml.属性値( properties, "FileWinx0" ))==null)?"-1":t );
    FileWiny0 = parseInt( ((t=xml.属性値( properties, "FileWiny0" ))==null)?"-1":t );
    FileWinWidth = parseInt( ((t=xml.属性値( properties, "FileWinWidth" ))==null)?"-1":t );
    FileWinHeight = parseInt( ((t=xml.属性値( properties, "FileWinHeight" ))==null)?"-1":t );
    EditWinx0 = parseInt( ((t=xml.属性値( properties, "EditWinx0" ))==null)?"-1":t );
    EditWiny0 = parseInt( ((t=xml.属性値( properties, "EditWiny0" ))==null)?"-1":t );
    EditWinWidth = parseInt( ((t=xml.属性値( properties, "EditWinWidth" ))==null)?"-1":t );
    EditWinHeight = parseInt( ((t=xml.属性値( properties, "EditWinHeight" ))==null)?"-1":t );
    PropWinx0 = parseInt( ((t=xml.属性値( properties, "PropWinx0" ))==null)?"-1":t );
    PropWiny0 = parseInt( ((t=xml.属性値( properties, "PropWiny0" ))==null)?"-1":t );
    PropWinWidth = parseInt( ((t=xml.属性値( properties, "PropWinWidth" ))==null)?"-1":t );
    PropWinHeight = parseInt( ((t=xml.属性値( properties, "PropWinHeight" ))==null)?"-1":t );
    DividerLocation1 = parseInt( xml.属性値( properties, "DividerLocation1" ) );
    DividerLocation2 = parseInt( xml.属性値( properties, "DividerLocation2" ) );
    MoveStep = parseInt( ((t=xml.属性値( properties, "MoveStep" ))==null)?"32":t );
    ToolBarVisible = int2boolean( parseInt( xml.属性値( properties, "ToolBarVisible" ) ) );
    ViewSourceAtCompile = int2boolean( parseInt( xml.属性値( properties, "ViewSourceAtCompile" ) ) );
    OpenCompileDialog = int2boolean( parseInt( xml.属性値( properties, "OpenCompileDialog" ) ) );
    NoOptimizePin = int2boolean( parseInt( xml.属性値( properties, "NoOptimizePin" ) ) );
    JavaEditCommand = ((t=xml.属性値( properties, "JavaEditCommand" ))==null?"":t);
    ScriptExecCommand = ((t=xml.属性値( properties, "ScriptExecCommand" ))==null?"":t);
    ImageEditCommand = ((t=xml.属性値( properties, "ImageEditCommand" ))==null?"":t);
    JavaViewCommand = ((t=xml.属性値( properties, "JavaViewCommand" ))==null?"":t);
    HtmlEditCommand = ((t=xml.属性値( properties, "HtmlEditCommand" ))==null?"":t);
    HelpCommand = ((t=xml.属性値( properties, "HelpCommand" ))==null?"":t);
    ApplicationType = parseInt( xml.属性値( properties, "ApplicationType" ) );

    CompileCommand[0] = ((t=xml.属性値( properties, "CompileCommand0" ))==null?"":t);
    RunCommand[0] = ((t=xml.属性値( properties, "RunCommand0" ))==null?"":t);
    GUIDesignerCommand[0] = ((t=xml.属性値( properties, "GUIDesignerCommand0" ))==null?"":t);
    ImportFiles[0] = ((t=xml.属性値( properties, "ImportFiles0" ))==null?"":t);
    ProgramStartupCode[0] = ((t=xml.属性値( properties, "ProgramStartupCode0" ))==null?"":t);
    NativeHelpCommand[0] = ((t=xml.属性値( properties, "NativeHelpCommand0" ))==null?"":t);

    CompileCommand[1] = ((t=xml.属性値( properties, "CompileCommand1" ))==null?"":t);
    RunCommand[1] = ((t=xml.属性値( properties, "RunCommand1" ))==null?"":t);
    GUIDesignerCommand[1] = ((t=xml.属性値( properties, "GUIDesignerCommand1" ))==null?"":t);
    ImportFiles[1] = ((t=xml.属性値( properties, "ImportFiles1" ))==null?"":t);
    ProgramStartupCode[1] = ((t=xml.属性値( properties, "ProgramStartupCode1" ))==null?"":t);
    NativeHelpCommand[1] = ((t=xml.属性値( properties, "NativeHelpCommand1" ))==null?"":t);

    CompileCommand[2] = ((t=xml.属性値( properties, "CompileCommand2" ))==null?"":t);
    RunCommand[2] = ((t=xml.属性値( properties, "RunCommand2" ))==null?"":t);
    GUIDesignerCommand[2] = ((t=xml.属性値( properties, "GUIDesignerCommand2" ))==null?"":t);
    ImportFiles[2] = ((t=xml.属性値( properties, "ImportFiles2" ))==null?"":t);
    ProgramStartupCode[2] = ((t=xml.属性値( properties, "ProgramStartupCode2" ))==null?"":t);
    NativeHelpCommand[2] = ((t=xml.属性値( properties, "NativeHelpCommand2" ))==null?"":t);

    CompileCommand[3] = ((t=xml.属性値( properties, "CompileCommand3" ))==null?"":t);
    RunCommand[3] = ((t=xml.属性値( properties, "RunCommand3" ))==null?"":t);
    GUIDesignerCommand[3] = ((t=xml.属性値( properties, "GUIDesignerCommand3" ))==null?"":t);
    ImportFiles[3] = ((t=xml.属性値( properties, "ImportFiles3" ))==null?"":t);
    ProgramStartupCode[3] = ((t=xml.属性値( properties, "ProgramStartupCode3" ))==null?"":t);
    NativeHelpCommand[3] = ((t=xml.属性値( properties, "NativeHelpCommand3" ))==null?"":t);

    CompileCommand[4] = ((t=xml.属性値( properties, "CompileCommand4" ))==null?"":t);
    RunCommand[4] = ((t=xml.属性値( properties, "RunCommand4" ))==null?"":t);
    GUIDesignerCommand[4] = ((t=xml.属性値( properties, "GUIDesignerCommand4" ))==null?"":t);
    ImportFiles[4] = ((t=xml.属性値( properties, "ImportFiles4" ))==null?"":t);
    ProgramStartupCode[4] = ((t=xml.属性値( properties, "ProgramStartupCode4" ))==null?"":t);
    NativeHelpCommand[4] = ((t=xml.属性値( properties, "NativeHelpCommand4" ))==null?"":t);

    IDF_LocalVariable[5] = ((t=xml.属性値( properties, "IDF_LocalVariable5" ))==null?"\\":t);
    CompileCommand[5] = ((t=xml.属性値( properties, "CompileCommand5" ))==null?"":t);
    RunCommand[5] = ((t=xml.属性値( properties, "RunCommand5" ))==null?"":t);
    GUIDesignerCommand[5] = ((t=xml.属性値( properties, "GUIDesignerCommand5" ))==null?"":t);
    ImportFiles[5] = ((t=xml.属性値( properties, "ImportFiles5" ))==null?"":t);
    ProgramStartupCode[5] = ((t=xml.属性値( properties, "ProgramStartupCode5" ))==null?"":t);
    NativeHelpCommand[5] = ((t=xml.属性値( properties, "NativeHelpCommand5" ))==null?"":t);

    CompileCommand[6] = ((t=xml.属性値( properties, "CompileCommand6" ))==null?"":t);
    RunCommand[6] = ((t=xml.属性値( properties, "RunCommand6" ))==null?"":t);
    GUIDesignerCommand[6] = ((t=xml.属性値( properties, "GUIDesignerCommand6" ))==null?"":t);
    ImportFiles[6] = ((t=xml.属性値( properties, "ImportFiles6" ))==null?"":t);
    ProgramStartupCode[6] = ((t=xml.属性値( properties, "ProgramStartupCode6" ))==null?"":t);
    NativeHelpCommand[6] = ((t=xml.属性値( properties, "NativeHelpCommand6" ))==null?"":t);

    IDF_LocalVariable[7] = ((t=xml.属性値( properties, "IDF_LocalVariable7" ))==null?"\\":t);
    CompileCommand[7] = ((t=xml.属性値( properties, "CompileCommand7" ))==null?"":t);
    RunCommand[7] = ((t=xml.属性値( properties, "RunCommand7" ))==null?"":t);
    GUIDesignerCommand[7] = ((t=xml.属性値( properties, "GUIDesignerCommand7" ))==null?"":t);
    ImportFiles[7] = ((t=xml.属性値( properties, "ImportFiles7" ))==null?"":t);
    ProgramStartupCode[7] = ((t=xml.属性値( properties, "ProgramStartupCode7" ))==null?"":t);
    NativeHelpCommand[7] = ((t=xml.属性値( properties, "NativeHelpCommand7" ))==null?"":t);

    RunCommand[8] = ((t=xml.属性値( properties, "RunCommand8" ))==null?"":t);

    CompileCommand[9] = ((t=xml.属性値( properties, "CompileCommand9" ))==null?"":t);
    RunCommand[9] = ((t=xml.属性値( properties, "RunCommand9" ))==null?"":t);
    GUIDesignerCommand[9] = ((t=xml.属性値( properties, "GUIDesignerCommand9" ))==null?"":t);
    ImportFiles[9] = ((t=xml.属性値( properties, "ImportFiles9" ))==null?"":t);
    ProgramStartupCode[9] = ((t=xml.属性値( properties, "ProgramStartupCode9" ))==null?"":t);
    NativeHelpCommand[9] = ((t=xml.属性値( properties, "NativeHelpCommand9" ))==null?"":t);

    if( objecteditor != null ){

          messagewindow.setBounds( MesgWinx0, MesgWiny0, MesgWinWidth, MesgWinHeight );
          filewindow.setBounds( FileWinx0, FileWiny0, FileWinWidth, FileWinHeight );
          texteditor.setBounds( EditWinx0, EditWiny0, EditWinWidth, EditWinHeight );
          propertywindow.setBounds( PropWinx0, PropWiny0, PropWinWidth, PropWinHeight );

          if( objecteditor.gui.isVisible() ){
            objecteditor.gui.setBounds( MainWinx0, MainWiny0, MainWinWidth, MainWinHeight );
            objecteditor.gui.display.setDividerLocation( DividerLocation1 );
            objecteditor.gui.contents.setDividerLocation( DividerLocation2 );
          }
          else if( stateeditor.gui.isVisible() ){
            stateeditor.gui.setBounds( MainWinx0, MainWiny0, MainWinWidth, MainWinHeight );
            stateeditor.gui.display.setDividerLocation( DividerLocation1 );
            stateeditor.gui.contents.setDividerLocation( DividerLocation2 );
          }

    }

//Syetem.out.println("sync property end");
  }//~syncProperty()

  // 現在の状態をプロパティに反映させる
  public void restoreProperty(){


//Syetem.out.println("restotre property");

    if( objecteditor != null ){

//Syetem.out.println("restotre property execute");

          MesgWinx0 = messagewindow.getLocation().x;
          MesgWiny0 = messagewindow.getLocation().y;
          MesgWinWidth = messagewindow.getWidth();
          MesgWinHeight = messagewindow.getHeight();
          FileWinx0 = filewindow.getLocation().x;
          FileWiny0 = filewindow.getLocation().y;
          FileWinWidth = filewindow.getWidth();
          FileWinHeight = filewindow.getHeight();
          EditWinx0 = texteditor.getLocation().x;
          EditWiny0 = texteditor.getLocation().y;
          EditWinWidth = texteditor.getWidth();
          EditWinHeight = texteditor.getHeight();
          PropWinx0 = propertywindow.getLocation().x;
          PropWiny0 = propertywindow.getLocation().y;
          PropWinWidth = propertywindow.getWidth();
          PropWinHeight = propertywindow.getHeight();
          if( objecteditor.gui.isVisible() ){
            MainWinx0 = objecteditor.gui.getLocation().x;
            MainWiny0 = objecteditor.gui.getLocation().y;
            MainWinWidth = objecteditor.gui.getWidth();
            MainWinHeight = objecteditor.gui.getHeight();
            DividerLocation1 = objecteditor.gui.display.getDividerLocation();
            DividerLocation2 = objecteditor.gui.contents.getDividerLocation();
          }
          else if( stateeditor.gui.isVisible() ){
            MainWinx0 = stateeditor.gui.getLocation().x;
            MainWiny0 = stateeditor.gui.getLocation().y;
            MainWinWidth = stateeditor.gui.getWidth();
            MainWinHeight = stateeditor.gui.getHeight();
            DividerLocation1 = stateeditor.gui.display.getDividerLocation();
            DividerLocation2 = stateeditor.gui.contents.getDividerLocation();
          }

    }
    xml.属性値をセット( properties, "LookandFeel", LookandFeel );
    xml.属性値をセット( properties, "MainWinx0", "" + MainWinx0 );
    xml.属性値をセット( properties, "MainWiny0", "" + MainWiny0 );
    xml.属性値をセット( properties, "MainWinWidth", "" + MainWinWidth );
    xml.属性値をセット( properties, "MainWinHeight", "" + MainWinHeight );
    xml.属性値をセット( properties, "MesgWinx0", "" + MesgWinx0 );
    xml.属性値をセット( properties, "MesgWiny0", "" + MesgWiny0 );
    xml.属性値をセット( properties, "MesgWinWidth", "" + MesgWinWidth );
    xml.属性値をセット( properties, "MesgWinHeight", "" + MesgWinHeight );
    xml.属性値をセット( properties, "FileWinx0", "" + FileWinx0 );
    xml.属性値をセット( properties, "FileWiny0", "" + FileWiny0 );
    xml.属性値をセット( properties, "FileWinWidth", "" + FileWinWidth );
    xml.属性値をセット( properties, "FileWinHeight", "" + FileWinHeight );
    xml.属性値をセット( properties, "EditWinx0", "" + EditWinx0 );
    xml.属性値をセット( properties, "EditWiny0", "" + EditWiny0 );
    xml.属性値をセット( properties, "EditWinWidth", "" + EditWinWidth );
    xml.属性値をセット( properties, "EditWinHeight", "" + EditWinHeight );
    xml.属性値をセット( properties, "PropWinx0", "" + PropWinx0 );
    xml.属性値をセット( properties, "PropWiny0", "" + PropWiny0 );
    xml.属性値をセット( properties, "PropWinWidth", "" + PropWinWidth );
    xml.属性値をセット( properties, "PropWinHeight", "" + PropWinHeight );
    xml.属性値をセット( properties, "DividerLocation1", "" + DividerLocation1 );
    xml.属性値をセット( properties, "DividerLocation2", "" + DividerLocation2 );
    xml.属性値をセット( properties, "MoveStep", "" + MoveStep );
    xml.属性値をセット( properties, "ToolBarVisible", "" + boolean2int(ToolBarVisible) );
    xml.属性値をセット( properties, "ViewSourceAtCompile", "" + boolean2int(ViewSourceAtCompile) );
    xml.属性値をセット( properties, "OpenCompileDialog", "" + boolean2int(OpenCompileDialog) );
    xml.属性値をセット( properties, "NoOptimizePin", "" + boolean2int(NoOptimizePin) );
    xml.属性値をセット( properties, "JavaEditCommand", JavaEditCommand );
    xml.属性値をセット( properties, "ScriptExecCommand", ScriptExecCommand );
    xml.属性値をセット( properties, "ImageEditCommand", ImageEditCommand );
    xml.属性値をセット( properties, "JavaViewCommand", JavaViewCommand );
    xml.属性値をセット( properties, "HtmlEditCommand", HtmlEditCommand );
    xml.属性値をセット( properties, "HelpCommand", HelpCommand );
    xml.属性値をセット( properties, "ApplicationType", "" + ApplicationType );

    xml.属性値をセット( properties, "CompileCommand0", CompileCommand[0] );
    xml.属性値をセット( properties, "RunCommand0", RunCommand[0] );
    xml.属性値をセット( properties, "GUIDesignerCommand0", GUIDesignerCommand[0] );
    xml.属性値をセット( properties, "ImportFiles0", ImportFiles[0] );
    xml.属性値をセット( properties, "ProgramStartupCode0", ProgramStartupCode[0] );
    xml.属性値をセット( properties, "NativeHelpCommand0", NativeHelpCommand[0] );

    xml.属性値をセット( properties, "CompileCommand1", CompileCommand[1] );
    xml.属性値をセット( properties, "RunCommand1", RunCommand[1] );
    xml.属性値をセット( properties, "GUIDesignerCommand1", GUIDesignerCommand[1] );
    xml.属性値をセット( properties, "ImportFiles1", ImportFiles[1] );
    xml.属性値をセット( properties, "ProgramStartupCode1", ProgramStartupCode[1] );
    xml.属性値をセット( properties, "NativeHelpCommand1", NativeHelpCommand[1] );

    xml.属性値をセット( properties, "CompileCommand2", CompileCommand[2] );
    xml.属性値をセット( properties, "RunCommand2", RunCommand[2] );
    xml.属性値をセット( properties, "GUIDesignerCommand2", GUIDesignerCommand[2] );
    xml.属性値をセット( properties, "ImportFiles2", ImportFiles[2] );
    xml.属性値をセット( properties, "ProgramStartupCode2", ProgramStartupCode[2] );
    xml.属性値をセット( properties, "NativeHelpCommand2", NativeHelpCommand[2] );

    xml.属性値をセット( properties, "CompileCommand3", CompileCommand[3] );
    xml.属性値をセット( properties, "RunCommand3", RunCommand[3] );
    xml.属性値をセット( properties, "GUIDesignerCommand3", GUIDesignerCommand[3] );
    xml.属性値をセット( properties, "ImportFiles3", ImportFiles[3] );
    xml.属性値をセット( properties, "ProgramStartupCode3", ProgramStartupCode[3] );
    xml.属性値をセット( properties, "NativeHelpCommand3", NativeHelpCommand[3] );

    xml.属性値をセット( properties, "CompileCommand4", CompileCommand[4] );
    xml.属性値をセット( properties, "RunCommand4", RunCommand[4] );
    xml.属性値をセット( properties, "GUIDesignerCommand4", GUIDesignerCommand[4] );
    xml.属性値をセット( properties, "ImportFiles4", ImportFiles[4] );
    xml.属性値をセット( properties, "ProgramStartupCode4", ProgramStartupCode[4] );
    xml.属性値をセット( properties, "NativeHelpCommand4", NativeHelpCommand[4] );

    xml.属性値をセット( properties, "IDF_LocalVariable5", IDF_LocalVariable[5] );
    xml.属性値をセット( properties, "CompileCommand5", CompileCommand[5] );
    xml.属性値をセット( properties, "RunCommand5", RunCommand[5] );
    xml.属性値をセット( properties, "GUIDesignerCommand5", GUIDesignerCommand[5] );
    xml.属性値をセット( properties, "ImportFiles5", ImportFiles[5] );
    xml.属性値をセット( properties, "ProgramStartupCode5", ProgramStartupCode[5] );
    xml.属性値をセット( properties, "NativeHelpCommand5", NativeHelpCommand[5] );

    xml.属性値をセット( properties, "CompileCommand6", CompileCommand[6] );
    xml.属性値をセット( properties, "RunCommand6", RunCommand[6] );
    xml.属性値をセット( properties, "GUIDesignerCommand6", GUIDesignerCommand[6] );
    xml.属性値をセット( properties, "ImportFiles6", ImportFiles[6] );
    xml.属性値をセット( properties, "ProgramStartupCode6", ProgramStartupCode[6] );
    xml.属性値をセット( properties, "NativeHelpCommand6", NativeHelpCommand[6] );

    xml.属性値をセット( properties, "IDF_LocalVariable7", IDF_LocalVariable[7] );
    xml.属性値をセット( properties, "CompileCommand7", CompileCommand[7] );
    xml.属性値をセット( properties, "RunCommand7", RunCommand[7] );
    xml.属性値をセット( properties, "GUIDesignerCommand7", GUIDesignerCommand[7] );
    xml.属性値をセット( properties, "ImportFiles7", ImportFiles[7] );
    xml.属性値をセット( properties, "ProgramStartupCode7", ProgramStartupCode[7] );
    xml.属性値をセット( properties, "NativeHelpCommand7", NativeHelpCommand[7] );

    xml.属性値をセット( properties, "RunCommand8", RunCommand[8] );

    xml.属性値をセット( properties, "CompileCommand9", CompileCommand[9] );
    xml.属性値をセット( properties, "RunCommand9", RunCommand[9] );
    xml.属性値をセット( properties, "GUIDesignerCommand9", GUIDesignerCommand[9] );
    xml.属性値をセット( properties, "ImportFiles9", ImportFiles[9] );
    xml.属性値をセット( properties, "ProgramStartupCode9", ProgramStartupCode[9] );
    xml.属性値をセット( properties, "NativeHelpCommand9", NativeHelpCommand[9] );

  }//~restoreProperty()

  // lookandfeelを更新する
  public void setlookandfeel(){
    try{

      UIManager.setLookAndFeel(LookandFeel);
	    SwingUtilities.updateComponentTreeUI(objecteditor.gui);
	    SwingUtilities.updateComponentTreeUI(stateeditor.gui);
	    SwingUtilities.updateComponentTreeUI(texteditor);
	    SwingUtilities.updateComponentTreeUI(messagewindow);
	    SwingUtilities.updateComponentTreeUI(propertywindow);
	    SwingUtilities.updateComponentTreeUI(filewindow);
  	    SwingUtilities.updateComponentTreeUI(initialdialog);
  	    SwingUtilities.updateComponentTreeUI(inputdialog);
  	    SwingUtilities.updateComponentTreeUI(dialog1);
  	    SwingUtilities.updateComponentTreeUI(dialog2);
  	    SwingUtilities.updateComponentTreeUI(dialog3);
    } catch( Exception ex ) {System.out.println("exception:\n"+ex);}
  }
  

  // プログラムを終了する
  public void exitProgram(){
    FTmpTextFile.Xdelete();
    FTmpImageFile.Xdelete();
    System.exit(0);
  }
  
  // boolean を intに変換する    
  public int boolean2int( boolean b ){ if(b) return(1); else return(0); }
  
  // int を booleanに変換する
  public boolean int2boolean( int i){  return( i!= 0 ); }
  
  //エラーを報告する
  public void reportError( Object o ){
    System.out.println( o );
  }

  // 与えられたコマンドをＯＳに発行し実行させる(mode が trueなら終了までまつ falseならばすぐに次の処理にうつる)
  public void execute( String s, boolean mode ){
        Process p=null;
        int ExitCode;
        
        try{
          p = java.lang.Runtime.getRuntime().exec(s);
        } catch( Exception ie ){ reportError(s+"は実行できません\n" ); }
        if( ( p!= null ) && mode ){
          try{
            ExitCode = p.waitFor();
          } catch( InterruptedException ie ){ }
        }
  }
  
  //文字列を整数に変換する
  public int parseInt( String s ){
    if( s == null  || s.length() == 0 ) return 0;
    char c = s.charAt(0);
    if ( c < '0' || c > '9' ) return 0;
    return( Xinteger.parseInt( s ) );
  }
      
  // 文字列の1行目を取り出す
  public String getFirstLine( String buf ){
    int p = buf.indexOf( '\n' );
    if( p < 0 ) return( buf );
    else if( p == 0 ) return( "" );
    else return( buf.substring( 0, p ) ); 
  }

  // 文字列の2行目以降を取り出す
  public String getNextLines( String buf ){
    int p = buf.indexOf( '\n' );
    if( ( p < 0 ) || ( p > buf.length() - 2 ) ) return( "" );
    else return( buf.substring( p + 1 ) ); 
  }

  //文字列の部分文字列str1をstr2に置き換えた文字列を返す
  public String Xreplace( String base, String str1, String str2 ){
     int p = base.indexOf( str1 );
     if( p < 0 ) return( base );
     else if( p == 0 ) return( str2 + Xreplace( base.substring( str1.length() ), str1, str2 ) );
     else return( base.substring( 0, p ) + str2 + Xreplace( base.substring( p + str1.length() ), str1, str2 ) );
  }

  //文字列からファイル名に含まれてはならない文字を取り除く
  public String compack( String buf ){
    String r;
    char c;
    int i;

    r="";
    for( i = 0; i < buf.length(); i++ ){
      c = buf.charAt(i);
      if( (c!='\n') && (c!='\t') && (c!='\r') && (c!='\\') && (c!='\"') && (c!='\'') && 
          (c!='/' ) && (c!=':' ) && (c!=';' ) && (c!='.' ) && (c!='*' ) && (c!='?' ) &&
          (c!='+' ) && (c!='<' ) && (c!='>' ) && (c!=' ' ) && (c!='=' ) && (c!='-' ) &&
          (c!='|' ) && (c!='^' ) && (c!='&' ) && (c!='(' ) && (c!=')' ) && (c!='[' ) &&
          (c!=']' ) && (c!='{' ) && (c!='}' ) && (c!='!' ) && (c!='#' ) && (c!='$' ) &&
          (c!='%' ) && (c!='~' )
      ){
        r = r + c;
      }
    }
    return( r );
  }

  // ２つのコンポーネントを接続する線の端点を求める
  public Point getBorderPoint( int x1, int y1, int width1, int height1, int x2, int y2, int width2, int height2 ){
    int   xo, yo, rx, ry;
    float g1, g2;
        
    xo = x1 + width1  / 2;
    yo = y1 + height1 / 2;
    rx = x2 + width2  / 2 - xo;
    ry = y2 + height2 / 2 - yo;
    if( width1 == 0 || height1 == 0 || ( rx == 0 && ry == 0 ) ) return( new Point( -1, -1 ) );
    if( rx == 0 ){
      if(ry > 0) return( new Point( xo, y1 + height1 ) );
      else       return( new Point( xo, y1 ) );
    }
    g1 = (float)height1 / width1;
    g2 = (float)ry / rx;
    if( -g1 <=  g2 &&  g2 <= g1 ){
      if( rx > 0 ) return( new Point( x1 + width1,   yo + (int)( g2 * width1 / 2.0F ) ) );
      else         return( new Point( x1,  yo - (int)( g2 * width1 / 2.0F ) ) );
    }
    else{
      if( ry > 0 ) return( new Point( xo + (int)( height1 / g2 / 2.0F ), y1 + height1 ) );
      else         return( new Point( xo - (int)( height1 / g2 / 2.0F ), y1 ) );
    }
  }
       
  //メソッド文字列からメソッド名を取り出す
  public String getbase( String method ){
    return( method.substring( 0, method.indexOf( '(' ) ) );
  }

  // メソッド文字列から添字を取り出す
  public String getsubscript( String method ){
    return( method.substring( method.indexOf( '(' ) + 1, method.indexOf( ')' ) ) );
  }

  //メソッド文字列からシグニチャを取り出す。このとき型宣言部(int,doubleなど)は除去される
  public String getsignature( String method ){
    int i, j;
    StringBuffer buf1, buf2;
       
    buf1 = new StringBuffer( method );
    for( i = 0;( i < buf1.length() ) && ( buf1.charAt(i) != '(' ); i++ ) ;
    if( ( i == 0 ) || ( i >= buf1.length()-1 ) ) return( "<<<ERROR="+method+">>>" );
    buf1 = buf1.delete( 0, i + 1 );
    for( i = 0;( i < buf1.length() ) && ( buf1.charAt(i) != ')' ); i++ ) ;
    if( i == 0 ) return( "" );
    if( i >= buf1.length() ) return( "<<<ERROR="+method+">>>" );
    buf1 = buf1.delete( i, buf1.length() ).reverse();
    buf2 = new StringBuffer("");
    i = 0;
    while( true ){
      while( ( i < buf1.length() ) && ( buf1.charAt(i) == ' ' ) ) i++;
      if( i >= buf1.length() ) return( buf2.reverse().toString() );
      j = i;
      while( ( j < buf1.length() ) && ( buf1.charAt(j) != ' ' ) && ( buf1.charAt(j) != ',' ) ) j++;
      buf2.append( buf1.substring( i, j) );
      if( j >= buf1.length() ) return( buf2.reverse().toString() );
      while( ( j < buf1.length() ) && ( buf1.charAt(j) != ',' ) ) j++;
      if( j == buf1.length() ) return( buf2.reverse().toString() );
      buf2.append( "," );
      i = j + 1;
    }
  }
     
  // メソッド文字列からシグニチャを取り出して宣言文に変換する
  public String getdeclare( String method ){
    int i, j;
    StringBuffer buf1, buf2;
      
    buf1 = new StringBuffer( method );
    for( i = 0;( i < buf1.length() ) && ( buf1.charAt(i) != '(' ); i++ ) ;
    if( ( i == 0 ) || ( i >= buf1.length()-1 ) ) return( "<<<ERROR="+method+">>>\n" );
    buf1 = buf1.delete( 0, i + 1 );
    for( i = 0;( i < buf1.length() ) && ( buf1.charAt(i) != ')' ); i++ ) ;
    if( i == 0 ) return( "\n" );
    if( i >= buf1.length() ) return( "<<<ERROR="+method+">>>\n" );
    buf1 = buf1.delete( i, buf1.length() );
    buf2 = new StringBuffer("");
    while( true ){
      int l = buf1.length();
      for( i = 0; ( i < l ) && ( buf1.charAt(i) != ',' ); i++ ) ;
      buf2.append( buf1.substring( 0, i ) + ";\n" );
      buf1 = buf1.delete( 0, i + 1 );
      if( i >= l - 1 ) return( buf2.toString() );
    }
  }
      
// オブジェクトやピンのフルパス名を返す (C++)
  public String getAbsoluteName( Object elem ){
    if( elem == null ) return("");
    String ptyp = xml.要素の名前( xml.親要素( elem ) );
    String typ = xml.要素の名前( elem );
    if( typ.equals("xobject" ) ){
      if( ptyp.equals("xobject") ) return( getAbsoluteName( xml.親要素( elem ) ) + "::" + xml.属性値( elem, "objectname" ) );
      else return( xml.属性値( elem, "objectname" ) );
    }
    else if( typ.equals("aobject" ) ){
      if( ptyp.equals("xobject") ) return( getAbsoluteName( xml.親要素( elem ) ) + "::" + xml.属性値( elem, "objectname" ) );
      else return( xml.属性値( elem, "objectname" ) );
    }
    else if( typ.equals("pin" ) ){
      if( ptyp.equals("xobject") || ptyp.equals("aobject") ) return( getAbsoluteName( xml.親要素( elem ) ) + "::" + xml.属性値( elem, "text" ) );
      else return( "" );
    }
    else if( typ.equals("operation" ) ){
      if( ptyp.equals("xobject") || ptyp.equals("aobject") ) return( getAbsoluteName( xml.親要素( elem ) ) + "::" + xml.要素のID( elem ) + "_" + xml.属性値( elem, "inpintext" ) );
      else return( "" );
    }
    else if( typ.equals("state" ) ){
      if( ptyp.equals("aobject") ) return( getAbsoluteName( xml.親要素( elem ) ) + "::" + xml.要素のID( elem ) );
      else return( "" );
    }
    else return("");
  }

// オブジェクトやピンのフルパス名を返す(Basic, C言語, oregengo-R, Javascript) 
  public String getAbsoluteName2( Object elem ){
    String buf =  null;
    if( xml.属性値( xml.親要素( elem ), "レイアウト" ) == null ) buf =  "_"+elem.hashCode();
    String typ = xml.要素の名前( elem );
    if( typ.equals("state" ) ){
       buf = xml.要素のID( elem );
    }
    else if( typ.equals("pin" ) ) {
       if( xml.要素のID( elem ).equals("_PSTART") ) buf = "_PSTART()";
	     else{
          if( buf==null ) buf = xml.属性値( elem, "text" );
          else  buf = buf + "_" + xml.属性値( elem, "text" );
       }
    }
    else if( typ.equals("operation" ) ){
       buf = "_"+elem.hashCode()+"_"+xml.属性値( elem, "inpintext" ) ;
    }
    return buf;
  }


  // プロジェクトをコンパイルする
  public void compile_project(Object top_element){

    // 現在のプロパティを退避する
    restoreProperty();
    Object prop0 = properties;

    // プロパティを更新する
    Object elem = top_element;
    while(true){
      Object p = xml.子要素(elem, "properties");
      if(p != null){
        properties = p;
        break;
      }
      if(elem == project) break; // ここが有効になることは多分ない
      elem = xml.親要素(elem);
    }
    syncProperty(); // 現在の状態を更新したプロパティにあわせる

    // マルチ言語環境
    if(ApplicationType == 8){
      Vector lst = xml.子要素のリスト(top_element, "xobject"); // プロジェクトオブジェクト以外は無視する
      for(int i = 0; i < lst.size(); i++){
        Object proj = lst.get(i);
        Object prop = xml.子要素(proj, "properties");
        if(prop != null){
          properties = prop;
          syncProperty();
          compile_project(proj);
        }
      }
 
      // 退避したプロパティを元に戻す
      properties = prop0;
      syncProperty();
      return;
	}

    // マルチ言語環境以外のコンパイル処理
    boolean selected = true;
    String target = "\"noname\"";
    if(OpenCompileDialog){
      XFileFilter filter = new XFileFilter( ExectableFileMode[ApplicationType] );
      JFileChooser xchooser = new JFileChooser( CurrentDir );
      xchooser.setFileFilter(filter);
      xchooser.setSelectedFile( new XFile( CurrentDir, SelectedFile[ApplicationType]) );
      selected = (xchooser.showDialog(objecteditor.gui, "実行ファイルの生成") == JFileChooser.APPROVE_OPTION);
      if(selected) target = " \""  + xchooser.getSelectedFile().getAbsolutePath() +  "\"";
    }
    if(selected) {
      SourceFile[ApplicationType].Xdelete();

      // Java
      if( ApplicationType == 0 || ApplicationType == 1 || ApplicationType == 4 ){
        String s = ImportFiles[ApplicationType]
                 + compile_JAVA( top_element, true, new Vector() );
        s = Xreplace( s, "%AppName%", xml.属性値( top_element, "objectname" ) );
        SourceFile[ApplicationType].Xappend( s );

        // android Java
        if( ApplicationType == 4 ){
          try{
            BufferedWriter dout = new BufferedWriter( new FileWriter( new File("AndroidManifest.xml") ) );
            dout.write( NativeHelpCommand[4]  );
            dout.close();
          } catch( IOException e ){  }
        }
      }

      // C++
      else  if( ApplicationType ==2 || ApplicationType == 3 ){
        StringBuffer clsbuf  = new StringBuffer("");
        StringBuffer funcbuf = new StringBuffer("");
        StringBuffer initbuf = new StringBuffer("");
        compile_CPP( top_element, clsbuf, funcbuf, initbuf, new Vector() );
        String s = ImportFiles[ApplicationType]
                 + clsbuf.toString() + "\n"
                 + funcbuf.toString()
                 + "\nvoid Startup(){\n" + initbuf.toString() +" \n}\n"
                 + ProgramStartupCode[ApplicationType];
        s = Xreplace( s, "%AppName%", xml.属性値( top_element, "objectname" ) );
        SourceFile[ApplicationType].Xappend( s );
      } 

      // Basic
      else  if( ApplicationType == 5 ){
        StringBuffer clsbuf  = new StringBuffer("");
        StringBuffer funcbuf = new StringBuffer("");
        StringBuffer initbuf = new StringBuffer("");
        compile_BASIC( top_element, clsbuf, funcbuf, initbuf, new Vector() );
        String s = ImportFiles[ApplicationType]
                 + clsbuf.toString() 
                 + initbuf.toString()
                 + ProgramStartupCode[ApplicationType]
                 + funcbuf.toString();
        s = Xreplace( s, "%AppName%", xml.属性値( top_element, "objectname" ) );
        SourceFile[ApplicationType].Xappend( s );
      } 

      // C言語
      else  if( ApplicationType == 6 ){
        StringBuffer clsbuf  = new StringBuffer("");
        StringBuffer funcbuf = new StringBuffer("");
        StringBuffer initbuf = new StringBuffer("");
        compile_C( top_element, clsbuf, funcbuf, initbuf, new Vector() );
        String s = ImportFiles[ApplicationType]
                 + clsbuf.toString() 
                 + initbuf.toString()
                 + ProgramStartupCode[ApplicationType]
                 + funcbuf.toString();
        s = Xreplace( s, "%AppName%", xml.属性値( top_element, "objectname" ) );
        SourceFile[ApplicationType].Xappend( s );
      } 

      // oregengo-R
      else  if( ApplicationType == 7 ){
        StringBuffer clsbuf  = new StringBuffer("");
        StringBuffer funcbuf = new StringBuffer("");
        StringBuffer initbuf = new StringBuffer("");
        compile_oregengo_R( top_element, clsbuf, funcbuf, initbuf, new Vector() );
        String s = ImportFiles[ApplicationType]
                 + clsbuf.toString() 
                 + "\n_INIT_STATES:\n"+initbuf.toString()+"\n end\n"
                 + ProgramStartupCode[ApplicationType]
                 + funcbuf.toString();
        s = Xreplace( s, "%AppName%", xml.属性値( top_element, "objectname" ) );
        SourceFile[ApplicationType].Xappend( s );
      } 

      // Javascript
      else  if( ApplicationType == 9 ){
        HtmlBuffer = "";
        StringBuffer clsbuf  = new StringBuffer("");
        StringBuffer funcbuf = new StringBuffer("");
        StringBuffer initbuf = new StringBuffer("");
        compile_javascript( top_element, clsbuf, funcbuf, initbuf, new Vector() );
        String s = ImportFiles[ApplicationType]
                 + clsbuf.toString() 
                 + funcbuf.toString()
                 + initbuf.toString()
                 + ProgramStartupCode[ApplicationType];
        s = Xreplace( s, "%AppName%", xml.属性値( top_element, "objectname" ) );

        // Html文書に変換
        s = "<!DOCTYPE html>\n"+
            "<html lang=\"ja\">\n"+
            "<head>\n"+
            "<meta charset=\"utf-8\">\n"+
            "<title>" + xml.属性値( top_element, "objectname" ) + "</title>\n"+
            "</head>\n"+
            "<body>\n"+
            HtmlBuffer+"\n"+
            "<script>\n"+
            s+"\n"+
            "</script>\n"+
            "</body>\n"+
            "</html>\n";
            
        SourceFile[ApplicationType].Xappend( s );
      } 

      else {}

      if(ViewSourceAtCompile){
        execute( JavaViewCommand + " " + SourceFile[ApplicationType].getName(), false );
      }
      XFile cf = new XFile( "classfiles" );
      if( cf.isDirectory() ) cf.Xdelete();
      cf.mkdir();
      String cmd = Xreplace( CompileCommand[ApplicationType], "$1", target );
      messagewindow.execcommand("コンパイルします．\n", "\nコンパイルできません\n", cmd);
    }

    // 退避したプロパティを元に戻す
    properties = prop0;
    syncProperty();
  }

  // C++プログラムを作成する
  public void compile_CPP( Object element, StringBuffer clsbuf, StringBuffer funcbuf, StringBuffer initbuf, Vector signal ){
    int i, j;
    Vector list;

    String element_name = xml.要素の名前( element );
    String comp_name = xml.要素のID( element );

    if( element_name.equals("codeclip") ){
      clsbuf.append( xml.属性値( element, "codetext" ) + "\n" );
    }

    else if( element_name.equals("xobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );
      clsbuf.append( "namespace " + cls + " {\n" );

      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_CPP( list.get(i), clsbuf, funcbuf, initbuf, null );// codebuf->clsbuf
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "relation" );
      for( i = 0; i < list.size(); i++ ){
        Object pin1, pin2;
        String pin1name = xml.属性値( list.get(i), "pin1name" );
        String pin2name = xml.属性値( list.get(i), "pin2name" );
        if( pin2name.charAt( pin2name.length()-1 ) == ')' ){  // pin2 is pinlabel
          Object base = xml.子要素( element, getbase( pin2name ) );
          pin2 = xml.子要素( base, getsignature( pin2name ) );
        }
        else{
          pin2 = xml.子要素( element, pin2name );             // pin2 is pin or operation
        }
        signal2.add( new StringCouple( pin1name, getAbsoluteName( pin2 ) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_CPP( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
       
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_CPP( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "aobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_CPP( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "xobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_CPP( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
      clsbuf.append( codebuf.toString() + "\n}\n" );
    }

    else if( element_name.equals("aobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );
      clsbuf.append( "namespace " + cls + "{\nint STATE, STATE2;\n" );

      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_CPP( list.get(i), codebuf, funcbuf, initbuf, null );
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "action" );
      for( i = 0; i < list.size(); i++ ){
        Object comp1, comp2;
        String comp1name = xml.属性値( list.get(i), "comp1name" );
        String comp2name = xml.属性値( list.get(i), "comp2name" );
        comp2 = xml.子要素( element, comp2name );
        signal2.add( new StringCouple( comp1name, getAbsoluteName(comp2) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method = new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_CPP( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
      Vector statemethod = new Vector();
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        Vector method= new Vector();
        Object op = list.get(i);
        String name = xml.要素のID( op );

        if( parseInt( xml.属性値( op, "inpinlinkcount" ) ) == 0 ){
          statemethod.add( new StringCouple( xml.属性値( op, "state1" ), name + "_" + xml.属性値( op, "inpintext" ) ) );
        }

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_CPP( op, clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "state" );
      for( i = 0; i < list.size(); i++ ){
        compile_CPP( list.get(i), clsbuf, funcbuf, initbuf, statemethod );
      }

      clsbuf.append( codebuf.toString() + "\n}\n" );
      initbuf.append( getAbsoluteName( element ) + "::_SINIT();\n" );
    }

    else if( element_name.equals("pin") ){
      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      String method = xml.属性値( element, "text" );
      String signature = getsignature( method );
      clsbuf.append( "void " + method + ";\n" );
      funcbuf.append( "void " + getAbsoluteName( element ) + "{\n" );
      if(transition) funcbuf.append("STATE2 = STATE;\n");
      for( j = 0; j < signal.size(); j++ ){
        funcbuf.append( getbase( (String)( signal.get(j) ) ) + "(" + signature + ");\n" );
      }
      funcbuf.append( "}\n" );
    }

    else if( element_name.equals("operation" ) ){
      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      int inpinlinkcount = parseInt( xml.属性値( element, "inpinlinkcount" ) );
      String outpin = xml.属性値( element, "outpintext" );
      String signature = getsignature( outpin );
      String description = xml.属性値( element, "description" );
      String condition = getFirstLine( description );
      clsbuf.append( "void " + comp_name + "_" + xml.属性値( element, "inpintext" ) + ";\n" );
//      funcbuf.append( "\n/*\n" + description + "\n*/\n" );
      funcbuf.append( "void " + getAbsoluteName( element ) + "{\n" );
      if( signal.size() > 0 ){
        funcbuf.append( getdeclare( outpin ) );
      }
      if( transition && ( inpinlinkcount != 0 ) ){
         funcbuf.append( "if( STATE2 != " +
         xml.子要素( xml.親要素( element ), xml.属性値( element, "state1" ) ).hashCode()
         + " ) return;\n" );
      }
      if( condition.startsWith(TransientConditionPrefix) ){
        funcbuf.append( " if( !( " + condition.substring( TransientConditionPrefix.length()+1 ) + " ) ) return;\n" );
      }

      String  code  = description;
      boolean fcode = false;
      String  line;
      
      while( !code.equals( "" ) ){
        line = getFirstLine( code );
        code = getNextLines( code );
        if( fcode = line.startsWith( FollowIsCodePrefix ) ) break;
      }

      if( !fcode ) code = xml.属性値( element, "codetext" );
      funcbuf.append( code + "\n" );

      for( j = 0; j < signal.size(); j++ ){
        funcbuf.append( getbase( (String)( signal.get(j) ) ) + "(" + signature + ");\n" );
      }
      if(transition) {
        funcbuf.append(
          "\n// "
        + xml.属性値( xml.子要素( xml.親要素( element ), xml.属性値( element, "state2" ) ), "text" )
        + " に遷移する\n"
        + getAbsoluteName( xml.親要素( element ) ) + "::" + xml.属性値( element, "state2" ) + "();\n"
        );
      }
      funcbuf.append( "}\n" );
    }

    else if( element_name.equals("state") ){
      clsbuf.append( "void " + xml.要素のID( element ) + "();\n" );
      funcbuf.append(
        "\n// " + xml.属性値( element, "text" ) + "\n"
      + "void " + getAbsoluteName( element ) + "(){\nSTATE = " + element.hashCode() + ";\n"
      );
      for( j = signal.size()-1; j >= 0; j-- ){
        StringCouple k = (StringCouple)( signal.get(j) );
        if( k.String1.equals( comp_name ) ){
          funcbuf.append( k.String2 + ";\n" );
          signal.remove( k );
        }
      }
      funcbuf.append( "}\n" );
    }

    else{
      reportError( "can\'t compile for " + element_name + "\n" );
    }

  }

  // javaプログラムを作成する
  public String compile_JAVA( Object element, boolean isRoot, Vector signal ){
    int i, j;
    Vector list;

    StringBuffer javatext = new StringBuffer(""); 
    String element_name = xml.要素の名前( element );
    String comp_name = xml.要素のID( element );

    if( element_name.equals("xobject") ){
      StringBuffer javaconst = new StringBuffer("");
      String cls = xml.属性値( element, "objectname" );
      javatext.append( "class " + cls + "{\n" );
      if( isRoot ){
        javaconst.append( cls + "( ){\n" );
      }
      else{
        String percls =  xml.属性値( xml.親要素( element ), "objectname" );
        javatext.append( percls + " parent;\n" );
        javaconst.append( cls + "( " + percls + " pnt ){\n parent = pnt;\n");
      }

      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        javatext.append( compile_JAVA( list.get(i), false, null ) );
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "relation" );
      for( i = 0; i < list.size(); i++ ){
        Object pin1, pin2;
        String pin1name = xml.属性値( list.get(i), "pin1name" );
        String pin2name = xml.属性値( list.get(i), "pin2name" );
        String s1 = "";
        if( pin1name.charAt( pin1name.length()-1 ) == ')' ){ // pin1 is pinlabel
          pin1 = xml.子要素( xml.子要素( element, getbase( pin1name ) ), getsignature( pin1name ) );
          s1 = "parent.";
        }
        else{
          pin1 = xml.子要素( element, pin1name );
        }
        String s2 = "";
        if( pin2name.charAt( pin2name.length()-1 ) == ')' ){  // pin2 is pinlabel
          Object base = xml.子要素( element, getbase( pin2name ) );
          pin2 = xml.子要素( base, getsignature( pin2name ) );
          s2 = "I" + xml.属性値( base, "objectname" ) + ".";
        }
        else{
          pin2 = xml.子要素( element, pin2name );             // pin2 is pin or operation
          if( xml.要素の名前( pin2 ).equals("operation") ) s2 = pin2name + "_"; 
        }
        String method2 = "";
        if( xml.要素の名前( pin2 ).equals( "pin") )            method2 = xml.属性値( pin2, "text" );
        else if( xml.要素の名前( pin2 ).equals( "operation") ) method2 = xml.属性値( pin2, "inpintext" );
        signal2.add( new StringCouple( pin1name, s1 + s2 + method2 ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        javatext.append( compile_JAVA( list.get(i), false, method ) );
      }
       
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        javatext.append( compile_JAVA( list.get(i), false, method ) );
      }

      list = xml.子要素のリスト( element, "aobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        String xcls = xml.属性値( list.get(i), "objectname" );
        javaconst.append( "I" + xcls + " = new " + xcls + "( this );\n" );
        javatext.append( xcls + " I" + xcls + ";\n" + compile_JAVA( list.get(i), false, method ) );
      }

      list = xml.子要素のリスト( element, "xobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        String xcls = xml.属性値( list.get(i), "objectname" );
        javaconst.append( "I" + xcls + " = new " + xcls + "( this );\n" );
        javatext.append( xcls + " I" + xcls + ";\n" + compile_JAVA( list.get(i), false, method ) );
      }
         
      javatext.append( javaconst.toString() );
      javatext.append( "\n}\n}\n" );
      return( javatext.toString() );
    }


    else if( element_name.equals("aobject") ){
      StringBuffer javaconst = new StringBuffer("");
      String cls = xml.属性値( element, "objectname" );
      javatext.append( "class " + cls + "{\nint STATE, STATE2;\n" );
      if( isRoot ){
        javaconst.append( cls + "( ){\n" );
      }
      else{
        String percls =  xml.属性値( xml.親要素( element ), "objectname" );
        javatext.append( percls + " parent;\n" );
        javaconst.append( cls + "( " + percls + " pnt ){\n parent = pnt;\n");
      }

      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        javatext.append( compile_JAVA( list.get(i), false, null ) );
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "action" );
      for( i = 0; i < list.size(); i++ ){
        Object comp1, comp2;
        String comp1name = xml.属性値( list.get(i), "comp1name" );
        String comp2name = xml.属性値( list.get(i), "comp2name" );
        comp1 = xml.子要素( element, comp1name );
        comp2 = xml.子要素( element, comp2name );
        String method2 = "";
        if( xml.要素の名前( comp2 ).equals("pin") )            method2 = xml.属性値( comp2, "text" );
        else if( xml.要素の名前( comp2 ).equals("operation") ) method2 = comp2name + "_" + xml.属性値( comp2, "inpintext" );
        signal2.add( new StringCouple( comp1name, method2 ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method = new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        javatext.append( compile_JAVA( list.get(i), false, method ) );
      }
         
      Vector statemethod = new Vector();
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        Vector method= new Vector();
        Object op = list.get(i);
        String name = xml.要素のID( op );

        if( parseInt( xml.属性値( op, "inpinlinkcount" ) ) == 0 ){
          xml.属性値をセット( op, "inpintext", "in()" );
          statemethod.add( new StringCouple( xml.属性値( op, "state1" ), name + "_in()"  ) );
        }

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }

        javatext.append( compile_JAVA( op, false, method ) );
      }

      list = xml.子要素のリスト( element, "state" );
      for( i = 0; i < list.size(); i++ ){
        javatext.append( compile_JAVA( list.get(i), false, statemethod ) );
      }

      javatext.append( javaconst.toString() );
      javatext.append( "_SINIT();\n}\n}\n" );
      return( javatext.toString() );
    }

    else if( element_name.equals("codeclip") ){
      return( xml.属性値( element, "codetext" ) + "\n" );
    }

    else if( element_name.equals("pin") ){
      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      String method = xml.属性値( element, "text" );
      String signature = getsignature( method );
      javatext.append( "public void " + method + "{\n" );
      if(transition) javatext.append("STATE2 = STATE;\n");
      for( j = 0; j < signal.size(); j++ ){
        javatext.append( getbase( (String)( signal.get(j) ) ) + "(" + signature + ");\n" );
      }
      javatext.append( "}\n" );
      return( javatext.toString() );
    }

    else if( element_name.equals("operation" ) ){
      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      int inpinlinkcount = parseInt( xml.属性値( element, "inpinlinkcount" ) );
      String outpin = xml.属性値( element, "outpintext" );
      String signature = getsignature( outpin );
      String description = xml.属性値( element, "description" );
      String condition = getFirstLine( description );
//      javatext.append( "\n/*\n" + description + "\n*/\n" );
      javatext.append( "private void " + comp_name + "_" + xml.属性値( element, "inpintext" ) + "{\n" );
      if( signal.size() > 0 ){
        javatext.append( getdeclare( outpin ) );
      }
      if( transition && ( inpinlinkcount != 0 ) ){
         javatext.append( "if( STATE2 != " +
         xml.子要素( xml.親要素( element ), xml.属性値( element, "state1" ) ).hashCode()
         + " ) return;\n" );
      }
      if( condition.startsWith(TransientConditionPrefix) ){
        javatext.append( " if( !( " + condition.substring( TransientConditionPrefix.length()+1 ) + " ) ) return;\n" );
      }

      String  code  = description;
      boolean fcode = false;
      String  line;
      
      while( !code.equals( "" ) ){
        line = getFirstLine( code );
        code = getNextLines( code );
        if( fcode = line.startsWith( FollowIsCodePrefix ) ) break;
      }

      if( !fcode ) code = xml.属性値( element, "codetext" );
      
      javatext.append( code + "\n" );
      for( j = 0; j < signal.size(); j++ ){
        javatext.append( getbase( (String)( signal.get(j) ) ) + "(" + signature + ");\n" );
      }
      if(transition){
        javatext.append(
          "\n// "
        + xml.属性値( xml.子要素( xml.親要素( element ), xml.属性値( element, "state2" ) ), "text" )
        + " に遷移する\n" 
        +  xml.属性値( element, "state2" ) + "();\n"
        );
      }
      javatext.append( "}\n" );
      return( javatext.toString() );
    }

    else if( element_name.equals("state") ){
      javatext.append(
        "\n// " + xml.属性値( element, "text" ) + "\n"
      + "private void " + comp_name + "(){\nSTATE = " + element.hashCode() + ";\n"
      );
      for( j = signal.size()-1; j >= 0; j-- ){
        StringCouple k = (StringCouple)( signal.get(j) );
        if( k.String1.equals( comp_name ) ){
          javatext.append( k.String2 + ";\n" );
          signal.remove( k );
        }
      }
      javatext.append( "}\n" );
      return( javatext.toString() );
    }

    else{
      reportError( "can\'t compile for " + element_name + "\n" );
      return( "" );
    }

  }

  // Basicプログラムを作成する
  public void compile_BASIC( Object element, StringBuffer clsbuf, StringBuffer funcbuf, StringBuffer initbuf, Vector signal ){
    int i, j;
    Vector list;

    String element_name = xml.要素の名前( element );
    String comp_name = xml.要素のID( element );

    if( element_name.equals("codeclip") ){
      String id = getAbsoluteName2( element  );
      String buf = xml.属性値( element, "codetext" ) + "\n";
      buf = Xreplace( buf, IDF_LocalVariable[5], id );
      clsbuf.append( buf );
    }

    else if( element_name.equals("xobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );
//      clsbuf.append( "' xobject " + cls + " {\n" );

      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_BASIC( list.get(i), clsbuf, funcbuf, initbuf, null );// codebuf->clsbuf
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "relation" );
      for( i = 0; i < list.size(); i++ ){
        Object pin1, pin2;
        String pin1name = xml.属性値( list.get(i), "pin1name" );
        String pin2name = xml.属性値( list.get(i), "pin2name" );

        if( pin2name.charAt( pin2name.length()-1 ) == ')' ){  // pin2 is pinlabel
          Object base = xml.子要素( element, getbase( pin2name ) );
          pin2 = xml.子要素( base, getsignature( pin2name ) );
        }
        else{
          pin2 = xml.子要素( element, pin2name );             // pin2 is pin or operation
        }

        signal2.add( new StringCouple( pin1name, getAbsoluteName2( pin2 ) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_BASIC( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
       
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_BASIC( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "aobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_BASIC( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "xobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_BASIC( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
    }

    else if( element_name.equals("aobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );
      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_BASIC( list.get(i), codebuf, funcbuf, initbuf, null );
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "action" );
      for( i = 0; i < list.size(); i++ ){
        Object comp1, comp2;
        String comp1name = xml.属性値( list.get(i), "comp1name" );
        String comp2name = xml.属性値( list.get(i), "comp2name" );
        comp2 = xml.子要素( element, comp2name );
        signal2.add( new StringCouple( comp1name, getAbsoluteName2(comp2) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method = new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_BASIC( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
      Vector statemethod = new Vector();
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        Vector method= new Vector();
        Object op = list.get(i);

        if( parseInt( xml.属性値( op, "inpinlinkcount" ) ) == 0 ){
          statemethod.add( new StringCouple( xml.属性値( op, "state1" ), getAbsoluteName2( op ) ) );
        }

        String name = xml.要素のID( op );
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_BASIC( op, clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "state" );
      for( i = 0; i < list.size(); i++ ){
        compile_BASIC( list.get(i), clsbuf, funcbuf, initbuf, statemethod );
      }

      initbuf.append( "gosub @_SINIT"+getAbsoluteName2( element ) +"\n" );
    }

    else if( element_name.equals("pin") ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String path = getAbsoluteName2( element );
      String base = getbase( path );
      String signature = getsignature( path );
      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      String buf = "@"+base +":\n";
      if(transition) buf =buf + "STATE2" + parent + "=STATE" + parent +"\n";
      for( j = 0; j < signal.size(); j++ ){
        StringTokenizer tk1 = new StringTokenizer( signature, ","  );
        StringTokenizer tk2 = new StringTokenizer( getsignature( (String)( signal.get(j) ) ), ","  );
        String base2 = getbase( (String)( signal.get(j) ) );
        while( tk1.hasMoreTokens() &&  tk2.hasMoreTokens() ){
          buf = buf + Xreplace( tk2.nextToken(), IDF_LocalVariable[5], base2 ) +"="+ Xreplace( tk1.nextToken(), IDF_LocalVariable[5], base ) +"\n";
	    }
        buf = buf + "gosub @"+ base2+"\n";
      }
      buf = buf + "return\n";
      funcbuf.append( buf );
    }

    else if( element_name.equals("operation" ) ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String path = getAbsoluteName2( element );
      String base = getbase( path );
      String outpin = xml.属性値( element, "outpintext" );
      String signature = getsignature( outpin );
      String code = xml.属性値( element, "codetext" );

      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      int inpinlinkcount = parseInt( xml.属性値( element, "inpinlinkcount" ) );
      String buf = "@"+base +":\n";
      if( transition && ( inpinlinkcount != 0 ) ){
         buf = buf +  "if  STATE2" + parent + "<>" +
         xml.子要素( xml.親要素( element ), xml.属性値( element, "state1" ) ).hashCode()
         + " then  return\n";
      }
      buf = buf + code;
      for( j = 0; j < signal.size(); j++ ){
        StringTokenizer tk1 = new StringTokenizer( signature, ","  );
        StringTokenizer tk2 = new StringTokenizer( getsignature( (String)( signal.get(j) ) ), ","  );
        String base2 = getbase( (String)( signal.get(j) ) );
        while( tk1.hasMoreTokens() ){
          buf = buf + Xreplace( tk2.nextToken(), IDF_LocalVariable[5], base2 ) +"="+ Xreplace( tk1.nextToken(), IDF_LocalVariable[5], base ) +"\n";
	    }
        buf = buf + "gosub @"+ base2+"\n";
      }
      if(transition)  buf = buf + "gosub @"+ xml.属性値( element, "state2" ) + parent + "\n";
      buf = buf + "return\n";
      buf = Xreplace( buf, IDF_LocalVariable[5], base );

      funcbuf.append( buf );
    }

    else if( element_name.equals("state") ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String base = getAbsoluteName2( element );
      String buf = "@"+base + parent +":\n";
      buf= buf + "STATE" + parent + "=" + element.hashCode() + "\n";
      for( j = signal.size()-1; j >= 0; j-- ){
        StringCouple k = (StringCouple)( signal.get(j) );
        if( k.String1.equals( comp_name ) ){
          buf = buf + "gosub @"+ getbase( k.String2 ) +"\n";
          signal.remove( k );
        }
      }
      buf = buf + "return\n";
      funcbuf.append( buf );
    }

    else{
      reportError( "can\'t compile for " + element_name + "\n" );
    }

  }


  // C言語プログラムを作成する
  public void compile_C( Object element, StringBuffer clsbuf, StringBuffer funcbuf, StringBuffer initbuf, Vector signal ){
    int i, j;
    Vector list;

    String element_name = xml.要素の名前( element );
    String comp_name = xml.要素のID( element );

    if( element_name.equals("codeclip") ){
      String buf = xml.属性値( element, "codetext" ) + "\n";
      clsbuf.append( buf );
    }

    else if( element_name.equals("xobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );
//      clsbuf.append( "' xobject " + cls + " {\n" );

      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_C( list.get(i), clsbuf, funcbuf, initbuf, null );// codebuf->clsbuf
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "relation" );
      for( i = 0; i < list.size(); i++ ){
        Object pin1, pin2;
        String pin1name = xml.属性値( list.get(i), "pin1name" );
        String pin2name = xml.属性値( list.get(i), "pin2name" );

        if( pin2name.charAt( pin2name.length()-1 ) == ')' ){  // pin2 is pinlabel
          Object base = xml.子要素( element, getbase( pin2name ) );
          pin2 = xml.子要素( base, getsignature( pin2name ) );
        }
        else{
          pin2 = xml.子要素( element, pin2name );             // pin2 is pin or operation
        }

        signal2.add( new StringCouple( pin1name, getAbsoluteName2( pin2 ) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_C( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
       
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_C( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "aobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_C( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "xobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_C( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
    }

    else if( element_name.equals("aobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );
      String id = getAbsoluteName2( element );
      clsbuf.append( "int STATE" + id + ", STATE2" + id + ";\n" );
      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_C( list.get(i), clsbuf, funcbuf, initbuf, null );
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "action" );
      for( i = 0; i < list.size(); i++ ){
        Object comp1, comp2;
        String comp1name = xml.属性値( list.get(i), "comp1name" );
        String comp2name = xml.属性値( list.get(i), "comp2name" );
        comp2 = xml.子要素( element, comp2name );
        signal2.add( new StringCouple( comp1name, getAbsoluteName2(comp2) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method = new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_C( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
      Vector statemethod = new Vector();
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        Vector method= new Vector();
        Object op = list.get(i);

        if( parseInt( xml.属性値( op, "inpinlinkcount" ) ) == 0 ){
          statemethod.add( new StringCouple( xml.属性値( op, "state1" ), getAbsoluteName2( op ) ) );
        }

        String name = xml.要素のID( op );
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_C( op, clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "state" );
      for( i = 0; i < list.size(); i++ ){
        compile_C( list.get(i), clsbuf, funcbuf, initbuf, statemethod );
      }

      initbuf.append( "_SINIT" + id +"();\n" );
    }

    else if( element_name.equals("pin") ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String path = getAbsoluteName2( element );
      String signature = getsignature( path );
      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      String buf = "void " + path + "{\n";

      if(transition) buf = buf + "STATE2" + parent + " = STATE" + parent +";\n";
      for( j = 0; j < signal.size(); j++ ){
        buf = buf + getbase( (String)( signal.get(j) ) ) + "(" + signature + ");\n";
      }
      buf = buf + "}\n";
      funcbuf.append( buf );
      clsbuf.append( "void " + path + ";\n" );
    }

    else if( element_name.equals("operation" ) ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String path = getAbsoluteName2( element );
      String base = getbase( path );
      String outpin = xml.属性値( element, "outpintext" );
      String signature = getsignature( outpin );
      String code = xml.属性値( element, "codetext" );

      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      int inpinlinkcount = parseInt( xml.属性値( element, "inpinlinkcount" ) );
      String buf = "void " + path + "{\n";
      if( signal.size() > 0 ){
        buf = buf + getdeclare( outpin );
      }
      if( transition && ( inpinlinkcount != 0 ) ){
         buf = buf +  "if( STATE2" + parent + " != " +
         xml.子要素( xml.親要素( element ), xml.属性値( element, "state1" ) ).hashCode()
         + " )  return;\n";
      }
      buf = buf + code;
      for( j = 0; j < signal.size(); j++ ){
        buf = buf + getbase( (String)( signal.get(j) ) ) + "(" + signature + ");\n";
      }
      if(transition){
        buf = buf + "\n/* "
        + xml.属性値( xml.子要素( xml.親要素( element ), xml.属性値( element, "state2" ) ), "text" )
        + " に遷移する */\n"
		+ " "+ xml.属性値( element, "state2" ) + parent + "();\n";
      }
      buf = buf + "}\n";
      funcbuf.append( buf );
      clsbuf.append( "void "+ path +";\n" );
    }

    else if( element_name.equals("state") ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String base = getAbsoluteName2( element );
      String buf = "\n/* " + xml.属性値( element, "text" ) + "*/\nvoid " + base + parent +"(){\n";
      buf= buf + "STATE" + parent + "=" + element.hashCode() + ";\n";
      for( j = signal.size()-1; j >= 0; j-- ){
        StringCouple k = (StringCouple)( signal.get(j) );
        if( k.String1.equals( comp_name ) ){
          buf = buf + k.String2 +";\n";
          signal.remove( k );
        }
      }
      buf = buf + "}\n";
      funcbuf.append( buf );
      clsbuf.append( "void " + base + parent +"();\n" );
    }

    else{
      reportError( "can\'t compile for " + element_name + "\n" );
    }

  }


  // oregengo_Rプログラムを作成する
  public void compile_oregengo_R( Object element, StringBuffer clsbuf, StringBuffer funcbuf, StringBuffer initbuf, Vector signal ){
    int i, j;
    Vector list;

    String element_name = xml.要素の名前( element );
    String comp_name = xml.要素のID( element );

    if( element_name.equals("codeclip") ){
      String id = getAbsoluteName2( element );
      String buf = xml.属性値( element, "codetext" ) + "\n";
      buf = Xreplace( buf, IDF_LocalVariable[7], id );
      clsbuf.append( buf );
    }

    else if( element_name.equals("xobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );

      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_oregengo_R( list.get(i), clsbuf, funcbuf, initbuf, null );
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "relation" );
      for( i = 0; i < list.size(); i++ ){
        Object pin1, pin2;
        String pin1name = xml.属性値( list.get(i), "pin1name" );
        String pin2name = xml.属性値( list.get(i), "pin2name" );

        if( pin2name.charAt( pin2name.length()-1 ) == ')' ){  // pin2 is pinlabel
          Object base = xml.子要素( element, getbase( pin2name ) );
          pin2 = xml.子要素( base, getsignature( pin2name ) );
        }
        else{
          pin2 = xml.子要素( element, pin2name );             // pin2 is pin or operation
        }

        signal2.add( new StringCouple( pin1name, getAbsoluteName2( pin2 ) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_oregengo_R( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
       
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_oregengo_R( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "aobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_oregengo_R( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "xobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_oregengo_R( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
    }

    else if( element_name.equals("aobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );
      String id = getAbsoluteName2( element );
      clsbuf.append( " long STATE" + id + "#,STATE2" + id + "#\n" );
      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_oregengo_R( list.get(i), clsbuf, funcbuf, initbuf, null );
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "action" );
      for( i = 0; i < list.size(); i++ ){
        Object comp1, comp2;
        String comp1name = xml.属性値( list.get(i), "comp1name" );
        String comp2name = xml.属性値( list.get(i), "comp2name" );
        comp2 = xml.子要素( element, comp2name );
        signal2.add( new StringCouple( comp1name, getAbsoluteName2(comp2) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method = new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_oregengo_R( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
      Vector statemethod = new Vector();
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        Vector method= new Vector();
        Object op = list.get(i);

        if( parseInt( xml.属性値( op, "inpinlinkcount" ) ) == 0 ){
          statemethod.add( new StringCouple( xml.属性値( op, "state1" ), getAbsoluteName2( op ) ) );
        }

        String name = xml.要素のID( op );
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_oregengo_R( op, clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "state" );
      for( i = 0; i < list.size(); i++ ){
        compile_oregengo_R( list.get(i), clsbuf, funcbuf, initbuf, statemethod );
      }

      initbuf.append( " _SINIT"+getAbsoluteName2( element ) +"\n" );
    }

    else if( element_name.equals("pin") ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String path = getAbsoluteName2( element );
      String base = getbase( path );
      String signature = getsignature( path );
      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      String buf = ""+base +":\n";
      if(transition) buf =buf + " STATE" + parent +"#, STATE2" + parent + "#=\n";
      for( j = 0; j < signal.size(); j++ ){
        StringTokenizer tk1 = new StringTokenizer( signature, ","  );
        StringTokenizer tk2 = new StringTokenizer( getsignature( (String)( signal.get(j) ) ), ","  );
        String base2 = getbase( (String)( signal.get(j) ) );
        while( tk1.hasMoreTokens() &&  tk2.hasMoreTokens() ){
          buf = buf + " "+ Xreplace( tk1.nextToken(), IDF_LocalVariable[7], base )+", "+Xreplace( tk2.nextToken(), IDF_LocalVariable[7], base2 ) +"=\n";
	    }
        buf = buf + " "+ base2+"\n";
      }
      buf = buf + "\n end\n";
      funcbuf.append( buf );
    }

    else if( element_name.equals("operation" ) ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String path = getAbsoluteName2( element );
      String base = getbase( path );
      String outpin = xml.属性値( element, "outpintext" );
      String signature = getsignature( outpin );
      String code = xml.属性値( element, "codetext" );

      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      int inpinlinkcount = parseInt( xml.属性値( element, "inpinlinkcount" ) );
      String buf = ""+base +":\n";
      if( transition && ( inpinlinkcount != 0 ) ){
         buf = buf +  " if  STATE2" + parent + "#<>" +
         xml.子要素( xml.親要素( element ), xml.属性値( element, "state1" ) ).hashCode()
         + " then  end\n";
      }
      buf = buf + code;
      for( j = 0; j < signal.size(); j++ ){
        StringTokenizer tk1 = new StringTokenizer( signature, ","  );
        StringTokenizer tk2 = new StringTokenizer( getsignature( (String)( signal.get(j) ) ), ","  );
        String base2 = getbase( (String)( signal.get(j) ) );
        while( tk1.hasMoreTokens() ){
          buf = buf + " "+Xreplace( tk1.nextToken(), IDF_LocalVariable[7], base )+", "+Xreplace( tk2.nextToken(), IDF_LocalVariable[7], base2 ) +"=\n";
	    }
        buf = buf + " "+ base2+"\n";
      }
      if(transition)  buf = buf + " "+ xml.属性値( element, "state2" ) + parent + "\n";
      buf = buf + "\n end\n";
      buf = Xreplace( buf, IDF_LocalVariable[7], base );

      funcbuf.append( buf );
    }

    else if( element_name.equals("state") ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String base = getAbsoluteName2( element );
      String buf = ""+base + parent +":\n";
      buf= buf + " " + element.hashCode()+", STATE" + parent + "#=\n";
      for( j = signal.size()-1; j >= 0; j-- ){
        StringCouple k = (StringCouple)( signal.get(j) );
        if( k.String1.equals( comp_name ) ){
          buf = buf + " "+ getbase( k.String2 ) +"\n";
          signal.remove( k );
        }
      }
      buf = buf + "\n end\n";
      funcbuf.append( buf );
    }

    else{
      reportError( "can\'t compile for " + element_name + "\n" );
    }

  }


  // Javascriptプログラムを作成する
  public void compile_javascript( Object element, StringBuffer clsbuf, StringBuffer funcbuf, StringBuffer initbuf, Vector signal ){

    int i, j;
    Vector list;

    String element_name = xml.要素の名前( element );
    String comp_name = xml.要素のID( element );

    if( element_name.equals("codeclip") ){
      String buf = xml.属性値( element, "codetext" ) + "\n";
      StringTokenizer st = new StringTokenizer( buf, "\n" );
      while(st.hasMoreTokens()){
        String lin = st.nextToken();

        // HTML文書の場合はHTMLバッファに追加
        if(lin.startsWith("//html")){
          HtmlBuffer = HtmlBuffer + lin.substring(6) +"\n";
        }

        // それ以外のときはクラスバッファに追加
        else{
          clsbuf.append(lin+"\n");
        }
      }
       
    }

    else if( element_name.equals("xobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );

      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_javascript( list.get(i), clsbuf, funcbuf, initbuf, null );// codebuf->clsbuf
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "relation" );
      for( i = 0; i < list.size(); i++ ){
        Object pin1, pin2;
        String pin1name = xml.属性値( list.get(i), "pin1name" );
        String pin2name = xml.属性値( list.get(i), "pin2name" );

        if( pin2name.charAt( pin2name.length()-1 ) == ')' ){  // pin2 is pinlabel
          Object base = xml.子要素( element, getbase( pin2name ) );
          pin2 = xml.子要素( base, getsignature( pin2name ) );
        }
        else{
          pin2 = xml.子要素( element, pin2name );             // pin2 is pin or operation
        }

        signal2.add( new StringCouple( pin1name, getAbsoluteName2( pin2 ) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_javascript( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
       
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_javascript( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "aobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_javascript( list.get(i), clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "xobject" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method= new Vector();
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( ( k.String1.charAt( k.String1.length()-1 ) == ')' ) && name.equals( getbase( k.String1 ) ) ){
            method.add( new StringCouple( getsignature( k.String1 ), k.String2 ) );
            signal2.remove( k );
          }
        }
        compile_javascript( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
    }

    else if( element_name.equals("aobject") ){
      StringBuffer codebuf = new StringBuffer(""); 
      String cls = xml.属性値( element, "objectname" );
      String id = getAbsoluteName2( element );
      clsbuf.append( "var STATE" + id + ", STATE2" + id + ";\n" );
      list = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < list.size(); i++ ){
        compile_javascript( list.get(i), clsbuf, funcbuf, initbuf, null );
      }

      Vector signal2 = new Vector();
      list = xml.子要素のリスト( element, "action" );
      for( i = 0; i < list.size(); i++ ){
        Object comp1, comp2;
        String comp1name = xml.属性値( list.get(i), "comp1name" );
        String comp2name = xml.属性値( list.get(i), "comp2name" );
        comp2 = xml.子要素( element, comp2name );
        signal2.add( new StringCouple( comp1name, getAbsoluteName2(comp2) ) );
      }
        
      list = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < list.size(); i++ ){
        String name = xml.要素のID( list.get(i) );
        Vector method = new Vector();

        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        for( j = signal.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal.remove( k );
          }
        }
        compile_javascript( list.get(i), clsbuf, funcbuf, initbuf, method );
      }
         
      Vector statemethod = new Vector();
      list = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < list.size(); i++ ){
        Vector method= new Vector();
        Object op = list.get(i);

        if( parseInt( xml.属性値( op, "inpinlinkcount" ) ) == 0 ){
          statemethod.add( new StringCouple( xml.属性値( op, "state1" ), getAbsoluteName2( op ) ) );
        }

        String name = xml.要素のID( op );
        for( j = signal2.size()-1; j >= 0; j-- ){
          StringCouple k = (StringCouple)( signal2.get(j) );
          if( name.equals( k.String1 ) ){
            method.add( k.String2 );
            signal2.remove( k );
          }
        }
        compile_javascript( op, clsbuf, funcbuf, initbuf, method );
      }

      list = xml.子要素のリスト( element, "state" );
      for( i = 0; i < list.size(); i++ ){
        compile_javascript( list.get(i), clsbuf, funcbuf, initbuf, statemethod );
      }

      initbuf.append( "_SINIT" + id +"();\n" );
    }

    else if( element_name.equals("pin") ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String path = getAbsoluteName2( element );
      String signature = getsignature( path );
      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      String buf = "function " + path + "{\n";

      if(transition) buf = buf + "STATE2" + parent + " = STATE" + parent +";\n";
      for( j = 0; j < signal.size(); j++ ){
        buf = buf + getbase( (String)( signal.get(j) ) ) + "(" + signature + ");\n";
      }
      buf = buf + "}\n";
      funcbuf.append( buf );
    }

    else if( element_name.equals("operation" ) ){
		
System.out.println("compile operation");		
		
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String path = getAbsoluteName2( element );
      String base = getbase( path );
      String outpin = xml.属性値( element, "outpintext" );
      String signature = getsignature( outpin );
      String code = xml.属性値( element, "codetext" );

System.out.println("code:"+code);		
		
      boolean transition = xml.要素の名前( xml.親要素( element ) ).equals("aobject");
      int inpinlinkcount = parseInt( xml.属性値( element, "inpinlinkcount" ) );
      String buf = "function " + path + "{\n";
      if( signal.size() > 0 ){
        buf = buf + getdeclare( outpin );
      }
      if( transition && ( inpinlinkcount != 0 ) ){
         buf = buf +  "if( STATE2" + parent + " != " +
         xml.子要素( xml.親要素( element ), xml.属性値( element, "state1" ) ).hashCode()
         + " )  return;\n";
      }
      buf = buf + code;
      for( j = 0; j < signal.size(); j++ ){
        buf = buf + getbase( (String)( signal.get(j) ) ) + "(" + signature + ");\n";
      }
      if(transition){
        buf = buf + "\n/* "
        + xml.属性値( xml.子要素( xml.親要素( element ), xml.属性値( element, "state2" ) ), "text" )
        + " に遷移する */\n"
		+ " "+ xml.属性値( element, "state2" ) + parent + "();\n";
      }
      buf = buf + "}\n";
      funcbuf.append( buf );
    }

    else if( element_name.equals("state") ){
      String parent = getAbsoluteName2( xml.親要素( element ) );
      String base = getAbsoluteName2( element );
      String buf = "\n/* " + xml.属性値( element, "text" ) + "*/\nfunction " + base + parent +"(){\n";
      buf= buf + "STATE" + parent + "=" + element.hashCode() + ";\n";
      for( j = signal.size()-1; j >= 0; j-- ){
        StringCouple k = (StringCouple)( signal.get(j) );
        if( k.String1.equals( comp_name ) ){
          buf = buf + k.String2 +";\n";
          signal.remove( k );
        }
      }
      buf = buf + "}\n";
      funcbuf.append( buf );
    }

    else{
      reportError( "can\'t compile for " + element_name + "\n" );
    }

  }


  //１対の文字列(コンパイルで検索のときに使う)
  class StringCouple{
    String String1;
    String String2;

    StringCouple( String s1, String s2 ){
      String1 = s1;
      String2 = s2;
    }//StringCouple()

  }//~StringCouple



  //アプリケーションを起動
  App1( String[] startfiles ){
    boolean b;

    Xinteger = new Integer(0);
    TransientConditionPrefix = "条件";
    FollowIsCodePrefix = "コード";

    SourceFile =    new XFile[]{
                      new XFile("javatext.java"),
                      new XFile("javatext.java"),
                      new XFile("NewApplication.cpp"),
                      new XFile("NewApplication.cpp"),
                      new XFile("javatext.java"),
                      new XFile("test.bas"),
                      new XFile("test.c"),
                      new XFile("test.r"),
                      new XFile("test"),
                      new XFile("test.html"),
                    };

    ObjectLib =     new XFile[] {
                      new XFile("ObjectLib_J"),
                      new XFile("ObjectLib_J"),
                      new XFile("ObjectLib_C"),
                      new XFile("ObjectLib_C"),
                      new XFile("ObjectLib_J"),
                      new XFile("ObjectLib_B"),
                      new XFile("ObjectLib_C"),
                      new XFile("ObjectLib_R"),
                      new XFile("Projects"),
                      new XFile("ObjectLib_S"),
                    };

    CurrentDir   =  new XFile( "." );
    ProjectDir   =  new XFile( CurrentDir, "Projects" );
    if( startfiles.length > 0 ) ProjectFile = new XFile( startfiles[0] ); else   ProjectFile  =  new XFile( ProjectDir, "NewApplication.prj" );

    SetupFile = new XFile("ObjectEditor.xml");
    GUIDesignerWork = new XFile("GUI.xml");
    LayoutData = new XFile("Layout.dat");

    xml = new Nxml();
    ApplicationType = 0;
    if( startfiles.length > 0 ) loadProject(); else newProject();
    clipboad =   xml.新しい要素( xml.ルート要素(), "clipboad",   "clipboad"   );
//    properties = xml.新しい要素( xml.ルート要素(), "プロパティ", "properties" );

    objecteditor = new ObjectEditor();
    objecteditor.gui.setVisible(true);
    stateeditor    = new StateEditor();
    stateeditor.gui.setVisible(false);
    messagewindow = new MessageWindow();
    filewindow = new FileWindow();
    texteditor = new TextEditor();
    propertywindow = new PropertyWindow();
    initialdialog = new InitialDialog();
    inputdialog = new InputDialog();    
    dialog1 = new Dialog1();    
    dialog2 = new Dialog2();    
    dialog3 = new Dialog3();  

    if( startfiles.length == 0 ){
      new DelayTimer(1000);
    }
		 
  }


// 遅延タイマー
class DelayTimer implements ActionListener{
  javax.swing.Timer unit;
 
  DelayTimer( int del ){
   unit = new javax.swing.Timer( del, this );
   unit.start();
  }

  public void actionPerformed( ActionEvent e){
    objecteditor.CommandReceived( "CLRALL" );
    unit.stop();
  }

}
   
  //mainメソッド
  public static void main( String[] args ){
    App1 a = new App1( args );
  }

  //オブジェクトエディタクラス
  class ObjectEditor{
      
    GUI gui;
    Xnode node;
    Object element;
    int x0, y0;
    int width, height;
    int ID_maker;
    String name;
    String description;

    XFile loadfile;
    Component comp1,comp2;
    String mode;

    // ObjectEditorを初期化
    ObjectEditor(){
      gui = new GUI();
      initialise();
    }
     
    // Objecteditorを初期化する
    public void initialise(){
    
      treetool     = new TreeTool( project );
      gui.display.setLeftComponent(treetool);
      treetool.validate();
      treetool.repaint();
      mae_node = treetool.top;
      Login( treetool.top );
      if( filewindow != null ){
		  
        boolean vis = filewindow.isVisible();
        FileWinx0 = filewindow.getLocation().x;
        FileWiny0 = filewindow.getLocation().y;
        FileWinWidth = filewindow.getWidth();
        FileWinHeight = filewindow.getHeight();
        filewindow.dispose();
        filewindow = new FileWindow();
        filewindow.setVisible( vis );
      }
    }

    // 新しいpathに内容を変更する
    public void Login( Xnode nod ){


      // プロパティを更新する
      Object elem = nod.element;

      while(true){
        Object p = xml.子要素(elem, "properties");
        if(p != null){
          properties = p;
          break;
        }
        if(elem == project) break; // ここが有効になることは多分ない
        elem = xml.親要素(elem);
      }
      syncProperty(); // 現在の状態を更新したプロパティにあわせる

      //内容をロードする
      node = nod;
      element = node.element;
      x0 = parseInt( xml.属性値( element, "x0" ) );
      y0 = parseInt( xml.属性値( element, "y0" ) );
      width = parseInt( xml.属性値( element, "width" ) );
      height = parseInt( xml.属性値( element, "height" ) );
      ID_maker = parseInt( xml.属性値( element, "ID_maker" ) );
      gui.setobjectname( xml.属性値( element, "objectname" ) );
      gui.setdescription( xml.属性値( element, "description" ) );

      //Loginモードでコンポーネントを生成する
      Vector v;
      int i, nn;
       
      // xobject, aobject
      nn = node.getChildCount();
      for( i=0;i<nn;i++){
        Object child = node.getChildAt(i);
        if( child instanceof Xnode ) gui.addcomponent( new xobject( (Xnode)child ) );
        if( child instanceof Anode ) gui.addcomponent( new aobject( (Anode)child ) );
      } 
       
      // operation
      v = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < v.size(); i++ ) gui.addcomponent( new operation( v.get(i) ) );

      // pin
      v = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < v.size(); i++ ) gui.addcomponent( new pin( v.get(i) ) );

      // codeclip
      v = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < v.size(); i++ ) gui.addcomponent( new codeclip( v.get(i) ) );
       
      // kjgroup
      v = xml.子要素のリスト( element, "KJgroup" );
      for( i = 0; i < v.size(); i++ ){
        KJgroup gr = new KJgroup( v.get(i) );
        gui.addcomponent( gr );
      }

      // ImgIcon
      v = xml.子要素のリスト( element, "ImgIcon" );
      for( i = 0; i < v.size(); i++ ){
        ImgIcon ic = new ImgIcon( v.get(i) );
        gui.addcomponent( ic );
      }

      // relation
      v = xml.子要素のリスト( element, "relation" );
      for( i = 0; i < v.size(); i++ ){
        Object r = v.get(i);
        String pin1 = xml.属性値( r, "pin1name" );
        String pin2 = xml.属性値( r, "pin2name" );
        if( gui.getcomponent( pin1 ) != null && gui.getcomponent( pin2 )!= null )
           gui.addcomponent( new relation( r ) );
        else xml.要素を削除( r );
      }

      gui.setBounds( MainWinx0, MainWiny0, MainWinWidth, MainWinHeight );
      gui.contents.setDividerLocation( DividerLocation2 );
      gui.display.setDividerLocation( DividerLocation1 );
      gui.disptoolbox.setSelected( ToolBarVisible);
      gui.toolBar.setVisible( ToolBarVisible );
      gui.setVisible();
      gui.buttonreset();
      mode = "NOP";

    }
     
    //内容をセーブして内容を消去する
    public void Logout(){
      int i;

      restoreProperty();// プロパティを現在の状態にあわせる

      MainWinx0 = gui.getLocation().x;
      MainWiny0 = gui.getLocation().y;
      MainWinWidth = gui.getWidth();
      MainWinHeight =  gui.getHeight();
      DividerLocation1 = gui.display.getDividerLocation();
      DividerLocation2 = gui.contents.getDividerLocation();
      node.setUserObject(gui.getobjectname());
      treetool.repaint();

      xml.属性値をセット( element, "x0", "" + x0 );
      xml.属性値をセット( element, "y0", "" + y0 );
      xml.属性値をセット( element, "width", "" + width );
      xml.属性値をセット( element, "height", "" + height );
      xml.属性値をセット( element, "ID_maker", "" + ID_maker );
      xml.属性値をセット( element, "objectname", gui.getobjectname() );   gui.setobjectname("");
      xml.属性値をセット( element, "description", gui.getdescription() ); gui.setdescription("");

      // コンポーネントをすべてLogoutする
      Component[] comp = gui.getcomponents();

      // relation
      for( i=0;i<comp.length;i++){
        if( comp[i] instanceof relation  ) ( (relation)comp[i] ).Logout();
      }

      // xobject, aobject, operation, pin, codeclip, kjgroup
      for( i=0;i<comp.length;i++){
        if( comp[i] instanceof xobject  )  ( (xobject)comp[i] ).Logout();
        else if( comp[i] instanceof aobject  )  ( (aobject)comp[i] ).Logout();
        else if( comp[i] instanceof operation ) ( (operation)comp[i] ).Logout();
        else if( comp[i] instanceof pin  )      ( (pin)comp[i] ).Logout();
        else if( comp[i] instanceof codeclip )  ( (codeclip)comp[i] ).Logout();
        else if( comp[i] instanceof KJgroup )   ( (KJgroup)comp[i] ).Logout();
        else if( comp[i] instanceof ImgIcon )   ( (ImgIcon)comp[i] ).Logout();
      }
    }
     
    //コンポーネントをすべて削除する
    public void removeallcomponents(){
      Component[] comp = gui.getcomponents();
      for( int i = 0; i < comp.length; i++ ) { removecomponent( comp[i] ); }
    }
      
    //コンポーネントを削除する
    public void removecomponent( Component comp ){
      if( comp instanceof xobject )        { ( (xobject) comp ).suicide();   }
      else if( comp instanceof aobject )   { ( (aobject) comp ).suicide();   }
      else if( comp instanceof operation ) { ( (operation) comp ).suicide(); }
      else if( comp instanceof pin )       { ( (pin) comp ).suicide();       }
      else if( comp instanceof relation )  { ( (relation) comp).suicide();   }
      else if( comp instanceof codeclip )  { ( (codeclip) comp).suicide();   }
      else if( comp instanceof KJgroup )   { ( (KJgroup) comp).suicide();    }
      else if( comp instanceof ImgIcon )   { ( (ImgIcon) comp).suicide();    }
      else if( comp instanceof pinlabel )  { ( (pinlabel) comp).suicide();   }
    }

    // xp, yp で指定された位置にオブジェクトをロードする
    public Object LoadObject( XFile loadfile, int xp, int yp ){
      Object obj = null;
      Xnode cnode = node;
 
      if( !loadfile.isxml() ) return( null );
      if( loadfile.isxobject() ){
        obj = xml.新しい要素( element, loadfile, "_X"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
        Xnode xn = new Xnode( obj );
        treetool.addNode( xn, objecteditor.node );
      }

      else if( loadfile.isaobject() ){
        obj = xml.新しい要素( element, loadfile, "_A"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
        Anode an = new Anode( obj );
        treetool.addNode( an, objecteditor.node );
      }

      else if( loadfile.isoperation() ){
        obj = xml.新しい要素( element, loadfile, "_O"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
        xml.属性値をセット( obj, "state1", "_SINIT" );
        xml.属性値をセット( obj, "state2", "_SINIT" );
        xml.属性値をセット( obj, "inconnectx0", "0" );
        xml.属性値をセット( obj, "inconnecty0", "30" );
        xml.属性値をセット( obj, "outconnectx0", "0" );
        xml.属性値をセット( obj, "outconnecty0", "30" );
      }
      
      else if( loadfile.ispin() ){
        obj = xml.新しい要素( element, loadfile, "_P"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
      }

      else if( loadfile.iscodeclip() ){
        obj = xml.新しい要素( element, loadfile, "_C"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
      }


      else if( loadfile.isKJgroup() ){
        obj = xml.新しい要素( element, loadfile, "_G"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
      }

      else if( loadfile.isImgIcon() ){
        obj = xml.新しい要素( element, loadfile, "_I"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
      }

      restoreProperty();
      Logout();
      Login( cnode );
      return( obj );
    }

    // クリップボードからxp, yp で指定された位置にオブジェクトを貼り付ける
    public void PasteObject( int xp, int yp ){
      Xnode cnode = node;
 
      if( xml.要素の名前( clipboad ).equals("xobject") ){
        Object xobj = xml.新しい要素( element, clipboad, "_X"+ID_maker++);
        xml.属性値をセット( xobj, "x0", "" + xp );
        xml.属性値をセット( xobj, "y0", "" + yp );
        Xnode xn = new Xnode( xobj );
        treetool.addNode( xn, objecteditor.node );
      }

      else if( xml.要素の名前( clipboad ).equals("aobject") ){
        Object aobj = xml.新しい要素( element, clipboad, "_A"+ID_maker++);
        xml.属性値をセット( aobj, "x0", "" + xp );
        xml.属性値をセット( aobj, "y0", "" + yp );
        Anode an = new Anode( aobj );
        treetool.addNode( an, objecteditor.node );
      }

      else if( xml.要素の名前( clipboad ).equals("operation") ){
        Object op = xml.新しい要素( element, clipboad, "_O"+ID_maker++);
        xml.属性値をセット( op, "x0", "" + xp );
        xml.属性値をセット( op, "y0", "" + yp );
        xml.属性値をセット( op, "state1", "_SINIT" );
        xml.属性値をセット( op, "state2", "_SINIT" );
        xml.属性値をセット( op, "inconnectx0", "0" );
        xml.属性値をセット( op, "inconnecty0", "30" );
        xml.属性値をセット( op, "outconnectx0", "0" );
        xml.属性値をセット( op, "outconnecty0", "30" );
      }
      
      else if( xml.要素の名前( clipboad ).equals("pin") ){
        Object pi = xml.新しい要素( element, clipboad, "_P"+ID_maker++);
        xml.属性値をセット( pi, "x0", "" + xp );
        xml.属性値をセット( pi, "y0", "" + yp );
      }

      else if( xml.要素の名前( clipboad ).equals("codeclip") ){
        Object cod = xml.新しい要素( element, clipboad, "_C"+ID_maker++);
        xml.属性値をセット( cod, "x0", "" + xp );
        xml.属性値をセット( cod, "y0", "" + yp );
      }


      else if( xml.要素の名前( clipboad ).equals("KJgroup") ){
        Object gr = xml.新しい要素( element, clipboad, "_G"+ID_maker++);
        xml.属性値をセット( gr, "x0", "" + xp );
        xml.属性値をセット( gr, "y0", "" + yp );
      }

      else if( xml.要素の名前( clipboad ).equals("ImgIcon") ){
        Object ic = xml.新しい要素( element, clipboad, "_I"+ID_maker++);
        xml.属性値をセット( ic, "x0", "" + xp );
        xml.属性値をセット( ic, "y0", "" + yp );
      }

      restoreProperty();
      Logout();
      Login( cnode );
    }

//  プロジェクトを開く
    private void open(){

//Syetem.out.println("open");

      XFileFilter filter = new XFileFilter( ProjectFileMode );
      JFileChooser xchooser = new JFileChooser( ProjectDir );
      xchooser.setFileFilter(filter);
      xchooser.setDialogTitle( "プロジェクトを開く" );
   
      int retval = xchooser.showOpenDialog(objecteditor.gui );
      if(retval == JFileChooser.APPROVE_OPTION) {
        File f;

        if( ( f = xchooser.getSelectedFile() ).isFile() ){
          Logout();
          ProjectFile = new XFile( f );
          loadProject();
          syncProperty();
          setlookandfeel();
          ApplicationType = parseInt( xml.属性値( properties, "ApplicationType" ) );
          initialise();
        }
      }
    }

//  プロジェクトを貼り付ける
    private void paste_project(int xp, int yp){
      XFileFilter filter = new XFileFilter( ProjectFileMode );
      JFileChooser xchooser = new JFileChooser( ProjectDir );
      xchooser.setFileFilter(filter);
      xchooser.setDialogTitle( "プロジェクトを貼り付ける" );
   
      int retval = xchooser.showOpenDialog(objecteditor.gui );
      if(retval == JFileChooser.APPROVE_OPTION) {
        File f;
        if( ( f = xchooser.getSelectedFile() ).isFile() ){
          LoadObject(new XFile(f), xp, yp);
        }
      }
    }

//  確認のダイアログを表示後、プロジェクトを保存する
    private int saveWithDialog(){
      Xnode cnode = node;
      restoreProperty();
      Logout();
      Login( cnode );
      String pname = xml.属性値( project, "objectname" );
      int flg = dialog3.age( "プロジェクト " + pname + "を保存しますか？" );
      if( flg == 1 ) save();
      return( flg );
    }

// プロジェクトを保存する
    private void save(){
      Xnode cnode = node;
      restoreProperty();
      Logout();
      Login( cnode );
      XFileFilter filter = new XFileFilter( ProjectFileMode );
      JFileChooser xchooser = new JFileChooser( ProjectDir );
      xchooser.setFileFilter(filter);
      xchooser.setDialogTitle( "プロジェクトの保存" );
      xchooser.setSelectedFile( new XFile( ProjectDir, xml.属性値( project, "objectname" ) + ".prj" ) );
    
      int retval = xchooser.showSaveDialog(objecteditor.gui );
      if(retval == JFileChooser.APPROVE_OPTION) {
        cnode = node;
        restoreProperty();
        Logout();
        ProjectFile = new XFile( xchooser.getSelectedFile() );
        saveProject();
        Login( cnode );
      }
    }

    // 接続を検証する
    public void checkconnect(){
      int i;
      Component cmp[] = gui.getcomponents();
      for( i = 0; i < cmp.length; i++ ){
         if( cmp[i] instanceof relation )   ( (relation)cmp[i] ).redrawLine();
      }
    }

    //ファイルウィンドウからロードコマンドを受け取る
    public void LoadComponent( XFile xf ){
      mode = "LOAD_COMP";
      loadfile = xf;
    }

    //ファイルウィンドウからセーブコマンドを受け取る
    public void SaveComponent(){
      mode = "SAVE_COMP";
    }

    //コマンドを受け取る
    public void CommandReceived( String command ){
      if(command.equals("CRE_XOBJ"))   mode = command;
      if(command.equals("CRE_AOBJ"))   mode = command;
      if(command.equals("CRE_OP"))     mode = command;
      if(command.equals("CRE_PIN"))    mode = command;
      if(command.equals("CRE_REL"))    mode = command;
      if(command.equals("DEL_OBJ"))    mode = command;
      if(command.equals("CRE_CODE"))   mode = command;
      if(command.equals("CRE_KJG"))    mode = command;
      if(command.equals("CRE_ICO"))    mode = command;
      if(command.equals("CUT"))        mode = command;
      if(command.equals("COPY"))       mode = command;
      if(command.equals("PASTE"))      mode = command;
      if(command.equals("TOX"))        mode = command;
      if(command.equals("TOGROUP"))    mode = command;
      if(command.equals("GUIDSIN"))    mode = command;
      if(command.equals("PASTE_PROJ")) mode = command;

      if(command.equals("UPALL"))      treetool.upALL();
      if(command.equals("DOWNALL"))    treetool.downALL();
      if(command.equals("LEFTALL"))    treetool.leftALL();
      if(command.equals("RIGHTALL"))   treetool.rightALL();

      if(command.equals("FILEWIN")){
        filewindow.setVisible(true);
gui.buttonreset();
      }

      if(command.equals("RESULT")){
        messagewindow.setVisible(true);
gui.buttonreset();
      }

      if(command.equals("OPEN")){
        if( saveWithDialog() != -1 ){
          open();
        }
System.gc();
gui.buttonreset();
      }

      if(command.equals("SAVE")){
        save();        
System.gc();
gui.buttonreset();
      }

      if(command.equals("SAVEDIRECT")){
      Xnode cnode = node;
      restoreProperty();
      Logout();
      ProjectFile = new XFile( ProjectDir, xml.属性値( project, "objectname" ) + ".prj" );
      saveProject();;
      Login( cnode );
System.gc();
gui.buttonreset();
      }

      if(command.equals("SETTING")){
        propertywindow.age();
gui.buttonreset();
      }

      if(command.equals("HTMLEDIT")){
        execute( HtmlEditCommand, false );
gui.buttonreset();
      }

      if(command.equals("CLRALL")){
         clear_all( initialdialog.age() );
gui.buttonreset();
      }

      if(command.equals("VERSION")){
         dialog1.age(VERSION_STRING);
gui.buttonreset();
      }

      if(command.equals("HELP")){
         execute( HelpCommand, false );
gui.buttonreset();
      }

      if(command.equals("JAVAHELP")){
         execute( NativeHelpCommand[ApplicationType], false );
gui.buttonreset();
      }

      if(command.equals("UP_OBJ")){
        treetool.changeParent();
gui.buttonreset();
      }

      if(command.equals("COMPILE") ){
          if(compile_ready){
            Xnode cnode = node;
            Logout();
            messagewindow.clearText();
            messagewindow.setVisible(true);
            new Thread(new Runnable() {
              @Override
	          public void run() {
                compile_ready = false;
                compile_project(treetool.top.element);
                compile_ready = true;
	          }
	        }).start();
            Login( cnode );
            System.gc();
gui.buttonreset();
          }
          else{
            dialog1.age("まだコンパイル中です");
	      }
      }
      
      if(command.equals("RUN")){
        messagewindow.execcommand("実行します\n", "\n実行できません\n", RunCommand[ApplicationType]);
gui.buttonreset();

      }
      
      if (command.equals("PRINT")) {
        treetool.printtool.printCurrent();
System.gc();
gui.buttonreset();
      }

      if (command.equals("PRINTALL")) {
        treetool.printtool.printAll();
System.gc();
gui.buttonreset();
      }

      if(command.equals("BACK")){
gui.buttonreset();
        treetool.changeNode( mae_node );
      }
      
      if(command.equals("QUIT"))     {
System.gc();
gui.buttonreset();
        if( saveWithDialog() != -1 ){
          exitProgram();
        }
      }

    }


    //  プログラム初期化
    public void clear_all( int mode ){
      if( mode >= 0 ){
		int mode0 = mode;
        Logout();
        ApplicationType = mode0;
        newProject();
        syncProperty();
        initialise();
        ApplicationType = mode0; // プロジェクト新規作成の際にApplicationTypeが書き換えられてしまうので

        setlookandfeel();
        gui.resize();
        gui.name.setCaretPosition(0);
        gui.name.moveCaretPosition( gui.name.getText().length() );
        gui.name.requestFocus();
        gui.setobjectname( xml.属性値( element, "objectname" ) );
        gui.setdescription( xml.属性値( element, "description" ) );
      }
      else if( mode == -2 ){
        open();
      }
System.gc();
    }


    // マウスで位置データを与える
    public void mousePointed( int xp, int yp ){

      // create xobject
      if( mode.equals("CRE_XOBJ")){
        Point p = gui.gedit.getLocationOnScreen();
        String objname = inputdialog.age( p.x + xp, p.y + yp, "オブジェクト名の入力", "Object" + ID_maker );
        xobject xobj = new xobject( xml.新しい要素( element, "xobject", "_X"+ID_maker++ ), objname, "",xp, yp );
        gui.addcomponent( xobj );
        treetool.addNode( xobj.node, node );
        mode = "NOP";
gui.buttonreset();
      }
      
      // create aobject
      else if( mode.equals("CRE_AOBJ")){
        Point p = gui.gedit.getLocationOnScreen();
        String objname = inputdialog.age( p.x + xp, p.y + yp, "オブジェクト名の入力", "Object" + ID_maker );
        aobject aobj = new aobject( xml.新しい要素( element, "aobject", "_A"+ID_maker++ ), objname, "",xp, yp );
        gui.addcomponent( aobj );
        treetool.addNode( aobj.node, node );
        mode = "NOP";
gui.buttonreset();
      }
      
      // create operation
      else if( mode.equals("CRE_OP")){
        gui.addcomponent( new operation( xml.新しい要素( element,"operation", "_O"+ID_maker++ ), xp, yp, "in()", 90, 20, 40, 20, "out()", 90, 40, 40, 20 ) );
        mode = "NOP";
gui.buttonreset();
      }
      
      // create pin
      else if( mode.equals("CRE_PIN")){
        pin npin = new pin( xml.新しい要素( element, "pin", "_P"+ID_maker ), "Pin"  + ID_maker + "()", xp, yp );
        ID_maker++;
        gui.addcomponent( npin );
        npin.setCaretPosition(0);
        npin.moveCaretPosition( npin.getText().length() );
        npin.requestFocus();
        mode = "NOP";
gui.buttonreset();
      }
       
      // create codeclip
      else if( mode.equals("CRE_CODE")){
        gui.addcomponent( new codeclip( xml.新しい要素(element, "codeclip", "_C"+ID_maker++ ), "", xp, yp ) );
        mode = "NOP";
gui.buttonreset();
      }
  
      // create kjgroup
      else if( mode.equals("CRE_KJG")){
        KJgroup gr = new KJgroup( xml.新しい要素( element, "KJgroup", "_G"+ID_maker ), "Group"  + ID_maker, xp, yp );
        ID_maker++;
        gui.addcomponent( gr );
        mode = "NOP";
gui.buttonreset();
      }
       
      // create ImgIcon
      else if( mode.equals("CRE_ICO")){
        ImgIcon ic = new ImgIcon( xml.新しい要素( element, "ImgIcon", "_I"+ID_maker ), xp, yp );
        ID_maker++;
        gui.addcomponent( ic );
        mode = "NOP";
gui.buttonreset();
      }
       
      // paste object file
      else if( mode.equals("PASTE")){
        PasteObject( xp, yp );
        mode = "NOP";
gui.buttonreset();
      }

      // load object file
      else if( mode.equals("LOAD_COMP")){
        LoadObject( loadfile, xp, yp );
        mode = "NOP";
gui.buttonreset();
      }

      // GUIデザイナで作成したオブジェクトを貼り付ける
      else if( mode.equals("GUIDSIN")){
        Object obj;
        GUIDesignerWork.Xdelete();
        LayoutData.Xdelete();
        execute( GUIDesignerCommand[ ApplicationType ], true );
        if( ( obj = LoadObject( GUIDesignerWork, xp, yp ) ) != null ){
           xml.属性値をセット( obj,"レイアウト", LayoutData.toTextString() );
        }
        mode = "NOP";
gui.buttonreset();
      }

      // プロジェクトを貼り付ける
      else if( mode.equals("PASTE_PROJ")){
        if(ApplicationType == 8){
          paste_project(xp, yp);
        }
        else{
          dialog1.age("マルチ言語アプリケーション専用です");
	    }
        mode = "NOP";
gui.buttonreset();
      }

      gui.resize();
    }
    
    //マウスでコンポーネントをクリックする
    public void componentClicked( Component comp, MouseEvent mouse_event ){

      //create relation (input pin selection phase)
      if( mode.equals("CRE_REL") ){
        if( comp instanceof pin || comp instanceof pinlabel || comp instanceof operation ){
          comp1 = comp;
          mode = "CRE_REL2";
        }
        else{
          mode = "NOP";
gui.buttonreset();
        }
      }
      
      //create relation (output pin selection phase)
      else if( mode.equals("CRE_REL2") ){
        if( comp instanceof pin || comp instanceof pinlabel || comp instanceof operation ){
          comp2 = comp;
          if( comp1 != comp2 ){
            relation rel = new relation( xml.新しい要素( element, "relation", "_R"+ID_maker++ ), comp1, comp2 );
            gui.addcomponent(rel);
            rel.redrawLine();
          }
        }
        mode = "NOP";
gui.buttonreset();
      }
     
     
      // delete a object
      else if( mode.equals("DEL_OBJ") ){
        comp1 = comp;
        if( ( comp1 instanceof xobject ) || ( comp1 instanceof aobject ) ){
          removecomponent( comp1 );
          treetool.validate();
          treetool.repaint();
        }
        else removecomponent( comp1 );
        mode = "NOP";
gui.buttonreset();
      }

      // xobjectに変換する
      else if( mode.equals("TOX") ){
        if( comp instanceof operation ){
          treetool.utox( ((operation)comp).element );
        }
        else if( comp instanceof KJgroup ){
          treetool.grouptox( ((KJgroup)comp).element );
        }
gui.buttonreset();
      }

      // KJgroupに変換する
      else if( mode.equals("TOGROUP") ){
        if( comp instanceof xobject ){
          treetool.xtogroup( ((xobject)comp).node );
          if( !NoOptimizePin ) treetool.optpin();
        }
gui.buttonreset();
      }

      // copy a object
      else if( mode.equals("COPY") ){
        comp1 = comp;
        xml.要素を削除( xml.子要素( xml.ルート要素(), "clipboad" ) );
        if( comp1 instanceof xobject )      {
          ((xobject)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((xobject)comp1).element, "clipboad" );
         }
        else if( comp1 instanceof aobject ) {
          ((aobject)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((aobject)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof operation ) {
          ((operation)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((operation)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof pin )     {
          ((pin)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((pin)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof codeclip ){
          ((codeclip)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((codeclip)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof KJgroup ){
          ((KJgroup)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((KJgroup)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof ImgIcon ){
          ((ImgIcon)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((ImgIcon)comp1).element, "clipboad" );
        }
        mode = "NOP";
gui.buttonreset();
      }

      // cut a object
      else if( mode.equals("CUT") ){
        comp1 = comp;
        xml.要素を削除( xml.子要素( xml.ルート要素(), "clipboad" ) );
        if( comp1 instanceof xobject )      {
          ((xobject)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((xobject)comp1).element, "clipboad" );
         }
        else if( comp1 instanceof aobject ) {
          ((aobject)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((aobject)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof operation ) {
          ((operation)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((operation)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof pin )     {
          ((pin)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((pin)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof codeclip ){
          ((codeclip)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((codeclip)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof KJgroup ){
          ((KJgroup)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((KJgroup)comp1).element, "clipboad" );
        }
        else if( comp1 instanceof ImgIcon ){
          ((ImgIcon)comp1).save();
          clipboad = xml.新しい要素( xml.ルート要素(), ((ImgIcon)comp1).element, "clipboad" );
        }
        if( ( comp1 instanceof xobject ) || ( comp1 instanceof aobject ) ){
          removecomponent( comp1 );
          treetool.validate();
          treetool.repaint();
        }
        else removecomponent( comp1 );
        mode = "NOP";
gui.buttonreset();
      }

      // save a object
      else if( mode.equals("SAVE_COMP") ){
        comp1 = comp;

        if( comp1 instanceof xobject )      {
          xobject xo = (xobject)comp1;
          xo.save();
          filewindow.save( xo.element, new XFile( ObjectLib[ApplicationType], xo.namebutton.getText()+".xml" ) );
         }

        else if( comp1 instanceof aobject ) {
          aobject ao = (aobject)comp1;
          ao.save();
          filewindow.save( ao.element, new XFile( ObjectLib[ApplicationType], ao.namebutton.getText()+".xml" ) );
        }

        else if( comp1 instanceof operation ){
          operation op = (operation)comp1;
          op.save();
          filewindow.save( op.element, new XFile( ObjectLib[ApplicationType], "~" + compack( op.description.getText() )+".xml" ) );
        }

        else if( comp1 instanceof pin )     {
          pin pi = (pin)comp1;
          pi.save();
          filewindow.save( pi.element, new XFile( ObjectLib[ApplicationType], "~" + pi.getText()+".xml" ) );
        }

        else if( comp1 instanceof codeclip ){
          codeclip co = (codeclip)comp1;
          co.save();
          filewindow.save( co.element, new XFile( ObjectLib[ApplicationType], "~" + compack( getFirstLine( co.codetext.getText() ) )+".xml" ) );
        }


        else if( comp1 instanceof KJgroup ){
          KJgroup gr = (KJgroup)comp1;
          gr.save();
          filewindow.save( gr.element, new XFile( ObjectLib[ApplicationType], "~" + compack(  gr.comment.getText()  )+".xml" ) );
        }

        filewindow.chooser.rescanCurrentDirectory();
        mode = "NOP";
gui.buttonreset();
      }

      else if( mode.equals("CRE_PIN") ){
        if( comp instanceof xobject ){
          xobject xo = (xobject)comp;
          Point p = xo.getLocationOnScreen();
          String method = inputdialog.age( p.x + mouse_event.getX(), p.y + mouse_event.getY(), "メソッド名の入力", "pin" + ( xo.ID_maker ) + "()" );
          xo.addcomponent( new pinlabel( xml.新しい要素( xo.element, "pin", "_P" + xo.ID_maker++ ), method, mouse_event.getX(), mouse_event.getY() ) );
        }
        else if( comp instanceof aobject ){
          aobject ao = (aobject)comp;
          Point p = ao.getLocationOnScreen();
          String method = inputdialog.age( p.x + mouse_event.getX(), p.y + mouse_event.getY(), "メソッド名の入力", "pin" + ( ao.ID_maker ) + "()" );
          ao.addcomponent( new pinlabel( xml.新しい要素( ao.element, "pin", "_P" + ao.ID_maker++ ), method, mouse_event.getX(), mouse_event.getY() ) );
        }
        mode = "NOP";
gui.buttonreset();
      }

      else if( mode.equals("NOP") && ( comp instanceof pinlabel ) ){
        pinlabel pin = (pinlabel)comp;
        Point p = pin.getLocationOnScreen();
        pin.setText( inputdialog.age( p.x, p.y, "メソッドを入力",  pin.getText() ) );
        pin.setSize( pin.getPreferredSize() );
      }

      checkconnect();
      gui.resize();
    }
    
    // ObjectEditorのGUIを記述
  class GUI extends JFrame implements ActionListener, ItemListener, MouseListener, WindowListener {
      Font font;
      Insets mergin;
      ButtonGroup group;
      JToggleButton noselected;

      JMenuBar menuBar;
          JMenu filemenu;
              JMenuItem upobj;
              JMenuItem clrall;
              JMenuItem open;
              JMenuItem filewin;
              JMenuItem savedirect;
              JMenuItem save;
              JMenuItem prnt;
              JMenuItem prntall;
              JMenuItem compile;
              JMenuItem run;
              JMenuItem exitprog;

          JMenu toolmenu;
              JMenuItem xobj;
              JMenuItem aobj;
              JMenuItem uobj;
              JMenuItem signal;
              JMenuItem pin;
              JMenuItem codeclip;
              JMenuItem kjgroup;

              JMenuItem guidsin;
              JMenuItem proj;

          JMenu editmenu;
              JMenuItem delete;
              JMenuItem cut;
              JMenuItem copy;
              JMenuItem paste;

//            JMenuItem optpin;
              JMenuItem togroup;
              JMenuItem tox;


          JMenu dispmenu;
              JCheckBoxMenuItem disptoolbox;
              JMenuItem dispresult;
              JMenuItem upall;
              JMenuItem downall;
              JMenuItem leftall;
              JMenuItem rightall;

          JMenu setmenu;
              JMenuItem setting;
              JMenuItem applethtml;
              
          JMenu helpmenu;
              JMenuItem objver;
              JMenuItem objhelp;
              JMenuItem javahelp;

    JToolBar toolBar;
          JToggleButton bback;
          JToggleButton bupobj;
          JToggleButton bclrall;
          JToggleButton bfilewin;
          JToggleButton bcompile;
          JToggleButton brun;

          JToggleButton bxobj;
          JToggleButton baobj;
          JToggleButton buobj;
          JToggleButton bsignal;
          JToggleButton bpin;
          JToggleButton bcodeclip;
          JToggleButton bkjgroup;
          JToggleButton bicon;
          JToggleButton bguidsin;

          JToggleButton bdelete;
          JToggleButton bcut;
          JToggleButton bcopy;
          JToggleButton bpaste;
          JToggleButton btogroup;
          JToggleButton btox;

          JToggleButton bsetting;
          JToggleButton bhelp;

    JSplitPane display;
        JTextField name;                    //名前を表示するエリア      
        JSplitPane contents;                      //(VERTICAL Splitレイアウト)
            JScrollPane description;             //(Scrollレイアウト)
                JTextArea descriptionarea;               //説明文を表示するエリア
            JScrollPane graphic;                //(Scrollレイアウト)
                JPanel  gedit;                    //グラフィック編集エリア

    GUI() {

        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        mergin = new Insets( 0, 0, 0, 0 );
        group = new ButtonGroup();
        noselected = new JToggleButton();
        group.add( noselected );

        //Create the menu bar.
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        //ファイルメニューを生成
        filemenu = new JMenu("ファイル(F)");
        font = new Font( filemenu.getFont().getName(), Font.PLAIN, 12 );
        filemenu.setFont( font );
        filemenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(filemenu);

        upobj = new JMenuItem("上位のオブジェクトに移動(U)", KeyEvent.VK_U);
        upobj.setFont( font );
        upobj.setActionCommand("UP_OBJ");
        upobj.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_U, ActionEvent.CTRL_MASK ) );
        upobj.addActionListener(this);
        filemenu.add(upobj);

        clrall = new JMenuItem("プロジェクトの新規作成(N)", KeyEvent.VK_N);
        clrall.setFont( font );
        clrall.setActionCommand("CLRALL");
        clrall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, ActionEvent.CTRL_MASK ) );
        clrall.addActionListener(this);
        filemenu.add(clrall);

        open = new JMenuItem("プロジェクトを開く(O)", KeyEvent.VK_O);
        open.setFont( font );
        open.setActionCommand("OPEN");
        open.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.CTRL_MASK ) );
        open.addActionListener(this);
        filemenu.add(open);

        filewin = new JMenuItem("部品棚を表示(F)", KeyEvent.VK_F);
        filewin.setFont( font );
        filewin.setActionCommand("FILEWIN");
        filewin.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F, ActionEvent.CTRL_MASK ) );
        filewin.addActionListener(this);
        filemenu.add(filewin);

        savedirect = new JMenuItem("プロジェクトの上書き保存(S)", KeyEvent.VK_S);
        savedirect.setFont( font );
        savedirect.setActionCommand("SAVEDIRECT");
        savedirect.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK ) );
        savedirect.addActionListener(this);
        filemenu.add(savedirect);

        save = new JMenuItem("プロジェクトの保存(A)", KeyEvent.VK_A);
        save.setFont( font );
        save.setActionCommand("SAVE");
        save.addActionListener(this);
        filemenu.add(save);

        prnt = new JMenuItem("このページの印刷(1)", KeyEvent.VK_1);
        prnt.setFont( font );
        prnt.setActionCommand("PRINT");
        prnt.addActionListener(this);
        filemenu.add(prnt);

        prntall = new JMenuItem("全て印刷(P)", KeyEvent.VK_P);
        prntall.setFont( font );
        prntall.setActionCommand("PRINTALL");
        prntall.addActionListener(this);
        filemenu.add(prntall);

        filemenu.addSeparator();

        compile = new JMenuItem("コンパイル(C)", KeyEvent.VK_C);
        compile.setFont( font );
        compile.setActionCommand("COMPILE");
        compile.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, ActionEvent.ALT_MASK ) );
        compile.addActionListener(this);
        filemenu.add(compile);

        run = new JMenuItem("実行(R)", KeyEvent.VK_R);
        run.setFont( font );
        run.setActionCommand("RUN");
        run.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, ActionEvent.ALT_MASK ) );
        run.addActionListener(this);
        filemenu.add(run);

        filemenu.addSeparator();

        exitprog = new JMenuItem("終了(X)", KeyEvent.VK_X);
        exitprog.setFont( font );
        exitprog.setActionCommand("QUIT");
        exitprog.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q, ActionEvent.CTRL_MASK ) );
        exitprog.addActionListener(this);
        filemenu.add(exitprog);

        //ツールメニューを生成
        toolmenu = new JMenu("ツール(T)");
        toolmenu.setFont( font );
        toolmenu.setMnemonic(KeyEvent.VK_T);
        menuBar.add(toolmenu);

        xobj = new JMenuItem("Ｘオブジェクト(X)", KeyEvent.VK_X);
        xobj.setFont( font );
        xobj.setActionCommand("CRE_XOBJ");
        xobj.addActionListener(this);
        toolmenu.add(xobj);

        aobj = new JMenuItem("Ａオブジェクト(A)", KeyEvent.VK_A);
        aobj.setFont( font );
        aobj.setActionCommand("CRE_AOBJ");
        aobj.addActionListener(this);
        toolmenu.add(aobj);

        uobj = new JMenuItem("Ｕオブジェクト(U)", KeyEvent.VK_U);
        uobj.setFont( font );
        uobj.setActionCommand("CRE_OP");
        uobj.addActionListener(this);
        toolmenu.add(uobj);

        signal = new JMenuItem("信号(S)", KeyEvent.VK_S);
        signal.setFont( font );
        signal.setActionCommand("CRE_REL");
        signal.addActionListener(this);
        toolmenu.add(signal);

        pin = new JMenuItem("ピン(P)", KeyEvent.VK_P);
        pin.setFont( font );
        pin.setActionCommand("CRE_PIN");
        pin.addActionListener(this);
        toolmenu.add(pin);

        codeclip = new JMenuItem("コードクリップ(C)", KeyEvent.VK_C);
        codeclip.setFont( font );
        codeclip.addActionListener(this);
        codeclip.setActionCommand("CRE_CODE");
        toolmenu.add(codeclip);

        kjgroup = new JMenuItem("グループ(G)", KeyEvent.VK_G);
        kjgroup.setFont( font );
        kjgroup.setActionCommand("CRE_KJG");
        kjgroup.addActionListener(this);
        toolmenu.add(kjgroup);

        toolmenu.addSeparator();
     
        guidsin = new JMenuItem("GUIデザイナ", KeyEvent.VK_D);
        guidsin.setFont( font );
        guidsin.setActionCommand("GUIDSIN");
        guidsin.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_G, ActionEvent.CTRL_MASK ) );
        guidsin.addActionListener(this);
        toolmenu.add(guidsin);

        proj = new JMenuItem("プロジェクトの貼り付け(P)", KeyEvent.VK_P);
        proj.setFont( font );
        proj.setActionCommand("PASTE_PROJ");
        proj.addActionListener(this);
        toolmenu.add(proj);

        //編集メニューを生成
        editmenu = new JMenu("編集(E)");
        editmenu.setFont( font );
        editmenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editmenu);

        delete = new JMenuItem("削除(D)", KeyEvent.VK_D);
        delete.setFont( font );
        delete.setActionCommand("DEL_OBJ");
        delete.addActionListener(this);
        editmenu.add(delete);

        cut = new JMenuItem("切り取り(T)", KeyEvent.VK_T);
        cut.setFont( font );
        cut.setActionCommand("CUT");
        cut.addActionListener(this);
        editmenu.add(cut);

        copy = new JMenuItem("コピー(C)", KeyEvent.VK_C);
        copy.setFont( font );
        copy.setActionCommand("COPY");
        copy.addActionListener(this);
        editmenu.add(copy);

        paste = new JMenuItem("貼り付け(P)", KeyEvent.VK_P);
        paste.setFont( font );
        paste.setActionCommand("PASTE");
        paste.addActionListener(this);
        editmenu.add(paste);

        editmenu.addSeparator();
     
        tox = new JMenuItem("Ｘオブジェクトに変換(X)", KeyEvent.VK_X);
        tox.setFont( font );
        tox.setActionCommand("TOX");
        tox.addActionListener(this);
        editmenu.add(tox);

        togroup = new JMenuItem("Ｘオブジェクトを展開(Y)", KeyEvent.VK_Y);
        togroup.setFont( font );
        togroup.setActionCommand("TOGROUP");
        togroup.addActionListener(this);
        editmenu.add(togroup);

        //表示メニューを生成
        dispmenu = new JMenu("表示(V)");
        dispmenu.setFont( font );
        dispmenu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(dispmenu);

        disptoolbox = new JCheckBoxMenuItem("ツールバー(T)", ToolBarVisible);
        disptoolbox.setFont( font );
        disptoolbox.setMnemonic(KeyEvent.VK_T);
        disptoolbox.addItemListener(this);
        dispmenu.add(disptoolbox);

        dispmenu.addSeparator();

        dispresult = new JMenuItem("コンパイル･実行結果(V)", KeyEvent.VK_V);
        dispresult.setFont( font );
        dispresult.setActionCommand("RESULT");
        dispresult.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, ActionEvent.CTRL_MASK ) );
        dispresult.addActionListener(this);
        dispmenu.add(dispresult);

        dispmenu.addSeparator();

        upall = new JMenuItem("全体を上にずらす(U)", KeyEvent.VK_U);
        upall.setFont( font );
        upall.setActionCommand("UPALL");
        upall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_UP, ActionEvent.ALT_MASK ) );
        upall.addActionListener(this);
        dispmenu.add(upall);

        downall = new JMenuItem("全体を下にずらす(D)", KeyEvent.VK_D);
        downall.setFont( font );
        downall.setActionCommand("DOWNALL");
        downall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, ActionEvent.ALT_MASK ) );
        downall.addActionListener(this);
        dispmenu.add(downall);

        leftall = new JMenuItem("全体を左にずらす(L)", KeyEvent.VK_L);
        leftall.setFont( font );
        leftall.setActionCommand("LEFTALL");
        leftall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, ActionEvent.ALT_MASK ) );
        leftall.addActionListener(this);
        dispmenu.add(leftall);

        rightall = new JMenuItem("全体を右にずらす(R)", KeyEvent.VK_R);
        rightall.setFont( font );
        rightall.setActionCommand("RIGHTALL");
        rightall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK ) );
        rightall.addActionListener(this);
        dispmenu.add(rightall);

        //設定メニューを生成
        setmenu = new JMenu("設定(S)");
        setmenu.setFont( font );
        setmenu.setMnemonic(KeyEvent.VK_S);
        menuBar.add(setmenu);

        setting = new JMenuItem("設定(S)", KeyEvent.VK_S );
        setting.setFont( font );
        setting.setActionCommand("SETTING");
        setting.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, ActionEvent.CTRL_MASK ) );
        setting.addActionListener(this);
        setmenu.add(setting);

        setmenu.addSeparator();

        applethtml = new JMenuItem("アプレットhtml(A)", KeyEvent.VK_A );
        applethtml.setFont( font );
        applethtml.setActionCommand("HTMLEDIT");
        applethtml.addActionListener(this);
        setmenu.add(applethtml);

        //ヘルプメニューを生成
        helpmenu = new JMenu("ヘルプ(H)");
        helpmenu.setFont( font );
        helpmenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpmenu);

        objver = new JMenuItem("バージョン情報", KeyEvent.VK_V );
        objver.setFont( font );
        objver.setActionCommand("VERSION");
        objver.addActionListener(this);
        helpmenu.add(objver);

        objhelp = new JMenuItem("ObjectEditorのヘルプ", KeyEvent.VK_O );
        objhelp.setFont( font );
        objhelp.setActionCommand("HELP");
        objhelp.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H, ActionEvent.CTRL_MASK ) );
        objhelp.addActionListener(this);
        helpmenu.add(objhelp);

        javahelp = new JMenuItem("言語のヘルプ", KeyEvent.VK_J );
        javahelp.setFont( font );
        javahelp.setActionCommand("JAVAHELP");
        javahelp.addActionListener(this);
        helpmenu.add(javahelp);


        //ツールバーを生成
        toolBar = new JToolBar();
        toolBar.setMargin( new Insets(1, 1, 1, 1 ) );


        name = new JTextField("NewObject");
        name.setMinimumSize( new Dimension( 80, 16 ) );
        name.setMaximumSize( new Dimension( 240, 24 ) );
        name.setToolTipText("オブジェクトの名前");
        name.setActionCommand("SET_NAME");
        toolBar.add(name);

        bback = new JToggleButton(new ImageIcon("resources/back.jpg"));
        bback.setToolTipText("ひとつ前に戻る");
        bback.setActionCommand("BACK");
        bback.addActionListener(this);
        bback.setMargin(mergin);
        toolBar.add(bback);
        group.add(bback);

        bupobj = new JToggleButton(new ImageIcon("resources/upobj.jpg"));
        bupobj.setToolTipText("上位オブジェクトに移動");
        bupobj.setActionCommand("UP_OBJ");
        bupobj.addActionListener(this);
        bupobj.setMargin(mergin);
        toolBar.add(bupobj);
        group.add(bupobj);

        bclrall = new JToggleButton(new ImageIcon("resources/clrall.jpg"));
        bclrall.setToolTipText("新規作成");
        bclrall.setActionCommand("CLRALL");
        bclrall.addActionListener(this);
        bclrall.setMargin(mergin);
        toolBar.add(bclrall);
        group.add(bclrall);

        bfilewin = new JToggleButton(new ImageIcon("resources/filewin.jpg"));
        bfilewin.setToolTipText("部品棚を表示");
        bfilewin.setActionCommand("FILEWIN");
        bfilewin.addActionListener(this);
        bfilewin.setMargin(mergin);
        toolBar.add(bfilewin);
        group.add(bfilewin);

        bcompile = new JToggleButton(new ImageIcon("resources/compile.jpg"));
        bcompile.setToolTipText("コンパイル");
        bcompile.setActionCommand("COMPILE");
        bcompile.addActionListener(this);
        bcompile.setMargin(mergin);
        toolBar.add(bcompile);
        group.add(bcompile);

        brun = new JToggleButton(new ImageIcon("resources/run.jpg"));
        brun.setToolTipText("実行");
        brun.setActionCommand("RUN");
        brun.addActionListener(this);
        brun.setMargin(mergin);
        toolBar.add(brun);
        group.add(brun);

        toolBar.addSeparator();

        bxobj = new JToggleButton(new ImageIcon("resources/xobj.jpg"));
        bxobj.setToolTipText("Ｘオブジェクトを貼り付ける");
        bxobj.setActionCommand("CRE_XOBJ");
        bxobj.addActionListener(this);
        bxobj.setMargin(mergin);
        toolBar.add(bxobj);
        group.add(bxobj);

        baobj = new JToggleButton(new ImageIcon("resources/aobj.jpg"));
        baobj.setToolTipText("Ａオブジェクトを貼り付ける");
        baobj.setActionCommand("CRE_AOBJ");
        baobj.addActionListener(this);
        baobj.setMargin(mergin);
        toolBar.add(baobj);
        group.add(baobj);

        buobj = new JToggleButton(new ImageIcon("resources/uobj.jpg"));
        buobj.setToolTipText("Ｕオブジェクトを貼り付ける");
        buobj.setActionCommand("CRE_OP");
        buobj.addActionListener(this);
        buobj.setMargin(mergin);
        toolBar.add(buobj);
        group.add(buobj);

        bsignal = new JToggleButton(new ImageIcon("resources/signal.jpg"));
        bsignal.setToolTipText("信号線を貼り付ける");
        bsignal.setActionCommand("CRE_REL");
        bsignal.addActionListener(this);
        bsignal.setMargin(mergin);
        toolBar.add(bsignal);
        group.add(bsignal);

        bpin = new JToggleButton(new ImageIcon("resources/pin.jpg"));
        bpin.setToolTipText("ピンを貼り付ける");
        bpin.setActionCommand("CRE_PIN");
        bpin.addActionListener(this);
        bpin.setMargin(mergin);
        toolBar.add(bpin);
        group.add(bpin);

        bcodeclip = new JToggleButton(new ImageIcon("resources/codeclip.jpg"));
        bcodeclip.setToolTipText("コードクリップを貼り付ける");
        bcodeclip.setActionCommand("CRE_CODE");
        bcodeclip.addActionListener(this);
        bcodeclip.setMargin(mergin);
        toolBar.add(bcodeclip);
        group.add(bcodeclip);

        bkjgroup = new JToggleButton(new ImageIcon("resources/group.jpg"));
        bkjgroup.setToolTipText("グループを貼り付ける");
        bkjgroup.setActionCommand("CRE_KJG");
        bkjgroup.addActionListener(this);
        bkjgroup.setMargin(mergin);
        toolBar.add(bkjgroup);
        group.add(bkjgroup);

        bicon = new JToggleButton(new ImageIcon("resources/icon.jpg"));
        bicon.setToolTipText("アイコンを貼り付ける");
        bicon.setActionCommand("CRE_ICO");
        bicon.addActionListener(this);
        bicon.setMargin(mergin);
        toolBar.add(bicon);
        group.add(bicon);

        bguidsin = new JToggleButton(new ImageIcon("resources/guidsin.jpg"));
        bguidsin.setToolTipText("ＧＵＩデザイナ");
        bguidsin.setActionCommand("GUIDSIN");
        bguidsin.addActionListener(this);
        bguidsin.setMargin(mergin);
        toolBar.add(bguidsin);
        group.add(bguidsin);

        toolBar.addSeparator();

        bdelete = new JToggleButton(new ImageIcon("resources/delete.jpg"));
        bdelete.setToolTipText("削除");
        bdelete.setActionCommand("DEL_OBJ");
        bdelete.addActionListener(this);
        bdelete.setMargin(mergin);
        toolBar.add(bdelete);
        group.add(bdelete);

        bcut = new JToggleButton(new ImageIcon("resources/cut.jpg"));
        bcut.setToolTipText("切り取り");
        bcut.setActionCommand("CUT");
        bcut.addActionListener(this);
        bcut.setMargin(mergin);
        toolBar.add(bcut);
        group.add(bcut);

        bcopy = new JToggleButton(new ImageIcon("resources/copy.jpg"));
        bcopy.setToolTipText("コピー");
        bcopy.setActionCommand("COPY");
        bcopy.addActionListener(this);
        bcopy.setMargin(mergin);
        toolBar.add(bcopy);
        group.add(bcopy);

        bpaste = new JToggleButton(new ImageIcon("resources/paste.jpg"));
        bpaste.setToolTipText("貼り付け");
        bpaste.setActionCommand("PASTE");
        bpaste.addActionListener(this);
        bpaste.setMargin(mergin);
        toolBar.add(bpaste);
        group.add(bpaste);

        btox = new JToggleButton(new ImageIcon("resources/g2x.jpg"));
        btox.setToolTipText("Ｘオブジェクトに変換");
        btox.setActionCommand("TOX");
        btox.addActionListener(this);
        btox.setMargin(mergin);
        toolBar.add(btox);
        group.add(btox);

        btogroup = new JToggleButton(new ImageIcon("resources/x2g.jpg"));
        btogroup.setToolTipText("Ｘオブジェクトを展開");
        btogroup.setActionCommand("TOGROUP");
        btogroup.addActionListener(this);
        btogroup.setMargin(mergin);
        toolBar.add(btogroup);
        group.add(btogroup);

        toolBar.addSeparator();

        bsetting = new JToggleButton(new ImageIcon("resources/setting.jpg"));
        bsetting.setToolTipText("設定");
        bsetting.setActionCommand("SETTING");
        bsetting.addActionListener(this);
        bsetting.setMargin(mergin);
        toolBar.add(bsetting);
        group.add(bsetting);

        bhelp = new JToggleButton(new ImageIcon("resources/help.jpg"));
        bhelp.setToolTipText("ヘルプ");
        bhelp.setActionCommand("HELP");
        bhelp.addActionListener(this);
        bhelp.setMargin(mergin);
        toolBar.add(bhelp);
        group.add(bhelp);

        descriptionarea = new JTextArea("Objectの説明");
        descriptionarea.setToolTipText("オブジェクトの説明");
        description = new JScrollPane(descriptionarea);
        description.setMinimumSize( new Dimension( 1, 1 ) );
        
        gedit = new JPanel();
        gedit.setBackground( Color.white );
        gedit.setToolTipText("オブジェクトの接続図");
        gedit.setLayout( null );
        gedit.setSize(new Dimension(600,300));
        gedit.addMouseListener(this);
        graphic = new JScrollPane(gedit);

        contents = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, description, graphic );
        contents.setDividerSize(4);
        contents.setAlignmentX(JComponent.LEFT_ALIGNMENT);        

        display = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
        display.setDividerSize(4);
        display.setPreferredSize(new Dimension(600,300));
        display.setRightComponent(contents);

        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(display, BorderLayout.CENTER);
        addWindowListener(this);
        pack();

    }//~GUI()

      //名前を得る
      public String getobjectname(){ return(name.getText()); }

      //名前をセットする
      public void setobjectname( String nm ){
        name.setText(nm);
        setTitle("ObjectEditor "+ name.getText() + AppTypes[ApplicationType]);
      }

      //説明を得る
      public String getdescription(){ return(descriptionarea.getText()); }

      //説明をセットする
      public void setdescription( String descript ){
        descriptionarea.setText(descript);
      }

      //格納されているコンポーネントを取得する
      public Component[] getcomponents(){ return( gedit.getComponents() ); }

      //与えられた名前のコンポーネントを返す
      public Component getcomponent( String name ){
        int i;
        Component[]  comp = getcomponents();
        if( name.charAt( name.length()-1 ) == ')' ){
          Component cmp1 = getcomponent( getbase( name ) );
          if( cmp1 instanceof xobject )  return( ((xobject)cmp1).getcomponent(name) );
          else if( cmp1 instanceof aobject )  return( ((aobject)cmp1).getcomponent(name) );
          else return( null );
        }
        for( i=0; i<comp.length; i++){
          if( comp[i].getName().equals( name ) ) return( comp[i] );
        }
        return( null );
      }

      // コンポーネントを追加する
      public void addcomponent( Component cmp ){
        gedit.add(cmp);
        gedit.validate();
      }

      // 画面の内容を消去する
      public void clear(){
        name.setText("");
        descriptionarea.setText("");
        gedit.removeAll();
        buttonreset();
      }

      public void buttonreset(){
           noselected.setSelected(true);
      }

      //画面のサイズを最適化する
      public void resize(){
        int i;
        Component[] cmp;
        int width;
        int height;

        cmp = getcomponents();
        width = 200;
        height = 100;
        for( i=0; i<cmp.length; i++ ){
          if( cmp[i].getLocation().x + cmp[i].getWidth()  > width  ) width  = cmp[i].getLocation().x + cmp[i].getWidth();
          if( cmp[i].getLocation().y + cmp[i].getHeight() > height ) height = cmp[i].getLocation().y + cmp[i].getHeight();
        }
        gedit.setPreferredSize( new Dimension( width, height ) );
        gedit.setSize( width, height );
        validate();
        repaint();
        if( filewindow != null ) filewindow.setVisible( filewindow.isVisible() );
      }

      // action event
      public void actionPerformed(ActionEvent e ){  CommandReceived( e.getActionCommand() ); } 

      // mouse event
      public void mousePressed(MouseEvent e) { mousePointed( e.getX(), e.getY() ); }
      public void mouseReleased(MouseEvent e){ resize(); }
      public void mouseClicked(MouseEvent e) {   }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }

      // window event
      public void windowClosing(WindowEvent e)    { CommandReceived( "QUIT" ); }
      public void windowActivated(WindowEvent e)  {   }
      public void windowClosed(WindowEvent e)     {   }
      public void windowDeactivated(WindowEvent e){   }
      public void windowDeiconified(WindowEvent e){   }
      public void windowIconified(WindowEvent e)  {   }
      public void windowOpened(WindowEvent e)     {   }

      public void itemStateChanged(ItemEvent e) {
        ToolBarVisible = disptoolbox.isSelected();
        setVisible();
      }

      // 
      public void setVisible(){
        if( isVisible() ){
          stateeditor.gui.disptoolbox.setState(ToolBarVisible);
          toolBar.setVisible(ToolBarVisible);
          resize();
        }
      } 

    }//GUI

      

    // xobject クラス
    class xobject extends JPanel implements ActionListener, MouseListener, MouseMotionListener{

      boolean exist;
      Xnode node;
      Object element;
      int x0, y0,x1, y1, x2, y2;
      int width, height;
      int ID_maker;
      int mode;
      Graphics grp;
      String txt;

      JButton namebutton;
      String description;

      // 新規作成(ファイルも一緒に生成する)
      xobject( Object elem, String name, String descript, int x, int y ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        x0 = x;
        y0 = y;
        width  = 107;
        height = 50;
        ID_maker = 0;
        namebutton = new JButton( name );
        namebutton.setName("NameButton");
        description = descript;
namebutton.setToolTipText( getFirstLine( description ) );
        setBounds(x0,y0,width,height);
        setForeground(Color.black);
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        namebutton.addActionListener(this);
        add(namebutton);
        Dimension d=namebutton.getPreferredSize();
        namebutton.setBounds(0,0,d.width,d.height);
        save();
        node = new Xnode( element );
        mode = 1;
      }

      // Loginモードで生成(ファイルの情報をもとに生成)  
      xobject( Xnode nod ){
        exist = true;
        node = nod;
        element = node.element;
        setName( xml.要素のID( element ) );
        namebutton = new JButton( " " );
        namebutton.setName("NameBotton");
        load();
        namebutton.setToolTipText( getFirstLine( description ) );
        setBounds( x0, y0, width, height );
        setForeground(Color.black);
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        namebutton.addActionListener(this);
        add(namebutton);
        Dimension d=namebutton.getPreferredSize();
        namebutton.setSize(d);

        //pinlabelを作成
        Vector pins = xml.子要素のリスト( element, "pin" );
        for(int i=0; i<pins.size();i++){
          addcomponent( new pinlabel( pins.get(i) ) );
        }
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        int i;
        if( exist ){

          // pinlabelをLogoutする  
          Component cmp[] = getcomponents();
          for( i = 0;i<cmp.length;i++ ){
            if( cmp[i] instanceof pinlabel ) ( (pinlabel)cmp[i] ).Logout();
          }
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        node.suicide();
        node = null;
        element = null;
      }

      // Logout(内容をxmlにセーブして消去)
      public void Logout(){
          int i;
          if( exist ){
          save();

          // pinlabelをLogoutする  
          Component cmp[] = getcomponents();
          for( i = 0;i<cmp.length;i++ ){
            if( cmp[i] instanceof pinlabel ) ( (pinlabel)cmp[i] ).Logout();
          }
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        ID_maker = parseInt( xml.属性値( element, "ID_maker" ) );
        namebutton.setText( xml.属性値( element, "objectname" ) );
        description = xml.属性値( element, "description" );
      }

      // 内容をセーブ
      public void save(){
        Component cmp[] = getcomponents();
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "ID_maker", "" + ID_maker );
        xml.属性値をセット( element, "objectname", namebutton.getText() );
        xml.属性値をセット( element, "description", description );

          for( int i = 0;i<cmp.length;i++ ){
            if( cmp[i] instanceof pinlabel ) ( (pinlabel)cmp[i] ).save();
          }
      }

      //コンポーネントを追加する
      public void addcomponent( Component cmp ){
        add(cmp);
        Dimension d=cmp.getPreferredSize();
        cmp.setBounds(0,0,d.width,d.height);
      }

      //格納されているコンポーネントを取得する
      public Component[] getcomponents(){ return( getComponents() ); }

      //与えられた名前のコンポーネントを返す
      public Component getcomponent( String name ){
        int i;
        Component cmp[] = getcomponents();
        for( i = 0; i<cmp.length; i++){
          if( cmp[i].getName().equals( name ) ) return( cmp[i] );
        }
        return( null );
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          mode = 2;
        }
        super.paintComponent(g);
        g.drawRect( 0, 0, width-1, height-1 );
      }

      //ボタンがクリックされたときの処理
      public void actionPerformed(ActionEvent e ){ treetool.changeNode(node); }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          txt = namebutton.getText();
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
//          setSize(1,1);
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
        grp.setXORMode(Color.white);
        grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
          width =  x1 + 5;
          if( width < 10 ) width = 10;
          height = y1 + 5;
          if( height < 10 ) height = 10;
        }
        else if( mode == 4){
          x0 += x1 - x2;
          if( x0 < 0) x0 = 0;
          y0 += y1 - y2;
          if(y0 < 0 ) y0 = 0;
        }
        x2 = x1;
        y2 = y1;
        grp.drawRect( x0+1, y0+1, width-2, height-2 );
        grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this, e );  }
      public void mouseMoved(MouseEvent e)   {   } 
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }

    }//~xobject

    // aobject クラス
    class aobject extends JPanel implements ActionListener, MouseListener, MouseMotionListener{

      boolean exist;
      Anode node;
      Object element;
      int x0, y0,x1, y1, x2, y2;
      int width, height;
      int ID_maker;
      int mode;
      String txt;
      Graphics grp;

      JButton namebutton;
      String description;

      // 新規作成(ファイルも一緒に生成する)
      aobject( Object elem, String name, String descript, int x, int y ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        x0 = x;
        y0 = y;
        width  = 107;
        height = 50;
        ID_maker = 0;
        namebutton = new JButton( name );
        namebutton.setName("NameButton");
        description = descript;
        namebutton.setToolTipText( getFirstLine( description ) );
        setBounds(x0,y0,width,height);
        setForeground(Color.blue);
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        namebutton.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        namebutton.addActionListener(this);
        add(namebutton);
        Dimension d=namebutton.getPreferredSize();
        namebutton.setBounds(0,0,d.width,d.height);
        setBounds(x0,y0,width,height);
        save();

        state st = new state( xml.新しい要素( element, "state", "_SINIT" ), 1, "  初期状態", 10, 10 );
        st.Logout();
        node = new Anode(element);
        mode = 1;
      }

      // Loginモードで生成(ファイルの情報をもとに生成)  
      aobject( Anode nod ){
        exist = true;
        node = nod;
        element = node.element;
        setName( xml.要素のID( element ) );
        namebutton = new JButton( " " );
        namebutton.setName("NameBotton");
        load();
        namebutton.setToolTipText( getFirstLine( description ) );
        setBounds( x0, y0, width, height );
        setForeground(Color.blue);
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        namebutton.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        namebutton.addActionListener(this);
        add(namebutton);
        Dimension d=namebutton.getPreferredSize();
        namebutton.setBounds(0,0,d.width,d.height);

        //pinlabelを作成
        Vector list  = xml.子要素のリスト( element, "pin" );
        for(int i=0; i<list.size(); i++){
           addcomponent( new pinlabel( list.get(i) ) );
        }
        setBounds(x0,y0,width,height);
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        int i;
        if( exist ){

          // pinlabelをLogoutする  
          Component cmp[] = getcomponents();
          for( i = 0;i<cmp.length;i++ ){
            if( cmp[i] instanceof pinlabel ) ( (pinlabel)cmp[i] ).Logout();
          }
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        node.suicide();
        node = null;
        element = null;
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
          int i;
          if( exist ){
          save();

          // pinlabelをLogoutする  
          Component cmp[] = getcomponents();
          for( i = 0;i<cmp.length;i++ ){
            if( cmp[i] instanceof pinlabel ) ( (pinlabel)cmp[i] ).Logout();
          }
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        ID_maker = parseInt( xml.属性値( element, "ID_maker" ) );
        namebutton.setText( xml.属性値( element, "objectname" ) );
        description = xml.属性値( element, "description" );
      }

      // 内容をセーブ
      public void save(){
        Component cmp[] = getcomponents();
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "ID_maker", "" + ID_maker );
        xml.属性値をセット( element, "objectname", namebutton.getText() );
        xml.属性値をセット( element, "description", description );

          for( int i = 0;i<cmp.length;i++ ){
            if( cmp[i] instanceof pinlabel ) ( (pinlabel)cmp[i] ).save();
          }
      }


      //コンポーネントを追加する
      public void addcomponent( Component cmp ){
        add(cmp);
        Dimension d=cmp.getPreferredSize();
        cmp.setBounds(0,0,d.width,d.height);
    }

      //格納されているコンポーネントを取得する
      public Component[] getcomponents(){ return( getComponents() ); }

      //与えられた名前のコンポーネントを返す
      public Component getcomponent( String name ){
        int i;
        Component cmp[] = getcomponents();
        for( i = 0; i<cmp.length; i++){
          if( cmp[i].getName().equals( name ) ) return( cmp[i] );
        }
        return( null );
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          mode = 2;
        }
        super.paintComponent(g);
        g.drawRect( 0, 0, width-1, height-1 );
      }

      //ボタンがクリックされたときの処理
      public void actionPerformed(ActionEvent e ){ treetool.changeNode(node); }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          txt = namebutton.getText();
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
//          setSize(1,1);
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
          width =  x1 + 5;
          if( width < 10 ) width = 10;
             height = y1 + 5;
             if( height < 10 ) height = 10;
           }
           else if( mode == 4){
             x0 += x1 - x2;
             if( x0 < 0) x0 = 0;
             y0 += y1 - y2;
             if(y0 < 0 ) y0 = 0;
           }
           x2 = x1;
           y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this, e );  }
      public void mouseMoved(MouseEvent e)   {   } 
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }

    }//~aobject

    // operation
    class operation extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
      boolean exist;
      Object element;
      int x0, y0, x1, y1, x2, y2;
      int width, height;
      JButton button;
      JTextArea description;
      String statename1, statename2;
      int inconnectx0, inconnecty0, outconnectx0, outconnecty0;
      innerpin inpin, outpin;
      int mode;
      Graphics grp;

      // 新規作成(ファイルも一緒に生成する)
      operation( Object elem, int xp, int yp, String inname, int inx, int iny, int inw, int inh, String outname, int outx, int outy, int outw, int outh ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        setOpaque(false);
        setLayout( null );
        button = new JButton(" ");
        button.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        button.setBackground(Color.blue);
        add( button );
        inpin = new innerpin( "in", inname, Color.cyan,inx, iny, inw, inh );
        add( inpin );
        outpin = new innerpin( "out", outname, Color.pink, outx, outy, outw, outh );
        add( outpin );
        description = new JTextArea("");
        description.setOpaque( false );
        add( description );
        x0 = xp;
        y0 = yp;
        width  = 130;
        height = 90;
        setBounds( x0, y0, width, height );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        button.addActionListener(this);
        save();
        mode = 1;
      }


      // Loginモードで生成(ファイルの情報をもとに生成)  
      operation( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        setOpaque(false);
        setLayout( null );
        button = new JButton(" ");
        button.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        button.setBackground(Color.blue);
        add( button );
        inpin = new innerpin( "in", "in()", Color.cyan,10, 10, 10, 10 );
        add( inpin );
        outpin = new innerpin( "out", "out()",Color.pink, 10, 10, 10, 10 );
        add( outpin );
        description = new JTextArea("");
        description.setOpaque( false );
        add( description );
        load();
        setBounds( x0, y0, width, height );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        button.addActionListener(this);
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        if( exist ){
          remove(button);
          remove(description);
          remove(inpin);
          remove(outpin);
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
        if( exist ){
          save();
          remove(button);
          remove(description);
          remove(inpin);
          remove(outpin);
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
      }

      //   ロード
      public void load(){
        statename1 = xml.属性値( element, "state1" );
        statename2 = xml.属性値( element, "state2" );
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        inconnectx0 = parseInt( xml.属性値( element, "inconnectx0" ) );
        inconnecty0 = parseInt( xml.属性値( element, "inconnecty0" ) );
        outconnectx0 = parseInt( xml.属性値( element, "outconnectx0" ) );
        outconnecty0 = parseInt( xml.属性値( element, "outconnecty0" ) );
        inpin.setText( xml.属性値( element, "inpintext" ) );
        inpin.x0 = parseInt( xml.属性値( element, "inpinx0" ) );
        inpin.y0 = parseInt( xml.属性値( element, "inpiny0" ) );
        inpin.width = parseInt( xml.属性値( element, "inpinwidth" ) );
        inpin.height = parseInt( xml.属性値( element, "inpinheight" ) );
        outpin.setText( xml.属性値( element, "outpintext" ) );
        outpin.x0 = parseInt( xml.属性値( element, "outpinx0" ) );
        outpin.y0 = parseInt( xml.属性値( element, "outpiny0" ) );
        outpin.width = parseInt( xml.属性値( element, "outpinwidth" ) );
        outpin.height = parseInt( xml.属性値( element, "outpinheight" ) );
        String des = xml.属性値( element, "description" );
        if( des.equals("") ){
          description.setText( xml.属性値( element, "codetext" ) );
        }
        else{
          String line = getFirstLine( des );
          if( line.startsWith(TransientConditionPrefix) ){
            description.setText(
              "/* " + des + " */\n"
            + " if( !( " + line.substring( TransientConditionPrefix.length()+1 ) + " ) ) return;\n"
            + xml.属性値( element, "codetext" ) 
            );
          }
          else{
            String code = "";
            do{
              code = code + "// " + line + "\n";
              des = getNextLines( des );
              if( line.startsWith(FollowIsCodePrefix) ){
                description.setText( code + des );
                setBounds( x0, y0, width, height );
                return;
              }
              if( des.equals("") ) break;
              line = getFirstLine( des );
            } while(true);
            description.setText( code + xml.属性値( element, "codetext" ) );
          }
        }
        setBounds( x0, y0, width, height );
      }

      //   セーブ
      public void save(){
        xml.属性値をセット( element, "state1", "_SINIT" );
        xml.属性値をセット( element, "state2", "_SINIT" );
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "inconnectx0", "0" );
        xml.属性値をセット( element, "inconnecty0", "40" );
        xml.属性値をセット( element, "outconnectx0", "0" );
        xml.属性値をセット( element, "outconnecty0", "40" );
        xml.属性値をセット( element, "inpintext", inpin.getText() );
        xml.属性値をセット( element, "inpinx0", "" + inpin.x0 );
        xml.属性値をセット( element, "inpiny0", "" + inpin.y0 );
        xml.属性値をセット( element, "inpinwidth", "" + inpin.width );
        xml.属性値をセット( element, "inpinheight", "" + inpin.height );
        xml.属性値をセット( element, "outpintext", outpin.getText() );
        xml.属性値をセット( element, "outpinx0", "" + outpin.x0 );
        xml.属性値をセット( element, "outpiny0", "" + outpin.y0 );
        xml.属性値をセット( element, "outpinwidth", "" + outpin.width );
        xml.属性値をセット( element, "outpinheight", "" + outpin.height );
        xml.属性値をセット( element, "inpinlinkcount", "" + inpin.linkcount );
        xml.属性値をセット( element, "description", "" );
        xml.属性値をセット( element, "codetext", description.getText() );
      }

      //自分がクリックされた信号をObjectEditorに発行する   
      public void operationClick( MouseEvent e ){
        componentClicked( this, e );
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          button.setBounds( 0, 0, width, 10 );
          description.setBounds( 10, 10, width-20, height-10 );
          mode = 2;
        }
        super.paintComponent(g);
        g.setColor(Color.gray);
        g.drawRect( 0, 0, width-1, height-1 );
        g.drawRect( 0, 10, width-10, height-10 );
      }

      // action event (エディタを起動してコードを編集する)
      public void actionPerformed(ActionEvent e ){
        if( JavaEditCommand.equals("") ){ description.setText( texteditor.start( description.getText() ) ); }
        else{
          if( FTmpTextFile.isFile() || FTmpTextFile.isDirectory() ) FTmpTextFile.Xdelete();
          FTmpTextFile.Xappend( description.getText() );
          execute( JavaEditCommand+" "+TmpTextFile, true );
          try{
            BufferedReader din = new BufferedReader( new FileReader( FTmpTextFile ) );
            String line;
            String code = "";
            while((line=din.readLine())!=null){
              code = code + line + "\n";
            }
            din.close();
            description.setText( code );
          } catch( IOException ie ){ reportError("ソースコードを編集できません．\n"); }
        }
        filewindow.setVisible( filewindow.isVisible() );
      }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
//          setSize(1,1);
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
            width =  x1 + 5;
            if( width < 10 ) width = 10;
            height = y1 + 5;
            if( height < 10 ) height = 10;
        }
        else if( mode == 4){
          x0 += x1 - x2;
          if( x0 < 0) x0 = 0;
          y0 += y1 - y2;
          if(y0 < 0 ) y0 = 0;
        }
        x2 = x1;
        y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        button.setBounds( 0, 0, width, 10 );
        description.setBounds( 10, 10, width-20, height-10 );
        gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { operationClick( e ); }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }


      //   operationの内部オブジェクト
      class innerpin extends JTextField implements MouseMotionListener, MouseListener {
        int x0, y0, x1, y1, x2, y2;
        int width, height;
        int mode;
        String txt;
        Graphics grp;
        int linkcount;

        innerpin( String name, String text, Color color, int xx, int yy, int ww, int hh ){
          x0 = xx;
          y0 = yy;
          linkcount = 0;
          setName( name );    
          setText( text );
          setBackground( color );
          width = ww;
          height = hh;
          addMouseMotionListener(this);            //マウスリスナーを設定
          addMouseListener(this);                  //マウスリスナーを設定
          setBounds( x0, y0, width, height );
          setOpaque(true);
          setVisible( false );
          mode = 1;
        }

        public void inclinkcount(){
          if( ++linkcount > 0 ) setVisible(true);
          getParent().repaint();
        }

        public void declinkcount(){
          if( --linkcount <= 0 ){
            setVisible( false );
            linkcount = 0;
            setText( getName() + "()" );
          }
        }

        //コンポーネント描画
        public void paintComponent(Graphics g){
          if( mode == 1 ){
            setBounds( x0, y0, width, height );
            mode = 2;
          }
          super.paintComponent(g);
        }

        //mouse event
        public void mousePressed(MouseEvent e) {
          if( mode == 2 ){
            grp = getParent().getGraphics();
            x2 = e.getX();
            y2 = e.getY();
            if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
            txt = getText();
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
          }
        }

        public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          x1 = e.getX();
          y1 = e.getY();
          if( mode == 3 ){
            width =  x1 + 5;
            if( width < 10 ) width = 10;
            height = y1 + 5;
            if( height < 10 ) height = 10;
          }
          else if( mode == 4){
            x0 += x1 - x2;
            if( x0 < 0) x0 = 0;
            y0 += y1 - y2;
            if(y0 < 0 ) y0 = 0;
          }
          x2 = x1;
          y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
        }

        public void mouseReleased(MouseEvent e){
          setBounds( x0, y0, width, height );
          getParent().repaint();
          mode = 2;
        }    

        public void mouseClicked(MouseEvent e) { operationClick( e ); }
        public void mouseMoved(MouseEvent e)   {   }
        public void mouseEntered(MouseEvent e) {   }
        public void mouseExited(MouseEvent e)  {   }
 
      }//~innerpin


    }//~operation


    // pinlabel
    class pinlabel extends JLabel implements MouseMotionListener, MouseListener {
      boolean exist;
      Object element;
      int x1, y1, x2, y2;
      int px, py;
      Graphics grp;
      String txt;
      int wid,hei;
      int mode;
      int fsize;

      // 新規作成(ファイルも一緒に生成する)
      pinlabel( Object elem, String method, int x, int y ){
        exist = true;
        element = elem;
        setName( xml.要素のID( xml.親要素( element ) ) + "("+ xml.要素のID( element ) + ")" );
        xml.属性値をセット( element, "x0", "100" );
        xml.属性値をセット( element, "y0", "100" );
        xml.属性値をセット( element, "width", "60" );
        xml.属性値をセット( element, "height", "30" );
        setText( method );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        setLocation( px= x, py = y );
        mode = 1;
      }

      // Loginモードで生成(ファイルの情報をもとに生成)
      pinlabel( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( xml.親要素( element ) ) + "("+ xml.要素のID( element ) + ")" );
        load();
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        setLocation( px, py );
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        if( exist ){
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
        if( exist ){
          save();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        setText( xml.属性値( element, "text" ) );
        px = parseInt( xml.属性値( element, "px" ) );
        py = parseInt( xml.属性値( element, "py" ) );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "text", getText() );
        xml.属性値をセット( element, "px", "" + px );
        xml.属性値をセット( element, "py", "" + py );
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){
        if( mode == 1 ){
          setLocation( px, py );
          mode = 2;
        } 
        super.paintComponent(g);
     }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode != 3 ){
          mode = 3;
          txt = getText();
          grp = getParent().getGraphics();
          wid = getWidth();
          hei = getHeight();
          x2 = e.getX();
          y2 = e.getY();
          fsize = grp.getFont().getSize();
          grp.setXORMode(Color.white);
          grp.drawString(txt,px,py+fsize);
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawString(txt,px,py+fsize);
        x1 = e.getX();
        y1 = e.getY();
        px += x1 - x2;
        if( px < 0 ) px = 0;
        py += y1 - y2;
        if( py < 0 ) py = 0;
        x2 = x1;
        y2 = y1;
          grp.drawString(txt,px,py+fsize);
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setLocation( px, py );
        getParent().repaint();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this, e );  }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }
    }//~pinlabel

    //pin
    class pin extends JTextField implements MouseMotionListener, MouseListener {
      boolean exist;
      Object element;
      int x0, y0, x1, y1, x2, y2;
      int width, height;
      int px, py;
      String txt;
      Graphics grp;
      int mode;

      // 新規作成(ファイルも一緒に生成する)
      pin( Object elem, String method, int x, int y ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        x0 = x;
        y0 = y;
        width = 60;
        height = 30;
        px = 10;
        py = 40;
        setText( method );
        setBounds( x0, y0, width, height );
        save();
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        mode = 1;
      }


      // Loginモードで生成(ファイルの情報をもとに生成)
      pin( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        load();
        setBounds( x0, y0, width, height );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        if( exist ){
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
        if( exist ){
          save();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        setText( xml.属性値( element, "text" ) );
        px = parseInt( xml.属性値( element, "px" ) );
        py = parseInt( xml.属性値( element, "py" ) );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "text", getText() );
        xml.属性値をセット( element, "px", "" + px );
        xml.属性値をセット( element, "py", "" + py );
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){    
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          mode = 2;
        }
        super.paintComponent(g);
      }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          txt = getText();
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
            width =  x1 + 5;
            if( width < 10 ) width = 10;
            height = y1 + 5;
            if( height < 10 ) height = 10;
        }
        else if( mode == 4){
          x0 += x1 - x2;
          if( x0 < 0) x0 = 0;
          y0 += y1 - y2;
          if(y0 < 0 ) y0 = 0;
        }
        x2 = x1;
        y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this, e );   }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }

    }//~pin


    // codeclip
    class codeclip extends JPanel implements MouseMotionListener, MouseListener, ActionListener{
      boolean exist;
      Object element;
      int x0, y0, x1, y1, x2, y2;
      int width, height;
      int mode;
      Graphics grp;

      JButton openWindow;
      JTextArea codetext;
      
      // 新規作成(ファイルも一緒に生成する)
      codeclip( Object elem, String code, int x, int y ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        x0 = x;
        y0 = y;
        width = 100;
        height = 20;
        setLayout( null );
        openWindow = new JButton();
        openWindow.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        add(openWindow);
        openWindow.setBackground( Color.blue );
        openWindow.addActionListener(this);
        codetext = new JTextArea(code);
        codetext.setBorder( new LineBorder(Color.gray) );
        add(codetext);
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        save();
        setBounds( x0, y0, width, height );
        setBackground( Color.white );
        openWindow.setBounds( 0, 0, 10, height );
        mode = 1;
      }


      // Loginモードで生成(ファイルの情報をもとに生成)
      codeclip( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        setLayout( null );
        openWindow = new JButton( );
        openWindow.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        add(openWindow);
        openWindow.setBackground( Color.blue );
        openWindow.addActionListener(this);
        codetext = new JTextArea("  ");
        codetext.setBorder( new LineBorder(Color.gray) );
        add(codetext);
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        load();
        setBounds( x0, y0, width, height );
        setBackground( Color.white );
        openWindow.setBounds( 0, 0, 10, height );
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        if( exist ){
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
        if( exist ){
          save();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        codetext.setText( xml.属性値( element, "codetext" ) );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "codetext", codetext.getText() );
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){    
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          openWindow.setBounds( 0, 0, 10, height );
          codetext.setBounds( 10, 0, width-20, height );
          mode = 2;
        }
        super.paintComponent(g);
        g.drawRect( 0, 0, width - 1, height - 1 );
      }

      // action event (エディタを起動してコードを編集する)
      public void actionPerformed(ActionEvent e ){
        if( JavaEditCommand.equals("") ){ codetext.setText( texteditor.start( codetext.getText() ) ); }
        else{
          if( FTmpTextFile.isFile() || FTmpTextFile.isDirectory() ) FTmpTextFile.Xdelete();
          FTmpTextFile.Xappend( codetext.getText() );
          execute( JavaEditCommand+" "+TmpTextFile, true );
          try{
            BufferedReader din = new BufferedReader( new FileReader( FTmpTextFile ) );
            String line;
            String code = "";
            while((line=din.readLine())!=null){
              code = code + line + "\n";
            }
            din.close();
            codetext.setText(code);
          } catch( IOException ie ){ reportError("ソースコードを編集できません．\n"); }
        }
        filewindow.setVisible( filewindow.isVisible() );
      }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
//          setSize(1,1);
           grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
       }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
            width =  x1 + 5;
            if( width < 30 ) width = 30;
            height = y1 + 5;
            if( height < 10 ) height = 10;
        }
        else if( mode == 4){
          x0 += x1 - x2;
          if( x0 < 0) x0 = 0;
          y0 += y1 - y2;
          if(y0 < 0 ) y0 = 0;
        }
        x2 = x1;
        y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        openWindow.setBounds( 0, 0, 10, height );
        codetext.setBounds( 10, 0, width-20, height);
        mode = 2;
        gui.resize();
        repaint();
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this, e ); }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }
    
    }//~codeclip


    // relation
    class relation extends JPanel implements ComponentListener, MouseListener{
      boolean exist;
      Object element;
      Component pin1, pin2;
      String pin1name, pin2name;
      int x0, y0;
      int width, height;
      int      startx, starty, endx, endy;

      // 新規作成(ファイルも一緒に生成する)
      relation( Object elem, Component p1, Component p2 ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        pin1 = p1;
        pin2 = p2;
        pin1.addComponentListener( this );
        if( pin1 instanceof pinlabel) pin1.getParent().addComponentListener( this );
        else if( pin1 instanceof operation ){
          ((operation)pin1).outpin.addComponentListener(this);
          ((operation)pin1).outpin.inclinkcount();
        }
        pin2.addComponentListener( this );
        if( pin2 instanceof pinlabel) pin2.getParent().addComponentListener( this );
        else if( pin2 instanceof operation ){
          ((operation)pin2).inpin.addComponentListener(this);
          ((operation)pin2).inpin.inclinkcount();
        }
        save();
        addMouseListener(this);                  //マウスリスナーを設定
        setOpaque( false );
        redrawLine();
      }

      // Loginモードで生成  (ファイルの情報をもとに生成／注意：接続するコンポーネントが存在していること)
      relation( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        load();
        pin1.addComponentListener( this );
        if( pin1 instanceof pinlabel) pin1.getParent().addComponentListener( this );
        else if( pin1 instanceof operation ){
          ((operation)pin1).outpin.addComponentListener(this);
          ((operation)pin1).outpin.inclinkcount();
        }
        pin2.addComponentListener( this );
        if( pin2 instanceof pinlabel) pin2.getParent().addComponentListener( this );
        else if( pin2 instanceof operation ){
          ((operation)pin2).inpin.addComponentListener(this);
          ((operation)pin2).inpin.inclinkcount();
        }
        addMouseListener(this);                  //マウスリスナーを設定
        setOpaque( false );
        redrawLine();
      }

      //削除(ファイルごと削除)
      public void suicide(){
        if( exist ){
          xml.要素を削除( element );
          exist = false;
          pin1.removeComponentListener( this );
          if( pin1 instanceof pinlabel && pin1.getParent() != null ) pin1.getParent().removeComponentListener( this );
          else if( pin1 instanceof operation ){
            ((operation)pin1).outpin.removeComponentListener(this);
            ((operation)pin1).outpin.declinkcount();
          }
          pin2.removeComponentListener( this );
          if( pin2 instanceof pinlabel && pin2.getParent() != null ) pin2.getParent().removeComponentListener( this );
          else if( pin2 instanceof operation ){
            ((operation)pin2).inpin.removeComponentListener(this);
            ((operation)pin2).inpin.declinkcount();
          }
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
        if( exist ){
          save();
          exist = false;
          pin1.removeComponentListener( this );
          if( pin1 instanceof pinlabel) pin1.getParent().removeComponentListener( this );
          pin2.removeComponentListener( this );
          if( pin2 instanceof pinlabel) pin2.getParent().removeComponentListener( this );
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        pin1name = xml.属性値( element, "pin1name" );
        pin2name = xml.属性値( element, "pin2name" );
        pin1 = gui.getcomponent( pin1name );
        pin2 = gui.getcomponent( pin2name );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "pin1name", pin1.getName() );
        xml.属性値をセット( element, "pin2name", pin2.getName() );
      }

      // 線を引き直す
      public void redrawLine() {
        boolean exist1,exist2;
        int      x1, y1, width1, height1;
        int      x2, y2, width2, height2;
        Point    p1,p2;

        if( pin1 instanceof pin ) exist1 = ( (pin)pin1 ).exist;
        else if( pin1 instanceof pinlabel )  exist1 = ( (pinlabel) pin1 ).exist;
        else if( pin1 instanceof operation ) exist1 = ( (operation)pin1 ).exist;
        else  exist1 = false;
        if( pin2 instanceof pin ) exist2 = ( (pin)pin2 ).exist;
        else if( pin2 instanceof pinlabel )  exist2 = ( (pinlabel) pin2 ).exist;
        else if( pin2 instanceof operation ) exist2 = ( (operation)pin2 ).exist;
        else  exist2 = false;
        if( exist1 == false || exist2 == false ){
          suicide();
          return;
        }
        if( pin1 instanceof pinlabel ){
          x1   = pin1.getLocation().x + pin1.getParent().getLocation().x;
          y1   = pin1.getLocation().y + pin1.getParent().getLocation().y;
          width1  = pin1.getWidth();
          height1 = pin1.getHeight();
        }
        else if( pin1 instanceof operation ){
           operation op = (operation) pin1;
           x1 = op.getLocation().x + op.outpin.getLocation().x;
           y1 = op.getLocation().y + op.outpin.getLocation().y;
           width1  = op.outpin.getWidth();
           height1 = op.outpin.getHeight();
        }
        else {
          x1 = pin1.getLocation().x;
          y1 = pin1.getLocation().y;
          width1  = pin1.getWidth();
          height1 = pin1.getHeight();
        }

        if( pin2 instanceof pinlabel ){
          x2   = pin2.getLocation().x + pin2.getParent().getLocation().x;
          y2   = pin2.getLocation().y + pin2.getParent().getLocation().y;
          width2  = pin2.getWidth();
          height2 = pin2.getHeight();
        }
        else if( pin2 instanceof operation ){
           operation op = (operation) pin2;
           x2 = op.getLocation().x + op.inpin.getLocation().x;
           y2 = op.getLocation().y + op.inpin.getLocation().y;
           width2  = op.inpin.getWidth();
           height2 = op.inpin.getHeight();
        }
        else {
          x2 = pin2.getLocation().x;
          y2 = pin2.getLocation().y;
          width2  = pin2.getWidth();
          height2 = pin2.getHeight();
        }
        p1 = getBorderPoint( x1, y1, width1, height1, x2, y2, width2, height2 );
        p2 = getBorderPoint( x2, y2, width2, height2, x1, y1, width1, height1 );
        startx = p1.x;
        starty = p1.y;
        endx   = p2.x;
        endy   = p2.y;
        if( startx < endx ){
          x0 = startx - 5;
          width = endx - startx + 11;
          startx = 5;
          endx = width - 6;
        }
        else{
          x0 = endx - 5;
          width = startx - endx + 11;
          startx = width - 6;
          endx = 5;
        }
        if( starty < endy ){
          y0 = starty - 5;
          height = endy - starty + 11;
          starty = 5;
          endy = height - 6;
        }
        else{
          y0 = endy - 5;
          height = starty - endy + 11;
          starty = height - 6;
          endy = 5;
        }
        setBounds( x0, y0, width, height );
        repaint();
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){
        int   rx, ry;
        double len, co, si;
        rx = startx - endx;
        ry = starty - endy;
        len = java.lang.Math.sqrt( (double)rx * rx + ry * ry );
        co = 9.659D / len;
        si = 2.588D / len;
        setBounds( x0, y0, width, height );
        super.paintComponent(g);
        g.drawLine( startx, starty, endx, endy );
        g.drawLine( (int)( co * rx + si * ry ) + endx, (int)(-si * rx + co * ry ) + endy, endx, endy );
        g.drawLine( (int)( co * rx - si * ry ) + endx, (int)( si * rx + co * ry ) + endy, endx, endy );
      }

      // Component event
      public void componentMoved( ComponentEvent e )  {  redrawLine(); }
      public void componentResized( ComponentEvent e ){  redrawLine(); }
      public void componentShown( ComponentEvent e )  {  }
      public void componentHidden( ComponentEvent e ) {  }

      // Mouse event
      public void mouseClicked(MouseEvent e) { componentClicked( this, e ); }
      public void mousePressed(MouseEvent e) {   }
      public void mouseReleased(MouseEvent e){   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }

    }//~relation

//KJコンポーネント

    class KJgroup extends JPanel implements MouseMotionListener, MouseListener, ActionListener {

      boolean exist;
      Object element;
      int x0, y0,x1, y1, x2, y2;
      int width, height;
      int mode;
      Graphics grp;
      String txt;

      JTextField comment;

      // 新規作成(ファイルも一緒に生成する)
      KJgroup( Object elem, String cmt, int x, int y ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        x0 = x;
        y0 = y;
        width  = 107;
        height = 50;
        comment = new JTextField( cmt );
        setBounds(x0,y0,width,height);
        setForeground(new Color( 0, 160, 0 ));
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        comment.addActionListener(this);
        add(comment);
        Dimension d=comment.getPreferredSize();
        comment.setBounds(1,1,d.width,d.height);
        save();
        mode = 1;
      }

      // Loginモードで生成(ファイルの情報をもとに生成)  
      KJgroup( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        comment = new JTextField( " " );
        load();
        setBounds( x0, y0, width, height );
        setForeground(new Color(0, 200, 0 ));
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        comment.addActionListener(this);
        add(comment);
        Dimension d=comment.getPreferredSize();
        comment.setBounds(1,1,d.width,d.height);
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        int i;
        if( exist ){
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // Logout(内容をxmlにセーブして消去)
      public void Logout(){
        int i;
        if( exist ){
          save();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        comment.setText( xml.属性値( element, "comment" ) );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "comment", comment.getText() );
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          mode = 2;
        }
        super.paintComponent(g);
        g.drawRect( 0, 0, width-1, height-1 );
//        g.drawRect( 0, 0, comment.getWidth()+1, comment.getHeight()+1 );
      }

      // 
      public void actionPerformed(ActionEvent e ){
        comment.setSize( comment.getPreferredSize() );
        repaint();
      }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
          width =  x1 + 5;
          if( width < 10 ) width = 10;
             height = y1 + 5;
             if( height < 10 ) height = 10;
           }
           else if( mode == 4){
             x0 += x1 - x2;
             if( x0 < 0) x0 = 0;
             y0 += y1 - y2;
             if(y0 < 0 ) y0 = 0;
           }
           x2 = x1;
           y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        comment.setSize( comment.getPreferredSize() ); 
        gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this, e );  }
      public void mouseMoved(MouseEvent e)   {   } 
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }

    }//~KJgroup

    //画像アイコン
    class ImgIcon extends JPanel implements MouseMotionListener, MouseListener {

      boolean exist;
      Object element;
      int x0, y0,x1, y1, x2, y2;
      int width, height;
      int mode, state0;
      Graphics grp;
      BufferedImage image;

      // 新規作成(ファイルも一緒に生成する)
      ImgIcon( Object elem, int x, int y ){
        exist = true;
        element = elem;
        image = null;
        setName( xml.要素のID( element ) );
        x0 = x;
        y0 = y;
        width  = 107;
        height = 50;
        setBounds(x0,y0,width,height);
        setForeground(new Color( 255, 0, 0 ));
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        save();
        mode = 1;
        state0 = 0;
      }

      // Loginモードで生成(ファイルの情報をもとに生成)  
      ImgIcon( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        load();
        setBounds( x0, y0, width, height );
        setForeground(new Color(255, 0, 0 ));
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        mode = 1;
        state0 = 0;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        int i;
        if( exist ){
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
        image = null;
      }

      // Logout(内容をxmlにセーブして消去)
      public void Logout(){
        int i;
        if( exist ){
          save();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        image = text2image( xml.属性値( element, "image" ) );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "image", image2text(image) );
      }

      // 画像からテキストを生成
      public String image2text( BufferedImage img ){
        byte[] sbyte = null;
        int i =0,j = 0;
        if( img == null ) return "";
        try{
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          BufferedOutputStream os = new BufferedOutputStream( bos );
          img.flush();
          ImageIO.write( img, "jpg", os );
          sbyte = bos.toByteArray();
        }catch( Exception e ){e.printStackTrace();}
        int n = sbyte.length;
        if( n == 0 ) return "";
        byte[] tbyte = new byte[ n * 2 + n / 32 ];
        for( i = j = 0; i < n; i++ ){
          tbyte[j++] = (byte)(((int)sbyte[i] & 0x0f) + (int)'a' ); 
          tbyte[j++] = (byte)((((int)sbyte[i] >> 4) & 0x0f) + (int)'a' ); 
          if( (i&31)==31 ) tbyte[j++] = (byte)'\n';
        }
        return new String(tbyte);
      }
  
      // テキストから画像を生成
      public BufferedImage text2image( String txt ){
         BufferedImage img = null;
         int i =0,j =0;
         if( txt == null || txt.equals("") ) return null;
		 byte[] sbyte = txt.getBytes();
         int nn = sbyte.length;
         if( nn == 0 ) return null;
         int n = nn * 32 / 65;
		 byte[] tbyte = new byte[ n ];
		 for( i = j = 0; i < n; i++ ){
           tbyte[i] = (byte)( ((int)sbyte[j++]-(int)'a') | (((int)sbyte[j++]-(int)'a')<<4) );
           if( sbyte[j] == (byte)'\n' ) j++;
         }
		 try{ 
           img = ImageIO.read( new ByteArrayInputStream( tbyte ) );
        }catch( Exception e ){e.printStackTrace();}
        return img;
      }
  
      //コンポーネント描画
      public void paintComponent(Graphics g){
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          mode = 2;
        }
        super.paintComponent(g);
        g.drawRect( 0, 0, width-1, height-1 );
        if( image != null )  g.drawImage(image, 0, 0, width, height, null );
      }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
          state0 = 0;
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
          width =  x1 + 5;
          if( width < 10 ) width = 10;
             height = y1 + 5;
             if( height < 10 ) height = 10;
           }
           else if( mode == 4){
             x0 += x1 - x2;
             if( x0 < 0) x0 = 0;
             y0 += y1 - y2;
             if(y0 < 0 ) y0 = 0;
           }
           x2 = x1;
           y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
          state0 = 1;
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        if( mode ==3 && state0 == 0 ){
          try{
            if( image != null ) ImageIO.write(image, "jpg", FTmpImageFile);
            else{
              FileInputStream fileIn = new FileInputStream("dmy.jpg");
              FileOutputStream fileOut = new FileOutputStream(TmpImageFile);
              byte[] buf = new byte[256];
              int len;
              while((len = fileIn.read(buf)) != -1){
                fileOut.write(buf);    
              }
              fileOut.flush();
              fileOut.close();
              fileIn.close();
            }
            execute( ImageEditCommand+" "+TmpImageFile, true );
            image = ImageIO.read(FTmpImageFile);
	      } catch(Exception ee){ ee.printStackTrace();}
        }
        gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this, e );  }
      public void mouseMoved(MouseEvent e)   {   } 
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }


    }//~ImgIcon


  }//~ObjectEditor


  // 状態遷移図エディタ 
  class StateEditor{
    GUI gui;
    Anode node;
    Object element;
    int x0, y0;
    int width, height;
    int ID_maker;
    String name;
    String description;
      
    XFile loadfile;
    Component comp1,comp2;
    String mode;
      
    // StateEditorを初期化
    StateEditor(){
      node = null;
      gui = new GUI();
    }
     
    // 新しいpathに内容を変更する
    public void Login( Anode nod ){

      //内容をロードする
      node = nod;
      element = node.element;
      x0 = parseInt( xml.属性値( element, "x0" ) );
      y0 = parseInt( xml.属性値( element, "y0" ) );
      width = parseInt( xml.属性値( element, "width" ) );
      height = parseInt( xml.属性値( element, "height" ) );
      ID_maker = parseInt( xml.属性値( element, "ID_maker" ) );
      gui.setobjectname( xml.属性値( element, "objectname" ) );
      gui.setdescription( xml.属性値( element, "description" ) );
       
      //Loginモードでコンポーネントを生成する
      Vector v;
      int i;
       
      // state
      v = xml.子要素のリスト( element, "state" );
      for( i = 0; i < v.size(); i++ ) gui.addcomponent( new state( v.get(i) ) );

      // pin
      v = xml.子要素のリスト( element, "pin" );
      for( i = 0; i < v.size(); i++ ) gui.addcomponent( new pin( v.get(i) ) );

      // codeclip
      v = xml.子要素のリスト( element, "codeclip" );
      for( i = 0; i < v.size(); i++ ) gui.addcomponent( new codeclip( v.get(i) ) );

      // ImgIcon
      v = xml.子要素のリスト( element, "ImgIcon" );
      for( i = 0; i < v.size(); i++ ) gui.addcomponent( new ImgIcon( v.get(i) ) );

      // operation
      v = xml.子要素のリスト( element, "operation" );
      for( i = 0; i < v.size(); i++ ){
        Object op = v.get(i);
        String state1 = xml.属性値( op, "state1" );
        String state2 = xml.属性値( op, "state2" );
        if( gui.getcomponent( state1 ) != null && gui.getcomponent( state2 )!= null ){
          gui.addcomponent( new operation( op ) );
        }
        else xml.要素を削除( op );
      }

      // action
      v = xml.子要素のリスト( element, "action" );
      for( i = 0; i < v.size(); i++ ){
        Object a = v.get(i);
        String comp1 = xml.属性値( a, "comp1name" );
        String comp2 = xml.属性値( a, "comp2name" );
        if( ( gui.getcomponent( comp1 ) != null ) && ( gui.getcomponent( comp2 ) != null ) ){
          gui.addcomponent( new action( a ) );
        }
        else xml.要素を削除( a );
      }

      gui.setBounds( MainWinx0, MainWiny0, MainWinWidth, MainWinHeight );
      gui.contents.setDividerLocation( DividerLocation2 );
      gui.display.setDividerLocation( DividerLocation1 );
      gui.disptoolbox.setSelected( ToolBarVisible);
      gui.toolBar.setVisible( ToolBarVisible );
      gui.setVisible();
      mode = "NOP";
gui.buttonreset();
    }

    //内容をセーブして内容を消去する
    public void Logout(){
      int i;

      MainWinx0 = gui.getLocation().x;
      MainWiny0 = gui.getLocation().y;
      MainWinWidth = gui.getWidth();
      MainWinHeight =  gui.getHeight();
      DividerLocation1 = gui.display.getDividerLocation();
      DividerLocation2 = gui.contents.getDividerLocation();
      node.setUserObject(gui.getobjectname());
      treetool.repaint();

      xml.属性値をセット( element, "x0", "" + x0 );
      xml.属性値をセット( element, "y0", "" + y0 );
      xml.属性値をセット( element, "width", "" + width );
      xml.属性値をセット( element, "height", "" + height );
      xml.属性値をセット( element, "ID_maker", "" + ID_maker );
      xml.属性値をセット( element, "objectname", gui.getobjectname() );   gui.setobjectname("");
      xml.属性値をセット( element, "description", gui.getdescription() ); gui.setdescription("");
      
      // コンポーネントをすべてLogoutする
      Component[] comp = gui.getcomponents();

      // action
      for( i=0;i<comp.length;i++){
        if( comp[i] instanceof action  ) ( (action)comp[i] ).save();
      }

      // operation
      for( i=0;i<comp.length;i++){
        if( comp[i] instanceof operation  ) ( (operation)comp[i] ).save();
      }

      // state, pin, codeclip, ImgIcon
      for( i=0;i<comp.length;i++){
        if( comp[i] instanceof state )     ( (state)comp[i] ).save();
        else if( comp[i] instanceof pin  )      ( (pin)comp[i] ).save();
        else if( comp[i] instanceof codeclip  ) ( (codeclip)comp[i] ).save();
        else if( comp[i] instanceof ImgIcon  ) ( (ImgIcon)comp[i] ).save();
      }
      gui.clear();
    }

    //コンポーネントをすべて削除する
    public void removeallcomponents(){
      Component[] comp = gui.getcomponents();
      for( int i = 0; i < comp.length; i++ ) {
        if( (!(comp[i] instanceof state )) || ( ((state)comp[i]).isinitstate == 0 ) ) {
          removecomponent( comp[i] );
        }
      }
    }
   
    //コンポーネントを削除する
    public void removecomponent( Component comp ){
      if( comp instanceof state )     { ((state )comp).suicide(); }
      if( comp instanceof operation ) { ((operation)comp).suicide(); }
      if( comp instanceof pin )       { ((pin)comp).suicide(); }
      if( comp instanceof action)     { ((action)comp).suicide(); }
      if( comp instanceof codeclip )  { ((codeclip)comp).suicide(); }
      if( comp instanceof ImgIcon )  { ((ImgIcon)comp).suicide(); }
    }

    // xp, yp で指定された位置にオブジェクトをロードする
    public Object LoadObject( XFile loadfile, int xp, int yp ){
      Object obj = null;
      Anode cnode = node;

      if( !loadfile.isxml() ) return( null );
      if( loadfile.isstate() ){
        obj = xml.新しい要素( element, loadfile, "_S"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
      }

      else if( loadfile.isoperation() ){
        obj = xml.新しい要素( element, loadfile, "_O"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
        xml.属性値をセット( obj, "state1", "_SINIT" );
        xml.属性値をセット( obj, "state2", "_SINIT" );
        xml.属性値をセット( obj, "inconnectx0", "0" );
        xml.属性値をセット( obj, "inconnecty0", "30" );
        xml.属性値をセット( obj, "outconnectx0", "0" );
        xml.属性値をセット( obj, "outconnecty0", "30" );
      }
      
      else if( loadfile.ispin() ){
        obj = xml.新しい要素( element, loadfile, "_P"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
      }

      else if( loadfile.iscodeclip() ){
        obj = xml.新しい要素( element, loadfile, "_C"+ID_maker++);
        xml.属性値をセット( obj, "x0", "" + xp );
        xml.属性値をセット( obj, "y0", "" + yp );
      }

      Logout();
      Login( cnode );
      return( obj );
    }

    // クリップボードからxp, yp で指定された位置にオブジェクトを貼り付ける
    public void PasteObject( int xp, int yp ){
      Anode cnode = node;
 
      if( xml.要素の名前( clipboad ).equals("operation") ){
        Object op = xml.新しい要素( element, clipboad, "_O"+ID_maker++);
        xml.属性値をセット( op, "x0", "" + xp );
        xml.属性値をセット( op, "y0", "" + yp );
        xml.属性値をセット( op, "state1", "_SINIT" );
        xml.属性値をセット( op, "state2", "_SINIT" );
        xml.属性値をセット( op, "inconnectx0", "0" );
        xml.属性値をセット( op, "inconnecty0", "30" );
        xml.属性値をセット( op, "outconnectx0", "0" );
        xml.属性値をセット( op, "outconnecty0", "30" );
      }
      
      else if( xml.要素の名前( clipboad ).equals("state") ){
        Object st = xml.新しい要素( element, clipboad, "_S"+ID_maker++);
        xml.属性値をセット( st, "x0", "" + xp );
        xml.属性値をセット( st, "y0", "" + yp );
      }

      else if( xml.要素の名前( clipboad ).equals("pin") ){
        Object pi = xml.新しい要素( element, clipboad, "_P"+ID_maker++);
        xml.属性値をセット( pi, "x0", "" + xp );
        xml.属性値をセット( pi, "y0", "" + yp );
      }

      else if( xml.要素の名前( clipboad ).equals("codeclip") ){
        Object cod = xml.新しい要素( element, clipboad, "_C"+ID_maker++);
        xml.属性値をセット( cod, "x0", "" + xp );
        xml.属性値をセット( cod, "y0", "" + yp );
      }

      else if( xml.要素の名前( clipboad ).equals("ImgIcon") ){
        Object ic = xml.新しい要素( element, clipboad, "_I"+ID_maker++);
        xml.属性値をセット( ic, "x0", "" + xp );
        xml.属性値をセット( ic, "y0", "" + yp );
      }


      Logout();
      Login( cnode );
    }

     //エディタの内容をaobjectとしてセーブする
    public void save( XFile f ){
      Anode cnode = node;
      Logout();
      filewindow.save( element, f );
      Login( cnode );
      filewindow.chooser.rescanCurrentDirectory();
   }

   // 接続の確認をする
    public void checkconnect(){
      int i;
      Component cmp[] = gui.getcomponents();
      for( i = 0; i < cmp.length; i++ ){
         if( cmp[i] instanceof action )    ( (action)cmp[i] ).redrawLine();
         if( cmp[i] instanceof operation ) ( (operation)cmp[i] ).check();
      }
    }

    //ファイルウィンドウからロードコマンドを受け取る
    public void LoadComponent( XFile xf ){
      mode = "LOAD_COMP";
      loadfile = xf;
    }

    //ファイルウィンドウからセーブコマンドを受け取る
    public void SaveComponent(){
      mode = "SAVE_COMP";
    }

    //コマンドを受け取る
    public void CommandReceived( String command ){
      if(command.equals("CRE_STA"))    mode = command;
      if(command.equals("CRE_OP"))     mode = command;
      if(command.equals("CRE_PIN"))    mode = command;
      if(command.equals("CRE_ACT"))    mode = command;
      if(command.equals("CRE_CODE"))   mode = command;
      if(command.equals("CRE_ICO"))    mode = command;
      if(command.equals("DEL_OBJ"))    mode = command;
      if(command.equals("CUT"))        mode = command;
      if(command.equals("COPY"))       mode = command;
      if(command.equals("PASTE"))      mode = command;

      if(command.equals("UPALL"))      treetool.upALL();
      if(command.equals("DOWNALL"))    treetool.downALL();
      if(command.equals("LEFTALL"))    treetool.leftALL();
      if(command.equals("RIGHTALL"))   treetool.rightALL();

      if(command.equals("FILEWIN")){
       filewindow.setVisible(true);
gui.buttonreset();
      }

      if(command.equals("COMPILE")){
        if(compile_ready){
          Anode cnode = node;
          Logout();
          messagewindow.clearText();
          messagewindow.setVisible(true);
          new Thread(new Runnable() {
            @Override
            public void run() {
              compile_ready = false;
              compile_project(treetool.top.element);
              compile_ready = true;
	        }
	      }).start();
          Login(cnode);
          System.gc();
gui.buttonreset();
        }
        else{
          dialog1.age("まだコンパイル中です");
	    }
      }
      
      if(command.equals("RUN")){
        messagewindow.execcommand("実行します\n", "\n実行できません\n", RunCommand[ApplicationType]);
gui.buttonreset();
      }
      
      if(command.equals("RESULT")){
        messagewindow.setVisible(true);
gui.buttonreset();
      }

      if(command.equals("SETTING")){
        propertywindow.age();
gui.buttonreset();
      }

      if(command.equals("CLRALL")){
        int mode = initialdialog.age();
        if( mode >= 0 ){
          Logout();
          objecteditor.gui.setVisible(true);
          stateeditor.gui.setVisible(false);
          objecteditor.clear_all( mode );
        }
        else if( mode == -2 ) open();
gui.buttonreset();
      }

      if(command.equals("OPEN")){
        open();
gui.buttonreset();
      }

      if(command.equals("SAVE")){
        save();        
gui.buttonreset();
      }

      if(command.equals("SAVEDIRECT")){
      Anode cnode = node;
      restoreProperty();
      Logout();
      ProjectFile = new XFile( ProjectDir, xml.属性値( project, "objectname" ) + ".prj" );
      saveProject();
      Login( cnode );
System.gc();
gui.buttonreset();
      }

      if(command.equals("GUIDSIN")){
        String data;
        if( ( data = xml.属性値( element, "レイアウト" ) ) != null ){// GUIデザイナの出力ファイルならレイアウト情報が含まれる
          GUIDesignerWork.Xdelete();
          LayoutData.Xdelete();
          LayoutData.Xappend( data );
          Anode cnode = node;
          Logout();
          execute( GUIDesignerCommand[ ApplicationType ], true );
          if( GUIDesignerWork.isxml() ){
            Object prnt = xml.親要素( element );
            String id = xml.要素のID( element );
            xml.要素を削除( element ); 
            cnode.element = element = xml.新しい要素( prnt, GUIDesignerWork, id );
            xml.属性値をセット( element, "レイアウト", LayoutData.toTextString() );
          }
          Login( cnode );
        }
gui.buttonreset();
      }

      if(command.equals("VERSION")){
         dialog1.age(VERSION_STRING);
gui.buttonreset();
      }

      if(command.equals("HELP")){
         execute( HelpCommand, false );
gui.buttonreset();
      }

      if(command.equals("JAVAHELP")){
         execute( NativeHelpCommand[ApplicationType], false );
gui.buttonreset();
      }

      if(command.equals("HTMLEDIT")){
        execute( HtmlEditCommand, false );
gui.buttonreset();
      }

      if(command.equals("UP_OBJ")){
        treetool.changeParent();
gui.buttonreset();
      }

      if (command.equals("PRINT")) {
        treetool.printtool.printCurrent();
gui.buttonreset();
      }

      if (command.equals("PRINTALL")) {
        treetool.printtool.printAll();
gui.buttonreset();
      }

      if(command.equals("BACK")){
       treetool.changeNode( mae_node );
gui.buttonreset();
      }

      if(command.equals("QUIT"))     {
gui.buttonreset();
        if( saveWithDialog() != -1 ){
          exitProgram();
        }
      }

    }
     

//  確認のダイアログを表示後、プロジェクトを保存する
    private int saveWithDialog(){
      String pname = xml.属性値( project, "objectname" );
      int flg = dialog3.age( "プロジェクト " + pname + "を保存しますか？" );
      if( flg == 1 ) save();
      return( flg );
    }


// プロジェクトを保存する
    private void save(){
      Anode cnode = node;
      Logout();
      Login( cnode );
      XFileFilter filter = new XFileFilter( ProjectFileMode );
      JFileChooser xchooser = new JFileChooser( ProjectDir );
      xchooser.setFileFilter(filter);
      xchooser.setDialogTitle( "プロジェクトの保存" );
      xchooser.setSelectedFile( new XFile( ProjectDir, xml.属性値( project, "objectname" ) + ".prj" ) );
    
      int retval = xchooser.showSaveDialog(objecteditor.gui );
      if(retval == JFileChooser.APPROVE_OPTION) {
        restoreProperty();
        cnode = node;
        Logout();
        ProjectFile = new XFile( xchooser.getSelectedFile() );
        saveProject();
        Login( cnode );
System.gc();
      }
    }


// プロジェクトを開く
    private void open(){
      if( saveWithDialog() != -1 ){
        XFileFilter filter = new XFileFilter( ProjectFileMode );
        JFileChooser xchooser = new JFileChooser( ProjectDir );
        xchooser.setFileFilter(filter);
        xchooser.setDialogTitle( "プロジェクトを開く" );
   
        int retval = xchooser.showOpenDialog( stateeditor.gui );
        if(retval == JFileChooser.APPROVE_OPTION) {
          File f;

          if( ( f = xchooser.getSelectedFile() ).isFile() ){
            Logout();
            ProjectFile = new XFile( f );
            loadProject();
            syncProperty();
            setlookandfeel();
            ApplicationType = parseInt( xml.属性値( properties, "ApplicationType" ) );
            objecteditor.initialise();
            objecteditor.gui.setVisible(true);
            stateeditor.gui.setVisible(false);
System.gc();
          }
        }
      }
    }


    // マウスで位置データを与える
    public void mousePointed( int xp, int yp ){

      // create state
      if( mode.equals("CRE_STA")){
        gui.addcomponent( new state( xml.新しい要素( element, "state", "_S"+ID_maker++ ), 0, "  新しい状態",xp , yp ) );
        mode = "NOP";
gui.buttonreset();
      }
      
      // create operation
      else if( mode.equals("CRE_OP3")){
        gui.addcomponent( new operation( xml.新しい要素( element,"operation", "_O"+ID_maker++ ), xp, yp, (state)comp1, (state)comp2, "in()", 90, 20, 40, 20, "out()", 90, 40, 40, 20 ) );
        mode = "NOP";
gui.buttonreset();
      }
      
      // create pin
      else if( mode.equals("CRE_PIN")){
        pin npin = new pin( xml.新しい要素( element, "pin", "_P"+ID_maker ), "Pin"  + ID_maker + "()", xp, yp );
        ID_maker++;
        gui.addcomponent( npin );
        npin.setCaretPosition(0);
        npin.moveCaretPosition( npin.getText().length() );
        npin.requestFocus();
        mode = "NOP";
gui.buttonreset();
      }
       
      // create codeclip
      else if( mode.equals("CRE_CODE")){
        gui.addcomponent( new codeclip( xml.新しい要素(element, "codeclip", "_C"+ID_maker++ ), "", xp, yp ) );
        mode = "NOP";
gui.buttonreset();
      }
  
      // create icon
      else if( mode.equals("CRE_ICO")){
        gui.addcomponent( new ImgIcon( xml.新しい要素(element, "ImgIcon", "_I"+ID_maker++ ), xp, yp ) );
        mode = "NOP";
gui.buttonreset();
      }
  
      // paste object file
      else if( mode.equals("PASTE")){
        PasteObject( xp, yp );
        mode = "NOP";
gui.buttonreset();
      }

      // load object file
      else if( mode.equals("LOAD_COMP")){
        LoadObject( loadfile, xp, yp );
        mode = "NOP";
gui.buttonreset();
      }

      gui.resize();
    }
    
    //マウスでコンポーネントをクリックする
    public void componentClicked( Component comp ){
       
      //create action (input componentn selection phase)
      if( mode.equals("CRE_ACT") ){
        if( comp instanceof pin || comp instanceof operation ){
          comp1 = comp;
          mode = "CRE_ACT2";
        }
        else{
          mode = "NOP";
gui.buttonreset();
        }
      }
      
      //create action (output component selection phase)
      else if( mode.equals("CRE_ACT2") ){
        if( comp instanceof pin || comp instanceof operation ){
          comp2 = comp;
          if( ( comp1 != comp2 ) && !( comp1 instanceof operation && comp2 instanceof operation )){
            action act = new action( xml.新しい要素( element, "action", "_A"+ID_maker++ ), comp1, comp2 );
            gui.addcomponent(act);
            act.redrawLine();
          }
        }
        mode = "NOP";
gui.buttonreset();
      }

      //create operation (state1 select mode)
      else if( mode.equals("CRE_OP") ){
          if( comp instanceof state ){
              comp1 = comp;
              mode = "CRE_OP2";
          }
          else{
            mode = "NOP";
gui.buttonreset();
          }
      }

      //create operation (state2 selection phase)
      else if( mode.equals("CRE_OP2" ) ){
          if( comp instanceof state ){
              comp2 = comp;
              mode = "CRE_OP3";
          }
          else{
            mode = "NOP";
gui.buttonreset();
          }
      }

      // delete a object
      else if( mode.equals("DEL_OBJ") ){
        comp1 = comp;
        if( !( comp1 instanceof state ) || ( ((state)comp1).isinitstate != 1 ) ) removecomponent( comp1 );
        mode = "NOP";
gui.buttonreset();
      }

      // reconnect flow between operation and state
      //  phase1:select flow
      else if( mode.equals("NOP") && comp instanceof xflow ){
          mode = "REC1";
          comp1 = comp;
      }

      //  phase2:select state
      else if( mode.equals("REC1") && comp instanceof state ){
          ( (xflow)comp1).reconnect( comp );
          mode = "NOP";
gui.buttonreset();
      }

      // copy a object
      else if( mode.equals("COPY") ){
        comp1 = comp;
        if( !( comp1 instanceof state ) || ( ((state)comp1).isinitstate != 1 ) ) {
          xml.要素を削除( xml.子要素( xml.ルート要素(), "clipboad" ) );
          if( comp1 instanceof state ) {
            ((state)comp1).save();
            clipboad = xml.新しい要素( xml.ルート要素(), ((state)comp1).element, "clipboad" );
          }
          else if( comp1 instanceof pin ) {
            ((pin)comp1).save();
             clipboad = xml.新しい要素( xml.ルート要素(), ((pin)comp1).element, "clipboad" );
          }
          else if( comp1 instanceof codeclip ) {
            ((codeclip)comp1).save();
             clipboad = xml.新しい要素( xml.ルート要素(), ((codeclip)comp1).element, "clipboad" );
          }
          else if( comp1 instanceof ImgIcon ) {
            ((ImgIcon)comp1).save();
             clipboad = xml.新しい要素( xml.ルート要素(), ((ImgIcon)comp1).element, "clipboad" );
          }
          else if( comp1 instanceof operation ) {
            ((operation)comp1).save();
            clipboad = xml.新しい要素( xml.ルート要素(), ((operation)comp1).element, "clipboad" );
          }
        }
        mode = "NOP";
gui.buttonreset();
      }

      // cut a object
      else if( mode.equals("CUT") ){
        comp1 = comp;
        if( !( comp1 instanceof state ) || ( ((state)comp1).isinitstate != 1 ) ) {
          xml.要素を削除( xml.子要素( xml.ルート要素(), "clipboad" ) );
          if( comp1 instanceof state ) {
            ((state)comp1).save();
            clipboad = xml.新しい要素( xml.ルート要素(), ((state)comp1).element, "clipboad" );
          }
          else if( comp1 instanceof pin ) {
            ((pin)comp1).save();
             clipboad = xml.新しい要素( xml.ルート要素(), ((pin)comp1).element, "clipboad" );
          }
          else if( comp1 instanceof codeclip ) {
            ((codeclip)comp1).save();
             clipboad = xml.新しい要素( xml.ルート要素(), ((codeclip)comp1).element, "clipboad" );
          }
          else if( comp1 instanceof ImgIcon ) {
            ((ImgIcon)comp1).save();
             clipboad = xml.新しい要素( xml.ルート要素(), ((codeclip)comp1).element, "clipboad" );
          }
          else if( comp1 instanceof operation ) {
            ((operation)comp1).save();
            clipboad = xml.新しい要素( xml.ルート要素(), ((operation)comp1).element, "clipboad" );
          }
          removecomponent( comp1 );
        }
        mode = "NOP";
gui.buttonreset();
      }

       // save a object
      else if( mode.equals("SAVE_COMP") ){
        comp1 = comp;
        if( comp1 instanceof pin )     {
          pin pi = (pin)comp1;
          pi.save();
          filewindow.save( pi.element, new XFile( ObjectLib[ApplicationType], "~" + pi.getText()+".xml" ) );
        }
        else if( comp1 instanceof codeclip ){
          codeclip co = (codeclip)comp1;
          co.save();
          filewindow.save( co.element, new XFile( ObjectLib[ApplicationType], "~" + compack( getFirstLine( co.codetext.getText() ) )+".xml" ) );
        }
        else if( comp1 instanceof operation ){
          operation op = (operation)comp1;
          op.save();
          filewindow.save( op.element, new XFile( ObjectLib[ApplicationType], "~" + compack( op.description.getText() )+".xml" ) );
        }
        filewindow.chooser.rescanCurrentDirectory();
        mode = "NOP";
gui.buttonreset();
      }

      checkconnect();
      gui.resize();
    }
     
    // StateEditorのGUIを記述
    class GUI extends JFrame implements ActionListener, ItemListener, MouseListener, WindowListener {
      Font font;
      Insets mergin;
      ButtonGroup group;
      JToggleButton noselected;

      JMenuBar menuBar;
          JMenu filemenu;
              JMenuItem upobj;
              JMenuItem clrall;
              JMenuItem open;
              JMenuItem filewin;
              JMenuItem savedirect;
              JMenuItem save;
              JMenuItem prnt;
              JMenuItem prntall;
              JMenuItem compile;
              JMenuItem run;
              JMenuItem exitprog;

          JMenu toolmenu;
              JMenuItem state;
              JMenuItem trans;
              JMenuItem signal;
              JMenuItem pin;
              JMenuItem codeclip;
              JMenuItem guidsin;

          JMenu editmenu;
              JMenuItem delete;
              JMenuItem cut;
              JMenuItem copy;
              JMenuItem paste;

          JMenu dispmenu;
              JCheckBoxMenuItem disptoolbox;
              JMenuItem dispresult;
              JMenuItem upall;
              JMenuItem downall;
              JMenuItem leftall;
              JMenuItem rightall;

          JMenu setmenu;
              JMenuItem setting;
              JMenuItem applethtml;

          JMenu helpmenu;
              JMenuItem objver;
              JMenuItem objhelp;
              JMenuItem javahelp;

    JToolBar toolBar;
          JToggleButton bback;
          JToggleButton bupobj;
          JToggleButton bclrall;
          JToggleButton bfilewin;
          JToggleButton bcompile;
          JToggleButton brun;

          JToggleButton bstate;
          JToggleButton btrans;
          JToggleButton bsignal;
          JToggleButton bpin;
          JToggleButton bcodeclip;
          JToggleButton bicon;
          JToggleButton bguidsin;

          JToggleButton bdelete;
          JToggleButton bcut;
          JToggleButton bcopy;
          JToggleButton bpaste;

          JToggleButton bsetting;
          JToggleButton bhelp;

    JSplitPane display;
        JTextField name;                    //名前を表示するエリア      
        JSplitPane contents;                      //(VERTICAL Splitレイアウト)
            JScrollPane description;             //(Scrollレイアウト)
                JTextArea descriptionarea;               //説明文を表示するエリア
            JScrollPane graphic;                //(Scrollレイアウト)
                JPanel  gedit;                    //グラフィック編集エリア

    GUI() {

        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        mergin = new Insets( 0, 0, 0, 0 );
        group = new ButtonGroup();
        noselected = new JToggleButton();
        group.add( noselected );

        //Create the menu bar.
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        //ファイルメニューを生成
        filemenu = new JMenu("ファイル(F)");
        font = new Font( filemenu.getFont().getName(), Font.PLAIN, 12 );
        filemenu.setFont( font );
        filemenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(filemenu);

        upobj = new JMenuItem("上位のオブジェクトに移動(U)", KeyEvent.VK_U);
        upobj.setFont( font );
        upobj.setActionCommand("UP_OBJ");
        upobj.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_U, ActionEvent.CTRL_MASK ) );
        upobj.addActionListener(this);
        filemenu.add(upobj);

        clrall = new JMenuItem("プロジェクトの新規作成(N)", KeyEvent.VK_N);
        clrall.setFont( font );
        clrall.setActionCommand("CLRALL");
        clrall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, ActionEvent.CTRL_MASK ) );
        clrall.addActionListener(this);
        filemenu.add(clrall);

        open = new JMenuItem("プロジェクトを開く(O)", KeyEvent.VK_O);
        open.setFont( font );
        open.setActionCommand("OPEN");
        open.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.CTRL_MASK ) );
        open.addActionListener(this);
        filemenu.add(open);

        filewin = new JMenuItem("部品棚を表示(F)", KeyEvent.VK_F);
        filewin.setFont( font );
        filewin.setActionCommand("FILEWIN");
        filewin.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F, ActionEvent.CTRL_MASK ) );
        filewin.addActionListener(this);
        filemenu.add(filewin);

        savedirect = new JMenuItem("プロジェクトの上書き保存(S)", KeyEvent.VK_S);
        savedirect.setFont( font );
        savedirect.setActionCommand("SAVEDIRECT");
        savedirect.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK ) );
        savedirect.addActionListener(this);
        filemenu.add(savedirect);

        save = new JMenuItem("プロジェクトの保存(A)", KeyEvent.VK_A);
        save.setFont( font );
        save.setActionCommand("SAVE");
        save.addActionListener(this);
        filemenu.add(save);

        prnt = new JMenuItem("このページの印刷(1)", KeyEvent.VK_1);
        prnt.setFont( font );
        prnt.setActionCommand("PRINT");
        prnt.addActionListener(this);
        filemenu.add(prnt);

        prntall = new JMenuItem("全て印刷(P)", KeyEvent.VK_P);
        prntall.setFont( font );
        prntall.setActionCommand("PRINTALL");
        prntall.addActionListener(this);
        filemenu.add(prntall);

        filemenu.addSeparator();

        compile = new JMenuItem("コンパイル(C)", KeyEvent.VK_C);
        compile.setFont( font );
        compile.setActionCommand("COMPILE");
        compile.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, ActionEvent.ALT_MASK ) );
        compile.addActionListener(this);
        filemenu.add(compile);

        run = new JMenuItem("実行(R)", KeyEvent.VK_R);
        run.setFont( font );
        run.setActionCommand("RUN");
        run.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, ActionEvent.ALT_MASK ) );
        run.addActionListener(this);
        filemenu.add(run);

        filemenu.addSeparator();

        exitprog = new JMenuItem("終了(X)", KeyEvent.VK_X);
        exitprog.setFont( font );
        exitprog.setActionCommand("QUIT");
        exitprog.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q, ActionEvent.CTRL_MASK ) );
        exitprog.addActionListener(this);
        filemenu.add(exitprog);

        //ツールメニューを生成
        toolmenu = new JMenu("ツール(T)");
        toolmenu.setFont( font );
        toolmenu.setMnemonic(KeyEvent.VK_T);
        menuBar.add(toolmenu);

        state = new JMenuItem("状態(J)", KeyEvent.VK_J);
        state.setFont( font );
        state.setActionCommand("CRE_STA");
        state.addActionListener(this);
        toolmenu.add(state);

        trans = new JMenuItem("遷移(T)", KeyEvent.VK_T);
        trans.setFont( font );
        trans.setActionCommand("CRE_OP");
        trans.addActionListener(this);
        toolmenu.add(trans);

        signal = new JMenuItem("信号(S)", KeyEvent.VK_S);
        signal.setFont( font );
        signal.setActionCommand("CRE_ACT");
        signal.addActionListener(this);
        toolmenu.add(signal);

        pin = new JMenuItem("ピン(P)", KeyEvent.VK_P);
        pin.setFont( font );
        pin.setActionCommand("CRE_PIN");
        pin.addActionListener(this);
        toolmenu.add(pin);

        codeclip = new JMenuItem("コードクリップ(C)", KeyEvent.VK_C);
        codeclip.setFont( font );
        codeclip.setActionCommand("CRE_CODE");
        codeclip.addActionListener(this);
        toolmenu.add(codeclip);

        toolmenu.addSeparator();
     
        guidsin = new JMenuItem("GUIデザイナ", KeyEvent.VK_G);
        guidsin.setFont( font );
        guidsin.setActionCommand("GUIDSIN");
        guidsin.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_G, ActionEvent.CTRL_MASK ) );
        guidsin.addActionListener(this);
        toolmenu.add(guidsin);

        //編集メニューを生成
        editmenu = new JMenu("編集(E)");
        editmenu.setFont( font );
        editmenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editmenu);

        delete = new JMenuItem("削除(D)", KeyEvent.VK_D);
        delete.setFont( font );
        delete.setActionCommand("DEL_OBJ");
        delete.addActionListener(this);
        editmenu.add(delete);

        cut = new JMenuItem("切り取り(T)", KeyEvent.VK_T);
        cut.setFont( font );
        cut.setActionCommand("CUT");
        cut.addActionListener(this);
        editmenu.add(cut);

        copy = new JMenuItem("コピー(C)", KeyEvent.VK_C);
        copy.setFont( font );
        copy.setActionCommand("COPY");
        copy.addActionListener(this);
        editmenu.add(copy);

        paste = new JMenuItem("貼り付け(P)", KeyEvent.VK_P);
        paste.setFont( font );
        paste.setActionCommand("PASTE");
        paste.addActionListener(this);
        editmenu.add(paste);

        //表示メニューを生成
        dispmenu = new JMenu("表示(V)");
        dispmenu.setFont( font );
        dispmenu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(dispmenu);

        disptoolbox = new JCheckBoxMenuItem("ツールバー(T)", ToolBarVisible );
        disptoolbox.setFont( font );
        disptoolbox.setMnemonic(KeyEvent.VK_T);
        disptoolbox.addItemListener(this);
        dispmenu.add(disptoolbox);

        dispmenu.addSeparator();

        dispresult = new JMenuItem("コンパイル･実行結果(V)", KeyEvent.VK_V);
        dispresult.setFont( font );
        dispresult.setActionCommand("RESULT");
        dispresult.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, ActionEvent.CTRL_MASK ) );
        dispresult.addActionListener(this);
        dispmenu.add(dispresult);

        dispmenu.addSeparator();

        upall = new JMenuItem("全体を上にずらす(U)", KeyEvent.VK_U);
        upall.setFont( font );
        upall.setActionCommand("UPALL");
        upall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_UP, ActionEvent.ALT_MASK ) );
        upall.addActionListener(this);
        dispmenu.add(upall);

        downall = new JMenuItem("全体を下にずらす(D)", KeyEvent.VK_D);
        downall.setFont( font );
        downall.setActionCommand("DOWNALL");
        downall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, ActionEvent.ALT_MASK ) );
        downall.addActionListener(this);
        dispmenu.add(downall);

        leftall = new JMenuItem("全体を左にずらす(L)", KeyEvent.VK_L);
        leftall.setFont( font );
        leftall.setActionCommand("LEFTALL");
        leftall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, ActionEvent.ALT_MASK ) );
        leftall.addActionListener(this);
        dispmenu.add(leftall);

        rightall = new JMenuItem("全体を右にずらす(R)", KeyEvent.VK_R);
        rightall.setFont( font );
        rightall.setActionCommand("RIGHTALL");
        rightall.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK ) );
        rightall.addActionListener(this);
        dispmenu.add(rightall);

        //設定メニューを生成
        setmenu = new JMenu("設定(S)");
        setmenu.setFont( font );
        setmenu.setMnemonic(KeyEvent.VK_S);
        menuBar.add(setmenu);

        setting = new JMenuItem("設定(S)", KeyEvent.VK_S );
        setting.setFont( font );
        setting.setActionCommand("SETTING");
        setting.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, ActionEvent.CTRL_MASK ) );
        setting.addActionListener(this);
        setmenu.add(setting);

        setmenu.addSeparator();

        applethtml = new JMenuItem("アプレットhtml(A)", KeyEvent.VK_A );
        applethtml.setFont( font );
        applethtml.setActionCommand("HTMLEDIT");
        applethtml.addActionListener(this);
        setmenu.add(applethtml);

        //ヘルプメニューを生成
        helpmenu = new JMenu("ヘルプ(H)");
        helpmenu.setFont( font );
        helpmenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpmenu);

        objver = new JMenuItem("バージョン情報", KeyEvent.VK_V );
        objver.setFont( font );
        objver.setActionCommand("VERSION");
        objver.addActionListener(this);
        helpmenu.add(objver);

        objhelp = new JMenuItem("ObjectEditorのヘルプ", KeyEvent.VK_O );
        objhelp.setFont( font );
        objhelp.setActionCommand("HELP");
        objhelp.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H, ActionEvent.CTRL_MASK ) );
        objhelp.addActionListener(this);
        helpmenu.add(objhelp);

        javahelp = new JMenuItem("言語のヘルプ", KeyEvent.VK_J );
        javahelp.setFont( font );
        javahelp.setActionCommand("JAVAHELP");
        javahelp.addActionListener(this);
        helpmenu.add(javahelp);

        //ツールバーを生成
        toolBar = new JToolBar();
        toolBar.setMargin( new Insets(1, 1, 1, 1 ) );


        name = new JTextField("NewObject");
        name.setMinimumSize( new Dimension( 80, 16 ) );
        name.setMaximumSize( new Dimension( 240, 24 ) );
        name.setToolTipText("オブジェクトの名前");
        name.setActionCommand("SET_NAME");
        toolBar.add(name);

        bback = new JToggleButton(new ImageIcon("resources/back.jpg"));
        bback.setToolTipText("ひとつ前に戻る");
        bback.setActionCommand("BACK");
        bback.addActionListener(this);
        bback.setMargin(mergin);
        toolBar.add(bback);
        group.add(bback);

        bupobj = new JToggleButton(new ImageIcon("resources/upobj.jpg"));
        bupobj.setToolTipText("上位オブジェクトに移動");
        bupobj.setActionCommand("UP_OBJ");
        bupobj.addActionListener(this);
        bupobj.setMargin(mergin);
        toolBar.add(bupobj);
        group.add(bupobj);

        bclrall = new JToggleButton(new ImageIcon("resources/clrall.jpg"));
        bclrall.setToolTipText("新規作成");
        bclrall.setActionCommand("CLRALL");
        bclrall.addActionListener(this);
        bclrall.setMargin(mergin);
        toolBar.add(bclrall);
        group.add(bclrall);

        bfilewin = new JToggleButton(new ImageIcon("resources/filewin.jpg"));
        bfilewin.setToolTipText("部品棚を表示");
        bfilewin.setActionCommand("FILEWIN");
        bfilewin.addActionListener(this);
        bfilewin.setMargin(mergin);
        toolBar.add(bfilewin);
        group.add(bfilewin);

        bcompile = new JToggleButton(new ImageIcon("resources/compile.jpg"));
        bcompile.setToolTipText("コンパイル");
        bcompile.setActionCommand("COMPILE");
        bcompile.addActionListener(this);
        bcompile.setMargin(mergin);
        toolBar.add(bcompile);
        group.add(bcompile);

        brun = new JToggleButton(new ImageIcon("resources/run.jpg"));
        brun.setToolTipText("実行");
        brun.setActionCommand("RUN");
        brun.addActionListener(this);
        brun.setMargin(mergin);
        toolBar.add(brun);
        group.add(brun);

        toolBar.addSeparator();

        bstate = new JToggleButton(new ImageIcon("resources/state.jpg"));
        bstate.setToolTipText("状態を貼り付ける");
        bstate.setActionCommand("CRE_STA");
        bstate.addActionListener(this);
        bstate.setMargin(mergin);
        toolBar.add(bstate);
        group.add(bstate);

        btrans = new JToggleButton(new ImageIcon("resources/trans.jpg"));
        btrans.setToolTipText("遷移を貼り付ける");
        btrans.setActionCommand("CRE_OP");
        btrans.addActionListener(this);
        btrans.setMargin(mergin);
        toolBar.add(btrans);
        group.add(btrans);

        bsignal = new JToggleButton(new ImageIcon("resources/signal.jpg"));
        bsignal.setToolTipText("信号線を貼り付ける");
        bsignal.setActionCommand("CRE_ACT");
        bsignal.addActionListener(this);
        bsignal.setMargin(mergin);
        toolBar.add(bsignal);
        group.add(bsignal);

        bpin = new JToggleButton(new ImageIcon("resources/pin.jpg"));
        bpin.setToolTipText("ピンを貼り付ける");
        bpin.setActionCommand("CRE_PIN");
        bpin.addActionListener(this);
        bpin.setMargin(mergin);
        toolBar.add(bpin);
        group.add(bpin);

        bcodeclip = new JToggleButton(new ImageIcon("resources/codeclip.jpg"));
        bcodeclip.setToolTipText("コードクリップを貼り付ける");
        bcodeclip.setActionCommand("CRE_CODE");
        bcodeclip.addActionListener(this);
        bcodeclip.setMargin(mergin);
        toolBar.add(bcodeclip);
        group.add(bcodeclip);

        bicon = new JToggleButton(new ImageIcon("resources/icon.jpg"));
        bicon.setToolTipText("アイコンを貼り付ける");
        bicon.setActionCommand("CRE_ICO");
        bicon.addActionListener(this);
        bicon.setMargin(mergin);
        toolBar.add(bicon);
        group.add(bicon);

        bguidsin = new JToggleButton(new ImageIcon("resources/guidsin.jpg"));
        bguidsin.setToolTipText("ＧＵＩデザイナ");
        bguidsin.setActionCommand("GUIDSIN");
        bguidsin.addActionListener(this);
        bguidsin.setMargin(mergin);
        toolBar.add(bguidsin);
        group.add(bguidsin);

        toolBar.addSeparator();

        bdelete = new JToggleButton(new ImageIcon("resources/delete.jpg"));
        bdelete.setToolTipText("削除");
        bdelete.setActionCommand("DEL_OBJ");
        bdelete.addActionListener(this);
        bdelete.setMargin(mergin);
        toolBar.add(bdelete);
        group.add(bdelete);

        bcut = new JToggleButton(new ImageIcon("resources/cut.jpg"));
        bcut.setToolTipText("切り取り");
        bcut.setActionCommand("CUT");
        bcut.addActionListener(this);
        bcut.setMargin(mergin);
        toolBar.add(bcut);
        group.add(bcut);

        bcopy = new JToggleButton(new ImageIcon("resources/copy.jpg"));
        bcopy.setToolTipText("コピー");
        bcopy.setActionCommand("COPY");
        bcopy.addActionListener(this);
        bcopy.setMargin(mergin);
        toolBar.add(bcopy);
        group.add(bcopy);

        bpaste = new JToggleButton(new ImageIcon("resources/paste.jpg"));
        bpaste.setToolTipText("貼り付け");
        bpaste.setActionCommand("PASTE");
        bpaste.addActionListener(this);
        bpaste.setMargin(mergin);
        toolBar.add(bpaste);
        group.add(bpaste);

        toolBar.addSeparator();

        bsetting = new JToggleButton(new ImageIcon("resources/setting.jpg"));
        bsetting.setToolTipText("設定");
        bsetting.setActionCommand("SETTING");
        bsetting.addActionListener(this);
        bsetting.setMargin(mergin);
        toolBar.add(bsetting);
        group.add(bsetting);

        bhelp = new JToggleButton(new ImageIcon("resources/help.jpg"));
        bhelp.setToolTipText("ヘルプ");
        bhelp.setActionCommand("HELP");
        bhelp.addActionListener(this);
        bhelp.setMargin(mergin);
        toolBar.add(bhelp);
        group.add(bhelp);

        descriptionarea = new JTextArea("Objectの説明");
        descriptionarea.setToolTipText("オブジェクトの説明");
        description = new JScrollPane(descriptionarea);
        description.setMinimumSize( new Dimension( 1, 1 ) );

        gedit = new JPanel();
        gedit.setBackground( Color.white );
        gedit.setToolTipText("オブジェクトの状態遷移図");
        gedit.setLayout( null );
        gedit.setSize(new Dimension(600,300));
        gedit.addMouseListener(this);
        graphic = new JScrollPane(gedit);

        contents = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, description, graphic );
        contents.setDividerSize(4);
        contents.setAlignmentX(JComponent.LEFT_ALIGNMENT);        

        display = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,true, new JLabel("1 "), contents );
        display.setDividerSize(4);
        display.setPreferredSize(new Dimension(600,300));

        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(display, BorderLayout.CENTER);
        addWindowListener(this);
        pack();

    }//~GUI()

      //名前を得る
      public String getobjectname(){ return(name.getText()); }

      //名前をセットする
      public void setobjectname( String nm ){
        name.setText(nm);
        setTitle("State Editor "+ nm + AppTypes[ApplicationType] );
      }

      //説明を得る
      public String getdescription(){ return(descriptionarea.getText()); }

      //説明をセットする
      public void setdescription( String descript ){
        descriptionarea.setText(descript);
      }

      //格納されているコンポーネントを取得する
      public Component[] getcomponents(){ return( gedit.getComponents() ); }

      //与えられた名前のコンポーネントを返す
      public Component getcomponent( String name ){
        int i;
        Component[]  comp = getcomponents();
        for( i=0; i<comp.length; i++){
          if( comp[i].getName().equals( name ) ) return( comp[i] );
        }
        return( null );
      }

      // コンポーネントを追加する
      public void addcomponent( Component cmp ){
        gedit.add(cmp);
        cmp.validate();
        cmp.repaint();
      }

      // 画面の内容を消去する
      public void clear(){
        name.setText("");
        descriptionarea.setText("");
        gedit.removeAll();
        buttonreset();
      }

      public void buttonreset(){
        noselected.setSelected(true);
      }
   
      //画面のサイズを最適化する
      public void resize(){
        int i;
        Component[] cmp;
        int width;
        int height;

        cmp = getcomponents();
        width = 200;
        height = 100;
        for( i=0; i<cmp.length; i++ ){
          if( cmp[i].getLocation().x + cmp[i].getWidth()  > width  ) width  = cmp[i].getLocation().x + cmp[i].getWidth();
          if( cmp[i].getLocation().y + cmp[i].getHeight() > height ) height = cmp[i].getLocation().y + cmp[i].getHeight();
        }
        gedit.setPreferredSize( new Dimension( width, height ) );
        gedit.setSize( width, height );
        validate();
        repaint();
        if( filewindow != null ) filewindow.setVisible( filewindow.isVisible() );
      }

      // action event            
      public void actionPerformed(ActionEvent e ){  CommandReceived( e.getActionCommand() ); } 

      // mouse event
      public void mousePressed(MouseEvent e) { mousePointed( e.getX(), e.getY() );}
      public void mouseReleased(MouseEvent e){ resize(); }
      public void mouseClicked(MouseEvent e) {   }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }

      // window event

      public void windowClosing(WindowEvent e)    { CommandReceived( "QUIT" ); }
      public void windowActivated(WindowEvent e)  {   }
      public void windowClosed(WindowEvent e)     {   }
      public void windowDeactivated(WindowEvent e){   }
      public void windowDeiconified(WindowEvent e){   }
      public void windowIconified(WindowEvent e)  {   }
      public void windowOpened(WindowEvent e)     {   }


    public void itemStateChanged(ItemEvent e) {
        ToolBarVisible = disptoolbox.isSelected();
        setVisible();
    }

      // 
      public void setVisible(){
        if( isVisible() ){
          objecteditor.gui.disptoolbox.setState(ToolBarVisible);
          toolBar.setVisible(ToolBarVisible);
          resize();
        }
      }
       
}//GUI
    
    // operation
    class operation extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
      boolean exist;
      Object element;
      int x0, y0, x1, y1, x2, y2;
      int width, height;
      JButton button;
      JTextArea description;
      String statename1, statename2;
      xflow inflow, outflow;
      connector inconnect, outconnect;
      innerpin inpin, outpin;
      int mode;
      Graphics grp;

      // 新規作成(ファイルも一緒に生成する)
      operation( Object elem, int xp, int yp, state state1, state state2, String inname, int inx, int iny, int inw, int inh, String outname, int outx, int outy, int outw, int outh ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        setOpaque(false);
        setLayout( null );
        button = new JButton(" ");
        button.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        button.setBackground(Color.blue);
        add( button );
        inpin = new innerpin( "in", inname, Color.cyan,inx, iny, inw, inh );
        add( inpin );
        outpin = new innerpin( "out", outname, Color.pink, outx, outy, outw, outh );
        add( outpin );
        inconnect =  new connector("inconnect");
        add( inconnect );
        outconnect = new connector("outconnect");
        add( outconnect );
        description = new JTextArea("");
        description.setOpaque( false );
        add( description );
        inflow  = new xflow( "inflow("+getName()+")", state1,     inconnect );
        gui.addcomponent( inflow );
        outflow = new xflow( "outflow("+getName()+")", outconnect, state2 );
        gui.addcomponent( outflow );
        x0 = xp;
        y0 = yp;
        width  = 130;
        height = 90;
        setBounds( x0, y0, width, height );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        button.addActionListener(this);
        save();
        mode = 1;
      }   


      // Loginモードで生成(ファイルの情報をもとに生成)  
      operation( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        setOpaque(false);
        setLayout( null );
        button = new JButton(" ");
        button.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        button.setBackground(Color.blue);
        add( button );
        inpin = new innerpin( "in", "in()", Color.cyan,10, 10, 10, 10 );
        add( inpin );
        outpin = new innerpin( "out", "out()",Color.pink, 10, 10, 10, 10 );
        add( outpin );
        inconnect =  new connector("inconnect");
        add( inconnect );
        outconnect = new connector("outconnect");
        add( outconnect );
        description = new JTextArea("");
        description.setOpaque( false );
        add( description );
        load();
        setBounds( x0, y0, width, height );
        inflow  = new xflow( "inflow("+getName()+")", gui.getcomponent( statename1 ),     inconnect );
        gui.addcomponent( inflow );
        outflow = new xflow( "outflow("+getName()+")", outconnect, gui.getcomponent( statename2 ) );
        gui.addcomponent( outflow );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        button.addActionListener(this);
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        if( exist ){
          remove(button);
          remove(description);
          remove(inpin);
          remove(outpin);
          inconnect.suicide();
          outconnect.suicide();
          inflow.suicide();
          outflow.suicide();
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
        if( exist ){
          save();
          remove(button);
          remove(description);
          remove(inpin);
          remove(outpin);
          inconnect.suicide();
          outconnect.suicide();
          inflow.suicide();
          outflow.suicide();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
      }

      //   ロード
      public void load(){
        statename1 = xml.属性値( element, "state1" );
        statename2 = xml.属性値( element, "state2" );
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        inconnect.x0 = parseInt( xml.属性値( element, "inconnectx0" ) );
        inconnect.y0 = parseInt( xml.属性値( element, "inconnecty0" ) );
        outconnect.x0 = parseInt( xml.属性値( element, "outconnectx0" ) );
        outconnect.y0 = parseInt( xml.属性値( element, "outconnecty0" ) );
        inpin.setText( xml.属性値( element, "inpintext" ) );
        inpin.x0 = parseInt( xml.属性値( element, "inpinx0" ) );
        inpin.y0 = parseInt( xml.属性値( element, "inpiny0" ) );
        inpin.width = parseInt( xml.属性値( element, "inpinwidth" ) );
        inpin.height = parseInt( xml.属性値( element, "inpinheight" ) );
        outpin.setText( xml.属性値( element, "outpintext" ) );
        outpin.x0 = parseInt( xml.属性値( element, "outpinx0" ) );
        outpin.y0 = parseInt( xml.属性値( element, "outpiny0" ) );
        outpin.width = parseInt( xml.属性値( element, "outpinwidth" ) );
        outpin.height = parseInt( xml.属性値( element, "outpinheight" ) );
        String des = xml.属性値( element, "description" );
        if( des.equals("") ){
          description.setText( xml.属性値( element, "codetext" ) );
        }
        else{
          String line = getFirstLine( des );
          if( line.startsWith(TransientConditionPrefix) ){
            description.setText(
              "/* " + des + " */\n"
            + " if( !( " + line.substring( TransientConditionPrefix.length()+1 ) + " ) ) return;\n"
            + xml.属性値( element, "codetext" ) 
            );
          }
          else{
            String code = "";
            do{
              code = code + "// " + line + "\n";
              des = getNextLines( des );
              if( line.startsWith(FollowIsCodePrefix) ){
                description.setText( code + des );
                setBounds( x0, y0, width, height );
                inconnect.setLocation( inconnect.x0, inconnect.y0 );
                outconnect.setLocation( outconnect.x0, outconnect.y0 );
                return;
              }
              if( des.equals("") ) break;
              line = getFirstLine( des );
            } while(true);
            description.setText( code + xml.属性値( element, "codetext" ) );
          }
        }
        setBounds( x0, y0, width, height );
        inconnect.setLocation( inconnect.x0, inconnect.y0 );
        outconnect.setLocation( outconnect.x0, outconnect.y0 );
      }

      //   セーブ
      public void save(){
        xml.属性値をセット( element, "state1", inflow.connectionname );
        xml.属性値をセット( element, "state2", outflow.connectionname );
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "inconnectx0", "" + inconnect.x0 );
        xml.属性値をセット( element, "inconnecty0", "" + inconnect.y0 );
        xml.属性値をセット( element, "outconnectx0", "" + outconnect.x0 );
        xml.属性値をセット( element, "outconnecty0", "" + outconnect.y0 );
        xml.属性値をセット( element, "inpintext", inpin.getText() );
        xml.属性値をセット( element, "inpinx0", "" + inpin.x0 );
        xml.属性値をセット( element, "inpiny0", "" + inpin.y0 );
        xml.属性値をセット( element, "inpinwidth", "" + inpin.width );
        xml.属性値をセット( element, "inpinheight", "" + inpin.height );
        xml.属性値をセット( element, "outpintext", outpin.getText() );
        xml.属性値をセット( element, "outpinx0", "" + outpin.x0 );
        xml.属性値をセット( element, "outpiny0", "" + outpin.y0 );
        xml.属性値をセット( element, "outpinwidth", "" + outpin.width );
        xml.属性値をセット( element, "outpinheight", "" + outpin.height );
        xml.属性値をセット( element, "inpinlinkcount", "" + inpin.linkcount );
        xml.属性値をセット( element, "description", "" );
        xml.属性値をセット( element, "codetext", description.getText() );
      }

      // stateとの接続をチェックして接続していなかったら消去する     
      public void check(){
        if( gui.getcomponent( inflow.connectionname ) == null || gui.getcomponent( outflow.connectionname ) == null ) suicide();
      }

      //自分がクリックされた信号をStateEditorに発行する   
      public void operationClick(){
        componentClicked( this );
      }

       //コンポーネント描画
      public void paintComponent(Graphics g){
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          button.setBounds( 0, 0, width, 10 );
          description.setBounds( 10, 10, width-20, height-10 );
          inflow.redrawLine();
          outflow.redrawLine();
          mode = 2;
        }
        super.paintComponent(g);
        g.setColor(Color.gray);
        g.drawRect( 0, 0, width-1, height-1 );
        g.drawRect( 0, 10, width-10, height-10 );
      }

      // action event (エディタを起動してコードを編集する)
      public void actionPerformed(ActionEvent e ){
        if( JavaEditCommand.equals("") ){ description.setText( texteditor.start( description.getText() ) ); }
        else{
          if( FTmpTextFile.isFile() || FTmpTextFile.isDirectory() ) FTmpTextFile.Xdelete();
          FTmpTextFile.Xappend( description.getText() );
          execute( JavaEditCommand+" "+TmpTextFile, true );
          try{
            BufferedReader din = new BufferedReader( new FileReader( FTmpTextFile ) );
            String line;
            String code = "";
            while((line=din.readLine())!=null){
              code = code + line + "\n";
            }
            din.close();
            description.setText( code );
          } catch( IOException ie ){ reportError("ソースコードを編集できません．\n"); }
        }
        filewindow.setVisible( filewindow.isVisible() );
      }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
//          setSize(1,1);
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
            width =  x1 + 5;
            if( width < 10 ) width = 10;
            height = y1 + 5;
            if( height < 10 ) height = 10;
        }
        else if( mode == 4){
          x0 += x1 - x2;
          if( x0 < 0) x0 = 0;
          y0 += y1 - y2;
          if(y0 < 0 ) y0 = 0;
        }
        x2 = x1;
        y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        button.setBounds( 0, 0, width, 10 );
        description.setBounds( 10, 10, width-20, height-10 );
        gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { operationClick(); }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }

      //   operationの内部オブジェクト
      class innerpin extends JTextField implements MouseMotionListener, MouseListener {
        int x0, y0, x1, y1, x2, y2;
        int width, height;
        int mode;
        int linkcount;
        String txt;

        innerpin( String name, String text, Color color, int xx, int yy, int ww, int hh ){
          x0 = xx;
          y0 = yy;
          linkcount = 0;
          setName( name );    
          setText( text );
          setBackground( color );
          width = ww;
          height = hh;
          addMouseMotionListener(this);            //マウスリスナーを設定
          addMouseListener(this);                  //マウスリスナーを設定
          setBounds( x0, y0, width, height );
          setOpaque(true);
          setVisible( false );
          mode = 1;
        }

       public void inclinkcount(){
         if( ++linkcount > 0 ) setVisible(true);
         getParent().repaint();
       }

       public void declinkcount(){
         if( --linkcount <= 0 ){
           setVisible( false );
           linkcount = 0;
           setText( getName() + "()" );
         }
       }

       //コンポーネント描画
       public void paintComponent(Graphics g){
         if( mode == 1 ){
           setBounds( x0, y0, width, height );
           mode = 2;
         }
         super.paintComponent(g);
       }

       //mouse event
       public void mousePressed(MouseEvent e) {
         if( mode == 2 ){
           grp = getParent().getGraphics();
           x2 = e.getX();
           y2 = e.getY();
           if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
           txt = getText();
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
         }
       }

       public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
         x1 = e.getX();
         y1 = e.getY();
         if( mode == 3 ){
           width =  x1 + 5;
           if( width < 10 ) width = 10;
           height = y1 + 5;
           if( height < 10 ) height = 10;
         }
         else if( mode == 4){
           x0 += x1 - x2;
           if( x0 < 0) x0 = 0;
           y0 += y1 - y2;
           if(y0 < 0 ) y0 = 0;
         }
         x2 = x1;
         y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
       }

       public void mouseReleased(MouseEvent e){
         setBounds( x0, y0, width, height );
         getParent().repaint();
         mode = 2;
       }    

       public void mouseClicked(MouseEvent e) { operationClick(); }
       public void mouseMoved(MouseEvent e)   {   }
       public void mouseEntered(MouseEvent e) {   }
       public void mouseExited(MouseEvent e)  {   }

      }//~innerpin


    }//~operation

     // 遷移フロー      
     class xflow extends JPanel implements ComponentListener, MouseListener {
       boolean exist;
       Component node1, node2;
       int x0, y0, width, height;
       String connectionname;
       int startx, starty, endx, endy;

       xflow( String name, Component cp1, Component cp2 ) {
         exist = true;
         setName( name );
         node1 = cp1;
         node2 = cp2;

         node1.addComponentListener( this );
         if( node1 instanceof connector) node1.getParent().addComponentListener( this );
         node2.addComponentListener( this );
         if( node2 instanceof connector) node2.getParent().addComponentListener( this );

         if( node1 instanceof state ) {
           ( (state)node1 ).connectflow();
           connectionname = node1.getName();
         }
       
         if( node2 instanceof state ) {
           ( (state)node2 ).connectflow();
           connectionname = node2.getName();
         }
         addMouseListener(this);                  //マウスリスナーを設定
         setForeground(Color.magenta);
         setOpaque( false );
         setBounds( 0, 0, 10, 10 );
       }

       public void suicide(){
         if( exist ){
           exist = false;
           if( node1 instanceof state ){
             ( (state)node1 ).disconnectflow();
             node1.removeComponentListener( this );
           }
           if( node2 instanceof state ){
             ( (state)node2 ).disconnectflow();
             node2.removeComponentListener( this );
           }
           if( getParent() != null ) getParent().remove( this );
         }
       }

       // 接続先(state)を変える
       public void reconnect( Component comp ){
         if( ( comp instanceof state ) == false ) return;

         if( node1 instanceof state ){
           node1.removeComponentListener( this );
           ( (state)node1 ).disconnectflow();
           node1 = comp;
           node1.addComponentListener( this );
           connectionname = node1.getName();
           ( (state)node1 ).connectflow();
         }

         if( node2 instanceof state ){
           node2.removeComponentListener( this );
           ( (state)node2 ).disconnectflow();
           node2 = comp;
           node2.addComponentListener( this );
           connectionname = node2.getName();
           ( (state)node2 ).connectflow();
         }

         redrawLine();
       }

       public void paintComponent(Graphics g){    
         int   rx, ry;
         double len, co, si;

         rx = startx - endx;
         ry = starty - endy;
         len = java.lang.Math.sqrt( (double)rx * rx + ry * ry );
         co = 9.659D / len;
         si = 2.588D / len;

         setBounds( x0, y0, width, height );
         super.paintComponent(g);
         g.drawLine( startx, starty, endx, endy );
         g.drawLine( (int)( co * rx + si * ry ) + endx, (int)(-si * rx + co * ry ) + endy, endx, endy );
         g.drawLine( (int)( co * rx - si * ry ) + endx, (int)( si * rx + co * ry ) + endy, endx, endy );
       }

       // 線を引き直す
       public void redrawLine() {
         boolean exist1,exist2;
         int      x1, y1, width1, height1;
         int      x2, y2, width2, height2;
         Point    p1,p2;

         if( node1 instanceof state ) exist1 = ( (state)node1 ).exist;
         else if( node1 instanceof connector ) exist1 = ( (connector) node1 ).exist;
         else  exist1 = true;
         if( node2 instanceof state ) exist2 = ( (state)node2 ).exist;
         else if( node2 instanceof connector ) exist2 = ( (connector) node2 ).exist;
         else  exist2 = true;
         if( exist1 == false || exist2 == false ){
           suicide();
           return;
         }

         if( node1 instanceof connector ){
           x1   = node1.getLocation().x + node1.getParent().getLocation().x;
           y1   = node1.getLocation().y + node1.getParent().getLocation().y;
         }
         else {
           x1 = node1.getLocation().x;
           y1 = node1.getLocation().y;
         }
         width1  = node1.getWidth();
         height1 = node1.getHeight();

         if( node2 instanceof connector ){
           x2   = node2.getLocation().x + node2.getParent().getLocation().x;
           y2   = node2.getLocation().y + node2.getParent().getLocation().y;
         }
         else {
           x2 = node2.getLocation().x;
           y2 = node2.getLocation().y;
         }
         width2  = node2.getWidth();
         height2 = node2.getHeight();

         p1 = getBorderPoint( x1, y1, width1, height1, x2, y2, width2, height2 );
         p2 = getBorderPoint( x2, y2, width2, height2, x1, y1, width1, height1 );
         startx = p1.x;
         starty = p1.y;
         endx   = p2.x;
         endy   = p2.y;
         if( startx < endx ){
           x0 = startx - 5;
           width = endx - startx + 11;
           startx = 5;
           endx = width - 6;
         }
         else{
           x0 = endx - 5;
           width = startx - endx + 11;
           startx = width - 6;
           endx = 5;
         }
         if( starty < endy ){
           y0 = starty - 5;
           height = endy - starty + 11;
           starty = 5;
           endy = height - 6;
         }
         else{
           y0 = endy - 5;
           height = starty - endy + 11;
           starty = height - 6;
           endy = 5;
         }
         setBounds( x0, y0, width, height );
         repaint();
       }

       // Component event
       public void componentMoved( ComponentEvent e )  {  redrawLine(); }
       public void componentResized( ComponentEvent e ){  redrawLine(); }
       public void componentShown( ComponentEvent e )  {  }
       public void componentHidden( ComponentEvent e ) {  }

       // Mouse event
       public void mouseClicked(MouseEvent e) { componentClicked( this );  }
       public void mousePressed(MouseEvent e) {   }
       public void mouseReleased(MouseEvent e){   }
       public void mouseEntered(MouseEvent e) {   }
       public void mouseExited(MouseEvent e)  {   }
 
     }//xflow

       class connector extends JLabel implements MouseListener, MouseMotionListener {
         boolean exist;
         int x0, y0;
         int x1, y1, x2, y2;

         connector( String name ){
           exist = true;
           setName( name );
           x0 = 0;
           y0 = 30;
           setText("      ");
           addMouseMotionListener(this);            //マウスリスナーを設定
           addMouseListener(this);                  //マウスリスナーを設定
           setSize( getPreferredSize() );
         }

         //削除(ファイルごと削除)
         public void suicide(){
           if( exist ){
             exist = false;
             if( getParent() != null ) getParent().remove(this);
           }
         }

         public void paintComponent(Graphics g){
           setLocation( x0, y0 );
           super.paintComponent(g);
         }

         public void mousePressed(MouseEvent e) {
           x2 = e.getX();
           y2 = e.getY();
         }

         public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
           x1 = e.getX();
           y1 = e.getY();
           x0 += x1 - x2;
           if( x0 < 0 ) x0 = 0;
           if( x0 > getParent().getWidth() ) x0 = getParent().getWidth();
           y0 += y1 - y2;

           if( y0 < 0 ) y0 = 0;
           if( y0 > getParent().getHeight() ) x0 = getParent().getHeight();
           x2 = x1;
           y2 = y1;
           repaint();
         }

         public void mouseClicked(MouseEvent e) {   }
         public void mouseReleased(MouseEvent e){   }
         public void mouseMoved(MouseEvent e)   {   }
         public void mouseEntered(MouseEvent e) {   }
         public void mouseExited(MouseEvent e)  {   }

       }//~connector

    //pin
    class pin extends JTextField implements MouseMotionListener, MouseListener {
      boolean exist;
      Object element;
      int x0, y0, x1, y1, x2, y2;
      int width, height;
      int px, py;
      String txt;
      Graphics grp;
      int mode;

      // 新規作成(ファイルも一緒に生成する)
      pin( Object elem, String method, int x, int y ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        x0 = x;
        y0 = y;
        width = 60;
        height = 30;
        px = 10;
        py = 40;
        setText( method );
        setBounds( x0, y0, width, height );
        save();
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        mode = 1;
      }


      // Loginモードで生成(ファイルの情報をもとに生成)
      pin( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        load();
        setBounds( x0, y0, width, height );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        if( exist ){
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
        if( exist ){
          save();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        setText( xml.属性値( element, "text" ) );
        px = parseInt( xml.属性値( element, "px" ) );
        py = parseInt( xml.属性値( element, "py" ) );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "text", getText() );
        xml.属性値をセット( element, "px", "" + px );
        xml.属性値をセット( element, "py", "" + py );
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){    
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          mode = 2;
        }
        super.paintComponent(g);
      }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          txt = getText();
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
            width =  x1 + 5;
            if( width < 10 ) width = 10;
            height = y1 + 5;
            if( height < 10 ) height = 10;
        }
        else if( mode == 4){
          x0 += x1 - x2;
          if( x0 < 0) x0 = 0;
          y0 += y1 - y2;
          if(y0 < 0 ) y0 = 0;
        }
        x2 = x1;
        y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this );   }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }

    }//~pin

     class action extends JPanel implements ComponentListener, MouseListener {
       boolean exist;
       Object element;
       Component comp1, comp2;
       String comp1name, comp2name;
       int x0, y0, width, height;
       int startx, starty, endx, endy;
 
       // 新規作成(ファイルも一緒に生成する)
       action( Object elem, Component cp1, Component cp2 ) {
         exist = true;
         element = elem;
         setName( xml.要素のID( element ) );
         comp1 = cp1;
         comp2 = cp2;
         comp1.addComponentListener( this );
         if( comp1 instanceof operation ){
           ((operation)comp1).outpin.addComponentListener(this);
           ((operation)comp1).outpin.inclinkcount();
         }
         comp2.addComponentListener( this );
         if( comp2 instanceof operation ){
           ((operation)comp2).inpin.addComponentListener(this);
           ((operation)comp2).inpin.inclinkcount();
         }
         save();
         addMouseListener(this);                  //マウスリスナーを設定
         setOpaque( false );
         redrawLine();
       }

       // Loginモードで生成  (ファイルの情報をもとに生成／注意：接続するコンポーネントが存在していること)
       action( Object elem ){
         exist = true;
         element = elem;
         setName( xml.要素のID( element  ) );
         load();
         comp1.addComponentListener( this );
         if( comp1 instanceof operation ){
           ((operation)comp1).outpin.addComponentListener(this);
           ((operation)comp1).outpin.inclinkcount();
         }
         comp2.addComponentListener( this );
         if( comp2 instanceof operation ){
           ((operation)comp2).inpin.addComponentListener(this);
           ((operation)comp2).inpin.inclinkcount();
         }
         addMouseListener(this);                  //マウスリスナーを設定
         setOpaque( false );
         redrawLine();
       }

       //削除(ファイルごと削除)
       public void suicide(){
         if( exist ){
           xml.要素を削除( element );
           exist = false;
           comp1.removeComponentListener( this );
           if( comp1 instanceof operation ){
             ((operation)comp1).outpin.removeComponentListener(this);
             ((operation)comp1).outpin.declinkcount();
           }
           comp2.removeComponentListener( this );
           if( comp2 instanceof operation ){
             ((operation)comp2).inpin.removeComponentListener(this);
             ((operation)comp2).inpin.declinkcount();
           }
           if( getParent() != null ) getParent().remove(this);
         }
         element = null;
       } 

       // Logout(内容をファイルにセーブして消去)
       public void Logout(){
         if( exist ){
           save();
           exist = false;
           comp1.removeComponentListener( this );
           if( comp1 instanceof operation ){
             ((operation)comp1).outpin.removeComponentListener(this);
             ((operation)comp1).outpin.declinkcount();
           }
           comp2.removeComponentListener( this );
           if( comp2 instanceof operation ){
             ((operation)comp2).inpin.removeComponentListener(this);
             ((operation)comp2).inpin.declinkcount();
           }
           if( getParent() != null ) getParent().remove(this);
         }
         element = null;
       }

       // 内容をロード
       public void load(){
         comp1name = xml.属性値( element, "comp1name" );
         comp2name = xml.属性値( element, "comp2name" );
         comp1 = gui.getcomponent( comp1name );
         comp2 = gui.getcomponent( comp2name );
       }

       // 内容をセーブ
       public void save(){
        xml.属性値をセット( element, "comp1name", comp1.getName() );
        xml.属性値をセット( element, "comp2name", comp2.getName() );
       }

       // 線を引き直す
       public void redrawLine() {
         boolean exist1,exist2;
         int      x1, y1, width1, height1;
         int      x2, y2, width2, height2;
         operation op;
         Point    p1,p2;

         if( comp1 instanceof pin ) exist1 = ( (pin)comp1 ).exist;
         else if( comp1 instanceof operation ) exist1 = ( (operation)comp1 ).exist;
         else  exist1 = false;
         if( comp2 instanceof pin ) exist2 = ( (pin)comp2 ).exist;
         else if( comp2 instanceof operation ) exist2 = ( (operation)comp2 ).exist;
         else  exist2 = false;
         if( exist1 == false || exist2 == false ){
           suicide();
           return;
         }

         if( comp1 instanceof operation ){
           op = (operation) comp1;
           x1 = op.getLocation().x + op.outpin.getLocation().x;
           y1 = op.getLocation().y + op.outpin.getLocation().y;
           width1  = op.outpin.getWidth();
           height1 = op.outpin.getHeight();
         }
         else{
           x1 = comp1.getLocation().x;
           y1 = comp1.getLocation().y;
           width1  = comp1.getWidth();
           height1 = comp1.getHeight();
         }

         if( comp2 instanceof operation ){
           op = (operation) comp2;
           x2 = op.getLocation().x + op.inpin.getLocation().x;
           y2 = op.getLocation().y + op.inpin.getLocation().y;
           width2  = op.inpin.getWidth();
           height2 = op.inpin.getHeight();
         }
         else{
           x2 = comp2.getLocation().x;
           y2 = comp2.getLocation().y;
           width2  = comp2.getWidth();
           height2 = comp2.getHeight();
         }

         p1 = getBorderPoint( x1, y1, width1, height1, x2, y2, width2, height2 );
         p2 = getBorderPoint( x2, y2, width2, height2, x1, y1, width1, height1 );
         startx = p1.x;
         starty = p1.y;
         endx   = p2.x;
         endy   = p2.y;
         if( startx < endx ){
           x0 = startx - 5;
           width = endx - startx + 11;
           startx = 5;
           endx = width - 6;
         }
         else{
           x0 = endx - 5;
           width = startx - endx + 11;
           startx = width - 6;
           endx = 5;
         }
         if( starty < endy ){
           y0 = starty - 5;
           height = endy - starty + 11;
           starty = 5;
           endy = height - 6;
         }
         else{
            y0 = endy - 5;
            height = starty - endy + 11;
            starty = height - 6;
            endy = 5;
         }
         setBounds( x0, y0, width, height );
         repaint();
       }

       public void paintComponent(Graphics g){
         int   rx, ry;
         double len, co, si;

         rx = startx - endx;
         ry = starty - endy;
         len = java.lang.Math.sqrt( (double)rx * rx + ry * ry );
         co = 9.659D / len;
         si = 2.588D / len;

         setBounds( x0, y0, width, height );
         super.paintComponent(g);
         g.drawLine( startx, starty, endx, endy );
         g.drawLine( (int)( co * rx + si * ry ) + endx, (int)(-si * rx + co * ry ) + endy, endx, endy );
         g.drawLine( (int)( co * rx - si * ry ) + endx, (int)( si * rx + co * ry ) + endy, endx, endy );
       }

       // Component event
       public void componentMoved( ComponentEvent e )  {  redrawLine(); }
       public void componentResized( ComponentEvent e ){  redrawLine(); }
       public void componentShown( ComponentEvent e )  {  }
       public void componentHidden( ComponentEvent e ) {  }

       // Mouse event
       public void mouseClicked(MouseEvent e) {
         componentClicked( this );
       }
       public void mousePressed(MouseEvent e) {   }
       public void mouseReleased(MouseEvent e){   }
       public void mouseEntered(MouseEvent e) {   }
       public void mouseExited(MouseEvent e)  {   }

     }//~action

    // codeclip
    class codeclip extends JPanel implements MouseMotionListener, MouseListener, ActionListener{
      boolean exist;
      Object element;
      int x0, y0, x1, y1, x2, y2;
      int width, height;
      int mode;
      Graphics grp;

      JButton openWindow;
      JTextArea codetext;
      
      // 新規作成(ファイルも一緒に生成する)
      codeclip( Object elem, String code, int x, int y ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        x0 = x;
        y0 = y;
        width = 100;
        height = 20;
        setLayout( null );
        openWindow = new JButton();
        openWindow.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        add(openWindow);
        openWindow.setBackground( Color.blue );
        openWindow.addActionListener(this);
        codetext = new JTextArea(code);
        codetext.setBorder( new LineBorder(Color.gray) );
        add(codetext);
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        save();
        setBounds( x0, y0, width, height );
        setBackground( Color.white );
        mode = 1;
      }


      // Loginモードで生成(ファイルの情報をもとに生成)
      codeclip( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        setLayout( null );
        openWindow = new JButton( );
        openWindow.setAlignmentX( JComponent.LEFT_ALIGNMENT );
        add(openWindow);
        openWindow.setBackground( Color.blue );
        openWindow.addActionListener(this);
        codetext = new JTextArea("  ");
        codetext.setBorder( new LineBorder(Color.gray) );
        add(codetext);
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        load();
        setBounds( x0, y0, width, height );
        setBackground( Color.white );
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        if( exist ){
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
        if( exist ){
          save();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        codetext.setText( xml.属性値( element, "codetext" ) );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "codetext", codetext.getText() );
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){    
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          openWindow.setBounds( 0, 0, 10, height );
          codetext.setBounds( 10, 0, width-20, height );
          mode = 2;
        }
        super.paintComponent(g);
        g.drawRect( 0, 0, width - 1, height - 1 );
      }

      // action event (エディタを起動してコードを編集する)
      public void actionPerformed(ActionEvent e ){
        if( JavaEditCommand.equals("") ){ codetext.setText( texteditor.start( codetext.getText() ) ); }
        else{
          if( FTmpTextFile.isFile() || FTmpTextFile.isDirectory() ) FTmpTextFile.Xdelete();
          FTmpTextFile.Xappend( codetext.getText() );
          execute( JavaEditCommand+" "+TmpTextFile, true );
          try{
            BufferedReader din = new BufferedReader( new FileReader( FTmpTextFile ) );
            String line;
            String code = "";
            while((line=din.readLine())!=null){
              code = code + line + "\n";
            }
            din.close();
            codetext.setText(code);
          } catch( IOException ie ){ reportError("ソースコードを編集できません．\n"); }
        }
        filewindow.setVisible( filewindow.isVisible() );
      }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
//          setSize(1,1);
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
            width =  x1 + 5;
            if( width < 30 ) width = 30;
            height = y1 + 5;
            if( height < 10 ) height = 10;
        }
        else if( mode == 4){
          x0 += x1 - x2;
          if( x0 < 0) x0 = 0;
          y0 += y1 - y2;
          if(y0 < 0 ) y0 = 0;
        }
        x2 = x1;
        y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        openWindow.setBounds( 0, 0, 10, height );
        codetext.setBounds( 10, 0, width-20, height );
        mode = 2;
        gui.resize();
        repaint();
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this ); }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }
    
    }//~codeclip

    //画像アイコン
    class ImgIcon extends JPanel implements MouseMotionListener, MouseListener {

      boolean exist;
      Object element;
      int x0, y0,x1, y1, x2, y2;
      int width, height;
      int mode, state0;
      Graphics grp;
      BufferedImage image;

      // 新規作成(ファイルも一緒に生成する)
      ImgIcon( Object elem, int x, int y ){
        exist = true;
        element = elem;
        image = null;
        setName( xml.要素のID( element ) );
        x0 = x;
        y0 = y;
        width  = 107;
        height = 50;
        setBounds(x0,y0,width,height);
        setForeground(new Color( 255, 0, 0 ));
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        save();
        mode = 1;
        state0 = 0;
      }

      // Loginモードで生成(ファイルの情報をもとに生成)  
      ImgIcon( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        load();
        setBounds( x0, y0, width, height );
        setForeground(new Color(255, 0, 0 ));
        setBackground(Color.white );
        setOpaque( false );
        setLayout( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        mode = 1;
        state0 = 0;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        int i;
        if( exist ){
          xml.要素を削除( element );
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
        image = null;
      }

      // Logout(内容をxmlにセーブして消去)
      public void Logout(){
        int i;
        if( exist ){
          save();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        image = text2image( xml.属性値( element, "image" ) );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "image", image2text(image) );
      }

      // 画像からテキストを生成
      public String image2text( BufferedImage img ){
        byte[] sbyte = null;
        int i =0,j = 0;
        if( img == null ) return "";
        try{
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          BufferedOutputStream os = new BufferedOutputStream( bos );
          img.flush();
          ImageIO.write( img, "jpg", os );
          sbyte = bos.toByteArray();
        }catch( Exception e ){e.printStackTrace();}
        int n = sbyte.length;
        if( n == 0 ) return "";
        byte[] tbyte = new byte[ n * 2 + n / 32 ];
        for( i = j = 0; i < n; i++ ){
          tbyte[j++] = (byte)(((int)sbyte[i] & 0x0f) + (int)'a' ); 
          tbyte[j++] = (byte)((((int)sbyte[i] >> 4) & 0x0f) + (int)'a' ); 
          if( (i&31)==31 ) tbyte[j++] = (byte)'\n';
        }
        return new String(tbyte);
      }
  
      // テキストから画像を生成
      public BufferedImage text2image( String txt ){
         BufferedImage img = null;
         int i =0,j =0;
         if( txt == null || txt.equals("") ) return null;
		 byte[] sbyte = txt.getBytes();
         int nn = sbyte.length;
         if( nn == 0 ) return null;
         int n = nn * 32 / 65;
		 byte[] tbyte = new byte[ n ];
		 for( i = j = 0; i < n; i++ ){
           tbyte[i] = (byte)( ((int)sbyte[j++]-(int)'a') | (((int)sbyte[j++]-(int)'a')<<4) );
           if( sbyte[j] == (byte)'\n' ) j++;
         }
		 try{ 
           img = ImageIO.read( new ByteArrayInputStream( tbyte ) );
        }catch( Exception e ){e.printStackTrace();}
        return img;
      }
  
      //コンポーネント描画
      public void paintComponent(Graphics g){
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          mode = 2;
        }
        super.paintComponent(g);
        g.drawRect( 0, 0, width-1, height-1 );
        if( image != null )  g.drawImage(image, 0, 0, width, height, null );
      }

      //mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
          state0 = 0;
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
          width =  x1 + 5;
          if( width < 10 ) width = 10;
             height = y1 + 5;
             if( height < 10 ) height = 10;
           }
           else if( mode == 4){
             x0 += x1 - x2;
             if( x0 < 0) x0 = 0;
             y0 += y1 - y2;
             if(y0 < 0 ) y0 = 0;
           }
           x2 = x1;
           y2 = y1;
          grp.drawRect( x0+1, y0+1, width-2, height-2 );
          grp.setPaintMode();
          state0 = 1;
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        if( mode ==3 && state0 == 0 ){
          try{
            if( image != null ) ImageIO.write(image, "jpg", FTmpImageFile);
            else{
              FileInputStream fileIn = new FileInputStream("dmy.jpg");
              FileOutputStream fileOut = new FileOutputStream(TmpImageFile);
              byte[] buf = new byte[256];
              int len;
              while((len = fileIn.read(buf)) != -1){
                fileOut.write(buf);    
              }
              fileOut.flush();
              fileOut.close();
              fileIn.close();
            }
            execute( ImageEditCommand+" "+TmpImageFile, true );
            image = ImageIO.read(FTmpImageFile);
	      } catch(Exception ee){ ee.printStackTrace();}
        }
        gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { componentClicked( this );  }
      public void mouseMoved(MouseEvent e)   {   } 
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }


    }//~ImgIcon

  }//StateEditor

    // state
    class state extends JTextField implements MouseMotionListener, MouseListener {
      boolean exist;
      Object element;
      int x0, y0, x1, y1, x2, y2;
      int width, height;
      int isinitstate;
      int flowcount, mode;
      String txt;
      Graphics grp;

      // 新規作成(ファイルも一緒に生成する)
      state( Object elem, int isinista, String name, int xx, int yy ){
        exist = true;
        element = elem;
        isinitstate = isinista;
        setName( xml.要素のID( element ) );
        x0 = xx;
        y0 = yy;
        width = 70;
        height = 40;
        setText( name );
        flowcount = 0;
        setBounds( x0, y0, width, height );
        save();
        setBorder( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        setOpaque(false);
        mode = 1;
      }

      // Loginモードで生成(ファイルの情報をもとに生成)
      state( Object elem ){
        exist = true;
        element = elem;
        setName( xml.要素のID( element ) );
        load();
        flowcount = 0;
        setBounds( x0, y0, width, height );
        setBorder( null );
        addMouseMotionListener(this);            //マウスリスナーを設定
        addMouseListener(this);                  //マウスリスナーを設定
        mode = 1;
      }

      //削除(ファイルごと削除)
      public void suicide(){
        if( flowcount == 0 ){
          if( exist ){
            xml.要素を削除( element );
            exist = false;
            if( getParent() != null ) getParent().remove(this);
          }
          element = null;
        }
      }

      // Logout(内容をファイルにセーブして消去)
      public void Logout(){
        if( exist ){
          save();
          exist = false;
          if( getParent() != null ) getParent().remove(this);
        }
        element = null;
      }

      // 内容をロード
      public void load(){
        x0 = parseInt( xml.属性値( element, "x0" ) );
        y0 = parseInt( xml.属性値( element, "y0" ) );
        width = parseInt( xml.属性値( element, "width" ) );
        height = parseInt( xml.属性値( element, "height" ) );
        setText( xml.属性値( element, "text" ) );
        isinitstate = parseInt( xml.属性値( element, "isinitstate" ) );
      }

      // 内容をセーブ
      public void save(){
        xml.属性値をセット( element, "x0", "" + x0 );
        xml.属性値をセット( element, "y0", "" + y0 );
        xml.属性値をセット( element, "width", "" + width );
        xml.属性値をセット( element, "height", "" + height );
        xml.属性値をセット( element, "text", getText() );
        xml.属性値をセット( element, "isinitstate", "" + isinitstate );
      }

      // operationと接続する     
      public void connectflow(){
        flowcount ++;
      }

      // operationと切り離す
      public void disconnectflow(){
        if( flowcount > 0 ) flowcount--;
      }

      //コンポーネント描画
      public void paintComponent(Graphics g){    
        if( mode == 1 ){
          setBounds( x0, y0, width, height );
          mode = 2;
        }
        super.paintComponent(g);
        g.drawOval( 2, 2, width-5, height-5 );
        if( isinitstate == 1){
          g.drawOval( 0, 0, width-1, height-1 );
        }
      }

      // mouse event
      public void mousePressed(MouseEvent e) {
        if( mode == 2 ){
          txt = getText();
          grp = getParent().getGraphics();
          x2 = e.getX();
          y2 = e.getY();
          if( x2 > width-10 && y2 > height-10 ) mode = 3; else mode = 4;
          grp.setXORMode(Color.white);
          grp.drawOval( x0+2, y0+2, width-5, height-5 );
          grp.setPaintMode();
        }
      }

      public void mouseDragged(MouseEvent e){ //ドラッグした時の処理
          grp.setXORMode(Color.white);
          grp.drawOval( x0+2, y0+2, width-5, height-5 );
        x1 = e.getX();
        y1 = e.getY();
        if( mode == 3 ){
            width =  x1 + 5;
            if( width < 10 ) width = 10;
            height = y1 + 5;
            if( height < 10 ) height = 10;
        }
        else if( mode == 4){
          x0 += x1 - x2;
          if( x0 < 0) x0 = 0;
          y0 += y1 - y2;
          if(y0 < 0 ) y0 = 0;
        }
        x2 = x1;
        y2 = y1;
          grp.drawOval( x0+2, y0+2, width-5, height-5 );
          grp.setPaintMode();
      }

      public void mouseReleased(MouseEvent e){
        setBounds( x0, y0, width, height );
        stateeditor.gui.resize();
        mode = 2;
      }

      public void mouseClicked(MouseEvent e) { stateeditor.componentClicked( this ); }
      public void mouseMoved(MouseEvent e)   {   }
      public void mouseEntered(MouseEvent e) {   }
      public void mouseExited(MouseEvent e)  {   }
  
    }//~state



  // プロパティウィンドウ   
  class PropertyWindow extends JFrame implements ActionListener {

    int i;
    JCheckBox      viewsourceatcompile;
    JCheckBox      opencompiledialog;
    JCheckBox      nooptimizepin;
    JLabel         Llookandfeels;
    JComboBox      lookandfeels;
    JLabel         Ljavaeditcommand;
    JTextField     javaeditcommand;
    JLabel         Lscript_exec_command;
    JTextField     script_exec_command;
    JLabel         Limgeditcommand;
    JTextField     imgeditcommand;
    JLabel         Ljavaviewcommand;
    JTextField     javaviewcommand;
    JLabel         Lhtmleditcommand;
    JTextField     htmleditcommand;
    JLabel         Lhelpcommand;
    JTextField     helpcommand;
    JPanel         properties;
        
    JLabel         Lcompilecommand0;
    TextButton     compilecommand0;
    JLabel         Lruncommand0;
    TextButton     runcommand0;
    JLabel         Lguidesignercommand0;
    TextButton     guidesignercommand0;
    JLabel         Limportfiles0;
    TextButton      importfiles0;
    JLabel         Lprogramstartupcode0;
    TextButton      programstartupcode0;
    JLabel         Lnativehelpcommand0;
    TextButton     nativehelpcommand0;
    JPanel         properties0;

    JLabel         Lcompilecommand1;
    TextButton     compilecommand1;
    JLabel         Lruncommand1;
    TextButton     runcommand1;
    JLabel         Lguidesignercommand1;
    TextButton     guidesignercommand1;
    JLabel         Limportfiles1;
    TextButton     importfiles1;
    JLabel         Lprogramstartupcode1;
    TextButton      programstartupcode1;
    JLabel         Lnativehelpcommand1;
    TextButton     nativehelpcommand1;
    JPanel         properties1;

    JLabel         Lcompilecommand2;
    TextButton     compilecommand2;
    JLabel         Lruncommand2;
    TextButton     runcommand2;
    JLabel         Lguidesignercommand2;
    TextButton     guidesignercommand2;
    JLabel         Limportfiles2;
    TextButton      importfiles2;
    JLabel         Lprogramstartupcode2;
    TextButton      programstartupcode2;
    JLabel         Lnativehelpcommand2;
    TextButton     nativehelpcommand2;
    JPanel         properties2;

    JLabel         Lcompilecommand3;
    TextButton     compilecommand3;
    JLabel         Lruncommand3;
    TextButton     runcommand3;
    JLabel         Lguidesignercommand3;
    TextButton     guidesignercommand3;
    JLabel         Limportfiles3;
    TextButton      importfiles3;
    JLabel         Lprogramstartupcode3;
    TextButton      programstartupcode3;
    JLabel         Lnativehelpcommand3;
    TextButton     nativehelpcommand3;
    JPanel         properties3;

    JLabel         Lcompilecommand4;
    TextButton     compilecommand4;
    JLabel         Lruncommand4;
    TextButton     runcommand4;
    JLabel         Lguidesignercommand4;
    TextButton     guidesignercommand4;
    JLabel         Limportfiles4;
    TextButton      importfiles4;
    JLabel         Lprogramstartupcode4;
    TextButton      programstartupcode4;
    JLabel         Lnativehelpcommand4;
    TextButton      nativehelpcommand4;
    JPanel         properties4;

    JLabel         Lidf_localvariable5;
    TextButton   idf_localvariable5;
    JLabel         Lcompilecommand5;
    TextButton     compilecommand5;
    JLabel         Lruncommand5;
    TextButton     runcommand5;
    JLabel         Lguidesignercommand5;
    TextButton     guidesignercommand5;
    JLabel         Limportfiles5;
    TextButton      importfiles5;
    JLabel         Lprogramstartupcode5;
    TextButton      programstartupcode5;
    JLabel         Lnativehelpcommand5;
    TextButton     nativehelpcommand5;
    JPanel         properties5;

    JLabel         Lcompilecommand6;
    TextButton     compilecommand6;
    JLabel         Lruncommand6;
    TextButton     runcommand6;
    JLabel         Lguidesignercommand6;
    TextButton     guidesignercommand6;
    JLabel         Limportfiles6;
    TextButton      importfiles6;
    JLabel         Lprogramstartupcode6;
    TextButton      programstartupcode6;
    JLabel         Lnativehelpcommand6;
    TextButton     nativehelpcommand6;
    JPanel         properties6;

    JLabel         Lidf_localvariable7;
    TextButton   idf_localvariable7;
    JLabel         Lcompilecommand7;
    TextButton     compilecommand7;
    JLabel         Lruncommand7;
    TextButton     runcommand7;
    JLabel         Lguidesignercommand7;
    TextButton     guidesignercommand7;
    JLabel         Limportfiles7;
    TextButton      importfiles7;
    JLabel         Lprogramstartupcode7;
    TextButton      programstartupcode7;
    JLabel         Lnativehelpcommand7;
    TextButton     nativehelpcommand7;
    JPanel         properties7;

    JLabel         Lruncommand8;
    TextButton     runcommand8;
    JPanel         properties8;

    JLabel         Lcompilecommand9;
    TextButton     compilecommand9;
    JLabel         Lruncommand9;
    TextButton     runcommand9;
    JLabel         Lguidesignercommand9;
    TextButton     guidesignercommand9;
    JLabel         Limportfiles9;
    TextButton      importfiles9;
    JLabel         Lprogramstartupcode9;
    TextButton      programstartupcode9;
    JLabel         Lnativehelpcommand9;
    TextButton     nativehelpcommand9;
    JPanel         properties9;

    JTabbedPane    tproperties;
    
    JButton        yesbutton;
    JButton        nobutton;
    JButton        optbutton;
    JButton        rstbutton;

    JPanel         selectbuttons;
    JScrollPane    sx,s02,s12,s22,s32,s42,s52,s62,s72,s82,s92;

    PropertyWindow(){

      setTitle("プロジェクトのプロパティ");

      JTabbedPane tproperties = new JTabbedPane();

      viewsourceatcompile  = new JCheckBox("コンパイル時にソースファイルを開く",ViewSourceAtCompile);
      opencompiledialog    = new JCheckBox("コンパイル時にファイル選択ダイアログを開く",OpenCompileDialog);
      nooptimizepin       = new JCheckBox("Xオブジェクト展開時にピンを整理しない",NoOptimizePin);
      Ljavaeditcommand     = new JLabel("プログラムを編集するコマンド(外部のエディタを使用しないときは空にしておく)");
      javaeditcommand      = new JTextField(JavaEditCommand);
      Lscript_exec_command  = new JLabel("スクリプトを実行するコマンド");
      script_exec_command  = new JTextField(ScriptExecCommand);
      Limgeditcommand     = new JLabel("アイコンを編集するコマンド");
      imgeditcommand      = new JTextField(ImageEditCommand);
      Ljavaviewcommand     = new JLabel("コンパイル時にソースファイルを開くコマンド");
      javaviewcommand      = new JTextField(JavaViewCommand);
      Lhtmleditcommand     = new JLabel("アプレットのhtml文書を編集するコマンド");
      htmleditcommand      = new JTextField(HtmlEditCommand);
      Lhelpcommand         = new JLabel("ヘルプファイルを開くコマンド");
      helpcommand          = new JTextField(HelpCommand);
      Llookandfeels        = new JLabel("Look&Feel");
      lookandfeels         = new JComboBox();

      Lcompilecommand0     = new JLabel("コンパイラを起動するコマンド");
      compilecommand0      = new TextButton(CompileCommand[0]);
      Lruncommand0         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand0          = new TextButton(RunCommand[0]);
      Lguidesignercommand0 = new JLabel("GUIデザイナを起動するコマンド");
      guidesignercommand0  = new TextButton(GUIDesignerCommand[0]);
      Limportfiles0        = new JLabel("インポートするパッケージ & スタートアップコード");
      importfiles0         = new TextButton(ImportFiles[0]);
      Lprogramstartupcode0 = new JLabel("グローバル宣言など");
      programstartupcode0  = new TextButton(ProgramStartupCode[0]);
      Lnativehelpcommand0  = new JLabel("Javaのヘルプファイルを開くコマンド");
      nativehelpcommand0   = new TextButton(NativeHelpCommand[0]);

      Lcompilecommand1     = new JLabel("コンパイラを起動するコマンド");
      compilecommand1      = new TextButton(CompileCommand[1]);
      Lruncommand1         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand1          = new TextButton(RunCommand[1]);
      Lguidesignercommand1 = new JLabel("GUIデザイナを起動するコマンド");
      guidesignercommand1  = new TextButton(GUIDesignerCommand[1]);
      Limportfiles1        = new JLabel("インポートするパッケージ & スタートアップコード");
      importfiles1         = new TextButton(ImportFiles[1]);
      Lprogramstartupcode1 = new JLabel("グローバル宣言など");
      programstartupcode1  = new TextButton(ProgramStartupCode[1]);
      Lnativehelpcommand1  = new JLabel("アプレットのヘルプファイルを開くコマンド");
      nativehelpcommand1   = new TextButton(NativeHelpCommand[1]);

      Lcompilecommand2     = new JLabel("コンパイラを起動するコマンド");
      compilecommand2      = new TextButton(CompileCommand[2]);
      Lruncommand2         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand2          = new TextButton(RunCommand[2]);
      Lguidesignercommand2 = new JLabel("GUIデザイナを起動するコマンド");
      guidesignercommand2  = new TextButton(GUIDesignerCommand[2]);
      Limportfiles2        = new JLabel("インクルード宣言など");
      importfiles2         = new TextButton(ImportFiles[2]);
      Lprogramstartupcode2 = new JLabel("スタートアップコード");
      programstartupcode2  = new TextButton(ProgramStartupCode[2]);
      Lnativehelpcommand2  = new JLabel("C++のヘルプファイルを開くコマンド");
      nativehelpcommand2   = new TextButton(NativeHelpCommand[2]);

      Lcompilecommand3     = new JLabel("コンパイラを起動するコマンド");
      compilecommand3      = new TextButton(CompileCommand[3]);
      Lruncommand3         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand3          = new TextButton(RunCommand[3]);
      Lguidesignercommand3 = new JLabel("GUIデザイナを起動するコマンド");
      guidesignercommand3  = new TextButton(GUIDesignerCommand[3]);
      Limportfiles3        = new JLabel("インクルード宣言など");
      importfiles3         = new TextButton(ImportFiles[3]);
      Lprogramstartupcode3 = new JLabel("スタートアップコード");
      programstartupcode3  = new TextButton(ProgramStartupCode[3]);
      Lnativehelpcommand3  = new JLabel("C++のヘルプファイルを開くコマンド");
      nativehelpcommand3   = new TextButton(NativeHelpCommand[3]);

      Lcompilecommand4     = new JLabel("コンパイラを起動するコマンド");
      compilecommand4      = new TextButton(CompileCommand[4]);
      Lruncommand4         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand4          = new TextButton(RunCommand[4]);
      Lguidesignercommand4 = new JLabel("GUIデザイナを起動するコマンド");
      guidesignercommand4  = new TextButton(GUIDesignerCommand[4]);
      Limportfiles4        = new JLabel("インポートするパッケージ & スタートアップコード");
      importfiles4         = new TextButton(ImportFiles[4]);
      Lprogramstartupcode4 = new JLabel("グローバル宣言など");
      programstartupcode4  = new TextButton(ProgramStartupCode[4]);
      Lnativehelpcommand4  = new JLabel("androidマニフェスト");
      nativehelpcommand4   = new TextButton(NativeHelpCommand[4]);

      Lidf_localvariable5     = new JLabel("局所変数の識別子");
      idf_localvariable5     = new TextButton(IDF_LocalVariable[5]);
      Lcompilecommand5     = new JLabel("コンパイラを起動するコマンド");
      compilecommand5      = new TextButton(CompileCommand[5]);
      Lruncommand5         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand5          = new TextButton(RunCommand[5]);
      Lguidesignercommand5 = new JLabel("GUIデザイナを起動するコマンド");
      guidesignercommand5  = new TextButton(GUIDesignerCommand[5]);
      Limportfiles5        = new JLabel("変数宣言など");
      importfiles5         = new TextButton(ImportFiles[5]);
      Lprogramstartupcode5 = new JLabel("スタートアップコード");
      programstartupcode5  = new TextButton(ProgramStartupCode[5]);
      Lnativehelpcommand5  = new JLabel("Basicのヘルプファイルを開くコマンド");
      nativehelpcommand5   = new TextButton(NativeHelpCommand[5]);

      Lcompilecommand6     = new JLabel("コンパイラを起動するコマンド");
      compilecommand6      = new TextButton(CompileCommand[6]);
      Lruncommand6         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand6          = new TextButton(RunCommand[6]);
      Lguidesignercommand6 = new JLabel("GUIデザイナを起動するコマンド");
      guidesignercommand6  = new TextButton(GUIDesignerCommand[6]);
      Limportfiles6        = new JLabel("インクルード宣言など");
      importfiles6         = new TextButton(ImportFiles[6]);
      Lprogramstartupcode6 = new JLabel("スタートアップコード");
      programstartupcode6  = new TextButton(ProgramStartupCode[6]);
      Lnativehelpcommand6  = new JLabel("C言語のヘルプファイルを開くコマンド");
      nativehelpcommand6   = new TextButton(NativeHelpCommand[6]);

      Lidf_localvariable7     = new JLabel("局所変数の識別子");
      idf_localvariable7     = new TextButton(IDF_LocalVariable[7]);
      Lcompilecommand7     = new JLabel("コンパイラを起動するコマンド");
      compilecommand7      = new TextButton(CompileCommand[7]);
      Lruncommand7         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand7          = new TextButton(RunCommand[7]);
      Lguidesignercommand7 = new JLabel("GUIデザイナを起動するコマンド");
      guidesignercommand7  = new TextButton(GUIDesignerCommand[7]);
      Limportfiles7        = new JLabel("変数宣言など");
      importfiles7         = new TextButton(ImportFiles[7]);
      Lprogramstartupcode7 = new JLabel("スタートアップコード");
      programstartupcode7  = new TextButton(ProgramStartupCode[7]);
      Lnativehelpcommand7  = new JLabel("oregengo-Rのヘルプファイルを開くコマンド");
      nativehelpcommand7   = new TextButton(NativeHelpCommand[7]);

      Lruncommand8         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand8          = new TextButton(RunCommand[8]);

      Lcompilecommand9     = new JLabel("コンパイラを起動するコマンド");
      compilecommand9      = new TextButton(CompileCommand[6]);
      Lruncommand9         = new JLabel("作成したプログラムを起動するコマンド");
      runcommand9          = new TextButton(RunCommand[6]);
      Lguidesignercommand9 = new JLabel("GUIデザイナを起動するコマンド");
      guidesignercommand9  = new TextButton(GUIDesignerCommand[6]);
      Limportfiles9        = new JLabel("インクルード宣言など");
      importfiles9         = new TextButton(ImportFiles[6]);
      Lprogramstartupcode9 = new JLabel("スタートアップコード");
      programstartupcode9  = new TextButton(ProgramStartupCode[6]);
      Lnativehelpcommand9  = new JLabel("Javascriptのヘルプファイルを開くコマンド");
      nativehelpcommand9   = new TextButton(NativeHelpCommand[6]);

      properties = new JPanel();
      properties.setLayout(new BoxLayout( properties, BoxLayout.Y_AXIS) );
      properties.add(viewsourceatcompile);
      properties.add(opencompiledialog);
      properties.add(nooptimizepin);
      lookandfeels.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      properties.add(Llookandfeels);
      properties.add(lookandfeels);
      for( i = 0; i < UIManager.getInstalledLookAndFeels().length; i++){
        lookandfeels.addItem( (UIManager.getInstalledLookAndFeels())[i].getClassName() );
      }
      properties.add(Ljavaeditcommand);
      properties.add(javaeditcommand);
      properties.add(Lscript_exec_command);
      properties.add(script_exec_command);
      properties.add(Limgeditcommand);
      properties.add(imgeditcommand);
      properties.add(Ljavaviewcommand);
      properties.add(javaviewcommand);
      properties.add(Lhtmleditcommand);
      properties.add(htmleditcommand);
      properties.add(Lhelpcommand);
      properties.add(helpcommand);
      sx = new JScrollPane(properties);
//      sx.setPreferredSize(new Dimension(600,200) );

      properties0 = new JPanel();
      properties0.setLayout(new BoxLayout( properties0, BoxLayout.Y_AXIS) );
      properties0.add(Lcompilecommand0);
      properties0.add(compilecommand0);
      properties0.add(Lruncommand0);
      properties0.add(runcommand0);
      properties0.add(Lguidesignercommand0);
      properties0.add(guidesignercommand0);
      properties0.add(Lnativehelpcommand0);
      properties0.add(nativehelpcommand0);
      properties0.add(Limportfiles0);
      properties0.add(importfiles0);
      properties0.add( Lprogramstartupcode0);
      properties0.add( programstartupcode0);
      s02 = new JScrollPane(properties0);
//      s02.setPreferredSize(new Dimension(600,200) );

      properties1 = new JPanel();
      properties1.setLayout(new BoxLayout( properties1, BoxLayout.Y_AXIS) );
      properties1.add( Lcompilecommand1);
      properties1.add( compilecommand1);
      properties1.add( Lruncommand1);
      properties1.add( runcommand1);
      properties1.add( Lguidesignercommand1);
      properties1.add( guidesignercommand1);
      properties1.add(Lnativehelpcommand1);
      properties1.add(nativehelpcommand1);
      properties1.add( Limportfiles1);
      properties1.add( importfiles1);
      properties1.add( Lprogramstartupcode1);
      properties1.add( programstartupcode1);
      s12 = new JScrollPane(properties1);
//      s12.setPreferredSize(new Dimension(600,200) );

      properties2 = new JPanel();
      properties2.setLayout(new BoxLayout( properties2, BoxLayout.Y_AXIS) );
      properties2.add(Lcompilecommand2);
      properties2.add(compilecommand2);
      properties2.add(Lruncommand2);
      properties2.add(runcommand2);
      properties2.add(Lguidesignercommand2);
      properties2.add(guidesignercommand2);
      properties2.add(Lnativehelpcommand2);
      properties2.add(nativehelpcommand2);
      properties2.add(Limportfiles2);
      properties2.add(importfiles2);
      properties2.add( Lprogramstartupcode2);
      properties2.add( programstartupcode2);
      s22 = new JScrollPane(properties2);
//      s22.setPreferredSize(new Dimension(600,200) );

      properties3 = new JPanel();
      properties3.setLayout(new BoxLayout( properties3, BoxLayout.Y_AXIS) );
      properties3.add( Lcompilecommand3);
      properties3.add( compilecommand3);
      properties3.add( Lruncommand3);
      properties3.add( runcommand3);
      properties3.add( Lguidesignercommand3);
      properties3.add( guidesignercommand3);
      properties3.add(Lnativehelpcommand3);
      properties3.add(nativehelpcommand3);
      properties3.add( Limportfiles3);
      properties3.add( importfiles3);
      properties3.add( Lprogramstartupcode3);
      properties3.add( programstartupcode3);
      s32 = new JScrollPane(properties3);
//      s32.setPreferredSize(new Dimension(600,200) );

      properties4 = new JPanel();
      properties4.setLayout(new BoxLayout( properties4, BoxLayout.Y_AXIS) );
      properties4.add( Lcompilecommand4);
      properties4.add( compilecommand4);
      properties4.add( Lruncommand4);
      properties4.add( runcommand4);
      properties4.add( Lguidesignercommand4);
      properties4.add( guidesignercommand4);
      properties4.add(Lnativehelpcommand4);
      properties4.add(nativehelpcommand4);
      properties4.add( Limportfiles4);
      properties4.add( importfiles4);
      properties4.add( Lprogramstartupcode4);
      properties4.add( programstartupcode4);
      s42 = new JScrollPane(properties4);
//      s42.setPreferredSize(new Dimension(600,160) );

      properties5 = new JPanel();
      properties5.setLayout(new BoxLayout( properties5, BoxLayout.Y_AXIS) );
      properties5.add( Lidf_localvariable5);
      properties5.add( idf_localvariable5);
      properties5.add( Lcompilecommand5);
      properties5.add( compilecommand5);
      properties5.add( Lruncommand5);
      properties5.add( runcommand5);
      properties5.add( Lguidesignercommand5);
      properties5.add( guidesignercommand5);
      properties5.add(Lnativehelpcommand5);
      properties5.add(nativehelpcommand5);
      properties5.add( Limportfiles5);
      properties5.add( importfiles5);
      properties5.add( Lprogramstartupcode5);
      properties5.add( programstartupcode5);
      s52 = new JScrollPane(properties5);
//      s52.setPreferredSize(new Dimension(600,200) );

      properties6 = new JPanel();
      properties6.setLayout(new BoxLayout( properties6, BoxLayout.Y_AXIS) );
      properties6.add( Lcompilecommand6);
      properties6.add( compilecommand6);
      properties6.add( Lruncommand6);
      properties6.add( runcommand6);
      properties6.add( Lguidesignercommand6);
      properties6.add( guidesignercommand6);
      properties6.add(Lnativehelpcommand6);
      properties6.add(nativehelpcommand6);
      properties6.add( Limportfiles6);
      properties6.add( importfiles6);
      properties6.add( Lprogramstartupcode6);
      properties6.add( programstartupcode6);
      s62 = new JScrollPane(properties6);
//      s62.setPreferredSize(new Dimension(600,200) );

      properties7 = new JPanel();
      properties7.setLayout(new BoxLayout( properties7, BoxLayout.Y_AXIS) );
      properties7.add( Lidf_localvariable7);
      properties7.add( idf_localvariable7);
      properties7.add( Lcompilecommand7);
      properties7.add( compilecommand7);
      properties7.add( Lruncommand7);
      properties7.add( runcommand7);
      properties7.add( Lguidesignercommand7);
      properties7.add( guidesignercommand7);
      properties7.add(Lnativehelpcommand7);
      properties7.add(nativehelpcommand7);
      properties7.add( Limportfiles7);
      properties7.add( importfiles7);
      properties7.add( Lprogramstartupcode7);
      properties7.add( programstartupcode7);
      s72 = new JScrollPane(properties7);
//      s72.setPreferredSize(new Dimension(600,200) );

      properties8 = new JPanel();
      properties8.setLayout(new BoxLayout( properties8, BoxLayout.Y_AXIS) );
      properties8.add( Lruncommand8);
      properties8.add( runcommand8);
      s82 = new JScrollPane(properties8);

      properties9 = new JPanel();
      properties9.setLayout(new BoxLayout( properties9, BoxLayout.Y_AXIS) );
      properties9.add( Lcompilecommand9);
      properties9.add( compilecommand9);
      properties9.add( Lruncommand9);
      properties9.add( runcommand9);
      properties9.add( Lguidesignercommand9);
      properties9.add( guidesignercommand9);
      properties9.add(Lnativehelpcommand9);
      properties9.add(nativehelpcommand9);
      properties9.add( Limportfiles9);
      properties9.add( importfiles9);
      properties9.add( Lprogramstartupcode9);
      properties9.add( programstartupcode9);
      s92 = new JScrollPane(properties9);
//      s92.setPreferredSize(new Dimension(600,200) );

      yesbutton     = new JButton("OK");;
      yesbutton.setActionCommand("YES");
      yesbutton.addActionListener(this);
      nobutton      = new JButton("キャンセル");
      nobutton.setActionCommand("NO");
      nobutton.addActionListener(this);
      optbutton      = new JButton("デフォルトに設定");
      optbutton.setActionCommand("OPTION");
      optbutton.addActionListener(this);
      rstbutton      = new JButton("デフォルトに戻す");
      rstbutton.setActionCommand("RESET");
      rstbutton.addActionListener(this);
      selectbuttons = new JPanel();
      selectbuttons.setLayout( new FlowLayout() );
      selectbuttons.add(yesbutton);
      selectbuttons.add(nobutton);
      selectbuttons.add(rstbutton);
      selectbuttons.add(optbutton);

      tproperties.addTab("全般", sx);
      tproperties.addTab("Java", s02);
      tproperties.addTab("Applet", s12);
      tproperties.addTab("C++コンソール", s22);
      tproperties.addTab("C++Windows", s32);
      tproperties.addTab("android", s42);
      tproperties.addTab("Basic", s52);
      tproperties.addTab("C言語", s62);
      tproperties.addTab("oregengo-R", s72);
      tproperties.addTab("マルチ言語", s82);
      tproperties.addTab("Javascript", s92);
      getContentPane().add(tproperties, BorderLayout.CENTER);
      getContentPane().add(selectbuttons, BorderLayout.SOUTH);
      pack();
      if( PropWinWidth > 0 ) setBounds( PropWinx0, PropWiny0, PropWinWidth, PropWinHeight );
    }
     
    public void age(){
      recall();
      setVisible( true );
    }
    
    public void actionPerformed(ActionEvent e ){

      if( e.getActionCommand().equals("RESET") ){
        loadProperty();
        recall();
        return;
      }

      else if( !e.getActionCommand().equals("NO") ){
        ViewSourceAtCompile = viewsourceatcompile.isSelected();
        OpenCompileDialog = opencompiledialog.isSelected();
        NoOptimizePin = nooptimizepin.isSelected();
        JavaEditCommand = javaeditcommand.getText();
        ScriptExecCommand = script_exec_command.getText();
        ImageEditCommand = imgeditcommand.getText();
        JavaViewCommand = javaviewcommand.getText();
        HtmlEditCommand = htmleditcommand.getText();
        HelpCommand = helpcommand.getText();
        LookandFeel = (String)(lookandfeels.getSelectedItem());

        CompileCommand[0] = compilecommand0.get_text();
        RunCommand[0] = runcommand0.get_text();
        GUIDesignerCommand[0] = guidesignercommand0.get_text();
        ImportFiles[0] = importfiles0.get_text();
        ProgramStartupCode[0] = programstartupcode0.get_text();
        NativeHelpCommand[0] = nativehelpcommand0.getText();

        CompileCommand[1] = compilecommand1.get_text();
        RunCommand[1] = runcommand1.get_text();
        GUIDesignerCommand[1] = guidesignercommand1.get_text();
        ImportFiles[1] = importfiles1.get_text();
        ProgramStartupCode[1] = programstartupcode1.get_text();
        NativeHelpCommand[1] = nativehelpcommand1.get_text();

        CompileCommand[2] = compilecommand2.get_text();
        RunCommand[2] = runcommand2.get_text();
        GUIDesignerCommand[2] = guidesignercommand2.get_text();
        ImportFiles[2] = importfiles2.get_text();
        ProgramStartupCode[2] = programstartupcode2.get_text();
        NativeHelpCommand[2] = nativehelpcommand2.get_text();

        CompileCommand[3] = compilecommand3.get_text();
        RunCommand[3] = runcommand3.get_text();
        GUIDesignerCommand[3] = guidesignercommand3.get_text();
        ImportFiles[3] = importfiles3.get_text();
        ProgramStartupCode[3] = programstartupcode3.get_text();
        NativeHelpCommand[3] = nativehelpcommand3.get_text();

        CompileCommand[4] = compilecommand4.get_text();
        RunCommand[4] = runcommand4.get_text();
        GUIDesignerCommand[4] = guidesignercommand4.get_text();
        ImportFiles[4] = importfiles4.get_text();
        ProgramStartupCode[4] = programstartupcode4.get_text();
        NativeHelpCommand[4] = nativehelpcommand4.get_text();

        IDF_LocalVariable[5] = idf_localvariable5.get_text();
        CompileCommand[5] = compilecommand5.get_text();
        RunCommand[5] = runcommand5.get_text();
        GUIDesignerCommand[5] = guidesignercommand5.get_text();
        ImportFiles[5] = importfiles5.get_text();
        ProgramStartupCode[5] = programstartupcode5.get_text();
        NativeHelpCommand[5] = nativehelpcommand5.get_text();

        CompileCommand[6] = compilecommand6.get_text();
        RunCommand[6] = runcommand6.get_text();
        GUIDesignerCommand[6] = guidesignercommand6.get_text();
        ImportFiles[6] = importfiles6.get_text();
        ProgramStartupCode[6] = programstartupcode6.get_text();
        NativeHelpCommand[6] = nativehelpcommand6.get_text();

        IDF_LocalVariable[7] = idf_localvariable7.get_text();
        CompileCommand[7] = compilecommand7.get_text();
        RunCommand[7] = runcommand7.get_text();
        GUIDesignerCommand[7] = guidesignercommand7.get_text();
        ImportFiles[7] = importfiles7.get_text();
        ProgramStartupCode[7] = programstartupcode7.get_text();
        NativeHelpCommand[7] = nativehelpcommand7.get_text();

        RunCommand[8] = runcommand8.get_text();

        CompileCommand[9] = compilecommand9.get_text();
        RunCommand[9] = runcommand9.get_text();
        GUIDesignerCommand[9] = guidesignercommand9.get_text();
        ImportFiles[9] = importfiles9.get_text();
        ProgramStartupCode[9] = programstartupcode9.get_text();
        NativeHelpCommand[9] = nativehelpcommand9.get_text();

        restoreProperty();
        setlookandfeel();

        if( e.getActionCommand().equals("OPTION") ){
          restoreProperty();
          saveProperty();
        }
      }
      setVisible(false);
      treetool.changeNode( treetool.currentnode );

    }

    private void recall(){
      viewsourceatcompile.setSelected(ViewSourceAtCompile);
      opencompiledialog.setSelected(OpenCompileDialog);
      nooptimizepin.setSelected(NoOptimizePin);
      javaeditcommand.setText(JavaEditCommand);
      script_exec_command.setText(ScriptExecCommand);
      imgeditcommand.setText(ImageEditCommand);
      javaviewcommand.setText(JavaViewCommand);
      htmleditcommand.setText(HtmlEditCommand);
      helpcommand.setText(HelpCommand);
      for( i = 0; i < lookandfeels.getItemCount() && !( LookandFeel.equals((String)(lookandfeels.getItemAt(i)))) ; i++) ;
      lookandfeels.setSelectedIndex( (i >= lookandfeels.getItemCount())? 0 : i );

      compilecommand0.set_text(CompileCommand[0]);
      runcommand0.set_text(RunCommand[0]);
      guidesignercommand0.set_text(GUIDesignerCommand[0]);
      importfiles0.set_text(ImportFiles[0]);
      programstartupcode0.set_text(ProgramStartupCode[0]);
      nativehelpcommand0.set_text(NativeHelpCommand[0]);

      compilecommand1.set_text(CompileCommand[1]);
      runcommand1.set_text(RunCommand[1]);
      guidesignercommand1.set_text(GUIDesignerCommand[1]);
      importfiles1.set_text(ImportFiles[1]);
      programstartupcode1.set_text(ProgramStartupCode[1]);
      nativehelpcommand1.set_text(NativeHelpCommand[1]);

      compilecommand2.set_text(CompileCommand[2]);
      runcommand2.set_text(RunCommand[2]);
      guidesignercommand2.set_text(GUIDesignerCommand[2]);
      importfiles2.set_text(ImportFiles[2]);
      programstartupcode2.set_text(ProgramStartupCode[2]);
      nativehelpcommand2.set_text(NativeHelpCommand[2]);

      compilecommand3.set_text(CompileCommand[3]);
      runcommand3.set_text(RunCommand[3]);
      guidesignercommand3.set_text(GUIDesignerCommand[3]);
      importfiles3.set_text(ImportFiles[3]);
      programstartupcode3.set_text(ProgramStartupCode[3]);
      nativehelpcommand3.set_text(NativeHelpCommand[3]);

      compilecommand4.set_text(CompileCommand[4]);
      runcommand4.set_text(RunCommand[4]);
      guidesignercommand4.set_text(GUIDesignerCommand[4]);
      importfiles4.set_text(ImportFiles[4]);
      programstartupcode4.set_text(ProgramStartupCode[4]);
      nativehelpcommand4.set_text(NativeHelpCommand[4]);

      idf_localvariable5.set_text(IDF_LocalVariable[5]);
      compilecommand5.set_text(CompileCommand[5]);
      runcommand5.set_text(RunCommand[5]);
      guidesignercommand5.set_text(GUIDesignerCommand[5]);
      importfiles5.set_text(ImportFiles[5]);
      programstartupcode5.set_text(ProgramStartupCode[5]);
      nativehelpcommand5.set_text(NativeHelpCommand[5]);

      compilecommand6.set_text(CompileCommand[6]);
      runcommand6.set_text(RunCommand[6]);
      guidesignercommand6.set_text(GUIDesignerCommand[6]);
      importfiles6.set_text(ImportFiles[6]);
      programstartupcode6.set_text(ProgramStartupCode[6]);
      nativehelpcommand6.set_text(NativeHelpCommand[6]);

      idf_localvariable7.set_text(IDF_LocalVariable[7]);
      compilecommand7.set_text(CompileCommand[7]);
      runcommand7.set_text(RunCommand[7]);
      guidesignercommand7.set_text(GUIDesignerCommand[7]);
      importfiles7.set_text(ImportFiles[7]);
      programstartupcode7.set_text(ProgramStartupCode[7]);
      nativehelpcommand7.set_text(NativeHelpCommand[7]);

      runcommand8.set_text(RunCommand[8]);

      compilecommand9.set_text(CompileCommand[9]);
      runcommand9.set_text(RunCommand[9]);
      guidesignercommand9.set_text(GUIDesignerCommand[9]);
      importfiles9.set_text(ImportFiles[9]);
      programstartupcode9.set_text(ProgramStartupCode[9]);
      nativehelpcommand9.set_text(NativeHelpCommand[9]);

    }

  }//~PropertyWindow

  class MessageWindow extends JFrame implements ActionListener{

    JToolBar toolBar;
      JTextField cmdent;
      JButton    bexec;
      JButton    bclear;
      JButton    bstore;
      JButton    bcopy;

    JTextArea text;
    JScrollPane display;
  
    MessageWindow(){
      super("コンパイル･実行結果");
        //ツールバーを生成
        toolBar = new JToolBar();
        toolBar.setMargin( new Insets(1, 1, 1, 1 ) );


        cmdent = new JTextField(" ");
        cmdent.setToolTipText("ここにコマンドを入力しEnterキーを押すとコマンドを実行します");
        cmdent.setActionCommand("EXEC");
        cmdent.addActionListener(this);
        toolBar.add(cmdent);

        bexec = new JButton("実行", new ImageIcon("resources/run.jpg"));
        bexec.setActionCommand("EXEC");
        bexec.addActionListener(this);
        toolBar.add(bexec);

        bclear = new JButton("消去", new ImageIcon("resources/clrall.jpg"));
        bclear.setActionCommand("CLEAR");
        bclear.addActionListener(this);
        toolBar.add(bclear);

        bstore = new JButton("ファイルに保存", new ImageIcon("resources/savefile.jpg"));
        bstore.setActionCommand("SAVE");
        bstore.addActionListener(this);
        toolBar.add(bstore);

        bcopy = new JButton("クリップボードにコピー", new ImageIcon("resources/copy.jpg"));
        bcopy.setActionCommand("COPY");
        bcopy.addActionListener(this);
        toolBar.add(bcopy);

      text = new JTextArea("");
      display = new JScrollPane( text );
      display.setPreferredSize( new Dimension( 640, 100 ));
      getContentPane().add( toolBar, BorderLayout.NORTH);
      getContentPane().add( display, BorderLayout.CENTER );
      pack();
      setBounds( MesgWinx0, MesgWiny0, MesgWinWidth, MesgWinHeight );
    }

    // action event
    public void actionPerformed(ActionEvent e ){
      String cmd = e.getActionCommand();
      if( cmd.equals("EXEC") ){
        execcommand("コマンドを実行します\n", "コマンドを実行できません\n", cmdent.getText() );
      }
      
      else if( cmd.equals("CLEAR") ){
        clearText();
      }
 
      else if( cmd.equals("SAVE") ){
        XFile xf = new XFile("コンパイル･実行結果.txt");
        xf.Xdelete();
        xf.Xappend( text.getText() + "\n" );
      }
 
      else if( cmd.equals("COPY") ){
        text.selectAll();
        text.copy();
      }

    } 
      
    private void txtappend( String s ){
      text.append(s);
      text.setCaretPosition( text.getText().length() );
    }
    
    public void clearText(){
      text.setText("");
    }
    
    public void execcommand( String smsg, String emsg, String cmd ){
      Process p=null;
      setVisible(true);
      txtappend( smsg );
      try{
        if("".equals(ScriptExecCommand)){
          p = java.lang.Runtime.getRuntime().exec( cmd );
	    }
        else{
          if(FTmpTextFile.isFile() || FTmpTextFile.isDirectory() ) FTmpTextFile.Xdelete();
          FTmpTextFile.Xappend(cmd);
          p = java.lang.Runtime.getRuntime().exec(ScriptExecCommand+" "+TmpTextFile);
	    }
        IOPipe pip1 = new IOPipe( p.getErrorStream() );
        IOPipe pip2 = new IOPipe( p.getInputStream() );
        pip1.start(); 
        pip2.start(); 
        int ret = p.waitFor();
      } catch( Exception ie ){ messagewindow.txtappend( ie+"\n"+emsg ); }
    }

    // I/O Pipe
    class IOPipe extends Thread{
      BufferedReader br;
      String line;

      IOPipe(InputStream s ){
        super("PIP");
        br = new BufferedReader( new InputStreamReader( s ) );
      }

      public void run(){
        try{
          while ((line = br.readLine()) != null) { txtappend(line+"\n"); }
        } catch( IOException ie ){ }
      }

    }//~IOPipe

  }//~MessageWindow

  class FileWindow extends JFrame implements ActionListener{

    JToolBar toolBar;
          JButton bload;
          JButton bstore;
          JButton bdelete;

    JFileChooser chooser;


    FileWindow() {

        setTitle("部品棚");
        setDefaultCloseOperation( HIDE_ON_CLOSE );


        //ツールバーを生成
        toolBar = new JToolBar();
        toolBar.setMargin( new Insets(1, 1, 1, 1 ) );


        bload = new JButton("部品を取り出す", new ImageIcon("resources/openfile.jpg"));
        bload.setActionCommand("LOAD");
        bload.addActionListener(this);
        toolBar.add(bload);

        bstore = new JButton("部品を収納する", new ImageIcon("resources/savefile.jpg"));
        bstore.setActionCommand("SAVE");
        bstore.addActionListener(this);
        toolBar.add(bstore);

        bdelete = new JButton("部品を削除", new ImageIcon("resources/delete.jpg"));
        bdelete.setActionCommand("FDELETE");
        bdelete.addActionListener(this);
        toolBar.add(bdelete);

        chooser = new JFileChooser( ObjectLib[ApplicationType] );
        chooser.setFileView(new ImageFileView());
        chooser.setControlButtonsAreShown(false);

        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(chooser, BorderLayout.CENTER);
        pack();
        if( FileWinWidth > 0 ) setBounds( FileWinx0, FileWiny0, FileWinWidth, FileWinHeight );
        else setLocation( MesgWinx0, MesgWiny0 );

    }//~FileWindow()

    public void save( Object e, XFile xf2 ){
      if( xf2.isFile() || xf2.isDirectory() ){
        String fname = xf2.getName();
        int flg = dialog3.age( "  " + fname + "を上書きしますか？" );
        if( flg == 1 ) xml.要素を保存( e, xf2 );
        if( flg == 0 ){
          if( fname.endsWith( ".xml" ) )  fname = fname.substring( 0, fname.length()-4 );
          if( fname.length() == 0 ) fname = "~default";
          int fileno = 1;
          XFile xf3;
          while( ( xf3 = new XFile( xf2.getParentFile(), fname + "[" + (fileno++) + "].xml" ) ).isFile() || xf3.isDirectory() ) {}
          xml.要素を保存( e, xf3 );        
        }
      }
      else xml.要素を保存( e, xf2 );
    }

    // action event
    public void actionPerformed(ActionEvent e ){
      String cmd = e.getActionCommand();
      if( cmd.equals("LOAD") ){
        File f = chooser.getSelectedFile(); 
        if( f != null ){
          XFile xf = new XFile( f );
          if( objecteditor.gui.isVisible() ){
            objecteditor.LoadComponent( xf );
          }
          else if( stateeditor.gui.isVisible() ){
            stateeditor.LoadComponent( xf );
          }
        }
      }
      
      else if( cmd.equals("SAVE") ){
        if( objecteditor.gui.isVisible() ){
          objecteditor.SaveComponent();
        }
        else if( stateeditor.gui.isVisible() ){
          stateeditor.SaveComponent();
        }
        chooser.rescanCurrentDirectory();
      }
 
      else if( cmd.equals("FDELETE") ){
        File f = chooser.getSelectedFile();
        if( f != null ){
          ( new XFile( f ) ).Xdelete();
          chooser.rescanCurrentDirectory();
        }
      }

    } 
      
    class ImageFileView extends FileView {
      ImageIcon xobjIcon = new ImageIcon("resources/xobj.jpg");
      ImageIcon aobjIcon = new ImageIcon("resources/aobj.jpg");
      ImageIcon codeIcon = new ImageIcon("resources/codeclip.jpg");
      ImageIcon tranIcon = new ImageIcon("resources/operation.jpg");
      ImageIcon pinIcon  = new ImageIcon("resources/pin.jpg");
      ImageIcon gropIcon = new ImageIcon("resources/group.jpg");

      public String getName(File f) {
        return null; //let the L&F FileView figure this out
      }

      public String getDescription(File f) {
        if( ( new XFile( f ) ).isxml() ){
          Nxml x = new Nxml();
          Object e = x.新しい要素( x.ルート要素(), f, "001" );
          if( e == null ) return( null );
          String nm =  x.要素の名前( e );
          if( nm.equals("xobject") || nm.equals("aobject") ){
            return( getFirstLine( x.属性値( e, "description" ) ) );
          }
          else return( null );
        }
        else return( null );
      }

      public Boolean isTraversable(File f) {
        return null; //let the L&F FileView figure this out
      }

      public String getTypeDescription(File f) {
        return null;
      }

      public Icon getIcon(File f) {
        XFile x = new XFile( f );
        if( x.isxml() ){
          Icon icon = null;
          if ( x.isxobject() )        icon = xobjIcon;
          else if ( x.isaobject() )   icon = aobjIcon;
          else if ( x.iscodeclip() )  icon = codeIcon;
          else if ( x.isoperation() ) icon = tranIcon;
          else if ( x.ispin() )       icon = pinIcon;
          else if ( x.isKJgroup() )   icon = gropIcon;
          return icon;
        }
        else return( null );
      }
  
    }//~ImageFileView


  }//~FileWindow

  class TextEditor extends JDialog implements ActionListener, WindowListener{
    JFileChooser chooser;
  
    JToolBar toolBar;
      JButton    bload;
      JButton    bstore;
      JButton    bcut;
      JButton    bcopy;
      JButton    bpaste;
      JButton    bhelp;

    JScrollPane display;
      JTextArea text;

    TextEditor(){
      setTitle("コードエディタ");
      setModal( true );
        //ツールバーを生成
        toolBar = new JToolBar();
        toolBar.setMargin( new Insets(1, 1, 1, 1 ) );

        bload = new JButton( new ImageIcon("resources/openfile.jpg"));
        bload.setToolTipText("開く");
        bload.setActionCommand("LOAD");
        bload.addActionListener(this);
        toolBar.add(bload);

        bstore = new JButton( new ImageIcon("resources/savefile.jpg"));
        bstore.setToolTipText("保存");
        bstore.setActionCommand("SAVE");
        bstore.addActionListener(this);
        toolBar.add(bstore);
        
        toolBar.addSeparator();

        bcut = new JButton( new ImageIcon("resources/cut.jpg"));
        bcut.setToolTipText("切取り");
        bcut.setActionCommand("CUT");
        bcut.addActionListener(this);
        toolBar.add(bcut);

        bcopy = new JButton( new ImageIcon("resources/copy.jpg"));
        bcopy.setToolTipText("コピー");
        bcopy.setActionCommand("COPY");
        bcopy.addActionListener(this);
        toolBar.add(bcopy);

        bpaste = new JButton( new ImageIcon("resources/paste.jpg"));
        bpaste.setToolTipText("貼付け");
        bpaste.setActionCommand("PASTE");
        bpaste.addActionListener(this);
        toolBar.add(bpaste);

        toolBar.addSeparator();

        bhelp = new JButton( new ImageIcon("resources/help.jpg"));
        bhelp.setToolTipText("言語のヘルプ");
        bhelp.setActionCommand("HELP");
        bhelp.addActionListener(this);
        toolBar.add(bhelp);

      text = new JTextArea("");
      display = new JScrollPane( text );
      display.setPreferredSize( new Dimension( 400, 320 ));
      getContentPane().add( toolBar, BorderLayout.NORTH);
      getContentPane().add( display, BorderLayout.CENTER );
      pack();
      if( EditWinWidth > 0 ) setBounds( EditWinx0, EditWiny0, EditWinWidth, EditWinHeight );
      setVisible(false);
    }

    // start
    public String start( String s ){
      text.setText( s );
      show();
      return( text.getText() );
    }

    // action event
    public void actionPerformed(ActionEvent e ){
      chooser = new JFileChooser(".");
      String cmd = e.getActionCommand();
      if( cmd.equals("LOAD") ){
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ){
          try{
            BufferedReader din = new BufferedReader( new FileReader( chooser.getSelectedFile() ) );
            String line;
            text.setText("");
            while( (line=din.readLine()) != null ){
              text.append( line + "\n" );
            }
            din.close();
          } catch( IOException ie ){ System.out.println("読みだしエラー"); }
        }
      }
      
      else if( cmd.equals("SAVE") ){
        if( chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION ){
          XFile xf = new XFile( chooser.getSelectedFile() );
          xf.Xdelete();
          xf.Xappend( text.getText() );
        }
      }
 
      else if( cmd.equals("CUT") ){
        text.cut();
      }

      if( cmd.equals("COPY") ){
        text.copy();
      }
      
      else if( cmd.equals("PASTE") ){
        text.paste();
      }
 
      else if( cmd.equals("HELP") ){
        execute( NativeHelpCommand[ApplicationType], false );
      }

    } 
      
    // window event
    public void windowClosing(WindowEvent e)    { hide();}
    public void windowActivated(WindowEvent e)  {   }
    public void windowClosed(WindowEvent e)     {   }
    public void windowDeactivated(WindowEvent e){   }
    public void windowDeiconified(WindowEvent e){   }
    public void windowIconified(WindowEvent e)  {   }
    public void windowOpened(WindowEvent e)     {   }

  }//~TextEditor

  class TextButton extends JButton implements ActionListener{
    String text;

    TextButton(){
      text = "";
      setText("     ");
      setHorizontalAlignment(SwingConstants.LEFT);
      setVerticalAlignment(SwingConstants.TOP);
      setBackground( Color.white );
      addActionListener(this);
    }

    TextButton(String s){
      set_text(s);
      setHorizontalAlignment(SwingConstants.LEFT);
      setVerticalAlignment(SwingConstants.TOP);
      setBackground( Color.white );
      addActionListener(this);
    }

    // 文字列を得る
    public String get_text(){
      return text;
    }

    // 文字列をセットする
    public void set_text(String s){
      text = s;
      String ss = getFirstLine(s);
      if(ss.length() < 40){
        ss = (ss+"                                        ").substring(0,40);
      }
      setText(ss);
    }

    // 編集する
    public void edit(){
      if( JavaEditCommand.equals("") ){ set_text( texteditor.start(text) ); }
      else{
        if( FTmpTextFile.isFile() || FTmpTextFile.isDirectory() ) FTmpTextFile.Xdelete();
        FTmpTextFile.Xappend(text);
        execute( JavaEditCommand+" "+TmpTextFile, true );
        try{
          BufferedReader din = new BufferedReader( new FileReader( FTmpTextFile ) );
          String line;
          String code = "";
          while((line=din.readLine())!=null){
            code = code + line + "\n";
          }
          din.close();
          set_text(code);
        } catch( IOException ie ){ reportError("編集できません．\n"); }
      }
    } 

    public void actionPerformed( ActionEvent e ){
      edit();
    }
  }//~TextButton

  class TreeTool extends JPanel implements ActionListener{
    Thread     mythread = null;
    boolean    stopflg = true;
    JTextField serchtext;
    JButton    serchbutton; 
    JTree tree;
    Xnode top;
    DefaultMutableTreeNode currentnode;
    DefaultTreeModel treemodel;
    PrintTool printtool;

    TreeTool( Object element ) {
// System.out.println( element );
      setLayout( new BorderLayout() );
      top = new Xnode( element );
      currentnode = top;
      treemodel = new DefaultTreeModel(top);

      tree = new JTree(treemodel);
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      tree.putClientProperty("JTree.lineStyle", "Angled");

      tree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          changeNode( (DefaultMutableTreeNode)tree.getLastSelectedPathComponent() );
        }
      });

      tree.setCellRenderer(new MyRenderer());


      JToolBar toolbar = new JToolBar();
      toolbar.add( serchtext = new JTextField(), BorderLayout.WEST);
      serchtext.setActionCommand("START");
      serchtext.addActionListener(this);
      toolbar.add( serchbutton = new JButton("次を検索"), BorderLayout.EAST);
      serchbutton.setActionCommand("CONTINUE");
      serchbutton.addActionListener(this);
      JPanel xview = new JPanel( new BorderLayout() );
      xview.add(toolbar, BorderLayout.NORTH);
      xview.add(tree, BorderLayout.CENTER);
      JScrollPane treeView = new JScrollPane(xview);
      Dimension minimumSize = new Dimension(1, 1);
      treeView.setMinimumSize(minimumSize);
      add(treeView, BorderLayout.CENTER);
      printtool = new PrintTool();
    }

    public void removeNode( DefaultMutableTreeNode node ){
      treemodel.removeNodeFromParent( node );
    }

    public void addNode( DefaultMutableTreeNode child, DefaultMutableTreeNode parent ){
      treemodel.insertNodeInto( child, parent, parent.getChildCount() );
      tree.scrollPathToVisible( new TreePath( child.getPath() ) );
    }


    public void changeParent(){
      changeNode( (DefaultMutableTreeNode)( currentnode.getParent() ) );
    }
    
    public void changeNode( DefaultMutableTreeNode newnode ){ //存在しないオブジェクトへのトラップ
        if( newnode == null ) return;
        if( ( newnode instanceof Xnode ) && ( !xml.要素の名前( ((Xnode)newnode).element ).equals("xobject") ) ) return;
        if( ( newnode instanceof Anode ) && ( !xml.要素の名前( ((Anode)newnode).element ).equals("aobject") ) ) return;

        DefaultMutableTreeNode bnode = currentnode; //ひとつ前のノード
        
        if( currentnode instanceof Xnode ){
          objecteditor.Logout();
        }
        else if( currentnode instanceof Anode ){
          stateeditor.Logout();
        }
        else{}

        if( (currentnode instanceof Xnode) && (newnode instanceof Anode)){
          stateeditor.gui.display.setLeftComponent(this);
          stateeditor.gui.setVisible(true);
          objecteditor.gui.setVisible(false);
        }
        else if( (currentnode instanceof Anode) && (newnode instanceof Xnode)){
          objecteditor.gui.display.setLeftComponent(this);
          objecteditor.gui.setVisible(true);
          stateeditor.gui.setVisible(false);
        }
        
        currentnode = newnode;

        if( currentnode instanceof Xnode ){
          objecteditor.Login((Xnode)currentnode);
        }
        else if( currentnode instanceof Anode ){
          stateeditor.Login((Anode)currentnode);
        }
        else{}
        tree.setSelectionPath( new TreePath( currentnode.getPath() ) );
        mae_node = bnode;  //ひとつ前のノードを保存

    }

//  ピン配置を最適化する
    public void optpin(){
      if( currentnode instanceof Xnode ){
        Object here = ((Xnode)currentnode).element;
        objecteditor.Logout();

        Vector pins = null, signals = null, esignals = null;
        int outlink = 0, inlink = 0, extoutlink = 0, extinlink = 0;
        Object rel1 = null, rel2 = null;

        String here_id = xml.要素のID( here );
        pins = xml.子要素のリスト( here, "pin" );
        if( currentnode != top ) esignals = xml.子要素のリスト( xml.親要素(here), "relation" );
        for( int i = pins.size()-1; i >= 0; i-- ){
          signals = xml.子要素のリスト( here, "relation" );
          inlink = outlink = extinlink = extoutlink = 0;
          String pin_id = xml.要素のID( pins.get(i) );
          for( int j = signals.size()-1; j >= 0; j-- ){
            if( xml.属性値( signals.get(j), "pin1name" ).equals( pin_id ) ){
              rel1 = signals.get(j);
              outlink++;
            }
            if( xml.属性値( signals.get(j), "pin2name" ).equals( pin_id ) ){
              rel2 = signals.get(j);
              inlink++;
            }
          }
          if( currentnode != top ){
            String ep = here_id + "(" + pin_id + ")";
            for( int j = esignals.size()-1; j >= 0; j-- ){
              if( xml.属性値( esignals.get(j), "pin1name" ).equals( ep ) ) extoutlink++;
              if( xml.属性値( esignals.get(j), "pin2name" ).equals( ep ) ) extinlink++;
            }
          }
          if( outlink==0 && inlink==0 && extoutlink==0 && extinlink==0 && !pin_id.equals( "_PSTART" ) ){
            xml.要素を削除( pins.get(i) );
          }
          if( outlink==1 && inlink==1 && extoutlink==0 && extinlink==0 ){
            xml.要素を削除( pins.get(i) );
            xml.属性値をセット( rel1, "pin1name", xml.属性値( rel2, "pin1name" ) );
            xml.要素を削除( rel2 );
          }
        }

        objecteditor.Login( (Xnode)currentnode );
      }
    }

//  uobjectをxobjectに変換する
    public void utox( Object uobj0 ){
      if( currentnode instanceof Xnode ){
        Object here = ((Xnode)currentnode).element;
        objecteditor.Logout();

        String uid  = xml.要素のID( uobj0 );

        Object xobj, uobj, inpin, outpin, inrel=null, outrel=null;
        int inlink = 0, outlink = 0;

        xobj = xml.新しい要素( here, "xobject", "_X" + uid );
        xml.属性値をセット( xobj, "x0", xml.属性値( uobj0, "x0" ) );
        xml.属性値をセット( xobj, "y0", xml.属性値( uobj0, "y0" ) );
        xml.属性値をセット( xobj, "width", xml.属性値( uobj0, "width" ) );
        xml.属性値をセット( xobj, "height", xml.属性値( uobj0, "height" ) );
        xml.属性値をセット( xobj, "ID_maker", "1" );
        xml.属性値をセット( xobj, "objectname", "Object" + uid );
        xml.属性値をセット( xobj, "description", xml.属性値( uobj0, "description" ) );

        uobj = xml.新しい要素( xobj, uobj0, "_OX" );
        xml.属性値をセット( uobj, "x0", "120");
        xml.属性値をセット( uobj, "y0", "10");

        Vector signals = xml.子要素のリスト( here, "relation" );
        for( int j = signals.size()-1; j >= 0; j-- ){
          if( xml.属性値( signals.get(j), "pin1name" ).equals( uid ) ){
            outlink++;
            xml.属性値をセット( signals.get(j), "pin1name", "_X" + uid + "(_Pout)" );  
          } 
          if( xml.属性値( signals.get(j), "pin2name" ).equals( uid ) ){
            inlink++;
            xml.属性値をセット( signals.get(j), "pin2name", "_X" + uid + "(_Pin)" );  
          }
        }
        if( outlink > 0 ){ 
          outpin = xml.新しい要素( xobj, "pin", "_Pout" );
          xml.属性値をセット( outpin, "x0", "0" );
          xml.属性値をセット( outpin, "y0", "" + ( parseInt( xml.属性値( uobj0, "outpiny0" ) ) + 10 ) );
          xml.属性値をセット( outpin, "width", xml.属性値( uobj0, "outpinwidth" ) );
          xml.属性値をセット( outpin, "height", xml.属性値( uobj0, "outpinheight" ) );
          xml.属性値をセット( outpin, "text", xml.属性値( uobj0, "outpintext" ) );
          xml.属性値をセット( outpin, "px", xml.属性値( uobj0, "outpinx0" ) );
          xml.属性値をセット( outpin, "py", xml.属性値( uobj0, "outpiny0" ) );
          outrel = xml.新しい要素( xobj, "relation", "_Rout" );
          xml.属性値をセット( outrel, "pin1name", "_OX" );
          xml.属性値をセット( outrel, "pin2name", "_Pout" );
        }
        if( inlink > 0 ){ 
          inpin = xml.新しい要素( xobj, "pin", "_Pin" );
          xml.属性値をセット( inpin, "x0", "0" );
          xml.属性値をセット( inpin, "y0", "" + ( parseInt( xml.属性値( uobj0, "inpiny0" ) ) + 10 ) );
          xml.属性値をセット( inpin, "width", xml.属性値( uobj0, "inpinwidth" ) );
          xml.属性値をセット( inpin, "height", xml.属性値( uobj0, "inpinheight" ) );
          xml.属性値をセット( inpin, "text", xml.属性値( uobj0, "inpintext" ) );
          xml.属性値をセット( inpin, "px", xml.属性値( uobj0, "inpinx0" ) );
          xml.属性値をセット( inpin, "py", xml.属性値( uobj0, "inpiny0" ) );
          inrel = xml.新しい要素( xobj, "relation", "_Rin" );
          xml.属性値をセット( inrel, "pin1name", "_Pin" );
          xml.属性値をセット( inrel, "pin2name", "_OX" );
        }

        xml.要素を削除( uobj0 );

        addNode( new Xnode( xobj ), (Xnode)currentnode );
        objecteditor.Login( (Xnode)currentnode );
      }
    }

//  KJgroupをxobjectに変換する
    public void grouptox( Object group ){
      if( currentnode instanceof Xnode ){
        Object here = ((Xnode)currentnode).element;
        objecteditor.Logout();

        Vector incomps0 = new Vector();
        Vector incomps = new Vector();
        Vector links0 = new Vector();
        Vector links = new Vector();

// xobjectを生成( rectangleは同一、 名前はコメントと同一 )
        String gid  = xml.要素のID( group );
        int ID_maker = parseInt( xml.属性値( here, "ID_maker" ) );
        Object xobj = xml.新しい要素( here, "xobject", "_X" + gid );

        xml.属性値をセット( xobj, "x0", xml.属性値( group, "x0" ) );
        xml.属性値をセット( xobj, "y0", xml.属性値( group, "y0" ) );
        xml.属性値をセット( xobj, "width", xml.属性値( group, "width" ) );
        xml.属性値をセット( xobj, "height", xml.属性値( group, "height" ) );
        xml.属性値をセット( xobj, "objectname", xml.属性値( group, "comment" ) );
        xml.属性値をセット( xobj, "description", "" );

        // KJgroup内のコンポーネントをxobject内にコピー
        int  x0 = parseInt( xml.属性値( group, "x0" ) );
        int  y0 = parseInt( xml.属性値( group, "y0" ) );
        int  width  = parseInt( xml.属性値( group, "width" ) );
        int  height = parseInt( xml.属性値( group, "height" ) );
        Vector comps = xml.子要素のリスト( here );
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );

          // コンポーネント: xobject, aobject, uobject, pin, codeclip, group,ImgIcon
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "KJgroup" ) ){
            Object c = comps.get(j);
            int x =  parseInt( xml.属性値( c, "x0" ) );
            int y =  parseInt( xml.属性値( c, "y0" ) );
            if( ( x > x0 ) && ( x < x0 + width ) && ( y > y0 ) && ( y < y0 + height ) ){
              Object c2 = xml.新しい要素( xobj, c, xml.要素のID( c ) );
              incomps0.add( c );
              incomps.add( c2 );
              if( cname.equals( "pin" ) ){
                xml.属性値をセット( c2, "px", ""+ ( x - x0 ) );
                xml.属性値をセット( c2, "py", ""+ ( y - y0 ) );
              }
            }
          }
        }


//KJgroup内のrelationをxobject内にコピー(接続元＆接続先がグループ内にある場合)
        Vector rels = xml.子要素のリスト( here, "relation" );
        for( int j = rels.size()-1; j >= 0; j-- ){
          Object r = rels.get(j);
          String p1 = xml.属性値( r, "pin1name" );
          if( p1.indexOf( '(' ) > 0 ) p1 = getbase( p1 ); 
          Object c1 = xml.子要素( here, p1 ); 
          String p2 = xml.属性値( r, "pin2name" );
          if( p2.indexOf( '(' ) > 0 ) p2 = getbase( p2 ); 
          Object c2 = xml.子要素( here, p2 );
          if( incomps0.contains( c1 ) && incomps0.contains( c2 ) ){
            links0.add( r );
            links.add( xml.新しい要素( xobj, r, xml.要素のID( r ) ) );
          } 
        }

// 一方がグループ内でもう一方がグループ外にリンクしているrelationを探す
        for( int j = rels.size()-1; j >= 0; j-- ){
          Object r = rels.get(j);
          String p1 = xml.属性値( r, "pin1name" );
          String q1 = p1;
          if( p1.indexOf( '(' ) >= 0 ) p1 = getbase( p1 ); 
          Object c1 = xml.子要素( here, p1 ); 
          String p2 = xml.属性値( r, "pin2name" );
          String q2 = p2;
          if( p2.indexOf( '(' ) >= 0 ) p2 = getbase( p2 ); 
          Object c2 = xml.子要素( here, p2 );
          if( ( incomps0.contains( c1 ) && ( !incomps0.contains( c2 ) ) ) ||
              ( ( !incomps0.contains( c1 ) ) && incomps0.contains( c2 ) ) ){

            boolean reverse = false;
            if( ( !incomps0.contains( c1 ) ) && incomps0.contains( c2 ) ){
              reverse = true;
              Object t = c1;
              c1 = c2;
              c2 = t;
              String s = q1;
              q1 = q2;
              q2 = s;
            }

// pinの場合はダイレクトにリンクさせる
            String cname1 = xml.要素の名前( c1 );
            if( cname1.equals( "pin" ) ){
              if( reverse ) xml.属性値をセット( r, "pin2name", "_X" + gid + "(" + xml.要素のID( c1 ) + ")" );
              else          xml.属性値をセット( r, "pin1name", "_X" + gid + "(" + xml.要素のID( c1 ) + ")" );
            }

// xobject, aobject, uobjectのときは新規に pin&relationを生成して中継させる
            else{

              String pintext = null;

              int c1x = 0, c1y = 0, c1w = 0, c1h = 0;
              int c2x = 0, c2y = 0, c2w = 0, c2h = 0;
  
              int gx = parseInt( xml.属性値( group, "x0" ) );
              int gy = parseInt( xml.属性値( group, "y0" ) );
              int gw = parseInt( xml.属性値( group, "width" ) );
              int gh = parseInt( xml.属性値( group, "height" ) );

              if( cname1.equals( "xobject" ) || cname1.equals( "aobject" ) ){
                Object cp = xml.子要素( c1, getsubscript( q1 ) );
                pintext = xml.属性値( cp, "text" );
                c1x = parseInt( xml.属性値( c1, "x0" ) ) + parseInt( xml.属性値( cp, "px" ) );
                c1y = parseInt( xml.属性値( c1, "y0" ) ) + parseInt( xml.属性値( cp, "py" ) );
                c1w = 16 * pintext.length();
                c1h = 16;
              }
              else if( cname1.equals( "operation" ) ){
                if( reverse ){
                  pintext = xml.属性値( c1, "inpintext" );
                  c1x = parseInt( xml.属性値( c1, "x0" ) ) + parseInt( xml.属性値( c1, "inpinx0" ) );
                  c1y = parseInt( xml.属性値( c1, "y0" ) ) + parseInt( xml.属性値( c1, "inpiny0" ) );
                  c1w = parseInt( xml.属性値( c1, "inpinwidth" ) );
                  c1h = parseInt( xml.属性値( c1, "inpinheight" ) );
                }
                else{
                  pintext = xml.属性値( c1, "outpintext" );
                  c1x = parseInt( xml.属性値( c1, "x0" ) ) + parseInt( xml.属性値( c1, "outpinx0" ) );
                  c1y = parseInt( xml.属性値( c1, "y0" ) ) + parseInt( xml.属性値( c1, "outpiny0" ) );
                  c1w = parseInt( xml.属性値( c1, "outpinwidth" ) );
                  c1h = parseInt( xml.属性値( c1, "outpinheight" ) );
                }
              }

              String cname2 = xml.要素の名前( c2 );
              if( cname2.equals( "pin" ) ){
                c2x = parseInt( xml.属性値( c2, "x0" ) );
                c2y = parseInt( xml.属性値( c2, "y0" ) );
                c2w = parseInt( xml.属性値( c2, "width" ) );
                c2h = parseInt( xml.属性値( c2, "height" ) );
              }
              else if( cname2.equals( "xobject" ) || cname2.equals( "aobject" ) ){
                Object cp = xml.子要素( c2, getsubscript( q2 ) );
                c2x = parseInt( xml.属性値( c2, "x0" ) ) + parseInt( xml.属性値( cp, "px" ) );
                c2y = parseInt( xml.属性値( c2, "y0" ) ) + parseInt( xml.属性値( cp, "py" ) );
                c2w = 16 * xml.属性値( cp, "text" ).length();
                c2h = 16;
              }
              else if( cname2.equals( "operation" ) ){
                if( reverse ){
                  c2x = parseInt( xml.属性値( c2, "x0" ) ) + parseInt( xml.属性値( c2, "outpinx0" ) );
                  c2y = parseInt( xml.属性値( c2, "y0" ) ) + parseInt( xml.属性値( c2, "outpiny0" ) );
                  c2w = parseInt( xml.属性値( c2, "outpinwidth" ) );
                  c2h = parseInt( xml.属性値( c2, "outpinheight" ) );
                }
                else{
                  c2x = parseInt( xml.属性値( c2, "x0" ) ) + parseInt( xml.属性値( c2, "inpinx0" ) );
                  c2y = parseInt( xml.属性値( c2, "y0" ) ) + parseInt( xml.属性値( c2, "intpiny0" ) );
                  c2w = parseInt( xml.属性値( c2, "inpinwidth" ) );
                  c2h = parseInt( xml.属性値( c2, "intpinheight" ) );
                }
              }

              int xx = c2x;
              int yy = c2y;
              int px = c1x - gx;
              int py = c1y - gy;

              Object pinX  = xml.新しい要素( xobj, "pin", "_P" + ID_maker );
              xml.属性値をセット( pinX, "text", "pin" + ID_maker + "(" + getsubscript( pintext ) + ")" );
              xml.属性値をセット( pinX, "x0", "" + xx );
              xml.属性値をセット( pinX, "y0", "" + yy );
              xml.属性値をセット( pinX, "width", "80" );
              xml.属性値をセット( pinX, "height", "24" );
              xml.属性値をセット( pinX, "px", "" + px );
              xml.属性値をセット( pinX, "py", "" + py );
              if( reverse ){
                Object relX  = xml.新しい要素( xobj, "relation", "_R" + ( ID_maker + 1 ) );
                xml.属性値をセット( relX, "pin2name", xml.属性値( r, "pin2name" ) );
                xml.属性値をセット( relX, "pin1name", "_P" + ID_maker );
                xml.属性値をセット( r, "pin2name", "_X" + gid + "(_P" + ID_maker + ")" );
              }
              else{
                Object relX  = xml.新しい要素( xobj, "relation", "_R" + ( ID_maker + 1 ) );
                xml.属性値をセット( relX, "pin1name", xml.属性値( r, "pin1name" ) );
                xml.属性値をセット( relX, "pin2name", "_P" + ID_maker );
                xml.属性値をセット( r, "pin1name", "_X" + gid + "(_P" + ID_maker + ")" );
              }
              ID_maker += 2;
            } 
          }
        }



// 内部コンポーネントの座標を変換する
        incomps = xml.子要素のリスト( xobj );
        int  ox = -1, oy = -1; //座標変換のオフセット
        for( int j = incomps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( incomps.get(j) );
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "KJgroup" ) ) {
            Object c = incomps.get(j);
            int x =  parseInt( xml.属性値( c, "x0" ) );
            int y =  parseInt( xml.属性値( c, "y0" ) );
            if( ( ox < 0 ) || ( ox > x  ) ) ox = x;
            if( ( oy < 0 ) || ( oy > y  ) ) oy = y;
          }
        }
        for( int j = incomps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( incomps.get(j) );
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "KJgroup" ) ) {
            Object c = incomps.get(j);
            xml.属性値をセット( c, "x0", "" + ( parseInt( xml.属性値( c, "x0" ) ) - ox + 20 ) );
            xml.属性値をセット( c, "y0", "" + ( parseInt( xml.属性値( c, "y0" ) ) - oy + 20 ) );
          }
        }

//　グループ＆グループ内のコンポーネントを削除
        xml.要素を削除( group );
        for( int j = incomps0.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( incomps0.get(j) );
          if( cname.equals( "xobject" ) ){
            XnodeOf( incomps.get(j) ).suicide();
          }
          if( cname.equals( "aobject" ) ){
            AnodeOf( incomps.get(j) ).suicide();
          }
          xml.要素を削除( incomps0.get(j) );
        }
        for( int j = links0.size()-1; j >= 0; j-- ){
          xml.要素を削除( links0.get(j) );
        }


        xml.属性値をセット( xobj, "ID_maker", "" + ID_maker  );

        addNode( new Xnode( xobj ), (Xnode)currentnode );
        objecteditor.Login( (Xnode)currentnode );
      }
    }

    private Xnode XnodeOf( Object xobj ){
      String xid = xml.要素のID( xobj );
      Enumeration enum0 = currentnode.children();
      while( enum0.hasMoreElements() ){
        DefaultMutableTreeNode c = (DefaultMutableTreeNode)enum0.nextElement();
        if( ( c instanceof Xnode ) && ( xml.要素のID( ((Xnode)c).element ).equals( xid ) ) ) return( (Xnode)c );
      }
      return( null );
    }

    private Anode AnodeOf( Object aobj ){
      String aid = xml.要素のID( aobj );
      Enumeration enum0 = currentnode.children();
      while( enum0.hasMoreElements() ){
        DefaultMutableTreeNode c = (DefaultMutableTreeNode)enum0.nextElement();
        if( ( c instanceof Anode ) && ( xml.要素のID( ((Anode)c).element ).equals( aid ) ) ) return( (Anode)c );
      }
      return( null );
    }

//  xobjectをKJgroupに変換する
    public void xtogroup( Xnode xnode ){
      if( currentnode instanceof Xnode ){
        Object here = ((Xnode)currentnode).element;
        objecteditor.Logout();

        Object xobj = xnode.element;
        int ID_maker = parseInt( xml.属性値( here, "ID_maker" ) );
        String gid = "_G" + ID_maker++;
        String xid = xml.要素のID( xobj );


// xobject内の画面の大きさを求める
        int ex0 = -1, ey0 = -1, ex1 = -1, ey1 = -1;
        Vector xcomps = xml.子要素のリスト( xobj );
        for( int j = xcomps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( xcomps.get(j) );
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "KJgroup" ) ) {
            Object c = xcomps.get(j);
            int x =  parseInt( xml.属性値( c, "x0" ) );
            int y =  parseInt( xml.属性値( c, "y0" ) );
            int w =  parseInt( xml.属性値( c, "width" ) );
            int h =  parseInt( xml.属性値( c, "height" ) );
            if( ( ex0 < 0 ) || ( ex0 > x  ) ) ex0 = x;
            if( ( ey0 < 0 ) || ( ey0 > y  ) ) ey0 = y;
            if( ( ex1 < 0 ) || ( ex1 < x + w ) ) ex1 = x + w;
            if( ( ey1 < 0 ) || ( ey1 < y + h ) ) ey1 = y + h;
          }
        }

        int wid = parseInt( xml.属性値( xobj, "width" ) );
        int wid2 = ex1 - ex0 + 20;
        if( wid < wid2 ) wid = wid2;
        int hei = parseInt( xml.属性値( xobj, "height" ) );
        int hei2 = ey1 - ey0 + 40;
        if( hei < hei2 ) hei = hei2;
        Object group = xml.新しい要素( here, "KJgroup", gid );
        xml.属性値をセット( group, "comment", xml.属性値( xobj, "objectname" ) );
        xml.属性値をセット( group, "x0", xml.属性値( xobj, "x0" ) );
        xml.属性値をセット( group, "y0", xml.属性値( xobj, "y0" ) );
        xml.属性値をセット( group, "width",  "" + wid );
        xml.属性値をセット( group, "height", "" + hei );

// 画面の中のxobjectの座標( ex0, ey0 )より大きいコンポーネントをずらす
        int x0 =  parseInt( xml.属性値( xobj, "x0" ) );
        int y0 =  parseInt( xml.属性値( xobj, "y0" ) );
        int width =  parseInt( xml.属性値( xobj, "width" ) );
        int height =  parseInt( xml.属性値( xobj, "height" ) );
        Vector comps = xml.子要素のリスト( here );
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "KJgroup" ) ) {
            Object c = comps.get(j);
            int x =  parseInt( xml.属性値( c, "x0" ) );
            int y =  parseInt( xml.属性値( c, "y0" ) );
            if( x >= x0 + width )  xml.属性値をセット( c, "x0","" + ( x + ex1 - ex0 - width  + 20 ) );
            if( y >= y0 + height ) xml.属性値をセット( c, "y0","" + ( y + ey1 - ey0 - height + 40 ) );
          }
        }

// ずらしたところにxobjectの中の画面をはめ込む
// コンポーネントの名前を重複しないようにつけかえる
        for( int j = xcomps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( xcomps.get(j) );
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "KJgroup" ) ) {
            Object c1 = xcomps.get(j);
            Object c2 = xml.新しい要素( here, c1, xml.要素のID( c1 ).substring( 0, 2 ) + ( ID_maker + j ) );
            int x =  parseInt( xml.属性値( c2, "x0" ) );
            int y =  parseInt( xml.属性値( c2, "y0" ) );
            xml.属性値をセット( c2, "x0","" + ( x + x0 - ex0 + 10 ) );
            xml.属性値をセット( c2, "y0","" + ( y + y0 - ey0 + 30 ) );
            if( cname.equals( "xobject" ) )       addNode( new Xnode( c2 ), (Xnode)currentnode );
            else if( cname.equals( "aobject" ) )  addNode( new Anode( c2 ), (Xnode)currentnode );
 
          }
        }

//relation  のコンポーネント名の所もつけかえる
        for( int j = xcomps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( xcomps.get(j) );

          //   relation
          if( cname.equals( "relation" ) ){
            int index = 0;
            Object r1 = xcomps.get(j);
            Object r2 = xml.新しい要素( here, r1, xml.要素のID( r1 ).substring( 0, 2 ) + ( ID_maker + j ) );
            String nm1 = xml.属性値( r1, "pin1name" );
            if( nm1.indexOf( '(' ) > 0 ){
              String  base = getbase( nm1 );
              for( index = xcomps.size(); ( --index >= 0 ) && ( !base.equals( xml.要素のID( xcomps.get( index ) ) ) ); ) ;
              xml.属性値をセット( r2, "pin1name", nm1.substring( 0, 2 ) + ( ID_maker + index ) + "("  + getsubscript( nm1 ) + ")" );
            }
            else{
              for( index = xcomps.size(); ( --index >= 0 ) && ( !nm1.equals( xml.要素のID( xcomps.get( index ) ) ) ); ) ;
              xml.属性値をセット( r2, "pin1name", nm1.substring( 0, 2 ) + ( ID_maker + index ) );
            }
            String nm2 = xml.属性値( r1, "pin2name" );
            if( nm2.indexOf( '(' ) > 0 ){
              String  base = getbase( nm2 );
              for( index = xcomps.size(); ( --index >= 0 ) && ( !base.equals( xml.要素のID( xcomps.get( index ) ) ) ); ) ;
              xml.属性値をセット( r2, "pin2name", nm2.substring( 0, 2 ) + ( ID_maker + index ) + "("  + getsubscript( nm2 ) + ")" );
            }
            else{
              for( index = xcomps.size(); ( --index >= 0 ) && ( !nm2.equals( xml.要素のID( xcomps.get( index ) ) ) ); ) ;
              xml.属性値をセット( r2, "pin2name", nm2.substring( 0, 2 ) + ( ID_maker + index ) );
            }
          }
        }

// xobjectにリンクしているrelationをつけかえる
        for( int j = comps.size()-1; j >= 0; j-- ){
          Object cmp = comps.get(j);
          String cname = xml.要素の名前( cmp );


          // relation
          if( cname.equals( "relation" ) ){
            int index = 0;
            String xnm1 = xml.属性値( cmp, "pin1name" );
            if( xnm1.startsWith( xid ) ){
              String pid = getsubscript( xnm1 );
              for( index = xcomps.size(); ( --index >= 0 ) && ( !pid.equals( xml.要素のID( xcomps.get( index ) ) ) ); ) ;
              xml.属性値をセット( cmp, "pin1name", pid.substring( 0, 2 ) + ( ID_maker + index ) );
            }
            String xnm2 = xml.属性値( cmp, "pin2name" );
            if( xnm2.startsWith( xid ) ){
              String pid = getsubscript( xnm2 );
              for( index = xcomps.size(); ( --index >= 0 ) && ( !pid.equals( xml.要素のID( xcomps.get( index ) ) ) ); ) ;
              xml.属性値をセット( cmp, "pin2name", pid.substring( 0, 2 ) + ( ID_maker + index ) );
            }
          }
        }


        xml.属性値をセット( here, "ID_maker", "" + ( ID_maker + xcomps.size() + 1 ) );
        xml.要素を削除( xobj );
        xnode.suicide();

        objecteditor.Login( (Xnode)currentnode );
      }
    }


    public void upALL(){
      Vector comps = null;
      if( currentnode instanceof Xnode ){
        Object here = ((Xnode)currentnode).element;
        objecteditor.Logout();

        comps = xml.子要素のリスト( here );
        int  oy = -1; //座標変換のオフセット
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "KJgroup" ) ) {
            Object c = comps.get(j);
            int y =  parseInt( xml.属性値( c, "y0" ) );
            if( ( oy < 0 ) || ( oy > y  ) ) oy = y;
          }
        }
        if( oy > MoveStep ){
          for( int j = comps.size()-1; j >= 0; j-- ){
            String cname = xml.要素の名前( comps.get(j) );
            if( cname.equals( "xobject" ) ||
            cname.equals( "aobject" ) ||
            cname.equals( "operation" ) ||
            cname.equals( "pin" ) ||
            cname.equals( "codeclip" ) ||
            cname.equals( "ImgIcon" ) ||
            cname.equals( "KJgroup" ) ) {
              Object c = comps.get(j);
              xml.属性値をセット( c, "y0", "" + ( parseInt( xml.属性値( c, "y0" ) ) - MoveStep ) );
            }
          }
        }

        objecteditor.Login( (Xnode)currentnode );
      }
      else if( currentnode instanceof Anode ){
        Object here = ((Anode)currentnode).element;
        stateeditor.Logout();

        comps = xml.子要素のリスト( here );
        int  oy = -1; //座標変換のオフセット
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );
          if( cname.equals( "state" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "codeclip" ) ){
            Object c = comps.get(j);
            int y =  parseInt( xml.属性値( c, "y0" ) );
            if( ( oy < 0 ) || ( oy > y  ) ) oy = y;
          }
        }
        if( oy > MoveStep ){
          for( int j = comps.size()-1; j >= 0; j-- ){
            String cname = xml.要素の名前( comps.get(j) );
            if( cname.equals( "state" ) ||
            cname.equals( "operation" ) ||
            cname.equals( "pin" ) ||
            cname.equals( "ImgIcon" ) ||
            cname.equals( "codeclip" ) ) {
              Object c = comps.get(j);
              xml.属性値をセット( c, "y0", "" + ( parseInt( xml.属性値( c, "y0" ) ) - MoveStep ) );
            }
          }
        }

        stateeditor.Login( (Anode)currentnode );
      }
    }


    public void downALL(){
      Vector comps = null;
      if( currentnode instanceof Xnode ){
        Object here = ((Xnode)currentnode).element;
        objecteditor.Logout();

        comps = xml.子要素のリスト( here );
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "KJgroup" ) ) {
            Object c = comps.get(j);
            xml.属性値をセット( c, "y0", "" + ( parseInt( xml.属性値( c, "y0" ) ) + MoveStep ) );
          }
        }

        objecteditor.Login( (Xnode)currentnode );
      }
      else if( currentnode instanceof Anode ){
        Object here = ((Anode)currentnode).element;
        stateeditor.Logout();

        comps = xml.子要素のリスト( here );
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );
          if( cname.equals( "state" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "codeclip" )){
            Object c = comps.get(j);
            xml.属性値をセット( c, "y0", "" + ( parseInt( xml.属性値( c, "y0" ) ) + MoveStep ) );
          }
        }

        stateeditor.Login( (Anode)currentnode );
      }
    }


    public void leftALL(){
      Vector comps = null;
      if( currentnode instanceof Xnode ){
        Object here = ((Xnode)currentnode).element;
        objecteditor.Logout();

        comps = xml.子要素のリスト( here );
        int  ox = -1; //座標変換のオフセット
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "KJgroup" ) ) {
            Object c = comps.get(j);
            int x =  parseInt( xml.属性値( c, "x0" ) );
            if( ( ox < 0 ) || ( ox > x  ) ) ox = x;
          }
        }
        if( ox > MoveStep ){
          for( int j = comps.size()-1; j >= 0; j-- ){
            String cname = xml.要素の名前( comps.get(j) );
            if( cname.equals( "xobject" ) ||
            cname.equals( "aobject" ) ||
            cname.equals( "operation" ) ||
            cname.equals( "pin" ) ||
            cname.equals( "codeclip" ) ||
            cname.equals( "ImgIcon" ) ||
            cname.equals( "KJgroup" ) ) {
              Object c = comps.get(j);
              xml.属性値をセット( c, "x0", "" + ( parseInt( xml.属性値( c, "x0" ) ) - MoveStep ) );
            }
          }
        }

        objecteditor.Login( (Xnode)currentnode );
      }
      else if( currentnode instanceof Anode ){
        Object here = ((Anode)currentnode).element;
        stateeditor.Logout();

        comps = xml.子要素のリスト( here );
        int  ox = -1; //座標変換のオフセット
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );
          if( cname.equals( "state" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "codeclip" )){
            Object c = comps.get(j);
            int x =  parseInt( xml.属性値( c, "x0" ) );
            if( ( ox < 0 ) || ( ox > x  ) ) ox = x;
          }
        }
        if( ox > MoveStep ){
          for( int j = comps.size()-1; j >= 0; j-- ){
            String cname = xml.要素の名前( comps.get(j) );
            if( cname.equals( "state" ) ||
            cname.equals( "operation" ) ||
            cname.equals( "pin" ) ||
            cname.equals( "ImgIcon" ) ||
            cname.equals( "codeclip" )){
              Object c = comps.get(j);
              xml.属性値をセット( c, "x0", "" + ( parseInt( xml.属性値( c, "x0" ) ) - MoveStep ) );
            }
          }
        }

        stateeditor.Login( (Anode)currentnode );
      }
    }


    public void rightALL(){
      Vector comps = null;
      if( currentnode instanceof Xnode ){
        Object here = ((Xnode)currentnode).element;
        objecteditor.Logout();

        comps = xml.子要素のリスト( here );
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );
          if( cname.equals( "xobject" ) ||
          cname.equals( "aobject" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "codeclip" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "KJgroup" ) ) {
            Object c = comps.get(j);
            xml.属性値をセット( c, "x0", "" + ( parseInt( xml.属性値( c, "x0" ) ) + MoveStep ) );
          }
        }

        objecteditor.Login( (Xnode)currentnode );
      }
      else if( currentnode instanceof Anode ){
        Object here = ((Anode)currentnode).element;
        stateeditor.Logout();

        comps = xml.子要素のリスト( here );
        for( int j = comps.size()-1; j >= 0; j-- ){
          String cname = xml.要素の名前( comps.get(j) );
          if( cname.equals( "state" ) ||
          cname.equals( "operation" ) ||
          cname.equals( "pin" ) ||
          cname.equals( "ImgIcon" ) ||
          cname.equals( "codeclip" )){
            Object c = comps.get(j);
            xml.属性値をセット( c, "x0", "" + ( parseInt( xml.属性値( c, "x0" ) ) + MoveStep ) );
          }
        }

        stateeditor.Login( (Anode)currentnode );
      }
    }


    public void actionPerformed( ActionEvent e ){
      String command = e.getActionCommand();
      if( command.equals("START") ){
        mythread = new SerchThread( currentnode, serchtext.getText() );
        stopflg = false;
        mythread.start();
      }
      else if( command.equals("CONTINUE") && ( mythread != null ) ){
        synchronized( mythread ){
          stopflg = false;
          mythread.notify();
        }
      }
    }


    private class PrintTool{
      DefaultMutableTreeNode tnode;
      DefaultMutableTreeNode bnode;

      NodeEnum nodeenum;
      XTimer xtimer;
      PrinterJob pjob;
      PageFormat fm;
      Book book;

      PrintTool(){
        nodeenum = new NodeEnum( top );
        xtimer = new XTimer();
      }

      public void printCurrent(){
        pjob = PrinterJob.getPrinterJob();
        fm = pjob.pageDialog(pjob.defaultPage());
        book = new Book();
        book.append( new MyPagePainter(), fm );
        pjob.setPageable( book );
        if( pjob.printDialog() ){
          try{
            pjob.print();
          } catch( Exception e ){ System.out.println(e); }
        }
      }

      public void printAll(){
        pjob = PrinterJob.getPrinterJob();
        fm = pjob.pageDialog(pjob.defaultPage());
        tnode = currentnode;
        bnode = mae_node;
        book = new Book();
        nodeenum.start();
      }

      public void setNode( DefaultMutableTreeNode node){
        if( node != null ){
          changeNode( node );
          xtimer.timer.start();
        }
        else{
          pjob.setPageable( book );
          if( pjob.printDialog() ){
            try{
              pjob.print();
            } catch( Exception e ){ System.out.println(e); }
          }
          changeNode( tnode );
          mae_node = bnode;
        }
      }

      public void restartPrint(){
        book.append( new MyPagePainter(), fm );
        nodeenum.NextNode();
      }

      private class MyPagePainter implements Printable {
        java.awt.image.BufferedImage bufferimage;

        Font   f;
        String name;
        String description;
        double ix;
        double iy;
        double iw;
        double ih;
        double ir;
        double locy;
        int fontsize;

        MyPagePainter(){
          fontsize = 12;
          ix = fm.getImageableX();
          iy = fm.getImageableY();
          iw = fm.getImageableWidth();
          ih = fm.getImageableHeight();

          if( currentnode instanceof Xnode ){
            Dimension d = objecteditor.gui.gedit.getSize();
            int ww = d.width;
            int hh = d.height;
            ir = iw/ww;
            locy = (double)hh + fontsize / ir * 1.2;
            f = objecteditor.gui.name.getFont();

            name = objecteditor.gui.name.getText();
            description = objecteditor.gui.descriptionarea.getText();
            bufferimage = new java.awt.image.BufferedImage( ww, hh, java.awt.image.BufferedImage.TYPE_INT_RGB ); 
            Graphics2D bufferG = bufferimage.createGraphics();
            objecteditor.gui.gedit.paintAll( bufferG );
          }
          else if( currentnode instanceof Anode ){
            Dimension d = stateeditor.gui.gedit.getSize();
            int ww = d.width;
            int hh = d.height;
            ir = iw/ww;
            locy = (double)hh + fontsize / ir * 1.2;
            f = stateeditor.gui.name.getFont();

            name = stateeditor.gui.name.getText();
            description = stateeditor.gui.descriptionarea.getText();
            bufferimage = new java.awt.image.BufferedImage( ww, hh, java.awt.image.BufferedImage.TYPE_INT_RGB ); 
            Graphics2D bufferG = bufferimage.createGraphics();
            stateeditor.gui.gedit.paintAll( bufferG );
          }
        }

        public int print(Graphics g, PageFormat fmt, int pageIndex) {
          double locy2 = locy; 
          int fontsize2 = fontsize;

          Graphics2D g2d = (Graphics2D)g;
          g2d.transform( new AffineTransform( ir, 0, 0, ir, ix, iy+fontsize2/ir*1.2 ) );
          g2d.drawImage( bufferimage, 0, 0, serchbutton );
          g2d.setColor(Color.black);
          g2d.setFont(new Font( f.getName(), f.getStyle(), (int)(fontsize2/ir) ) );
          g2d.drawString("オブジェクト \""+name+"\"", 0, (int)(-fontsize2/ir*1.2) );
          g2d.drawString("<説明>", 0, (int)locy2 );
          locy2 = locy2 + fontsize2/ir*1.2;
          fontsize2 = 10;
          g2d.setFont(new Font("MS Gothic", f.getStyle(), (int)(fontsize2/ir) ) );
          StringTokenizer st = new StringTokenizer( description, "\n" );
          while(st.hasMoreTokens()){
            String lin = st.nextToken();
            g2d.drawString( lin, 0, (int)locy2 );
            locy2 = locy2 + fontsize2/ir*1.2;
          }
          return Printable.PAGE_EXISTS;
        }// ~print()

      }//~MyPagePainter

      private class NodeEnum{
        int state;
        DefaultMutableTreeNode cnode;
        Enumeration enum0;
        NodeEnum child;

        NodeEnum( DefaultMutableTreeNode node ){
          cnode = node;
          if( cnode == null ) state = 0; else state = 1;
        }

        public void start(){
          cnode = top;
          if( cnode == null ) state = 0; else state = 1;
          NextNode();
        }

        public void NextNode(){
          if( state == 0 ){
            setNode( null );
          }
          else if( state == 1 ){
            setNode( cnode );
            enum0 = cnode.children();
            if( enum0.hasMoreElements() ) state = 2; else state = 0;
          }          
          else if( state == 2 ){
            child = new NodeEnum( (DefaultMutableTreeNode)enum0.nextElement() );
            child.NextNode();
            if( child.state == 0 ){
               if( enum0.hasMoreElements() ) state = 2; else state = 0;
            }
            else state = 3;
          }
          else if( state == 3 ){
            child.NextNode();
            if( child.state == 0 ){
              if( enum0.hasMoreElements() ) state = 2; else state = 0;
            }
          }
        }

      }//~NodeEnum

      private class XTimer implements ActionListener{
        javax.swing.Timer timer;

        XTimer(){
          timer = new javax.swing.Timer( 3000, this );
          timer.setRepeats( false );
        }

        public void actionPerformed( ActionEvent e ){
          restartPrint();
        }
      }//~XTimer
  
    }//~PrintTool

    private class SerchThread extends Thread{
      DefaultMutableTreeNode node;
      String serchstring;

      SerchThread( DefaultMutableTreeNode n, String s ){
        node = n;
        serchstring = s;
      }

      public void run(){ serch( node ); }

      private void serch( DefaultMutableTreeNode xnode ){
        Thread th = Thread.currentThread();
        Enumeration enum0 = xnode.children();
        while( enum0.hasMoreElements() ){
          if( mythread != th ) return;
          DefaultMutableTreeNode nod = (DefaultMutableTreeNode)enum0.nextElement();
          if( ((String)(nod.getUserObject())).equals(serchstring) ){
            changeNode( nod );
            stopflg = true;
            try{
              synchronized( mythread ){
                while( stopflg ) mythread.wait();
              }
            } catch( InterruptedException e ){}
          }
          serch( nod );
        }
      }

    }//~SerchThread

    private class MyRenderer extends DefaultTreeCellRenderer {
        ImageIcon xobjIcon;
        ImageIcon aobjIcon;

        public MyRenderer() {
            xobjIcon = new ImageIcon("resources/xobj.jpg");
            aobjIcon = new ImageIcon("resources/aobj.jpg");
        }

        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);
            if (value instanceof Anode ) {
                setIcon(aobjIcon);
            } else if( value instanceof Xnode ) {
                setIcon(xobjIcon);
            } else{}

            return this;
        }

    }//~MyRenderer

  }//~Treetool

  abstract class ObjeditNode extends DefaultMutableTreeNode{
    Object element;

    public void suicide(){
      treetool.removeNode( this );
      element = null;
    }
  }

  class Xnode extends ObjeditNode{

    Xnode( Object elem ){
      int i;
      Vector list;
      element = elem;
      super.setUserObject( xml.属性値( element, "objectname" ) );
      list = xml.子要素のリスト( element, "xobject" );
      for( i = 0; i < list.size(); i++ ){
        add( new Xnode( list.get(i) ) );
      }
      list = xml.子要素のリスト( element, "aobject" );
      for( i = 0; i < list.size(); i++ ){
        add( new Anode( list.get(i) ) );
      }
    }

    public void add( MutableTreeNode c ){ super.add(c); }

  }//~Xnode

  class Anode extends ObjeditNode{

    Anode( Object elem ){
      element = elem;
      super.setUserObject( xml.属性値( element, "objectname" ) );
    }

  }//~Anode


  class InitialDialog extends JDialog implements ActionListener{
    JLabel msg;
    int    mode;
    JRadioButton creapplication;
    JRadioButton creapplet;
    JRadioButton crecppcon;
    JRadioButton crecppwin;
    JRadioButton creandroid;
    JRadioButton crebasic;
    JRadioButton creclang;
    JRadioButton creoregengo;
    JRadioButton cremultigengo;
    JRadioButton crejavascript;
    JRadioButton fileopen;

    InitialDialog(){
      setTitle("新規作成");
      setModal(true);
      mode = 0;
      creapplication = new JRadioButton("Javaアプリケーションを作成");
      creapplet = new JRadioButton("アプレットを作成");
      crecppcon = new JRadioButton("C++ コンソールアプリケーションを作成");
      crecppwin = new JRadioButton("C++ Windowsアプリケーションを作成");
      creandroid = new JRadioButton("androidアプリケーションを作成");
      crebasic = new JRadioButton("Basicアプリケーションを作成");
      creclang = new JRadioButton("C言語アプリケーションを作成");
      creoregengo = new JRadioButton("oregengo-Rアプリケーションを作成");
      cremultigengo = new JRadioButton("マルチ言語アプリケーションを作成");
      crejavascript = new JRadioButton("Javascriptアプリケーションを作成");
      fileopen = new JRadioButton("プロジェクトファイルを開く");
      creapplication.setSelected(true);
      creapplet.setSelected(false);
      crecppcon.setSelected(false);
      crecppwin.setSelected(false);
      creandroid.setSelected(false);
      crebasic.setSelected(false);
      creclang.setSelected(false);
      creoregengo.setSelected(false);
      cremultigengo.setSelected(false);
      crejavascript.setSelected(false);
      fileopen.setSelected(false);
      ButtonGroup g = new ButtonGroup();
      g.add(creapplication);
      g.add(creapplet);
      g.add(crecppcon);
      g.add(crecppwin);
      g.add(creandroid);
      g.add(crebasic);
      g.add(creclang);
      g.add(creoregengo);
      g.add(cremultigengo);
      g.add(crejavascript);
      g.add(fileopen);
      creapplication.setSelected( ApplicationType == 0 );
      creapplet.setSelected( ApplicationType == 1 );
      crecppcon.setSelected( ApplicationType == 2 );
      crecppwin.setSelected( ApplicationType == 3 );
      creandroid.setSelected( ApplicationType == 4 );
      crebasic.setSelected( ApplicationType == 5 );
      creclang.setSelected( ApplicationType == 6 );
      creoregengo.setSelected( ApplicationType == 7 );
      cremultigengo.setSelected( ApplicationType == 8 );
      crejavascript.setSelected( ApplicationType == 9 );
      JPanel p1 = new JPanel( );
      p1.setLayout( new BoxLayout( p1, BoxLayout.Y_AXIS ) ); 
      p1.add( creapplication );
      p1.add( creapplet );
      p1.add( crecppcon );
      p1.add( crecppwin );
      p1.add( creandroid );
      p1.add( crebasic );
      p1.add( creclang );
      p1.add( creoregengo );
      p1.add( cremultigengo );
      p1.add( crejavascript );
      p1.add( fileopen );
      JButton ok = new JButton( "OK" );
      JButton can  = new JButton( "キャンセル"  );
      ok.setActionCommand("OK");
      can.setActionCommand("CAN");
      ok.addActionListener(this);
      can.addActionListener(this);
      can.addActionListener(this);
      JPanel p2 = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
      p2.add( ok );
      p2.add( can );
      getContentPane().add( new JLabel(" 作成するアプリケーションの種類を選択して下さい"), BorderLayout.NORTH );
      getContentPane().add( p1,  BorderLayout.CENTER );
      getContentPane().add( p2,  BorderLayout.SOUTH );
      pack();
    }
    
    //ダイアログを表示
    public int age(){
      int x0, y0;

      creapplication.setSelected( ApplicationType == 0 );
      creapplet.setSelected( ApplicationType == 1 );
      crecppcon.setSelected( ApplicationType == 2 );
      crecppwin.setSelected( ApplicationType == 3 );
      creandroid.setSelected( ApplicationType == 4 );
      crebasic.setSelected( ApplicationType == 5 );
      creclang.setSelected( ApplicationType == 6 );
      creoregengo.setSelected( ApplicationType == 7 );
      cremultigengo.setSelected( ApplicationType == 8 );
      crejavascript.setSelected( ApplicationType == 9 );
      fileopen.setSelected(false);

      // ウィンドウの中央をもとめる
      if( objecteditor.gui.isVisible() ){
        x0 =  ( objecteditor.gui.getLocation().x + objecteditor.gui.getWidth() ) / 2;
        y0 =  ( objecteditor.gui.getLocation().y + objecteditor.gui.getHeight()) / 2;
      }
      else{
        x0 =  ( stateeditor.gui.getLocation().x + stateeditor.gui.getWidth() ) / 2;
        y0 =  ( stateeditor.gui.getLocation().y + stateeditor.gui.getHeight()) / 2;
      }

      //ダイアログの中央をウィンドウの中央にあわせる
      setLocation( new Point( x0 - getWidth() / 2, y0-getHeight() / 2 ) );

      show();
      return( mode );
    }
    
    //ボタンがクリックされたら終了
    public void actionPerformed( ActionEvent e ){
      if( e.getActionCommand().equals( "OK" ) ){
        if( creapplication.isSelected() ) mode = 0;
        else if( creapplet.isSelected() ) mode = 1;
        else if( crecppcon.isSelected() ) mode = 2;
        else if( crecppwin.isSelected() ) mode = 3;
        else if( creandroid.isSelected() ) mode = 4;
        else if( crebasic.isSelected() ) mode = 5;
        else if( creclang.isSelected() ) mode = 6;
        else if( creoregengo.isSelected() ) mode = 7;
        else if( cremultigengo.isSelected() ) mode = 8;
        else if( crejavascript.isSelected() ) mode = 9;
        else if( fileopen.isSelected() ) mode = -2;
      } 
      else if( e.getActionCommand().equals( "CAN" ) )mode = -1;
      hide();
    }
  
  }//~InitialDialog
  
  class InputDialog extends JDialog implements ActionListener{
      JTextField txt;
      int        mode;

      InputDialog(){
      setModal(true);
      setTitle("変更");
      mode = 0;
      JButton yes = new JButton( "OK" );
      JButton no  = new JButton( "キャンセル"  );
      yes.setActionCommand("YES");
      no.setActionCommand("NO");
      yes.addActionListener(this);
      no.addActionListener(this);
      JPanel p2 = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
      p2.add( yes );
      p2.add( no );
      txt = new JTextField("          ");
      txt.setActionCommand("YES");
      txt.addActionListener(this);
      getContentPane().add( txt, BorderLayout.CENTER );
      getContentPane().add( p2,  BorderLayout.SOUTH );
    }
    
    //ダイアログを表示
    public String age( int px, int py, String message, String originaltxt ){
      int x0, y0;

      setTitle( message );
      txt.setText( originaltxt );
      txt.setCaretPosition(0);
      txt.moveCaretPosition( originaltxt.length() );
      pack();
      setLocation( px, py );
      txt.requestFocus();
      show();
      if( mode == 1 ) return( txt.getText() );
      else return( originaltxt );
    }

    //ボタンがクリックされたら終了
    public void actionPerformed( ActionEvent e ){
      if( e.getActionCommand().equals( "YES" ) ) mode =1; else mode = 0;
      hide();
    }
  
  }//~InputDialog
  
  class Dialog1 extends JDialog implements ActionListener{
      JLabel msg;
      int    mode;

      Dialog1(){
      setModal(true);
      mode = 0;
      JButton ok = new JButton( "OK" );
      ok.setActionCommand("YES");
      ok.addActionListener(this);
      JPanel p2 = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
      p2.add( ok );
      msg = new JLabel(" ");
      getContentPane().add( msg, BorderLayout.NORTH );
      getContentPane().add( p2,  BorderLayout.CENTER );
    }
    
    //ダイアログを表示
    public void age( String ms ){
      int x0, y0;

      msg.setText( ms );
      pack();

      // ウィンドウの中央をもとめる
      if( objecteditor.gui.isVisible() ){
        x0 =  ( objecteditor.gui.getLocation().x + objecteditor.gui.getWidth() ) / 2;
        y0 =  ( objecteditor.gui.getLocation().y + objecteditor.gui.getHeight()) / 2;
      }
      else{
        x0 =  ( stateeditor.gui.getLocation().x + stateeditor.gui.getWidth() ) / 2;
        y0 =  ( stateeditor.gui.getLocation().y + stateeditor.gui.getHeight()) / 2;
      }

      //ダイアログの中央をウィンドウの中央にあわせる
      setLocation( new Point( x0 - getWidth() / 2, y0-getHeight() / 2 ) );
      show();
    }
    
    //ボタンがクリックされたら終了
    public void actionPerformed( ActionEvent e ){
      hide();
    }
  
  }//~Dialog1
  
  class Dialog2 extends JDialog implements ActionListener{
      JLabel msg;
      int    mode;

      Dialog2(){
      setModal(true);
      mode = 0;
      JButton yes = new JButton( "Yes" );
      JButton no  = new JButton( "No"  );
      yes.setActionCommand("YES");
      no.setActionCommand("NO");
      yes.addActionListener(this);
      no.addActionListener(this);
      JPanel p2 = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
      p2.add( yes );
      p2.add( no );
      msg = new JLabel(" ");
      getContentPane().add( msg, BorderLayout.NORTH );
      getContentPane().add( p2,  BorderLayout.CENTER );
    }
    
    //ダイアログを表示
    public int age( String ms ){
      int x0, y0;

      msg.setText( ms );
      pack();

      // ウィンドウの中央をもとめる
      if( objecteditor.gui.isVisible() ){
        x0 =  ( objecteditor.gui.getLocation().x + objecteditor.gui.getWidth() ) / 2;
        y0 =  ( objecteditor.gui.getLocation().y + objecteditor.gui.getHeight()) / 2;
      }
      else{
        x0 =  ( stateeditor.gui.getLocation().x + stateeditor.gui.getWidth() ) / 2;
        y0 =  ( stateeditor.gui.getLocation().y + stateeditor.gui.getHeight()) / 2;
      }

      //ダイアログの中央をウィンドウの中央にあわせる
      setLocation( new Point( x0 - getWidth() / 2, y0-getHeight() / 2 ) );
      show();
      return( mode );
    }
    
    //ボタンがクリックされたら終了
    public void actionPerformed( ActionEvent e ){
      if( e.getActionCommand().equals( "YES" ) ) mode =1; else mode = 0;
      hide();
    }
  
  }//~Dialog2
  
  class Dialog3 extends JDialog implements ActionListener{
    JLabel msg;
    int    mode;

    Dialog3(){
      setModal(true);
      mode = 0;
      JButton yes = new JButton( "Yes" );
      JButton no  = new JButton( "No"  );
      JButton can  = new JButton( "Cancel"  );
      yes.setActionCommand("YES");
      no.setActionCommand("NO");
      can.setActionCommand("CAN");
      yes.addActionListener(this);
      no.addActionListener(this);
      can.addActionListener(this);
      msg = new JLabel(" ");
      JPanel p2 = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
      p2.add( yes );
      p2.add( no );
      p2.add( can );
      getContentPane().add( msg, BorderLayout.NORTH );
      getContentPane().add( p2,  BorderLayout.CENTER );
    }
    
    //ダイアログを表示
    public int age(String ms){
      int x0, y0;

      msg.setText(ms);
      pack();

      // ウィンドウの中央をもとめる
      if( objecteditor.gui.isVisible() ){
        x0 =  ( objecteditor.gui.getLocation().x + objecteditor.gui.getWidth() ) / 2;
        y0 =  ( objecteditor.gui.getLocation().y + objecteditor.gui.getHeight()) / 2;
      }
      else{
        x0 =  ( stateeditor.gui.getLocation().x + stateeditor.gui.getWidth() ) / 2;
        y0 =  ( stateeditor.gui.getLocation().y + stateeditor.gui.getHeight()) / 2;
      }

      //ダイアログの中央をウィンドウの中央にあわせる
      setLocation( new Point( x0 - getWidth() / 2, y0-getHeight() / 2 ) );

      show();
      return( mode );
    }
    
    //ボタンがクリックされたら終了
    public void actionPerformed( ActionEvent e ){
      if( e.getActionCommand().equals( "YES" ) ) mode = 1; 
      else if( e.getActionCommand().equals( "CAN" ) )mode = -1;
      else mode = 0;
      hide();
    }
  
  }//~Dialog3

}//~App1


// XFile:Fileクラスの拡張クラス
class XFile extends File{
  StringBuffer javatext;
  
  //コンストラクタ宣言
  XFile( String fname ){ super( fname );}
  XFile( File fil, String fname ){ super( fil, fname );}
  XFile( File fil ){ super( fil.getParentFile(), fil.getName() ); }

  //ディレクトリを作成する
  public boolean mkdir(){ return( super.mkdir() ); }

  //ディレクトリかどうか調べる
  public boolean isDirectory(){ return( super.isDirectory() ); }

  //ファイルかどうか調べる
  public boolean isFile(){ return( super.isFile() ); }

  //親のPathを返す
  public XFile getParentfile(){
    File pf1, pf2;
    pf1 = super.getParentFile();
    if( pf1 == null ) return( null );
    pf2 = pf1.getParentFile();
    if( pf2 == null ) return( new XFile( pf1.getName() ) );
    return( new XFile( pf2, pf1.getName() ) );
  }

  //ディレクトリのリストを取り出す
  public XFile[] listFile(){
    int i;
    File[]   files = super.listFiles();
    if( files == null ) return( null );
    XFile[] xfiles = new XFile[ files.length ];
    for( i = 0; i<files.length; i++){
      xfiles[i] = new XFile( this, files[i].getName() );
    } 
    return( xfiles );
  }

  // 中身ごとディレクトリを削除
  public void Xdelete(){
    int i;
    XFile[] files;
    if( this.isDirectory() ){
      files = listFile();
      if( files!= null ) for( i=0; i<files.length; i++ ) { files[i].Xdelete(); }
    }
    boolean b = super.delete();
  }

  // 中身ごとディレクトリをコピー  
  public void Xcopy( XFile d ){
    int i;
    XFile[] files;
    XFile dst = d;

    if( this.isDirectory() ){
      files = listFile();
      dst.Xdelete();
      dst.mkdir();
      if( files == null ) return;
      for( i=0; i<files.length; i++ ) files[i].Xcopy( new XFile( dst, files[i].getName() ) );
    }

    else if( this.isFile() ){
      try{
      BufferedReader din  = new BufferedReader( new FileReader( this ) );
      BufferedWriter dout = new BufferedWriter( new FileWriter( dst  ) );
      while( din.ready() ) dout.write( din.read() );
      din.close();
      dout.close();
      } catch( IOException e ){  }
    }
  }


  // すべてのサブディレクトリからファイル名を検索してをファイル返す  
  public XFile Xserch( String filename ){
    int i;
    XFile[] files;
    XFile target;

    if( this.isDirectory() ){
      files = listFile();
      if( files == null ) return null;
      for( i=0; i<files.length; i++ ){
        if( ( target = files[i].Xserch( filename ) ) != null ) return target;
      }
      return null;
    }
    else if( this.isFile() ){
      if( this.getName().equals( filename ) ) return this; else return null;
    }
    return null;
  }

  //ファイルとファイルを結合する
  public void Xappend( XFile apfile ){
    XFile tmpfile = new XFile( getParentfile(), "_TEMP_" );
    try{
    BufferedWriter dout = new BufferedWriter( new FileWriter( tmpfile ) );
    BufferedReader din  = new BufferedReader( new FileReader( this ) );
    while( din.ready() ) dout.write( din.read() );
    din.close();
    din  = new BufferedReader( new FileReader( apfile ) );
    while( din.ready() ) dout.write( din.read() );
    din.close();
    dout.close();
    } catch( IOException e ){  }
    tmpfile.Xcopy( this );
    tmpfile.Xdelete();
  }

  //ファイルと文字列を結合する
  public void Xappend( String str ){
    if( this.isDirectory() ) return;
    else if( this.isFile() ){
      XFile tmpfile = new XFile( getParentfile(), "_TEMP_" );
      try{
      BufferedWriter dout = new BufferedWriter( new FileWriter( tmpfile ) );
      BufferedReader din  = new BufferedReader( new FileReader( this ) );
      while( din.ready() ) dout.write( din.read() );
      din.close();
      dout.write( str );
      dout.close();
      } catch( IOException e ){ System.out.println( e ); }
      tmpfile.Xcopy( this );
      tmpfile.Xdelete();
    }
    else{
      try{
      BufferedWriter dout  = new BufferedWriter( new FileWriter( this ) );
      dout.write( str );
      dout.close();
      } catch( IOException e ){ System.out.println( e ); }
    }
  }

  //ファイルの内容を文字列に変換する
  public String toTextString(){
    int c;
    StringBuffer s = new StringBuffer("");
    try{
    BufferedReader din = new BufferedReader( new FileReader( this ) );
    while( ( c = din.read() ) != -1 ) s.append( (char)c );
    din.close();
    } catch( IOException ie ){ return( s.toString() ); }
    return( s.toString() );
  }    
   
  public boolean isxobject()  {
    return( isxml() && DocType().equals("<xobject>") );
  }

  public boolean isaobject()  {
    return( isxml() && DocType().equals("<aobject>") );
  }

  public boolean isoperation(){
    return( isxml() && DocType().equals("<operation>") );
  }
  
  public boolean ispin(){
    return( isxml() && DocType().equals("<pin>") );
  }
  
  public boolean iscodeclip(){
    return( isxml() && DocType().equals("<codeclip>") );
  }
  
  public boolean isKJgroup(){
    return( isxml() && DocType().equals("<KJgroup>") );
  }
  
  public boolean isImgIcon(){
    return( isxml() && DocType().equals("<ImgIcon>") );
  }
  
  public boolean isstate(){ 
      return( isxml() && DocType().equals("<state>") );
  }
  
  // XMLドキュメントかどうか判断する
  public boolean isxml(){
    String s;
    if( !this.isFile() ) return( false );
    try{
    BufferedReader din = new BufferedReader( new FileReader( this ) );
    s = din.readLine();
    din.close();
    } catch( IOException ie ){ return( false ); }
    return( s.equals( "<?xml version=\"1.0\" encoding=\"Shift_JIS\" ?>" ) );
  }

  // XMLドキュメントのタイプを返す
  private String DocType(){
    String dmy, type;
    try{
    BufferedReader din = new BufferedReader( new FileReader( this ) );
    dmy = din.readLine();
    dmy = din.readLine();
    type  = din.readLine();
    din.close();
    } catch( IOException e ){ return( "" ); }
    return( type );
  }

}//~XFile
  

//XML用パッケージ
//なんちゃってXMLドキュメントもモデル ver 0.1 ( ObjectEditor専用 ) 

/*
[ルート要素]
    :
 [親要素]
    |
    +-----[要素1]    ･要素はXML文書を構成する単位
    :        :       ･要素は子の要素や属性を持ことができる
    +-----[要素m]    ･ルート要素はすべての要素の親要素である
    |                ･1つのXML文書には1つのルート要素が存在する
    +-----[ID属性]
    |             
    +-----[属性1]    ･属性には要素のデータが入る
    :        :       ･ID属性には要素の固有の文字列が入る
    +-----[属性n]     (変更や削除は不可。ただし新しい要素をつくるときは重複のない範囲で任意につけることができる)


コンストラクタ
Nxml()

主要API

使用例
  Nxml n = new Nxml();
  Object  root = n.ルート要素();
  Object  parts1 = n.新しい要素( root, "部品", "P001" );
  boolean b = n.属性値をセット( parts1, "大きさ", "1cm" );
  boolean c = n.要素を保存( parts1, new File("部品1.xml") );
  Object parts2 = n.新しい要素( root, new File("部品2.xml"), "P002" );


要素を操作するAPI
  public Object   ルート要素()
  public Object   親要素( Object 要素 )
  public Object   新しい要素( Object 親要素, Object オブジェクト, String ID ) ※オブジェクトの型は
  public Vector   子要素のリスト( Object 親要素 )                               文字列、ファイル、要素の３種類
  public Vector   子要素のリスト( Object 親要素, String 要素名 )
  public Object   子要素( Object 親要素, String ID )
  public String   要素の名前( Object 要素 )                                   ※要素の名前は重複可能
  public String   要素のID( Object 要素 )                                     ※要素のIDは重複不可
  public boolean  要素を保存( Object 要素, File ファイル )
  public boolean  要素を削除( Object 要素 )

属性を操作するAPI
  public Vector   属性名のリスト( Object 要素 )
  public String   属性値( Object 要素, String 属性名 )
  public boolean  属性値をセット( Object 要素, String 属性名, String 属性値 )
  public boolean  属性を削除( Object 要素, String 属性名 )

※戻り値がboolaenのときは以下を意味する
true…成功
false…失敗

*/


// XML処理系を表すクラス
class Nxml{
  NxmlElement root;

  // コンストラクタ(ルート要素を生成)
  Nxml(){
    root = new NxmlElement( null, "ROOT" );
    new NxmlAttribute( root, "ID", "ROOT" );
  }

  // ルート要素を返す
  public Object   ルート要素(){
                    return( root );
                  }

  //親要素を返す
  public Object   親要素( Object 要素 ){
                    if( 要素 instanceof NxmlElement ) return( ( (NxmlElement)要素 ).parent );
                    return( null );
                  }

  // 新しい要素を作って返す
  public Object   新しい要素( Object 親要素, Object obj, String ID ){
                    NxmlElement e;
                    if( obj == null ) return( null );
                    if( 親要素 instanceof NxmlElement ){
                      if( 子要素( 親要素, ID ) != null ) return( null );
                      if( obj instanceof NxmlElement ) e = new NxmlElement( (NxmlElement)親要素, (NxmlElement)obj );
                      else if( obj instanceof String ) e = new NxmlElement( (NxmlElement)親要素, (String)obj );
                      else if( ( obj instanceof File ) && ( (File)obj ).isFile() ){
                        e = new NxmlElement( (NxmlElement)親要素, (File)obj );
                      }
                      else return( null );
                      if( 属性値( e, "ID" ) == null ) new NxmlAttribute( e, "ID", ID );
                      else XsetAttributeValue( e, "ID", ID );
                      return( e );
                    }
                    return( null );
                  }

  // 子要素のリストを返す
  public Vector   子要素のリスト( Object 親要素 ){
                    if( 親要素 instanceof NxmlElement ){
                      Vector v = new Vector();
                      for( int i = 0; i < ( (NxmlElement)親要素 ).child.size(); i++ ){
                        Object n = ( (NxmlElement)親要素 ).child.get(i);
                        if( n instanceof NxmlElement ) v.add( n );
                      }
                      return( v );
                    }
                    return( null );
                  }

  // 特定の名前を持つ子要素のリストを返す
  public Vector   子要素のリスト( Object 親要素, String 要素名 ){
                    if( 親要素 instanceof NxmlElement ){
                      Vector v = new Vector();
                      for( int i = 0; i < ( (NxmlElement)親要素 ).child.size(); i++ ){
                        Object n = ( (NxmlElement)親要素 ).child.get(i);
                        if( ( n instanceof NxmlElement ) && 要素の名前( n ).equals( 要素名 ) ) v.add( n );
                      }
                      return( v );
                    }
                    return( null );
                  }

  // 特定のIDを持つ子要素を返す
  public Object   子要素( Object 親要素, String ID ){
                    if( 親要素 instanceof NxmlElement ){
                      for( int i = 0; i < ( (NxmlElement)親要素 ).child.size(); i++ ){
                        Object n = ( (NxmlElement)親要素 ).child.get(i);
                        if( ( n instanceof NxmlElement ) && 要素のID( n ).equals( ID ) ) return( n );
                      }
                    }
                    return( null );
                  }

  // 要素の名前を返す
  public String   要素の名前( Object 要素 ){
                    if( 要素 instanceof NxmlElement ) return( ( (NxmlElement)要素 ).name );
                    return( null );
                  }

  // 要素のＩＤを返す
  public String   要素のID( Object 要素 ){
                    if( 要素 instanceof NxmlElement ) return( 属性値( 要素, "ID" ) );
                    else return( null );
                  }

  // 要素をXML形式でファイルに保存する
  public boolean  要素を保存( Object 要素, File ファイル ){
                    if( 要素 instanceof NxmlElement ){
                      ( (NxmlElement)要素 ).save( ファイル );
                      return( true );
                    }
                    return( false );
                  }

  // 要素を削除する
  public boolean  要素を削除( Object 要素 ){
                    if( ( 要素 instanceof NxmlElement) && !要素.equals(root) ){
                      ( (NxmlElement)要素 ).suicide();
                      return( true );
                    }
                    return( false );
                  }

  // 属性の名前のリストを返す
  public Vector   属性名のリスト( Object 要素 ){
                    if( 要素 instanceof NxmlElement ){
                      Vector v = new Vector();
                      Vector c = ( (NxmlElement)要素 ).child;
                      for( int i = 0; i < c.size(); i++ ){
                        if( c.get(i) instanceof NxmlAttribute ) v.add( ( (NxmlAttribute)( c.get(i) ) ).name );
                      }
                      return ( v );
                    }
                    return( null );
                  }

  // 属性の値を返す
  public String   属性値( Object 要素, String 属性名 ){
                    if( 要素 instanceof NxmlElement ){
                      Vector c = ( (NxmlElement)要素 ).child;
                      for( int i = 0; i < c.size(); i++ ){
                        if( ( c.get(i) instanceof NxmlAttribute ) && ( (NxmlAttribute)( c.get(i) ) ).name.equals( 属性名 ) ){
                          return( ( (NxmlAttribute)( c.get(i) ) ).value );
                        }
                      }
                      return ( null );
                    }
                    return( null );
                  }


  // 属性の値をセットする
  public boolean  属性値をセット( Object 要素, String 属性名, String 属性値 ){
                    if( 属性名.equals("ID") || !( 要素 instanceof NxmlElement ) ) return( false );
                    if( 属性値( 要素, 属性名 ) == null ) {
                      new NxmlAttribute( (NxmlElement)要素, 属性名, 属性値 );
                      return( true );
                    }
                    else return( XsetAttributeValue( (NxmlElement)要素, 属性名, 属性値 ) );
                  }


  // 属性を削除する
  public boolean  属性を削除( Object 要素, String 属性名 ){
                    if( ( 要素 instanceof NxmlElement )  && !属性名.equals("ID") ){
                      Vector c = ( (NxmlElement)要素 ).child;
                      for( int i = 0; i < c.size(); i++ ){
                        if( ( c.get(i) instanceof NxmlAttribute ) && ( (NxmlAttribute)( c.get(i) ) ).name.equals( 属性名 ) ){
                          ( (NxmlAttribute)( c.get(i) ) ).suicide();
                          return( true );
                        }
                      }
                      return ( false );
                    }
                    return( false );
                  }



  // 属性の値をセットする(外部での使用不可)
  private boolean XsetAttributeValue( NxmlElement element, String name, String val ){
                    if( element instanceof NxmlElement ){
                      Vector c = ( (NxmlElement)element ).child;
                      for( int i = 0; i < c.size(); i++ ){
                        if( ( c.get(i) instanceof NxmlAttribute ) && ( (NxmlAttribute)( c.get(i) ) ).name.equals( name ) ){
                          ( (NxmlAttribute)( c.get(i) ) ).value = val;
                          return( true );
                        }
                      }
                      return ( false );
                    }
                    return( false );
                  }


  // NxmlDocumentのデータの基本単位クラス
  class NxmlNode{
    NxmlElement parent;                   // 親ノード
    String name;                                // 名前
    String value;                               // 値
    public void save( BufferedWriter dout ){}    // ストリームにXML文書を出力
    public void suicide(){}                      // データを消去してメモリを開放する
  }//~NxmlNode

  // NxmlNodeの実装クラス
  class NxmlElement extends NxmlNode{
    Vector      child;

    //コンストラクタ ( 既存の要素をコピー )
    NxmlElement( NxmlElement pnt,  NxmlElement e ){
      parent = pnt;
      child = new Vector();
      name = e.name;
      value = null;
      for( int i = 0; i < e.child.size(); i++ ){
        Object o = e.child.get(i);
        if( o instanceof NxmlAttribute ) new NxmlAttribute( this, ((NxmlAttribute)o).name, ((NxmlAttribute)o).value );
        else if( o instanceof NxmlElement ) new NxmlElement( this, (NxmlElement)o );
      }
      if( parent != null) parent.child.add( this );
    }

    //コンストラクタ ( 名前から新しく作る )
    NxmlElement( NxmlElement pnt,  String nam ){
      parent = pnt;
      child = new Vector();
      name = nam;
      value = null;
      if( parent != null) parent.child.add( this );
    }

    //コンストラクタ ( ファイルからXML文書を読み込んでデータを構成する )
    NxmlElement( NxmlElement pnt,  File fil ){
      char c;
      parent = pnt;
      child = new Vector();
      try{
      BufferedReader din = new BufferedReader( new FileReader( fil ) );
      if( din.markSupported() ){
        din.readLine();
        din.readLine();
        while( ( ( c = getchar( din ) ) != '\0' ) && ( c != '<' ) ) { }
        if(  c != '\0' ) Parse( din );
      }
      if( parent != null) parent.child.add( this );
      din.close();
      } catch( IOException ie ){}
    }

    //コンストラクタ ( 入力ストリームからXML文書を読み込んでデータを構成する )
    NxmlElement( NxmlElement pnt, BufferedReader din ){
      parent = pnt;
      child = new Vector();
      Parse( din );
      if( parent != null) parent.child.add( this );
    }

    //ファイルにXML文書を出力
    public void save( File fil ){
      try{
      BufferedWriter dout = new BufferedWriter( new FileWriter( fil ) );
      dout.write("<?xml version=\"1.0\" encoding=\"Shift_JIS\" ?>\n");
      dout.write("<!-- なんちゃってXML version 0.1( ObjectEditor専用 ) -->\n" );
      save( dout );
      dout.close();
      } catch( IOException ie ){ return; }
    }

    //ストリームにXML文書を出力
    public void save( BufferedWriter dout ){
      int i;
      char c;

      try{
      dout.write( "<" + name + ">\n" );
      for( i = 0; i < child.size(); i++ ){
        NxmlNode chld = (NxmlNode)child.get(i);
        if( chld != null ) chld.save( dout );
        else{ System.out.println("xml element: "+name+" have null children at ("+ i + ")." ); }
      }
      dout.write( "</" + name + ">\n" );
      } catch( IOException ie ){ return; }
    }

    // データを消去してメモリを開放する
    public void suicide(){
      name = null;
      for( int i = 0; i < child.size(); i++ ){
        ((NxmlNode)child .get(i)).suicide();
      }
      child.clear();
      child = null;
      if( parent != null ) parent.child.remove( this );
      parent = null;
    }

    //子を持っていなければ要素を縮退させて属性にする
    public NxmlNode validate(){
      if( child.size() == 0 ) return( new NxmlAttribute( this ) );
      else{
        value = null;
        return( this );
      }
    }

    // ストリームからXML文書を読み込む
    private void Parse( BufferedReader din ){
      char c;
      String s;

      try{
      if( ( ( c = getchar( din ) ) == '\0' ) || ( c == '/' ) ) return;
      name = String.valueOf( c );
      while( ( c = getchar( din ) ) != '\0' && ( c != '>' ) ) name = name + String.valueOf( c );
      if( c == '\0' ) return;
      value = "";
      while( true ){
        while( ( ( c = getchar( din ) ) != '\0' ) && ( c != '<' ) ){
          if( c == '&' ){
            char b;
            String enty = "";
            while( ( ( b = getchar( din ) ) != '\0' ) && ( b != ';' ) ){
              enty = enty + String.valueOf( b );
            }
            if( b == '\0' ) return;
            if( enty.equals( "amp" ) )       c = '&';
            else if( enty.equals( "lt" ) )   c = '<';
            else if( enty.equals( "gt" ) )   c = '>';
            else if( enty.equals( "quot" ) ) c = '\"';
            else if( enty.equals( "apos" ) ) c = '\'';
          }
          value = value + String.valueOf( c );
        }
        if( c == '\0' ) return;
        din.mark(4);
        if( ( c = getchar( din ) ) == '\0' ) return;
        else if( c == '/' ){
          while( ( ( c = getchar( din ) ) != '\0' ) && ( c != '>' ) ) {}
          return;
        }
        else{
          din.reset();
          ( new NxmlElement( this, din ) ).validate();
        }
      }
      } catch( IOException ie ){}
    }

    // ストリームから１文字読み込む
    private char getchar( BufferedReader din ){
      int c;
      try{
      if( ( c = din.read() ) != -1 ) return( (char)c ); else return( '\0' );
      } catch( IOException ie ){  return('\0'); }
    }

  } //~NxmlElement

// NxmElementの属性クラス
  class NxmlAttribute extends NxmlNode{

    //コンストラクタ ( データを新規作成する )
    NxmlAttribute( NxmlElement prnt, String nam, String val ){
      parent = prnt;
      name = nam;
      value = val;
      if( parent != null ) parent.child.add( this );
    }

    //コンストラクタ ( 要素のデータをコピーして縮退させる )
    NxmlAttribute( NxmlElement alas ){
      parent = alas.parent;
      name = alas.name;
      value = alas.value;
      alas.suicide();
      if( parent != null ) parent.child.add( this );
    }

    //ストリームにXML文書を出力
    public void save( BufferedWriter dout ){
      int i;
      char c;

      if( value == null ){
        System.out.println( "xml element: "+name+" value is null.");
        return;
      }

      try{
      dout.write( "<" + name + ">" );
      for( i = 0; i < value.length(); i++ ){
        c = value.charAt( i );
        if( c =='<' )        dout.write( "&lt;" );
        else if( c == '>' )  dout.write( "&gt;" );
        else if( c == '\"' ) dout.write( "&quot;" );
        else if( c == '\'' ) dout.write( "&apos;" );
        else if( c == '&' ) dout.write( "&amp;" );
        else dout.write( c );
      } 
      dout.write( "</" + name + ">\n" );
      } catch( IOException ie ){}
    }

    // データを消去してメモリを開放する
    public void suicide(){
      if( parent != null ) parent.child.remove( this );
      parent = null;
      name = null;
      value = null;
    }

  }//~NxmlAttribute

}// ~Nxml

class XFileFilter extends javax.swing.filechooser.FileFilter {
  String extention;
  String description;

  public XFileFilter(){
    extention = "*";
    description = "ALL Files";
  }

  public XFileFilter( String mode ){
    this();
    int i = mode.lastIndexOf('/');
    if( i > 0 && i < mode.length()-1 ){ 
      description = mode.substring( 0, i );
      extention = mode.substring( i+1 ).toLowerCase();
    }
  }

  public  boolean accept(File f) {
    String ext;
    if(f == null) return false;
    if(f.isDirectory()) return true;
    if( extention.equals("*") ) return true;
    String fname = f.getName();
    int i = fname.lastIndexOf('.');
    if( i > 0 && i < fname.length()-1 ) ext = fname.substring( i+1 ).toLowerCase(); else return false;
    if( ext.equals( extention ) ) return true;
    return false;
  }

  public String getDescription() {
     return description;
  }
}
