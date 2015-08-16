package net.floodlightcontroller.debugevent;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;

import org.projectfloodlight.openflow.types.DatapathId;

public class CustomFormatterDpid implements CustomFormatter<DatapathId> {

    @Override
    public EventResourceBuilder customFormat(@Nullable DatapathId dpid,
                                             String name,
                                             EventResourceBuilder edb) {
        if (dpid != null) {
            edb.dataFields.add(new Metadata(name, dpid.toString()));
        }
        return edb;
    }

}
