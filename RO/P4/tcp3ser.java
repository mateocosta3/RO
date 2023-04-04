import java.io.*;
import java.util.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.*;

public class tcp3ser {
    public static void main(String[] args ) {

        try {

            if (args.length != 1) { //Comprobacion de sintaxis
                System.out.println("\nSintaxis: tcp3ser port_numer\n");
                System.exit(-1);
            }

            int i = 0;

            int serverPort = Integer.parseInt(args[0]);

            SocketAddress serverAddress = new InetSocketAddress(serverPort);

            ByteBuffer bufferSendSocket = ByteBuffer.allocate(4);
            ByteBuffer bufferRecSocket = ByteBuffer.allocate(4);

            Selector selector = Selector.open();
            
            DatagramChannel channel = DatagramChannel.open();
            channel.socket().bind(serverAddress);
            channel.configureBlocking(false);
            SelectionKey key2 = channel.register(selector, SelectionKey.OP_READ);

            int acumUDP = 0;

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(serverAddress);
            serverChannel.configureBlocking(false);
            SelectionKey key = serverChannel.register(selector, SelectionKey.OP_ACCEPT);


            while(true) {

                int readyChannels = selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while(keyIterator.hasNext()) {

                    SelectionKey nextKey = keyIterator.next();

                    if (nextKey.isAcceptable()) {

                        int acumTCP = 0;

                        SocketChannel socketChannel = serverChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ,acumTCP);

                    } else if (nextKey.isReadable()) {

                        try {

                            SocketChannel socketChannel = (SocketChannel) nextKey.channel();

                            bufferRecSocket.clear();
                            socketChannel.read(bufferRecSocket);
                            bufferRecSocket.flip();
                            int entero = bufferRecSocket.getInt();

                            Object obj = nextKey.attachment();
                            int acumTCP = (Integer)obj;

                            acumTCP += entero;
                            nextKey.attach(acumTCP);

                            System.out.println("\nValor actualizado de un acumulador TCP: " + acumTCP);

                            bufferSendSocket.clear();
                            bufferSendSocket.putInt(acumTCP);
                            bufferSendSocket.flip();
                            socketChannel.write(bufferSendSocket);
                        
                        }catch(SocketException e) {
                            continue;
                        }catch(ClassCastException e) {

                            SocketAddress remoteAddress;
                            bufferRecSocket.clear();
                            remoteAddress = channel.receive(bufferRecSocket);
                            bufferRecSocket.flip();
                            int entero = bufferRecSocket.getInt();

                            acumUDP = acumUDP + entero;
                            System.out.println("\nValor actualizado del acumulador UDP: " + acumUDP);

                            bufferSendSocket.clear();
                            bufferSendSocket.putInt(acumUDP);
                            bufferSendSocket.flip();
                            channel.send(bufferSendSocket, remoteAddress);

                        }
                    }

                    keyIterator.remove();
                }
            }
        
        }catch(Exception e){
            System.out.println("\nError:" + e);
        }
    }
}
