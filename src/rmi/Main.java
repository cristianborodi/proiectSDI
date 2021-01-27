/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;


public class Main {

	static int regPort = Configurations.REG_PORT;

	static Registry registry ;
        
      
         
       static int nr = 0;
                
	/**
	 * respawns replica servers and register replicas at master
	 * @param master
	 * @throws IOException
	 */
	static void respawnReplicaServers(Master master)throws IOException{
		System.out.println("[@main] respawning replica servers ");
		// TODO make file names global
		BufferedReader br = new BufferedReader(new FileReader("repServers.txt"));
		int n = Integer.parseInt(br.readLine().trim());
		ReplicaLoc replicaLoc;
		String s;

		for (int i = 0; i < n; i++) {
			s = br.readLine().trim();
			replicaLoc = new ReplicaLoc(i, s.substring(0, s.indexOf(':')) , true);
			ReplicaServer rs = new ReplicaServer(i, "./"); 

			ReplicaInterface stub = (ReplicaInterface) UnicastRemoteObject.exportObject((Remote) rs, 0);
			registry.rebind("ReplicaClient"+i, stub);

			master.registerReplicaServer(replicaLoc, stub);

			System.out.println("replica server state [@ main] = "+rs.isAlive());
		}
		br.close();
	}
        
	public static void launchClients() throws IOException, NotBoundException, MessageNotFoundException{
                    while(1>0){
                                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                                
                                System.out.println( "Introduceti 1 - type; 2 - write -  sau 3 - view directory");
                                
                                String  numar = reader.readLine();
                                nr=Integer.parseInt(numar); //convertim nr din string in int
                    switch(nr){
                        
                        case 1:
                                Client c = new Client();
                   
                                System.out.println("Type: Introduceti numele fisierului pe care doriti sa-l cititi" );
                                System.out.println();
      
                                String fisier = reader.readLine(); // citim de la tastatura
                                        char[] ss = " ".toCharArray(); // creem un fisier gol
                                        byte[] data = new byte[ss.length]; // pentru a putea citi un fisier fara a fi nevoie sa il creem 
                                         for (int i = 0; i < ss.length; i++) { 
                                               data[i] = (byte) ss[i]; 
                                         }
                                 c.write(fisier, data); // creem fisierul gol
                                 String str = new String(c.read(fisier)); // citim continutul fisierului
                                 
                                 break;
                        case 2:
                            
                                Client c1 = new Client();
                                System.out.println( "Write: introduceti numele fisierului in care doriti sa scrieti" );
                                System.out.println();
        
                                String filename = reader.readLine();
                                System.out.println( "Introduceti textul: " );
                                System.out.println();
                                String text = reader.readLine();
                                ss = text.toCharArray();
                                data = new byte[ss.length];
                                    for (int i = 0; i < ss.length; i++) {
                                              data[i] = (byte) ss[i];
                                             }
                                c1.write(filename, data);	
                                
                                break;
                                
                        case 3:
                                c = new Client();
                                File directoryPath = new File("C:\\Users\\Toshiba\\Documents\\NetBeansProjects\\rmi");
                                String contents[]=directoryPath.list();
                                System.out.println("Numerul de fisiere: " + contents.length);
                                System.out.println("Fisierele: ");
                                    for (int i = 0; i < contents.length; i++) {
                                             System.out.println(contents[i]);
                                        } 
                                break;
                                
                        default: System.exit(0);
                    }
               
                    }
                
	}
         
	/**
	 * runs a custom test as follows
	 * 1. write initial text to "file1"
	 * 2. reads the recently text written to "file1"
	 * 3. writes a new message to "file1"
	 * 4. while the writing operation in progress read the content of "file1"
	 * 5. the read content should be = to the initial message
	 * 6. commit the 2nd write operation
	 * 7. read the content of "file1", should be = initial messages then second message 
	 * 
	 * @throws IOException
	 * @throws NotBoundException
	 * @throws MessageNotFoundException
	 */
	public  static void customTest() throws IOException, NotBoundException, MessageNotFoundException{
		Client c = new Client();
		String fileName = "file1";
                
		char[] ss = "[INITIAL DATA!]".toCharArray(); // len = 15
		byte[] data = new byte[ss.length];
		for (int i = 0; i < ss.length; i++) 
			data[i] = (byte) ss[i];

		c.write(fileName, data);

		c = new Client();
		ss = "File 1 test test END".toCharArray(); // len = 20
		data = new byte[ss.length];
		for (int i = 0; i < ss.length; i++) 
			data[i] = (byte) ss[i];

		
		byte[] chunk = new byte[Configurations.CHUNK_SIZE];

		int seqN =data.length/Configurations.CHUNK_SIZE;
		int lastChunkLen = Configurations.CHUNK_SIZE;

		if (data.length%Configurations.CHUNK_SIZE > 0) {
			lastChunkLen = data.length%Configurations.CHUNK_SIZE;
			seqN++;
		}
		
		WriteAck ackMsg = c.masterStub.write(fileName);
		ReplicaServerClientInterface stub = (ReplicaServerClientInterface) registry.lookup("ReplicaClient"+ackMsg.getLoc().getId());

		FileContent fileContent;
		@SuppressWarnings("unused")
		ChunkAck chunkAck;
		//		for (int i = 0; i < seqN; i++) {
		System.arraycopy(data, 0*Configurations.CHUNK_SIZE, chunk, 0, Configurations.CHUNK_SIZE);
		fileContent = new FileContent(fileName, chunk);
		chunkAck = stub.write(ackMsg.getTransactionId(), 0, fileContent);


		System.arraycopy(data, 1*Configurations.CHUNK_SIZE, chunk, 0, Configurations.CHUNK_SIZE);
		fileContent = new FileContent(fileName, chunk);
		chunkAck = stub.write(ackMsg.getTransactionId(), 1, fileContent);

		// read here 
		List<ReplicaLoc> locations = c.masterStub.read(fileName);
		System.err.println("[@CustomTest] Read1 started ");

		// TODO fetch from all and verify 
		ReplicaLoc replicaLoc = locations.get(0);
		ReplicaServerClientInterface replicaStub = (ReplicaServerClientInterface) registry.lookup("ReplicaClient"+replicaLoc.getId());
		fileContent = replicaStub.read(fileName);
		System.err.println("[@CustomTest] data:");
		System.err.println(new String(fileContent.getData()));


		// continue write 
		for(int i = 2; i < seqN-1; i++){
			System.arraycopy(data, i*Configurations.CHUNK_SIZE, chunk, 0, Configurations.CHUNK_SIZE);
			fileContent = new FileContent(fileName, chunk);
			chunkAck = stub.write(ackMsg.getTransactionId(), i, fileContent);
		}
		// copy the last chuck that might be < CHUNK_SIZE
		System.arraycopy(data, (seqN-1)*Configurations.CHUNK_SIZE, chunk, 0, lastChunkLen);
		fileContent = new FileContent(fileName, chunk);
		chunkAck = stub.write(ackMsg.getTransactionId(), seqN-1, fileContent);

		
		
		//commit
		ReplicaLoc primaryLoc = c.masterStub.locatePrimaryReplica(fileName);
		ReplicaServerClientInterface primaryStub = (ReplicaServerClientInterface) registry.lookup("ReplicaClient"+primaryLoc.getId());
		primaryStub.commit(ackMsg.getTransactionId(), seqN);

		
		// read
		locations = c.masterStub.read(fileName);
		System.err.println("[@CustomTest] Read3 started ");

		replicaLoc = locations.get(0);
		replicaStub = (ReplicaServerClientInterface) registry.lookup("ReplicaClient"+replicaLoc.getId());
		fileContent = replicaStub.read(fileName);
		System.err.println("[@CustomTest] data:");
		System.err.println(new String(fileContent.getData()));

	}

	static Master startMaster() throws AccessException, RemoteException{
		Master master = new Master();
		MasterServerClientInterface stub = 
				(MasterServerClientInterface) UnicastRemoteObject.exportObject(master, 0);
		registry.rebind("MasterServerClientInterface", stub);
		System.err.println("Server ready");
		return master;
	}

	public static void main(String[] args) throws IOException, NotBoundException, MessageNotFoundException {


		try {
			LocateRegistry.createRegistry(regPort);
			registry = LocateRegistry.getRegistry(regPort);

			Master master = startMaster();
			respawnReplicaServers(master);
                  

//			customTest();2

			launchClients();

		} catch (RemoteException   e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

}
