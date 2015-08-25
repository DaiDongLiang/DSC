package net.dsc.hazelcast.message;

import java.io.Serializable;

public class FlowMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String json;
	
	
	public FlowMessage(String json){
		this.json = json;
	
	}
}
