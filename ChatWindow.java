import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ChatWindow implements Runnable {

    public JTextField textField;
    public JTextArea textArea;
    public String login;
    BufferedWriter writer;
    BufferedReader reader;
    ArrayList<String> myRooms = new ArrayList<String>();
    //ArrayList<Rooms> rooms = new ArrayList<Rooms>();

    public ChatWindow(String user){
        login = user;

        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());

        // TODO: Add side panel again
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        JLabel rooms = new JLabel("Rooms:                     ");
        sidePanel.add(rooms);
        panel2.add(sidePanel, BorderLayout.WEST);

        textField = new JTextField();
        panel1.add(textField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton sendButton = new JButton("Send");
        sendButton.setOpaque(true);
        sendButton.setForeground(Color.blue);
        JButton clearButton = new JButton("Clear");
        clearButton.setOpaque(true);
        clearButton.setForeground(Color.red);
        JButton addRoom = new JButton("Add Room");
        addRoom.setOpaque(true);
        addRoom.setForeground(Color.gray);

        buttonPanel.add(sendButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(addRoom);
        panel1.add(buttonPanel, BorderLayout.EAST);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText("Current Room: General\n");
        panel2.add(textArea, BorderLayout.CENTER);
        panel2.add(panel1, BorderLayout.SOUTH);

        myRooms.add("General");
        JButton button = new JButton("General");
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        sidePanel.add(button);
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
            textArea.setText("");
            String thisRoom = myRooms.get(0);
            textArea.setText("Current Room: " + thisRoom + "\n");
          }
        });

        frame.setContentPane(panel2);

        try {
          Socket socketClient = new Socket("localhost", 4444);
          writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
          reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        } catch (Exception e) {
          e.printStackTrace();
        }

        Action sendAction = new AbstractAction() {
          public void actionPerformed(ActionEvent ev) {
            String message = login + " > " + textField.getText();
            textField.setText("");
            try {
              writer.write(message);
              writer.write("\r\n");
              writer.flush();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };
        textField.addActionListener(sendAction);
        sendButton.addActionListener(sendAction);

        clearButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
            textField.setText("");
          }
        });

        addRoom.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
              String s = (String)JOptionPane.showInputDialog(
              frame,
              "Enter Name of New Room: ",
              "New Room",
              JOptionPane.PLAIN_MESSAGE
            );

            if ((s != null) && (s.length() > 0)) {
              myRooms.add(s);
              sidePanel.removeAll();
              JLabel rooms = new JLabel("Rooms:                     ");
              sidePanel.add(rooms);
              for (int j = 0; j < myRooms.size(); j++) {
                final int num = j;
                JButton button = new JButton(myRooms.get(j));
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                sidePanel.add(button);
                button.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent ev) {
                    textArea.setText("");
                    String thisRoom = myRooms.get(num);
                    textArea.setText("Current Room: " + thisRoom + "\n");
                  }
                });
                textArea.setText("");
                textArea.setText("Current Room: " + s + "\n");
              }
              return;
            }
          }
        });

        frame.setVisible(true);
    }

    public void run() {
      try {
        String serverMsg = "";
        while ((serverMsg = reader.readLine()) != null) {
          textArea.append(serverMsg + "\n");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}