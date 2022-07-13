<link rel="stylesheet" type="text/css" href="css/form.css" />
<link rel="stylesheet" type="text/css" href="css/home.css" />

<script type="text/javascript">
function populateTable(id, ip, venue, hostname, lastDiscovery, nextDiscovery, alive){

	var venueId = "inv_venue_" + id;
	var hostId = "inv_hostname_" + id;
	var ipId = "inv_ip_" + id;
	var lastDiscId = "inv_lastDiscovery_" + id;
	var urlId = "inv_url_" + id;
	var healthId = "inv_health_" + id;
	
	$("tr#inv_tr_" + id).children("td").each(function(i){
		tdId = $(this).attr("id");
		if (tdId === venueId) {
			$(this).html(venue);
		} else if (tdId === hostId){
			$(this).html(hostname);
		} else if (tdId === ipId){
			$(this).html(ip);
		} else if (tdId === lastDiscId){
			$(this).html(lastDiscovery);
		} else if (tdId === urlId){
			url = "<a href='http://" + ip + ":8080/UI' target='_new'>http://" + ip + ":8080/UI</a>";
			$(this).html(url);
		} else if (tdId === healthId){
			if (alive){
				$(this).html("<a href='http://" + ip + ":8080/UI' target='_new'><img src='images/green-light.png'/></a>");
			} else {
				$(this).html("<a href='http://" + ip + ":8080/UI' target='_new'><img src='images/red-light.png'/></a>");
			}
		}
	});
}
</script>