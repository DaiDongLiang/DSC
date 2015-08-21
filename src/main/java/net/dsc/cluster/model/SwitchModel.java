package net.dsc.cluster.model;

import java.io.Serializable;
import java.util.Date;

public class SwitchModel implements Serializable{

	private static final long serialVersionUID = 1L;
	private String dpid;
	private Date date;
	private String ip;
	private String versoin;
	
	public static class Builder{
		private String dpid="0";
		private Date date=null;
		private String ip="0.0.0.0";
		private String versoin="None";
		
		public Builder(){}
		
		public Builder dpid(String val){
			dpid=val;
			return this;
		}
		public Builder date(Date val){
			date=val;
			return this;
		}
		public Builder ip(String val){
			ip=val;
			return this;
		}
		public Builder version(String val){
			versoin=val;
			return this;
		}
		public SwitchModel build(){
			return new SwitchModel(this);
		}
	}
	
	public SwitchModel(Builder builder) {
		this.setDpid(builder.dpid);
		this.setDate(builder.date);
		this.setIp(builder.ip);
		this.setVersoin(builder.versoin);
	}
	
	public String getDpid() {
		return dpid;
	}
	public void setDpid(String dpid) {
		this.dpid = dpid;
	}

	public String getVersoin() {
		return versoin;
	}
	public void setVersoin(String versoin) {
		this.versoin = versoin;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
