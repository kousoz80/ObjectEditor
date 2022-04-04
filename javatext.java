 import java.awt.*;
 import java.awt.event.*;
 import javax.swing.*;
 import javax.swing.event.*;
 import javax.swing.tree.*;
 import java.io.*;
 import java.net.*;
 import java.util.*;
import java.awt.geom.*;
import java.awt.print.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
 import javax.swing.*;
 import javax.swing.event.*;
 import javax.swing.tree.*;
 import java.io.*;
 import java.net.*;
 import java.util.*;
import java.awt.image.BufferedImage;
import javax.sound.sampled.*;

class Starter{
  public static void main( String args[] ){
    NewApplication ap = new NewApplication();
    ap.ARGS = args;
    ap.Start();
  }
}
class NewApplication{
 String[] ARGS;

public void Start(){
IGUI.Start();
}
private void _O2_in(){
// 終了する
System.exit(0);

}
GUI IGUI;
class GUI{
int STATE, STATE2;
NewApplication parent;
 class XGUI extends JFrame implements WindowListener{
 JPanel cnt;
T_AREA0 IT_AREA0;
 class T_AREA0 extends JTextArea{
 JScrollPane scrl;
  T_AREA0(){
 setOpaque( true );
 setName(  "T_AREA0" );
 setFont( new Font( "Dialog", 0, 12 ));
 setForeground( new Color(  51, 51, 51 ));
 setBackground( new Color(  255, 255, 255 ));
 setText( "Text" );
 scrl = new JScrollPane( this );
 T_AREA0_created( this );
}
}
BUTTON1 IBUTTON1;
 class BUTTON1 extends JButton implements ActionListener{
  BUTTON1(){
 setOpaque( true );
 setName(  "BUTTON1" );
 setFont( new Font( "Dialog", 1, 12 ));
 setForeground( new Color(  51, 51, 51 ));
 setBackground( new Color(  192, 192, 192 ));
 setHorizontalAlignment( 0 );
 setVerticalAlignment( 0 );
 setText( "BUTTON1" );
 addActionListener( this );
 BUTTON1_created( this );
}
 public void actionPerformed( ActionEvent e ){ BUTTON1_clicked(); }
}
BUTTON2 IBUTTON2;
 class BUTTON2 extends JButton implements ActionListener{
  BUTTON2(){
 setOpaque( true );
 setName(  "BUTTON2" );
 setFont( new Font( "Dialog", 1, 12 ));
 setForeground( new Color(  51, 51, 51 ));
 setBackground( new Color(  192, 192, 192 ));
 setHorizontalAlignment( 0 );
 setVerticalAlignment( 0 );
 setText( "BUTTON2" );
 addActionListener( this );
 BUTTON2_created( this );
}
 public void actionPerformed( ActionEvent e ){ BUTTON2_clicked(); }
}
 XGUI(){
 setTitle("NoTitle");
 cnt = new JPanel( new BorderLayout() );
 cnt.setPreferredSize(  new  Dimension( 400, 298 ) );
 ((Component)cnt).setBackground( new Color( 252, 253, 186 ) );
 IT_AREA0 = new T_AREA0();
 cnt.add( IT_AREA0.scrl, 0 );
 cnt.getLayout().removeLayoutComponent( IT_AREA0.scrl );
 (  IT_AREA0.scrl ).setBounds( 17, 15, 335, 207 );
 IBUTTON1 = new BUTTON1();
 cnt.add( IBUTTON1, 0 );
 cnt.getLayout().removeLayoutComponent( IBUTTON1 );
 IBUTTON1.setBounds( 28, 238, 140, 35 );
 IBUTTON2 = new BUTTON2();
 cnt.add( IBUTTON2, 0 );
 cnt.getLayout().removeLayoutComponent( IBUTTON2 );
 IBUTTON2.setBounds( 178, 238, 140, 35 );
 setContentPane( cnt );
 pack();
 addWindowListener( this );
 setVisible( true );
 GUI_created( this );
}
 public void windowActivated( WindowEvent e ){}
 public void windowClosed( WindowEvent e ){}
 public void windowClosing( WindowEvent e ){ GUI_closed(); }
 public void windowDeactivated( WindowEvent e ){}
 public void windowDeiconified( WindowEvent e ){}
 public void windowIconified( WindowEvent e ){}
 public void windowOpened( WindowEvent e ){}
}

public void Start(){
STATE2 = STATE;
_Ocreate_in();
}
public void GUI_created(JFrame f){
STATE2 = STATE;
}
public void GUI_closed(){
STATE2 = STATE;
parent._O2_in();
}
public void T_AREA0_created(JTextArea t){
STATE2 = STATE;
}
public void BUTTON1_created(JButton b){
STATE2 = STATE;
}
public void BUTTON1_clicked(){
STATE2 = STATE;
}
public void BUTTON2_created(JButton b){
STATE2 = STATE;
}
public void BUTTON2_clicked(){
STATE2 = STATE;
}
private void _Ocreate_in(){
if( STATE2 != 74534551 ) return;
XGUI x = new XGUI();



//   InitState に遷移する
_SINIT();
}

//   InitState
private void _SINIT(){
STATE = 74534551;
}
GUI( NewApplication pnt ){
 parent = pnt;
_SINIT();
}
}
NewApplication( ){
IGUI = new GUI( this );

}
}
