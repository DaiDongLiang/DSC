package net.dsc.cluster.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.dsc.cluster.IClusterService;
import net.dsc.hazelcast.IHazelcastService;
import net.dsc.hazelcast.message.FlowMessage;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;
import net.floodlightcontroller.storage.IStorageSourceService;
import net.floodlightcontroller.util.InstructionUtils;
import net.floodlightcontroller.util.MatchUtils;

import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.google.common.collect.Multimap;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

public class FlowEntryPusherResource extends ServerResource {

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
	public static final String COLUMN_NW_SRC = MatchUtils.STR_NW_SRC; // includes
																		// CIDR-style
																		// netmask,
																		// e.g.
																		// "128.8.128.0/24"
	public static final String COLUMN_NW_DST = MatchUtils.STR_NW_DST;

	public static final String COLUMN_SCTP_SRC = MatchUtils.STR_SCTP_SRC;
	public static final String COLUMN_SCTP_DST = MatchUtils.STR_SCTP_DST;
	public static final String COLUMN_UDP_SRC = MatchUtils.STR_UDP_SRC;
	public static final String COLUMN_UDP_DST = MatchUtils.STR_UDP_DST;
	public static final String COLUMN_TCP_SRC = MatchUtils.STR_TCP_SRC;
	public static final String COLUMN_TCP_DST = MatchUtils.STR_TCP_DST;
	public static final String COLUMN_TP_SRC = MatchUtils.STR_TP_SRC; // support
																		
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

	public static final String COLUMN_INSTR_GOTO_TABLE = InstructionUtils.STR_GOTO_TABLE; // instructions
																							
	public static final String COLUMN_INSTR_WRITE_METADATA = InstructionUtils.STR_WRITE_METADATA;
	public static final String COLUMN_INSTR_WRITE_ACTIONS = InstructionUtils.STR_WRITE_ACTIONS;
	public static final String COLUMN_INSTR_APPLY_ACTIONS = InstructionUtils.STR_APPLY_ACTIONS;
	public static final String COLUMN_INSTR_CLEAR_ACTIONS = InstructionUtils.STR_CLEAR_ACTIONS;
	public static final String COLUMN_INSTR_GOTO_METER = InstructionUtils.STR_GOTO_METER;
	public static final String COLUMN_INSTR_EXPERIMENTER = InstructionUtils.STR_EXPERIMENTER;

	

	@Post
	public Map<String, String> progressFlow(String json) {
		

		IHazelcastService hazelcastService = (IHazelcastService) getContext() // 获取switch服务
				.getAttributes().get(IHazelcastService.class.getCanonicalName());
		IClusterService clusterService = (IClusterService) getContext()
				.getAttributes().get(IClusterService.class.getCanonicalName());
		
		IStorageSourceService storageSourceService = (IStorageSourceService)getContext()
				.getAttributes().get(IStorageSourceService.class.getCanonicalName());
		IMap<String, String> masterMap = clusterService.getMasterMap();

		String localControllerId = hazelcastService.getLocalMember().getUuid().toString();
		String switchId = "";
		Map<String, String> result = new HashMap<String, String>();
		String status = "";
		result.put("status",status);
		
		try {
			switchId = getSwitchId(json);
			System.out.println(masterMap.containsKey(switchId));
			if (masterMap.containsKey(switchId)) {// 如果交换机有主
				String controllerId = masterMap.get(switchId);
				System.out.println(controllerId+"==="+localControllerId);
				
				if (controllerId.equals(localControllerId)) {// 如果请求的交换机的主是本地控制器
					Map<String, Object> rowValues = FlowEntryPushUtil
							.jsonToStorageEntry(json);
					int state = FlowEntryPushUtil.checkFlow(rowValues);
					status = checkStatus(state);
					if (status.equals("Entry pushed")) {
						
						storageSourceService.insertRowAsync(StaticFlowEntryPusher.TABLE_NAME, rowValues);
						result.put("status", "流表已经下发");
					
					} else {
						result.put("status", status);
						
					}

				} else {// 如果请求的交换机不是本地控制器
					hazelcastService.publishFlowMessage(new FlowMessage(json), controllerId);
					result.put("status","流表已下发至请求控制器");
				

				}

			} else {
				result.put("status", "交换机无master或不存在");
				
			}

		} catch (IOException e) {
		

			e.printStackTrace();
			result.put("status", "发生未知错误");
			

		}
		
		return result;
	}

	

	private String getSwitchId(String fmJson) throws IOException {
		System.out.println(fmJson);
		MappingJsonFactory f = new MappingJsonFactory();
		JsonParser jp;
		String requestSwitch = "";
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

			switch (n) {
			case StaticFlowEntryPusher.COLUMN_SWITCH:
				requestSwitch = jp.getText();
				System.out.println(requestSwitch);
				return requestSwitch;

			}
		}
		return requestSwitch;

	}

	private String checkStatus(int state) {
		String status = "";
		if (state == 1) {
			status = "Warning! Must specify eth_type of IPv4/IPv6 to "
					+ "match on IPv4/IPv6 fields! The flow has been discarded.";
			log.error(status);
		} else if (state == 2) {
			status = "Warning! eth_type not recognized! The flow has been discarded.";
			log.error(status);
		} else if (state == 3) {
			status = "Warning! Must specify ip_proto to match! The flow has been discarded.";
			log.error(status);
		} else if (state == 4) {
			status = "Warning! ip_proto invalid! The flow has been discarded.";
			log.error(status);
		} else if (state == 5) {
			status = "Warning! Must specify icmp6_type to match! The flow has been discarded.";
			log.error(status);
		} else if (state == 6) {
			status = "Warning! icmp6_type invalid! The flow has been discarded.";
			log.error(status);
		} else if (state == 7) {
			status = "Warning! IPv4 & IPv6 fields cannot be specified in the same flow! The flow has been discarded.";
			log.error(status);
		} else if (state == 0) {
			status = "Entry pushed";
		} else {
			status = "发生未知错误";
		}
		return status;
	}
	
	
	

}
