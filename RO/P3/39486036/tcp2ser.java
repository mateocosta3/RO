import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.net.*;

public class tcp2ser {
    public static void main(String[] args ) {

        try {

            if (args.length != 1) { //Comprobacion de sintaxis
                System.out.println("\nSintaxis: tcp2ser port_numer\n");
                System.exit(-1);
            }

            int i = 0;

            int serverPort = Integer.parseInt(args[0]);

            ServerSocket serverSocket = new ServerSocket(serverPort);

            while(true) {

                Socket socket = serverSocket.accept();

                i = i + 1;
                MyTask task = new MyTask(socket,i);
                task.start();

            }

        }catch(Exception e){
            System.out.println("\nError:" + e);
        }
    }

    public static int ByteArrayToInt(byte[] bToint) { //Metodo que convierte un array de 4 bytes en un entero
        return ByteBuffer.wrap(bToint).getInt();
    }

    public static byte[] intToByteArray(int intTob) { //Metodo que convierte un entero en un array de 4 bytes

        byte[] result = new byte[4];

        result[0] = (byte)(intTob >> 24);
        result[1] = (byte)(intTob >> 16);
        result[2] = (byte)(intTob >> 8);
        result[3] = (byte) intTob;

        return result;
    }
}

class MyTask extends Thread {
    private int exit = 0;
    private int acul = 0;
    private Socket socket;
    private int n = 0;

    public MyTask(Socket sck,int a) {
        socket = sck;
        n = a;
    }

    public void run() {

        try {

            System.out.println("\n[Nuevo cliente: numero " + n + "]");
            
            while(exit == 0) {

                byte[] inBytes= new byte[1024];

                try {

                    InputStream in = socket.getInputStream();
                    in.read(inBytes);

                    for (int i = 0; i < inBytes.length;) { //Bucle que acualiza el acumulador
                        byte[] bm = new byte[4];
                            for (int j = 0 ; j < 4 ; j++) {
                                bm[j] = inBytes[i];
                                i = i + 1;
                            }
                        
                        int ent = ByteArrayToInt(bm);
                        int aculaux = acul;
                        acul = acul + ent;
                        if (acul != aculaux) {
                            System.out.println("\nAcumulador del cliente " + n + " actualizado: " + acul);
                        }
                    }

                    byte[] outBytes = null;
                    outBytes = intToByteArray(acul);
                    OutputStream out = socket.getOutputStream();
                    out.write(outBytes);

                }catch(SocketException e) {
                    exit = 1;
                    System.out.println("\n[Cierre del cliente " + n + "]\n");
                }
            }

            socket.close();
            
        }catch(Exception e){
            System.out.println("\nError:" + e);
        }
    }

    public static int ByteArrayToInt(byte[] bToint) { //Metodo que convierte un array de 4 bytes en un entero
        return ByteBuffer.wrap(bToint).getInt();
    }

    public static byte[] intToByteArray(int intTob) { //Metodo que convierte un entero en un array de 4 bytes

        byte[] result = new byte[4];

        result[0] = (byte)(intTob >> 24);
        result[1] = (byte)(intTob >> 16);
        result[2] = (byte)(intTob >> 8);
        result[3] = (byte) intTob;

        return result;
    }
}
