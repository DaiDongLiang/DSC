package net.dsc.hazelcast;

import net.dsc.hazelcast.message.FlowMessage;
import net.floodlightcontroller.core.module.IFloodlightService;

import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.MultiMap;

public interface IHazelcastService extends IFloodlightService{
	public <K,V> IMap<K,V> getMap(String MapName);
	
	public <K,V> MultiMap<K,V> getMultiMap(String MapName);
	
	public <T> ISet<T> getSet(String SetName);
	
	public <T> IQueue<T> getQueue(String QueueName);
	
	public <T> IList<T>  getList(String ListName);
	
	public void publishFlowMessage(FlowMessage flowMessage);
	
	public Member getLocalMember();
	
	public void addMemberListener(MembershipListener mebershipListener);
}
