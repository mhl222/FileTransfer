package fileSend;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.*;

public  class server{
    public static void main(String []args) throws IOException {
        new FileChooser("文件传输server");

    }

}
 class FileChooser extends JFrame {
     File saveFile = null;
     File sendFile = null;
    ServerSocket server=null;
     Thread ServerThread=null;

     TextField SendPath = new  TextField(15);
     TextField SavePath = new  TextField("C:\\Users\\admin\\Desktop\\新建文件夹",15);
     TextField port = new  TextField("8000");
     TextArea sfTA = new TextArea(14,  35);
     JButton send = new JButton("Send");
     JButton chooseSend = new JButton("选择");
     JButton chooseSave = new JButton("选择");
     JButton connect = new JButton("运行服务");
     CFListener listener = new CFListener();
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
        connect.addActionListener(this.listener);
        //CENTER
        Panel Center = new Panel();
        Center.add(sfTA);
        Center.add(new JLabel("发送文件路径："));
        Center.add(SendPath);
        Center.add(chooseSend);
        Center.add(new JLabel("保存文件路径："));
        Center.add(SavePath);
        Center.add(chooseSave);
        chooseSend.addActionListener(this.listener);
        chooseSave.addActionListener(this.listener);
        this.add(Center,BorderLayout.CENTER);
        //SOUTH
        Panel South = new Panel();
        South.add(send);
        this.add(South,BorderLayout.SOUTH);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


     class CFListener implements ActionListener {

         @Override
         public void actionPerformed(ActionEvent actionEvent) {
             if(actionEvent.getSource()==chooseSend){
                 JFileChooser chooser = new JFileChooser();
                 chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                 chooser.showDialog(new JLabel(), "选择");
                 sendFile = chooser.getSelectedFile();
                 SendPath.setText(sendFile.getAbsoluteFile().toString());
             }else  if(actionEvent.getSource()==chooseSave){
                 JFileChooser chooser = new JFileChooser();
                 chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                 chooser.showDialog(new JLabel(), "选择");
                 saveFile = chooser.getSelectedFile();
                 SavePath.setText(saveFile.getAbsoluteFile().toString());
             }else if(actionEvent.getSource()==connect){
                 try {
                      connect.setEnabled(false);
                     server = new ServerSocket(8000);
                     sfTA.append("Server Port :"+port.getText()+"\n");
                     int i = Integer.parseInt(port.getText());
                     sfTA.append("Server Port :"+port.getText()+"\n");
                     ServerThread  = new Thread(new SeverProcessThread(server));  //
                     ServerThread.start();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }
         }
     }
     class SeverProcessThread implements Runnable{

        ServerSocket server=null;

         public SeverProcessThread( ServerSocket server) {

             this.server = server;

         }

         @Override
         public void run() {
             while (true) {
                 try {
                     Socket client= server.accept();
                     sfTA.append("a client connect ...\n");
                    InputStream serverIn = client.getInputStream();
                     OutputStream serverOut =client.getOutputStream();
                     byte[] fname = new byte[1024];
                     System.out.println("进入accpet");

                     int len = serverIn.read(fname);
                     System.out.println("进入1");

                     String fileName = new String(fname, 0, len);
                     int isAccept = JOptionPane.showConfirmDialog(null, "是否接受发来文件：" + fileName, "新文件", JOptionPane.YES_NO_OPTION);
                     System.out.println("进入2");

                     if (isAccept == JOptionPane.NO_OPTION) {
                         serverOut.write("noaccept".getBytes());

                         serverIn.close();
                         serverOut.close();
                     }
                     System.out.println("进入3");
                     serverOut.write("accept".getBytes());

                     saveFile = new File(saveFile, fileName);
                   OutputStream out = new FileOutputStream(saveFile);
                     byte[] data = new byte[1024 * 1024];
                     int datalen = 0;
                     while ((datalen = serverIn.read(data)) != -1) {
                         System.out.println("进入4");
                         out.write(data, 0, datalen);

                     }
                     sfTA.append("文件传输完毕\n");
                     saveFile=saveFile.getParentFile();


                     serverIn.close();
                     serverOut.close();
                     out.close();
                     client.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
     }
 }
