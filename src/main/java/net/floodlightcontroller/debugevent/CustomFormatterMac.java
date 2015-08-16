package net.floodlightcontroller.debugevent;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;

import org.projectfloodlight.openflow.util.HexString;

public class CustomFormatterMac implements CustomFormatter<Long> {

    @Override
    public EventResourceBuilder customFormat(@Nullable Long obj,
                                             String name,
                                             EventResourceBuilder edb) {
        if (obj != null) {
            edb.dataFields.add(new Metadata(name, HexString.toHexString(obj,
                                                                        6)));
        }
        return edb;
    }

}
