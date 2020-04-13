package fileSend;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public  class client{
    public static void main(String []args){
        new MyFrame("文件传输client");
    }

}

class MyFrame extends JFrame {
    File sendFile = null;
    Socket client=null;
    TextField SendPath = new  TextField(15);
    JButton Send = new JButton("Send");
    TextArea sfTA = new TextArea(14,  35);
    TextField port = new  TextField("8000");
    TextField adress = new  TextField("localhost");
    JButton chooseSend = new JButton("选择");
    JButton connect = new JButton("连接服务");
    Listener listener=new Listener();
    OutputStream clientOut = null;
    InputStream clientIn = null;
    public MyFrame(String title){
        super(title);
        setLocation(10,10);		//窗体位置
        setSize(350,400);			//窗体大小
        setResizable(false);		//设置窗体不可由用户调整大小

        //north
        Panel North = new Panel();
        North.add(new JLabel("IP:"));
        North.add(adress);
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
        chooseSend.addActionListener(this.listener);

        this.add(Center,BorderLayout.CENTER);
        //SOUTH
        Panel South = new Panel();
        South.add(Send);
        this.add(South,BorderLayout.SOUTH);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Send.addActionListener(this.listener );
    }
    class Listener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(actionEvent.getSource()==chooseSend){
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.showDialog(new JLabel(), "选择");
                sendFile = chooser.getSelectedFile();
                SendPath.setText(sendFile.getAbsoluteFile().toString());

            }else if (actionEvent.getSource()==connect){
                System.out.println("进入connect");
                sfTA.append("Socket Connect IP :"+adress.getText()+"\nConnect Port :"+port.getText()+"\n");
                try {
//                client =new Socket(adress.getText(),Integer.parseInt(port.getText()));
                    client =new Socket(adress.getText(),Integer.parseInt(port.getText()));
                    clientIn =   client.getInputStream();
                    clientOut = client.getOutputStream();
                    sfTA.append("connect ...\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (actionEvent.getSource()==Send){
                new Thread(new CFThread()).start();
            }
        }
    }
    class CFThread implements Runnable{
        public void run() {
            if (sendFile == null) {
                JOptionPane.showMessageDialog(null, "请选择发送文件", "警告", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                byte[] filename;
                filename = sendFile.getName().getBytes();
                System.out.println("1");
                clientOut.write(filename);
                byte[] nameIn = new byte[1024];
                int len = clientIn.read(nameIn);
                System.out.println("2");

                if (!"accept".equals(new String(nameIn, 0, len))) {
                    JOptionPane.showMessageDialog(null, "服务器拒绝接收文件", "失败", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                InputStream in = new FileInputStream(sendFile);
                byte[] data = new byte[1024];
                int data_len = 0;
                while ((data_len = in.read(data)) != -1) {
                    clientOut.write(data, 0, data_len);
                }
                sfTA.append("文件发送完毕\n");
                System.out.println("完毕");


                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    }
