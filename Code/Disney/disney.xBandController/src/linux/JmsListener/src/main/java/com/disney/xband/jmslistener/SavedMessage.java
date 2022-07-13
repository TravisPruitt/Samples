package com.disney.xband.jmslistener;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/4/13
 * Time: 11:31 AM
 */
@XmlRootElement(name = "jmsMessage")
public class SavedMessage {
    private Map<String, Object> props = new HashMap<String, Object>();
    private String text;
    private transient TextMessage message;

    public SavedMessage() {
        this.props = new HashMap<String, Object>();
    }

    public SavedMessage(TextMessage message) {
        this.props = new HashMap<String, Object>();

        try {
            this.message = message;
            Enumeration names = message.getPropertyNames();

            while(names.hasMoreElements()) {
                String name = (String) names.nextElement();
                Object o = message.getObjectProperty(name);
                props.put(name, o);
            }
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public String getText() throws JMSException {
        if(message == null) {
            return text;
        }

        return message.getText();
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean propertyExists(String s) throws JMSException {
        if(props == null) {
            return false;
        }

        return props.containsKey(s);
    }

    public String getStringProperty(String s) {
        if(props == null) {
            return null;
        }

        return (String) props.get(s);
    }
}
