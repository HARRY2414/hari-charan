import java.io.*; // For input and output related Classes and their exception handling classes
import java.net.*; //Socket and its related exception handling classes
import java.util.Scanner; // To read input from the user

class JokeClientAdmin {

    public static void main(String argv[]) throws InterruptedException {
        JokeClientAdmin cc = new JokeClientAdmin(argv);
        cc.run(argv);
    }

    public JokeClientAdmin(String argv[]) { // This Constructor is called when new cliend is added.

    }

    public void run(String argv[]) throws InterruptedException { 

        String serverName;
        if (argv.length < 1) {
            serverName = "localhost"; // Verify and take server name. If none, assign local host as server name 
        } else {
            serverName = argv[0];
        }

        String inpFromCLient = ""; //To maintain user input whther to quit or enter 

        System.out.println("Enter Name: ");
        Scanner consoleIn = new Scanner(System.in); // Intialize the Scanner class for reading input from user

        String userName = consoleIn.next(); //Read user name from console

        do { // Keep on reading user input and sent to server until user types in 'quit'

            inpFromCLient = consoleIn.nextLine(); //enter button pressed by 50 question
            if (inpFromCLient.indexOf("quit") < 0) { // Check if user entered quit
                getJoke(userName, serverName); //calling getJoke method
            }
        } while (inpFromCLient.indexOf("quit") < 0); //Condition to loop, till user executes quit
        System.out.println("Cancelled by user request.");

    }

    void getJoke(String userName, String serverName) throws InterruptedException {

        try {

            JokeData jokeObj = new JokeData(); // Intialize the Obj
            jokeObj.userName = userName;

            Socket socket = null;
            int i = 0;
            while (i < 60 && socket == null) { // This loop handles if the server is not started and client is started
                try {
                    socket = new Socket(serverName, 5050); //Create the connection using Socket object.
                } catch (ConnectException CE) {
                    System.out.println("No Server found. Start the server.Trying after 5 seconds");
                    Thread.sleep(5000); //Sleep for 5 seconds and retry
                }
                i += 5;
            }
            if (socket == null) {//If no connection after after 5 min exit with System out error 
                System.out.println("Connection Timeout.. Check whether the server is up and running");
                System.exit(-1);
            }
            System.out.println("closes Admin Connection to Server.\n");
            socket.close();
            System.out.println("Toggled state of server.\n");

        } catch (ConnectException CE) { //Whenever connection is not established 
            System.out.println("The Server refused the connection.");
            CE.printStackTrace();
        } catch (UnknownHostException UH) { //Whenever host is wrong or our client is not able to find the Host given in the program
            System.out.println("\nUnknown Host problem.\n"); //This exception caught when we commented and passed an invalid host name
            UH.printStackTrace();
        } catch (IOException IOE) {
            IOE.printStackTrace(); // For printing the Exceptiona and its related information. 
        }
    }
}