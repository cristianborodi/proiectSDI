/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
import java.io.Serializable;


public class ChunkAck implements Serializable{

	
	private static final long serialVersionUID = 4267009886985001938L;
	
	private long transactionId;
	private long seqNo;

	
	public ChunkAck(long tid, long seqNo) {
		this.transactionId = tid;
		this.seqNo = seqNo;
	}
	
	public long getSeqNo(){
		return seqNo;
	}
	
	public long getTxnID() {
		return transactionId;
	}
}
