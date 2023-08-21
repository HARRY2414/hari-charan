import java.io.*;
import java.net.*;
import java.util.*;

public class JokeServer {

    static HashMap<String, Integer> stateMapJokes;
    static HashMap<String, Integer> stateMapProverbs;

    private int state = 0;

    public synchronized void toggleState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public static void main(String[] args) throws Exception {

        stateMapJokes = new HashMap<>();
        stateMapProverbs = new HashMap<>();
        int mode = 0;
        int queueLength = 1;

        
        int prtNum = 50001; //Port number 
        Socket sock; //for socket connections
        SwitchState tm = new SwitchState();

        System.out.println("Waiting for new connections at port " + prtNum + ".\n");

        AdminLooper alObj = new AdminLooper(tm, stateMapJokes, stateMapProverbs); // create a DIFFERENT thread for listening for AdminCLient requests
        Thread admLooperTrdObj = new Thread(alObj);
        admLooperTrdObj.start();  //spin up the thread to listen for admin client requests to change state 
        System.out.println("Started looking for clients");
        ServerSocket sockObj = new ServerSocket(prtNum, queueLength); //Create a Socket object 
        

        
        while (true) { // Infinite loop to keep on doing below steps
            sock = sockObj.accept();   // Accept an incoming socket connection from the Client
            //Print the socket information onto console
            System.out.println("Connection from " + sock);
            new JokeWorker(sock, tm, stateMapJokes, stateMapProverbs).start();//Create a JokeWorker thread to handling the request from the Client
        }
    }
}

/**
 * This Class is responsible to handle each request coming to Server For each
 * connection coming from the Client, one JokeWorker thread will be spawn
 *
 */
class JokeWorker extends Thread {    //This class extends Thread class to run multiple threads at a time. This will ensure the server is able to serve multiple clients at the same time

    Socket sockObj;                        // Class variable, holds Socket (connection ) obj
    SwitchState tm;
    Map<String, Integer> stateMapJokes;
    Map<String, Integer> stateMapProverbs;

    String jokeOrder[] = {"JA", "JB", "JC", "JD"};
    String proverbOrder[] = {"PA", "PB", "PC", "PD"};

    JokeWorker(Socket s, SwitchState tm, Map<String, Integer> stateMapJokes, Map<String, Integer> stateMapJokesProverbs) { //Constructor . Holds the Socket obj coming from Server obj
        sockObj = s;
        this.tm = tm;
        this.stateMapJokes = stateMapJokes;
        this.stateMapProverbs = stateMapJokesProverbs;
    }

    public void run() {
        try {

            
            System.out.println("State of JokeServer from JokeServer worker method: " + tm.currentState());
           

            InputStream clientInputStream = sockObj.getInputStream(); //It accepts input sent by the client 
            ObjectInputStream objInpStream = new ObjectInputStream(clientInputStream); //Deserialize it

            JokeData jkData = (JokeData) objInpStream.readObject(); //Get the color object and cast it to use it

            OutputStream os = sockObj.getOutputStream(); //Get the output stream
            ObjectOutputStream clientToDataStream = new ObjectOutputStream(os); //serialize the object
            System.out.println("New connection from:" + jkData.userName);

            String clientId = jkData.clientID;
            String userName = jkData.userName;

            int stateIndexJokes = stateMapJokes.getOrDefault(clientId, 0);
            int stateIndexProverbs = stateMapProverbs.getOrDefault(clientId, 0);

            String msg;
            if (this.tm.currentState() == 0) {

                msg = getRandomJoke(stateIndexJokes);
                jkData.jokeOrProverbSentBack = String.format("%s %s:%s", jokeOrder[stateIndexJokes % 4], userName, msg);
                stateMapJokes.put(clientId, (stateIndexJokes + 1) % 20);

            } else {

                msg = getRandomProverb(stateIndexProverbs);
                jkData.jokeOrProverbSentBack = String.format("%s %s:%s", proverbOrder[stateIndexProverbs % 4], userName, msg);
                stateMapProverbs.put(clientId, (stateIndexProverbs + 1) % 20);
            }

            clientToDataStream.writeObject(jkData); // Send the ColorData object back to client

            System.out.println("Stopping the connection");
            sockObj.close(); 

        } catch (ClassNotFoundException exp) { //Handling any ClassNotFound exceptions
            exp.printStackTrace();
        } catch (IOException y) { //Handling IO related Exceptions
            System.err.println("Something wrong happened");
            y.printStackTrace();
        }
    }

    /**
     * @return : random joke from set of predefined jokes array
     *
     * Ref : http://iteslj.org/c/jokes-short.html
     */
    private static String getRandomJoke(int index) {
        String[] jokesArray = new String[]{
            "What is the name of a fish without eyes? Fsh!",
"What has four wheels and can fly? A garbage truck!",
"I am currently reading a book on anti-gravity, and I find it impossible to put down.",
"What do you call a counterfeit noodle? An impasta.",
"I used to play piano by ear, but now I use my hands.",
"I am on a whiskey diet, and I have already lost three days.",
"I previously worked in a calendar factory, but I was dismissed for taking a day off.",
"Why did the banana seek a surgeon's assistance? It was feeling peeled.",
"How can you make tissue dance? Add a little boogey to it!",
"I used to be indecisive, but now I am not sure.",
"Why did the cookie visit the doctor? It was feeling crummy.",
"What do you call a cow with no legs? Ground beef.",
"Why don't scientists trust atoms? Because they make up everything!",
"I am so talented at sleeping that I can do it with my eyes closed.",
"Did you hear about the kidnapping at the park? They woke up.",
"Why did the chicken cross the playground? To reach the other slide.",
"Why did the banana see a doctor? Because it wasn't peeling well.",
"Why don't some couples go to the gym? Because some relationships don't work out!",
"I am reading a book about anti-gravity, but it's too complicated to understand.",
"What do you call someone who tells dad jokes but isn't a dad? A faux-pa."

        };

        //int randomArrayIndex = (int) (Math.random() * jokesArray.length); //Generate a number between 0 and length of array( in this case 0 to 9 numbers will be produced) 
        //return (jokesArray[randomArrayIndex]);
        return (jokesArray[index]);
    }

    private static String getRandomProverb(int index) {
        String[] proverbsArray = new String[]{
           "It's not wise to count your chickens before they hatch.",
"A picture can convey a thousand words.",
"If you address a problem immediately, you can save yourself a lot of trouble later on - \"a stitch in time saves nine.\"",
"When someone is in a dire situation, they should be grateful for whatever they receive - \"beggars can't be choosers.\"",
"If you watch something intently, it seems to take forever - \"a watched pot never boils.\"",
"Don't harm someone who has helped you - \"don't bite the hand that feeds you.\"",
"What someone does is more important than what they say - \"actions speak louder than words.\"",
"Eating an apple every day can help keep the doctor away - \"an apple a day keeps the doctor away.\"",
"Your actions have consequences - \"you reap what you sow.\"",
"It's better to be safe than to regret your actions later - \"better safe than sorry.\"",
"Appearances can be deceiving - \"don't judge a book by its cover.\"",
"If you take care of something early, you can avoid bigger problems later - \"a stitch in time saves nine.\"",
"What you sow, you shall reap - \"as you sow, so you shall reap.\"",
"People often think other people's situations are better than their own - \"the grass is always greener on the other side.\"",
"Beauty is subjective - \"beauty is in the eye of the beholder.\"",
"It's better to do something late than not at all - \"better late than never.\"",
"Family is more important than anything else - \"blood is thicker than water.\"",
"When you are in a different culture, it is important to adapt to their ways - \"when in Rome, do as the Romans do.\"",
"It's not wise to count on something before it happens - \"don't count your chickens before they hatch.\"",
"You cannot judge someone or something by its outward appearance alone - \"don't judge a book by its cover.\""
        };

        //int randomArrayIndex = (int) (Math.random() * proverbsArray.length); //Generate a number between 0 and length of array( in this case 0 to 9 numbers will be produced) 
        //return (proverbsArray[randomArrayIndex]);
        return (proverbsArray[index]);
    }

}

class AdminLooper implements Runnable {

    SwitchState tm;
    public static boolean adminControlSwitch = true;
    Map<String, Integer> map;

    public AdminLooper(SwitchState tm, Map<String, Integer> mapJokes, Map<String, Integer> mapProverbs) {
        this.tm = tm;
        this.map = map;
    }

    public void run() { 
        
        int queueLength = 6;
        
        int port = 5050;  
        Socket adminConnection;

        try {
            ServerSocket connection = new ServerSocket(port, queueLength);
            while (adminControlSwitch) {
               
                adminConnection = connection.accept();
                System.out.println("Got New connection from AdminClient:Current State is" + tm.currentState());
                tm.putState();
               
                System.out.println("Swtiched server to state:" + tm.currentState());
            }
        } catch (IOException exo) {
            System.err.println(exo);
        }
    }

}