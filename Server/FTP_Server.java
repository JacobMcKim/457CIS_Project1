/* --------------------------------------------------------------------*
 * FTPServer.java                                                      *
 * --------------------------------------------------------------------*
 * Description - This class acts as the server to allow connections    *
 * to be made to the ftp server. It creates session objects to manage  *
 * client sessions.                                                    *
 * --------------------------------------------------------------------*
 * Project: Project 1                                                  *
 * Modified by : Kristian Trevino, Josh Krolik, &  McKim A. Jacob      *
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
import java.nio.charset.StandardCharsets;

//===================================================================//
// Class Definition                                                  //
//===================================================================//
public final class FTPServer {
    
    //---------------------------------------------------------------//
    // Class Atributes                                               //
    //---------------------------------------------------------------//
    
    /* The port that the server will be listening for new requests on.*/
    private static final int commandPort = 21;
    
    /* The Server socket that the commands will be communicated over. */
    private static ServerSocket commandSocket = null;
    
    //---------------------------------------------------------------//
    // Core Runtime Method                                           //
    //---------------------------------------------------------------//
    
    public static void main(String argv[]) throws Exception {
     
    // --- Variable Declarations  -------------------------------//
    
    /* N/A */
    
    // --- Main Routine ------------------------------------------//
    
    // 1. Establish the command listen socket.
    commandSocket = new ServerSocket(commandPort);
    
    // 2. Process HTTP service requests in an infinite loop.
    while (true) {
        // A. Listen for a TCP connection request.
        Socket connection = commandSocket.accept();
        
        // B. Generate a session object to handle
        FTPSession clientSession = new FTPSession (connection);
        
        // C. Create a new thread to process the request.
        Thread thread = new Thread(clientSession);
        
        // D. Start the thread.
        thread.start();
    }
    
    }
    
    //---------------------------------------------------------------//
    // Class Methods                                                 //
    //---------------------------------------------------------------//
    
    /* N/A */ 
    
}


 
