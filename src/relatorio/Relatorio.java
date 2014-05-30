package relatorio;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Relatorio extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private  long chatOut;
	private  long chatIn;
	private  long arquivoOut;
	private  long arquivoIn;
	private JTextArea textArea;	

	/**
	 * Create the dialog.
	 */
	public Relatorio(Frame frame,long chatOut, long chatIn,long arquivoOut,long arquivoIn) {
		super(frame);
		this.chatIn = chatIn;
		this.chatOut = chatOut;
		this.arquivoIn = arquivoIn;
		this.arquivoOut = arquivoOut;
		setTitle("Relt\u00F3rio Final");
		setBounds(100, 100, 450, 320);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblRelatrioDeFim = new JLabel("Relat\u00F3rio de Fim de Sess\u00E3o:");
		lblRelatrioDeFim.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblRelatrioDeFim.setBounds(10, 11, 222, 14);
		contentPanel.add(lblRelatrioDeFim);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 36, 404, 196);
		contentPanel.add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JButton btnNewButton = new JButton("Ok");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnNewButton.setBounds(170, 243, 89, 23);
		contentPanel.add(btnNewButton);
		constroiRelatorio();
	}
	
	private void constroiRelatorio() {
		textArea.setEditable(false);
		textArea.append("Bytes Trocados: \n");
		textArea.append("***No chat: *** \n");
		textArea.append("No outputStream(recebidos do servidor): \n");
		textArea.append(chatOut+" \n");
		textArea.append("No inputStream(enviados ao servidor): \n");
		textArea.append(chatIn+" \n");
		textArea.append(" \n");
		textArea.append("*** No envio de arquivos: *** \n");
		textArea.append("Bytes enviados transferindo arquivos(entre usuários): \n");
		textArea.append(arquivoIn+" \n");
		textArea.append("Bytes recebidos transferindo arquivos(entre usuários): \n");
		textArea.append(arquivoOut+" \n");
	}
	
}
