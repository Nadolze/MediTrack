# MediTrack



## CI/CD-Pipeline

Diese Projekt verwendet GitHub Actions.



1. **Build \& Unit-Tests**

&nbsp;  - Jede Änderung an beliebigen Branches (`push`) wird automatisch gebaut.

&nbsp;  - Unit-Tests werden mit JUnit 5 ausgeführt.

&nbsp;  - Maven wird verwendet, um das Projekt zu bauen:  




2\. **Integrationstests**

&nbsp;  - Werden im Profil integration-test ausgeführt.

&nbsp;  - Prüfen das Zusammenspiel von Komponenten, z. B. Services mit Repositories.




3\. **Dokumentation**

&nbsp;  - Wenn Änderungen im main-Branch gemerged werden, wird automatisch \*\*Javadoc\*\* generiert.

&nbsp;  - Das Ergebnis wird als Artefakt hochgeladen: target/site/apidocs.

