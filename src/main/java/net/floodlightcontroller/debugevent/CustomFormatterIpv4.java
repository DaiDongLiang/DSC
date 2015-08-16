package net.floodlightcontroller.debugevent;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;
import net.floodlightcontroller.packet.IPv4;

public class CustomFormatterIpv4 implements CustomFormatter<Integer> {

    @Override
    public EventResourceBuilder customFormat(@Nullable Integer obj,
                                             String name,
                                             EventResourceBuilder edb) {
        if (obj != null) {
            edb.dataFields.add(new Metadata(name, IPv4.fromIPv4Address(obj)));
        }
        return edb;
    }

}
