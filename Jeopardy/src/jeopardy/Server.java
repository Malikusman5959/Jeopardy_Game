/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeopardy;

/**
 *
 * @author USMAN
 */
// Java implementation of Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Server class 
public class Server {

    // Vector to store active clients 
    static Vector<ClientHandler> ar = new Vector<>();
    static ArrayList<String> buzzerSeq = new ArrayList<String>();
    static String category[] = new String[4];
    static String questions[][] = new String[4][4];
    static String answers[][] = new String[4][4];
    static int amount[][] = {{200, 200, 200, 200}, {400, 400, 400, 400}, {600, 600, 600, 600}, {1000, 1000, 1000, 1000}};
    static boolean helpingVar = true;
    static int index = -1;

    static int ans_index_0;
    static int ans_index_1;

    static int trackQuestion = 0;
    static int trackRound = 0;
    static boolean resetCategory = false;
    static boolean isExecuted = false;

    // counter for clients 
    static int i = 0;
    static int a;

    public static void main(String[] args) throws IOException {
        // server is listening on port 1234 

        category[0] = "ACTIONS";
        category[1] = "SCIENCE";
        category[2] = "SPORT";
        category[3] = "ACCORD";

        //category 1
        questions[0][0] = "After one of your checkers reaches your opponent's first row\ndo this to your piece to show it's now a king";
        answers[0][0] = "Top it on the other piece";
        questions[1][0] = "Steph Curry is one of many athletes who tap their chests &\nthen do this to thank God after a successful play";
        answers[1][0] = "LOOK UP";
        questions[2][0] = "In dancing the Lindy Hop the kaye is this type of move to\nbe done carefully & without surprising your partner";
        answers[2][0] = "A Dip";
        questions[3][0] = "In knitting it means to pull a loop of yarn through the front\nto the back of the fabric";
        answers[3][0] = "Purl";
        //category 2
        questions[0][1] = "In 2019 using the Event Horizon Telescope the first of these\ncosmic objects was photographed";
        answers[0][1] = "Black Hole";
        questions[1][1] = "A study at Johns Hopkins suggests that being around dogs at\nan early age may lessen the risk of this split mind disorder";
        answers[1][1] = "Schizophernia";
        questions[2][1] = "RBBP6 one of these compounds that contain amino acids has\nproven effective against Ebola";
        answers[2][1] = "Protein";
        questions[3][1] = "Developers of this hyphenated type of battery that powers\nsmartphones & electric cars won the 2019 Nobel Chemistry Prize";
        answers[3][1] = "Lithium-ion";
        //category 3
        questions[0][2] = "I hereby challenge Pat Sajak to this sport where we'll\nattempt to stay on a spinning piece of floating wood";
        answers[0][2] = "Log Rolling";
        questions[1][2] = "Armand Duplantis broke the world record in this sport on 2\nconsecutive Saturdays in 2020 the second time clearing 20' 3";
        answers[1][2] = "Pole Vaulting";
        questions[2][2] = "In Basque & Spanish regions pelota vasca is another name for\nthis fast-moving game";
        answers[2][2] = "Jai Alai";
        questions[3][2] = "Shooting swimming & tossing a grenade-like projectile are\nincluded in the military version of this 5-event sport";
        answers[3][2] = "Pentathlon";
        //category 4
        questions[0][3] = "The 1995 Dayton Accords ended the war in Bosnia that followed\nthe breakup of this Balkan republic";
        answers[0][3] = "Yugoslavia";
        questions[1][3] = "These 1978 accords named for a presidential retreat led to a\n1979 peace treaty between Israel & Egypt";
        answers[1][3] = "The Camp david Accords";
        questions[2][3] = "In 1951 the Treasury Department reached an accord with this\nother institution allowing the latter to pursue its own\nmonetary policy";
        answers[2][3] = "The federal Reserve";
        questions[3][3] = "The 1985 Plaza Accord devalued the dollar in relation to this the\ncurrency of the only Asian G-5 member";
        answers[3][3] = "The Yen";

//      
        Server.randomNumber();
        System.out.println("Random before the value : " + a);

        ServerSocket ss = new ServerSocket(1234);

        Socket s;
        ClientHandler mtch;

        // running infinite loop for getting 
        // client request 
        while (true) {

            // Accept the incoming request 
            s = ss.accept();
            System.out.println("\n  New client request received : " + s);

            // obtain input and output streams 
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            if (ar.size() < 3) {
                dos.writeUTF("\n  You Entered the Game! Kindly submit your name below#69");

                // Create a new handler object for handling this request. 
                mtch = new ClientHandler(s, "", i, dis, dos);

                // Create a new Thread with this object. 
                Thread t = new Thread(mtch);

                System.out.println("Adding this client to active client list");

                // add this client to active clients list 
                ar.add(mtch);

                // start the thread. 
                t.start();

                // increment i for new client. 
                // i is used for naming only, and can be replaced 
                // by any naming scheme 
                i++;
            } else {
                dos.writeUTF("\n  The Game is full! You cannot enter the game." + "#0");
                s.close();
            }

        }
    }

    public static void randomNumber() {
        a = (int) (Math.random() * 3) + 0;
    }
    public static ArrayList<String> removeDuplicates(ArrayList<String> list) 
    { 
  
        // Create a new ArrayList 
        ArrayList<String> newList = new ArrayList<String>(); 
  
        // Traverse through the first list 
        for (int x =0 ; x< list.size();x++) { 
  
            // If this element is not present in newList 
            // then add it 
            if (!newList.contains(list.get(x))) { 
  
                newList.add(list.get(x)); 
            } 
        } 
  
        // return the new list 
        return newList; 
    } 
}

// ClientHandler class 
class ClientHandler implements Runnable {

    private String name;
    public int state;
    public int credit;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;
    int i = 0;	// constructor 
    double buzzerTime;
    boolean allow = true;

    public ClientHandler(Socket s, String name, int i, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.credit = 0;
        this.s = s;
        this.isloggedin = true;
        this.i = i;
        this.state = 0;
        this.buzzerTime = 0.0;
    }

    @Override
    public void run() {

        String received;
        int x = 0;
        while (true) {
            try {
                // receive the string 
                received = dis.readUTF();
                System.out.println("Message from Client : :  "+ received);
                System.out.println("___________ Status _________________");
                for(int c = 0;c< Server.ar.size();c++){
                    System.out.println(Server.ar.get(c).name +"  :  " + Server.ar.get(c).state);
                }
                
                if (state == -1) {
                    this.s.close();
                    break;
                }

//        ________________Split the Client Message_______________________________
                String[] splitText = received.split("#");
                int checkMsgType = Integer.parseInt(splitText[0]);

                if (checkMsgType == 0) {

                    if (splitText.length == 1) {
                        this.dos.writeUTF("Name Required #" + state);
                    } else {
                        Server.ar.get(i).name = splitText[1];
                        state = 1;
                        this.dos.writeUTF("\n  " + Server.ar.get(i).name + ", Please wait for other players#" + state);
                    }
                }

//        ________________Client Request phase is completed_______________________________
                if (Server.ar.size() == 3 && state == 1) {

                    if ((Server.ar.get(0).state == 0) || (Server.ar.get(1).state == 0) || (Server.ar.get(2).state == 0)) {

                    } else {
                        for (int p = 0; p < Server.ar.size(); p++) {
                            Server.ar.get(p).state = 2;
                        }

                    }
                }

//        ________________Make Sure that All the Clients name has received by the server_______________________________
                if ((Server.ar.get(0).state == 2) && (Server.ar.get(1).state == 2) && (Server.ar.get(2).state == 2)) {

                    for (ClientHandler mc : Server.ar) {
                        mc.dos.writeUTF("\n  " + mc.name + ", the Game will start in 10 Sec #" + state);
                    }

//                        wait  for 10 sec
                    Thread.sleep(10000);
                    for (int p = 0; p < Server.ar.size(); p++) {
                        Server.ar.get(p).state = 3;
                    }
                }

//        ________________RESET THE CETOGORY_______________________________
                if (Server.resetCategory && checkMsgType == 116) {

                    Server.randomNumber();
                    Server.resetCategory = false;
                }

                int a = Server.a;

//        ________________Send message to the Client, that who is selecting the category_______________________________
                if (state == 3) {

                    for (int p = 0; p < Server.ar.size(); p++) {
                        if (p == a) {
                            Server.ar.get(p).dos.writeUTF("\n  " + Server.ar.get(p).name + ", please select the Category#100");
                        } else {
                            Server.ar.get(p).dos.writeUTF("\n  " + Server.ar.get(a).name + " is selecting the Category plz wait#1000");
                        }

                    }

                    for (int p = 0; p < Server.ar.size(); p++) {
                        Server.ar.get(p).state = 4;
                    }
                }

//        ________________Inform each Client about the Category selection_______________________________
                if (checkMsgType == 100) {
                    for (int p = 0; p < Server.ar.size(); p++) {
                        if (p == a) {
                        } else {
                            Server.ar.get(p).dos.writeUTF("\n  " + Server.ar.get(a).name + " has selected '" + splitText[1] + "' Category#" + state);
                        }
                    }
                    state = 5;
                }
//        ________________Message_______________________________

                if (checkMsgType == 150) {

                    for (int p = 0; p < Server.ar.size(); p++) {
                        Server.ar.get(p).dos.writeUTF("\n  " + " Question will be displayed in 10s #2");
                    }

                    Thread.sleep(11000);

//        ________________STATE == 6_______________________________
                    for (int p = 0; p < Server.ar.size(); p++) {
                        Server.ar.get(p).state = 6;
                    }

                }

//        ________________Question recivied && Store the Index of Recived Question_______________________________
                if (state == 6) {
                    Server.isExecuted = false;

                    System.out.println("Label Name  :" + splitText[1]);

                    Server.trackQuestion += 1;

                    int index_0 = Integer.parseInt("" + splitText[1].charAt(0));
                    Server.ans_index_0 = index_0;

                    int index_1 = Integer.parseInt("" + splitText[1].charAt(1));
                    Server.ans_index_1 = index_1;

                    for (int p = 0; p < Server.ar.size(); p++) {
                        Server.ar.get(p).dos.writeUTF(Server.questions[index_0][index_1] + "\n(" + Server.answers[index_0][index_1] + ")" + "#" + "" + index_0 + "" + index_1 + "" + state);
                    }

//        ________________STATE == 7_______________________________
                    for (int p = 0; p < Server.ar.size(); p++) {
                        Server.ar.get(p).state = 7;
                    }
                }
//        ________________Add Client to buzzer array on clicking the buzzer btn_______________________________

                if (checkMsgType == 101 && state == 7) {

                    Server.buzzerSeq.add(splitText[1]);

                }

//        ________________Buzzer time out_______________________________
                if ((checkMsgType == 102 && Server.ar.get(Server.a).name.equals(splitText[1]))) {
//                  Remove the Clients Duplication for the Buzzer

                        Server.buzzerSeq=Server.removeDuplicates(Server.buzzerSeq);
                    
                    
                    
                    for (int p = 0; p < Server.buzzerSeq.size(); p++) {
                        System.out.println(p + " : "+ Server.buzzerSeq.get(p));
                    }
                    
                    for (int p = 0; p < Server.ar.size(); p++) {
                        Server.ar.get(p).state = 8;
                    }
//        ________________Select the index == 0 client from the buzzer Array_______________________________

                    if (!Server.buzzerSeq.isEmpty()) {
                        for (int p = 0; p < Server.ar.size(); p++) {
                            if ((Server.buzzerSeq.get(0)).equals(Server.ar.get(p).name)) {
                                Server.index = p;
                            }
                        }
                    } //        ________________state == 10 if no client in the buzzer array_______________________________
                    else {

                        for (int p = 0; p < Server.ar.size(); p++) {
                            System.out.println("Question skiped no one ans Line 330");
                            Server.ar.get(p).dos.writeUTF("\n   Questioned Skipped, Because no one answered!#0");
                            System.out.println("Question skiped no one ans sended Line 330");
                            Thread.sleep(1000);
                            Server.ar.get(p).state = 10;

                        }

                    }
                }

                if ((checkMsgType == 102 && !Server.ar.get(Server.a).name.equals(splitText[1]))) {
                    System.out.println("Go back   :: " + splitText[1] );
                    continue;
                }

                if (state == 8 && !Server.buzzerSeq.isEmpty() && Server.buzzerSeq.get(0).equals(Server.ar.get(Server.index).name)) {
                    System.out.println("Enter Status --------------------- 08");
                    if (Server.buzzerSeq.isEmpty()) {

                        for (int p = 0; p < Server.ar.size(); p++) {
                           
                            System.out.println("Question Skipping Statment ready to send");
                            Server.ar.get(p).dos.writeUTF("\n   Questioned Skipped, Because no one answered!#0");
                            System.out.println("Question Skipping Statment sended line 352");
                            Server.ar.get(p).state = 10;
                            Thread.sleep(1000);

                        }
                    } else {
                        System.out.println("Before if Line 360");
                        System.out.println("Helping var  " +Server.helpingVar);
                        if (Server.helpingVar == true) {
                            System.out.println("stats 03 sending line 363");
                            Server.ar.get(Server.index).dos.writeUTF(Server.ar.get(Server.index).name + "#3");
                           System.out.println("stats 03 sended line 363");
                            for (int p = 0; p < Server.ar.size(); p++) {
                                if (p != Server.index) {
                                    System.out.println("____ is ans the question");
                                    Server.ar.get(p).dos.writeUTF("\n    " + Server.ar.get(Server.index).name + " is answering the question. #0");
                                    System.out.println("____ is answered the question line 366");
                                }
                            }
                            Server.helpingVar = false;

                        }
                        System.out.println("After If Line 376");
                    }

                }
///        ________________state == 8 Client Allowed to ans Client responce will be either time out or ans____________________

                if (state == 8 && ((checkMsgType == 103) || (checkMsgType == 104))) {
                    Server.buzzerSeq.remove(0);
                    System.out.println("In if Line 384");
                    Server.ar.get(Server.index).state = 9;
                    
                    System.out.println("Before if Line 387");
                    if (splitText[1].equalsIgnoreCase(Server.answers[Server.ans_index_0][Server.ans_index_1]) && checkMsgType == 103) {
                        String nName = "";
                        System.out.println("In if Line 390");
                        for (int p = 0; p < Server.ar.size(); p++) {

                            if (Server.ar.get(p).state == 9) {
                                System.out.println("Ans Correct");
                                Server.ar.get(p).dos.writeUTF("\n   Correct Answer!" + "#0");
                                Thread.sleep(1000);
                                Server.ar.get(Server.index).dos.writeUTF("\n   You Won " + Server.amount[Server.ans_index_0][Server.ans_index_1] + "!#0");
                                Thread.sleep(1000);
                                
                                Server.ar.get(p).credit += Server.amount[Server.ans_index_0][Server.ans_index_1];
                                Server.ar.get(p).dos.writeUTF(Server.ar.get(p).credit + "#15");
                                
                                System.out.println("Ans Correct sended line 397");
                                
                                nName = Server.ar.get(p).name;
                                Server.ar.get(p).state = 10;

                            } else {
                                System.out.println("In else Line 409");
                                System.out.println("____ make the correct ans sending");
                                Server.ar.get(p).state = 10;
                                Server.ar.get(p).dos.writeUTF("\n  " + nName + " gave the correct answer first" + "#0");
                                System.out.println("____ make the correct ans sended line 406");
                            }

                        }

                    } else {
//        ________________Time out no ans is submitted even after buzzer clicked_______________________________
                         System.out.println("In else Line 420");
                        if (checkMsgType == 104) {
                            System.out.println("In if Line 422");
                            Server.ar.get(Server.index).credit -= Server.amount[Server.ans_index_0][Server.ans_index_1];
                            
                            System.out.println("Time up sending line 418");
                            Server.ar.get(Server.index).dos.writeUTF(Server.ar.get(Server.index).credit + "#15");
                            Server.ar.get(Server.index).dos.writeUTF("\n    Time's Up! You Lost " + Server.amount[Server.ans_index_0][Server.ans_index_1] + ".#0");
                            System.out.println("Time up sended");
                            for (int p = 0; p < Server.ar.size(); p++) {
                                if (p != Server.index) {
                                    System.out.println("Wait for next q sending");
                                    Server.ar.get(p).dos.writeUTF("\n    Wait for the next player's turn #0");
                                    System.out.println("Wait for next q sendEd line 426");
                                }
                            }
                            Thread.sleep(1000);

                        } else {
                            System.out.println("In Else Line 439");

                            Server.ar.get(Server.index).credit -= Server.amount[Server.ans_index_0][Server.ans_index_1];
                            System.out.println("Wrong and sending line 437");
                            Server.ar.get(Server.index).dos.writeUTF(Server.ar.get(Server.index).credit + "#15");
                            Server.ar.get(Server.index).dos.writeUTF("\n    Wrong Answer!" + "#0");
                            System.out.println("Wrong and sendEd line 439");
                            Thread.sleep(1000);
                            System.out.println("you lost message sending line 443");
                            Server.ar.get(Server.index).dos.writeUTF("\n   You Lost " + Server.amount[Server.ans_index_0][Server.ans_index_1] + "!#0");
                            System.out.println("you lost message sended line 441");
                            Thread.sleep(1000);
                            
                        }

                        state = 10;
//        ________________Check buzzer Array contain clientsz_______________________________
                        System.out.println("Before If Line 456");
                        if (!Server.buzzerSeq.isEmpty()) {
                            System.out.println("In if Line 458");
                            int indx = -1;
                            for (int p = 0; p < Server.ar.size(); p++) {
                                if ((Server.buzzerSeq.get(0)).equals(Server.ar.get(p).name)) {
                                    indx = p;
                                    Server.index = p;
                                }
                            }

                            for (int p = 0; p < Server.ar.size(); p++) {
                                if ((Server.buzzerSeq.get(0)).equals(Server.ar.get(p).name)) {
                                } else {
                                    System.out.println("_______ is ansing the question");
                                    Server.ar.get(p).dos.writeUTF("\n    " + Server.ar.get(Server.index).name + " is answering the question. #0");
                                    System.out.println("_____ ans the question line 464");
                                }
                            }
                            System.out.println("Sending the quesiton line 467");
                            Server.ar.get(indx).dos.writeUTF(Server.questions[Server.ans_index_0][Server.ans_index_1] + "\n(" + Server.answers[Server.ans_index_0][Server.ans_index_1] + "#0");
                            Server.ar.get(indx).dos.writeUTF("wrong" + "#3");
                            System.out.println("Sended the quesiton line 467");

                        } //        ________________state == 10 _______________________________   
                        else {
                            System.out.println("In Else Line 482");
                            for (int p = 0; p < Server.ar.size(); p++) {

                                Server.ar.get(p).state = 10;
                            }

                            Server.helpingVar = true;
                            System.out.println("Leaving Else Line 489");
                        }

                    }
                }

//        ________________if all clients have state == 10 _______________________________________________________
//        ________________no client in the buzzer array___________________________________________________________
//        ________________OR all client in the buzzer Array submitted their responce_______________________________
                if (Server.ar.get(0).state == 10 && Server.ar.get(1).state == 10 && Server.ar.get(2).state == 10 && Server.isExecuted==false) {
                    
                    Server.buzzerSeq.clear();
                    System.out.println("In If Line 499");
                    System.out.println("Status :: "+Server.isExecuted);
                    Server.isExecuted = true;

//        ________________All four questions of the selected category have been asked. Time to reSelect the Category_______________________________
                    if (Server.trackQuestion == 4 ) {
                        
                        Server.helpingVar = true;

                        System.out.println("Categoy Selectoion");
                        System.out.println("Question Track " + Server.trackQuestion);
                        System.out.println("Category Track " + Server.trackRound);
                        Server.resetCategory = true;

                        for (int p = 0; p < Server.ar.size(); p++) {
                            Server.ar.get(p).state = 3;
                        }
                        Server.trackRound++;
                        
                        System.out.println("_____________________________________________________________");
                        if (Server.trackRound != 4) {
                            this.dos.writeUTF("Send me message#114");
                        } else {
                            //calculate winner
                            System.out.println("State __________________________  06");
                            int winnerCredit = -99999;
                            int winnerIndex = -1;
                            for (int p = 0; p < Server.ar.size(); p++) {
                                if (Server.ar.get(p).credit > winnerCredit) {
                                    winnerCredit = Server.ar.get(p).credit;
                                    winnerIndex = p;
                                }

                            }

                            for (int p = 0; p < Server.ar.size(); p++) {
                                if (p == winnerIndex) {
                                    Server.ar.get(p).dos.writeUTF("    CONGRATULATIONS, You are the Winner.\n    You get $" + Server.ar.get(p).credit + ".#121");
                                } else {
                                    if (Server.ar.get(p).credit > 0) {
                                        Server.ar.get(p).dos.writeUTF("   GAME OVER!\n   " + Server.ar.get(winnerIndex).name + " won the game.   \n   You win $" + Server.ar.get(p).credit + ".#121");
                                    } else if (Server.ar.get(p).credit == 0) {
                                        Server.ar.get(p).dos.writeUTF("   GAME OVER!\n   " + Server.ar.get(winnerIndex).name + " won the game.   \n   You win nothing" + ".#121");
                                    } else {
                                        Server.ar.get(p).dos.writeUTF("   GAME OVER!\n   " + Server.ar.get(winnerIndex).name + " won the game.   \n   You lost $" + Server.ar.get(p).credit + ".#121");
                                    }

                                }
                                System.out.println("");
                            }

                        }

                        Server.trackQuestion = 0;

//                    Server will send the message to the current Index Client for to ready for the next round
                    } else {

                        System.out.println("Question Selectoion");
                        System.out.println("Question Track " + Server.trackQuestion);
                        System.out.println("Category Track " + Server.trackRound);
                        for (int p = 0; p < Server.ar.size(); p++) {
                            if (p == Server.a) {
                                Thread.sleep(2000);
                                Server.ar.get(p).dos.writeUTF("\n   Select the next Question." + "#500");
                            } else {
                                Server.ar.get(p).dos.writeUTF("\n   Please Wait for the next Question.#0");
                            }
                            Server.ar.get(p).state = 6;
                        }
                        Server.helpingVar = true;
                           System.out.println("_____________________________________________________________");
                    }

                }

//                System.out.println("______________________________________________");
//                for (int p = 0; p < Server.ar.size(); p++) {
//                    System.out.println("Client " + p + ":" + Server.ar.get(p).state);
//                    System.out.println();
//
//                }
//                System.out.println("__________________________________________________");
            } catch (IOException e) {
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        try {
            // closing resources 
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
