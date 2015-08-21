package net.dsc.cluster.model;

import com.google.common.base.Objects;

public class MacPort {
	private String mac;
	private String port;
	
	public MacPort(String mac, String port) {
		this.mac = mac;
		this.port = port;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	@Override
	public boolean equals(Object obj) {
		return mac.equals(obj);
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(mac);
	}
	
}
