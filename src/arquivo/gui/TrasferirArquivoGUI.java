package arquivo.gui;

import gui.ClienteGUI;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import requests.FileTransferPermissionRequest;

public class TrasferirArquivoGUI extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private JTextField diretorio;
	private Frame frame;
	private JComboBox comboBox;	
	
	public String getNomeArquivo() {
		return this.diretorio.getText();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {			
			TrasferirArquivoGUI dialog = new TrasferirArquivoGUI(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public TrasferirArquivoGUI(Frame owner) {		
		super(owner,false);
		this.setTitle("Transferir um arquivo....");
		this.frame = owner;
		setBounds(100, 100, 347, 193);
		getContentPane().setLayout(null);
		
		JLabel lblNomeDoDestino = new JLabel("Nome do Destino");
		lblNomeDoDestino.setBounds(10, 11, 122, 14);
		getContentPane().add(lblNomeDoDestino);
		
		JLabel lblArquivo = new JLabel("Arquivo");
		lblArquivo.setBounds(10, 62, 46, 14);
		getContentPane().add(lblArquivo);
		
		diretorio = new JTextField();
		diretorio.setBounds(10, 87, 207, 20);
		getContentPane().add(diretorio);
		diretorio.setColumns(10);
		
		JButton btnArquivo = new JButton("Arquivo...");
		btnArquivo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showDialog(TrasferirArquivoGUI.this, "Escolher");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					TrasferirArquivoGUI.this.diretorio.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnArquivo.setBounds(232, 86, 89, 23);
		getContentPane().add(btnArquivo);
		
		JButton btnEnviar = new JButton("Enviar");
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(TrasferirArquivoGUI.this.diretorio.getText().trim() != "") {
					ClienteGUI clienteRef = (ClienteGUI)TrasferirArquivoGUI.this.frame;
					clienteRef.getContatos().keySet().toArray();
					String valor = (String)comboBox.getSelectedItem();										
					File arquivo = new  File(TrasferirArquivoGUI.this.diretorio.getText());
					FileTransferPermissionRequest ftp = new FileTransferPermissionRequest();					
					ftp.setClienteDestino(valor);					
					ftp.setClienteOrigm(clienteRef.getMeuNome());
					ftp.setNomeArquivo(arquivo.getName());
					ftp.setTamanho(arquivo.length());					
					ftp.setPath(arquivo.getAbsolutePath());
					try {
						clienteRef.getSaida().writeObject(ftp);
						clienteRef.getSaida().flush();						
					} catch (UnknownHostException e1) {						
						e1.printStackTrace();
						JOptionPane.showMessageDialog(TrasferirArquivoGUI.this, "Erro: Destino Desconhecido!");
					} catch (IOException e1) {						
						e1.printStackTrace();
						JOptionPane.showMessageDialog(TrasferirArquivoGUI.this, "Erro: Problemas de E/S!");
					} finally {
						TrasferirArquivoGUI.this.dispose();
					}
				}
			}
		});
		btnEnviar.setBounds(7, 118, 89, 23);
		getContentPane().add(btnEnviar);
		
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TrasferirArquivoGUI.this.dispose();
			}
		});
		btnCancelar.setBounds(106, 118, 89, 23);
		getContentPane().add(btnCancelar);
		
		comboBox = new JComboBox();		
		comboBox.setBounds(10, 31, 142, 20);
		getContentPane().add(comboBox);
		ClienteGUI clienteRef = (ClienteGUI)TrasferirArquivoGUI.this.frame;
		Map<String,String> listaEnvioPessoas = clienteRef.getContatos();
		listaEnvioPessoas.remove(clienteRef.getMeuNome());
		if(listaEnvioPessoas.keySet().toArray().length <= 0) {
			comboBox.setEnabled(false);
		} else {
			comboBox.setModel(new DefaultComboBoxModel(listaEnvioPessoas.keySet().toArray()));
		}
	}
}
