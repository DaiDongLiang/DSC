package net.floodlightcontroller.debugevent;

import java.util.Collection;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;
import net.floodlightcontroller.devicemanager.SwitchPort;

public class CustomFormatterCollectionAttachmentPoint implements
CustomFormatter<Collection<SwitchPort>> {

@Override
public EventResourceBuilder
        customFormat(@Nullable Collection<SwitchPort> aps2, String name,
                     EventResourceBuilder edb) {
    if (aps2 != null) {
        StringBuilder apsStr2 = new StringBuilder();
        if (aps2.size() == 0) {
            apsStr2.append("--");
        } else {
            for (SwitchPort ap : aps2) {
                apsStr2.append(ap.getSwitchDPID().toString());
                apsStr2.append("/");
                apsStr2.append(ap.getPort());
                apsStr2.append(" ");
            }
            // remove trailing space
            apsStr2.deleteCharAt(apsStr2.length());
        }
        edb.dataFields.add(new Metadata(name, apsStr2.toString()));
    }
    return edb;
}
}