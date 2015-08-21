package net.dsc.cluster;

import java.io.Serializable;

public class ControllerModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String controllerId;
	private String controllerIp;
	
	public ControllerModel(){}
	
	public ControllerModel(String controllerId, String controllerIp) {
		this.controllerId = controllerId;
		this.controllerIp = controllerIp;
	}

	public String getControllerId() {
		return controllerId;
	}
	public void setControllerId(String controllerId) {
		this.controllerId = controllerId;
	}
	public String getControllerIp() {
		return controllerIp;
	}
	public void setControllerIp(String controllerIp) {
		this.controllerIp = controllerIp;
	}
	@Override
	public String toString() {
		return "ControllerModel [controllerId=" + controllerId
				+ ", controllerIp=" + controllerIp + "]";
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if(null==obj) return false;
		if(!(obj instanceof SwitchConnectModel)) return false;
		ControllerModel c=(ControllerModel)obj;

		return c.getControllerId().equals(controllerId)&&c.getControllerIp().equals(controllerIp);

	}
	@Override
	public int hashCode() {
		return controllerId.hashCode() +controllerIp.hashCode();
	}
}
