<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">

<%--
 * <p>Title: Egocentric Networks Web Client</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 *  $Id: alter_pair_applet.jsp,v 1.4 2004/05/17 00:05:23 admin Exp $
 --%>

<%@ page import="java.util.*" %>
<%@ page import="org.jboss.logging.Logger" %>
<%@ page import="com.endlessloopsoftware.egonet.web.WebShared" %>

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

<%--
	<html:img src="/EgoWeb/drawnetwork" alt="Network Graph Placeholder" />
--%>
	
	<layout:form width="95%" action="/alterpairappletSubmit" styleClass="FORM" key="alterprompt.title">
	
		<bean:define id="position"			name="alterPairAppletForm" property="position" 		type="java.lang.Integer"/>
		<bean:define id="interviewId"		name="alterPairAppletForm" property="interviewId" 	type="java.lang.Long"/>
		<bean:define id="answer"				name="alterPairAppletForm" property="answer"			type="com.endlessloopsoftware.egonet.util.AnswerDataValue"/>
	
		<tr>
			<td colspan="2" align="center">
				<%! String mode = WebShared.LINK_MODE; %>

				<%@ include file="applet.jsp" %>  
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<p>&nbsp;</p>
			</td>
		</tr>

		<tr>
			<td colspan="2">
				<p class="LABEL"><%= answer.getQuestionText() %></p>
			</td>
		</tr>

	 	<tr><td colspan="2">&nbsp</td></tr>
		
		<layout:row>
			<layout:formActions>
				<layout:submit property="submit" value="Submit"/>   
			</layout:formActions>
		 </layout:row>
		 
	</layout:form>

	<html:javascript formName="alterNameForm"/>
 
</layout:html>