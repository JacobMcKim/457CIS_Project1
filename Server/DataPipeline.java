import java.io.*;
import java.net.*;

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
		ServerSocket server = new ServerSocket(8000);
		Socket client = server.accept();
		
		File file = new File(fileName);
		FileInputStream fInStream = new FileInputStream(file);
		BufferedInputStream bInStream = new BufferedInputStream(fInStream);
		
		OutputStream os = client.getOutputStream();
		
		byte[] fileContent = new byte[4096];
		int bytes = 0;
		int offset = 0;
		
		while((bytes = bInStream.read(fileContent)) != -1)
			os.write(fileContent, offset, bytes);
		

		os.close();
		bInStream.close();
		server.close();
	}
	
		
    	boolean checkFileExists() throws IOException {
    		File file = new File(fileName);
        	if (file.exists()) {
            		return true;
        	}
        	return false;
    	}
	
	
	public void receiveData()throws Exception{
		Socket socket = new Socket(ipAddress, port);
		byte[] contents = new byte[4096];
		int read = 0;

		String directory = System.getProperty("user.dir");
		
		FileOutputStream fOutStream = new FileOutputStream(directory);
		BufferedOutputStream bOutStream = new BufferedOutputStream(fOutStream);
		InputStream inStream = socket.getInputStream();
		
		while((read = inStream.read(contents)) != -1)
			bOutStream.write(contents, 0, read);
		
		fOutStream.close();
		inStream.close();
		socket.close();
	}
	public static void main(String[] args) {
		

	}

}
