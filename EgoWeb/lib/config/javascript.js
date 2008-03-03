/**
 * struts-layout core javascript
 *
 * All rights reserved.
 */
// type checking functions

function checkValue(field, property, type, required) {

	if (field.value!="") {
	
		document.images[property + "required"].src= imgsrc + "clearpixel.gif";
		if (type=="NUMBER" && !isNumber(field.value)) document.images[property + "required"].src= imgsrc + "ast.gif";
		if (type=="DATE" && !isDate(field.value)) document.images[property + "required"].src = imgsrc + "ast.gif";
		if (type=="EMAIL" && !isEmail(field.value)) document.images[property + "required"].src= imgsrc + "ast.gif";
	
	} else {	
		if (required) document.images[property + "required"].src= imgsrc + "ast.gif";
	}
}

// Return true if value is an e-mail address
function isEmail(value) {
	invalidChars = " /:,;";
	if (value=="") return false;
	
	for (i=0; i<invalidChars.length;i++) {
	   badChar = invalidChars.charAt(i);
	   if (value.indexOf(badChar,0) != -1) return false;
	}
	
	atPos = value.indexOf("@", 1);
	if (atPos == -1) return false;
	if (value.indexOf("@", atPos + 1) != -1) return false;
	
	periodPos = value.indexOf(".", atPos);
	if (periodPos == -1) return false;
	
	if (periodPos+3 > value.length) return false;

	return true;
}



// Return true if value is a number
function isNumber(value) {
	if (value=="") return false;

	var d = parseInt(value);
	if (!isNaN(d)) return true; else return false;		

}

// return true if value is a date
// ie in the format XX/YY/ZZ where XX YY and ZZ are numbers
function isDate(value) {
	if (value=="") return false;
	
	var pos = value.indexOf("/");
	if (pos == -1) return false;
	var d = parseInt(value.substring(0,pos));
	value = value.substring(pos+1, 999);
	pos = value.indexOf("/");
	if (pos==-1) return false;
	var m = parseInt(value.substring(0,pos));
	value = value.substring(pos+1, 999);
	var y = parseInt(value);	
	if (isNaN(d)) return false;	
	if (isNaN(m)) return false;	
	if (isNaN(y)) return false;	
	
	var type=navigator.appName;
	if (type=="Netscape") var lang = navigator.language;
	else var lang = navigator.userLanguage;
	lang = lang.substr(0,2);

	if (lang == "fr") var date = new Date(y, m-1, d);
	else var date = new Date(d, m-1, y);
	if (isNaN(date)) return false;	
	return true;
 }

// menu functions

function initMenu(menu) {
	if (getMenuCookie(menu)=="hide") {
		document.getElementById(menu).style.display="none";
	} else {
		document.getElementById(menu).style.display="";
	}
}

function changeMenu(menu) {
if (document.getElementById(menu).style.display=="none") {
	document.getElementById(menu).style.display="";
	element = document.getElementById(menu+"b");
	if (element != null) {
		document.getElementById(element).style.display="none";
	}
	setMenuCookie(menu,"show");
} else {
	document.getElementById(menu).style.display="none";
	element = document.getElementById(menu+"b");
	if (element != null) {	
		var width = document.getElementById(menu).offsetWidth;	
		if (navigator.vendor == ("Netscape6") || navigator.product == ("Gecko"))
			document.getElementById(menu+"b").style.width = width;	
		else 
			document.getElementById(menu+"b").width = width;
		document.getElementById(menu+"b").style.display="";
	}
	setMenuCookie(menu,"hide");
}
return false;
}

function setMenuCookie(name, state) {	
	if (name.indexOf("treeView")!=-1) {
		if (state=="show") {
			var cookie = getMenuCookie("treeView", "");
			if (cookie=="???") cookie = "_";
			cookie = cookie + name + "_";
			document.cookie = "treeView=" + escape(cookie);

		} else {
			var cookie = getMenuCookie("treeView", "");
			var begin = cookie.indexOf("_" + name + "_");
			if (cookie.length > begin + name.length + 2) {
				cookie = cookie.substring(0, begin+1) + cookie.substring(begin + 2 + name.length);
			} else {
				cookie = cookie.substring(0, begin+1);
			}		
			document.cookie = "treeView=" + escape(cookie);
		}
	} if (name.indexOf("selectedTab"!=-1)) {
		document.cookie = "selectedTab=" + escape(state);
	} else {
		var cookie = name + "STRUTSMENU=" + escape(state);
		document.cookie = cookie;	
	}
}

function setTabCookie(name, value) {
	var cookie = getMenuCookie("selectedTab", "");	
	var start;
	var end;
	if (cookie=="undefined") cookie = "";
	if (cookie==null) cookie = "";
	if (cookie=="???") cookie = "";	
	start = cookie.indexOf(name + "=");
	if (start==-1) {
		cookie = cookie + name + "=" + value + ";"
	} else {
		end = cookie.substring(start).indexOf(";");
		cookie = cookie.substring(0, start) + name + "=" + value + ";" + cookie.substring(end);
	}
	setMenuCookie("selectedTab", cookie);
}

function getMenuCookie(name, suffix) {
	if (suffix==null) {
		suffix = "STRUTSMENU";
	}
	var prefix = name + suffix + "=";
	var cookieStartIndex = document.cookie.indexOf(prefix);
	if (cookieStartIndex == -1) return "???";
	var cookieEndIndex = document.cookie.indexOf(";", cookieStartIndex + prefix.length);
	if (cookieEndIndex == -1) cookieEndIndex = document.cookie.length;
	return unescape(document.cookie.substring(cookieStartIndex + prefix.length, cookieEndIndex));
}

// sort functions
function arrayCompare(e1,e2) {
	return e1[0] < e2[0] ? -1 : (e1[0] == e2[0] ? 0 : 1);

}

var tables = new Array();
function arraySort(tableName, column, lineNumber, columnNumber) {
	var aTable = tables[tableName];
	var arrayToSort;
	var array;
	var reverse = 0;
	if (aTable) {
		array = aTable[0];
		arrayToSort = new Array(lineNumber);
		for (i=0;i<lineNumber;i++) {
			arrayToSort[i] = new Array(2);
			arrayToSort[i][0] = array[i][column];
			arrayToSort[i][1] = i;				
		}
		reverse = 1 - aTable[1];
		aTable[1] = reverse;
	} else {
		array = new Array(lineNumber);
		arrayToSort = new Array(lineNumber);
		for (i=0;i<lineNumber;i++) {	
			array[i] = new Array(columnNumber);
			for (j=0;j<columnNumber;j++) {
				obj = document.getElementById("t" + tableName + "l" + (i+1) +"c" + j);		
				array[i][j] = obj.innerHTML;
			}
			arrayToSort[i] = new Array(2);
			arrayToSort[i][0] = array[i][column];
			arrayToSort[i][1] = i;		
	
			aTable = new Array(2);
			aTable[0] = array;
			aTable[1] = 0;
			tables[tableName] = aTable;
		}
	}

	arrayToSort.sort(arrayCompare);
	if (reverse) {
		arrayToSort.reverse();
	}

	for (i=0;i<lineNumber;i++) {
		goodLine = arrayToSort[i][1];
		for (j=0;j<columnNumber;j++) {
			document.getElementById("t" + tableName + "l" + (i+1) +"c" + j).innerHTML = array[goodLine][j];
		}
	}
}

// calendar functions

var calformname;
var calformelement;
var calpattern;

/**
 * Static code included one time in the page.
 *
 * a {text-decoration: none; color: #000000;}");
 * TD.CALENDRIER {background-color: #C2C2C2; font-weight: bold; text-align: center; font-size: 10px; }");
 *
 * bgColor => #000000, #C9252C, 
 */
function printCalendar(day1, day2, day3, day4, day5, day6, day7, month1, month2, month3, month4, month5, month6, month7, month8, month9, month10, month11, month12, day, month, year) {
	document.write('<div id="caltitre" style="z-index:10;">');	
	document.write('<table cellpadding="0" cellspacing="0" border="0" width="267">');
//	document.write('<form>');
	document.write('<tr><td colspan="15" class="CALENDARBORDER"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td></tr>');
	document.write('<tr>');
	document.write('	<td class="CALENDARBORDER" width="1"><img src="' + imgsrc + 'shim.gif" width=1 height=20></td>');
	document.write('	<td class="CALENDARTITLE" colspan="3" align="right"><img src="' + imgsrc + 'previous.gif" onclick="cal_before(' + day + ');"></td>');
	document.write('	<td colspan=7 align="center" class="CALENDARTITLE">');
	
	// month
	document.write('<select id="calmois" name="calmois" onchange="cal_chg(' + day + ');"><option value=0>...</option>');	
		
	for(i=1;i<=12;i++) {
		var str='<option value=' + i + '>';
		monthIndex = i-1;
		switch (monthIndex) {
			case 0: str += month1; break;
			case 1: str += month2; break;
			case 2: str += month3; break;
			case 3: str += month4; break;
			case 4: str += month5; break;
			case 5: str += month6; break;
			case 6: str += month7; break;
			case 7: str += month8; break;
			case 8: str += month9; break;
			case 9: str += month10; break;
			case 10: str += month11; break;
			case 11: str += month12; break;
		}
		document.write(str);
	}	

	document.write('</select>');
	
	// year
	document.write('<select id="calyear" name="calyear" onchange="cal_chg('+ day + ');">');	
	document.write("</select>");
	
	document.write('	</td>');
	document.write('	<td class="CALENDARTITLE" align="left" colspan="3"><img src="' + imgsrc + 'next.gif" onclick="cal_after(' + day + ');">&nbsp;&nbsp;<img src="' + imgsrc + 'close.gif" onclick="hideCalendar()"></td>');
	document.write('	<td class="CALENDARBORDER" width=1><img src="' + imgsrc + 'shim.gif" width="1" height="1"></td>');
	document.write('</tr>');
	document.write('<tr><td colspan=15 class="CALENDARBORDER"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td></tr>');
	document.write('<tr>');
	document.write('	<td class="CALENDARBORDER" width="1"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td>');
	document.write('	<td class="CALENDRIER" width="38">' + day1 + '</td>');
	document.write('	<td class="CALENDRIER" width="1"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td>');
	document.write('	<td class="CALENDRIER" width="38">' + day2 + '</td>');
	document.write('	<td class="CALENDRIER" width="1"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td>');
	document.write('	<td class="CALENDRIER" width="38">' + day3 + '</td>');
	document.write('	<td class="CALENDRIER" width="1"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td>');
	document.write('	<td class="CALENDRIER" width="38">' + day4 + '</td>');
	document.write('	<td class="CALENDRIER" width="1"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td>');
	document.write('	<td class="CALENDRIER" width="38">' + day5 + '</td>');
	document.write('	<td class="CALENDRIER" width="1"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td>');
	document.write('	<td class="CALENDRIER" width="38">' + day6 + '</td>');
	document.write('	<td class="CALENDRIER" width="1"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td>');
	document.write('	<td class="CALENDRIER" width="38">' + day7 + '</td>');
	document.write('	<td class="CALENDARBORDER" width="1"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td>');
	document.write('</tr>');
	document.write('<tr><td colspan=15 class="CALENDARBORDER"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td></tr>');
//	document.write('</form>');
	document.write('</table>');
	document.write('</div>');
//	document.write('<div id="caljour" style="position:absolute; left:0px; top:45px; width:253; height:130; z-index:10;"></div>');
	document.write('<div id="caljour" style="z-index:10;"></div>');	
}

/**
 * Show the calendar
 */
function showCalendar(year, month, day, pattern, formName, formProperty, event, startYear, endYear) {
	if (startYear!=null) {
		var calyear = document.getElementById("calyear");
		for (i = startYear; i <= endYear; i++) {			
			calyear.options[i - startYear] = new Option(i,i);
		}
		calyear.options.length = endYear - startYear + 1;
	}

	if(document.all) {
		// IE.
		var ofy=document.body.scrollTop;
		var ofx=document.body.scrollLeft;
		document.all.slcalcod.style.left = event.clientX+ofx+10;
		document.all.slcalcod.style.top = event.clientY+ofy+10;
		document.all.slcalcod.style.visibility="visible";
		document.all.calmois.selectedIndex= month;
		hideElement("SELECT");
	} else if(document.layers) {
		// Netspace 4
		document.slcalcod.left = e.pageX+10;
		document.slcalcod.top = e.pageY+10;
		document.slcalcod.visibility="visible";
		document.slcalcod.document.caltitre.document.forms[0].calmois.selectedIndex=month;
	} else {
		// Mozilla
		var calendrier = document.getElementById("slcalcod");
		var ofy=document.body.scrollTop;
		var ofx=document.body.scrollLeft;
		calendrier.style.left = event.clientX+ofx+10;
		calendrier.style.top = event.clientY+ofy+10;
		calendrier.style.visibility="visible";
		document.getElementById("calmois").selectedIndex=month;
	}
	if (document.forms[formName].elements[formProperty].stlayout) {
		var lc_day = document.forms[formName].elements[formProperty].stlayout.day;
		var lc_month = document.forms[formName].elements[formProperty].stlayout.month;
		var lc_year = parseInt(document.forms[formName].elements[formProperty].stlayout.year);
		cal_chg(lc_day, lc_month, lc_year);	
	} else {
		cal_chg(day, month, year);	
	}
	calformname = formName;
	calformelement = formProperty;
	calpattern = pattern;
}

/**
 * Redraw the calendar for the current date and a selected month
 */
function cal_chg(day, month, year){
	var str='',j;	
	
	champMonth = document.getElementById("calmois");
	if (month==null) {		
		month = champMonth.options[champMonth.selectedIndex].value;
	} else {
		champMonth.selectedIndex = month;
	}
		
	
	champYear = document.getElementById("calyear");
	if (year==null) {		
		year = champYear.options[champYear.selectedIndex].value;
	} else {
		index = year - champYear.options[0].value;
		if (index >= 0 && index < champYear.options.length) {
			champYear.selectedIndex = index;
		} else {
			// the initial year is not in the calendar allowed years.
			year = champYear.options[0].value;
		}
	}
	
	
	if(month>0) {
	
		j=1;
		
		str+='<table cellpadding=0 cellspacing=0 border=0 width=267>\n';
		for(u=0;u<6;u++){
			str+='	<tr>\n';
			for(i=0;i<7;i++){
				ldt=new Date(year,month-1,j);
				str+='		<td class="CALENDARBORDER" width=1><img src="' + imgsrc + 'shim.gif" width=1 height=20></td>\n';
				str+='		<td class="CALENDAR'; if(ldt.getDay()==i && ldt.getDate()==j && j==day /*&& newMonth==month && lc_annee==year*/) str+='SELECTED'; else if(i==0 || i==6) str+='WEEKEND'; else str+='WEEK'; str+='" width="38" align="center">';
				if (ldt.getDay()==i && ldt.getDate()==j) {str+='<a class="CALENDRIER" href="javascript://" class="CALENDRIER" onmousedown="dtemaj(\'' + j + '\',\'' + month + '\',\'' + year +'\');">'+j+'</a>'; j++;} else str+='&nbsp;';
				str+='</td>\n';
			}
			str+='		<td class="CALENDARBORDER" width=1><img src="' + imgsrc + 'shim.gif" width=1 height=1></td>\n';
			str+='	</tr>\n';
			str+='	<tr><td colspan=15 class="CALENDARBORDER"><img src="' + imgsrc + 'shim.gif" width=1 height=1></td></tr>\n';
		}
		str+='</table>\n';
	
	}
	
	if(document.all) {
		document.all.caljour.innerHTML=str;
	}
	if(document.layers) {
		obj=document.calendrier.document.caljour; obj.top=48; obj.document.write(str); obj.document.close();
	}
	if (!document.all && document.getElementById) {
		document.getElementById("caljour").innerHTML = str;
	}
}

/**
 * Display the previous month
 */
function cal_before(day, month, year) {
	var champMonth, champYear;
	champMonth = document.getElementById("calmois");
	champYear = document.getElementById("calyear");
			
	if (champMonth.selectedIndex>1) { 
		champMonth.selectedIndex--;
	} else if (champYear.selectedIndex>0) {
		champYear.selectedIndex--;
		champMonth.selectedIndex = champMonth.options.length - 1;
	}
	cal_chg(day, champMonth.options[champMonth.selectedIndex].value, champYear.options[champYear.selectedIndex].value);
}

/**
 * Display the next month
 */
function cal_after(day, month, year) {
	// get required objects
	var champMonth, champYear;
	champMonth = document.getElementById("calmois");
	champYear = document.getElementById("calyear");
	if (champMonth.selectedIndex < champMonth.options.length - 1) {
		champMonth.selectedIndex++;
	} else if (champYear.selectedIndex < champYear.options.length - 1) {
		champYear.selectedIndex++;	
		champMonth.selectedIndex = 1;
	}
	cal_chg(day, champMonth.options[champMonth.selectedIndex].value, champYear.options[champYear.selectedIndex].value);
}

/**
 * Update the date in the input field and hide the calendar.
 * PENDING: find a way to make the format customable.
 */
function dtemaj(jour, mois, annee){
	document.forms[calformname].elements[calformelement].value = formatDate(jour, mois, annee);
	document.forms[calformname].elements[calformelement].stlayout = new Object();
	document.forms[calformname].elements[calformelement].stlayout.day = jour;
	document.forms[calformname].elements[calformelement].stlayout.month = mois;
	document.forms[calformname].elements[calformelement].stlayout.year = annee;
	hideCalendar();
}

function formatDate(day, month, year) {
	var date = "";
	var pos = 0;
	var pattern;
	var previousPattern;
	var patternLength = 0;
	if (calpattern!=null && calpattern.length>0) {		
		previousPattern = calpattern.charAt(0);
		while (pos <= calpattern.length) {
			if (pos < calpattern.length) {
				pattern = calpattern.charAt(pos);
			}  else {
				pattern = "";
			}
			if (pattern != previousPattern) {			
				switch (previousPattern) {
					case 'y':
						date += padYear(year, patternLength);				
						break;
					case 'M':
						date += padNumber(month, patternLength);
						break;
					case 'd':
						date += padNumber(day, patternLength);
						break;
					case '\'':
						// PENDING
						break;
					default:
						date += previousPattern;
				}
				previousPattern = pattern;
				patternLength = 0;
			}
			patternLength++;
			pos++;
		}
	}
	return date;
}

function padYear(year, patternLength) {
	if (patternLength==2 && year.length==4) {
		return year.substring(2);
	} else {
		return year;
	}
}

function padNumber(number,length) {
    var str = '' + number;
    while (str.length < length)
        str = '0' + str;
    return str;
}

function hideCalendar() {
	if(document.all) {
		// IE.
		document.all.slcalcod.style.visibility="hidden";
		showElement("SELECT");
	} else if(document.layers) {
		// Netspace 4
		document.slcalcod.visibility="hidden";
	} else {
		// Mozilla
		var calendrier = document.getElementById("slcalcod");
		calendrier.style.visibility="hidden";
	}
}

/**
 * Fix IE bug
 */
function hideElement(elmID)
{
	if (!document.all) {
		return;
	}
	x = parseInt(document.all.slcalcod.style.left);
	y = parseInt(document.all.slcalcod.style.top);
	xxx = 253; // document.all.slcalcod.offsetWidth;
	yyy = 145; // document.all.slcalcod.offsetHeight;
		
	for (i = 0; i < document.all.tags(elmID).length; i++)
	{
		obj = document.all.tags(elmID)[i];
		if (! obj || ! obj.offsetParent || obj.id=="calmois" || obj.id=="calyear")
			continue;

		// Find the element's offsetTop and offsetLeft relative to the BODY tag.
		objLeft   = obj.offsetLeft;
		objTop    = obj.offsetTop;
		objParent = obj.offsetParent;
		while (objParent.tagName.toUpperCase() != "BODY")
		{
			objLeft  += objParent.offsetLeft;
			objTop   += objParent.offsetTop;
			objParent = objParent.offsetParent;
		}
										
		// Adjust the element's offsetTop relative to the dropdown menu
		//objTop = objTop - y;
	
		if (x > (objLeft + obj.offsetWidth) || objLeft > (x + xxx))
			;
		else if (objTop > y + yyy)
			;
		else if (y > (objTop + obj.offsetHeight))
			;
		else
			obj.style.visibility = "hidden";
	}
}

/**
 * Fix IE bug
 */
function showElement(elmID)
{
	if (!document.all) {
		return;
	}
	for (i = 0; i < document.all.tags(elmID).length; i++)
	{
		obj = document.all.tags(elmID)[i];
		if (! obj || ! obj.offsetParent)
			continue;
		obj.style.visibility = "";
	}
}

/**
 * Tabs code.
 *
 * @param tabVarName: name of the form variable that holds the id of the selected tab.
 */
function selectTab(tabGroupId, tabGroupSize, selectedTabId, enabledStyle, disabledStyle, errorStyle, tabKeyName, tabKeyValue) {
	// first unselect all tab in the tag groups.
	for (i=0;i<tabGroupSize;i++) {
		element = document.getElementById("tabs" + tabGroupId + "head" + i);
		if (element.classNameErrorStdLayout) {
			element.className = errorStyle;
			element.style.color = "";			
		} else if (element.className == enabledStyle) {
			element.className = disabledStyle;
			element.style.color = "";
		} else if (element.className == errorStyle) {
			// do nothing more
		}
		document.getElementById("tabs" + tabGroupId + "tab" + i).style.display = "none";
	}
	if (document.getElementById("tabs" + tabGroupId + "head" + selectedTabId).className==errorStyle) {
		document.getElementById("tabs" + tabGroupId + "head" + selectedTabId).classNameErrorStdLayout = new Object();
	}
	document.getElementById("tabs" + tabGroupId + "head" + selectedTabId).className = enabledStyle;
	document.getElementById("tabs" + tabGroupId + "head" + selectedTabId).style.cursor = "default";
	document.getElementById("tabs" + tabGroupId + "tab" + selectedTabId).style.display = "";
	
	// update a cookie holding the name of the selected tab.
	if (tabKeyName!=null) {
		setTabCookie(tabKeyName, tabKeyValue);
	}
}
function onTabHeaderOver(tabGroupId, selectedTabId, enabledStyle) {
	element = document.getElementById("tabs" + tabGroupId + "head" + selectedTabId);
	if (element.className == enabledStyle) {
		element.style.cursor = "default";
	} else {
		element.style.cursor = "hand";
	}
}

/**
 * Treeview code
 */
function loadTree(url, tree) {
	element = document.getElementById("treeView" + url);
	element.innerHTML = tree;	
	element.style.display = "";
	element = document.getElementById("treeViewNode" + url);
	element.href = "javascript://";
	setMenuCookie("treeView" + url, "show")	
}

function changeTree(tree, image1, image2) {
	var image = document.getElementById("treeViewImage" + tree);
	if (image.src.indexOf(image1)!=-1) {
		image.src = image2;
	} else {
		image.src = image1;
	}

	if (document.getElementById("treeView" + tree).innerHTML == "") {
		return true;
	} else {
		changeMenu("treeView" + tree);
		return false;
	}
}

function changeTreeAndSubtrees(tree) {
	var image = document.getElementById("treeViewImage" + tree);
	
	var link = image.parentNode;
	if (link.href.indexOf("javascript://") == -1) {
		// il s'agit d'un lien vers le treeview.do
		// => les sous-menus n'ont pas été chargés dans la page HTML
		// => on ne peut pas procéder au changement
		return false;
	}
	
	if (image.src.indexOf("Close")!=-1) {
		reg=new RegExp("Close", "g");
		image.src = image.src.replace(reg, "Open");
	} else {
		reg=new RegExp("Open", "g");
		image.src = image.src.replace(reg, "Close");
	}
	
	if (document.getElementById("treeView" + tree).innerHTML == "") {
		return true;
	} else {
		// change the menu itself
		menu = "treeView" + tree;
		changeMenu(menu);
					
		toShow = true;
		if (document.getElementById(menu).style.display=="none") {
			// the "menu" menu has just been hidden : all its subtrees must collapse too
			toShow = false;
		}
		
		list = document.getElementsByTagName("td");
		for (i=0; i<list.length; i++) {
			currentElement = list[i];
			if (currentElement.id.indexOf(menu) != -1
				&& currentElement.id!=menu) {
				// we are at a submenu level
				subTreeName = currentElement.id.substring(8);
				
				if (currentElement.style.display=="none" && toShow
					|| currentElement.style.display=="" && !toShow) {	
					image = document.getElementById("treeViewImage" + subTreeName);
					
					link = image.parentNode;
					if (link.href.indexOf("javascript://") != -1) {
						// il s'agit d'un javascript
						// => les sous-menus ont été chargés dans la page HTML
						// => on peut procéder au changement
						
						if (image.src.indexOf("Close")!=-1) {
							reg=new RegExp("Close", "g");
							image.src = image.src.replace(reg, "Open");
						} else {
							reg=new RegExp("Open", "g");
							image.src = image.src.replace(reg, "Close");
						}
					
						if (document.getElementById("treeView" + subTreeName).innerHTML == "") {
							//return true;
						} else {
							changeMenu("treeView" + subTreeName);
						}
					}
					
				}
			}
		}
		
		return false;
	}
}

function expandFirstLevels(treeviewId, numberOfLevelsToExpand) {
	menuId = "treeView" + treeviewId;
		
	list = document.getElementsByTagName("td");
	for (i=0; i<list.length; i++) {
		currentElement = list[i];
		if (currentElement.id.indexOf(menuId) != -1
			&& currentElement.id!=menuId) {
			// we are at a submenu level
			
			idSuffix = currentElement.id.substring(menuId.length);
			if (countStringOccurence(idSuffix, "*") <= numberOfLevelsToExpand) {
				
				subTreeName = currentElement.id.substring(8);
				
				image = document.getElementById("treeViewImage" + subTreeName);
				
				link = image.parentNode;
				if (link.href.indexOf("javascript://") != -1) {
					// il s'agit d'un javascript
					// => les sous-menus ont été chargés dans la page HTML
					// => on peut procéder au changement
					
					if (image.src.indexOf("Close")!=-1) {
						reg=new RegExp("Close", "g");
						image.src = image.src.replace(reg, "Open");
					} else {
						reg=new RegExp("Open", "g");
						image.src = image.src.replace(reg, "Close");
					}
				
					if (document.getElementById("treeView" + subTreeName).innerHTML == "") {
						//return true;
					} else {
						changeMenu("treeView" + subTreeName);
					}
				}
			}
		}
	}
}

function countStringOccurence(stringToTest, occurenceToCount) {
	index = stringToTest.indexOf(occurenceToCount);
	if (stringToTest.indexOf(occurenceToCount) != -1) {
		/*document.write((index + occurenceToCount.length) + "<br/>");
		document.write(stringToTest.substring(index + occurenceToCount.length) + "<br/>");
		occ = countStringOccurence(stringToTest.substring(index + occurenceToCount.length) , occurenceToCount);
		document.write(occ + "<br/>");*/
		return 1 + countStringOccurence(stringToTest.substring(index + occurenceToCount.length) , occurenceToCount);
	} else {
		return 0;
	}
}

/**
 * Popup code
 */
function openpopup(form, popup, width, height, e) {
	var xx, yy;
	xx = e.screenX;
	yy = e.screenY;
	window.open('about:blank', 'popup', 'directories=0, location=0, menubar=0, status=0, toolbar=0, width=' + width + ', height=' + height + ', top=' + yy + ', left=' + xx); 	
	var action = form.action;
	var target = form.target;
	if (popup == null || popup == "") {
		popup = action;
	}
	form.target='popup';
	form.action = popup
	form.submit();
	form.target = target;
	form.action = action;
		
	return false;
}

function closepopup(form, openerField, popupField) {
	var inputField = form[popupField];
	var value;
	if (inputField.options) {
		value = inputField.options[form[popupField].selectedIndex].value;
	} else {
		for (i=0; i < form.elements.length; i++) {
			var element = form.elements[i];
			if (element.name == popupField && element.checked) {
				value = element.value;
				break;
			}
		}
	}
	window.opener.document.forms[0][openerField].value = value;
	window.close();
}

/**
 * form changes detect code
 */
function checkFormChange(link, text) {
  var ok = true;
  for (var form=0; form < document.forms.length; form++) {
    what = document.forms[form];
    for (var i=0, j=what.elements.length; i<j; i++) {

        if (what.elements[i].type == "checkbox" || what.elements[i].type == "radio") {
            if (what.elements[i].checked != what.elements[i].defaultChecked) {
		ok = false; break;
	    }
	} else if (what.elements[i].type == "text" || what.elements[i].type == "hidden" || what.elements[i].type == "password" || what.elements[i].type == "textarea") {
            if (what.elements[i].value != what.elements[i].defaultValue) {
		ok = false; break;
	    }
	} else if (what.elements[i].type == "select-one" || what.elements[i].type == "select-multiple") {
		for (var k=0, l=what.elements[i].options.length; k<l; k++) {
			if (what.elements[i].options[k].selected != what.elements[i].options[k].defaultSelected) {
				ok = false; break;
			}
		}
	} else if (what.elements[i].type == "submit") {
		continue;	    
	}  else if (what.elements[i].type == "file") {
		if (what.elements[i].value !=null && what.elements[i].value!="") {
			ok = false;
			break;			
		}			
	} else {
		alert(what.elements[i].type);
	}
    }
  }
    if (ok) {	
	window.location.href = link;
	return;
    }
    if (confirm(text == null ? "Data will be lost. Continue ?" : text)) {
	window.location.href = link;
	return;
    }
}