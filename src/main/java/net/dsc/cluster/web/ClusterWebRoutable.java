package net.dsc.cluster.web;


import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class ClusterWebRoutable implements RestletRoutable{

	public  static final String STR_SWITCH_ID = "switchId";
	public static final String STR_ALL = "all";
	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
        router.attach("/switches/json", SwitchesResource.class);
        router.attach("/controllers/json", ControllersResource.class);
        router.attach("/balance/json", BalanceResource.class);
        router.attach("/switches/{" + STR_SWITCH_ID + "}/role/json", SwitchesRoleResource.class);
        router.attach("/entrypusher/json",FlowEntryPusherResource.class);
        router.attach("/links/json",LinksResource.class);
        router.attach("/master/json",MasterResource.class);
        router.attach("/shutdown/{controllerId}/json",ShutDownResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/cluster";
	}

}
