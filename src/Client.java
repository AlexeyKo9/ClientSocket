import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

class Client implements Runnable {
    Socket socket;

    Scanner in;
    PrintStream out;
    ChatServer server;
    private static int clientsCount = 0;

    public Client(Socket socket, ChatServer server){
        clientsCount++;

        this.socket = socket;
        this.server = server;
        // запускаем поток
        new Thread(this).start();
    }

    void receive(String message){
        out.println(message);
    }

    public void run() {
        try {
            // получаем потоки ввода и вывода
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            // создаем удобные средства ввода и вывода
            in = new Scanner(is);
            out = new PrintStream(os);

            // читаем из сети и пишем в сеть
            out.println("Welcome to chat!");
            server.sendAll("New client add");
            server.sendAll("Clients in the chat = " + clientsCount);
            String input = in.nextLine();
            while (!input.equals("bye")) {
                server.sendAll(input);
                input = in.nextLine();
            }
            socket.close();
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        // удаляем клиента из списка
        server.removeClient(this);
        clientsCount--;
        server.sendAll("Clients in the chat = " + clientsCount);
    }
}