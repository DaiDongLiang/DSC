package net.dsc.hazelcast.message;

import java.io.Serializable;

public class FlowMessage implements Serializable{
	public String json;
	
	
	public FlowMessage(String json){
		this.json = json;
	
	}
}
