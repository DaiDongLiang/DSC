package net.dsc.cluster;

/**
 * This is a dummy marker. IListener enforces call ordering by type. However,
 * for IHAListeners we only have a single order. So we use this type as a
 * placeholder to satisfy the generic requirement.
 * 这是一个空标记。 IListener通过类型强制调用顺序。然而对于IHAListeners我们只有一个单一的顺序。
 * 因此我们使用这种类型作为占位符，以满足通用要求。 
 * @author gregor
 *
 */
public enum HAListenerTypeMarker {
}
