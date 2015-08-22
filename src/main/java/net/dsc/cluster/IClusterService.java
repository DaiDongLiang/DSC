package net.dsc.cluster;

import java.util.List;
import java.util.Map;

import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

import net.dsc.cluster.model.ControllerModel;
import net.dsc.cluster.model.LinkModel;
import net.dsc.cluster.model.SwitchConnectModel;
import net.dsc.cluster.model.SwitchModel;
import net.floodlightcontroller.core.module.IFloodlightService;

public interface  IClusterService extends IFloodlightService{
	
	//控制器<->交换机映射
	public  void putControllerMappingSwitch(ControllerModel c,String dpid,String role);
	public void removeControllerMappingSwitch(ControllerModel c, String dpid,String role);
	public MultiMap<ControllerModel, SwitchConnectModel> getControllerMappingSwitch();
	public boolean isConnected(String dpid,String uuid);
	//控制器集合
	public void addController(ControllerModel c);
	public void removeController(ControllerModel c);
	public IMap<String, ControllerModel> getControllers();
	
	//master switch dpid<->controllerId映射 
	//Master集合
	public void putMasterMap(String dpid);
    public void removeMasterMap(String dpid);
	public IMap<String, String> getMasterMap();
	
	//控制器负载集合
	public void ControllerLoadIncrease(String controllerId,int num);
	public void ControllerLoadReduce(String controllerId,int num);
	public void ControllerLoadReset(String controllerId);
	public List<String> getSortedControllerLoad();
	public IMap<String , Integer> getControllerLoad();
	
	//交换机集合
	public void putSwitch(SwitchModel s);
	public void removeSwitch(String dpid);
	public IMap<String, SwitchModel> getSwithcs();
	
	//链路集合
	public MultiMap<String, LinkModel> getLinks();
	public void addLink(String dpid,LinkModel link);
	public void deleteLink(String dpid,LinkModel link);
	
}
