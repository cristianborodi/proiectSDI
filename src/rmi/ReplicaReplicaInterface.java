/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface ReplicaReplicaInterface extends ReplicaInterface {
	
	public boolean reflectUpdate(long txnID, String fileName, ArrayList<byte[]> data) throws RemoteException, IOException;
	
	public void releaseLock(String fileName)throws RemoteException;
}
