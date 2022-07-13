package com.disney.xband.xbrms.client.model;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.XbrcDto;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/29/13
 * Time: 12:46 PM
 */
public class XbrcDpo extends XbrcDto implements IPresentationObject {
    private XbrcDto dto;
    private String displayName;

    public XbrcDpo(final XbrcDto dto) {
        super();
        this.id = dto.getId();
        this.ip = dto.getIp();
        this.port = dto.getPort();
        this.hostname = dto.getHostname();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.active = dto.getActive();
        this.haStatus = dto.getHaStatus();
        this.vip = dto.getVip();
        this.vport = dto.getVport();
        this.facilityId = dto.getFacilityId();
        this.lastDiscovery = dto.getLastDiscovery();
        this.nextDiscovery = dto.getNextDiscovery();
    }
    
    public String getDipDisplayName()
    {
    	if (!XbrmsUtils.isEmpty(hostname)) {
            return name + " @ " + hostname;
        }
        else {
            if (!XbrmsUtils.isEmpty(ip)) {
                return name + " @ " + ip;
            }
            else {
                return name;
            }
        }
    }
    
    public String getVipDisplayName()
    {
    	if (vip != null && !vip.trim().isEmpty() && !vip.startsWith("#")) {
        	return name + " @ " + vip;
        }
    	else if (!XbrmsUtils.isEmpty(hostname)) {
            return name + " @ " + hostname;
        }
        else {
            if (!XbrmsUtils.isEmpty(ip)) {
                return name + " @ " + ip;
            }
            else {
                return name;
            }
        }
    }
}
