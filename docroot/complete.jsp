<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">

<%--
 * <p>Title: Egocentric Networks Web Client</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 *  $Id: complete.jsp,v 1.7 2004/05/17 00:05:23 admin Exp $
 --%>

<%@ page import="java.util.*" %>
<%@ page import="org.jboss.logging.Logger" %>
<%@ page import="com.endlessloopsoftware.egonet.web.WebShared" %>
<%@ page import="com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK" %>
<%@ page import="com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>

<layout:html locale="true" styleClass="FORM" key='global.title'>

	<html:errors property="org.apache.struts.action.GLOBAL_ERROR"/>

	<tr>
		<td colspan="2" align="center">

			<%
				InterviewEJBLocal interview 	= WebShared.retrieveInterview(request);
				Integer 				position 		= new Integer(interview.getAlters().length - 1);
				Long					interviewId 	= ((InterviewEJBPK) interview.getPrimaryKey()).getId();
				String 				mode 			= WebShared.ALTER_QUESTION_MODE;
			%>

			<%@ include file="applet.jsp" %>  
		</td>
	</tr>
	
	<P class="LABEL">
		You have completed this survey. Thank you for your time.
	</P> 
	<P>&nbsp</P>
	
</layout:html>