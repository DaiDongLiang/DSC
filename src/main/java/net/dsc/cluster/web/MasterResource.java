package net.dsc.cluster.web;

import java.util.Collection;

import net.dsc.cluster.IClusterService;
import net.dsc.cluster.model.SwitchConnectModel;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class MasterResource extends ServerResource{
	@Get("json")
	public Collection<SwitchConnectModel> masterMap(){
        IClusterService clusterService = (IClusterService) getContext().getAttributes().get(IClusterService.class.getCanonicalName());
        return clusterService.getControllerMappingSwitch().values();
	}
}
