package requests;

import java.io.Serializable;

public class FileTransferPermissionRequest implements Serializable {
	
	public static final short ASKING = 0;
	public static final short YES = 1;
	public static final short NO = 2;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String clienteOrigem;
	private String clienteDestino;
	private short status;	
	private String nomeArquivo;
	private long tamanho;
	private String path;	
	
	public FileTransferPermissionRequest() {
		this.status = FileTransferPermissionRequest.ASKING;
	}
		
	public void setStatus(short status) {
		this.status = status;
	}
	
	public short getStatus() {
		return this.status;
	}	
	
	public String getClienteOrigm() {
		return clienteOrigem;
	}
	public void setClienteOrigm(String clienteOrigm) {
		this.clienteOrigem = clienteOrigm.toUpperCase();
	}
	public String getClienteDestino() {
		return clienteDestino;
	}
	public void setClienteDestino(String clienteDestino) {
		this.clienteDestino = clienteDestino.toUpperCase();
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public long getTamanho() {
		return tamanho;
	}

	public void setTamanho(long tamanho) {
		this.tamanho = tamanho;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
