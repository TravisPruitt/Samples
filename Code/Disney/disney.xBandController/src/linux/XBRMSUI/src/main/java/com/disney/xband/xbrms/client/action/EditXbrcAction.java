package com.disney.xband.xbrms.client.action;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;

import com.disney.xband.xbrms.client.model.XbrcUiKeyDpo;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.common.lib.ConfigInfo;
import com.disney.xband.common.lib.ConfigInfo.ClazzAndNameComparator;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.model.XbrcModel;
import com.opensymphony.xwork2.Preparable;
import com.disney.xband.xbrms.common.*;
import com.disney.xband.xbrms.common.model.*;

public class EditXbrcAction extends BaseAction implements Preparable {
    private Logger logger = Logger.getLogger(EditXbrcAction.class);

    private Collection<XbrcDto> xbrcs;
    private String updateXbrcs;
    private Map<String, Collection<XbrcConfig>> configMap;
    private Map<String, Map<XbrcUiKeyDpo, Collection<ConfigInfo>>> tabs;
    private Collection<String> tabHeaders;
    private Map<String, Map<String, ConfigInfo>> common;
    private Map<String, String> userInput;

    // zero based index that drives which tab will be selected on load
    private int tabSelected = 0;
    private String model;
    private String group;
    private String group2;

    @Override
    public void prepare() throws Exception 
    {
        tabHeaders = new LinkedList<String>();
        userInput = new HashMap<String, String>();
    }

    @Override
    public String execute() throws Exception {

        boolean success = init();

        if (!success) {
            return INPUT;
        }

        return SUCCESS;
    }

    public String updateXbrcConfig() throws Exception {

        if (model == null || model.isEmpty()) {
            init();
            return INPUT;
        }

        final Map request = ServletActionContext.getRequest().getParameterMap();
        final XbrcModel modelType = XbrcModel.valueOf(model);

        if (modelType == null) {
            init();
            return INPUT;
        }

        final String updateXbrcsKey = "updateXbrcs_" + modelType.name();

        if (request.containsKey(updateXbrcsKey)) {
            updateXbrcs = ((String[]) request.get(updateXbrcsKey))[0];
        }

        // per model update
        boolean failedToPushSomeConfigurations = false;

        if (updateXbrcs != null && updateXbrcs.length() > 0) {
            // get all attraction xbrcs
            try {
                xbrcs = XbrmsUtils.getRestCaller().getFacilitiesByModelType(model).getFacility();
            }
            catch(Exception e) {
                final String error = "Edit xBRC action failed. ";
                this.addActionMessage(error);
                logger.error(error + e.getMessage());
                return INPUT;
            }

            // organize by venue name for fast lookups
            final Map<String, XbrcDto> xbrcMap = new HashMap<String, XbrcDto>();

            for (XbrcDto xbrcDto : xbrcs) {
                xbrcMap.put((new XbrcUiKeyDpo(xbrcDto.getIp(), xbrcDto.getPort().toString(), xbrcDto.getName(), xbrcDto.getHaStatus())).getId(), xbrcDto);
            }

            // update status info on checked xbrcs
            final String[] selected = updateXbrcs.split(",");
            XbrcConfig xbrcConfig = null;

            XbrcDto current = null;
            for (String s : selected) {

                current = xbrcMap.get(s);
                if (current == null) {
                    continue;
                }

                // slave's configuration may only be updated by its master
                if (current.getHaStatus() != null && current.getHaStatus().equals(HAStatusEnum.slave)) {
                    continue;
                }

                // the envelope
                xbrcConfig = new XbrcConfig();
                xbrcConfig.setName(current.getName());
                xbrcConfig.setIp(current.getIp());
                xbrcConfig.setPort("" + current.getPort());
                xbrcConfig.setId(current.getFacilityId());

                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                xbrcConfig.setTime(sdf.format(dt));

                xbrcConfig.setConfiguration(new LinkedList<ConfigInfo>());

                // the payload
                updateChanged(model, ServletActionContext.getRequest().getParameterMap(), xbrcConfig.getConfiguration());

                // update configuration
                boolean success;

                try {
                    XbrmsUtils.getRestCaller().updateXbrcConfig(xbrcConfig, current.getId().toString());
                    success = true;
                }
                catch(Exception e) {
                    success = false;
                    final String error = "Edit xBRC action failed.";
                    // this.addActionMessage(error);
                    logger.error(error + e.getMessage());
                }

                if (!success) {
                    this.addActionMessage("Failed to push configuration changes to " + current.getName() + " at " + current.getIp());
                    failedToPushSomeConfigurations = true;
                }
            }
        }

        boolean success = init();
        if (!success || failedToPushSomeConfigurations) {
            return INPUT;
        }

        return SUCCESS;
    }

    public void updateChanged(String model, Map<String, String[]> source, Collection<ConfigInfo> destination) {

        String configClass = null;
        String fieldName = null;
        String fieldValue = null;
        String fieldUniqueId = null;
        String prefix = "input_" + model + "_";

        String nullOut = this.getText("null.out");
        String doNotChange = this.getText("do.not.change");

        for (String key : source.keySet()) {

            if (key.indexOf(prefix) < 0) {
                continue;    // not a form input
            }

            fieldUniqueId = key.substring(prefix.length());

            int iDash = fieldUniqueId.indexOf('-');
            configClass = fieldUniqueId.substring(0, iDash).replace('_', '.');
            fieldName = fieldUniqueId.substring(iDash + 1);

            ConfigInfo info = new ConfigInfo();
            info.setName(fieldName);
            info.setConfigClass(configClass);

            fieldValue = source.get(key)[0];

            if (fieldValue.equals(doNotChange) || fieldValue.equals("-1")) {
                continue;
            }

            if (fieldValue.equals(nullOut))
                /*
                     * Erase the value of the current configuration in the database.
                     * Boolean type values will be set to false.
                     * All other types values will be set to an empty String.
                     */
            {
                fieldValue = null;
            }

            info.setValue(fieldValue);

            destination.add(info);
        }
    }

    public String xbrcCheckChange() throws Exception {
    	
        boolean success = init();
        if (!success) {
            return INPUT;
        }

        if (model == null || model.isEmpty()) {
            return INPUT;
        }

        if (updateXbrcs != null && updateXbrcs.length() > 0) {

            String[] selected = updateXbrcs.split(",");
            String xbrcId = null;
            XbrcUiKeyDpo xbrcUiKey = null;

            // save user's input and organize it by ConfigInfo variable names for quick lookup
            Map<String, String[]> request = ServletActionContext.getRequest().getParameterMap();

            String fieldName = null;
            String fieldValue = null;
            String fieldUniqueId = null;
            String prefix = "input_" + model + "_";

            for (String key : request.keySet()) {

                if (key.indexOf(prefix) < 0) {
                    continue;    // not a form input
                }

                fieldUniqueId = key.substring(prefix.length());

                int iDash = fieldUniqueId.indexOf('-');
                fieldName = fieldUniqueId.substring(iDash + 1);
                fieldValue = request.get(key)[0];

                userInput.put(fieldName, fieldValue);
            }

            // collect all info common to all the selected xbrcs
            for (int i = 0; i < selected.length; i++) {

                // xbrc name
                xbrcId = selected[i];

                xbrcUiKey = new XbrcUiKeyDpo(xbrcId);

                if (i == 0) {
                    populateWithCommonValues(model, tabs.get(model).get(xbrcUiKey), common.get(model), true);
                }
                else {
                    populateWithCommonValues(model, tabs.get(model).get(xbrcUiKey), common.get(model), false);
                }
            }
        }
        else {
            if (common == null || common.get(model) == null)
            // must have lost connection to all xBRCs some time during this ajax call
            {
                return INPUT;
            }

            Map<String, ConfigInfo> cmn = common.get(model);
            for (String infoName : cmn.keySet()) {
                cmn.put(infoName, null);
            }
        }

        return SUCCESS;
    }

    private void populateWithCommonValues(String model, Collection<ConfigInfo> source, Map<String, ConfigInfo> destination, boolean firstPass) {

        if (source == null) {
            return;
        }

        if (destination == null) {
            destination = new LinkedHashMap<String, ConfigInfo>();
        }

        // figure out the common values
        if (firstPass) {
            // only one xbrc selected, so its values become the common values
            destination = new LinkedHashMap<String, ConfigInfo>();
            for (ConfigInfo info : source) {

                if (userInput.containsKey(info.getUniqueId()) && !userInput.get(info.getUniqueId()).trim().isEmpty()) {
                    // unless the user typed in some values before selecting xBRC(s) to update
                    info.setValue(userInput.get(info.getUniqueId()));
                }

                destination.put(info.getUniqueId(), info);
            }

            common.put(model, destination);

            return;
        }

        // multiple xBRCs selected
        ConfigInfo destInfo = null;
        for (ConfigInfo info : source) {
            if (destination.containsKey(info.getUniqueId())) {
                destInfo = destination.get(info.getUniqueId());

                if (!info.equals(destInfo) && !userInput.containsKey(info.getUniqueId())) {
                    destination.remove(info.getUniqueId());
                }
            }
        }
    }

    private boolean init() throws Exception {
        // Grab all xbrcs we know of
        final Collection<HealthItemDto> allXbrcDtos = XbrmsUtils.getRestCaller().getHealthItemsInventoryForType("XbrcDto").getHealthItem();

        // no xbrcs listed in xbrms.XbrcDto table means that we are not getting discovery messages
        if (allXbrcDtos == null || allXbrcDtos.size() == 0) {
            addActionMessage("No xBRCs found. Check your JMS broker configuration.");
            return false;
        }

        // weed out the dead xBRCs
        xbrcs = new LinkedList<XbrcDto>();
        for (HealthItemDto xbrcDto : allXbrcDtos) {
            if (xbrcDto.isAlive()) {
                xbrcs.add((XbrcDto) xbrcDto);
            }
        }

        // GET their current status and divide it into groups for display
        final List<String> xbrcsStr = new LinkedList<String>();

        for(XbrcDto x : xbrcs) {
            xbrcsStr.add(x.getId().toString());
        }

        Map<String, Collection<XbrcConfig>> newConfigMap = null;

        if (!xbrcs.isEmpty()) // fix for bug 6164
        {
	        try {
	            final XbrcIdListDto xbrcsList = new XbrcIdListDto();
	            xbrcsList.setXbrcId(xbrcsStr);
	            final Map<String, XbrcConfigListDto> props = XbrmsUtils.getRestCaller().getXbrcProperties(xbrcsList).getMap();
	
	            if((props != null) && (props.size() != 0)) {
	                newConfigMap = new HashMap<String, Collection<XbrcConfig>>();
	
	                for(String key : props.keySet()) {
	                    if(props.get(key).getXbrcConfiguration() != null) {
	                        newConfigMap.put(key, props.get(key).getXbrcConfiguration());
	                    }
	                }
	            }
	        }
	        catch(Exception e) {
	            final String error = "Failed to retrieve xBRCs properties.";
	            this.addActionMessage(error);
	            logger.error(error + e.getMessage());
	            throw new RuntimeException(e.getMessage());
	        }
        }

        configMap = newConfigMap;

        // have any xbrcs died while we were asking for their properties?
        StringBuffer inaccessibleXbrcs = null;
        if (configMap == null || configMap.size() == 0 || configMap.size() != xbrcs.size()) {
            if (configMap == null || configMap.size() == 0) {
                // they are all inaccessible at the moment
                for (HealthItemDto xbrcDto : allXbrcDtos) {
                    if (inaccessibleXbrcs == null) {
                        inaccessibleXbrcs = new StringBuffer();
                    }
                    else {
                        inaccessibleXbrcs.append(", ");
                    }

                    inaccessibleXbrcs.append(xbrcDto.getName()).append(" (").append(xbrcDto.getHostname()).append(")");
                }
            }
            else {
                // check which ones died
                for (HealthItemDto xbrcDto : allXbrcDtos) {
                    if (!xbrcDto.isAlive()) {
                        if (inaccessibleXbrcs == null) {
                            inaccessibleXbrcs = new StringBuffer();
                        }
                        else {
                            inaccessibleXbrcs.append(", ");
                        }

                        inaccessibleXbrcs.append(xbrcDto.getName()).append(" (").append(xbrcDto.getHostname()).append(")");
                    }
                }
            }
        }

        if (inaccessibleXbrcs != null) {
            addActionMessage("Failed to connect to the following xBRCs: " + inaccessibleXbrcs.toString());
        }

        if (configMap == null)
        	return true;
        
        // organize the status info by xbrc model, so that we can display each model in its own tab
        tabs = new LinkedHashMap<String, Map<XbrcUiKeyDpo, Collection<ConfigInfo>>>();
        Collection<XbrcConfig> configColl = null;
        XbrcUiKeyDpo xbrcUiKey = null;
        ClazzAndNameComparator comparator = new ConfigInfo().new ClazzAndNameComparator();
        for (String model : configMap.keySet()) 
        {
        	if (XbrmsUtils.isEmpty(this.model))
        		this.model = model;
        	
            //get tabs ready - model per tab
            tabs.put(model, new LinkedHashMap<XbrcUiKeyDpo, Collection<ConfigInfo>>());
            tabHeaders.add(model);

            configColl = configMap.get(model);
            Matcher m = null;
            for (XbrcConfig xbrcConfig : configColl) {

                // TODO remove this safeguard against xbrcs running an old version
                if (xbrcConfig.getIp() == null || xbrcConfig.getIp().trim().isEmpty() ||
                        xbrcConfig.getPort() == null || xbrcConfig.getPort().trim().isEmpty())
                {
                    continue;
                }

                xbrcUiKey = new XbrcUiKeyDpo(xbrcConfig.getIp(), xbrcConfig.getPort(), xbrcConfig.getName(), xbrcConfig.getHaStatus());

                Collections.sort(xbrcConfig.getConfiguration(), comparator);

                tabs.get(model).put(xbrcUiKey, xbrcConfig.getConfiguration());
            }
        }

        // data common to all selected xbrcs - one sample per model
        common = new LinkedHashMap<String, Map<String, ConfigInfo>>();
        Map<String, ConfigInfo> commonValues = null;
        XbrcUiKeyDpo firstXbrc = null;
        Set<XbrcUiKeyDpo> fields = null;
        for (String model : configMap.keySet()) {

            fields = tabs.get(model).keySet();
            if (fields.size() == 0) {
                continue;    // info on xbrc fields not available for this model
            }

            // sample xbrc
            firstXbrc = fields.iterator().next();

            commonValues = new LinkedHashMap<String, ConfigInfo>();
            for (ConfigInfo info : tabs.get(model).get(firstXbrc)) {
                commonValues.put(info.getUniqueId(), info);
            }

            common.put(model, commonValues);
        }

        return true;
    }

    public int getTabSelected() {
        return tabSelected;
    }

    public void setTabSelected(int tabSelected) {
        this.tabSelected = tabSelected;
    }

    /**
     * @return the tabs
     */
    public Map<String, Map<XbrcUiKeyDpo, Collection<ConfigInfo>>> getTabs() {
        return tabs;
    }

    public Collection<String> getTabHeaders() {
        return tabHeaders;
    }

    /**
     * @return the updateXbrcs
     */
    public String getUpdateXbrcs() {
        return updateXbrcs;
    }

    /**
     * @param updateXbrcs the updateXbrcs to set
     */
    public void setUpdateXbrcs(String updateXbrcs) {
        this.updateXbrcs = updateXbrcs;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Map<String, Map<String, ConfigInfo>> getCommon() {
        if (common != null && common.size() == 0) {
            common = null;    //doing a null check in jsps
        }

        return common;
    }

    /**
     * @return the userInput
     */
    public Map<String, String> getUserInput() {
        return userInput;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup2() {
        return group2;
    }

    public void setGroup2(String group2) {
        this.group2 = group2;
    }
}
