package net.dsc.cluster;

import java.util.List;

import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface  IClusterService extends IFloodlightService{
	public void putControllerMappingSwitch(ControllerModel c,String dpid,String role);
	public void removeControllerMappingSwitch(ControllerModel c,String dpid,String role);
	public void addController(ControllerModel c);
	public void removeController(ControllerModel c);
    public void putMasterMap(String dpid);
    public void removeMasterMap(String dpid);
	public List<ControllerModel> getControllers();
	public MultiMap<ControllerModel, SwitchConnectModel> getControllerMappingSwitch();
	public IMap<String, String> getMasterMap();
}
