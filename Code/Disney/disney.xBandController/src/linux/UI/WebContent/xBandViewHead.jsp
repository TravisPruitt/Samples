<link rel="stylesheet" type="text/css" href="css/form.css" />
<link type="text/css" href="script/jquery-ui-1.8.16.custom.darkblue/css/custom-theme/jquery-ui-1.8.16.custom.css" rel="Stylesheet" />
<link rel="stylesheet" type="text/css" href="css/xBandView.css" />

<script type="text/javascript" src="script/jquery-ui-1.8.16.custom.darkblue/js/jquery-ui-1.8.16.custom.min.js"></script>

<script type="text/javascript">

function myAlert(value){
	alert(value);
}

$(document).ready(function(){
	showProgress();
	
	$(".accordion .ui-accordion-header").click(function(){
		//open/close the location tab on click
		$(this).next().toggle();
		//toggle the location tab's css state between active and non-active
		$(this).toggleClass("ui-state-active");
		
		//toogle the triangle icon used based on css active state
		var icon = $(this).children("span.ui-icon");
		if ($(this).is(".ui-state-active")){
			$(this).removeClass("shadow ui-corner-all");
			$(this).addClass("lightHeader ui-corner-top");
			icon.removeClass("ui-icon-triangle-1-e");
			icon.addClass("ui-icon-triangle-1-s");
		} else {
			$(this).addClass("shadow ui-corner-all");
			$(this).removeClass("lightHeader ui-corner-top");
			icon.removeClass("ui-icon-triangle-1-s");
			icon.addClass("ui-icon-triangle-1-e");
		}
		
		return false;
	}).next().hide();
	
	//gain sliders
	$( ".gainSlider" ).slider({ 
		min: ${readerConfig.minimumGain},
		max: ${readerConfig.maximumGain},
		step: ${readerConfig.gainSliderIncrement},
		create: function() {
			//the actual gain value (shown on hover)
			var gain = $(this).attr("title");			
			var sliderValue = gain;
			//set the value
			$(this).slider("value", sliderValue);
			$(this).attr("title", sliderValue);
		},
		slide: function(event, ui) {
			$(this).attr("title", ui.value);
		},
		stop: function(event, ui) {
			var readerId = $(this).parent().attr("title");			
			var gain = ui.value;
			updateGain(readerId, gain);
		}
	});
	
	//threshold sliders
	$( ".thresholdSlider" ).slider({ 
		min: ${readerConfig.minimumThreshold},
		max: ${readerConfig.maximumThreshold},
		step: ${readerConfig.thresholdSliderIncrement},
		create: function() {
			$(this).slider("value", $(this).attr("title"));
		},
		slide: function(event, ui) {
			$(this).attr("title", ui.value);
		},
		stop: function(event, ui) {
			var readerId = $(this).parent().attr("title");
			updateThreshold(readerId, ui.value);
		}
	});
	
	//signal strength indicators
	$( ".progressbar" ).progressbar();
	$( ".locationProgressbar" ).progressbar();
});

function updateGain(readerId, gain){
	document.forms.idGainUpdateForm.gain.value = gain;
	document.forms.idGainUpdateForm.readerId.value = readerId;
	dojo.event.topic.publish('handleGainUpdate');
}

function updateThreshold(readerId, threshold){
	document.forms.idThresholdUpdateForm.threshold.value = threshold;
	document.forms.idThresholdUpdateForm.readerId.value = readerId;
	dojo.event.topic.publish('handleThresholdUpdate');
}
</script>