package net.dsc.hazelcast;



import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastManager {
	private HazelcastManager(){ }
	
	private static class  InstanceHolder{
		private static HazelcastInstance  instance = Hazelcast.newHazelcastInstance();
		private static HazelcastInstance client = HazelcastClient.newHazelcastClient();
	}
	
	public static HazelcastInstance  getHazelcastInstance(){
		return InstanceHolder.instance;
	}
	
	
	
	public static HazelcastInstance getHazelcastClient(){
		return InstanceHolder.client;
	}
	
	public static void ShutDownClient(){
		InstanceHolder.client.shutdown();
	}
	
	public static void ShutDownInstance(){
		InstanceHolder.instance.shutdown();
	}
}
