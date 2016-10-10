/* --------------------------------------------------------------------*
 * DataPipeline.java                                                   *
 * --------------------------------------------------------------------*
 * Description - This class sends and gets files for the server and    *
 * client classes.                                                     *
 * --------------------------------------------------------------------*
 * Project: Project 1                                                  *
 * Modified by : Kristian Trevino, Josh Krolik, &  McKim A. Jacob      *
 * Date Of Creation: 10 - 05 - 2016                                    *
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
import java.io.*;
import java.net.*;

//===================================================================//
// Class Definition                                                  //
//===================================================================//
public class DataPipeline{

    String ipAddress;
    String fileName;
    int port;
    
    public DataPipeline(String fName, int portNum){
        fileName = fName;
        port = portNum;
    }
    
    public DataPipeline(String fName, int portNum, String ipA){
        port = portNum;
        ipA = ipAddress;
        fileName = fName;
        
    }

    
    public void sendData()throws Exception{
        Socket socket = new Socket(ipAddress, port);
        
        File file = new File(fileName);
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
        FileInputStream fIn = new FileInputStream(file);
        int ch = 0;
        
        while(ch != -1){
            ch = fIn.read();
            dOut.writeUTF(String.valueOf(ch));
        }
        
        fIn.close();
        socket.close();
    }
    
    boolean checkFileExists() throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        }
        return false;
    }
    
    public void receiveData() throws Exception{
        ServerSocket server = new ServerSocket(port);
        Socket client = server.accept();
        
        File file = new File(fileName);
        FileOutputStream fOut = new FileOutputStream(file);
        DataInputStream dIn = new DataInputStream(client.getInputStream());
        int ch = 0;
        String temp;
        
        while(ch != -1){
            temp = dIn.readUTF();
            ch = Integer.parseInt(temp);
            if(ch != -1)
                fOut.write(ch);
        }
        
        fOut.close();
        server.close();
    }
    

}