import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.lang.ProcessBuilder;

public class PhotoCollectionRequestHandler implements Runnable {
	
	// shell script names
	private static final String importScript = "my_import.sh";
	private static final String registerScript = "my_register.sh";
	private static final String encodeScript = "my_encode.sh";
	private static final String recognitionScript = "my_recog_run.sh";

    // a flag indicating if the photo collection is triggered successfully
    private boolean triggerSuccessFlag;

    // connection to app side client
    private final Socket socket;
    private DataInputStream inputFromClient;
    private DataOutputStream outputToClient;

    PhotoCollectionRequestHandler(final Socket socket) {

        this.socket = socket;
        triggerSuccessFlag = false;

        try {

            inputFromClient = new DataInputStream(socket.getInputStream());
            outputToClient = new DataOutputStream(socket.getOutputStream());

        }
        catch (IOException e) {

            inputFromClient = null;
            outputToClient = null;
            e.printStackTrace();

        }

    }

    /**
     * Receive a trigger message with username, collect face photos for this user, and reply
     * a confirmation message to app side client
     */
    @Override
    public void run() {

        String username = null;

        // receive a trigger message with username and its length from client
        try {

            final int usernameLength = inputFromClient.readInt();
            final byte[] ch = new byte[1024];
            int lenTotal = 0, len = 0;
            while (lenTotal < usernameLength && len != -1) {

                len = inputFromClient.read(ch, lenTotal, usernameLength);
                if (len != -1)
                    lenTotal += len;

            }
            username = new String(ch, 0, usernameLength);

        } catch (IOException e) {

            e.printStackTrace();
            triggerSuccessFlag = false;

        }

        // TODO: trigger photo collection shell script and collect photos for this user
        //       and don't forget to update triggerSuccessFlag
        //       only trigger if username is not null
		if (username != null) {
			
			final ProcessBuilder builder = new ProcessBuilder();
			
			// import cv environment
			System.out.println("Running " + importScript + "...");
			builder.command(".", importScript);
			try {
				
				final Process process = builder.start();
				final int exitVal = process.waitFor();
				
				if (exitVal == 0)
					triggerSuccessFlag = true;
				else
					triggerSuccessFlag = false;
				
			} catch (IOException | InterruptedException e) {
				
				e.printStackTrace();
				triggerSuccessFlag = false;

			}
			
			// collect face photos
			
			
			
			
			// train dataset
			
		} else {
			
			triggerSuccessFlag = false;
			
		}
		
		
		

        // reply a confirmation message to client
        try {

            outputToClient.writeBoolean(triggerSuccessFlag);
            outputToClient.flush();

        } catch (IOException e) {

            e.printStackTrace();
            return;

        }

    }

}
