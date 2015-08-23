package net.dsc.cluster.web;

import java.util.Map;
import java.util.UUID;

import net.dsc.cluster.IClusterService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class MasterResource extends ServerResource{
	@Get("json")
	public Map<String,String> masterMap(){
        IClusterService clusterService = (IClusterService) getContext().getAttributes().get(IClusterService.class.getCanonicalName());
        return clusterService.getMasterIPMapFromCS();
	}
}
