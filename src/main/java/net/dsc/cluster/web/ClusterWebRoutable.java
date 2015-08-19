package net.dsc.cluster.web;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class ClusterWebRoutable implements RestletRoutable{

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		
		
		return router;
	}

	@Override
	public String basePath() {
		// TODO Auto-generated method stub
		return "/cluster";
	}

}
