var month=new Array(12);
month[0]="January";
month[1]="February";
month[2]="March";
month[3]="April";
month[4]="May";
month[5]="June";
month[6]="July";
month[7]="August";
month[8]="September";
month[9]="October";
month[10]="November";
month[11]="December";

var day = new Array(7);
day[0] = "Sunday";
day[1] = "Monday";
day[2] = "Tuesday";
day[3] = "Wednesday";
day[4] = "Thursday";
day[5] = "Friday";
day[6] = "Saturday";

function showtime () 
{
  var curtime = new Date();
  var curyear = curtime.getFullYear();
  var curmonth = month[curtime.getMonth()];
  var curdayofweek = day[curtime.getDay()];
  var curdayofmonth = curtime.getDate();
  var curhour = curtime.getHours();
  var curmin = curtime.getMinutes();
  var cursec = curtime.getSeconds();
  var time = "";
 
  if(curhour == 0) curhour = 12;
  time = curdayofweek + ", " +
  		 curmonth + " " + curdayofmonth + ", " +  curyear + "  " + 
	  	 (curhour > 12 ? curhour - 12 : curhour) + ":" +
         (curmin < 10 ? "0" : "") + curmin + ":" +
         (cursec < 10 ? "0" : "") + cursec + " " +
         (curhour > 12 ? "PM" : "AM");
 
  document.getElementById('clock').innerHTML = "<div><h3>" + time + "<h3></div>";
}

function drawLine(Ax,Ay,Bx,By)
{
	bla = "";
	var lineLength = Math.sqrt( (Ax-Bx)*(Ax-Bx)+(Ay-By)*(Ay-By) );
	for( var i=0; i<lineLength; i++ )
	{
	    bla += "<div style='position:absolute;left:"+ Math.round( Ax+(Bx-Ax)*i/lineLength  ) +"px;top:"+ Math.round( Ay+(By-Ay)*i/lineLength  ) +"px;width:2px;height:2px;background:#248519'></div>";
	}
	document.getElementById("attractionPlan").innerHTML += bla;
}

function getKey(data)
{
	var i = data.indexOf(":",0);
	if (i >= 0)
		return data.substr(0,i);
	return "";
}

function getValue(data)
{
	var i = data.indexOf(":",0);
	if (i >= 0)
		return data.substr(i+1);
	return "";
}

function showProgress() {
	document.getElementById("idProgress").style.display="block";
}

function hideProgress() {
	document.getElementById("idProgress").style.display="none";
}

