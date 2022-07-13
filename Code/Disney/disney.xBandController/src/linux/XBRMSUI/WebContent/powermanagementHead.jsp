<%@ taglib prefix="s" uri="/struts-tags" %>

<link rel="stylesheet" type="text/css" href="css/home.css" />
<link type="text/css" href="css/fg.menu.css" rel="Stylesheet" />
<script type="text/javascript" src="script/fg.menu.js"></script>

<script type="text/javascript">

function overrideXbrcSchedule(element, xbrcId, hours) {
    if (xbrcId !== undefined) {
        document.forms.idXbrcOverrideScheduleForm.xbrcId.value = xbrcId;
        document.forms.idXbrcOverrideScheduleForm.hours.value = hours;
        dojo.event.topic.publish('handleOverrideSchedule');
    }
}

$(document).ready(function(){
    <s:iterator var="xbrc" value="inventory" status="xbrcStat">
    $('#menu-${xbrc.id}').menu({
        content: $('#content-${xbrc.id}').html(),
        maxHeight: 180
    });
    </s:iterator>
});
</script>
