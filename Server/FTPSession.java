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
                    
                        break;
                    
                    case LIST: 
                    
                        break;
                    
                    case RETR: 
                    
                        break;
                    
                    case STORE: 
                    
                        break;
                    
                    case QUIT: 
                    
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
    private FTPState parseIncomming () {
        
        return null;
    }
    
}
