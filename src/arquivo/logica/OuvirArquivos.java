package arquivo.logica;

import gui.ClienteGUI;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.io.input.CountingInputStream;

import arquivo.gui.ProgressoTransmissao;



public class OuvirArquivos implements Runnable{

	private ServerSocket server;	
	private Socket connection;
	private long tamanho;	
	private CountingInputStream couterIn;
	private ClienteGUI cliente;
	
	public OuvirArquivos(ClienteGUI cliente) {
		this.cliente = cliente;
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
				server = new ServerSocket(5554);
				server.setSoTimeout(5000);
				connection = server.accept();
				couterIn = new CountingInputStream(connection.getInputStream());
				ObjectInputStream is = new ObjectInputStream(couterIn);
				String nome = is.readUTF();
				
				File arquivo = new File(nome);
				tamanho = is.readLong();
				long tamanhoOriginal = tamanho;
				
				if(tamanho == 0) {
					is.close();
					connection.close();
					server.close();
				
				}
				byte[] buffer = new byte[1024];
				BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream("recebeu/"+arquivo));
				ProgressoTransmissao barraProgresso = new ProgressoTransmissao();
				barraProgresso.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				barraProgresso.setVisible(true);				
				int progressoParcial = 0;
				try {
					while(tamanho > 0) {
						int len = is.read(buffer);
						if(len == -1) {
							break;
						}
						progressoParcial +=len;					
						barraProgresso.getProgressBar().setValue(ProgressoTransmissao.calculaPorcentagem(tamanhoOriginal, progressoParcial));
						os.write(buffer,0,len);
						tamanho -= len;						
					}
				} finally {
					os.close();					
					connection.close();
					server.close();
					System.out.println("Conexão encerrada");
				}
				barraProgresso.dispose();
				JOptionPane.showMessageDialog(null,"Arquivo transferido com sucesso!");				
				break;
			} catch (IOException e) {
				e.printStackTrace();
				try {				
					connection.close();
					server.close();
					System.out.println("Conexão encerrada");
					System.out.println("Arquivo transferido com sucesso");//
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
					break;
				}
			}
		}
		cliente.setCouterBytesRecebidosArquivos(couterIn.getByteCount());
	}	
}
