BEGIN {
	print "Check number series." 
	FS = " "
	old_num = 0
}


#######################################################################
#
#  AWK RULES
#
#######################################################################

#if the record is not our event, go to next record

# collect data 
$0 ~ /EVNT: / {	
	num = $5
	
	diff = num-old_num
	old_num = num;
	
	print num,diff
	
}

