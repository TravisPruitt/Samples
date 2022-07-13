package com.disney.xband.xbrms.client.action;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.HealthItem;
import com.disney.xband.xbrms.common.model.HealthItemDto;
import com.disney.xband.xbrms.common.model.JmsListenerDto;

public class JmsListenerAction extends BaseAction {

    private HealthItem jmslistener;
    private Long id;
    private int tabSelected;

    private static Logger logger = Logger.getLogger(JmsListenerAction.class);

    @Override
    public String execute() throws Exception {

        if (id == null)
            this.addActionError(this.getText("jmslistener.id.missing"));

        try {
        	initJmsListenerHealthItem();
        }
        catch (Exception ex) {
            this.addActionError(this.getText("jmslistener.not.found", new String[]{id.toString()}));
        }

        return super.execute();
    }
    
    private void initJmsListenerHealthItem()
    {
    	HealthItemDto item = null;

        try {
            item = XbrmsUtils.getRestCaller().getHealthItemById("" + id);
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
        }

        if (item == null || !(item instanceof JmsListenerDto)) {
            this.addActionMessage(this.getText("jmslistener.not.found", new String[]{id.toString()}));
            return;
        }

        jmslistener = new HealthItem(item);
    }

    public HealthItem getJmslistener() {
        return jmslistener;
    }

    public void setJmslistener(HealthItem jmslistener) {
        this.jmslistener = jmslistener;
    }

    public int getTabSelected() {
        return tabSelected;
    }

    public void setTabSelected(int tabSelected) {
        this.tabSelected = tabSelected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
