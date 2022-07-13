BEGIN {
    # convert the command line arguments to options and then remove the 
    # arguments so that they are not interpreted as additional files.
    option[0] = ""
    opt=0
    for (k=2;k<ARGC;k++)  {
		if(ARGV[k] == "fields=1") # do a different output, just the field names.
		{
			fields = 1
		}
    	else
		{ 
			fields = 0
			option[opt] = ARGV[k]
			gsub(/ /,"_", option[opt]) 									# field names are underscored
			ARGV[k] = ""
			opt++
		}
	}
	
    if (fields != 1) {
		for (opt in option)
			printf("%-20s ",option[opt])
		print
    }
    options=ARGC-2
    ARGC = 2
}

$0 ~ /\"(.+)\" : (.+)/ {			# assignments
   
	#take anything inside of quotes before the
	# : and combine it into a single name. 

	if (match($0,/\"[^:]+\"/) ) 
	{
		varname = substr ($0, RSTART+1, RLENGTH-2) # no quotes
		gsub(/ /,"_",varname)
		
		if (match($0,/:.+/) ) # look for the corresponding value
		{
			varvalue = substr ($0, RSTART, RLENGTH)
			gsub(/,|\\n|: /,"",varvalue) # remove all commas, ": ", "\n" (bad data..) 
		}
		
		# print "00--" $0 "--00"
		lrr[varname] = varvalue
		# print "lrr[" varname "] = " varvalue
	}
	else
		print "NO VAR NAME :> " $0
}

/}$/ {									# end of one reader
	if(fields != 1)	
    {
		for (opt in option)
		{	
			printf("%-20s ",lrr[option[opt]])
			delete lrr[option[opt]]
		}
		printf("\n")			
    }
}

END {
	if(fields == 1)
	{
	    for(name in lrr)
			print name
	}
}
