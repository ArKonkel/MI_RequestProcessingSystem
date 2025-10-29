--------------------------------- EXPERTISE ------------------------------------------------
INSERT INTO expertise (id, name, description)
VALUES
-- ERP-Module
(1, 'Finanzen', 'Fachwissen in Finanzbuchhaltung, Controlling und Bilanzierung mit ERP-Systemen.'),
(2, 'Personalwesen', 'Kenntnisse in Personalverwaltung, Lohn- und Gehaltsabrechnung sowie HR-Prozessen im ERP-Kontext.'),

-- Technisches Wissen
(3, 'Datenbanken', 'Fachwissen in relationalen und Cloud-basierten Datenbanken für ERP-Systeme.'),
(4, 'Schnittstellen', 'Kenntnisse in API-Design, Middleware und Systemintegration im ERP-Kontext.'),
(5, 'Cloud-Infrastruktur', 'Erfahrung mit Cloud-Betrieb von ERP-Systemen, inkl. Skalierung und Sicherheit.'),
(6, 'Integrationen', 'Fachwissen in der Anbindung externer Systeme, z. B. E-Commerce, BI oder Finanzsysteme.'),

-- Implementierungserfahrung
(7, 'Customizing', 'Kenntnisse in der Anpassung von ERP-Systemen an kundenspezifische Anforderungen.'),
(8, 'Rollouts', 'Praxis in der Planung und Durchführung von ERP-Systemeinführungen in Organisationen.'),

-- Support & Troubleshooting
(9, 'Fehleranalyse', 'Know-how in der Analyse und Behebung von Störungen im ERP-Betrieb.'),
(10, 'Performanceoptimierung', 'Erfahrung in der Optimierung von ERP-Systemen hinsichtlich Geschwindigkeit und Skalierbarkeit.'),
(11, 'Updates', 'Fachwissen in Release-Management, Patching und Versionswechseln bei ERP-Systemen.');

--------------------------------- DEPARTMENT ------------------------------------------------
INSERT INTO department (id, name, description)
VALUES
    (1, 'Entwicklung', 'Abteilung für Software- und Produktentwicklung'),
    (2, 'SystemService', 'Abteilung für IT-Systembetreuung und Infrastruktur'),
    (3, 'Projekte', 'Abteilung für Projektmanagement und -koordination'),
    (4, 'ProjectSpecialists', 'Backoffice-Abteilung für Projektunterstützung'),
    (5, 'Support', 'Abteilung für Kunden- und Anwendersupport');

--------------------------------- CALENDAR ------------------------------------------------
INSERT INTO calendar(id)
VALUES
    (1),
    (2),
    (3),
    (4),
    (5);
--------------------------------- CALENDAR_ENTRY ------------------------------------------------
INSERT INTO calendar_entry(id, calendar_id, date, duration_in_minutes, title)
VALUES
    (1, 1, CURRENT_TIMESTAMP, 300, 'Teammeeting Entwicklung'),
    (2, 1, CURRENT_TIMESTAMP - INTERVAL '1 day', 240, 'Projektplanung'),
    (3, 2, CURRENT_TIMESTAMP - INTERVAL '2 day', 180, 'Support-Schulung');

SELECT setval('calendar_entry_seq', (SELECT MAX(id) FROM calendar_entry));

--------------------------------- EMPLOYEE ------------------------------------------------
INSERT INTO employee(id, email, first_name, last_name, department_id, working_hours_per_day, calendar_id)
VALUES
    (1, 'max.mustermann@mail.de', 'Max', 'Mustermann', 1, 8, 1),
    (2, 'sabine.mustermann@mail.de', 'Sabine', 'Musterfrau', 1, 8, 2),
    (3, 'thomas.schneider@firma.de', 'Thomas', 'Schneider', 2, 8, 3),
    (4, 'lisa.berger@firma.de', 'Lisa', 'Berger', 3, 8, 4),
    (5, 'julia.fischer@firma.de', 'Julia', 'Fischer', 5, 8, 5);

--------------------------------- CUSTOMER ------------------------------------------------
INSERT INTO customer(id, first_name, last_name, address, email)
VALUES
    (1, 'WeinfestAG', 'Biedemeyer', 'In den Dorfwiesen 18', 'biedemeyer@gmx.de'),
    (2, 'BaumAG', 'Holzmann', 'Industriestraße 44', 'holzmann@baumag.de'),
    (3, 'Autohaus', 'Krause', 'Automeile 9', 'info@autohaus-krause.de'),
    (4, 'Modehandel', 'Weber', 'Einkaufspark 3', 'kontakt@mode-weber.de');

--------------------------------- ROLE ------------------------------------------------
INSERT INTO role (id, name, description)
VALUES
    (1, 'ADMIN', 'Hat Zugriff auf alles'),
    (2, 'CUSTOMER', 'Ist ein Kunde'),
    (3, 'CUSTOMER_REQUEST_REVISER', 'Bearbeitet und leitet Kundenanfragen weiter'),
    (4, 'CAPACITY_PLANNER', 'Plant Kapazitäten'),
    (5, 'TASK_REVISER', 'Bearbeitet zugewiesene Aufgaben'),
    (6, 'PROJECT_PLANNER', 'Plant Projekte');

--------------------------------- USER ------------------------------------------------
INSERT INTO users (id, name, password, employee_id)
VALUES
    (1, 'Max Mustermann', '{noop}Gandalf', 1),
    (2, 'Sabine Musterfrau', '{noop}Gandalf', 2),
    (3, 'Thomas Schneider', '{noop}Gandalf', 3),
    (4, 'Lisa Berger', '{noop}Gandalf', 4),
    (5, 'Julia Fischer', '{noop}Gandalf', 5);

INSERT INTO users(id, name, password, customer_id)
VALUES
    (6, 'WeinfestAG', '{noop}Gandalf', 1),
    (7, 'BaumAG', '{noop}Gandalf', 2),
    (8, 'Autohaus Krause', '{noop}Gandalf', 3);

SELECT setval('users_seq', (SELECT MAX(id) FROM users));

--------------------------------- USER_ROLES ------------------------------------------------
INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 3),
    (3, 4),
    (4, 6),
    (5, 5),
    (6, 2),
    (7, 2),
    (8, 2);

--------------------------------- EMPLOYEE_EXPERTISE ------------------------------------------------
INSERT INTO employee_expertise(id, employee_id, expertise_id, level)
VALUES
    (1, 1, 7, 'EXPERT'),
    (2, 1, 9, 'ADVANCED'),
    (3, 1, 6, 'INTERMEDIATE'),
    (4, 2, 3, 'EXPERT'),
    (5, 2, 4, 'EXPERT'),
    (6, 2, 7, 'INTERMEDIATE'),
    (7, 3, 3, 'EXPERT'),
    (8, 3, 5, 'ADVANCED'),
    (9, 4, 8, 'ADVANCED'),
    (10, 4, 7, 'EXPERT'),
    (11, 5, 9, 'EXPERT'),
    (12, 5, 10, 'INTERMEDIATE');

SELECT setval('employee_expertise_seq', (SELECT MAX(id) FROM employee_expertise));

--------------------------------- PROCESS_ITEM (TASKS, REQUESTS, PROJECTS) ------------------------------------------------
INSERT INTO process_item (id, title, description, creation_date)
VALUES
-- EXISTIERENDE BEISPIELE
(1, 'Customizing der Software beim Kunden für Produktdruck', 'Individuelle Anpassung an Produktionsprozesse.', '2025-09-10'),
(2, 'Implementierung einer neuen Druckpipeline', 'Neues Druckmodul in bestehendes System integrieren.', '2025-09-11'),
(3, 'Fehleranalyse in Produktionsumgebung', 'Kritischen Bug in Logikschicht finden und beheben.', '2025-09-12'),
(4, 'Onboarding neuer Mitarbeiter', 'Schulung und Dokumentation bereitstellen.', '2025-09-13'),
(5, 'Review Sicherheitskonzept', 'Überprüfung der aktuellen Sicherheitsmaßnahmen.', '2025-09-14'),
(6, 'Anfrage Lagerschulung', 'Wir möchten eine Lagerschulung haben.', '2025-09-14'),
(7, 'Idee zur Verbesserung von XX', 'Wir haben hier einen Verbesserungsvorschlag.', '2025-09-14'),
(8, 'Scannen funktioniert nicht mehr seit Update', 'Seit dem letzten Update funktioniert das Scannen nicht.', '2025-09-14'),
(9, 'Project zur Auslieferung einer PWA', 'Auslieferung der PWA bei Kunde XY', '2025-09-14'),
(10, 'Verbesserung der Teamkommunikation', 'Teambuildingmaßnahme', '2025-09-14'),
(11, 'Softwareinstallation bei BaumAG', 'Installation des ERP-Clients in der IT-Umgebung.', '2025-09-14'),
(12, 'Erstellung einer Serverinfrastruktur', 'Planung und Aufbau neuer Server für interne Nutzung.', '2025-09-14'),
(13, 'Planung des Weihnachtsfestes', 'Organisation der Weihnachtsfeier im Dezember.', '2025-09-14'),

(14, 'ERP-Update Anfrage', 'Wir möchten unser ERP-System auf die neue Version aktualisieren.', '2025-10-01'),
(15, 'Fehler bei der Rechnungserstellung', 'Beim Generieren der PDF-Rechnung tritt ein Fehler auf.', '2025-10-05'),
(16, 'Schulung für neue Mitarbeiter', 'Unsere neuen Mitarbeiter sollen im Lager-ERP geschult werden.', '2025-10-07'),
(17, 'Vorschlag: Dashboard-Verbesserung', 'Bitte mehr Filtermöglichkeiten im Dashboard.', '2025-10-09'),
(18, 'Analyse des Update-Aufwands', 'Überprüfung der notwendigen Änderungen für das ERP-Update.', '2025-10-02'),
(19, 'Datenbankmigration vorbereiten', 'Migration der Kundenstammdaten vorbereiten.', '2025-10-03'),
(20, 'Bugfix Rechnungserstellung', 'Fehler in der PDF-Generierung lokalisieren und beheben.', '2025-10-06'),
(21, 'Dashboard Filterfunktion erweitern', 'Frontend-Komponente für Filterung anpassen.', '2025-10-10'),
(22, 'ERP-Update Projekt BaumAG', 'Projekt zur Durchführung des ERP-Updates bei BaumAG.', '2025-10-05'),
(23, 'Dashboard-Verbesserung Projekt', 'Umsetzung der vorgeschlagenen UI-Optimierungen.', '2025-10-10');

--------------------------------- CUSTOMER REQUEST ------------------------------------------------
INSERT INTO customer_request (id, priority, chargeable, estimated_scope, scope_unit, category, customer_id, status, classified_as_project)
VALUES
    (6, 'LOW', 'NOT_DETERMINED', 0, 'HOUR', 'TRAINING_REQUEST', 1, 'RECEIVED', 'YES'),
    (7, 'MEDIUM', 'NOT_DETERMINED', 0, 'HOUR', 'SUGGESTION_FOR_IMPROVEMENT', 1, 'WAITING_FOR_PROCESSING', 'YES'),
    (8, 'HIGH', 'NOT_DETERMINED', 0, 'HOUR', 'BUG_REPORT', 1, 'RECEIVED', 'NOT_DETERMINED'),
    (14, 'HIGH', 'YES', 16, 'HOUR', 'OTHER', 2, 'IN_PROCESS', 'YES'),
    (15, 'HIGH', 'NOT_DETERMINED', 4, 'HOUR', 'BUG_REPORT', 3, 'RECEIVED', 'NOT_DETERMINED'),
    (16, 'LOW', 'YES', 8, 'HOUR', 'TRAINING_REQUEST', 4, 'RECEIVED', 'NOT_DETERMINED'),
    (17, 'MEDIUM', 'NO', 2, 'HOUR', 'SUGGESTION_FOR_IMPROVEMENT', 1, 'WAITING_FOR_PROCESSING', 'YES');

--------------------------------- TASK ------------------------------------------------
INSERT INTO task (id, estimated_time, estimation_unit, due_date, priority, status, request_id, working_time_in_minutes, is_already_planned)
VALUES
    (1, 2, 'HOUR', '2025-11-05', 'HIGH', 'OPEN', 6, 0, false),
    (2, 4, 'HOUR', '2025-10-20', 'MEDIUM', 'OPEN', 6, 0, false),
    (3, 1, 'HOUR', '2025-09-30', 'HIGH', 'OPEN', 7, 0, false),
    (4, 3, 'HOUR', '2025-10-15', 'LOW', 'OPEN', 7, 0, false),
    (5, 5, 'HOUR', '2025-12-01', 'MEDIUM', 'OPEN', 8, 0, false),
    (18, 4, 'HOUR', '2025-10-20', 'MEDIUM', 'OPEN', 14, 0, false),
    (19, 6, 'HOUR', '2025-10-22', 'HIGH', 'OPEN', 14, 0, false),
    (20, 3, 'HOUR', '2025-10-25', 'HIGH', 'OPEN', 15, 0, false),
    (21, 2, 'HOUR', '2025-11-01', 'LOW', 'OPEN', 17, 0, false);

--------------------------------- TASK_EXPERTISE ------------------------------------------------
INSERT INTO task_expertise (task_id, expertise_id)
VALUES
    (1, 7), (1, 9),
    (2, 7), (2, 9),
    (3, 7), (3, 9),
    (4, 7), (4, 9),
    (5, 7), (5, 9),
    (18, 11),
    (19, 3), (19, 5),
    (20, 9),
    (21, 7);

--------------------------------- PROJECT ------------------------------------------------
INSERT INTO project (id, end_date, start_date, request_id, status)
VALUES
    (9, '2025-11-05', '2025-09-26', 6, 'CREATED'),
    (10, '2025-11-05', '2025-09-26', 7, 'CREATED'),
    (11, '2025-11-05', '2025-09-26', 7, 'CREATED'),
    (12, '2025-11-05', '2025-09-26', 7, 'CREATED'),
    (13, '2025-11-05', '2025-09-26', 7, 'CREATED'),
    (22, '2025-11-15', '2025-10-10', 14, 'IN_PROGRESS'),
    (23, '2025-11-05', '2025-10-15', 17, 'CREATED');

--------------------------------- PROJECT_DEPENDENCIES ------------------------------------------------
INSERT INTO project_dependency(id, type, source_project_id, target_project_id)
VALUES
    (1, 'FINISH_TO_START', 9, 10),
    (2, 'FINISH_TO_START', 22, 23);

SELECT setval('project_dependency_seq', (SELECT MAX(id) FROM project_dependency));
SELECT setval('process_item_seq', (SELECT MAX(id) FROM process_item));
