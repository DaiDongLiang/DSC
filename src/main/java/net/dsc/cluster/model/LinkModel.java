package net.dsc.cluster.model;

import java.io.Serializable;

import net.floodlightcontroller.routing.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class LinkModel implements Serializable{

	private static final long serialVersionUID = 1L;
    @JsonProperty("src-switch")
	private String src;
    @JsonProperty("src-port")
	private Integer srcPort;
    @JsonProperty("dst-switch")
	private String dst;
    @JsonProperty("dst-port")
	private Integer dstPort;
    
	public LinkModel(String src, Integer srcPort, String dst, Integer dstPort) {
		super();
		this.src = src;
		this.srcPort = srcPort;
		this.dst = dst;
		this.dstPort = dstPort;
	}
	public static LinkModel of(Link link){
		return new LinkModel(link.getSrc().toString(), link.getSrcPort().getPortNumber(), link.getDst().toString(), link.getDstPort().getPortNumber());
	}
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public Integer getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(Integer srcPort) {
		this.srcPort = srcPort;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public Integer getDstPort() {
		return dstPort;
	}

	public void setDstPort(Integer dstPort) {
		this.dstPort = dstPort;
	}
	
    @Override
    public int hashCode() {
    	return Objects.hashCode(dst,dstPort,src,srcPort);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LinkModel other = (LinkModel) obj;
    	return src.equals(other.getSrc())&&srcPort.equals(other.getSrcPort())&&dst.equals(other.getDst())&&dstPort.equals(other.getDstPort());
    }
}
