import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.net.*;

public class tcp3cli {
    public static void main(String[] args ) {

        try{

            if (args.length != 2 && args.length != 3) { //Comprobacion de sintaxis correcta
                System.out.println("\nSintaxis: tcp3cli ip_address port_numer [-u]\n");
                System.exit(-1);
            }

            String servername = args[0];
            int serverPort = Integer.parseInt(args[1]);

            Scanner reader = new Scanner (System.in);
            int exit = 0;

            Socket socket = new Socket();
            SocketAddress serverAddress = new InetSocketAddress(servername,serverPort);

            ByteBuffer bufferSendSocket = ByteBuffer.allocate(4);
            ByteBuffer bufferRecSocket = ByteBuffer.allocate(4);

            if (args.length == 2) {

                SocketChannel socketChannel = SocketChannel.open();

                socketChannel.connect(serverAddress);

                while(exit == 0) {

                    System.out.println("\nIntroduce un entero (0 para finalizar): ");

                    int entero = reader.nextInt(); //Recoge el entero introducida por el usuario
                    
                    if (entero != 0) {
                        bufferSendSocket.clear();
                        bufferSendSocket.putInt(entero);
                        bufferSendSocket.flip();
                        socketChannel.write(bufferSendSocket);
                
                        bufferRecSocket.clear();
                        socketChannel.read(bufferRecSocket);
                        bufferRecSocket.flip();
                        int aculfinal = bufferRecSocket.getInt();

                        System.out.println("\nValor final del acumulador: " + aculfinal);
                    }

                    else exit = 1;

                    
                }

            }
            
            else {

                DatagramChannel datagramChannel = DatagramChannel.open();

                while(exit == 0) {

                    System.out.println("\nIntroduce un entero (0 para finalizar): ");

                    int entero = reader.nextInt(); //Recoge el entero introducida por el usuario
                    
                    if (entero != 0) {

                        bufferSendSocket.clear();
                        bufferSendSocket.putInt(entero);
                        bufferSendSocket.flip();
                        datagramChannel.send(bufferSendSocket, serverAddress);

                        bufferRecSocket.clear();
                        datagramChannel.receive(bufferRecSocket);
                        bufferRecSocket.flip();
                        int aculfinal = bufferRecSocket.getInt();

                        System.out.println("\nValor final del acumulador: " + aculfinal);
                    }

                    else exit = 1;;

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
