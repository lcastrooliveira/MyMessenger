package arquivo.gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class ProgressoTransmissao extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JProgressBar progressBar;
	
	

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ProgressoTransmissao dialog = new ProgressoTransmissao();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ProgressoTransmissao() {
		setBounds(100, 100, 344, 142);
		setTitle("Transferência em andamento...");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblProgressoDaTransferncia = new JLabel("Progresso da Transfer\u00EAncia:");
		lblProgressoDaTransferncia.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblProgressoDaTransferncia.setBounds(10, 11, 197, 27);
		contentPanel.add(lblProgressoDaTransferncia);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 50, 308, 27);
		contentPanel.add(progressBar);
	}
	
	public static int calculaPorcentagem(long tamanhoTotal, long tamanhoRecebido) {
		double porcentagem = ((double)tamanhoRecebido/tamanhoTotal)*100;
		return (int)porcentagem;
	}
}
