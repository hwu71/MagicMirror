import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.lang.ProcessBuilder;

public class ReloadConfigRequestHandler implements Runnable {
	
	// the user that is using the smart mirror
	private String usingUser;
	private static final String UNDEFINED = "undefined";

	// config file generation script
	private static final String configGenerationScript = "generate_config.py";

    // connection to app side client
    private final Socket socket;
	private BufferedReader inputFromClient;

    ReloadConfigRequestHandler(final Socket socket) {
		
		usingUser = UNDEFINED;

        this.socket = socket;

        try {

			inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        }
        catch (IOException e) {

            inputFromClient = null;
            e.printStackTrace();

        }

    }

    /**
     * Receive a reload message with username, generate config file for
	 * this user, and refresh smart mirror webpage
     */
    @Override
    public void run() {

        String username = null;

        // receive a reload message with username and its length from client
        try {

			final int usernameLength = Integer.valueOf(
				inputFromClient.readLine());
			
			final char[] ch = new char[1024];
            int lenTotal = 0, len = 0;
            while (lenTotal < usernameLength && len != -1) {

				len = inputFromClient.read(ch, lenTotal, usernameLength);
                if (len != -1)
                    lenTotal += len;

            }
            username = new String(ch, 0, usernameLength);

        } catch (IOException e) {

            e.printStackTrace();

        }

        // run a process to trigger config.js generation shell script with given username
		if (username != null && (usingUser.equals(UNDEFINED) || usingUser.equals(username))) {
			
			System.out.println("Serving " + username + "...");
			
			final ProcessBuilder builder = new ProcessBuilder();
			builder.command("python", configGenerationScript, username);
			try {
				
				final Process process = builder.start();
				final int exitVal = process.waitFor();
				
				if (exitVal == 0)
					System.out.println("Reloaded config.js for " + username + "!");
				else
					System.out.println("Failed to reload config.js for " + username + "!");
				
			} catch (IOException | InterruptedException e) {
				
				e.printStackTrace();
				System.out.println("Failed to reload config.js for " + username + "!");

			}
			
		}
		
		// TODO: refresh smart mirror webpage
		
    }

}
