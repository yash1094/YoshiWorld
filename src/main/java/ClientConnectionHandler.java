

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientConnectionHandler extends Thread {
    Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;

    int clientNumber;

    boolean isOpen;

    public ClientConnectionHandler(Socket socket, int number) {
        clientSocket = socket;
        clientNumber = number;
        isOpen = true;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String request = "";

        try {
            while (isOpen) {
                request = readMessage();
                processRequest(request);
            }
            clientSocket.close();
            System.out.println("Client #" + clientNumber + " has closed");
        } catch (Exception e) {}
    }

    public void processRequest(String request) {
        if (request.equals("CLOSE")) {
            isOpen = false;
        } else if (request.equals("GET ALL TEXT")) {
            String [] array = FileIOFunctions.getAllTexts();

            sendMessage("GET ALL TEXT");
            sendMessage(array[0]);
            sendMessage(array[1]);
        } else {}
    }

    public void sendMessage(String message) {
        sendMessageAsByte(message.getBytes());
    }

    public void sendMessageAsByte(byte [] message) {
        try {
            out.writeInt(message.length);
            out.write(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readMessage() {
        return new String(readMessageAsByte());
    }

    public byte [] readMessageAsByte() {
        byte [] data = new byte[256];
        int length;

        try {
            length = in.readInt();
            if (length > 0) {
                data = new byte[length];
                in.readFully(data, 0, data.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}