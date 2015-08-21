package net.dsc.cluster.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.TableId;
import org.projectfloodlight.openflow.types.U16;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntries;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;
import net.floodlightcontroller.util.ActionUtils;
import net.floodlightcontroller.util.InstructionUtils;
import net.floodlightcontroller.util.MatchUtils;

public class FlowEntryPusherResource extends ServerResource{
	
	private static final Logger log = LoggerFactory
			.getLogger(FlowEntryPusherResource.class);
	
	public static final String TABLE_NAME = "controller_staticflowtableentry";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SWITCH = "switch";
	public static final String COLUMN_TABLE_ID = "table";
	public static final String COLUMN_ACTIVE = "active";
	public static final String COLUMN_IDLE_TIMEOUT = "idle_timeout";
	public static final String COLUMN_HARD_TIMEOUT = "hard_timeout";
	public static final String COLUMN_PRIORITY = "priority";
	public static final String COLUMN_COOKIE = "cookie";

	// Common location for Match Strings. Still the same, but relocated.
	public static final String COLUMN_IN_PORT = MatchUtils.STR_IN_PORT;

	public static final String COLUMN_DL_SRC = MatchUtils.STR_DL_SRC;
	public static final String COLUMN_DL_DST = MatchUtils.STR_DL_DST;
	public static final String COLUMN_DL_VLAN = MatchUtils.STR_DL_VLAN;
	public static final String COLUMN_DL_VLAN_PCP = MatchUtils.STR_DL_VLAN_PCP;
	public static final String COLUMN_DL_TYPE = MatchUtils.STR_DL_TYPE;

	public static final String COLUMN_NW_TOS = MatchUtils.STR_NW_TOS;
	public static final String COLUMN_NW_ECN = MatchUtils.STR_NW_ECN;
	public static final String COLUMN_NW_DSCP = MatchUtils.STR_NW_DSCP;
	public static final String COLUMN_NW_PROTO = MatchUtils.STR_NW_PROTO;
	public static final String COLUMN_NW_SRC = MatchUtils.STR_NW_SRC; // includes CIDR-style netmask, e.g. "128.8.128.0/24"
	public static final String COLUMN_NW_DST = MatchUtils.STR_NW_DST;

	public static final String COLUMN_SCTP_SRC = MatchUtils.STR_SCTP_SRC;
	public static final String COLUMN_SCTP_DST = MatchUtils.STR_SCTP_DST;
	public static final String COLUMN_UDP_SRC = MatchUtils.STR_UDP_SRC;
	public static final String COLUMN_UDP_DST = MatchUtils.STR_UDP_DST;
	public static final String COLUMN_TCP_SRC = MatchUtils.STR_TCP_SRC;
	public static final String COLUMN_TCP_DST = MatchUtils.STR_TCP_DST;
	public static final String COLUMN_TP_SRC = MatchUtils.STR_TP_SRC; // support for OF1.0 generic transport ports (possibly sent from the rest api). Only use these to read them in, but store them as the type of port their IpProto is set to.
	public static final String COLUMN_TP_DST = MatchUtils.STR_TP_DST;

	/* newly added matches for OF1.3 port start here */
	public static final String COLUMN_ICMP_TYPE = MatchUtils.STR_ICMP_TYPE;
	public static final String COLUMN_ICMP_CODE = MatchUtils.STR_ICMP_CODE;

	public static final String COLUMN_ARP_OPCODE = MatchUtils.STR_ARP_OPCODE;
	public static final String COLUMN_ARP_SHA = MatchUtils.STR_ARP_SHA;
	public static final String COLUMN_ARP_DHA = MatchUtils.STR_ARP_DHA;
	public static final String COLUMN_ARP_SPA = MatchUtils.STR_ARP_SPA;
	public static final String COLUMN_ARP_DPA = MatchUtils.STR_ARP_DPA;

	/* IPv6 related columns */
	public static final String COLUMN_NW6_SRC = MatchUtils.STR_IPV6_SRC;
	public static final String COLUMN_NW6_DST = MatchUtils.STR_IPV6_DST;
	public static final String COLUMN_IPV6_FLOW_LABEL = MatchUtils.STR_IPV6_FLOW_LABEL;
	public static final String COLUMN_ICMP6_TYPE = MatchUtils.STR_ICMPV6_TYPE;
	public static final String COLUMN_ICMP6_CODE = MatchUtils.STR_ICMPV6_CODE;
	public static final String COLUMN_ND_SLL = MatchUtils.STR_IPV6_ND_SSL;
	public static final String COLUMN_ND_TLL = MatchUtils.STR_IPV6_ND_TTL;
	public static final String COLUMN_ND_TARGET = MatchUtils.STR_IPV6_ND_TARGET;	

	public static final String COLUMN_MPLS_LABEL = MatchUtils.STR_MPLS_LABEL;
	public static final String COLUMN_MPLS_TC = MatchUtils.STR_MPLS_TC;
	public static final String COLUMN_MPLS_BOS = MatchUtils.STR_MPLS_BOS;

	public static final String COLUMN_METADATA = MatchUtils.STR_METADATA;
	public static final String COLUMN_TUNNEL_ID = MatchUtils.STR_TUNNEL_ID;

	public static final String COLUMN_PBB_ISID = MatchUtils.STR_PBB_ISID;
	/* end newly added matches */

	public static final String COLUMN_ACTIONS = "actions";

	public static final String COLUMN_INSTR_GOTO_TABLE = InstructionUtils.STR_GOTO_TABLE; // instructions are each getting their own column, due to write and apply actions, which themselves contain a variable list of actions
	public static final String COLUMN_INSTR_WRITE_METADATA = InstructionUtils.STR_WRITE_METADATA;
	public static final String COLUMN_INSTR_WRITE_ACTIONS = InstructionUtils.STR_WRITE_ACTIONS;
	public static final String COLUMN_INSTR_APPLY_ACTIONS = InstructionUtils.STR_APPLY_ACTIONS;
	public static final String COLUMN_INSTR_CLEAR_ACTIONS = InstructionUtils.STR_CLEAR_ACTIONS;
	public static final String COLUMN_INSTR_GOTO_METER = InstructionUtils.STR_GOTO_METER;
	public static final String COLUMN_INSTR_EXPERIMENTER = InstructionUtils.STR_EXPERIMENTER;
	
	IOFSwitchService switchService = (IOFSwitchService) getContext() // 获取switch服务
			.getAttributes().get(IOFSwitchService.class.getCanonicalName());
	
	
	@Post
	public Map<String,String> progressFlow(String json){
		Map<String,String> result = new HashMap<String,String>();
		
		
		
		
		
		return result;
	}
	
	
	
	private int checkFlow(Map<String, Object> rows) {    
		//Declaring & Initializing flags
		int state = 0;
		boolean dl_type = false;
		boolean nw_proto = false;
		boolean nw_layer = false;
		boolean icmp6_type = false;
		boolean icmp6_code = false;
		boolean nd_target = false;
		boolean nd_sll = false;
		boolean nd_tll = false; 
		boolean ip6 = false;
		boolean ip4 = false;

		int eth_type = -1;
		int nw_protocol = -1;
		int icmp_type = -1;
		
		//Determine the dl_type if set
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_DL_TYPE)) {
			if (((String) rows.get(StaticFlowEntryPusher.COLUMN_DL_TYPE)).startsWith("0x")) {
				eth_type = Integer.parseInt(((String) rows.get(StaticFlowEntryPusher.COLUMN_DL_TYPE)).replaceFirst("0x", ""), 16);
				dl_type = true;
			} else {
				eth_type = Integer.parseInt((String) rows.get(StaticFlowEntryPusher.COLUMN_DL_TYPE));
				dl_type = true;
			}
			if (eth_type == 0x86dd) { /* or 34525 */
				ip6 = true;
				dl_type = true;
			} else if (eth_type == 0x800 || /* or 2048 */
					eth_type == 0x806 || /* or 2054 */
					eth_type == 0x8035) { /* or 32821*/
				ip4 = true;
				dl_type = true;
			}	
			//else {
			//	state = 2;    
			//	return state;
			//}
		}
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_NW_DST) ||
				rows.containsKey(StaticFlowEntryPusher.COLUMN_NW_SRC)) {
			nw_layer = true;
			ip4 = true;
		}
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_ICMP_CODE) ||
				rows.containsKey(StaticFlowEntryPusher.COLUMN_ICMP_TYPE) ||
				rows.containsKey(StaticFlowEntryPusher.COLUMN_ARP_DHA) ||
				rows.containsKey(StaticFlowEntryPusher.COLUMN_ARP_SHA) ||
				rows.containsKey(StaticFlowEntryPusher.COLUMN_ARP_SPA) ||
				rows.containsKey(StaticFlowEntryPusher.COLUMN_ARP_DPA) ||
				rows.containsKey(StaticFlowEntryPusher.COLUMN_ARP_OPCODE)) {
			ip4 = true;
		}
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_IPV6_FLOW_LABEL) || 
				rows.containsKey(StaticFlowEntryPusher.COLUMN_NW6_SRC) ||
				rows.containsKey(StaticFlowEntryPusher.COLUMN_NW6_DST)) {
			nw_layer = true;
			ip6 = true;
		}
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_NW_PROTO)) {
			nw_proto = true;
			if (((String) rows.get(StaticFlowEntryPusher.COLUMN_NW_PROTO)).startsWith("0x")) {
				nw_protocol = Integer.parseInt(((String) rows.get(StaticFlowEntryPusher.COLUMN_NW_PROTO)).replaceFirst("0x", ""), 16);
			} else {
				nw_protocol = Integer.parseInt((String) rows.get(StaticFlowEntryPusher.COLUMN_NW_PROTO));
			}
		}
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_ICMP6_CODE)) {
			icmp6_code = true;
			ip6 = true;
		}
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_ICMP6_TYPE)) {
			icmp6_type = true;
			ip6 = true;
			if (((String) rows.get(StaticFlowEntryPusher.COLUMN_ICMP6_TYPE)).startsWith("0x")) {
				icmp_type = Integer.parseInt(((String) rows.get(StaticFlowEntryPusher.COLUMN_ICMP6_TYPE)).replaceFirst("0x", ""), 16);
			} else {
				icmp_type = Integer.parseInt((String) rows.get(StaticFlowEntryPusher.COLUMN_ICMP6_TYPE));
			}
		}
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_ND_SLL)) {
			nd_sll = true;
			ip6 = true;
		}
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_ND_TLL)) {
			nd_tll = true;
			ip6 = true;
		}
		if (rows.containsKey(StaticFlowEntryPusher.COLUMN_ND_TARGET)) {
			nd_target = true;
			ip6 = true;
		}    

		if (nw_layer == true || nw_proto == true) {
			if (dl_type == true) {
				if (!(ip4 == true || ip6 == true)) {
					//invalid dl_type
					state = 2;    
					return state;
				}
			}
			else {
				//dl_type not set
				state = 1;    
				return state;
			}
		}
		if (icmp6_type == true || icmp6_code == true ) {
			if (nw_proto == true) {
				if (nw_protocol != 0x3A) { /* or 58 */
					//invalid nw_proto
					state = 4;    
					return state;
				}
			}
			else {
				//nw_proto not set
				state = 3;    
				return state;
			}
		}

		if (nd_sll == true || nd_tll == true || nd_target == true) {
			if (icmp6_type == true) {
				//icmp_type must be set to 135/136 to set ipv6_nd_target
				if (nd_target == true) {
					if (!(icmp_type == 135 || icmp_type == 136)) { /* or 0x87 / 0x88 */
						//invalid icmp6_type
						state = 6;
						return state;
					}
				}
				//icmp_type must be set to 136 to set ipv6_nd_tll
				else if (nd_tll == true) {
					if (!(icmp_type == 136)) {
						//invalid icmp6_type
						state = 6;
						return state;
					}
				}
				//icmp_type must be set to 135 to set ipv6_nd_sll
				else if (nd_sll == true) {
					if (!(icmp_type == 135)) {
						//invalid icmp6_type
						state = 6;
						return state;
					}
				}
			}
			else {
				//icmp6_type not set
				state = 5;    
				return state;
			}
		}

		int result = checkActions(rows);

		if ((ip4 == true && ip6 == true) || (result == -1) ||
				(result == 1 && ip6 == true) || (result == 2 && ip4 == true)) {
			//ipv4 & ipv6 conflict
			state = 7;    
			return state;
		}

		return state;

	}
	
	
	/**
	 * Validates actions/instructions
	 * 
	 * -1 --> IPv4/IPv6 conflict
	 * 0 --> no IPv4 or IPv6 actions
	 * 1 --> IPv4 only actions
	 * 2 --> IPv6 only actions
	 * 
	 * @param Map containing the fields of the flow
	 * @return state indicating whether a flow is valid or not
	 */
	public static int checkActions(Map<String, Object> entry) {

		boolean ip6 = false;
		boolean ip4 = false;
		String actions = null;

		if (entry.containsKey(StaticFlowEntryPusher.COLUMN_ACTIONS) || 
				entry.containsKey(StaticFlowEntryPusher.COLUMN_INSTR_APPLY_ACTIONS) ||
				entry.containsKey(StaticFlowEntryPusher.COLUMN_INSTR_WRITE_ACTIONS)) {
			if (entry.containsKey(StaticFlowEntryPusher.COLUMN_ACTIONS)) {
				actions = (String) entry.get(StaticFlowEntryPusher.COLUMN_ACTIONS);
			}
			else if (entry.containsKey(StaticFlowEntryPusher.COLUMN_INSTR_APPLY_ACTIONS)) {
				actions = (String) entry.get(StaticFlowEntryPusher.COLUMN_INSTR_APPLY_ACTIONS);
			}
			else if (entry.containsKey(StaticFlowEntryPusher.COLUMN_INSTR_WRITE_ACTIONS)) {
				actions = (String) entry.get(StaticFlowEntryPusher.COLUMN_INSTR_WRITE_ACTIONS);
			}

			if (actions.contains(MatchUtils.STR_ICMPV6_CODE) || actions.contains(MatchUtils.STR_ICMPV6_TYPE) ||
					actions.contains(MatchUtils.STR_IPV6_DST) || actions.contains(MatchUtils.STR_IPV6_SRC) || 
					actions.contains(MatchUtils.STR_IPV6_FLOW_LABEL) || actions.contains(MatchUtils.STR_IPV6_ND_SSL) ||
					actions.contains(MatchUtils.STR_IPV6_ND_TARGET) || actions.contains(MatchUtils.STR_IPV6_ND_TTL)) {
				ip6 = true;
			}
			if (actions.contains(MatchUtils.STR_NW_SRC) || actions.contains(MatchUtils.STR_NW_DST) || 
					actions.contains(MatchUtils.STR_ARP_OPCODE) || actions.contains(MatchUtils.STR_ARP_SHA) || 
					actions.contains(MatchUtils.STR_ARP_DHA) || actions.contains(MatchUtils.STR_ARP_SPA) || 
					actions.contains(MatchUtils.STR_ARP_DPA) || actions.contains(MatchUtils.STR_ICMP_CODE) || 
					actions.contains(MatchUtils.STR_ICMP_TYPE)) {
				ip4 = true;
			}
		}

		if (ip6 == false && ip4 == false) {
			return 0; // no actions involving ipv4 or ipv6
		} else if (ip6 == false && ip4 == true) {
			return 1; //ipv4
		} else if (ip6 == true && ip4 == false) {
			return 2; //ipv6
		} else {
			return -1; // conflict of ipv4 and ipv6 actions
		}
	}

	public static Map<String, Object> jsonToStorageEntry(String fmJson) throws IOException {
		Map<String, Object> entry = new HashMap<String, Object>();
		MappingJsonFactory f = new MappingJsonFactory();
		JsonParser jp;
		
		String tpSrcPort = "NOT_SPECIFIED";
		String tpDstPort = "NOT_SPECIFIED";
		String ipProto = "NOT_SPECIFIED";

		try {
			jp = f.createJsonParser(fmJson);
		} catch (JsonParseException e) {
			throw new IOException(e);
		}

		jp.nextToken();
		if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
			throw new IOException("Expected START_OBJECT");
		}

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME");
			}

			String n = jp.getCurrentName();
			jp.nextToken();

			// Java 7 switch-case on strings automatically checks for (deep) string equality.
			// IMHO, this makes things easier on the eyes than if, else if, else's, and it
			// seems to be more efficient than walking through a long list of if-else-ifs

			// A simplification is to make the column names the same strings as those used to
			// compose the JSON flow entry; keeps all names/keys centralized and reduces liklihood
			// for future string errors.
			switch (n) {
			case StaticFlowEntryPusher.COLUMN_NAME:
				entry.put(StaticFlowEntryPusher.COLUMN_NAME, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_SWITCH:
				entry.put(StaticFlowEntryPusher.COLUMN_SWITCH, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_TABLE_ID:
				entry.put(StaticFlowEntryPusher.COLUMN_TABLE_ID, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ACTIVE:
				entry.put(StaticFlowEntryPusher.COLUMN_ACTIVE, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_IDLE_TIMEOUT:
				entry.put(StaticFlowEntryPusher.COLUMN_IDLE_TIMEOUT, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_HARD_TIMEOUT:
				entry.put(StaticFlowEntryPusher.COLUMN_HARD_TIMEOUT, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_PRIORITY:
				entry.put(StaticFlowEntryPusher.COLUMN_PRIORITY, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_COOKIE: // set manually, or computed from name
				entry.put(StaticFlowEntryPusher.COLUMN_COOKIE, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_IN_PORT:
				entry.put(StaticFlowEntryPusher.COLUMN_IN_PORT, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_DL_SRC:
				entry.put(StaticFlowEntryPusher.COLUMN_DL_SRC, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_DL_DST:
				entry.put(StaticFlowEntryPusher.COLUMN_DL_DST, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_DL_VLAN:
				entry.put(StaticFlowEntryPusher.COLUMN_DL_VLAN, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_DL_VLAN_PCP:
				entry.put(StaticFlowEntryPusher.COLUMN_DL_VLAN_PCP, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_DL_TYPE:
				entry.put(StaticFlowEntryPusher.COLUMN_DL_TYPE, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_NW_TOS: // only valid for OF1.0; all other should specify specifics (ECN and/or DSCP bits)
				entry.put(StaticFlowEntryPusher.COLUMN_NW_TOS, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_NW_ECN:
				entry.put(StaticFlowEntryPusher.COLUMN_NW_ECN, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_NW_DSCP:
				entry.put(StaticFlowEntryPusher.COLUMN_NW_DSCP, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_NW_PROTO:
				entry.put(StaticFlowEntryPusher.COLUMN_NW_PROTO, jp.getText());
				ipProto = jp.getText();
				break;
			case StaticFlowEntryPusher.COLUMN_NW_SRC:
				entry.put(StaticFlowEntryPusher.COLUMN_NW_SRC, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_NW_DST:
				entry.put(StaticFlowEntryPusher.COLUMN_NW_DST, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_SCTP_SRC:
				entry.put(StaticFlowEntryPusher.COLUMN_SCTP_SRC, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_SCTP_DST:
				entry.put(StaticFlowEntryPusher.COLUMN_SCTP_DST, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_UDP_SRC:
				entry.put(StaticFlowEntryPusher.COLUMN_UDP_SRC, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_UDP_DST:
				entry.put(StaticFlowEntryPusher.COLUMN_UDP_DST, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_TCP_SRC:
				entry.put(StaticFlowEntryPusher.COLUMN_TCP_SRC, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_TCP_DST:
				entry.put(StaticFlowEntryPusher.COLUMN_TCP_DST, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_TP_SRC: // support for OF1.0 generic transport ports
				entry.put(StaticFlowEntryPusher.COLUMN_TP_SRC, jp.getText());
				tpSrcPort = jp.getText();
				break;
			case StaticFlowEntryPusher.COLUMN_TP_DST:
				entry.put(StaticFlowEntryPusher.COLUMN_TP_DST, jp.getText());
				tpDstPort = jp.getText();
				break;
			case StaticFlowEntryPusher.COLUMN_ICMP_TYPE:
				entry.put(StaticFlowEntryPusher.COLUMN_ICMP_TYPE, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ICMP_CODE:
				entry.put(StaticFlowEntryPusher.COLUMN_ICMP_CODE, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ARP_OPCODE:
				entry.put(StaticFlowEntryPusher.COLUMN_ARP_OPCODE, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ARP_SHA:
				entry.put(StaticFlowEntryPusher.COLUMN_ARP_SHA, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ARP_DHA:
				entry.put(StaticFlowEntryPusher.COLUMN_ARP_DHA, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ARP_SPA:
				entry.put(StaticFlowEntryPusher.COLUMN_ARP_SPA, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ARP_DPA:
				entry.put(StaticFlowEntryPusher.COLUMN_ARP_DPA, jp.getText());
				break;		
			case StaticFlowEntryPusher.COLUMN_NW6_SRC:				
				entry.put(StaticFlowEntryPusher.COLUMN_NW6_SRC, jp.getText());
				break;	
			case StaticFlowEntryPusher.COLUMN_NW6_DST:				
				entry.put(StaticFlowEntryPusher.COLUMN_NW6_DST, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_IPV6_FLOW_LABEL:								
				entry.put(StaticFlowEntryPusher.COLUMN_IPV6_FLOW_LABEL, jp.getText());
				break;	
			case StaticFlowEntryPusher.COLUMN_ICMP6_TYPE:				
				entry.put(StaticFlowEntryPusher.COLUMN_ICMP6_TYPE, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ICMP6_CODE:						
				entry.put(StaticFlowEntryPusher.COLUMN_ICMP6_CODE, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ND_SLL:				
				entry.put(StaticFlowEntryPusher.COLUMN_ND_SLL, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ND_TLL:			
				entry.put(StaticFlowEntryPusher.COLUMN_ND_TLL, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ND_TARGET:					
				entry.put(StaticFlowEntryPusher.COLUMN_ND_TARGET, jp.getText());
				break;				
			case StaticFlowEntryPusher.COLUMN_MPLS_LABEL:
				entry.put(StaticFlowEntryPusher.COLUMN_MPLS_LABEL, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_MPLS_TC:
				entry.put(StaticFlowEntryPusher.COLUMN_MPLS_TC, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_MPLS_BOS:
				entry.put(StaticFlowEntryPusher.COLUMN_MPLS_BOS, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_METADATA:
				entry.put(StaticFlowEntryPusher.COLUMN_METADATA, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_TUNNEL_ID:
				entry.put(StaticFlowEntryPusher.COLUMN_TUNNEL_ID, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_PBB_ISID: // not supported as match in loxi right now
				entry.put(StaticFlowEntryPusher.COLUMN_PBB_ISID, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_ACTIONS:
				entry.put(StaticFlowEntryPusher.COLUMN_ACTIONS, jp.getText());
				break;
				
			/* 
			 * All OF1.1+ instructions.
			 */
			case StaticFlowEntryPusher.COLUMN_INSTR_APPLY_ACTIONS:
				entry.put(StaticFlowEntryPusher.COLUMN_INSTR_APPLY_ACTIONS, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_INSTR_WRITE_ACTIONS:
				entry.put(StaticFlowEntryPusher.COLUMN_INSTR_WRITE_ACTIONS, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_INSTR_CLEAR_ACTIONS:
				entry.put(StaticFlowEntryPusher.COLUMN_INSTR_CLEAR_ACTIONS, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_INSTR_GOTO_METER:
				entry.put(StaticFlowEntryPusher.COLUMN_INSTR_GOTO_METER, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_INSTR_GOTO_TABLE:
				entry.put(StaticFlowEntryPusher.COLUMN_INSTR_GOTO_TABLE, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_INSTR_WRITE_METADATA:
				entry.put(StaticFlowEntryPusher.COLUMN_INSTR_WRITE_METADATA, jp.getText());
				break;
			case StaticFlowEntryPusher.COLUMN_INSTR_EXPERIMENTER:
				entry.put(StaticFlowEntryPusher.COLUMN_INSTR_EXPERIMENTER, jp.getText());
				break;
			default:
				log.error("Could not decode field from JSON string: {}", n);
				break;
			}  
		} 
		
		// For OF1.0, transport ports are specified using generic tp_src, tp_dst type strings.
		// Once the whole json string has been parsed, find out the IpProto to properly assign the ports.
		// If IpProto not specified, print error, and make sure all TP columns are clear.
		if (ipProto.equalsIgnoreCase("tcp")) {
			if (!tpSrcPort.equals("NOT_SPECIFIED")) {
				entry.remove(StaticFlowEntryPusher.COLUMN_TP_SRC);
				entry.put(StaticFlowEntryPusher.COLUMN_TCP_SRC, tpSrcPort);
			}
			if (!tpDstPort.equals("NOT_SPECIFIED")) {
				entry.remove(StaticFlowEntryPusher.COLUMN_TP_DST);
				entry.put(StaticFlowEntryPusher.COLUMN_TCP_DST, tpDstPort);
			}
		} else if (ipProto.equalsIgnoreCase("udp")) {
			if (!tpSrcPort.equals("NOT_SPECIFIED")) {
				entry.remove(StaticFlowEntryPusher.COLUMN_TP_SRC);
				entry.put(StaticFlowEntryPusher.COLUMN_UDP_SRC, tpSrcPort);
			}
			if (!tpDstPort.equals("NOT_SPECIFIED")) {
				entry.remove(StaticFlowEntryPusher.COLUMN_TP_DST);
				entry.put(StaticFlowEntryPusher.COLUMN_UDP_DST, tpDstPort);
			}
		} else if (ipProto.equalsIgnoreCase("sctp")) {
			if (!tpSrcPort.equals("NOT_SPECIFIED")) {
				entry.remove(StaticFlowEntryPusher.COLUMN_TP_SRC);
				entry.put(StaticFlowEntryPusher.COLUMN_SCTP_SRC, tpSrcPort);
			}
			if (!tpDstPort.equals("NOT_SPECIFIED")) {
				entry.remove(StaticFlowEntryPusher.COLUMN_TP_DST);
				entry.put(StaticFlowEntryPusher.COLUMN_SCTP_DST, tpDstPort);
			}
		} else {
			log.debug("Got IP protocol of '{}' and tp-src of '{}' and tp-dst of '" + tpDstPort + "' via SFP REST API", ipProto, tpSrcPort);
		}

		return entry;
	}   

	void parseRow(Map<String, Object> row, Map<String, Map<String, OFFlowMod>> entries) {
		String switchName = null;
		String entryName = null;

		StringBuffer matchString = new StringBuffer();
		OFFlowMod.Builder fmb = null; 

		if (!row.containsKey(COLUMN_SWITCH) || !row.containsKey(COLUMN_NAME)) {
			log.debug("skipping entry with missing required 'switch' or 'name' entry: {}", row);
			return;
		}
		// most error checking done with ClassCastException
		try {
			// first, snag the required entries, for debugging info
			switchName = (String) row.get(COLUMN_SWITCH);
			entryName = (String) row.get(COLUMN_NAME);
			if (!entries.containsKey(switchName)) {
				entries.put(switchName, new HashMap<String, OFFlowMod>());
			}

			// get the correct builder for the OF version supported by the switch
			fmb = OFFactories.getFactory(switchService.getSwitch(DatapathId.of(switchName)).getOFFactory().getVersion()).buildFlowModify();

			StaticFlowEntries.initDefaultFlowMod(fmb, entryName);

			for (String key : row.keySet()) {
				if (row.get(key) == null) {
					continue;
				}

				if (key.equals(COLUMN_SWITCH) || key.equals(COLUMN_NAME) || key.equals("id")) {
					continue; // already handled
				}

				if (key.equals(COLUMN_ACTIVE)) {
					if  (!Boolean.valueOf((String) row.get(COLUMN_ACTIVE))) {
						log.debug("skipping inactive entry {} for switch {}", entryName, switchName);
						entries.get(switchName).put(entryName, null);  // mark this an inactive
						return;
					}
				} else if (key.equals(COLUMN_HARD_TIMEOUT)) {
					fmb.setHardTimeout(Integer.valueOf((String) row.get(COLUMN_HARD_TIMEOUT)));
				} else if (key.equals(COLUMN_IDLE_TIMEOUT)) {
					fmb.setIdleTimeout(Integer.valueOf((String) row.get(COLUMN_IDLE_TIMEOUT)));
				} else if (key.equals(COLUMN_TABLE_ID)) {
					if (fmb.getVersion().compareTo(OFVersion.OF_10) > 0) {
						fmb.setTableId(TableId.of(Integer.parseInt((String) row.get(key)))); // support multiple flow tables for OF1.1+
					} else {
						log.error("Table not supported in OpenFlow 1.0");
					}
				} else if (key.equals(COLUMN_ACTIONS)) {
					ActionUtils.fromString(fmb, (String) row.get(COLUMN_ACTIONS), log);
				} else if (key.equals(COLUMN_COOKIE)) {
					fmb.setCookie(StaticFlowEntries.computeEntryCookie(Integer.valueOf((String) row.get(COLUMN_COOKIE)), entryName));
				} else if (key.equals(COLUMN_PRIORITY)) {
					fmb.setPriority(U16.t(Integer.valueOf((String) row.get(COLUMN_PRIORITY))));
				} else if (key.equals(COLUMN_INSTR_APPLY_ACTIONS)) {
					InstructionUtils.applyActionsFromString(fmb, (String) row.get(COLUMN_INSTR_APPLY_ACTIONS), log);
				} else if (key.equals(COLUMN_INSTR_CLEAR_ACTIONS)) {
					InstructionUtils.clearActionsFromString(fmb, (String) row.get(COLUMN_INSTR_CLEAR_ACTIONS), log);
				} else if (key.equals(COLUMN_INSTR_EXPERIMENTER)) {
					InstructionUtils.experimenterFromString(fmb, (String) row.get(COLUMN_INSTR_EXPERIMENTER), log);
				} else if (key.equals(COLUMN_INSTR_GOTO_METER)) {
					InstructionUtils.meterFromString(fmb, (String) row.get(COLUMN_INSTR_GOTO_METER), log);
				} else if (key.equals(COLUMN_INSTR_GOTO_TABLE)) {
					InstructionUtils.gotoTableFromString(fmb, (String) row.get(COLUMN_INSTR_GOTO_TABLE), log);
				} else if (key.equals(COLUMN_INSTR_WRITE_ACTIONS)) {
					InstructionUtils.writeActionsFromString(fmb, (String) row.get(COLUMN_INSTR_WRITE_ACTIONS), log);
				} else if (key.equals(COLUMN_INSTR_WRITE_METADATA)) {
					InstructionUtils.writeMetadataFromString(fmb, (String) row.get(COLUMN_INSTR_WRITE_METADATA), log);
				} else { // the rest of the keys are for Match().fromString()
					if (matchString.length() > 0) {
						matchString.append(",");
					}
					matchString.append(key + "=" + row.get(key).toString());
				}
			}
		} catch (ClassCastException e) {
			if (entryName != null && switchName != null) {
				log.warn("Skipping entry {} on switch {} with bad data : " + e.getMessage(), entryName, switchName);
			} else {
				log.warn("Skipping entry with bad data: {} :: {} ", e.getMessage(), e.getStackTrace());
			}
		}

		String match = matchString.toString();

		try {
			fmb.setMatch(MatchUtils.fromString(match, fmb.getVersion()));
		} catch (IllegalArgumentException e) {
			log.error(e.toString());
			log.error("Ignoring flow entry {} on switch {} with illegal OFMatch() key: " + match, entryName, switchName);
			return;
		} catch (Exception e) {
			log.error("OF version incompatible for the match: " + match);
			e.printStackTrace();
			return;
		}

		entries.get(switchName).put(entryName, fmb.build()); // add the FlowMod message to the table
	}

}
