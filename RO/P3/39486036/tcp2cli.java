import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.net.*;

public class tcp2cli {
    public static void main(String[] args ) {

        try{

            if (args.length != 2) { //Comprobacion de sintaxis correcta
                System.out.println("\nSintaxis: tcp2cli ip_address port_numer\n");
                System.exit(-1);
            }

            String servername = args[0];
            int serverPort = Integer.parseInt(args[1]);

            Scanner reader = new Scanner (System.in);
            int exit = 0;

            Socket socket = new Socket();
            SocketAddress serverAddress = new InetSocketAddress(servername,serverPort);
            socket.connect(serverAddress,15000);
            
            while(exit == 0) {

                byte[] outBytes= new byte[1024];
                byte[] inBytes= new byte[4];
                int j = 0;
                int comprobacion = 0;

                System.out.println("\nIntroduce uno o varios enteros (separados por /): ");

                String cadena = reader.nextLine(); //Recoge la cadena de enteros introducida por el usuario
                String[] ints = cadena.split("/"); //Elimina las '/'' y crea un array de strings con los enteros
                int[] enteros = new int[ints.length]; //Crea un array de enteros del mismo tamaño que el de strings

                for (int i = 0; i < enteros.length; i++) { //Bucle que introduce los enteros en el array de bytes hasta que se acaben o encuentre un cero
                    enteros[i] = Integer.parseInt(ints[i]); //Convierte los strings en enteros y los mete en el array

                    if (enteros[i] != 0) {
                        comprobacion = comprobacion + 1;
                        byte[] b = intToByteArray(enteros[i]);
                        for (int k = 0; k < b.length ; k++) {
                            outBytes[j] = b[k];
                            j = j + 1;
                        }
                    }

                    else {
                        if (comprobacion == 0) {
                            exit = 1;
                        }
                        break;
                    }
                }
                
                if (comprobacion != 0) {

                    OutputStream out = socket.getOutputStream();
                    out.write(outBytes);
        
                    InputStream in = socket.getInputStream();
                    in.read(inBytes);
                    int aculfinal = ByteArrayToInt(inBytes);

                    System.out.println("\nValor final del acumulador: " + aculfinal);
                }
            }
            
            socket.close();
            
        }catch(NumberFormatException e){ //Excepcion que cubre el caso de que solo se introduzca un salto de linea
            System.exit(-1);
        }catch(SocketTimeoutException e){ //Comprobacion del temporizador
            System.out.println("\nTiempo de espera excedido\n");
        }catch (Exception e) {
            System.out.println("\nError:" + e);
        }  
    }

    public static byte[] intToByteArray(int intTob) { //Metodo que convierte un entero en un array de 4 bytes

        byte[] result = new byte[4];

        result[0] = (byte)(intTob >> 24);
        result[1] = (byte)(intTob >> 16);
        result[2] = (byte)(intTob >> 8);
        result[3] = (byte) intTob;

        return result;
    }

    public static int ByteArrayToInt(byte[] bToint) { //Metodo que convierte un array de 4 bytes en un entero
        return ByteBuffer.wrap(bToint).getInt();
    }
}
