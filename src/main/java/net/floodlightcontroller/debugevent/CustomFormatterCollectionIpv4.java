package net.floodlightcontroller.debugevent;

import java.util.Collection;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;

import org.projectfloodlight.openflow.types.IPv4Address;

import com.google.common.base.Joiner;

class CustomFormatterCollectionIpv4 implements
CustomFormatter<Collection<IPv4Address>> {

@Override
public EventResourceBuilder
        customFormat(@Nullable Collection<IPv4Address> ipv4Addresses2,
                     String name, EventResourceBuilder edb) {
    if (ipv4Addresses2 != null) {
        String ipv4AddressesStr2 = "--";
        if (!ipv4Addresses2.isEmpty()) {
            ipv4AddressesStr2 = Joiner.on(" ").join(ipv4Addresses2);
        }
        edb.dataFields.add(new Metadata(name, ipv4AddressesStr2));
    }
    return edb;
}
}
