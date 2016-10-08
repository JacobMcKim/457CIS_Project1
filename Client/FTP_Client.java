/* --------------------------------------------------------------------*
 * FTP_Client.java                                                     *
 * --------------------------------------------------------------------*
 * Description - This class acts as the client device for the ftp      *
 * services.                                                           *
 * --------------------------------------------------------------------*
 * Project: Project 1                                                  *
 * Modified by : Kristian Trevino, Josh Krolik, &  McKim A. Jacob      *
 * Date Of Creation: 10 - 05 - 2016                                    *
 * ------------------------------------------------------------------- */
 
 
//===================================================================//
//  NOTES & BUGS AS OF 10-05-2016                                    //
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

public final class FTP_Client
{
    
    //---------------------------------------------------------------//
    // Class Atributes                                               //
    //---------------------------------------------------------------//
    
  
    //---------------------------------------------------------------//
    // Core Runtime Method                                           //
    //---------------------------------------------------------------//

    
    public static void main (String [] args) {
        
        // --- Variable Declarations  -------------------------------//
        
        /* Whether or not we should close the application.          */
        boolean appAlive = true;
        
        /* The reference to the console.                            */
        BufferedReader br = null;
        
        /* Parse in the user input from the command line.           */
        String userInput = "";
        
        /* The tokenizer that splits apart the string.              */
        StringTokenizer token = null; 
        
        /* The socket to use to make communication connections.     */
        Socket ftpCommandSocket = null; 
        
        /* The response data back from the server.                  */
        ArrayList <String> response = new ArrayList<String>();
        
        // --- Main Routine ----------------------------------------//
        
        try {
        
        // 1. Open the console stream.
        br = new BufferedReader(new InputStreamReader(System.in));
    
        // 2. Loop until exit requested.
            do {
                
                // A. Read the user input
                System.out.print("FTP Client:>");
                userInput = br.readLine();
                token = new StringTokenizer (userInput);
                //System.out.println (token.nextToken());
                
                
                // B. Select which action to take and take it.
                switch (token.nextToken().toUpperCase()) {
                    
                    case "CONNECT":
                        ftpCommandSocket = openFTPCmdConnection(token);
                        if (ftpCommandSocket != null) {
                            sendCommandRequest (ftpCommandSocket,"CONNECT".getBytes());  
                            response = getCommandResponse (ftpCommandSocket);
                            
                            if (response.get(0) == "SUCCESS") {
                                System.out.println("Connection Successful!");
                            }
                            else {
                                System.out.println("Unable to connect.");
                            }
                        }
                        
                    break;
                    
                    case "LIST":
                        
                        // A. Preform request.
                        sendCommandRequest (ftpCommandSocket,"LIST".getBytes()); 
                        response = getCommandResponse (ftpCommandSocket);
                        
                        // B. Echo back the result. 
                        for(String listItem : response) {
                            System.out.println(response);
                        }

                    break;
                    
                    case "RETR":
                        System.out.println("retrieving...");
                        // TODO send request, wait for port number.
                        
                    break;
                    
                    case "STORE":
                        System.out.println("storing...");
                        // TODO send request, wait for port number, send file.
                    break;
                    
                    case "QUIT":
                        if (ftpCommandSocket != null) {
                            sendCommandRequest (ftpCommandSocket,"QUIT".getBytes());  
                            ftpCommandSocket.close();
                            System.out.println("quiting connection...");
                        }
                        else {
                            System.out.println("No Connections are open.");
                        }
                        
                    break;
                    
                    case "EXIT":
                        if (ftpCommandSocket != null) {
                            sendCommandRequest (ftpCommandSocket,"QUIT".getBytes());  
                            ftpCommandSocket.close();
                            System.out.println("quiting connection...");
                        }
                        System.out.println("Exiting.");
                        appAlive = false;
                    break;
                    
                    case "HELP":
                        printHelpMenu();
                    break;
                    
                    default:
                        System.out.println ("Invalid command. Type \"HELP\""+
                        "\n for list of commands and use."); 
                }
        
                
                // C. Reset the token/response.
                response.clear();
                token = null;
                
            } while (appAlive);
        }
        
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }

    /******************************************************************
     * @Description - Called to open the connection with the server.
     *
     * @Parameter S- the tokenizer to pull the ip and port number from.
     *
     * @return a socket with either an open connection or null if failed. 
     *
     *****************************************************************/
    public static Socket openFTPCmdConnection (StringTokenizer tok) {
        
        // --- Variable Declarations  -------------------------------//
        
        /* The ip address we want to connect too.               */
        String ipAddress = tok.nextToken();
        
        /* The port number we want to connect to.               */   
        String port = tok.nextToken ();
        
        /* The attempted socket we will try to open.            */
        Socket s = null;
        
        // --- Main Routine ----------------------------------------//
        
        // See if the port can be opened.
        try {
             return new Socket(ipAddress,Integer.parseInt(port));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
      
        return null;
    }
    
    
    /******************************************************************
     * @Description - Called to send command data to the server.
     *
     * @Parameter S- the socket connection to pull data from.
     *
     * @Parameter Payload - the data to send.
     *
     * @return payload - A byte array of the data to be sent back. 
     *
     *****************************************************************/
    private static void sendCommandRequest (Socket s,byte [] payload) throws Exception {
        
        // --- Variable Declarations  -------------------------------//
        
        /* The output stream were going to used to send the command response.*/ 
        DataOutputStream outputResult = null;
        
        // --- Main Routine ----------------------------------------//
        
        // 1. Open output stream. 
        outputResult = new DataOutputStream(s.getOutputStream());
        
        // 2. Write data to the stream.
        outputResult.write (payload,0,payload.length);
    
        // 2. Close the output stream.
        outputResult.close();
        
    }
    
    
    /******************************************************************
     * @Description - Gets the response from the server and puts it 
     * into a parsable format.
     *
     * @Parameter S- the socket connection to pull data from.
     *
     * @return payload - An ArrayList of command strings.
     *
     *****************************************************************/
    private static ArrayList<String> getCommandResponse (Socket s) throws Exception {
        
        // --- Variable Declarations  -------------------------------//
        
        /* The buffered reader to pull data in.                     */
        BufferedReader inBR = null;
        
        /* The raw input of the string.                             */
        String rawInput = "";
        
        /* The incomming data after it is parsed.                   */
        ArrayList <String> commandResponse = new ArrayList <String> ();
        
        StringTokenizer token = null;
        
        // --- Main Routine ----------------------------------------//
        
        // 1. Open the input stream and read from it.
        inBR = new BufferedReader(new InputStreamReader(s.getInputStream()));
        rawInput = inBR.readLine();
        token = new StringTokenizer (rawInput);

        // 2. parse until out of tokens.
        while (token.hasMoreTokens()) {
            commandResponse.add(token.nextToken());
        }
        
        return commandResponse;
        
        
        
    }
    
    /******************************************************************
     * @Description - Gets the response from the server and puts it 
     * into a parsable format.
     * 
     * @return N/A.
     *
     *****************************************************************/
    public static void printHelpMenu () {
        
        // --- Variable Declarations  -------------------------------//

        /* N/A */
        
        // --- Main Routine ----------------------------------------//
        
        System.out.println ("---------------------------------------------");
        System.out.println (" FTP Client Rev 1.0 Reference Guide");
        System.out.println ("---------------------------------------------");
        System.out.println ("CONNECT <Server IP> <Server Port> - open con.");
        System.out.println ("LIST - Get file list in current directory.");
        System.out.println ("RETR <File Name> - Receive File.");
        System.out.println ("STORE <Local File Name> - Send File. ");
        System.out.println ("QUIT - Close FTP connection.");
        System.out.println ("EXIT - exits the application.");
        System.out.println ("HELP - list of instructions and usage.");
        System.out.println ("---------------------------------------------");
        
    }
}
