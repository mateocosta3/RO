import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;

public class udpser {
    public static void main(String[] args ) {

        try {

            if (args.length != 1) { //Comprobacion de sintaxis
                System.out.println("\nSintaxis: udpser port_numer\n");
                System.exit(-1);
            }

            int serverPort = Integer.parseInt(args[0]);

            byte[] inBytes= new byte[1024];
            DatagramSocket datagramSocket = new DatagramSocket(serverPort);
            int acul = 0;

            while(true) {
                
                
                DatagramPacket inDP = new DatagramPacket(inBytes, inBytes.length);
                datagramSocket.receive(inDP);
        
                InetAddress clientAddress = inDP.getAddress();
                int clientPort = inDP.getPort();
                byte [] bs = inDP.getData();

                for (int i = 0; i < bs.length;) { //Bucle que acualiza el acumulador
                    byte[] bm = new byte[4];
                    for (int j = 0 ; j < 4 ; j++) {
                        bm[j] = bs[i];
                        i = i + 1;
                    }
        
                    int ent = ByteArrayToInt(bm);
                    int aculaux = acul;
                    acul = acul + ent;
                    if (acul != aculaux) {
                        System.out.println("\nAcumulador actualizado: " + acul);
                    }
                }

                byte[] outBytes = null;
                outBytes = intToByteArray(acul);

                DatagramPacket outDP = new DatagramPacket(outBytes, outBytes.length,clientAddress,clientPort);
                datagramSocket.send(outDP);
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
