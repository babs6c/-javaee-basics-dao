<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Liste</title>
</head>
<body>
<c:forEach items="${ utilisateurs }" var="utilisateur">
	id:<c:out value="${ utilisateur.id }"/>
	<br>
	Nom:<c:out value="${ utilisateur.nom }"/>
	<br>
	Email:<c:out value="${ utilisateur.email }"/>
	<br>
	Pass:<c:out value="${ utilisateur.pass }"/>
	<br>
	Photo:<c:out value="${ utilisateur.photo }"/>
	<br><br><br>
</c:forEach>
</body>
</html>