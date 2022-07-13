function InputSanitizer(){}

InputSanitizer.sanitizeText = function(input, replace) 
{
	if (input == null || input === undefined){
		return input;
	}
	
	if (replace == null || replace === undefined){
		replace = "";
	}
	
	if (typeof(input) === "string"){
        input =	input.replace( /^\s+/, "" ).replace( /\s+$/, "" );
	}
	
	return input.replace(/<|>|&lt;|&gt;|"|&quot;|&|&amp;|\r\n|\n/gi, replace);
};


