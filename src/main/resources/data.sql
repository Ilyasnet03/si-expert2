-- ============================================
-- DONNÉES DE TEST POUR L'APPLICATION SI-EXPERT
-- ============================================

-- Supprimer les données existantes (version PostgreSQL)
TRUNCATE TABLE missions RESTART IDENTITY CASCADE;

-- ============================================
-- MISSIONS NOUVELLES
-- ============================================
INSERT INTO missions (
    ref_sinistre, num_police, immatriculation, type_mission, parcours,
    tel_assure, statut, date_creation, observations
) VALUES
      ('SN-2026-9901', 'POL-AZ-882', '1234 TU 78', 'EAD', 'Standard',
       '0661223344', 'NOUVELLE', NOW(), 'Mission créée automatiquement'),

      ('SIN2025-001', 'POL123456', '1234 TU 78', 'EAD', 'Classique',
       '0612345678', 'NOUVELLE', NOW() - INTERVAL '2 hours',
       'Véhicule accidenté - constat à vérifier'),

      ('SIN2025-002', 'POL789012', '5678 AB 90', 'Choc direct', 'Express',
       '0623456789', 'NOUVELLE', NOW() - INTERVAL '5 hours',
       'Choc arrière - témoignages à collecter'),

      ('SIN2025-003', 'POL345678', '9012 CD 34', 'EAD', 'Classique',
       '0634567890', 'NOUVELLE', NOW() - INTERVAL '1 day',
       'Dégâts des eaux - expertise urgente'),

      ('SIN2025-004', 'POL901234', '3456 EF 56', 'Vol', 'Premium',
       '0645678901', 'NOUVELLE', NOW() - INTERVAL '3 days',
       'Vol de véhicule - enquête en cours');

-- ============================================
-- MISSIONS NON CLÔTURÉES
-- ============================================
INSERT INTO missions (
    ref_sinistre, num_police, immatriculation, type_mission, parcours,
    tel_assure, statut, date_creation, date_affectation, observations
) VALUES
      ('SN-2026-1142', 'POL-VK-550', '5678 AB 90', 'Physique', 'Bris de Glace',
       '0670998877', 'NON_CLOTUREE', NOW() - INTERVAL '1 day',
       NOW() - INTERVAL '5 hours', 'Expertise en cours'),

      ('SIN2025-005', 'POL567890', '7890 GH 12', 'EAD', 'Classique',
       '0656789012', 'NON_CLOTUREE', NOW() - INTERVAL '2 days',
       NOW() - INTERVAL '1 day', 'Expertise en cours - rendez-vous pris'),

      ('SIN2025-006', 'POL123789', '2345 IJ 34', 'Choc direct', 'Express',
       '0667890123', 'NON_CLOTUREE', NOW() - INTERVAL '3 days',
       NOW() - INTERVAL '2 days', 'Devis en attente de validation'),

      ('SIN2025-007', 'POL456123', '6789 KL 56', 'EAD', 'Classique',
       '0678901234', 'NON_CLOTUREE', NOW() - INTERVAL '5 days',
       NOW() - INTERVAL '4 days', 'Réparation en cours - suivi nécessaire'),

      ('SIN2025-008', 'POL789456', '0123 MN 78', 'Incendie', 'Premium',
       '0689012345', 'NON_CLOTUREE', NOW() - INTERVAL '1 week',
       NOW() - INTERVAL '6 days', 'Expertise complexe - rapport à venir');

-- ============================================
-- MISSIONS REFUSÉES
-- ============================================
INSERT INTO missions (
    ref_sinistre, num_police, immatriculation, type_mission, parcours,
    tel_assure, statut, date_creation, date_affectation, motif_refus, observations
) VALUES
      ('SN-2026-REF-01', 'POL-RE-444', '3456 EF 56', 'Physique', 'Standard',
       '0600112233', 'REFUSEE', NOW() - INTERVAL '2 days',
       NOW() - INTERVAL '1 day', 'Expert indisponible', 'Refusé car expert non disponible'),

      ('SIN2025-009', 'POL012345', '4567 OP 90', 'EAD', 'Classique',
       '0690123456', 'REFUSEE', NOW() - INTERVAL '4 days',
       NOW() - INTERVAL '3 days', 'Hors délai de déclaration',
       'Déclaration trop tardive - non couvert'),

      ('SIN2025-010', 'POL678901', '8901 QR 12', 'Choc direct', 'Express',
       '0601234567', 'REFUSEE', NOW() - INTERVAL '6 days',
       NOW() - INTERVAL '5 days', 'Fausse déclaration',
       'Informations contradictoires - refus après enquête'),

      ('SIN2025-011', 'POL234567', '1234 ST 34', 'EAD', 'Classique',
       '0612345678', 'REFUSEE', NOW() - INTERVAL '1 week',
       NOW() - INTERVAL '6 days', 'Non couverture du risque',
       'Garantie non applicable au sinistre'),

      ('SIN2025-012', 'POL890123', '5678 UV 56', 'Vol', 'Premium',
       '0623456789', 'REFUSEE', NOW() - INTERVAL '8 days',
       NOW() - INTERVAL '7 days', 'Défaut de déclaration en mairie',
       'Pas de dépôt de plainte dans les délais');

-- ============================================
-- MISSIONS EN CARENCE
-- ============================================
INSERT INTO missions (
    ref_sinistre, num_police, immatriculation, type_mission, parcours,
    tel_assure, statut, date_creation, date_affectation, observations
) VALUES
      ('SN-2025-0089', 'POL-XP-112', '9012 CD 34', 'EAD', 'Standard',
       '0655443322', 'CARENCE', NOW() - INTERVAL '10 days',
       NOW() - INTERVAL '9 days', 'Délai dépassé - action requise'),

      ('SIN2025-013', 'POL456789', '7890 WX 78', 'EAD', 'Classique',
       '0634567890', 'CARENCE', NOW() - INTERVAL '5 days',
       NOW() - INTERVAL '4 days', 'Délai dépassé - relance nécessaire'),

      ('SIN2025-014', 'POL012678', '1234 YZ 90', 'Choc direct', 'Express',
       '0645678901', 'CARENCE', NOW() - INTERVAL '6 days',
       NOW() - INTERVAL '5 days', 'Plus de 48h sans réponse expert'),

      ('SIN2025-015', 'POL345901', '5678 AB 12', 'EAD', 'Classique',
       '0656789012', 'CARENCE', NOW() - INTERVAL '7 days',
       NOW() - INTERVAL '6 days', 'Dossier bloqué - action requise'),

      ('SIN2025-016', 'POL678234', '9012 CD 34', 'Incendie', 'Premium',
       '0667890123', 'CARENCE', NOW() - INTERVAL '8 days',
       NOW() - INTERVAL '7 days', 'Expert non disponible - réaffectation');

-- ============================================
-- MISSIONS NOTES D'HONORAIRES
-- ============================================
INSERT INTO missions (
    ref_sinistre, num_police, immatriculation, type_mission, parcours,
    tel_assure, statut, date_creation, date_affectation, observations
) VALUES
      ('SN-2026-HON-01', 'POL-HON-123', '7890 GH 12', 'EAD', 'Premium',
       '0678901234', 'HONORAIRES', NOW() - INTERVAL '3 days',
       NOW() - INTERVAL '2 days', 'Honoraires à régler - 450€'),

      ('SIN2025-017', 'POL901567', '3456 EF 56', 'EAD', 'Classique',
       '0678901234', 'HONORAIRES', NOW() - INTERVAL '3 days',
       NOW() - INTERVAL '2 days', 'Honoraires à régler - 450€'),

      ('SIN2025-018', 'POL234890', '7890 GH 78', 'Expertise', 'Premium',
       '0689012345', 'HONORAIRES', NOW() - INTERVAL '4 days',
       NOW() - INTERVAL '3 days', 'Note d''honoraires - 850€'),

      ('SIN2025-019', 'POL567123', '1234 IJ 90', 'EAD', 'Classique',
       '0690123456', 'HONORAIRES', NOW() - INTERVAL '5 days',
       NOW() - INTERVAL '4 days', 'Frais d''expertise - 320€'),

      ('SIN2025-020', 'POL890456', '5678 KL 12', 'Choc direct', 'Express',
       '0601234567', 'HONORAIRES', NOW() - INTERVAL '6 days',
       NOW() - INTERVAL '5 days', 'Honoraires - 275€');

-- ============================================
-- MISSIONS CLÔTURÉES
-- ============================================
INSERT INTO missions (
    ref_sinistre, num_police, immatriculation, type_mission, parcours,
    tel_assure, statut, date_creation, date_affectation, date_cloture, observations
) VALUES
      ('SN-2025-CLO-01', 'POL-CLO-789', '9012 MN 34', 'Vol', 'Premium',
       '0689012345', 'CLOTUREE', NOW() - INTERVAL '15 days',
       NOW() - INTERVAL '14 days', NOW() - INTERVAL '3 days',
       'Dossier clôturé - indemnisation versée'),

      ('SIN2025-021', 'POL123567', '9012 MN 34', 'EAD', 'Classique',
       '0612345678', 'CLOTUREE', NOW() - INTERVAL '10 days',
       NOW() - INTERVAL '9 days', NOW() - INTERVAL '2 days',
       'Dossier clôturé - indemnisation versée'),

      ('SIN2025-022', 'POL456890', '3456 OP 56', 'Vol', 'Premium',
       '0623456789', 'CLOTUREE', NOW() - INTERVAL '12 days',
       NOW() - INTERVAL '11 days', NOW() - INTERVAL '3 days',
       'Véhicule retrouvé - dossier clôturé'),

      ('SIN2025-023', 'POL789123', '7890 QR 78', 'Incendie', 'Premium',
       '0634567890', 'CLOTUREE', NOW() - INTERVAL '15 days',
       NOW() - INTERVAL '14 days', NOW() - INTERVAL '5 days',
       'Expertise terminée - rapport final envoyé');

-- ============================================
-- VÉRIFICATION (décommenter pour tester)
-- ============================================
-- SELECT statut, COUNT(*) FROM missions GROUP BY statut;