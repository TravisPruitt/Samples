#!/bin/bash

function FindXbrcSetting () 
{
	local HOST=$1
	local SETTINGS=$2
	local CLASS=$3
	local SETTINGNAME=$4
	local CURRENTVALUE;
	
	# Pull the current value.
	CURRENTVALUE=""
	EXPRESSION='<property.*class="'$CLASS'".*name="'$SETTINGNAME'".*>(.*)</property>'
	
	while IFS= read -r line
	do
		if [[ "$line" =~ $EXPRESSION ]]; then
			CURRENTVALUE="${BASH_REMATCH[1]}"
			#echo "Found $SETTINGNAME=$CURRENTVALUE"
		fi
	done <<< "$SETTINGS"
	
	echo $CURRENTVALUE
}

function VerifyXbrcSetting ()
{
	local HOST=$1
	local SETTINGS=$2
	local CLASS=$3
	local SETTINGNAME=$4
	local EXPECTEDVALUE=$5
	
	local CURRENTVALUE=$(FindXbrcSetting "$HOST" "$SETTINGS" "$CLASS" "$SETTINGNAME")
	
	# See if it matches the expected value.
	if [[ $CURRENTVALUE != $EXPECTEDVALUE ]]; then
		echo -e "\tCONFIGURATION WARNING (EXPECTED VALUE): Host=$HOST;Property=$CLASS.$SETTINGNAME;ExpectedValue=$EXPECTEDVALUE;CurrentValue=$CURRENTVALUE"
	fi
}

function VerifyUrlFullyQualified ()
{
	local HOST=$1
	local SETTINGS=$2
	local CLASS=$3
	local SETTINGNAME=$4
	local URL=$5

	case "$URL" in
	*wdw.disney.com*)
			;;
	*) HOSTTYPE=xbrc
		echo -e "\tCONFIGURATION WARNING (NOT FULLY QUALIFIED URL): Host=$HOST;Property=$CLASS.$SETTINGNAME;CurrentValue=$URL";;
	esac
}

function VerifyXbrcValidUrl ()
{
	local HOST=$1
	local SETTINGS=$2
	local CLASS=$3
	local SETTINGNAME=$4
	
	local URL=$(FindXbrcSetting "$HOST" "$SETTINGS" "$CLASS" "$SETTINGNAME")
	
	# Determine if the URL actually has characters.
	# See if it matches the expected value.
	if [ -z "$URL" ]; then
		echo -e "\tCONFIGURATION WARNING (MISSING URL): Host=$HOST;Property=$CLASS.$SETTINGNAME;CurrentValue=$URL"
	fi
	
	# Determine if the URL is fully qualified.
	VerifyUrlFullyQualified "$HOST" "$SETTINGS" "$CLASS" "$SETTINGNAME" "$URL" 
}

function CheckIdmsSettings ()
{
	local SETTINGS
	SETTINGS=`cat $2`
	echo "checkidmsettings $1 $SETTINGS" 
}

function CheckXbrmsSettings ()
{
	local SETTINGS
	SETTINGS=`cat $2`
	echo "checkxbrmssettings $1 $SETTINGS" 
}

function CheckGxpSettings ()
{
	local SETTINGS
	SETTINGS=`cat $2`
	echo "checkgxpsettings $1 $SETTINGS" 
}

function ParseXml ()
{
	local TEXT=$1
	local ELEMENTNAME=$2
	local CURRENTVALUE=""
	local EXPRESSION="<$ELEMENTNAME>(.*)</$ELEMENTNAME>"
	
	if [[ "$TEXT" =~ $EXPRESSION ]]; then
		CURRENTVALUE="${BASH_REMATCH[1]}"
	fi
	
	echo $CURRENTVALUE	
}

function VerifyXbrcLocationsAndReaders ()
{
	local HOST=$1
	local SETTINGS=$2
	local LASTYPE
	local SECTION
	local TEMP
	local NAME

	while IFS= read -r LINE
	do
		case "$LINE" in
		*\<readerlocation\>*)
			LASTTYPE=LOCATION
			;;
		*\<reader\>*) 
			LASTTYPE=READERINFO
			;;
		# Shared across reader locations and names.
		*\<name\>*)
			NAME=$(ParseXml $LINE "name")
			;; 
		# Location based verification of settings.
		*\<section\>*)
			if [ -z "$SECTION" ]; then
				SECTION=$(ParseXml $LINE "section")
			fi
			;;
		*\<usesecureid\>*)
			TEMP=$(ParseXml $LINE "usesecureid")
			if [[ ($TEMP != "null") && ($NAME != "UNKNOWN") ]]; then
				echo -e "\t$LASTTYPE CONFIGURATION WARNING: Host=$HOST;Setting=$LINE"
			fi
			;; 
		*\<successsequence\>*)
			TEMP=$(ParseXml $LINE "successsequence")
			if [[ ! -z $TEMP ]]; then
				echo -e "\t$LASTTYPE CONFIGURATION WARNING: Host=$HOST;Setting=$LINE"
			fi
			;; 
		*\<failuresequence\>*)
			TEMP=$(ParseXml $LINE "failuresequence")
			if [[ ! -z $TEMP ]]; then
				echo -e "\t$LASTTYPE CONFIGURATION WARNING: Host=$HOST;Setting=$LINE"
			fi
			;; 
		*\<errorsequence\>*)
			TEMP=$(ParseXml $LINE "errorsequence")
			if [[ ! -z $TEMP ]]; then
				echo -e "\t$LASTTYPE CONFIGURATION WARNING: Host=$HOST;Setting=$LINE"
			fi
			;; 
		*\<idlesequence\>*)
			TEMP=$(ParseXml $LINE "idlesequence")
			if [[ ! -z $TEMP ]]; then
				echo -e "\t$LASTTYPE CONFIGURATION WARNING: Host=$HOST;Setting=$LINE"
			fi
			;; 
		*\<usesecureid\>*)
			TEMP=$(ParseXml $LINE "usesecureid")
			if [[ $TEMP != "null" ]]; then
				echo -e "\t$LASTTYPE CONFIGURATION WARNING: Host=$HOST;Setting=$LINE"
			fi
			;; 
		# Reader based verification.
		*\<enabled\>*)
			TEMP=$(ParseXml $LINE "enabled")
			if [[ $TEMP != "true" ]]; then
				echo -e "\t$LASTTYPE CONFIGURATION WARNING: Host=$HOST;Setting=$LINE"
			fi
			;; 
		*\<deviceid\>*)
			TEMP=$(ParseXml $LINE "deviceid")
			if [[ -z $TEMP ]]; then
				echo -e "\t$LASTTYPE CONFIGURATION WARNING: Host=$HOST;Setting=$LINE"
			fi
			;; 
		esac
	done <<< "$SETTINGS"
}

function CheckXbrcAttractionSettings ()
{
	VerifyXbrcSetting "$1" "$2" "AttractionModelConfig" "cmsecreaderlockexpiry" "30000"
	VerifyXbrcValidUrl "$1" "$2" "AttractionModelConfig" "gxpurl"
}

function CheckParkEntrySettings ()
{
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "abandonmenttimesec" "25"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "castappcorethreadpoolsize" "10"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "castappmaxthreadpoolsize" "50"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "castappmessagetimeoutsec" "15"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "castappresponsetimeutsec" "30"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "castloginoklight" "entry_login_ok"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "flashcolors" "#5a1414|#505000|#005028|#002850|#3c0f3c"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "greenlighttimeoutms" "2500"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "guestretaptimeoutms" "1000"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "maxfpscanretry" "3"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "omniconnecttimeoutms" "4000"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "omnirequesttimeoutms" "15000"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "omniticketport" "9920"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "readerconnecttimeoutms" "2000"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "readerflashtimems" "3000"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "savebioimages" "none"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "savebioimagesfrequency" "1"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "startscandurationms" "0"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "startscanlight" "entry_start_scan"
	VerifyXbrcSetting "$1" "$2" "ParkEntryModelConfig" "testmode" "false"

	VerifyXbrcSettingExists "$1" "$2" "ParkEntryModelConfig" "omniid"
}

function CheckXbrcSpaceSettings ()
{
	VerifyXbrcSetting "$1" "$2" "SpaceModelConfig" "abandonmenttimeout_msec" "3000"
	VerifyXbrcSetting "$1" "$2" "SpaceModelConfig" "castmemberdetectdelay_msec" "2000"
	VerifyXbrcSetting "$1" "$2" "SpaceModelConfig" "guestdetectdelay_msec" "10000"
	VerifyXbrcSetting "$1" "$2" "SpaceModelConfig" "puckdetectdelay_msec" "10000"
	VerifyXbrcSetting "$1" "$2" "SpaceModelConfig" "sendunregisteredbandevents" "true"
	VerifyXbrcSetting "$1" "$2" "SpaceModelConfig" "cmsecreaderlockexpiry" "30000"
	VerifyXbrcSetting "$1" "$2" "SpaceModelConfig" "cmsecreaderlockexpiry" "30000"
}

function CheckXbrcSettings ()
{
	local SETTINGS
	SETTINGS=`cat $2`
	#echo "$SETTINGS"

	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "adjusteventtimes" "true"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "enableha" "true"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "errorsequence" "blue"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "errortimeout_msec" "2000"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "eventdumpmaxfiles" "30"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "eventdumpmaxsizemb" "1000"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "failuresequence" "exception"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "failuretimeout_msec" "30000"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "idlesequence" "off"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "idmscachetime_sec" "1800"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "securetapid" "true"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "successsequence" "GXP_success"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "successtimeout_msec" "2500"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "tapsequence" "GXP_tap"
	VerifyXbrcSetting "$1" "$SETTINGS" "ControllerInfo" "taptimeout_msec" "10000"

	local MODEL=$(FindXbrcSetting "$HOST" "$SETTINGS" "ControllerInfo" "model")
	case "$MODEL" in
	*attractionmodel*)
		CheckXbrcAttractionSettings "$1" "$SETTINGS";;
	*parkentrymodel*)
		CheckXbrcParkEntrySettings "$1" "$SETTINGS";;
	*spacemodel*)
		CheckXbrcSpaceSettings "$1" "$SETTINGS";;
	esac

	# Verify Url Based settings.
	VerifyXbrcValidUrl "$1" "$SETTINGS" "ControllerInfo" "xviewurl"
	VerifyXbrcValidUrl "$1" "$SETTINGS" "ControllerInfo" "vipaddress"
	
	# Verify location and reader settings.
	VerifyXbrcLocationsAndReaders "$1" "$SETTINGS"
}	

LIST=fpt3.list
OUTPUT=curloutput.txt
IFS='
'
VIPS=`(cut -f 2 -d ',' < $LIST )`
echo $VIPS

exec < $LIST
while read line; do
	#echo "$line"

	# Loop through all of the hosts defined in the list file.
	for (( i=2; i<6; i++ )) do
		
		# Parse out the chunk (the VIP) indicated by i. Remove spaces.
		HOST=`echo $line | cut -f $i -d ',' | sed 's/ //g'`
		
		# Set the host type based on a chunk in the VIP name.
		if [ $i -eq 2 ]; then
		
			# Find the type of machine based on the name of the
			# machine itself.
			case "$HOST" in
			*idms*) HOSTTYPE=idms
					CURLSTRING="IDMS/status" ;;
			*xbrms*) HOSTTYPE=xbrms
					 CURLSTRING="XBRMS/rest/status" ;;
			*gxp*)	HOSTTYPE=gxp
					CURLSTRING="gxp" ;;
			*fpp*)	HOSTTYPE=fpp
					CURLSTRING="" ;;
			*) HOSTTYPE=xbrc
				 CURLSTRING="currentconfiguration" ;;
			esac
			
		fi
		
		# Only execute if we've determined the host type.
		if [ ! -z "$HOST" ]; then
			if [ "$i" -gt 2 ]; then
			
				# Try to get the status from the machine and look for resulting
				# XML output.
				curl -m 15 http://$HOST:8080/$CURLSTRING >$OUTPUT 2>>$OUTPUT
				CURLOUTPUTXML='grep xml $OUTPUT'
				
				case "$HOSTTYPE" in
				idms) 
					CheckIdmsSettings "$HOST" "$OUTPUT";;
				xbrms) 
					CheckXbrmsSettings "$HOST" "$OUTPUT" ;;
				gxp) 
					CheckGxpSettings "$HOST" "$OUTPUT" ;;
				xbrc) 
					CheckXbrcSettings "$HOST" "$OUTPUT" ;;
				esac
			fi
		fi
	done
done
