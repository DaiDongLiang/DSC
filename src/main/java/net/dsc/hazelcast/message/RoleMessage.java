package net.dsc.hazelcast.message;

import java.io.Serializable;

public class RoleMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String Role ;
	public String SwitchId;
	
	public RoleMessage(String Role,String SwitchId){
		this.Role = Role;
		
		this.SwitchId = SwitchId;
	}
}
