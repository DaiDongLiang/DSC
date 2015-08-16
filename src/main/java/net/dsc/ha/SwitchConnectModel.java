package net.dsc.ha;

import java.io.Serializable;

public class SwitchConnectModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String controllerId;
	private String dpid;
	private String role;

	public String getControllerId() {
		return controllerId;
	}

	public void setControllerId(String controllerId) {
		this.controllerId = controllerId;
	}

	public String getDpid() {
		return dpid;
	}

	public void setDpid(String dpid) {
		this.dpid = dpid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "SwitchConnectModel [controllerId=" + controllerId + ", dpid="
				+ dpid + ", role=" + role + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if(null==obj) return false;
		if(!(obj instanceof SwitchConnectModel)) return false;
		SwitchConnectModel s=(SwitchConnectModel)obj;
		return s.getDpid().equals(dpid)&&s.getRole().equals(role)&&s.getControllerId().equals(controllerId);
	}
	
	@Override
	public int hashCode() {
		return controllerId.hashCode()+dpid.hashCode()+role.hashCode();
	}
}
