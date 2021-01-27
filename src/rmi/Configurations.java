/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
public class Configurations {

	public final static int REG_PORT = 50900;
	public final static String REG_ADDR = "localhost";
	public final static String MasterserverName = "MasterServer";
	public static final int CHUNK_SIZE = 3; // in bytes
	public static final int HEART_BEAT_RATE = 10000; // in milliseconds
	public static final int REPLICATION_N = 3; // number of file replicas
}
