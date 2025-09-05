--------------------------------- COMPETENCES ------------------------------------------------

INSERT INTO competence (id, name, description, dtype) VALUES
-- ERP-Module
(1, 'Finanzen', 'Fachwissen in Finanzbuchhaltung, Controlling und Bilanzierung mit ERP-Systemen.', 'Expertise'),
(2, 'Personalwesen', 'Kenntnisse in Personalverwaltung, Lohn- und Gehaltsabrechnung sowie HR-Prozessen im ERP-Kontext.', 'Expertise'),

-- Technisches Wissen
(3, 'Datenbanken', 'Fachwissen in relationalen und Cloud-basierten Datenbanken für ERP-Systeme.', 'Expertise'),
(4, 'Schnittstellen', 'Kenntnisse in API-Design, Middleware und Systemintegration im ERP-Kontext.', 'Expertise'),
(5, 'Cloud-Infrastruktur', 'Erfahrung mit Cloud-Betrieb von ERP-Systemen, inkl. Skalierung und Sicherheit.', 'Expertise'),
(6, 'Integrationen', 'Fachwissen in der Anbindung externer Systeme, z. B. E-Commerce, BI oder Finanzsysteme.', 'Expertise'),

-- Implementierungserfahrung
(7, 'Customizing', 'Kenntnisse in der Anpassung von ERP-Systemen an kundenspezifische Anforderungen.', 'Expertise'),
(8, 'Rollouts', 'Praxis in der Planung und Durchführung von ERP-Systemeinführungen in Organisationen.', 'Expertise'),

-- Support & Troubleshooting
(9, 'Fehleranalyse', 'Know-how in der Analyse und Behebung von Störungen im ERP-Betrieb.', 'Expertise'),
(10, 'Performanceoptimierung', 'Erfahrung in der Optimierung von ERP-Systemen hinsichtlich Geschwindigkeit und Skalierbarkeit.', 'Expertise'),
(11, 'Updates', 'Fachwissen in Release-Management, Patching und Versionswechseln bei ERP-Systemen.', 'Expertise');

INSERT INTO competence (id, name, description, dtype, obtained_date) VALUES
-- Entwicklungs-/Technologie-Zertifikate
(12, 'Java SE 11 Developer', 'Zertifizierung für Java-Programmierung, relevant für ERP-Entwicklung und Schnittstellen.', 'Qualification', DATE '2022-02-15'),
(13, 'Oracle Database SQL Certified Associate', 'Zertifizierung für Datenbankkenntnisse und SQL-Abfragen in ERP-Systemen.', 'Qualification', DATE '2021-08-30'),
(14, 'AWS Developer – Associate', 'Zertifizierung für Cloud-Entwicklung und Deployment von ERP-Lösungen auf AWS.', 'Qualification', DATE '2023-01-12'),
(15, 'Microsoft Azure Developer Associate', 'Zertifizierung für Entwicklung und Integration von ERP-Anwendungen in Microsoft Azure.', 'Qualification', DATE '2022-11-05'),

-- Scrum- / Agile-Zertifikate
(16, 'Certified Scrum Master (CSM)', 'Agile Projektmanagement-Zertifizierung für ERP-Implementierungen.', 'Qualification', DATE '2021-04-20'),
(17, 'Professional Scrum Product Owner (PSPO I)', 'Zertifizierung für Produktverantwortliche in agilen ERP-Projekten.', 'Qualification', DATE '2022-07-14'),
(18, 'SAFe Agilist', 'Zertifizierung für Skalierung agiler Methoden in großen ERP-Projekten.', 'Qualification', DATE '2020-09-28');


--------------------------------- DEPARTMENT ------------------------------------------------

INSERT INTO department (id, name, description) VALUES
(1, 'Entwicklung', 'Abteilung für Software- und Produktentwicklung'),
(2, 'SystemService', 'Abteilung für IT-Systembetreuung und Infrastruktur'),
(3, 'Projekte', 'Abteilung für Projektmanagement und -koordination'),
(4, 'ProjectSpecialists', 'Backoffice-Abteilung für Projektunterstützung'),
(5, 'Support', 'Abteilung für Kunden- und Anwendersupport');
--------------------------------- PERMISSION ------------------------------------------------

INSERT INTO permission (id, name, description) VALUES
(1, 'Projektplanung', 'Befugnis Projekte zu planen'),
(2, 'Kapazitätsplanung', 'Befugnis Kapazitäten einzusehen und Aufgaben zuzuweisen'),
(3, 'Aufgabenbearbeitung', 'Befugnis Aufgaben zu bearbeiten'),
(4, 'Anfrageeingangsprüfung', 'Befugnis Anfragen zu korrigieren und weiterzuleiten');

--------------------------------- ROLE ------------------------------------------------

INSERT INTO role (id, name, description) VALUES
(1, 'Projektplaner', 'Plant Projekte'),
(2, 'Anfragenbearbeiter', 'Nimmt Anfragen entgegen und leitet diese weiter.'),
(3, 'Kapazitätsplaner', 'Plant Kapazitäten bei Aufgabenzuweisungen'),
(4, 'Aufgabenbearbeiter', 'Bearbeitet aufgaben'),
(5,'Admin', 'Hat Zugriff auf alles');

--------------------------------- ROLE_PERMISSION ------------------------------------------------

INSERT INTO role_permission(role_id, permission_id) VALUES
(1, 1),
(2, 4),
(3, 2),
(4, 3),
(5, 1),
(5, 2),
(5, 3),
(5, 4);

--------------------------------- EMPLOYEE ------------------------------------------------

INSERT INTO employee(id, email, first_name, last_name, phone_number, department_id) VALUES
(1, 'gandalf@mittelerde.de', 'Gandalf', 'der Graue', '', null),
(2,'Frodo@mittelerde.de', 'Frodo', 'Beutlin', '', 1);

--------------------------------- USER ------------------------------------------------

INSERT INTO users (id, name, description, employee_id) VALUES
(1, 'Gandalf der Graue', 'Kann alles. Ist eine Zauberer.', 1),
(2, 'Frodo Beutlin', 'Ein lustiger Hobbit', 2);

--------------------------------- USER_ROLES ------------------------------------------------

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 5),
(2, 3),
(2, 4);

--------------------------------- employee_expertise ------------------------------------------------
INSERT INTO employee_expertise(id, employee_id, expertise_id, level) VALUES
(1, 1, 7, 'EXPERT'),
(2, 1, 9, 'ADVANCED'),
(3, 1, 6, 'INTERMEDIATE'),
(4, 2, 3, 'EXPERT'),
(5,2,4, 'EXPERT'),
(6,2,11, 'INTERMEDIATE');





