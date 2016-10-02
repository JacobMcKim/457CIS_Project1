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
    
    /* The parameter that is passed with the variable.               */
    private ArrayList<String> params; 
    
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
                        // TODO: Josh
                        break;
                    
                    case LIST: 
                        // TODO: Kristian
                        break;
                    
                    case RETR: 
                        // TODO: Kristian
                        break;
                    
                    case STORE: 
                        // TODO: Kristian
                        break;
                    
                    case QUIT: 
                        // TODO: Josh
                        isActive = false;
                        break;
                }
            }
            
            // X. Finally, close the socket and all open streams once the user quits. 
            socket.close();
    }
    
    catch (Exception e) {

    }
        
        
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
    
        /* The input buffer to pull all the data from.              */
        InputStream stream = socket.getInputStream ();
        
        /* The buffer reader used to pull in the data.              */
        BufferedReader inRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        // --- Main Routine ----------------------------------------//
        
        // 1. Read in the data.
        inCommand = inRead.readLine (); 
        
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
     * @return payload - A byte array of the data to be sent back. 
     *
     *****************************************************************/
    private void sendResponse (byte [] payload) {
        // TODO: Jake
    }
    
}
