/* --------------------------------------------------------------------*
 * FTPSession.java                                                     *
 * --------------------------------------------------------------------*
 * Description - This class acts as the controller for managing all    *
 * interactions made between the server and the client for the ftp     *
 * communications.                                                     *
 * --------------------------------------------------------------------*
 * Project: Project 1                                                  *
 * Created by : Kristian Trevino, Josh Krolik, &  McKim A. Jacob       *
 * Date Of Creation: 10 - 01 - 2016                                    *
 * ------------------------------------------------------------------- */
 
 
//===================================================================//
//  NOTES & BUGS AS OF 10-01-2016                                    //
//===================================================================//
/*
 * 
 */

//===================================================================//
//  Includes                                                         //
//===================================================================//
import java.io.* ;
import java.net.* ;
import java.util.* ;

final class FTPSession implements Runnable
{
    
    /* Used to keep track of what action is to take place.            */ 
    private enum FTPState {
        IDLE, 
        CONNECT,
        LIST,
        RETR,
        STORE,
        QUIT
    }

    /* Keep track of whether or not command connection should close. */
    private boolean isActive = true;
    
    /* The state the ftp command service provider is in.             */
    private FTPState state = FTPState.IDLE;
    
    /* The socket that we want to open.                              */ 
    Socket socket = null; 
    
    /* The ouput stream were going to keep open.                     */
    DataOutputStream dataOut = null;
    
    /* The input stream were going to keep open.                     */
    BufferedReader dataIn = null; 
    
    /* The parameter that is passed with the variable.               */
    private ArrayList<String> params = new ArrayList<String> (); 
    
    //---------------------------------------------------------------//
    // Constructor/Destructors                                       //
    //---------------------------------------------------------------//
    
    /******************************************************************
     * @Description - Called to build the session for the client 
     * connection.
     *
     * @param incommingSocket - The socket data will be comming in on 
     * from the client. 
     *
     * @return None
     *
     *****************************************************************/
    public FTPSession(Socket socket)
    {
        this.socket = socket;
        
    }
    
    //---------------------------------------------------------------//
    // Class Methods                                                 //
    //---------------------------------------------------------------//
    
    public void run () {
      
        try {
            
            // 1. open data streams.
            dataOut = new DataOutputStream(socket.getOutputStream());
            dataIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Loop While the connection is open.
            while (isActive) {
                
                // 1. If there is a request avalaible parse it for a state. 
                if (socket.getInputStream().available() > 0) {
                    state = parseIncomming ();
                }
                
                // 2. Take a course of action based on input state.
                switch (state) {
                    
                    case IDLE: 
                    
                        break;
                    
                    case CONNECT: 
                        dataOut.writeBytes("SUCCESS\n");
                        System.out.println("Connection Opened From: " + socket.getRemoteSocketAddress().toString());
                        state = FTPState.IDLE;
                        break;
                    
                    case LIST: 
                        listCommand();
                        state = FTPState.IDLE;
                        break;
                    
                    case RETR: 
                        retrCommand();
                        state = FTPState.IDLE;
                        break;
                    
                    case STORE: 
                        storCommand();
                        state = FTPState.IDLE;
                        break;
                    
                    case QUIT: 
                        isActive = false;
                        System.out.println("Connection Closed From: " + socket.getRemoteSocketAddress().toString());
                        break;
                }
            }
            
            // X. Finally, close the socket and all open streams once the user quits. 
            socket.close();
    }
    
    catch (Exception e) {
        System.out.println(e.getMessage());
    }
    
    System.out.println("Terminating Session Object.");
        
    }

    //---------------------------------------------------------------//
    // Class Methods                                                 //
    //---------------------------------------------------------------//
    
    /******************************************************************
     * @Description - Called to pull in a request command from the tcp
     * connection. 
     *
     * @return FTPState - the next state our ftp server should be in. 
     *
     *****************************************************************/
    private FTPState parseIncomming () throws Exception {
        
      
        // --- Variable Declarations  -------------------------------//
        
        /* The tokenizer used to break apart the request.           */
        StringTokenizer tokenizer = null;
        
        /* The string variable to store input data into.            */
        String inCommand = "";
        
        // --- Main Routine ----------------------------------------//
        
        // 1. Read in the data.
        inCommand = dataIn.readLine (); 
        
        // 2. Slice and Dice the payload.
        tokenizer = new StringTokenizer(inCommand);
        
        // 3. Bulid Params array.
        params.clear();
        while (tokenizer.hasMoreTokens()) {
            params.add(tokenizer.nextToken());    
        }
       
        // 4. Set the state.  
        switch (params.get(0)) {
            case "CONNECT":
                return FTPState.CONNECT;
            case "LIST":
                return FTPState.LIST;
            case "RETR":
                return FTPState.RETR;
            case "QUIT":
                return FTPState.QUIT;
            default:
                return FTPState.IDLE;
        }
       
    }
    
    /******************************************************************
     * @Description - Called to send data back to the client. 
     *
     * @return payload - A string of the data to be sent back. 
     *
     *****************************************************************/
    private void sendControlResponse (byte [] payload) throws Exception {
        
        // --- Variable Declarations  -------------------------------//
        
        /* N/A                                                      */
        
        // --- Main Routine ----------------------------------------//
       
        // Write data to the stream.
        //dataOut.writeBytes (payload + "\n");
        dataOut.flush();
        
    }
    
    
   /******************************************************************
     * @Description - gets the names of the files located in the 
     * directory and puts it into a string. It then passes the string
     * to the server. 
     * 
     * ****************************************************************/
    private void listCommand()throws Exception {

        File folder = new File(System.getProperty("user.dir"));
        File[] listOfFiles = folder.listFiles();
        
        String fileString = "";

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileString = fileString + "File: " + listOfFiles[i].getName() +"\n";
            } else if (listOfFiles[i].isDirectory()) {
                fileString = fileString + "Directory: " + listOfFiles[i].getName() + "\n";
            }
        }
        
        /*String[] splitStr = fileString.split("\\s+");
        
        for(int i=0; i < splitStr.length; i++){
            newString = splitStr[i];
            System.out.println(newString + "\n");
        }*/
        
        dataOut.writeBytes(fileString);
        //sendControlResponse(fileString.getBytes(StandardCharsets.UTF_8));
    }
    
    /******************************************************************
     * @Description - sends file specified by the client.
     * 
     * ****************************************************************/

    private void retrCommand()throws Exception {

        //need to figure out how to send a file from located in directory with maybe .equals

        //filesToSend = params.get(1) but it needs to check that this is not NULL and it also needs to check whether you actually have this file,
        //return a message back to the client if its one of these two things
        
        //send a message to client that tell what port number to connect to 
        //once connection is established, send the file,
        ServerSocket listenSocket = null;
        Socket connectionSocket = listenSocket.accept();
        
        //BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        
        //String clientMessage = inFromClient.readLine();
        try {
            File clientFile = new File( params.get(1));
            byte[] fileBytes = new byte[(int) clientFile.length()];
            BufferedInputStream readFile = new BufferedInputStream(new FileInputStream(clientFile));
            readFile.read(fileBytes, 0, fileBytes.length);

            sendControlResponse(fileBytes);
        }catch (Exception e){
            String errorMessage = "File Not Found";
            //sendControlResponse(errorMessage.getBytes(StandardCharsets.UTF_8));

        }
    }
    
     /******************************************************************
     * @Description - Recieves a file sent by the client. 
     * 
     * ****************************************************************/
    
    private void storCommand()throws Exception {

        //DataPipeline dp = new DataPipeline(6789, "127.0.0.1");
        //dp.receiveData();
            
    }
    
}
