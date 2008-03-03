<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">

<%--
 * <p>Title: Egocentric Networks Web Client</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 *  $Id: welcome.jsp,v 1.5 2004/05/26 12:35:47 admin Exp $
 --%>

<%@ page import="org.jboss.logging.Logger" %>
<%@ page import="fr.improve.struts.taglib.layout.util.LayoutUtils" %>
<%@ page import="com.endlessloopsoftware.egonet.web.WebShared" %>

<%@ page language="java" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>

<%
		LayoutUtils.init(pageContext.getServletContext());
		Logger logger = Logger.getLogger(this.getClass().getName());
		logger.debug("enter");
%>

<layout:html locale="true" key="global.title" styleClass="FORM">

	<logic:notPresent name="<%= WebShared.STUDY_KEY %>" scope="application">
	  <font color="red">
	    ERROR:  Survey not loaded -- check servlet container logs for error messages.
	  </font>
	  <hr>
	</logic:notPresent>
	
	<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
	  <font color="red">
	    ERROR:  Application resources not loaded -- check servlet container logs for error messages.
	  </font>
	</logic:notPresent>
	
	<h3><layout:message key="welcome.heading"/></h3>
	<layout:menu styleClass="FORM" align="center" width="0">
	        <layout:menuItem key="welcome.logon" link="/EgoWeb/egoname.do"/>
	</layout:menu>
	<p>&nbsp;</p>	
	
	<P>
		<B>
			This survey requires Java 1.4.0 or higher to be installed on your computer.<br/>
			If you do not have it installed you can download java <A HREF="http://www.java.com">Here</A>
		</B>
	</P>
	
	<A HREF="www.apple.com">
		<html:img page="/images/poweredbydarwin.gif" alt="Made with Macintosh"/>
	</A>

</layout:html>
