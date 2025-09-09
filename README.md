# RequestProcessingSystem
Dieses Repository enthält das im Rahmen der Bachelorarbeit entwickelte Softwaresystem "RequestProcessingSystem".  
Das Ziel des Projekts ist es, ein System zur Erstellung von Anfragen, Planung von Projekten, Aufgaben und Kapazitäten zu entwerfen und als Prototyp umzusetzen.

---

## Inhalt
- [Hintergrund](#hintergrund)
- [Systemarchitektur](#systemarchitektur)
- [Technologien](#technologien)
- [Voraussetzungen](#voraussetzungen)
- [Installation & Ausführung](#installation--ausführung)
- [Funktionen](#funktionen)
- [Hinweise zur Bachelorarbeit](#hinweise-zur-bachelorarbeit)

---

## Hintergrund
Das System wurde im Rahmen der Bachelorarbeit mit dem Titel **"Anfragenbearbeitungssystem mit integrierter Kapazitätssteuerung"**
von **Artur Konkel** im Zeitraum vom 06.08.2025 – 05.11.2025 an der 
**Hochschule RheinMain - Medieninformatik** in Zusammenarbeit mit dem Unternehmen
**ORGA-SOFT** entwickelt. 

Der Fokus liegt auf der Abbildung von Prozessen zur Anfrageerstellung von Kunden, Projekt- und,
Kapazitätsplanung, sowie Aufgabenbearbeitung und auf der Integration externer Dienste wie der Microsoft Graph API zur
Nutzung von Outlook-Kalenderfunktionen.

---

## Systemarchitektur
Das System folgt den Prinzipien der **Clean Architecture** nach Robert C. Martin.  
Es ist in folgende Hauptkomponenten unterteilt:

- **Backend**: RequestProcessingServer (Spring Boot, Java)
- **Frontend**: RequestClient und WorkstationClient (Vue.js)
- **Externe Integration**: Microsoft Graph API (Outlook-Anbindung)
- **Persistenz**: H2-Datenbank für den Prototyp

Die Kommunikation erfolgt über **REST-Schnittstellen** sowie **STOMP-WebSockets** für Echtzeitbenachrichtigungen.

---

## Technologien
- **Backend**: Spring Boot (Java 21)
- **Frontend**: Vue.js
- **Datenbank**: Postgres (In-Memory)
- **Externe API**: Microsoft Graph API
- **Build-Tool**: Gradle
- **Weitere Tools**: Lombok, MapStruct

---

## Voraussetzungen
- **Java 21** oder höher
- **Node.js** (Version >= 22)
- Internetzugang für Microsoft Graph API-Anbindung

---

## Installation & Ausführung
//How to start Backend

### Backend starten
```bash
./gradlew bootRun
```