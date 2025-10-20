# MediTrack



\*\*CI Pipeline mit Github Actions\*\*

Die Pipeline wird über ci.yml im Repository beschrieben.

Der Workflow wird bei jedem Push auf jeden Branch und jede Pull Request in main gestartet.

Für den Test wird der Repository überprüft, eine Java-Umgebung eingerichtet, Maven-Dependencies gecached, das Projekt gebaut und alle JUnit-Tests ausgeführt. Bei erfolgreichen Tests wird eine Dokumentation erstellt.

Durchgeführte Tests:

Build

Unit-Test

Integrationstest

Deployment-Test

