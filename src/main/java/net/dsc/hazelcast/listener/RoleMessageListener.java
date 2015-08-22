package net.dsc.hazelcast.listener;

import net.dsc.hazelcast.IMessageListener;
import net.dsc.hazelcast.message.RoleMessage;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class RoleMessageListener implements MessageListener<RoleMessage>{
	private static IMessageListener iMessageListener;
	
	
	public RoleMessageListener(IMessageListener iMessageListener){
		this.iMessageListener = iMessageListener;
		
	}
	@Override
	public void onMessage(Message<RoleMessage> message) {
		iMessageListener.progressRoleMessage(message);
		
	}

}
