package fileSend;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

public  class server{
    public static void main(String []args){
        new FileChooser("文件传输server");
    }

}
 class FileChooser extends JFrame implements ActionListener{
    ServerSocket server=null;
    Socket client =null;
     Thread ServerThread=null;
     TextField path = new  TextField(20);
    JButton send = new JButton("Send");
    TextArea sfTA = new TextArea(14,  35);
    TextField port = new  TextField("8000");
     JButton choose = new JButton("选择");
     JButton connect = new JButton("运行服务");
    public FileChooser(String title){
        super(title);
        setLocation(10,10);		//窗体位置
        setSize(350,400);			//窗体大小
        setResizable(false);		//设置窗体不可由用户调整大小

        //north
        Panel North = new Panel();
        North.add(new JLabel("port:"));
        North.add(port);
        North.add(connect);
        this.add(North,BorderLayout.NORTH);
        //CENTER
        Panel Center = new Panel();
        Center.add(sfTA);
        Center.add(new JLabel("文件路径："));
        Center.add(path);
        choose.addActionListener(this);
        Center.add(choose);
        this.add(Center,BorderLayout.CENTER);
        //SOUTH
        Panel South = new Panel();
        South.add(send);
        this.add(South,BorderLayout.SOUTH);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

     @Override
     public void actionPerformed(ActionEvent actionEvent) {

         if(actionEvent.getSource()==choose){
             JFileChooser chooser = new JFileChooser();
             chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
             chooser.showDialog(new JLabel(), "选择");
             File file = chooser.getSelectedFile();
             path.setText(file.getAbsoluteFile().toString());
         }else if(actionEvent.getSource()==connect){
             try {
                 connect.setEnabled(false);
                 int i = Integer.parseInt(port.getText());
                 sfTA.append("Server Port :"+port.getText()+"\n");
                 server = new ServerSocket(i);
                 client = server.accept();
                 sfTA.append("a client connect ...\n");
                 this.ServerThread  = new Thread(new SeverProcessThread());  //
                 ServerThread.start();
             } catch (IOException e) {
                 e.printStackTrace();
             }

         }else if(actionEvent.getSource()==send){

         }

     }
 }
class SeverProcessThread implements Runnable{
    @Override
    public void run() {

    }
}