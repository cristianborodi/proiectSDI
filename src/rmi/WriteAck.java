/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
import java.io.Serializable;


public class WriteAck implements Serializable{
	
	private static final long serialVersionUID = -4764830257785399352L;
	
	private long transactionId;
	private long timeStamp;
	private ReplicaLoc loc;

	
	public WriteAck(long tid, long timeStamp, ReplicaLoc replicaLoc) {
		this.transactionId = tid;
		this.timeStamp = timeStamp;
		this.loc = replicaLoc;
	}


	public long getTransactionId() {
		return transactionId;
	}


	public long getTimeStamp() {
		return timeStamp;
	}


	public ReplicaLoc getLoc() {
		return loc;
	}
}
