package net.dsc.hazelcast.listener;

import net.dsc.hazelcast.IMessageListener;
import net.dsc.hazelcast.message.FlowMessage;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class FlowMessageListener implements MessageListener<FlowMessage>{
	
	private static IMessageListener iMessageListener;
	
	public FlowMessageListener(IMessageListener iMessageListener){
		this.iMessageListener  = iMessageListener;
		
	}
	@Override
	public void onMessage(Message<FlowMessage> message) {
		// TODO Auto-generated method stub
		iMessageListener.progressFlowMessage(message);
		
	}

}
