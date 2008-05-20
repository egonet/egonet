<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">

<%--
 * <p>Title: Egocentric Networks Web Client</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 *  $Id: ego_name.jsp,v 1.3 2004/03/18 15:23:39 admin Exp $
 --%>

<%@ page import="org.jboss.logging.Logger" %>

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
	
	<layout:form action="/egonameSubmit" focus="firstName" onsubmit="return validateEgoNameForm(this)" styleClass="FORM" key="egoname.title">
	 
	 	<layout:text key="Study " property="study.studyName" styleClass="LABEL" mode="S,S,S"/>
	 	<tr>&nbsp</tr>
		<layout:text key="egoname.firstname.displayname" property="firstName" size="24" maxlength="32" isRequired="true" styleClass="LABEL" />
		<layout:text key="egoname.lastname.displayname"  property="lastName" size="24" maxlength="32" isRequired="true" styleClass="LABEL" />

	 	<tr>&nbsp</tr>
	
	 	<tr>&nbsp</tr>

		<layout:row>
			<layout:formActions>
				<layout:submit property="submit" value="Submit"/>   
				<layout:reset/>      
			</layout:formActions>
		 </layout:row>

	</layout:form>
	
	<html:javascript formName="egoNameForm"/>
 
</layout:html>