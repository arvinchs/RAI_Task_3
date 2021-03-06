/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rai_task3;
import java.io.*;
import java.net.*;
import java.util.HashSet;

/**
 *
 * @author Thinkpad
 */
public class ServerThread extends Thread {
    private static HashSet<String> names = new HashSet<String>();
        private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();   
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }
                out.println("NAMEACCEPTED");
                writers.add(out);
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
}
