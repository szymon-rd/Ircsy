<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@page session="true" %>
<html>
<head>
    <title>Ircsy</title>
</head>
<body>
    <div id="header">
        <t:insertAttribute name="header"/>
    </div>
    <div id="body">
        <t:insertAttribute name="body"/>
    </div>
    <div>
        <t:insertAttribute name="footer"/>
    </div>
</body>
</html>