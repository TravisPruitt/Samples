package com.disney.xband.common.lib.audit.model;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 7/1/13
 * Time: 12:31 PM
 */
public enum DbField {
    ID,
    AID,
    TYPE,
    CATEGORY,
    APPCLASS,
    APPID,
    HOST,
    VHOST,
    UID,
    SID,
    DESCRIPTION,
    RID,
    RDATA,
    DATETIME,
    DATETIMEMILLIS,
    SOURCETIMEZONE,
    COLLECTORHOST,
    CLIENT;

    private static String[] template;

    static {
        template = new String[DbField.CLIENT.ordinal() + 1];

        for(int i = 0; i < template.length; ++i) {
            template[i] = "%" + DbField.values()[i];
        }
    }

    @Override
    public String toString() {
        switch(this) {
            case ID:
                return "id";

            case AID:
                return "aggregationId";

            case TYPE:
                return "eventType";

            case CATEGORY:
                return "eventCategory";

            case APPCLASS:
                return "applicationClass";

            case APPID:
                return "applicationId";

            case HOST:
                return "sourceAddress";

            case VHOST:
                return "sourceVirtualAddress";

            case UID:
                return "userId";

            case SID:
                return "userSessionId";

            case DESCRIPTION:
                return "description";

            case RID:
                return "resourceId";

            case RDATA:
                return "resourceData";

            case DATETIME:
                return "dateTime";

            case DATETIMEMILLIS:
                return "dateTimeMillis";

            case SOURCETIMEZONE:
                return "sourceTimeZone";

            case COLLECTORHOST:
                return "collectedFromXbrmsAt";

            case CLIENT:
                return "clientAddress";
        }

        throw new AssertionError("Unknown DB field name");
    }

    public String getTemplateVar() {
        return DbField.template[this.ordinal()];
    }

    public static String[] getTemplateVars() {
        return DbField.template;
    }
}
