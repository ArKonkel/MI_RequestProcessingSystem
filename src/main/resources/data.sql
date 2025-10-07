--------------------------------- EXPERTISE ------------------------------------------------

INSERT INTO expertise (id, name, description)
VALUES
-- ERP-Module
(1, 'Finanzen', 'Fachwissen in Finanzbuchhaltung, Controlling und Bilanzierung mit ERP-Systemen.'),
(2, 'Personalwesen',
 'Kenntnisse in Personalverwaltung, Lohn- und Gehaltsabrechnung sowie HR-Prozessen im ERP-Kontext.'),

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
(10, 'Performanceoptimierung',
 'Erfahrung in der Optimierung von ERP-Systemen hinsichtlich Geschwindigkeit und Skalierbarkeit.'),
(11, 'Updates', 'Fachwissen in Release-Management, Patching und Versionswechseln bei ERP-Systemen.');

--------------------------------- DEPARTMENT ------------------------------------------------

INSERT INTO department (id, name, description)
VALUES (1, 'Entwicklung', 'Abteilung für Software- und Produktentwicklung'),
       (2, 'SystemService', 'Abteilung für IT-Systembetreuung und Infrastruktur'),
       (3, 'Projekte', 'Abteilung für Projektmanagement und -koordination'),
       (4, 'ProjectSpecialists', 'Backoffice-Abteilung für Projektunterstützung'),
       (5, 'Support', 'Abteilung für Kunden- und Anwendersupport');
--------------------------------- PERMISSION ------------------------------------------------

INSERT INTO permission (id, name, description)
VALUES (1, 'Projektplanung', 'Befugnis Projekte zu planen'),
       (2, 'Kapazitätsplanung', 'Befugnis Kapazitäten einzusehen und Aufgaben zuzuweisen'),
       (3, 'Aufgabenbearbeitung', 'Befugnis Aufgaben zu bearbeiten'),
       (4, 'Anfrageeingangsprüfung', 'Befugnis Anfragen zu korrigieren und weiterzuleiten');

--------------------------------- ROLE ------------------------------------------------

INSERT INTO role (id, name, description)
VALUES (1, 'Projektplaner', 'Plant Projekte'),
       (2, 'Anfragenbearbeiter', 'Nimmt Anfragen entgegen und leitet diese weiter.'),
       (3, 'Kapazitätsplaner', 'Plant Kapazitäten bei Aufgabenzuweisungen'),
       (4, 'Aufgabenbearbeiter', 'Bearbeitet aufgaben'),
       (5, 'Admin', 'Hat Zugriff auf alles');

--------------------------------- ROLE_PERMISSION ------------------------------------------------

INSERT INTO role_permission(role_id, permission_id)
VALUES (1, 1),
       (2, 4),
       (3, 2),
       (4, 3),
       (5, 1),
       (5, 2),
       (5, 3),
       (5, 4);

--------------------------------- CALENDAR ------------------------------------------------
INSERT INTO calendar(id)
VALUES (1),
       (2);

--------------------------------- CALENDAR_ENTRY ------------------------------------------------
INSERT INTO calendar_entry(id, calendar_id, date, duration_in_minutes, title)
VALUES (1, 1, CURRENT_TIMESTAMP, 300, 'Test Event'),
       (2, 1, CURRENT_TIMESTAMP - INTERVAL '1 day', 240, 'Test Event'),
       (3, 1, CURRENT_TIMESTAMP - INTERVAL '1 day', 240, 'Test Event');

--- Has to be done, so no double key error appears
SELECT setval('calendar_entry_seq', (SELECT MAX(id) FROM calendar_entry));

--------------------------------- EMPLOYEE ------------------------------------------------

INSERT INTO employee(id, email, first_name, last_name, department_id, working_hours_per_day, calendar_id)
VALUES (1, 'Max.Mustermann@mail.de', 'Max', 'Mustermann', null, 8, 1),
       (2, 'Sabine.Mustermann@mail.de', 'Sabine', 'Musterfrau', 1, 8, 2);

--------------------------------- USER ------------------------------------------------

INSERT INTO users (id, name, employee_id)
VALUES (1, 'Max Mustermann', 1),
       (2, 'Sabine Musterfrau', 2);

--------------------------------- USER_ROLES ------------------------------------------------

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 5),
       (2, 3),
       (2, 4);

--------------------------------- EMPLOYEE_EXPERTISE ------------------------------------------------
INSERT INTO employee_expertise(id, employee_id, expertise_id, level)
VALUES (1, 1, 7, 'EXPERT'),
       (2, 1, 9, 'ADVANCED'),
       (3, 1, 6, 'INTERMEDIATE'),
       (4, 2, 3, 'EXPERT'),
       (5, 2, 4, 'EXPERT'),
       (6, 2, 7, 'INTERMEDIATE');


--------------------------------- CUSTOMER ------------------------------------------------

INSERT INTO customer(id, first_name, last_name, address, email)
VALUES (1, 'WeinfestAG', 'Biedemeyer', 'In den Dorfwiesen 18', 'biedemeyer@gmx.de');

--------------------------------- Baseobject - PROCESS_ITEM ------------------------------------------------

-- Process Items
INSERT INTO process_item (id, title, description, creation_date)
--TASKS
VALUES (1, 'Customizing der Software beim Kunden für Produktdruck',
        'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat',
        '2025-09-10'),
       (2, 'Implementierung einer neuen Druckpipeline', 'Neues Druckmodul in bestehendes System integrieren',
        '2025-09-11'),
       (3, 'Fehleranalyse in Produktionsumgebung', 'Kritischen Bug in Logikschicht finden und beheben', '2025-09-12'),
       (4, 'Onboarding neuer Mitarbeiter', 'Schulung und Dokumentation bereitstellen', '2025-09-13'),
       (5, 'Review Sicherheitskonzept', 'Überprüfung der aktuellen Sicherheitsmaßnahmen', '2025-09-14'),
-- REQUESTS
       (6, 'Anfrage Lagerschulung', 'Hallo. Wir möchten eine Lagerschulung haben.', '2025-09-14'),
       (7, 'Idee zur Verbesserung von XX', 'Wir haben hier einen Verbesserungsvorschlag.', '2025-09-14'),
       (8, 'Scannen funktioniert nicht mehr seit Update', 'Seit dem letzten Update funktioniert das scannen nicht.',
        '2025-09-14'),
-- PROJECT
       (9, 'Project zur Auslieferung einer PWA', 'Auslieferung der PWA bei Kunde XY', '2025-09-14'),
       (10, 'Verbesserung der Teamkommunikation', 'Teambuildingmaßnahme', '2025-09-14'),
       (11, 'Sofwareinstallation bei BaumAg', 'Wir müssen bei der ...', '2025-09-14'),
       (12, 'Erstellung einer Serverinfrastruktur', 'Plane für das interne Netz eine neue...', '2025-09-14'),
       (13, 'Planung des Weihnachtsfestes', 'Wir haben am ... unser Weihnachtsfest..', '2025-09-14');


-- Requests (gehören zu den Process Items, gleiche ID wie process_item.id)
INSERT INTO customer_request (id, priority, chargeable, estimated_scope, scope_unit, category, customer_id, status)
VALUES (6, 'LOW', 'NOT_DETERMINED', 0, 'HOUR','TRAINING_REQUEST', 1, 'RECEIVED'),
       (7, 'MEDIUM', 'NOT_DETERMINED', 0, 'HOUR','SUGGESTION_FOR_IMPROVEMENT', 1, 'WAITING_FOR_PROCESSING'),
       (8, 'HIGH', 'NOT_DETERMINED', 0, 'HOUR','BUG_REPORT', 1, 'RECEIVED');

-- Tasks (gehören zu den Process Items, gleiche ID wie process_item.id)
INSERT INTO task (id, estimated_time, estimation_unit, due_date, priority, status, request_id)
VALUES (1, 2, 'HOUR', '2025-11-05', 'HIGH', 'OPEN', 6),
       (2, 4, 'HOUR', '2025-10-20', 'MEDIUM', 'OPEN', 6),
       (3, 1, 'HOUR', '2025-09-30', 'HIGH', 'OPEN', 7),
       (4, 3, 'HOUR', '2025-10-15', 'LOW', 'OPEN', 7),
       (5, 5, 'HOUR', '2025-12-01', 'MEDIUM', 'OPEN', 8);

-- Projects  (gehören zu den Process Items, gleiche ID wie process_item.id)

INSERT INTO project (id, end_date, start_date, request_id, status)
VALUES (9, '2025-11-05', '2025-09-26', 6, 'CREATED'),
       (10, '2025-11-05', '2025-09-26', 7, 'CREATED'),
       (11, '2025-11-05', '2025-09-26', 7, 'CREATED'),
       (12, '2025-11-05', '2025-09-26', 7, 'CREATED'),
       (13, '2025-11-05', '2025-09-26', 7, 'CREATED');

SELECT setval('process_item_seq', (SELECT MAX(id) FROM process_item));
--------------------------------- TASK_EXPERTISE ------------------------------------------------

INSERT INTO task_expertise (task_id, expertise_id)
VALUES (1, 7),
       (1, 9),
       (2, 7),
       (2, 9),
       (3, 7),
       (3, 9),
       (4, 7),
       (4, 9),
       (5, 7),
       (5, 9);



--------------------------------- Project_Dependencies  ------------------------------------------------
INSERT INTO project_dependency(id, type, source_project_id, target_project_id)
VALUES (1, 'FINISH_TO_START', 9,10);

SELECT setval('project_dependency_seq', (SELECT MAX(id) FROM task));
