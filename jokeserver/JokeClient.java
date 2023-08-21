import java.io.*; // For Input & Output related Classes and their exception handling classes
import java.net.*; //Socket and its related exception handling classes
import java.util.ArrayList;
import java.util.Scanner; //For reading input from user
import java.util.UUID;

class JokeClient {

    private static int clientColorCount = 0;
    ArrayList<JokeCycle> jokeCyclesList = new ArrayList<>();
    ArrayList<ProverbCycle> proverbCyclesList = new ArrayList<>();

    JokeCycle jc;
    ProverbCycle pc;
    String clientID;

    public static void main(String argv[]) throws InterruptedException {

        JokeClient cc = new JokeClient(argv);
        cc.run(argv);
    }

    public JokeClient(String argv[]) { // constructor is called when new cliend is added. 
        System.out.println("\n Is the constructor if you want to use \n");
    }

    public void run(String argv[]) throws InterruptedException {  
        clientID = UUID.randomUUID().toString();
        String serverName;
        if (argv.length < 1) {
            serverName = "localhost"; // Check and take the server name, If none then assign local host as server name 
        } else {
            serverName = argv[0];
        }

        String jokeFrmClnt = "";

        System.out.println("Enter your name: ");
        Scanner consoleIn = new Scanner(System.in); // Intialize the Scanner class for reading input from user

        String userName = consoleIn.next(); //Read user name from console

        jc = new JokeCycle();
        pc = new ProverbCycle();

        do { // Keep on reading user input and sent to server until user types in 'quit'

            System.out.println("Press enter or quit to the end: ");
            jokeFrmClnt = consoleIn.nextLine(); //Read color entered by user or quit 
            if (jokeFrmClnt.indexOf("quit") < 0) { // Check if user entered quit
                getJoke(userName, serverName, clientID); //calling getJoke method
            }

        } while (jokeFrmClnt.indexOf("quit") < 0); //Condition to loop, until user enters quit

        if (jc != null && jc._JA!=null &&  jc._JD == null) {
            jokeCyclesList.add(jc);

        }
        if (pc != null && pc._PA !=null && pc._PD == null) {
            proverbCyclesList.add(pc);
        }

        System.out.println("Cancelled by user request.");

        System.out.println("\n\n---------------------------------Joke Cycles------------------------------------\n\n");

        for (JokeCycle a : jokeCyclesList) {
            System.out.println(a.toString());
        }

        System.out.println("\n\n---------------------------------Proverb Cycles------------------------------------\n\n");
        for (ProverbCycle a : proverbCyclesList) {
            System.out.println(a.toString());
        }
    }

    void getJoke(String userName, String serverName, String clientID) throws InterruptedException {

        try {
            JokeData jokeObj = new JokeData(); // Intialize the Object
            //Setting user inputs to ColorData obj
            jokeObj.userName = userName;
            jokeObj.clientID = clientID;
            Socket socket =null;

            int i = 0;
            while(i<60 && socket == null){ 
                try{
                    socket= new Socket(serverName, 50001); //Create the connection using Socket object.     
                }catch(ConnectException CE){
                    System.out.println("No Server found. Start the server.Trying after 5 seconds");
                    Thread.sleep(5000);
                }
                
                i+=5;
            }
            if (socket == null) {
                System.out.println("Connection Timeout.. Check whether the server is up and running");
                System.exit(-1);
            }
           

            OutputStream OutputStream = socket.getOutputStream(); //Get o/p stream from the Socket obj. This is for sending object to Client
            ObjectOutputStream oos = new ObjectOutputStream(OutputStream); //This is to serialize the object when sending over network

            oos.writeObject(jokeObj); // Write the object onto the network.
           

            InputStream InStream = socket.getInputStream(); //Initialize an InputStream to read the data from Server
            ObjectInputStream ois = new ObjectInputStream(InStream); //For reading serialized objects
            JokeData InObject = (JokeData) ois.readObject(); // Read the data from the Socket and Cast it as the ColorData obj. 

            String messageFromServer = InObject.jokeOrProverbSentBack;

            //Display data on console
            System.out.println(messageFromServer);

            if (messageFromServer.startsWith("JA")) {
                jc._JA = messageFromServer;
            } else if (messageFromServer.startsWith("JB")) {
                jc._JB = messageFromServer;
            } else if (messageFromServer.startsWith("JC")) {
                jc._JC = messageFromServer;
            } else if (messageFromServer.startsWith("JD")) {
                jc._JD = messageFromServer;
                jokeCyclesList.add(jc);
                System.out.println("---------------JOKE CYCLE COMPLETED------------");
                jc = new JokeCycle();

            } else if (messageFromServer.startsWith("PA")) {
                pc._PA = messageFromServer;
            } else if (messageFromServer.startsWith("PB")) {
                pc._PB = messageFromServer;
            } else if (messageFromServer.startsWith("PC")) {
                pc._PC = messageFromServer;
            } else if (messageFromServer.startsWith("PD")) {
                pc._PD = messageFromServer;
                proverbCyclesList.add(pc);
                System.out.println("---------------PROVERB CYCLE COMPLETED------------");
                pc = new ProverbCycle();
            } else {
                System.out.println("Invalid response from Server");
            }
            
            socket.close();

        } catch (ConnectException CE) { //When connection is not established 
            System.out.println("\nOh no. The ColorServer refused our connection! Is it running?\n");
            CE.printStackTrace();
        } catch (UnknownHostException UH) { //When host is wrong or our client is not able to find the Host given in the program
            System.out.println("\nUnknown Host problem.\n"); //This exception caught when we commented and passed an invalid host name
            UH.printStackTrace();
        } catch (ClassNotFoundException CNF) {// ColorData obj not found exception
            CNF.printStackTrace();
        } catch (IOException IOE) {
            IOE.printStackTrace(); // For printing Exceptional and its related information. 
        }
    }
}//To store serialized information and send it back and forth on network betweeen client and server
class JokeData implements Serializable{
    String userName;
    String jokeOrProverbSentBack;
    String clientID;
}