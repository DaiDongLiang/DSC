package net.floodlightcontroller.core.internal;

import net.floodlightcontroller.core.IOFConnectionBackend;
import net.floodlightcontroller.core.IOFSwitchBackend;
import net.floodlightcontroller.core.IOFSwitchDriver;
import net.floodlightcontroller.core.SwitchDescription;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.types.DatapathId;

/**
 * Maintain a registry for SwitchDrivers. Drivers can register with the
 * registry and a user can get IOFSwitch instances based on the switch's
 * OFDescriptionStatistics.
 *	维护一个交换机驱动的注册表，驱动能登记到注册表并且使用者可以通过交换机的OFDescriptionStatistics得到IOFSwitch实例
 * A driver registers itself by specifying a <i>prefix string</i> of the
 * switch's <i>manufacturer</i> description. When a user request an
 * IOFSwitch instance the registry matches the manufacturer description
 * of the switch against the prefixes in the registry.
 *	一个驱动注册它本身通过指定交换机制造商的前置字符串。
 *	当使用者请求IOFSwitch实例，注册表匹配制造商的前置字符串。
 * See getOFSwitchInstance() for a description of the lookup contract
 *
 * @author gregor
 *
 */
public interface ISwitchDriverRegistry {

    /**
     * Register an IOFSwitchDriver with the registry
     *	在注册表中注册一个IOFSwitchDriver
     * @param manufacturerDescriptionPrefix Register the given prefix
     * with the driver. 制造商描述字符串的前缀
     * @param driver A IOFSwitchDriver instance to handle IOFSwitch instantiation
     * for the given manufacturer description prefix
     * @throws IllegalStateException If the the manufacturer description is
     * already registered
     * @throws NullPointerExeption if manufacturerDescriptionPrefix is null
     * @throws NullPointerExeption if driver is null
     */
    void addSwitchDriver(String manufacturerDescriptionPrefix,
                         IOFSwitchDriver driver);
    /**
     * Return an IOFSwitch instance according to the switch description.
     *	返回一个IOFSwitch实例根据交换机描述
     * The driver with the <i>longest matching prefix</i> will be picked first.
     * The description is then handed over to the choosen driver to return an
     * IOFSwitch instance. If the driver does not return an IOFSwitch
     * (returns null) the registry will continue to the next driver with
     * a matching manufacturer description prefix. If no driver returns an
     * IOFSwitch instance the registry returns a default OFSwitchImpl instance.
     *
     * The returned switch will have its description reply and
     * switch properties set according to the SwitchDescription passed in
     *
     *@param connection The main OF connection
     * @param description The SwitchDescription for which to return an
     * IOFSwitch implementation
     * @param factory The factory to use to create OF messages.
     * @param datapathId
     * @param debugCounterService; used to create a new switch if one does not exist
     * @return A IOFSwitch implementation matching the description or an
     * OFSwitchImpl if no driver returned a more concrete instance.
     * @throws NullPointerException If the SwitchDescription or any of its
     * members is null.
     */
    IOFSwitchBackend getOFSwitchInstance(IOFConnectionBackend connection, SwitchDescription description, OFFactory factory, DatapathId datapathId);
}
