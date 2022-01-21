/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeopardy;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author USMAN
 */
public class ClientScreen extends javax.swing.JFrame {

    static boolean isSelected = false;
    InetAddress ip;
    Socket socket;
    final static int ServerPort = 1234;
    Thread readMessage;
    DataInputStream dis;
    DataOutputStream dos;
    String msgTS;
    String categoryMessage = "";
    boolean answerSubmitted = false;
    String clientName;
    boolean disableLabels = false;
//    copy the msfSt message 
    int msgState;
    int copyCategoryMessageStatus = 0;
//   will have 100 so if the state update the hover effects stays remain

    public ClientScreen() throws UnknownHostException, IOException {

        initComponents();
        clientName = "";
        msgState = 0;
        msgTS = "";
        buzzer.setEnabled(false);
        ip = InetAddress.getByName("localhost");
        socket = new Socket(ip, ServerPort);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        JLabel[][] LabelsArray = new JLabel[4][4];
        LabelsArray[0][0] = q00;
        LabelsArray[0][1] = q01;
        LabelsArray[0][2] = q02;
        LabelsArray[0][3] = q03;

        LabelsArray[1][0] = q10;
        LabelsArray[1][1] = q11;
        LabelsArray[1][2] = q12;
        LabelsArray[1][3] = q13;

        LabelsArray[2][0] = q20;
        LabelsArray[2][1] = q21;
        LabelsArray[2][2] = q22;
        LabelsArray[2][3] = q23;

        LabelsArray[3][0] = q30;
        LabelsArray[3][1] = q31;
        LabelsArray[3][2] = q32;
        LabelsArray[3][3] = q33;

        readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client 
                        String msg = dis.readUTF();
                        String[] mesSplit = msg.split("#");
                        msgState = Integer.parseInt(mesSplit[1]);

                        if (msgState == 15) {
                            if (mesSplit[0].charAt(0) == '-') {
                                MScreen.setForeground(Color.red);
                            } else {
                                MScreen.setForeground(Color.white);
                            }
                            MScreen.setText("$" + mesSplit[0]);
                        }

                        if (msgState == 114) {
                            msgTS = "116#Reset Category";
                            sendMsg();
                        }

                        if (msgState == 121) {

                            jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/board2.jpg")));
                            Qscreen1.setText(mesSplit[0]);
                            if (mesSplit[0].charAt(4) == 'C') {
                                StartFairyLights();
                            }
                        }
                        if (msgState == 500) {
//                            enable all labels
                            System.out.println(disableLabels);
                            disableLabels = false;
                            Qscreen1.setText(mesSplit[0]);

                        }

                        if (msgState == 1000) {
                            copyCategoryMessageStatus = 0;
                        }
                        
                        if (msgState != 3 && msgState != 15 && msgState != 114 && msgState != 121 && msgState != 500 && msgState != 1000) {
//                            System.out.println("my answer is wrong");
//                            System.out.println(mesSplit[0]);

                            Qscreen1.setText(mesSplit[0]);
                            if (mesSplit[0].equals("\n   Correct Answer!")) {

                                Screen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/screenG.jpg")));
                                Thread.sleep(1000);
                                Screen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/screen.jpg")));

                            }
                            if (mesSplit[0].equals("\n    Wrong Answer!")) {
                                System.out.println("my answer is wrong 1");
                                Screen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/screenR.jpg")));
                                Thread.sleep(1000);
                                Screen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/screen.jpg")));
                            }

                        }

//                        set the Timer if the waiting 
                        if (msgState == 2) {

                            buzzer.setBackground(Color.red);
                            startTimer(10);

                        }
                        if (msgState == 100) {
                            msgTS = "";
                            disableLabels = false;
                            copyCategoryMessageStatus = msgState;
//                          remove the perdefine message from the 
                            ACTION.setForeground(Color.red);
                            SCIENCE.setForeground(Color.red);
                            SPORTS.setForeground(Color.red);
                            ART.setForeground(Color.red);
                            isSelected = false;

                        }
//                        renove the selected the Label
//                       index +index +States
                        if (msgState % 10 == 6) {
                            LabelsArray[Integer.parseInt(mesSplit[1].charAt(0) + "")][Integer.parseInt(mesSplit[1].charAt(1) + "")].setText("");

//                            BUZZER
//                            reset the buzzer color
                            buzzer.setBackground(Color.red);
//                            Start time for 5 seconds and change color of buzzer to green turn flag to true
                            buzzerTime(10);
                            buzzer.setEnabled(true);

                        }

                        if (msgState == 3) {

                            System.out.println("Blinker on Karo ");
                            blinker(10);
                            System.out.println("Blinker Band karo");
                            submit.setEnabled(true);

                        }

                    } catch (IOException e) {

                        e.printStackTrace();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        readMessage.start();

    }

    public void sendMsg() {

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // write on the output stream 
                    dos.writeUTF(msgTS);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        sendMessage.start();

    }

    public void startTimer(int startTime) {
        Thread timer = new Thread(new Runnable() {
            int time = startTime;

            @Override
            public void run() {
                System.out.println("Timer start kr leya");
                while (true) {

                    clock.setText(time + "");
                    time--;

                    if (time < 0) {
                        System.out.println("Timer end kr ley");
                        clock.setText("");
                        break;
                    }

                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        });
        timer.start();
    }

    public void StartFairyLights() {
        Thread lights = new Thread(new Runnable() {

            int time = 0;

            @Override
            public void run() {

                while (true) {
                    time++;
// bgpryw
                    if (time % 2 == 0) {
                        try {
                            //3 sec
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flb.png")));
                            Thread.sleep(500);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flg.png")));
                            Thread.sleep(500);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flp.png")));
                            Thread.sleep(500);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flr.png")));
                            Thread.sleep(500);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/fly.png")));
                            Thread.sleep(500);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flw.png")));
                            Thread.sleep(500);

                        } catch (InterruptedException ex) {
                            Logger.getLogger(ClientScreen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            //3 sec
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flb.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flg.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flp.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flr.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/fly.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flw.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flb.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flg.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flp.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flr.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/fly.png")));
                            Thread.sleep(250);
                            fairyLights.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/flw.png")));
                            Thread.sleep(250);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ClientScreen.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }

            }

        });
        lights.start();
    }

    public void buzzerTime(int startTime) {
        Thread timer = new Thread(new Runnable() {
            int time = startTime;

            @Override
            public void run() {
                System.out.println("Buzzer ka time start hn gaya ");

                while (true) {

                    clock.setText(time + "");
                    time--;

                    if (time < 0) {
                        clock.setText("");
                        break;
                    }

                    if (time == 0) {
                        System.out.println("Time out message send krnay kr leya Ready");

                        if (copyCategoryMessageStatus == 100) {

                            msgTS = "102#" + clientName;
                            sendMsg();

                        }
                        System.out.println("Time out message send kr deya sever ko");
                        submit.setEnabled(false);
                        break;
                    }

                    try {
                        System.out.println("I sec ka break ky leya ready");
                        Thread.sleep(1000);
                        System.out.println("I sec ka break end");
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                buzzer.setEnabled(false);
            }

        });
        timer.start();
    }

    public void blinker(int startTime) {
        Thread timer = new Thread(new Runnable() {
            int time = startTime;

            @Override
            public void run() {

                while (true) {

                    if (answerSubmitted == false) {
                        clock.setText(time + "");
                        led.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/green.png")));
                        try {
                            Thread.sleep(500);

                        } catch (InterruptedException ex) {
                            Logger.getLogger(ClientScreen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        led.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/red.png")));

                        time--;

                        if (time == 0) {
//                      Qscreen1.setText("\n    Time's Up, You lost 100$");
                            System.out.println("Sending 'answer not given' message");
                            System.out.println("Kisi nay jawab ni dya message ready h");
                            msgTS = "104#" + clientName;
                            sendMsg();
                            System.out.println("Message send kr deya gaya ka jawab ni milla server ko");
                            break;
                        }

                        try {
                            System.out.println("Blinker thread 1 scn sleep ky leya Ready");
                            Thread.sleep(500);
                            System.out.println("Blinker thread 1 sec ka sleep done");
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ClientScreen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        clock.setText("0");
                        time = 0;
                        led.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/red.png")));
                        answerSubmitted = false;

                        break;

                        //msgTS = "105#" + clientName;
                    }

                }
            }

        });
        timer.start();
    }

    /**
     * n
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        led = new javax.swing.JLabel();
        MScreen = new javax.swing.JLabel();
        IScreen = new javax.swing.JLabel();
        board2 = new javax.swing.JLabel();
        board1 = new javax.swing.JLabel();
        Qscreen1 = new javax.swing.JTextArea();
        clock = new javax.swing.JLabel();
        submit = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        AScreen = new javax.swing.JTextField();
        Screen = new javax.swing.JLabel();
        buzzer = new javax.swing.JButton();
        q30 = new javax.swing.JLabel();
        q31 = new javax.swing.JLabel();
        q32 = new javax.swing.JLabel();
        q33 = new javax.swing.JLabel();
        q20 = new javax.swing.JLabel();
        q21 = new javax.swing.JLabel();
        q22 = new javax.swing.JLabel();
        q23 = new javax.swing.JLabel();
        q10 = new javax.swing.JLabel();
        q11 = new javax.swing.JLabel();
        q12 = new javax.swing.JLabel();
        q13 = new javax.swing.JLabel();
        q01 = new javax.swing.JLabel();
        q02 = new javax.swing.JLabel();
        q03 = new javax.swing.JLabel();
        q00 = new javax.swing.JLabel();
        ACTION = new javax.swing.JLabel();
        SCIENCE = new javax.swing.JLabel();
        SPORTS = new javax.swing.JLabel();
        ART = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        fairyLights = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1200, 700));
        setSize(new java.awt.Dimension(800, 800));
        getContentPane().setLayout(null);

        led.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/red.png"))); // NOI18N
        getContentPane().add(led);
        led.setBounds(590, 390, 40, 40);

        MScreen.setFont(new java.awt.Font("Arial Black", 1, 24)); // NOI18N
        MScreen.setForeground(java.awt.Color.white);
        MScreen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MScreen.setText("$0");
        getContentPane().add(MScreen);
        MScreen.setBounds(120, 20, 130, 40);

        IScreen.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        IScreen.setForeground(java.awt.Color.white);
        IScreen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        IScreen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(IScreen);
        IScreen.setBounds(950, 20, 130, 40);

        board2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/board1.jpg"))); // NOI18N
        getContentPane().add(board2);
        board2.setBounds(940, 0, 160, 80);

        board1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/board1.jpg"))); // NOI18N
        getContentPane().add(board1);
        board1.setBounds(110, 0, 160, 80);

        Qscreen1.setEditable(false);
        Qscreen1.setBackground(new java.awt.Color(0, 0, 0));
        Qscreen1.setColumns(20);
        Qscreen1.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        Qscreen1.setForeground(new java.awt.Color(255, 255, 255));
        Qscreen1.setRows(5);
        Qscreen1.setBorder(null);
        getContentPane().add(Qscreen1);
        Qscreen1.setBounds(360, 435, 520, 60);

        clock.setFont(new java.awt.Font("Arial Black", 1, 48)); // NOI18N
        clock.setForeground(new java.awt.Color(0, 0, 102));
        clock.setText("9");
        getContentPane().add(clock);
        clock.setBounds(590, 40, 35, 50);

        submit.setBackground(new java.awt.Color(204, 0, 0));
        submit.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        submit.setForeground(new java.awt.Color(255, 255, 255));
        submit.setText("Submit");
        submit.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.black, java.awt.Color.darkGray));
        submit.setBorderPainted(false);
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitActionPerformed(evt);
            }
        });
        getContentPane().add(submit);
        submit.setBounds(700, 510, 90, 40);

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/clock1.png"))); // NOI18N
        getContentPane().add(jLabel24);
        jLabel24.setBounds(550, 0, 150, 130);

        AScreen.setBackground(new java.awt.Color(0, 0, 0));
        AScreen.setForeground(new java.awt.Color(255, 255, 255));
        AScreen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 0)));
        AScreen.setHighlighter(null);
        AScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AScreenActionPerformed(evt);
            }
        });
        getContentPane().add(AScreen);
        AScreen.setBounds(450, 510, 250, 40);

        Screen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/screen.jpg"))); // NOI18N
        Screen.setText("jLabel23");
        getContentPane().add(Screen);
        Screen.setBounds(320, 420, 590, 90);

        buzzer.setBackground(new java.awt.Color(204, 0, 0));
        buzzer.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        buzzer.setForeground(new java.awt.Color(255, 255, 255));
        buzzer.setText("BUZZER!");
        buzzer.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.darkGray, java.awt.Color.red, java.awt.Color.white, java.awt.Color.red));
        buzzer.setBorderPainted(false);
        buzzer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buzzerMouseClicked(evt);
            }
        });
        buzzer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buzzerActionPerformed(evt);
            }
        });
        getContentPane().add(buzzer);
        buzzer.setBounds(520, 560, 200, 70);

        q30.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q30.setForeground(new java.awt.Color(255, 153, 0));
        q30.setText("$1000");
        q30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q30MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q30MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q30MouseExited(evt);
            }
        });
        getContentPane().add(q30);
        q30.setBounds(430, 330, 70, 40);

        q31.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q31.setForeground(new java.awt.Color(255, 153, 0));
        q31.setText("$1000");
        q31.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q31MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q31MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q31MouseExited(evt);
            }
        });
        getContentPane().add(q31);
        q31.setBounds(520, 330, 70, 40);

        q32.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q32.setForeground(new java.awt.Color(255, 153, 0));
        q32.setText("$1000");
        q32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q32MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q32MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q32MouseExited(evt);
            }
        });
        getContentPane().add(q32);
        q32.setBounds(620, 330, 70, 40);

        q33.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q33.setForeground(new java.awt.Color(255, 153, 0));
        q33.setText("$1000");
        q33.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q33MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q33MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q33MouseExited(evt);
            }
        });
        getContentPane().add(q33);
        q33.setBounds(710, 330, 70, 40);

        q20.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q20.setForeground(new java.awt.Color(255, 153, 0));
        q20.setText("$600");
        q20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q20MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q20MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q20MouseExited(evt);
            }
        });
        getContentPane().add(q20);
        q20.setBounds(430, 290, 60, 40);

        q21.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q21.setForeground(new java.awt.Color(255, 153, 0));
        q21.setText("$600");
        q21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q21MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q21MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q21MouseExited(evt);
            }
        });
        getContentPane().add(q21);
        q21.setBounds(520, 290, 60, 40);

        q22.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q22.setForeground(new java.awt.Color(255, 153, 0));
        q22.setText("$600");
        q22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q22MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q22MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q22MouseExited(evt);
            }
        });
        getContentPane().add(q22);
        q22.setBounds(620, 290, 60, 40);

        q23.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q23.setForeground(new java.awt.Color(255, 153, 0));
        q23.setText("$600");
        q23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q23MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q23MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q23MouseExited(evt);
            }
        });
        getContentPane().add(q23);
        q23.setBounds(710, 290, 60, 40);

        q10.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q10.setForeground(new java.awt.Color(255, 153, 0));
        q10.setText("$400");
        q10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q10MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q10MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q10MouseExited(evt);
            }
        });
        getContentPane().add(q10);
        q10.setBounds(430, 250, 60, 40);

        q11.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q11.setForeground(new java.awt.Color(255, 153, 0));
        q11.setText("$400");
        q11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q11MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q11MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q11MouseExited(evt);
            }
        });
        getContentPane().add(q11);
        q11.setBounds(520, 250, 60, 40);

        q12.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q12.setForeground(new java.awt.Color(255, 153, 0));
        q12.setText("$400");
        q12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q12MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q12MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q12MouseExited(evt);
            }
        });
        getContentPane().add(q12);
        q12.setBounds(620, 250, 60, 40);

        q13.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q13.setForeground(new java.awt.Color(255, 153, 0));
        q13.setText("$400");
        q13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q13MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q13MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q13MouseExited(evt);
            }
        });
        getContentPane().add(q13);
        q13.setBounds(710, 250, 60, 40);

        q01.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q01.setForeground(new java.awt.Color(255, 153, 0));
        q01.setText("$200");
        q01.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q01MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q01MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q01MouseExited(evt);
            }
        });
        getContentPane().add(q01);
        q01.setBounds(520, 210, 60, 40);

        q02.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q02.setForeground(new java.awt.Color(255, 153, 0));
        q02.setText("$200");
        q02.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q02MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q02MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q02MouseExited(evt);
            }
        });
        getContentPane().add(q02);
        q02.setBounds(620, 210, 60, 40);

        q03.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q03.setForeground(new java.awt.Color(255, 153, 0));
        q03.setText("$200");
        q03.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q03MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q03MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q03MouseExited(evt);
            }
        });
        getContentPane().add(q03);
        q03.setBounds(710, 210, 60, 40);

        q00.setFont(new java.awt.Font("Arial Black", 1, 18)); // NOI18N
        q00.setForeground(new java.awt.Color(255, 153, 0));
        q00.setText("$200");
        q00.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                q00MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                q00MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                q00MouseExited(evt);
            }
        });
        getContentPane().add(q00);
        q00.setBounds(430, 210, 60, 40);

        ACTION.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        ACTION.setForeground(new java.awt.Color(255, 255, 255));
        ACTION.setText("ACTION");
        ACTION.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ACTIONMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ACTIONMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ACTIONMouseExited(evt);
            }
        });
        getContentPane().add(ACTION);
        ACTION.setBounds(430, 160, 78, 50);

        SCIENCE.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        SCIENCE.setForeground(new java.awt.Color(255, 255, 255));
        SCIENCE.setText("SCIENCE");
        SCIENCE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SCIENCEMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SCIENCEMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SCIENCEMouseExited(evt);
            }
        });
        getContentPane().add(SCIENCE);
        SCIENCE.setBounds(520, 160, 90, 50);

        SPORTS.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        SPORTS.setForeground(new java.awt.Color(255, 255, 255));
        SPORTS.setText("SPORTS");
        SPORTS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SPORTSMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SPORTSMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SPORTSMouseExited(evt);
            }
        });
        getContentPane().add(SPORTS);
        SPORTS.setBounds(620, 160, 80, 50);

        ART.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        ART.setForeground(new java.awt.Color(255, 255, 255));
        ART.setText("ART");
        ART.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ARTMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ARTMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ARTMouseExited(evt);
            }
        });
        getContentPane().add(ART);
        ART.setBounds(720, 160, 40, 50);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/board.jpg"))); // NOI18N
        getContentPane().add(jLabel3);
        jLabel3.setBounds(390, 90, 450, 340);
        getContentPane().add(fairyLights);
        fairyLights.setBounds(0, 90, 1210, 480);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeopardy/images/bg2.jpg"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(10, 0, 1197, 670);

        jLabel2.setText("jLabel2");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(590, 410, 34, 15);

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buzzerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buzzerActionPerformed

        if ((msgState % 10 == 6)) {

//                  change Color To green
            buzzer.setBackground(Color.green);
            buzzer.setEnabled(false);
            msgTS = "101#" + clientName;
            sendMsg();
        }

    }//GEN-LAST:event_buzzerActionPerformed
    boolean check = false;
    private void submitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitActionPerformed

        if (msgState == 69) {
            clientName = AScreen.getText();
            IScreen.setText(clientName);
            msgTS = "0#" + AScreen.getText();
            AScreen.setText("");
            sendMsg();
            submit.setEnabled(false);
        }

        if (msgState == 3) {
            System.out.println("I Have answered the question and sendind answer 103");
            answerSubmitted = true;
            msgTS = "103#" + AScreen.getText();
            sendMsg();
            submit.setEnabled(false);
            AScreen.setText("");
        }

    }//GEN-LAST:event_submitActionPerformed

    private void AScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AScreenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AScreenActionPerformed

    private void ACTIONMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ACTIONMouseEntered
        if (copyCategoryMessageStatus == 100 && categoryMessage.length() == 0) {

            ACTION.setForeground(Color.white);

        }

    }//GEN-LAST:event_ACTIONMouseEntered

    private void SCIENCEMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SCIENCEMouseEntered

        if (copyCategoryMessageStatus == 100 && categoryMessage.length() == 0) {

            SCIENCE.setForeground(Color.white);

        }
    }//GEN-LAST:event_SCIENCEMouseEntered

    private void SPORTSMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SPORTSMouseEntered
        if (copyCategoryMessageStatus == 100 && categoryMessage.length() == 0) {

            SPORTS.setForeground(Color.white);

        }
    }//GEN-LAST:event_SPORTSMouseEntered

    private void ARTMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ARTMouseEntered
        if (copyCategoryMessageStatus == 100 && categoryMessage.length() == 0) {

            ART.setForeground(Color.white);

        }
    }//GEN-LAST:event_ARTMouseEntered

    private void ACTIONMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ACTIONMouseExited
        if (copyCategoryMessageStatus == 100 && categoryMessage.length() == 0) {

            ACTION.setForeground(Color.red);

        }
    }//GEN-LAST:event_ACTIONMouseExited

    private void SCIENCEMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SCIENCEMouseExited
        if (copyCategoryMessageStatus == 100 && categoryMessage.length() == 0) {

            SCIENCE.setForeground(Color.red);

        }
    }//GEN-LAST:event_SCIENCEMouseExited

    private void SPORTSMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SPORTSMouseExited

        if (copyCategoryMessageStatus == 100 && categoryMessage.length() == 0) {

            SPORTS.setForeground(Color.red);

        }
    }//GEN-LAST:event_SPORTSMouseExited

    private void ARTMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ARTMouseExited

        if (copyCategoryMessageStatus == 100 && categoryMessage.length() == 0) {

            ART.setForeground(Color.red);

        }
    }//GEN-LAST:event_ARTMouseExited

    private void ACTIONMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ACTIONMouseClicked

        if (copyCategoryMessageStatus == 100 && isSelected == false) {
            msgTS = "100#ACTION";
            isSelected = true;
            categoryMessage = "100#ACTION";
            sendMsg();
            Qscreen1.setText("\n    Category 'ACTION' selected.");
            ACTION.setForeground(Color.white);
            SCIENCE.setForeground(Color.white);
            SPORTS.setForeground(Color.white);
            ART.setForeground(Color.white);

//            labels color for questions
            q00.setForeground(Color.red);
            q10.setForeground(Color.red);
            q20.setForeground(Color.red);
            q30.setForeground(Color.red);

//            Disable all category except the selected one
//            ACTION.setEnabled(true);
//            SCIENCE.setEnabled(false);
//            SPORTS.setEnabled(false);
//            ART.setEnabled(false);
//            System.out.println("I am Clicked");
        }


    }//GEN-LAST:event_ACTIONMouseClicked

    private void SCIENCEMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SCIENCEMouseClicked

        if (copyCategoryMessageStatus == 100 && isSelected == false) {
            msgTS = "100#SCIENCE";
            isSelected = true;
            categoryMessage = "100#SCIENCE";
            sendMsg();
            Qscreen1.setText("\n    Category 'SCIENCE' selected.");
            ACTION.setForeground(Color.white);
            SCIENCE.setForeground(Color.white);
            SPORTS.setForeground(Color.white);
            ART.setForeground(Color.white);

            q01.setForeground(Color.red);
            q11.setForeground(Color.red);
            q21.setForeground(Color.red);
            q31.setForeground(Color.red);
            //            Disable all category except the selected one
//            ACTION.setEnabled(false);
////            SCIENCE.setEnabled(false);
//            SPORTS.setEnabled(false);
//            ART.setEnabled(false);

        }
    }//GEN-LAST:event_SCIENCEMouseClicked

    private void SPORTSMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SPORTSMouseClicked

        if (copyCategoryMessageStatus == 100 && isSelected == false) {
            msgTS = "100#SPORTS";
            isSelected = true;
            categoryMessage = "100#SPORTS";
            sendMsg();
            Qscreen1.setText("\n    Category 'SPORTS' selected.");
            ACTION.setForeground(Color.white);
            SCIENCE.setForeground(Color.white);
            SPORTS.setForeground(Color.white);
            ART.setForeground(Color.white);

            q02.setForeground(Color.red);
            q12.setForeground(Color.red);
            q22.setForeground(Color.red);
            q32.setForeground(Color.red);

            //            Disable all category except the selected one
//            ACTION.setEnabled(false);
//            SCIENCE.setEnabled(false);
////            SPORTS.setEnabled(false);
//            ART.setEnabled(false);
        }
    }//GEN-LAST:event_SPORTSMouseClicked

    private void ARTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ARTMouseClicked

        if (copyCategoryMessageStatus == 100 && isSelected == false) {
            msgTS = "100#ART";
            categoryMessage = "100#ART";
            isSelected = true;
            sendMsg();
            Qscreen1.setText("\n    Category 'ART' selected.");
            ACTION.setForeground(Color.white);
            SCIENCE.setForeground(Color.white);
            SPORTS.setForeground(Color.white);
            ART.setForeground(Color.white);

            q03.setForeground(Color.red);
            q13.setForeground(Color.red);
            q23.setForeground(Color.red);
            q33.setForeground(Color.red);
            //            Disable all category except the selected one
//            ACTION.setEnabled(false);
//            SCIENCE.setEnabled(false);
//            SPORTS.setEnabled(false);
//            ART.setEnabled(false);

        }
    }//GEN-LAST:event_ARTMouseClicked

//    send the Selected Label name . Get the Price need be send to server 
//    on category selection apply hover effect to the Labels using category Last Char
    private void q00MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q00MouseEntered
        // TODO add your handling code here:
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q00.setForeground(Color.white);
            }
        }


    }//GEN-LAST:event_q00MouseEntered

    private void q00MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q00MouseExited
        // TODO add your handling code here:

        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q00.setForeground(Color.red);
            }
        }

    }//GEN-LAST:event_q00MouseExited

    private void q10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q10MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q10.setForeground(Color.white);
            }
        }


    }//GEN-LAST:event_q10MouseEntered

    private void q10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q10MouseExited

        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q10.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q10MouseExited

    private void q20MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q20MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q20.setForeground(Color.white);
            }
        }

    }//GEN-LAST:event_q20MouseEntered

    private void q20MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q20MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q20.setForeground(Color.red);
            }
        }

    }//GEN-LAST:event_q20MouseExited

    private void q30MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q30MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q30.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q30MouseEntered

    private void q30MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q30MouseExited
        // TODO add your handling code here:
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q30.setForeground(Color.red);
            }
        }

    }//GEN-LAST:event_q30MouseExited

//    Science

    private void q01MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q01MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q01.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q01MouseEntered

    private void q01MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q01MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q01.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q01MouseExited

    private void q11MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q11MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q11.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q11MouseEntered

    private void q11MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q11MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q11.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q11MouseExited

    private void q21MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q21MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q21.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q21MouseEntered

    private void q21MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q21MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q21.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q21MouseExited

    private void q31MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q31MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q31.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q31MouseEntered

    private void q31MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q31MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q31.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q31MouseExited

//    Sports
    private void q02MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q02MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q02.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q02MouseEntered

    private void q02MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q02MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q02.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q02MouseExited

    private void q12MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q12MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q12.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q12MouseEntered

    private void q12MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q12MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q12.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q12MouseExited

    private void q22MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q22MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q22.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q22MouseEntered

    private void q22MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q22MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q22.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q22MouseExited

    private void q32MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q32MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q32.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q32MouseEntered

    private void q32MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q32MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q32.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q32MouseExited

//    Art
    private void q03MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q03MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q03.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q03MouseEntered

    private void q03MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q03MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q03.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q03MouseExited

    private void q13MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q13MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q13.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q13MouseEntered

    private void q13MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q13MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q13.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q13MouseExited

    private void q23MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q23MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q23.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q23MouseEntered

    private void q23MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q23MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q23.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q23MouseExited

    private void q33MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q33MouseEntered
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q33.setForeground(Color.white);
            }
        }
    }//GEN-LAST:event_q33MouseEntered

    private void q33MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q33MouseExited
        if (categoryMessage.length() == 0) {
        } else {
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q33.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_q33MouseExited

//    mouse Clicked
    private void q00MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q00MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q00.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q00.setText("");
                msgTS = "150#00";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        }

    }//GEN-LAST:event_q00MouseClicked

    private void q10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q10MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            disableLabels = true;
            String test = q10.getText();

            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q10.setText("");
                msgTS = "150#10";
                sendMsg();
            }
        }
        if (categoryMessage.length() == 0) {
        } else {

        }

    }//GEN-LAST:event_q10MouseClicked

    private void q20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q20MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q20.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q20.setText("");
                msgTS = "150#20";
                sendMsg();
            }
        }
        if (categoryMessage.length() == 0) {
        } else {

        }

    }//GEN-LAST:event_q20MouseClicked

    private void q30MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q30MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q30.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('N')) {
                q30.setText("");
                msgTS = "150#30";
                sendMsg();
            }
        }
        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q30MouseClicked

    private void q01MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q01MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q01.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q01.setText("");
                msgTS = "150#01";
                sendMsg();
            }
        }
        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q01MouseClicked

    private void q11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q11MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q11.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q11.setText("");
                msgTS = "150#11";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q11MouseClicked

    private void q21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q21MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q21.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q21.setText("");
                msgTS = "150#21";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q21MouseClicked

    private void q31MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q31MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q31.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('E')) {
                q31.setText("");
                msgTS = "150#31";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q31MouseClicked

    private void q02MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q02MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q02.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q02.setText("");
                msgTS = "150#02";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q02MouseClicked

    private void q12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q12MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q12.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q12.setText("");
                msgTS = "150#12";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q12MouseClicked

    private void q22MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q22MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q22.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q22.setText("");
                msgTS = "150#22";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q22MouseClicked

    private void q32MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q32MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            disableLabels = true;
            String test = q32.getText();

            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('S')) {
                q32.setText("");
                msgTS = "150#32";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q32MouseClicked

    private void q03MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q03MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            disableLabels = true;
            String test = q03.getText();

            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q03.setText("");
                msgTS = "150#03";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q03MouseClicked

    private void q13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q13MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q13.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q13.setText("");
                msgTS = "150#13";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q13MouseClicked

    private void q23MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q23MouseClicked
        if (!categoryMessage.isEmpty() && disableLabels == false) {
            String test = q23.getText();
            disableLabels = true;
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q23.setText("");
                msgTS = "150#23";
                sendMsg();
            }
        }

        if (categoryMessage.length() == 0) {
        } else {

        }
    }//GEN-LAST:event_q23MouseClicked

    private void q33MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_q33MouseClicked

        if (!categoryMessage.isEmpty() && disableLabels == false) {
            disableLabels = true;
            String test = q33.getText();
            if (categoryMessage.charAt((categoryMessage.length()) - 1) == ('T')) {
                q33.setText("");
                msgTS = "150#33";
                sendMsg();
            }
        }
        if (categoryMessage.length() == 0) {
        }
    }//GEN-LAST:event_q33MouseClicked

    private void buzzerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buzzerMouseClicked

//      
    }//GEN-LAST:event_buzzerMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws UnknownHostException, IOException {

        ClientScreen s1 = new ClientScreen();
        s1.setVisible(true);
        // getting localhost ip 
        // establish the connection
        // obtaining input and out streams 
        // sendMessage thread 
        // readMessage thread 
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ACTION;
    private javax.swing.JLabel ART;
    private javax.swing.JTextField AScreen;
    private javax.swing.JLabel IScreen;
    private javax.swing.JLabel MScreen;
    private javax.swing.JTextArea Qscreen1;
    private javax.swing.JLabel SCIENCE;
    private javax.swing.JLabel SPORTS;
    private javax.swing.JLabel Screen;
    private javax.swing.JLabel board1;
    private javax.swing.JLabel board2;
    private javax.swing.JButton buzzer;
    private javax.swing.JLabel clock;
    private javax.swing.JLabel fairyLights;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel led;
    private javax.swing.JLabel q00;
    private javax.swing.JLabel q01;
    private javax.swing.JLabel q02;
    private javax.swing.JLabel q03;
    private javax.swing.JLabel q10;
    private javax.swing.JLabel q11;
    private javax.swing.JLabel q12;
    private javax.swing.JLabel q13;
    private javax.swing.JLabel q20;
    private javax.swing.JLabel q21;
    private javax.swing.JLabel q22;
    private javax.swing.JLabel q23;
    private javax.swing.JLabel q30;
    private javax.swing.JLabel q31;
    private javax.swing.JLabel q32;
    private javax.swing.JLabel q33;
    private javax.swing.JButton submit;
    // End of variables declaration//GEN-END:variables
}
