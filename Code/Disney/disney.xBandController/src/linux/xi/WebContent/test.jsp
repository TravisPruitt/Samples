<%@ page import="java.util.Enumeration" %>
<%@ page import="com.disney.xband.ac.lib.IXbPrincipal" %>
<html>
   <head>
      <title>xAG Test Page</title>
      <style>
      <jsp:include page="style.css" flush="true"/>
      </style>
   </head>
   <body>
      <h1>xAG Test Page</h1>
      <h2>User Information</h2>
      <%
         IXbPrincipal p = (IXbPrincipal) request.getUserPrincipal();
      %>
      <p>
          <% if(p != null) { %>
          User Name: <%= p.getName() %>
          <% } %>
      </p>
      <p>
          <% if(p != null) { %>
          Display Name: <%= p.getDisplayName() %>
          <% } %>
      </p>
      <p>
          <% if(p != null) { %>
          User Roles: <%= p.getRoles() %>
          <% } %>
      </p>
      <p>
          <% if(p != null) { %>
          Allowed Functional Areas: <%= p.getAssets() %>
          <% } %>
      </p>
      <p>
          <% if(p != null) { %>
          Deny Functional Areas: <%= p.getDenyAssets() %>
          <% } %>
      </p>
      <h2>HTTP Headers</h2>
      <table border="1" cellpadding="4" cellspacing="0">
      <%
         Enumeration eNames = request.getHeaderNames();
         while (eNames.hasMoreElements()) {
            String name = (String) eNames.nextElement();
            String value = normalize(request.getHeader(name));
      %>
         <tr><td><%= name %></td><td><%= value %></td></tr>
      <%
         }
      %>
      </table>
   </body>
</html>
<%!
   private String normalize(String value)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         sb.append(c);
         if (c == ';')
            sb.append("<br>");
      }
      return sb.toString();
   }
%>
