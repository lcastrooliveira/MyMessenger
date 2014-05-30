package arquivo.logica;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JDialog;

import org.apache.commons.io.output.CountingOutputStream;

import arquivo.gui.ProgressoTransmissao;

public class MandarArquivos {


	Socket conexao;

	public long mandarArquivo(File arquivo, String ip) throws UnknownHostException, IOException {

		conexao = new Socket(ip,5554);
		CountingOutputStream counterOut = new CountingOutputStream(conexao.getOutputStream());
		ObjectOutputStream os = new ObjectOutputStream(counterOut);
		os.writeUTF(arquivo.getName());
		os.writeLong(arquivo.length());
		System.out.println("tamanho do arquivo: "+arquivo.length());
		ProgressoTransmissao progresso = new ProgressoTransmissao();
		progresso.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		progresso.setVisible(true);
		//manda o arquivo
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(arquivo));
		byte[] buffer = new byte[1024];
		int len;
		int porcentagem;
		int progressoParcial = 0;
		while((len = is.read(buffer)) > 0) {
			os.write(buffer,0,len);
			os.flush();
			progressoParcial += len;
			porcentagem = ProgressoTransmissao.calculaPorcentagem(arquivo.length(), progressoParcial);
			progresso.getProgressBar().setValue(porcentagem);
		}
		os.flush();
		progresso.dispose();
		return counterOut.getByteCount();
	}
}
