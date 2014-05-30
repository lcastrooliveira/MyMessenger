package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import requests.DisconnectionAcknoledged;
import requests.FileTransferPermissionRequest;
import requests.MyDisconnectionRequest;

public class ServidorSocket extends Thread {
    private static Map<String, ObjectOutputStream> MAP_CLIENTES;   
    private Socket conexao;
    private String nomeCliente;
    private static Map<String,String> LISTA_DE_NOMES = new HashMap<String,String>();    
    
    private ObjectInputStream entrada;
	private ObjectOutputStream saida;
     
    
    public ServidorSocket() { }
    
    public ServidorSocket(Socket socket) {
        this.conexao = socket;
    }
    public boolean armazena(String newName) {
    	if(LISTA_DE_NOMES.containsKey(newName)) {
    		return true;
    	} else {
    		LISTA_DE_NOMES.put(newName, conexao.getInetAddress().toString());
    		return false;    		
    	}        
    }
    
    public String retornaIP(String nome) {
    	if(LISTA_DE_NOMES.containsKey(nome)) {
    		return LISTA_DE_NOMES.get(nome);
    	}
    	return "pau";
    }
    
    public void remove(String oldName) {
        LISTA_DE_NOMES.remove(oldName);
    }
    public static void main(String args[]) {
        MAP_CLIENTES = new HashMap<String, ObjectOutputStream>();
        try {
            ServerSocket server = new ServerSocket(5555);
            System.out.println("ServidorSocket rodando na porta 5555");
            while (true) {
                Socket conexao = server.accept();
                Thread t = new ServidorSocket(conexao);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }
    public void run() {
    	
    	String[] out = {" do bate-papo!"};
        try {        	
            entrada = new ObjectInputStream(this.conexao.getInputStream());
            saida = new ObjectOutputStream(this.conexao.getOutputStream());
            saida.flush();
            Object o = entrada.readObject();            
            this.nomeCliente = (String)o;
            if (armazena(this.nomeCliente)) {
            	saida.writeObject(("Este nome ja existe! Conecte novamente com outro Nome."));
            	saida.flush();
                this.conexao.close();
                return;
            } else {                
            	if(this.conexao.getInetAddress().toString().equals("/127.0.0.1")) {                	
            		LISTA_DE_NOMES.put(this.nomeCliente, "/"+ServidorSocket.getCurrentEnvironmentNetworkIp());
                }
            }
            if (this.nomeCliente == null) {
            	return;
            }
            MAP_CLIENTES.put(this.nomeCliente, saida);
            String contatos = LISTA_DE_NOMES.toString();
            System.out.println(contatos);
            saida.writeObject("Conectados: " + contatos);
            saida.flush();    
            for (Map.Entry<String, ObjectOutputStream> cliente : MAP_CLIENTES.entrySet()) {
            ObjectOutputStream chat = cliente.getValue();
	            if (chat != saida) {
	            	chat.writeObject("Conectados: " + contatos);
	            	chat.flush();
	            }
            }
            //Object ob = entrada.readObject();
            while (true) {
                Object ob = entrada.readObject();
	            if(ob instanceof String) {
	            	String mensagem = (String)ob;
	                String[] msg = mensagem.split(":");
	                send(saida, " escreveu: ", msg);	                	                
	        
	            } else if(ob instanceof MyDisconnectionRequest) {
	            	System.out.println(this.nomeCliente + " saiu do bate-papo!");                        
	                remove(this.nomeCliente);
	                MAP_CLIENTES.remove(this.nomeCliente);
	                saida.writeObject(new DisconnectionAcknoledged());
	                saida.flush();
	                this.conexao.close();
	                send(saida, " saiu", out);
	                break;
	            } else if(ob instanceof FileTransferPermissionRequest) {
	            	FileTransferPermissionRequest ftp = (FileTransferPermissionRequest)ob;
	            	ObjectOutputStream chat = MAP_CLIENTES.get(ftp.getClienteDestino());
	            	System.out.println("destino do ftp: "+ftp.getClienteDestino());
	            	chat.writeObject(ftp);
	            	chat.flush();
	            }
            }            
        } catch (IOException e) {
            System.out.println("Cliente saiu sem confirmação");
            //e.printStackTrace();
            remove(this.nomeCliente);
            MAP_CLIENTES.remove(this.nomeCliente);
            send(saida, " saiu", out);
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    /**
     * Se o array da msg tiver tamanho igual a 1, então envia para todos
     * Se o tamanho for 2, envia apenas para o cliente escolhido
     */
    public void send(ObjectOutputStream saida, String acao, String[] msg) {
    	try {
    		out:
    	        for (Map.Entry<String, ObjectOutputStream> cliente : MAP_CLIENTES.entrySet()) {
    	            ObjectOutputStream chat = cliente.getValue();
    	            if (chat != saida) {
    	                if (msg.length == 1) {
    	                    chat.writeObject(this.nomeCliente + acao + msg[0]);
    	                    chat.flush();
    	                } else {
    	                    if (msg[1].toUpperCase().equalsIgnoreCase(cliente.getKey())) {
    	                        chat.writeObject(this.nomeCliente + acao + msg[0]);
    	                        chat.flush();
    	                        break out;
    	                    }
    	                }
    	            }
    	        }
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}        
    }
    
    public static String getCurrentEnvironmentNetworkIp() {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            System.out.println("Somehow we have a socket error...");
        }

        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress addr = address.nextElement();
                if (!addr.isLoopbackAddress() && !addr.isSiteLocalAddress()
                        && !(addr.getHostAddress().indexOf(":") > -1)) {
                    return addr.getHostAddress();
                }
            }
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
}
