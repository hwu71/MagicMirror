import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.lang.ProcessBuilder;

public class PhotoCollectionRequestHandler implements Runnable {
	
	// trigger message header
	private static final int PHOTO_COLLECTION = 1;
	private static final int REFRESH = 2;
	
	// shell script names
	private static final String refreshScript = "my_refresh.sh";
	private static final String registerScript = "my_register.sh";
	private static final String encodeScript = "my_encode.sh";
	private static final String recognitionScript = "my_recog_run.sh";

    // connection to app side client
    private final Socket socket;
    private DataInputStream inputFromClient;
    private DataOutputStream outputToClient;

    PhotoCollectionRequestHandler(final Socket socket) {

        this.socket = socket;

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

		// a flag indicating if the photo collection is triggered successfully
		boolean triggerSuccessFlag = false;

        // receive a trigger message from client
        try {
			
			// run specific process according to header
			final int header = inputFromClient.readInt();
			
			if (header == REFRESH) {
				
				// trigger shell script to refresh page
				System.out.println("Refreshing smart mirror...");
				processRunningBuilder("bash", refreshScript);
					
			} else if (header == PHOTO_COLLECTION) {
				
				// receive a trigger message with username and its length from client
				final int usernameLength = inputFromClient.readInt();
				final byte[] ch = new byte[1024];
				int lenTotal = 0, len = 0;
				while (lenTotal < usernameLength && len != -1) {

					len = inputFromClient.read(ch, lenTotal, usernameLength);
					if (len != -1)
						lenTotal += len;

				}
				username = new String(ch, 0, usernameLength);
				
				// trigger shell scripts to collect face photos and train dataset
				if (username != null) {
					
					// collect face photos
					System.out.println("Collecting face photos...");
					triggerSuccessFlag = processRunningBuilder("bash", registerScript, username);
					
					if (triggerSuccessFlag) {
						
						// train dataset
						System.out.println("Training dataset...");
						triggerSuccessFlag = processRunningBuilder("bash", encodeScript);
						
					}
					
				} else {
					
					triggerSuccessFlag = false;
					
				}
					
				if (triggerSuccessFlag)
					System.out.println("Collection success!");
				else
					System.out.println("Collection failed!");
				
			} else {
				
				System.out.println("Unsupported message header " + header);
				triggerSuccessFlag = false;
				
			}

        } catch (IOException e) {

            e.printStackTrace();
            triggerSuccessFlag = false;

        }
		
		try {
				
			// reply a confirmation message to client
			outputToClient.writeBoolean(triggerSuccessFlag);
			outputToClient.flush();
			
		} catch (IOException e) {

            e.printStackTrace();

        }
		
    }
	
	/**
     * Helper method to build a process and run the given command
     *
     * @param commandList  the list of command segments to be executed
	 * @return successFlag the success status of the running process
     */
	private boolean processRunningBuilder(String... commandList) {
		
		final ProcessBuilder builder = new ProcessBuilder();
		builder.command(commandList);
		
		boolean successFlag = false;
		try {
				
			final Process process = builder.start();
			final int exitVal = process.waitFor();
				
			if (exitVal == 0)
				successFlag = true;
			else
				successFlag = false;
				
		} catch (IOException | InterruptedException e) {
				
			e.printStackTrace();
			successFlag = false;

		}
		
		return successFlag;
		
	}

}
