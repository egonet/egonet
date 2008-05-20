<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">

<%--
 * <p>Title: Egocentric Networks Web Client</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 *  $Id: tutorial_complete.jsp,v 1.1 2004/05/26 12:35:47 admin Exp $
 --%>

<%@ page import="java.util.*" %>
<%@ page import="org.jboss.logging.Logger" %>
<%@ page import="com.endlessloopsoftware.egonet.web.WebShared" %>
<%@ page import="com.endlessloopsoftware.egonet.Shared" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>

<%
		Logger logger = Logger.getLogger(this.getClass().getName());
		logger.debug("enter");
%>

<layout:html locale="true" styleClass="FORM" key='global.title'>

	<html:errors property="org.apache.struts.action.GLOBAL_ERROR"/>

	<layout:form action="/tutorialSubmit" styleClass="FORM" key="tutorial_complete.title">
	
		You have completed the Survey Tutorial. Now you may begin the survey.
		
		<layout:formActions>
			<layout:submit>
				<layout:message key="tutorial_complete.button.submit"/>
			</layout:submit>
		</layout:formActions>
		 
	</layout:form>
	
</layout:html>