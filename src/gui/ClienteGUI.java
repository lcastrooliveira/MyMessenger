package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;

import relatorio.Relatorio;
import requests.DisconnectionAcknoledged;
import requests.FileTransferPermissionRequest;
import requests.MyDisconnectionRequest;
import arquivo.gui.TrasferirArquivoGUI;
import arquivo.logica.MandarArquivos;
import arquivo.logica.OuvirArquivos;

public class ClienteGUI extends JFrame implements Runnable {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea;
	private Socket conexao;
	private ObjectOutputStream saida;
	private ObjectInputStream entrada;
	private OuvirArquivos ouvir;
	private Map<String, String> contatos;
	private JButton btnNewButton;
	private String meuNome;
	private Thread t;
	private CountingOutputStream couterOut;
	private CountingInputStream couterIn;
	private long couterBytesMandadosArquivos;
	private long couterBytesRecebidosArquivos;
	
	public void setCouterBytesRecebidosArquivos(long couterBytesRecebidosArquivos) {
		this.couterBytesRecebidosArquivos += couterBytesRecebidosArquivos;
	}

	public String getMeuNome() {
		return meuNome;
	}

	public Map<String, String> getContatos() {
		return contatos;
	}

	public Socket getConexao() {
		return conexao;
	}

	public ObjectOutputStream getSaida() {
		return saida;
	}

	public void setSaida(ObjectOutputStream saida) {
		this.saida = saida;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public JPanel getContentPane() {
		return contentPane;
	}

	public JTextField getTextField() {
		return textField;
	}	

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public ClienteGUI(Socket socket, String nick) throws IOException {
		meuNome = nick;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 465, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 23, 424, 182);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ClienteGUI.this.btnNewButton.doClick();
			}
		});
		textField.setBounds(10, 217, 317, 20);
		contentPane.add(textField);
		textField.setColumns(10);

		btnNewButton = new JButton("Enviar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String msg = ClienteGUI.this.getTextField().getText();
					ClienteGUI.this.getSaida().writeObject(msg);
					ClienteGUI.this.getSaida().flush();
					msg = "Você escreveu: "+msg;				
					ClienteGUI.this.getTextArea().append(msg);
					ClienteGUI.this.getTextArea().append("\n");
					ClienteGUI.this.getTextArea().setCaretPosition(ClienteGUI.this.getTextArea().getText().length());
					ClienteGUI.this.getTextField().setText("");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}				
			}
		});
		btnNewButton.setBounds(335, 216, 89, 23);
		contentPane.add(btnNewButton);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 449, 21);
		contentPane.add(menuBar);

		JMenu mnNewMenu = new JMenu("Arquivo");
		menuBar.add(mnNewMenu);

		JMenuItem mntmEnviarArquivo = new JMenuItem("Enviar Arquivo");
		mntmEnviarArquivo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TrasferirArquivoGUI t = new TrasferirArquivoGUI(ClienteGUI.this);
				t.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);				
				t.setVisible(true);
				System.out.println("foi");
			}
		});
		mnNewMenu.add(mntmEnviarArquivo);

		JMenu mnSair = new JMenu("Sair");
		menuBar.add(mnSair);

		JMenuItem mntmDesconectar = new JMenuItem("Desconectar");
		mntmDesconectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MyDisconnectionRequest dReq = new MyDisconnectionRequest();
				try {
					ClienteGUI.this.getSaida().writeObject(dReq);
					ClienteGUI.this.getSaida().flush();					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
		mnSair.add(mntmDesconectar);
		this.conexao = socket;
		couterOut = new CountingOutputStream(conexao.getOutputStream());
		saida = new ObjectOutputStream(couterOut);		
		saida.flush();
		couterIn = new CountingInputStream(conexao.getInputStream());
		entrada = new ObjectInputStream(couterIn);		
		contatos = new HashMap<String, String>();
		couterBytesMandadosArquivos = 0;
		couterBytesRecebidosArquivos = 0;
	}

	@Override
	public void run() {
		try {			
			this.setTitle("Cliente do Chat: "+meuNome);			
			saida.writeObject(meuNome.toUpperCase());
			saida.flush();			
			String msg;			
			while (true)
			{				
				Object o = entrada.readObject();
				System.out.println("avaliable:"+ entrada.available());
				if(o instanceof String) {
					msg = (String)o;					
					textArea.append((msg));
					atualizaLista(msg);
					textArea.append("\n");
					ClienteGUI.this.getTextArea().setCaretPosition(ClienteGUI.this.getTextArea().getText().length());
				} else if(o instanceof DisconnectionAcknoledged) {
					JOptionPane.showMessageDialog(this, "Você desconectou do servidor");
					this.conexao.close();					
					Relatorio relatorio = new Relatorio(this, couterOut.getByteCount(), couterIn.getByteCount(), couterBytesRecebidosArquivos, couterBytesMandadosArquivos);					
					relatorio.setVisible(true);
					break;					
				} else if(o instanceof FileTransferPermissionRequest) {
					FileTransferPermissionRequest ftp = (FileTransferPermissionRequest)o;
					fileHandle(ftp);
				}
			}
		} catch (IOException e) {			
			System.out.println("Ocorreu uma Falha... .. ." + " IOException: " + e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}		
	}

	private void atualizaLista(String msg) {
		if(msg.startsWith("Conectados: ")) {			
			String recorte = msg.substring(12, msg.length());			
			StringTokenizer st = new StringTokenizer(recorte, "{=,}");
			while(st.hasMoreTokens()) {
				String key = st.nextToken().trim();
				String val = st.nextToken().trim().substring(1);				
				contatos.put(key, val);
			} 
		} else if(msg.endsWith("saiu do bate-papo!")) {
			String oldName = msg.substring(0, msg.indexOf(" "));			
			contatos.remove(oldName);
		}
	}

	private void enviarArquivo(FileTransferPermissionRequest ftp) {		
		String ip = pegaIP(ftp.getClienteOrigm());		
		MandarArquivos mandar = new MandarArquivos();
		File arquivo = new File(ftp.getPath());
		try {
			couterBytesMandadosArquivos = mandar.mandarArquivo(arquivo,ip);
			JOptionPane.showMessageDialog(this, "Arquivo enviado com sucesso!");
		} catch (UnknownHostException e) {			
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Host desconhecido!","Erro!",JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {			
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Problemas de E/S","Erro!",JOptionPane.ERROR_MESSAGE);
		}		
	}

	public String pegaIP(String nome) {
		return contatos.get(nome);
	}

	private void fileHandle(FileTransferPermissionRequest ftp) throws IOException {
		if(ftp.getStatus() == FileTransferPermissionRequest.ASKING) {
			String aviso = ftp.getClienteOrigm()+" deseja enviar um arquivo. \n";
			aviso +="Dados do arquivo: \n";
			aviso +="Nome: "+ftp.getNomeArquivo()+"\n";
			aviso += "Tamanho: "+ftp.getTamanho()+" bytes. \n";
			aviso += "Deseja receber este arquivo ?";
			int resposta = JOptionPane.showConfirmDialog(this, aviso, "Requisição de Transferência", JOptionPane.YES_NO_OPTION);
			if(resposta == JOptionPane.NO_OPTION) {
				ftp.setStatus(FileTransferPermissionRequest.NO);
				String aux = ftp.getClienteOrigm();
				ftp.setClienteOrigm(ftp.getClienteDestino());
				ftp.setClienteDestino(aux);
				saida.writeObject(ftp);				
				saida.flush();			
			} else if(resposta == JOptionPane.YES_OPTION) {
				ftp.setStatus(FileTransferPermissionRequest.YES);
				String aux = ftp.getClienteOrigm();
				ftp.setClienteOrigm(ftp.getClienteDestino());
				ftp.setClienteDestino(aux);
				ouvir = new OuvirArquivos(this);
				t = new Thread(ouvir);
				t.start();
				saida.writeObject(ftp);
				saida.flush();				
			}				
		} else if(ftp.getStatus() == FileTransferPermissionRequest.NO) {
			String aviso = ftp.getClienteOrigm() + " negou a transferência!";
			JOptionPane.showMessageDialog(this,aviso,"Aviso!",JOptionPane.ERROR_MESSAGE);
		} else if(ftp.getStatus() == FileTransferPermissionRequest.YES) {
			enviarArquivo(ftp);
		}
	}
}

