package net.dsc.hazelcast.message;

public class RoleMessage {
	
	public String Role ;
	public String SwitchId;
	
	public RoleMessage(String Role,String SwitchId){
		this.Role = Role;
		
		this.SwitchId = SwitchId;
	}
}
