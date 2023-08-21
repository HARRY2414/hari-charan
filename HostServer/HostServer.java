import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


class AgentWorker extends Thread { //Class for AgentWorker
	
  Socket sock; 
  agentHolder parentAgentHolder; 
  int localPort; 
  
  //Constructor 
  AgentWorker (Socket s, int prt, agentHolder ah) {
    sock = s;
    localPort = prt;
    parentAgentHolder = ah;
  }
  public void run() {
    
   
    //Initialize the objects
    PrintStream out = null;
    BufferedReader in = null;
   
    //This is the host address. It can be over the network also. For now its localhost 
    String NewHost = "localhost";

//New port for the AgentWorker    
    int NewHostMainPort = 4242;		
    String buf = "";
    int newPort;
    Socket clientSock;
    BufferedReader fromHostServer; //Declare Buffered reader
    PrintStream toHostServer; //Declare PrintStream
    
    try {
      out = new PrintStream(sock.getOutputStream()) ;//Get the output stream from the socket connection
      in = new BufferedReader(new InputStreamReader(sock.getInputStream())); //Parse the out data from PrintStream using BufferedReader object
      
    
      String inLine = in.readLine(); //Read the line coming from socket
      StringBuilder htmlString = new StringBuilder(); //Initialize the htmlString for dynamic web page
      
      System.out.println(); //
      System.out.println("Request line: " + inLine); //print the inLine variable
      
      if(inLine.indexOf("migrate") > -1) { //If the user enter an output other than migrate do below steps 
	clientSock = new Socket(NewHost, NewHostMainPort);//create a new ClientSocket
	fromHostServer = new BufferedReader(new InputStreamReader(clientSock.getInputStream())); //get the BufferedReader
	toHostServer = new PrintStream(clientSock.getOutputStream()); //Get the PrintStream
        //get the state from the parentAgentHolder object which is passed to this current object
	toHostServer.println("Please host me. Send my port! [State=" + parentAgentHolder.agentState + "]");
	toHostServer.flush();//flush the stream
	
	for(;;) { //start an infinite loop and keep on reading the inputs from users unles migration happens
	  buf = fromHostServer.readLine();
	  if(buf.indexOf("[Port=") > -1) {
	    break;
	  }
	}
	//get the new port by doing String operations
	String tempbuf = buf.substring( buf.indexOf("[Port=")+6, buf.indexOf("]", buf.indexOf("[Port=")) );
	newPort = Integer.parseInt(tempbuf);
	System.out.println("newPort is: " + newPort);
	//Now send the newPort, newHost(localhost in our case) and inLine data to the sendHTMLheader html generator method and get the string
	htmlString.append(AgentListener.sendHTMLheader(newPort, NewHost, inLine));  //Keep on forming the html string
	htmlString.append("<h3>We are migrating to host " + newPort + "</h3> \n"); //Append the port information into dynamic html string
	htmlString.append("<h3>View the source of this page to see how the client is informed of the new location.</h3> \n");//append this message into dynamic html string
	htmlString.append(AgentListener.sendHTMLsubmit());//append the submit html code to dynamic html code
	
	System.out.println("Killing parent listening loop.");//print the message
	ServerSocket ss = parentAgentHolder.sock;//create a socket object from parentAgentHolder into ss local variable
	ss.close();//close the socket
	
	
      } else if(inLine.indexOf("person") > -1) { //since the data will contain person , this line will result true if migration didnt happen
	
	parentAgentHolder.agentState++;
	htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
	htmlString.append("<h3>We are having a conversation with state   " + parentAgentHolder.agentState + "</h3>\n");
	htmlString.append(AgentListener.sendHTMLsubmit());
	
      } else {//if the inLine variable didnt get migrate or new state means something invalid has been coming to this agent worker
	htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
	htmlString.append("You have not entered a valid request!\n");//inform to user that something is wrong 
	htmlString.append(AgentListener.sendHTMLsubmit());		//append the message to dynamic html string
	
	
      }
      AgentListener.sendHTMLtoStream(htmlString.toString(), out);//send the dynamically formed HTML to brower and close
      
      sock.close();//close the socket
      
      
    } catch (IOException ioe) {
      System.out.println(ioe);
    }
  }
  
}

class agentHolder { //object for holding the socket obj and agentState number
  ServerSocket sock;
  int agentState;
  
  agentHolder(ServerSocket s) { sock = s;}//constructor
}
class AgentListener extends Thread {//agent listener class 
  Socket sock;
  int localPort;
  
  AgentListener(Socket As, int prt) {//AgentListener constructor .it gets Socket object and port number
    sock = As;
    localPort = prt;
  }
  int agentState = 0;
  
  public void run() {
      //Initialize variables for BufferedReader and PrintStream
    BufferedReader in = null; 
    PrintStream out = null;
    String NewHost = "localhost"; //Here host is localhost but it can be any computer ip address which can be accessed by this java program
    System.out.println("In AgentListener Thread");		
    try {
      String buf;//intialize the String buffer variable to hold the incoming messages
      out = new PrintStream(sock.getOutputStream()); // to get the outputstream and pass them on to network
      in =  new BufferedReader(new InputStreamReader(sock.getInputStream()));//get the inputstream messages
      
      buf = in.readLine();//read the incoming message to the AgentLister object
      
      if(buf != null && buf.indexOf("[State=") > -1) {//if there is state related information present in the incomning message to AgentListener do below
	String tempbuf = buf.substring(buf.indexOf("[State=")+7, buf.indexOf("]", buf.indexOf("[State=")));//get the agent state using string operations
	agentState = Integer.parseInt(tempbuf);//convert state from String data type to integer datatype
	System.out.println("agentState is: " + agentState);//Print the state on to the console
	
      }
      
      System.out.println(buf);
     
      StringBuilder htmlResponse = new StringBuilder(); //start buding dynamic respose to be sent to browser
    
      htmlResponse.append(sendHTMLheader(localPort, NewHost, buf)); //append the port host and state information
      htmlResponse.append("Now in Agent Looper starting Agent Listening Loop\n<br />\n");//Append this info the dynamic html
      htmlResponse.append("[Port="+localPort+"]<br/>\n");//append the port number related info to the dynamic html form string
      htmlResponse.append(sendHTMLsubmit());
     
      sendHTMLtoStream(htmlResponse.toString(), out);//send this to browser
      
      ServerSocket servsock = new ServerSocket(localPort,2);//create a ServerSocket object
      agentHolder agenthold = new agentHolder(servsock); //create an agentHolder object using the ServerSocket object created in above step
      agenthold.agentState = agentState; //assing the state into agentHold object
      
      while(true) {
	sock = servsock.accept(); // start acceping connections
	
	System.out.println("Got a connection to agent at port " + localPort);//once a connections is reaceived print it on to the console
	
	new AgentWorker(sock, localPort, agenthold).start();//spawn a newAgent Worker
      }
      
    } catch(IOException ioe) {
      
      System.out.println("Either connection failed, or just killed listener loop for agent at port " + localPort);
      System.out.println(ioe);
    }
  }
  
  
  
  //this is a helper method which receives what ever is send by the user and forms a stirng using the details sent by user
  //This will be displayed once the user clicks the submit button
  static String sendHTMLheader(int localPort, String NewHost, String inLine) {
    
    StringBuilder htmlString = new StringBuilder();//intialize the string
    
    htmlString.append("<html><head> </head><body>\n");//add body and head tags
    htmlString.append("<h2>This is for submission to PORT " + localPort + " on " + NewHost + "</h2>\n"); //append port information
    htmlString.append("<h3>You sent: "+ inLine + "</h3>");//append inLine information ie.e. whatever is send by user that will be displyed to user once he clicks the submit button
    htmlString.append("\n<form method=\"GET\" action=\"http://" + NewHost +":" + localPort + "\">\n");//form the action url 
    htmlString.append("Enter text or <i>migrate</i>:");//label for the input text box in which user will enter his input
    htmlString.append("\n<input type=\"text\" name=\"person\" size=\"20\" value=\"YourTextInput\" /> <p>\n");//text box in which user will enter his input
    
    return htmlString.toString();//return the string
  }
  
  static String sendHTMLsubmit() {
    return "<input type=\"submit\" value=\"Submit\"" + "</p>\n</form></body></html>\n";//html code for the submit button
  }
  
  
  static void sendHTMLtoStream(String html, PrintStream out) {//send the html to output stream i.e. browser
    
    out.println("HTTP/1.1 200 OK");
    out.println("Content-Length: " + html.length());
    out.println("Content-Type: text/html");
    out.println("");		
    out.println(html);
  }
  
}

public class HostServer {//main HostServer class

 
  public static int NextPort = 3000; 
  
  public static void main(String[] a) throws IOException {
    int q_len = 6;
    int port = 4242;
    Socket sock;
    //Intial socket port for listening to connection requests
    ServerSocket servsock = new ServerSocket(port, q_len); 
    System.out.println("Elliott/Reagan DIA Master receiver started at port 4242.");
    System.out.println("Connect from 1 to 3 browsers using \"http:\\\\localhost:4242\"\n");
    //Infinite loop for continueous listening 
    while(true) {
      //Increment the port number to start listening to next port on the system
      NextPort = NextPort + 1;
      //Accept the incoming message from socket
      sock = servsock.accept();
      
      System.out.println("Starting AgentListener at port " + NextPort);
      //Start a new AgentListener thread
      new AgentListener(sock, NextPort).start();
    }
    
  }
}