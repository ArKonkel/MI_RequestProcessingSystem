INSERT INTO competence (id, name, description, dtype) VALUES
-- ERP-Module
(nextval('competence_seq'), 'Finanzen', 'Fachwissen in Finanzbuchhaltung, Controlling und Bilanzierung mit ERP-Systemen.', 'Expertise'),
(nextval('competence_seq'), 'Personalwesen', 'Kenntnisse in Personalverwaltung, Lohn- und Gehaltsabrechnung sowie HR-Prozessen im ERP-Kontext.', 'Expertise'),

-- Technisches Wissen
(nextval('competence_seq'), 'Datenbanken', 'Fachwissen in relationalen und Cloud-basierten Datenbanken für ERP-Systeme.', 'Expertise'),
(nextval('competence_seq'), 'Schnittstellen', 'Kenntnisse in API-Design, Middleware und Systemintegration im ERP-Kontext.', 'Expertise'),
(nextval('competence_seq'), 'Cloud-Infrastruktur', 'Erfahrung mit Cloud-Betrieb von ERP-Systemen, inkl. Skalierung und Sicherheit.', 'Expertise'),
(nextval('competence_seq'), 'Integrationen', 'Fachwissen in der Anbindung externer Systeme, z. B. E-Commerce, BI oder Finanzsysteme.', 'Expertise'),

-- Implementierungserfahrung
(nextval('competence_seq'), 'Customizing', 'Kenntnisse in der Anpassung von ERP-Systemen an kundenspezifische Anforderungen.', 'Expertise'),
(nextval('competence_seq'), 'Rollouts', 'Praxis in der Planung und Durchführung von ERP-Systemeinführungen in Organisationen.', 'Expertise'),

-- Support & Troubleshooting
(nextval('competence_seq'), 'Fehleranalyse', 'Know-how in der Analyse und Behebung von Störungen im ERP-Betrieb.', 'Expertise'),
(nextval('competence_seq'), 'Performanceoptimierung', 'Erfahrung in der Optimierung von ERP-Systemen hinsichtlich Geschwindigkeit und Skalierbarkeit.', 'Expertise'),
(nextval('competence_seq'), 'Updates', 'Fachwissen in Release-Management, Patching und Versionswechseln bei ERP-Systemen.', 'Expertise');


INSERT INTO competence (id, name, description, dtype, obtained_date) VALUES
-- Entwicklungs-/Technologie-Zertifikate
(nextval('competence_seq'), 'Java SE 11 Developer', 'Zertifizierung für Java-Programmierung, relevant für ERP-Entwicklung und Schnittstellen.', 'Qualification', DATE '2022-02-15'),
(nextval('competence_seq'), 'Oracle Database SQL Certified Associate', 'Zertifizierung für Datenbankkenntnisse und SQL-Abfragen in ERP-Systemen.', 'Qualification', DATE '2021-08-30'),
(nextval('competence_seq'), 'AWS Developer – Associate', 'Zertifizierung für Cloud-Entwicklung und Deployment von ERP-Lösungen auf AWS.', 'Qualification', DATE '2023-01-12'),
(nextval('competence_seq'), 'Microsoft Azure Developer Associate', 'Zertifizierung für Entwicklung und Integration von ERP-Anwendungen in Microsoft Azure.', 'Qualification', DATE '2022-11-05'),

-- Scrum- / Agile-Zertifikate
(nextval('competence_seq'), 'Certified Scrum Master (CSM)', 'Agile Projektmanagement-Zertifizierung für ERP-Implementierungen.', 'Qualification', DATE '2021-04-20'),
(nextval('competence_seq'), 'Professional Scrum Product Owner (PSPO I)', 'Zertifizierung für Produktverantwortliche in agilen ERP-Projekten.', 'Qualification', DATE '2022-07-14'),
(nextval('competence_seq'), 'SAFe Agilist', 'Zertifizierung für Skalierung agiler Methoden in großen ERP-Projekten.', 'Qualification', DATE '2020-09-28');
