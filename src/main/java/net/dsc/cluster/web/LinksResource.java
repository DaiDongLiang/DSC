package net.dsc.cluster.web;

import java.util.Set;

import net.dsc.cluster.IClusterService;
import net.dsc.cluster.model.LinkModel;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.common.collect.ImmutableSet;

public class LinksResource extends ServerResource {
	 @Get("json")
	    public Set<LinkModel> retrieve() {
	        IClusterService clusterService = (IClusterService)getContext().getAttributes().
	                get(IClusterService.class.getCanonicalName());
	        return ImmutableSet.copyOf(clusterService.getLinks().values());
	    }
}
