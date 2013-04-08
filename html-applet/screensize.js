var width, height;
	var small;
	if(screen.height <= 900)
	{
		width = 1054;
		height = 622;
		small = true;
	}
	else
	{
		width = 1256;
		height = 822;
		small = false;
	}
	document.write('<applet code="lorian.graph.GraphApplet.class" archive="graph.jar" width="'+width+'" height="'+height+'">');
	if(small)
	{
		document.write('<param name="small" value="true">');
	}
	else
	{
		document.write('<param name="small" value="false">');
	}
	document.write('</applet>');