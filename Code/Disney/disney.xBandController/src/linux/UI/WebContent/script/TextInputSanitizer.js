function TextInputSanitizer(){}

TextInputSanitizer.sanitize = function(input, replace) 
{
	if (input == null || input === undefined){
		return input;
	}
	
	if (replace == null || replace === undefined){
		replace = "";
	}
	
	return input.replace(/<|>|&lt;|&gt;|"|&quot;|&|&amp;|\r\n|\n/gi, replace);
};


