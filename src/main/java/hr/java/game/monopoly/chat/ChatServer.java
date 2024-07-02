package hr.java.game.monopoly.chat;

import hr.java.game.monopoly.jndi.ConfigurationReader;
import hr.java.game.monopoly.model.ConfigurationKey;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatServer {
    //public static final int RMI_PORT = 1099;
    private static final int RANDOM_PORT_HINT = 0;

    public static void main(String[] args) {
        try {
            Integer rmiPort = Integer.parseInt(ConfigurationReader.getValue(ConfigurationKey.RMI_PORT));
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            ChatService remoteService = new ChatServiceImpl();
            ChatService skeleton = (ChatService) UnicastRemoteObject.exportObject(remoteService, RANDOM_PORT_HINT);
            registry.rebind(ChatService.REMOTE_OBJECT_NAME, skeleton);
            System.err.println("Chat service ready!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}