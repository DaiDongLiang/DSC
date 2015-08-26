package net.dsc.hazelcast.listener;

import net.dsc.hazelcast.IMessageListener;
import net.dsc.hazelcast.message.ShutDownMessage;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class ShutDownMessageListener implements MessageListener<ShutDownMessage>{
	private static IMessageListener iMessageListener;
	
	public ShutDownMessageListener(IMessageListener iMessageListener){
		this.iMessageListener  = iMessageListener;
		
	}
	@Override
	public void onMessage(Message<ShutDownMessage> message) {
		iMessageListener.progressShutDownMessage(message);
	}
	
}
