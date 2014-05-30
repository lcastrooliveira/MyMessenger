package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class TelaInicial {

	private JFrame frame;
	private JTextField tfNickName;
	private JTextField tfIP;
	private JCheckBox chckbxLocalhost;	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaInicial window = new TelaInicial();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TelaInicial() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 313, 326);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNickname = new JLabel("NickName:");
		lblNickname.setBounds(33, 69, 65, 14);
		frame.getContentPane().add(lblNickname);
		
		tfNickName = new JTextField();
		tfNickName.setBounds(43, 94, 185, 20);
		frame.getContentPane().add(tfNickName);
		tfNickName.setColumns(10);
		
		JLabel lblIpDoServidor = new JLabel("IP do Servidor:");
		lblIpDoServidor.setBounds(33, 125, 102, 14);
		frame.getContentPane().add(lblIpDoServidor);
		
		tfIP = new JTextField();
		tfIP.setColumns(10);
		tfIP.setBounds(43, 150, 185, 20);
		frame.getContentPane().add(tfIP);
		
		chckbxLocalhost = new JCheckBox("LocalHost");
		chckbxLocalhost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(TelaInicial.this.chckbxLocalhost.isSelected()) {
					TelaInicial.this.tfIP.setText("localhost");
					TelaInicial.this.tfIP.setEditable(false);
				} else {
					TelaInicial.this.tfIP.setText("");
					TelaInicial.this.tfIP.setEditable(true);
					TelaInicial.this.tfIP.requestFocus();
				}
			}
		});
		chckbxLocalhost.setBounds(38, 177, 97, 23);
		frame.getContentPane().add(chckbxLocalhost);
		
		JButton btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iniciarChat();
			}
		});
		btnConectar.setBounds(176, 217, 89, 23);
		frame.getContentPane().add(btnConectar);
		
		JLabel lblNewLabel = new JLabel("Trabalho de Redes I");
		lblNewLabel.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 18));
		lblNewLabel.setBounds(33, 28, 232, 30);
		frame.getContentPane().add(lblNewLabel);
		
		JButton button = new JButton("Sair");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button.setBounds(74, 217, 89, 23);
		frame.getContentPane().add(button);
	}

	protected void iniciarChat() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if(TelaInicial.this.tfNickName.getText() != "" && TelaInicial.this.tfIP.getText() != "") {
						String nick = TelaInicial.this.tfNickName.getText().toUpperCase();
						String ip = TelaInicial.this.tfIP.getText();
						Socket socket = new Socket(ip, 5555);
						ClienteGUI frame = new ClienteGUI(socket,nick);
						frame.setVisible(true);
						Thread t = new Thread(frame);
						t.start();
						TelaInicial.this.frame.dispose();
					}										
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Um erro ocorreu","Erro!",JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});		
	}
}
