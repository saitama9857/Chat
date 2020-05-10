import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUDP extends JFrame {
    private JTextArea taMain;
    private JTextField tgMsg;
    private final String FRM_TITLE = "Messenger  на минималках";
    private final int FRM_LOC_X = 100;
    private final int FRM_LOC_Y = 100;
    private final int FRM_WIDTH = 400;
    private final int FRM_HEIGHI = 400;

    private final int PORT = 9423;
    private final String IP_BROADCAST = "192.168.43.35";

    private class thdReciver extends Thread{
        @Override
        public void start() {
            super.start();
            try {
                customize();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        private void customize() throws Exception{
             DatagramSocket resiveSocked = new DatagramSocket(PORT);
             Pattern regex = Pattern.compile("[\u0020-\uFFFF]");
             while (true) {
                 byte[] reciveData = new byte[1024];
                 DatagramPacket recivePacket = new DatagramPacket(reciveData, reciveData.length);
                 resiveSocked.receive(recivePacket);
                 InetAddress IPAddress = recivePacket.getAddress();
                 int port = recivePacket.getPort();
                 String senranse = new String(recivePacket.getData());
                 Matcher m = regex.matcher(senranse);
                 taMain.append(IPAddress.toString() + ": " + port + ": ");

                 while (m.find())
                     taMain.append(senranse.substring(m.start(), m.end()));
                     taMain.append("\r\n");
                     taMain.setCaretPosition(taMain.getText().length());
             }
        };
    }
    private void btnSend_Handler() throws Exception{
        DatagramSocket sendSocket = new DatagramSocket();
        InetAddress IPaddress = InetAddress.getByName(IP_BROADCAST);
        byte[] sendData;
        String sentence = tgMsg.getText();
        tgMsg.setText("");
        sendData = sentence.getBytes("UTF-8");
        DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,IPaddress,PORT);
        sendSocket.send(sendPacket);
    }
    private void frameDraw(JFrame frame){
        tgMsg = new JTextField();
        taMain = new JTextArea(FRM_HEIGHI/19, 50);
        JScrollPane spMain = new JScrollPane(taMain);
        spMain.setLocation(0,0);
        taMain.setLineWrap(true);
        taMain.setEnabled(false);

        JButton btnSend = new JButton();
        btnSend.setText("Send");
        btnSend.setToolTipText("Broadcast a messege");
        btnSend.addActionListener(e ->{
            try{
                btnSend_Handler();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle(FRM_TITLE);
        frame.setLocation(FRM_LOC_X,FRM_LOC_Y);
        frame.setSize(FRM_WIDTH,FRM_HEIGHI);
        frame.setResizable(false);
        frame.getContentPane().add(BorderLayout.NORTH, spMain);
        frame.getContentPane().add(BorderLayout.CENTER, tgMsg);
        frame.getContentPane().add(BorderLayout.EAST, btnSend);
        frame.setVisible(true);


    }
    private void antistatic(){
       frameDraw(new ChatUDP());
        new thdReciver().start();

    }
    public static void main(String[] args) {
        new ChatUDP().antistatic();
    }

}
//   IPv4-адрес. . . . . . . . . . . . : 192.168.43.35
//   Маска подсети . . . . . . . . . . : 255.255.255.0
//   Основной шлюз. . . . . . . . . : 192.168.43.1