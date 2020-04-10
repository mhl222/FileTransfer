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
    File saveFile = null;
    Socket client=null;
    Thread ClientThread=null;
    TextField SendPath = new  TextField(15);
    TextField SavePath = new  TextField("C:\\Users",15);
    JButton Send = new JButton("Send");
    TextArea sfTA = new TextArea(14,  35);
    TextField port = new  TextField("8000");
    TextField adress = new  TextField("localhost");
    JButton chooseSend = new JButton("选择");
    JButton chooseSave = new JButton("选择");
    JButton connect = new JButton("连接服务");
    Listener listener=new Listener();
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
        Center.add(new JLabel("保存文件路径："));
        Center.add(SavePath);
        Center.add(chooseSave);
        chooseSave.addActionListener(this.listener);
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

            }else  if(actionEvent.getSource()==chooseSave){
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.showDialog(new JLabel(), "选择");
                saveFile = chooser.getSelectedFile();
                SavePath.setText(saveFile.getAbsoluteFile().toString());
            }else if (actionEvent.getSource()==connect){
                System.out.println("进入connect");
                sfTA.append("Socket Connect IP :"+adress.getText()+"\nConnect Port :"+port.getText()+"\n");
                try {
//                client =new Socket(adress.getText(),Integer.parseInt(port.getText()));
                    client =new Socket(InetAddress.getLocalHost(),8000);
                    sfTA.append("connect ...\n");
                    ClientThread=new Thread(new clientThread(client));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (actionEvent.getSource()==Send){
                System.out.println((ClientThread==null));
                System.out.println("进入send");

                ClientThread.start();

            }
        }
    }
    class clientThread implements Runnable{
        private Socket client=null;

        public clientThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            if(sendFile==null){
                JOptionPane.showMessageDialog(null, "请选择发送文件", "警告", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                System.out.println("进入send1");

                OutputStream clientOut = client.getOutputStream();
                InputStream clientIn = client.getInputStream();
                System.out.println("进入send2");

                byte[] filename;
                filename = sendFile.getName().getBytes();
                System.out.println("进入send3");

                clientOut.write(filename);
                clientOut.flush();
                System.out.println("进入send4");
                byte[] nameIn=new byte[1024];
                int len =clientIn.read(nameIn);
                System.out.println("进入send5");

                if(!"accept".equals(new String(nameIn,0,len))){
                    JOptionPane.showMessageDialog(null, "服务器拒绝接收文件", "失败", JOptionPane.ERROR_MESSAGE);
                    clientIn.close();
                    clientOut.close();
                    return;
                }
                System.out.println("进入send6");

                InputStream in = new FileInputStream(sendFile);
                byte[] data = new byte[1024];
                int data_len=0;
                while ((data_len=in.read(data))!=-1){
                    clientOut.write(data,0,data_len);
                }
                sfTA.append("文件发送完毕\n");


                in.close();
                clientIn.close();
                clientOut.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    }

class FileUtil1{
    private File srcFile;
    private File desFile;
    public FileUtil1(String srcFile,String desFile){
        this.desFile=new File(desFile);
        this.srcFile=new File(srcFile);
    }
    public boolean copy(){
        if(!this.srcFile.exists()) {
            System.out.println("源文件不存在");
            return false;
        }


        return copyFileImpl(this.srcFile,this.desFile);

    }
    public boolean copyFileImpl(File srcFile,File desFile){
        if(!this.desFile.getParentFile().exists())
            this.desFile.getParentFile().mkdirs();
        byte[] data =new byte[1024];
        InputStream input=null;
        OutputStream output =null;
        try {
            input=new FileInputStream(srcFile);
            output=new FileOutputStream(desFile);
            int len=0;
            while ((len=input.read(data))!=-1){
                output.write(data,0,len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(input!=null)
                    input.close();
                if (output!=null)
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }
    public void copyImpl(File file){
        if(file.isDirectory()){
            File[] result = file.listFiles();
            if(result!=null){
                for(int n=0;n<result.length;n++)
                {
                    copyImpl(result[n]);
                }
            }
        }else {
            String newfilePath = file.getPath().replace(this.srcFile.getPath()+File.separator,"");
            File newFile = new File(this.desFile,newfilePath);

            copyFileImpl(file,newFile);
        }

    }
    public  boolean copyDir(){
        this.copyImpl(this.srcFile);
        return true;
    }

}