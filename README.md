<h1>Service "report"</h1>
<div>
<i>Service pour l'application Diabete Detector.</i>
<br>
Ce service contient les méthodes de détermination du niveau de risque d'un patient concernant le diabète.
</div>
<hr>
<div>
<ul>
<li> Projet Spring boot 2.5.1 Maven</li>
<li> Java 8</li>
</ul>
</div>
<hr>
<div>
<h2>Docker</h2>
<br>
<ul>
<li> création du jar : mvn package</li>
<li> création de l’image docker via la commande : <b>docker build --tag=report:latest .</b> (le tag doit être en miniscule)</li>
<li> lancement de l’image via la commande : <b>docker run -p8080:8080 report:latest</b></li>
</ul>
→ elle tournera sur le port 8080 <b>http://localhost:8080</b>

</div>

