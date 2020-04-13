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

    ServerSocket server=null;
     Thread ServerThread=null;

     TextField SavePath = new  TextField("C:\\Users\\admin\\Desktop\\新建文件夹",15);
     File saveFile = new File(SavePath.getText());
     TextField port = new  TextField("8000");
     TextArea sfTA = new TextArea(14,  35);
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
        Center.add(new JLabel("保存文件路径："));
        Center.add(SavePath);
        Center.add(chooseSave);
        chooseSave.addActionListener(this.listener);
        this.add(Center,BorderLayout.CENTER);
        //SOUTH
        Panel South = new Panel();
        this.add(South,BorderLayout.SOUTH);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


     class CFListener implements ActionListener {

         @Override
         public void actionPerformed(ActionEvent actionEvent) {
              if(actionEvent.getSource()==chooseSave){
                 JFileChooser chooser = new JFileChooser();
                 chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                 chooser.showDialog(new JLabel(), "选择");
                 saveFile = chooser.getSelectedFile();
                 SavePath.setText(saveFile.getAbsoluteFile().toString());
                 if(saveFile.isFile())
                     saveFile=saveFile.getParentFile();
             }else if(actionEvent.getSource()==connect){
                 try {
                      connect.setEnabled(false);
                     server = new ServerSocket(Integer.parseInt(port.getText()));
                     sfTA.append("Server Port :"+port.getText()+"\n");
                     int i = Integer.parseInt(port.getText());
                     sfTA.append("Server Port :"+port.getText()+"\n");
                     ServerThread  = new Thread(new SeverGetThread(server,server.accept()));  //
                     sfTA.append("a client connect ...\n");
                     ServerThread.start();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }
         }
     }
     class SeverGetThread implements Runnable{

        ServerSocket server=null;
         Socket client=null;
         InputStream serverIn = null;
         OutputStream serverOut = null;

         public SeverGetThread( ServerSocket server,Socket socket) {

             this.server = server;
             this.client=socket;
             try {
                  serverIn = client.getInputStream();
                 serverOut = client.getOutputStream();

             } catch (IOException e) {
                 e.printStackTrace();
             }

         }

         @Override
         public void run() {
                 while (true) {
                     try {

                         byte[] fname = new byte[1024];
                         System.out.println("进入accpet");

                         int len = serverIn.read(fname);
                         System.out.println("1");

                         String fileName = new String(fname, 0, len);
                         int isAccept = JOptionPane.showConfirmDialog(null, "是否接受发来文件：" + fileName, "新文件", JOptionPane.YES_NO_OPTION);

                         if (isAccept == JOptionPane.NO_OPTION) {
                             serverOut.write("noaccept".getBytes());
                             continue;
                         }
                         serverOut.write("accept".getBytes());

                         saveFile = new File(saveFile, fileName);
                         System.out.println("2");

                         OutputStream out = new FileOutputStream(saveFile);
                         System.out.println(saveFile.getAbsoluteFile());

                         byte[] data = new byte[1024];
                         int dlen = 0;
                         while ((dlen = serverIn.read(data)) ==1024) {
                             out.write(data, 0, dlen);
                             System.out.println(dlen);

                         }
                         sfTA.append("文件传输完毕\n");
                         System.out.println("完毕");
                         saveFile = saveFile.getParentFile();
                         System.out.println(saveFile.getAbsoluteFile());

                         out.close();

                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
         }
     }
 }
