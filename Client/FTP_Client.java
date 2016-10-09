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
            
        /* The ouput stream were going to keep open.                */
        DataOutputStream dataOut = null;
        
        /* The input stream were going to keep open.                */
        BufferedReader dataIn = null; 
        
        /* The file name we want to send or recive.                 */
        String fileName = "";
        
        /* The port number we want to preform the data connection. */
        int dataPort = 6789;
        
        DataPipeline ftpData = null;
        
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
                
                
                // B. Select which action to take and take it.
                switch (token.nextToken().toUpperCase()) {
                    
                    case "CONNECT":
                    
                        // A. Attempt to open connection.
                        if (ftpCommandSocket == null || !ftpCommandSocket.isConnected()) {
                            ftpCommandSocket = openFTPCmdConnection(token);
                        
                            if (ftpCommandSocket != null) {
                                // B. Open readers.
                                dataOut = new DataOutputStream(ftpCommandSocket.getOutputStream());
                                dataIn = new BufferedReader(new InputStreamReader(ftpCommandSocket.getInputStream()));
                                
                                // C. Send Request to connet.
                                sendCommandRequest (dataOut,"CONNECT"); 
      
                                // D. Get Response and handle it. 
                                response = getCommandResponse (dataIn);
      
                                if (response.get(0).equals("SUCCESS")) {
                                    System.out.println("Connection Successful!");
                                }
                                else {
                                    System.out.println("Unable to connect.");
                                    ftpCommandSocket = null;
                                }
    
                            }
                            else {
                                System.out.println ("Could not connect.");
                            }
                        }
                        else {
                            System.out.println ("ERROR: Only one connection can be made at a time.\n close existing one by using QUIT command first.");
                        }
                    break;
                    
                    case "LIST":
                        
                        // A. Preform request.
                        if (ftpCommandSocket != null) {
                            sendCommandRequest (dataOut,"LIST"); 
                            response = getCommandResponse (dataIn);
                            
                            System.out.println ("\n------------------------------------------------");
                            System.out.println ("Available Files");
                            System.out.println ("------------------------------------------------");
                           
                           
                            // B. Echo back the result. 
                            for (int i = 0; i < response.size(); i++) {
                                System.out.println(response.get(i));
                            }
                            
                            System.out.println("");
                            
                        }
                        else {
                            System.out.println("ERROR: A connection must be opened first using CONNECT command.");
                        }

                    break;
                    
                    case "RETR":
                        if (ftpCommandSocket != null) {
               
                            // A. get file name and port # to use and send.
                            if (token.hasMoreTokens()) {
                                fileName = token.nextToken();
                                ftpData = new DataPipeline(fileName,dataPort);
                                sendCommandRequest (dataOut, "RETR "+ fileName + " " + dataPort);
                                System.out.println("retrieving...");
                            }
                            
                            // B. Get response and determine whether or not we will open command stream.
                            response = getCommandResponse (dataIn);
                            if (response.get(0).equals("READY")) {
                                ftpData.receiveData();
                            }
                            
                            else if (response.get(0).equals("FILENOTFOUND")) {
                                System.out.println("ERROR: File doesn't exist on remote server.");
                            }
                            else {
                                System.out.println("ERROR: could not tranfer file.");
                            }
                            
                        }
                        else {
                            System.out.println("ERROR: A connection must be opened first using CONNECT command.");
                        }
                        
                        // C. Destroy the data pipeline.
                        ftpData = null;
                        
                    break;
                    
                    case "STORE":
                        if (ftpCommandSocket != null) {
                            // A. get file name and port # to use and send.
                            if (token.hasMoreTokens()) {
                                fileName = token.nextToken();
                               
                                ftpData = new DataPipeline(fileName,dataPort,ftpCommandSocket.getRemoteSocketAddress().toString());
                                // TODO: Check if file exists first?
                                if (ftpData.checkFileExists())    
                                {
                                    sendCommandRequest (dataOut, "STORE " + fileName + " " + dataPort);
                                    System.out.println("Storing...");   
                                    
                                    // B. Get response and determine whether or not we will open command stream.
                                    response = getCommandResponse (dataIn);
                                    if (response.get(0).equals("READY")) {
                                        ftpData.sendData();
                                    }
                                    else {
                                        System.out.println("ERROR: There was an error sending file.");
                                    }
                                }
                                
                                else {
                                    System.out.println("ERROR: File doesn't exist.");
                                }
                                
                            }
                            
                            
                        }
                        else {
                            System.out.println("ERROR: A connection must be opened first using CONNECT command.");
                        }
                        
                        // C. Destroy the data pipeline.
                        ftpData = null;
                        
                    break;
                    
                    case "QUIT":
                        if (ftpCommandSocket != null) {
                            sendCommandRequest (dataOut,"QUIT");  
                            dataOut.close();
                            dataIn.close();
                            ftpCommandSocket.close();
                            ftpCommandSocket = null;
                            System.out.println("quiting connection...");
                        }
                        else {
                            System.out.println("No Connections are open.");
                        }
                        
                    break;
                    
                    case "EXIT":
                    
                        // A. Close any open connections if we have any.
                        if (ftpCommandSocket != null) {
                            sendCommandRequest (dataOut,"QUIT");
                            dataOut.close();
                            dataIn.close();
                            ftpCommandSocket.close();
                            ftpCommandSocket = null;
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
        String ipAddress = "";
        
        /* The port number we want to connect to.               */   
        String port = "";
        
        /* The attempted socket we will try to open.            */
        Socket s = null;
        
        // --- Main Routine ----------------------------------------//
        
        // A. Get the tokens.
        if (tok.hasMoreTokens()) {
            ipAddress = tok.nextToken();
            if (tok.hasMoreTokens()) {
                port = tok.nextToken ();
            }
        }
        
        // B. Check the input.
        if (ipAddress == null || ipAddress.isEmpty() || port == null || port.isEmpty()) {
            System.out.println("CONNECT ERROR: Ip address / port number are invalid.");
            return null;
        }
        
        // C. See if the port can be opened.
        try {
             return new Socket(ipAddress,Integer.parseInt(port));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
      
    }
    
    
    /******************************************************************
     * @Description - Called to send command data to the server.
     *
     * @Parameter dataOut - the output stream to send info.
     *
     * @Parameter Payload - the data to send.
     *
     * @return payload - A byte array of the data to be sent back. 
     *
     *****************************************************************/
    private static void sendCommandRequest (DataOutputStream dataOut,String payload) throws Exception {
        
        // --- Variable Declarations  -------------------------------//
        
        /* N/A                                                      */
        
        // --- Main Routine ----------------------------------------//
      
        // 2. Write data to the stream.
        dataOut.writeBytes (payload + "\n");
        dataOut.flush();
        
    }
    
    
    /******************************************************************
     * @Description - Gets the response from the server and puts it 
     * into a parsable format.
     *
     * @Parameter dataIn - The input stream buffered reader.
     *
     * @return payload - An ArrayList of command strings.
     *
     *****************************************************************/
    private static ArrayList<String> getCommandResponse (BufferedReader dataIn) throws Exception {
        
        // --- Variable Declarations  -------------------------------//
        
        /* The raw input of the string.                             */
        String rawInput = "";
        
        /* The incomming data after it is parsed.                   */
        ArrayList <String> commandResponse = new ArrayList <String> ();
        
        /* The tokenizer used to parse the command response.        */
        StringTokenizer token = null;
        
        // --- Main Routine ----------------------------------------//
        
        // 1. Open the input stream and read from it.
        rawInput = dataIn.readLine();
        token = new StringTokenizer (rawInput);

        // 2. parse until out of tokens.
        while (token.hasMoreTokens()) {
            commandResponse.add(token.nextToken());
        }
        
        // 3. Return the result of connection.
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
