package fileSend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public  class client{
    public static void main(String []args){
        new MyFrame("文件传输server");
    }

}

class MyFrame extends JFrame implements ActionListener{
    TextField SendPath = new  TextField(15);
    TextField SavePath = new  TextField("C:\\Users",15);
    JButton Send = new JButton("Send");
    TextArea sfTA = new TextArea(14,  35);
    TextField port = new  TextField("8000");
    TextField adress = new  TextField("localhost");
    JButton chooseSend = new JButton("选择");
    JButton chooseSave = new JButton("选择");
    JButton connect = new JButton("连接服务");
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
        //CENTER
        Panel Center = new Panel();
        Center.add(sfTA);
        Center.add(new JLabel("发送文件路径："));
        Center.add(SendPath);
        Center.add(chooseSend);
        Center.add(new JLabel("保存文件路径："));
        Center.add(SavePath);
        Center.add(chooseSave);
        chooseSave.addActionListener(this);
        chooseSend.addActionListener(this);

        this.add(Center,BorderLayout.CENTER);
        //SOUTH
        Panel South = new Panel();
        South.add(Send);
        this.add(South,BorderLayout.SOUTH);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==chooseSend){
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.showDialog(new JLabel(), "选择");
            File file = chooser.getSelectedFile();
            SendPath.setText(file.getAbsoluteFile().toString());
            if(file.isDirectory())
                SendPath.setText("选择有效文件！");
            else if (file.isFile())
                SendPath.setText(file.getParentFile().toString());

        }else  if(actionEvent.getSource()==chooseSave){
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.showDialog(new JLabel(), "选择");
            File file = chooser.getSelectedFile();
            if(file.isDirectory())
                SavePath.setText(file.getAbsoluteFile().toString());
            else if (file.isFile())
                SavePath.setText(file.getParentFile().toString());


        }

    }
}