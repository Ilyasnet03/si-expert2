-- ============================================
-- DONNÉES DE TEST SIMPLES POUR SI-EXPERT
-- ============================================

-- Supprimer les données existantes
DELETE FROM notes_honoraires;
DELETE FROM missions;

-- Les utilisateurs sont créés par DataSeeder avec BCrypt encoding

-- Insérer quelques missions de test (seulement les champs obligatoires)
INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-001', 'POL-001', '1234-AB-78', 'EAD', 'Standard', '0612345678', 'NOUVELLE', CURRENT_TIMESTAMP, 'Mission de test');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-002', 'POL-002', '5678-CD-90', 'Expertise', 'Express', '0698765432', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Mission en cours');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-003', 'POL-003', '1111-EF-12', 'EAD', 'Standard', '0611111111', 'NOUVELLE', CURRENT_TIMESTAMP, 'Nouvelle mission EAD');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-004', 'POL-004', '2222-GH-34', 'Expertise', 'Express', '0622222222', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Expertise en cours');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-005', 'POL-005', '3333-IJ-56', 'Contrôle', 'Standard', '0633333333', 'REFUSEE', CURRENT_TIMESTAMP, 'Mission refusée - motif test');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-006', 'POL-006', '4444-KL-78', 'EAD', 'Express', '0644444444', 'CARENCE', CURRENT_TIMESTAMP, 'Mission en carence');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-007', 'POL-007', '5555-MN-90', 'Expertise', 'Standard', '0655555555', 'CLOTUREE', CURRENT_TIMESTAMP, 'Mission clôturée avec honoraires');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-008', 'POL-008', '6666-OP-12', 'Contrôle', 'Express', '0666666666', 'CLOTUREE', CURRENT_TIMESTAMP, 'Mission clôturée');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-009', 'POL-009', '7777-QR-34', 'EAD', 'Standard', '0677777777', 'NOUVELLE', CURRENT_TIMESTAMP, 'Nouvelle mission urgente');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-010', 'POL-010', '8888-ST-56', 'Expertise', 'Express', '0688888888', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Expertise complémentaire');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-011', 'POL-011', '9999-UV-78', 'EAD', 'Standard', '0699999999', 'NOUVELLE', CURRENT_TIMESTAMP, 'Nouvelle mission EAD');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-012', 'POL-012', 'AAAA-WX-90', 'Contrôle', 'Express', '0600000000', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Contrôle en cours');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-013', 'POL-013', 'BBBB-YZ-12', 'Expertise', 'Standard', '0611111112', 'REFUSEE', CURRENT_TIMESTAMP, 'Refusée - véhicule non retrouvé');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-014', 'POL-014', 'CCCC-AA-34', 'EAD', 'Express', '0622222223', 'CARENCE', CURRENT_TIMESTAMP, 'En attente dépassée');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-015', 'POL-015', 'DDDD-BB-56', 'Contrôle', 'Standard', '0633333334', 'CLOTUREE', CURRENT_TIMESTAMP, 'Contrôle clôturé');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-016', 'POL-016', 'EEEE-CC-78', 'Expertise', 'Express', '0644444445', 'CLOTUREE', CURRENT_TIMESTAMP, 'Mission terminée');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-017', 'POL-017', 'FFFF-DD-90', 'EAD', 'Standard', '0655555556', 'NOUVELLE', CURRENT_TIMESTAMP, 'Nouvelle demande');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-018', 'POL-018', 'GGGG-EE-12', 'Contrôle', 'Express', '0666666667', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Contrôle technique');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-019', 'POL-019', 'HHHH-FF-34', 'Expertise', 'Standard', '0677777778', 'REFUSEE', CURRENT_TIMESTAMP, 'Refusée - dossier incomplet');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-020', 'POL-020', 'IIII-GG-56', 'EAD', 'Express', '0688888889', 'CARENCE', CURRENT_TIMESTAMP, 'Carence administrative');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-021', 'POL-021', 'JJJJ-HH-78', 'Contrôle', 'Standard', '0699999990', 'CLOTUREE', CURRENT_TIMESTAMP, 'Contrôle clôturé');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-022', 'POL-022', 'KKKK-II-90', 'Expertise', 'Express', '0600000001', 'CLOTUREE', CURRENT_TIMESTAMP, 'Expertise clôturée');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-023', 'POL-023', 'LLLL-JJ-12', 'EAD', 'Standard', '0611111113', 'NOUVELLE', CURRENT_TIMESTAMP, 'Nouvelle mission');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-024', 'POL-024', 'MMMM-KK-34', 'Contrôle', 'Express', '0622222224', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Vérification en cours');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-025', 'POL-025', 'NNNN-LL-56', 'Expertise', 'Standard', '0633333335', 'REFUSEE', CURRENT_TIMESTAMP, 'Refusée - sinistre prescrit');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-026', 'POL-026', 'OOOO-MM-78', 'EAD', 'Express', '0644444446', 'CARENCE', CURRENT_TIMESTAMP, 'Délai dépassé');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-027', 'POL-027', 'PPPP-NN-90', 'Contrôle', 'Standard', '0655555557', 'CLOTUREE', CURRENT_TIMESTAMP, 'Contrôle clôturé');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-028', 'POL-028', 'QQQQ-OO-12', 'Expertise', 'Express', '0666666668', 'CLOTUREE', CURRENT_TIMESTAMP, 'Mission finalisée');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-029', 'POL-029', 'RRRR-PP-34', 'EAD', 'Standard', '0677777779', 'NOUVELLE', CURRENT_TIMESTAMP, 'Nouvelle expertise');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-030', 'POL-030', 'SSSS-QQ-56', 'Contrôle', 'Express', '0688888890', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Contrôle qualité');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-031', 'POL-031', 'TTTT-RR-78', 'Expertise', 'Standard', '0699999991', 'REFUSEE', CURRENT_TIMESTAMP, 'Refusée - absence de pièces');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-032', 'POL-032', 'UUUU-SS-90', 'EAD', 'Express', '0600000002', 'CARENCE', CURRENT_TIMESTAMP, 'Carence technique');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-033', 'POL-033', 'VVVV-TT-12', 'Contrôle', 'Standard', '0611111114', 'CLOTUREE', CURRENT_TIMESTAMP, 'Expertise clôturée');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-034', 'POL-034', 'WWWW-UU-34', 'Expertise', 'Express', '0622222225', 'CLOTUREE', CURRENT_TIMESTAMP, 'Clôturée avec succès');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-035', 'POL-035', 'XXXX-VV-56', 'EAD', 'Standard', '0633333336', 'NOUVELLE', CURRENT_TIMESTAMP, 'Nouvelle mission EAD');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-036', 'POL-036', 'YYYY-WW-78', 'Contrôle', 'Express', '0644444447', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Vérification express');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-037', 'POL-037', 'ZZZZ-XX-90', 'Expertise', 'Standard', '0655555558', 'REFUSEE', CURRENT_TIMESTAMP, 'Refusée - véhicule volé');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-038', 'POL-038', 'AAAA-YY-12', 'EAD', 'Express', '0666666669', 'CARENCE', CURRENT_TIMESTAMP, 'En carence');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-039', 'POL-039', 'BBBB-ZZ-34', 'Contrôle', 'Standard', '0677777780', 'CLOTUREE', CURRENT_TIMESTAMP, 'Contrôle clôturé');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-040', 'POL-040', 'CCCC-AB-56', 'Expertise', 'Express', '0688888891', 'CLOTUREE', CURRENT_TIMESTAMP, 'Mission clôturée');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-041', 'POL-041', 'DDDD-CD-78', 'EAD', 'Standard', '0699999992', 'NOUVELLE', CURRENT_TIMESTAMP, 'Nouvelle demande expertise');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-042', 'POL-042', 'EEEE-EF-90', 'Contrôle', 'Express', '0600000003', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Contrôle en cours');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-043', 'POL-043', 'FFFF-GH-12', 'Expertise', 'Standard', '0611111115', 'REFUSEE', CURRENT_TIMESTAMP, 'Refusée - sinistre non couvert');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-044', 'POL-044', 'GGGG-IJ-34', 'EAD', 'Express', '0622222226', 'CARENCE', CURRENT_TIMESTAMP, 'Carence dépassée');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-045', 'POL-045', 'HHHH-KL-56', 'Contrôle', 'Standard', '0633333337', 'CLOTUREE', CURRENT_TIMESTAMP, 'Contrôle clôturé');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-046', 'POL-046', 'IIII-MN-78', 'Expertise', 'Express', '0644444448', 'CLOTUREE', CURRENT_TIMESTAMP, 'Expertise terminée');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-047', 'POL-047', 'JJJJ-OP-90', 'EAD', 'Standard', '0655555559', 'NOUVELLE', CURRENT_TIMESTAMP, 'Nouvelle mission');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-048', 'POL-048', 'KKKK-QR-12', 'Contrôle', 'Express', '0666666670', 'NON_CLOTUREE', CURRENT_TIMESTAMP, 'Vérification technique');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-049', 'POL-049', 'LLLL-ST-34', 'Expertise', 'Standard', '0677777781', 'REFUSEE', CURRENT_TIMESTAMP, 'Refusée - dossier clos');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, observations)
VALUES ('TEST-050', 'POL-050', 'MMMM-UV-56', 'EAD', 'Express', '0688888892', 'CARENCE', CURRENT_TIMESTAMP, 'En attente prolongée');

-- ============================================
-- MISSIONS AVEC NOTES D'HONORAIRES
-- ============================================
INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, date_affectation, date_cloture, observations)
VALUES ('HON-001', 'POL-HON-001', '1234-HON-01', 'Expertise', 'Standard', '0612000001', 'HONORAIRES', DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -28, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP), 'Expertise collision VL complet');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, date_affectation, date_cloture, observations)
VALUES ('HON-002', 'POL-HON-002', '5678-HON-02', 'EAD', 'Express', '0622000002', 'HONORAIRES', DATEADD('DAY', -20, CURRENT_TIMESTAMP), DATEADD('DAY', -18, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP), 'EAD véhicule utilitaire');

INSERT INTO missions (ref_sinistre, num_police, immatriculation, type_mission, parcours, tel_assure, statut, date_creation, date_affectation, date_cloture, observations)
VALUES ('HON-003', 'POL-HON-003', '9012-HON-03', 'Contrôle', 'Standard', '0633000003', 'HONORAIRES', DATEADD('DAY', -15, CURRENT_TIMESTAMP), DATEADD('DAY', -13, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'Contrôle technique post-réparation');

-- ============================================
-- NOTES D'HONORAIRES LIÉES AUX MISSIONS
-- ============================================
INSERT INTO notes_honoraires (mission_id, numero_note, description, montant_ht, taux_tva, montant_tva, montant_ttc, observations, date_creation, statut)
VALUES (
  (SELECT id FROM missions WHERE ref_sinistre = 'HON-001'),
  'NOTE-2024-001',
  'Honoraires expertise collision VL - rapport complet avec évaluation des dommages',
  12000.00, 20.00, 2400.00, 14400.00,
  'Note réceptionnée et en cours de traitement',
  DATEADD('DAY', -4, CURRENT_TIMESTAMP),
  'EMISE'
);

INSERT INTO notes_honoraires (mission_id, numero_note, description, montant_ht, taux_tva, montant_tva, montant_ttc, observations, date_creation, statut)
VALUES (
  (SELECT id FROM missions WHERE ref_sinistre = 'HON-001'),
  'NOTE-2024-002',
  'Complément honoraires - contre-expertise contradictoire',
  4000.00, 20.00, 800.00, 4800.00,
  'Note complémentaire suite à contestation',
  DATEADD('DAY', -2, CURRENT_TIMESTAMP),
  'EMISE'
);

INSERT INTO notes_honoraires (mission_id, numero_note, description, montant_ht, taux_tva, montant_tva, montant_ttc, observations, date_creation, statut)
VALUES (
  (SELECT id FROM missions WHERE ref_sinistre = 'HON-002'),
  'NOTE-2024-003',
  'Honoraires EAD véhicule utilitaire léger - expertise à distance',
  6500.00, 20.00, 1300.00, 7800.00,
  'Note validée par l''assureur',
  DATEADD('DAY', -3, CURRENT_TIMESTAMP),
  'REGLEE'
);

INSERT INTO notes_honoraires (mission_id, numero_note, description, montant_ht, taux_tva, montant_tva, montant_ttc, observations, date_creation, statut)
VALUES (
  (SELECT id FROM missions WHERE ref_sinistre = 'HON-003'),
  'NOTE-2024-004',
  'Honoraires contrôle post-réparation - vérification qualité travaux',
  3500.00, 20.00, 700.00, 4200.00,
  'Contrôle satisfaisant - travaux conformes',
  DATEADD('DAY', -1, CURRENT_TIMESTAMP),
  'EMISE'
);