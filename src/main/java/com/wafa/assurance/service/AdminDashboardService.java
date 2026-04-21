package com.wafa.assurance.service;

import com.wafa.assurance.dto.ActiviteRecenteDTO;
import com.wafa.assurance.dto.AppNotificationDTO;
import com.wafa.assurance.dto.DashboardStatsDTO;
import com.wafa.assurance.dto.ExpertPerformanceDTO;
import com.wafa.assurance.dto.KpiDashboardDTO;
import com.wafa.assurance.dto.MissionAdminDTO;
import com.wafa.assurance.dto.NotificationDTO;
import com.wafa.assurance.model.*;
import com.wafa.assurance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service complet pour le dashboard administrateur.
 * Fournit les KPIs, graphiques, activités récentes et notifications.
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final MissionRepository missionRepository;
    private final ExpertiseRepository expertiseRepository;
    private final DevisRepository devisRepository;
    private final UserRepository userRepository;
    private final NoteHonoraireRepository noteHonoraireRepository;
        private final NotificationCenterService notificationCenterService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] MOIS_LABELS = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"};

    /**
     * Calcule tous les KPIs du dashboard.
     */
    public KpiDashboardDTO getKpis() {
        KpiDashboardDTO dto = new KpiDashboardDTO();
        List<Mission> allMissions = missionRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime debutMois = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime debutMoisPrecedent = debutMois.minusMonths(1);

        // --- Total missions ---
        dto.setTotalMissions(allMissions.size());

        // Évolution par rapport au mois dernier
        long missionsCeMois = allMissions.stream()
                .filter(m -> m.getDateCreation() != null && m.getDateCreation().isAfter(debutMois))
                .count();
        long missionsMoisDernier = allMissions.stream()
                .filter(m -> m.getDateCreation() != null &&
                        m.getDateCreation().isAfter(debutMoisPrecedent) &&
                        m.getDateCreation().isBefore(debutMois))
                .count();
        dto.setEvolutionMissions(calculerEvolution(missionsCeMois, missionsMoisDernier));

        // --- Missions en cours ---
        long enCours = allMissions.stream()
                .filter(m -> m.getStatut() != null &&
                        m.getStatut() != StatutMission.CLOTUREE &&
                        m.getStatut() != StatutMission.REFUSEE)
                .count();
        dto.setMissionsEnCours(enCours);
        dto.setPourcentageMissionsEnCours(allMissions.isEmpty() ? 0 : (enCours * 100.0 / allMissions.size()));

        // --- Expertises ce mois ---
        List<Expertise> allExpertises = expertiseRepository.findAll();
        long expertisesCeMois = allExpertises.stream()
                .filter(e -> e.getDateExpertise() != null && e.getDateExpertise().isAfter(debutMois))
                .count();
        long expertisesMoisDernier = allExpertises.stream()
                .filter(e -> e.getDateExpertise() != null &&
                        e.getDateExpertise().isAfter(debutMoisPrecedent) &&
                        e.getDateExpertise().isBefore(debutMois))
                .count();
        dto.setExpertisesCeMois(expertisesCeMois);
        dto.setEvolutionExpertises(calculerEvolution(expertisesCeMois, expertisesMoisDernier));

        // --- Sinistres déclarés (= nouvelles missions ce mois) ---
        dto.setSinistresDeclarees(missionsCeMois);

        // --- Montant total devis ---
        List<Devis> allDevis = devisRepository.findAll();
        BigDecimal totalDevis = allDevis.stream()
                .map(Devis::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setMontantTotalDevisBD(totalDevis);

        BigDecimal devisCeMois = allDevis.stream()
                .filter(d -> d.getDateCreation() != null && d.getDateCreation().isAfter(debutMois))
                .map(Devis::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal devisMoisDernier = allDevis.stream()
                .filter(d -> d.getDateCreation() != null &&
                        d.getDateCreation().isAfter(debutMoisPrecedent) &&
                        d.getDateCreation().isBefore(debutMois))
                .map(Devis::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setEvolutionMontantDevis(calculerEvolutionBD(devisCeMois, devisMoisDernier));

        // --- Délai moyen de traitement ---
        double delaiMoyen = allMissions.stream()
                .filter(m -> m.getDateCreation() != null && m.getDateCloture() != null)
                .mapToLong(m -> ChronoUnit.DAYS.between(m.getDateCreation(), m.getDateCloture()))
                .average()
                .orElse(0);
        dto.setDelaiMoyenTraitement(Math.round(delaiMoyen * 10.0) / 10.0);

        // --- Compteurs par statut ---
        dto.setMissionsNouvelles(missionRepository.countByStatut(StatutMission.NOUVELLE));
        dto.setMissionsNonCloturees(missionRepository.countByStatut(StatutMission.NON_CLOTUREE));
        dto.setMissionsRefusees(missionRepository.countByStatut(StatutMission.REFUSEE));
        dto.setMissionsCarence(missionRepository.countByStatut(StatutMission.CARENCE));
        dto.setMissionsHonoraires(missionRepository.countByStatut(StatutMission.HONORAIRES));
        dto.setMissionsCloturees(missionRepository.countByStatut(StatutMission.CLOTUREE));

        // --- Missions par mois (12 derniers mois) ---
        List<String> labels = new ArrayList<>();
        List<Long> missionsParMois = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            LocalDateTime debut = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fin = debut.plusMonths(1);
            labels.add(MOIS_LABELS[debut.getMonthValue() - 1] + " " + debut.getYear());
            long count = allMissions.stream()
                    .filter(m -> m.getDateCreation() != null &&
                            !m.getDateCreation().isBefore(debut) &&
                            m.getDateCreation().isBefore(fin))
                    .count();
            missionsParMois.add(count);
        }
        dto.setMoisLabels(labels);
        dto.setMissionsParMois(missionsParMois);

        // --- Répartition par type de mission ---
        Map<String, Long> repartition = allMissions.stream()
                .filter(m -> m.getTypeMission() != null)
                .collect(Collectors.groupingBy(Mission::getTypeMission, Collectors.counting()));
        dto.setRepartitionParType(repartition);

        // --- Dernières 5 missions ---
        List<KpiDashboardDTO.DerniereMissionDTO> dernieres = allMissions.stream()
                .sorted(Comparator.comparing(Mission::getDateCreation, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(m -> new KpiDashboardDTO.DerniereMissionDTO(
                        m.getId(),
                        m.getRefSinistre(),
                        m.getTypeMission(),
                        m.getStatut() != null ? m.getStatut().name() : "INCONNUE",
                        m.getDateCreation() != null ? m.getDateCreation().format(FMT) : "",
                        calculerProgression(m.getStatut()),
                        m.getImmatriculation()
                ))
                .collect(Collectors.toList());
        dto.setDernieresMissions(dernieres);

        return dto;
    }

        public DashboardStatsDTO getDashboardStats() {
                List<Mission> allMissions = missionRepository.findAll();
                if (allMissions.isEmpty()) {
                        return DashboardStatsDTO.empty();
                }

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime debutMois = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime debutMoisPrecedent = debutMois.minusMonths(1);

                long missionsTotales = allMissions.size();
                long missionsEnCours = allMissions.stream()
                        .filter(mission -> mission.getStatut() != StatutMission.CLOTUREE && mission.getStatut() != StatutMission.REFUSEE)
                        .count();
                long missionsCloturees = allMissions.stream().filter(mission -> mission.getStatut() == StatutMission.CLOTUREE).count();

                long missionsCeMois = allMissions.stream().filter(mission -> inRange(mission.getDateCreation(), debutMois, now.plusSeconds(1))).count();
                long missionsMoisPrecedent = allMissions.stream().filter(mission -> inRange(mission.getDateCreation(), debutMoisPrecedent, debutMois)).count();

                List<User> experts = userRepository.findAll().stream()
                        .filter(user -> "EXPERT".equalsIgnoreCase(user.getRole()))
                        .toList();
                long expertsActifs = experts.stream().filter(user -> Boolean.TRUE.equals(user.getActif())).count();
                long expertsActifsMoisPrecedent = experts.stream()
                        .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isBefore(debutMois))
                        .count();

                BigDecimal totalIndemnites = devisRepository.findAll().stream()
                        .map(Devis::getMontantTotal)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                double delaiMoyen = allMissions.stream()
                        .filter(mission -> mission.getDateCreation() != null)
                        .mapToLong(mission -> ChronoUnit.DAYS.between(mission.getDateCreation(), mission.getDateCloture() != null ? mission.getDateCloture() : now))
                        .average()
                        .orElse(0);

                return DashboardStatsDTO.builder()
                        .missionsTotales(missionsTotales)
                        .evolutionMissions(calculerEvolution(missionsCeMois, missionsMoisPrecedent))
                        .missionsEnCours(missionsEnCours)
                        .progressionMissionsEnCours(missionsTotales == 0 ? 0 : round((missionsEnCours * 100.0) / missionsTotales))
                        .missionsCloturees(missionsCloturees)
                        .tauxCloture(missionsTotales == 0 ? 0 : round((missionsCloturees * 100.0) / missionsTotales))
                        .expertsActifs(expertsActifs)
                        .evolutionExperts(calculerEvolution(expertsActifs, expertsActifsMoisPrecedent))
                        .sinistresDeclaresMois(missionsCeMois)
                        .evolutionSinistres(calculerEvolution(missionsCeMois, missionsMoisPrecedent))
                        .montantTotalIndemnites(totalIndemnites)
                        .delaiMoyenTraitement(round(delaiMoyen))
                        .evolutionMensuelle(buildMonthlySeries(allMissions, now))
                        .repartitionSinistres(buildTypeDistribution(allMissions))
                        .performanceExperts(buildExpertPerformance(experts))
                        .delaisTraitement(buildDelayStats(allMissions, now))
                        .carteSinistres(buildGeoDistribution(allMissions))
                        .dernieresMissions(allMissions.stream()
                                .sorted(Comparator.comparing(Mission::getDateCreation, Comparator.nullsLast(Comparator.reverseOrder())))
                                .limit(10)
                                .map(MissionAdminDTO::from)
                                .toList())
                        .build();
        }

    /**
     * Retourne les activités récentes (dernières missions, expertises, devis créés).
     */
    public List<ActiviteRecenteDTO> getActivitesRecentes() {
        List<ActiviteRecenteDTO> activites = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Dernières missions créées
        missionRepository.findAll().stream()
                .sorted(Comparator.comparing(Mission::getDateCreation, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .forEach(m -> activites.add(new ActiviteRecenteDTO(
                        m.getId(),
                        "MISSION_CREEE",
                        "Mission " + m.getRefSinistre() + " créée (" + m.getTypeMission() + ")",
                        formatDateRelative(m.getDateCreation(), now),
                        m.getDateCreation() != null ? m.getDateCreation().format(FMT) : "",
                        "/admin/missions/" + m.getId(),
                        "fas fa-file-alt",
                        "primary"
                )));

        // Derniers devis
        devisRepository.findAll().stream()
                .sorted(Comparator.comparing(Devis::getDateCreation, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(3)
                .forEach(d -> activites.add(new ActiviteRecenteDTO(
                        d.getId(),
                        "DEVIS_GENERE",
                        "Devis " + (d.getTypeDevis() != null ? d.getTypeDevis().name() : "") +
                                " - " + (d.getMontantTotal() != null ? d.getMontantTotal() + " DH" : ""),
                        formatDateRelative(d.getDateCreation(), now),
                        d.getDateCreation() != null ? d.getDateCreation().format(FMT) : "",
                        "/admin/missions/" + d.getMission().getId(),
                        "fas fa-file-invoice-dollar",
                        "success"
                )));

        // Dernières expertises
        expertiseRepository.findAll().stream()
                .sorted(Comparator.comparing(Expertise::getDateExpertise, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(3)
                .forEach(e -> activites.add(new ActiviteRecenteDTO(
                        e.getId(),
                        "EXPERTISE_AJOUTEE",
                        "Expertise " + (e.getType() != null ? e.getType().name() : "") + " ajoutée",
                        formatDateRelative(e.getDateExpertise(), now),
                        e.getDateExpertise() != null ? e.getDateExpertise().format(FMT) : "",
                        "/admin/missions/" + e.getMission().getId(),
                        "fas fa-search",
                        "info"
                )));

        // Trier par date
        activites.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return activites.stream().limit(10).collect(Collectors.toList());
    }

    /**
     * Retourne les notifications non lues.
     */
    public List<NotificationDTO> getNotifications() {
                List<NotificationDTO> notifs = new ArrayList<>();

        // Missions nouvelles (non traitées)
        missionRepository.findByStatut(StatutMission.NOUVELLE).forEach(m ->
                notifs.add(new NotificationDTO(
                        m.getId(), "Nouvelle mission : " + m.getRefSinistre(),
                        false,
                        m.getDateCreation() != null ? m.getDateCreation().format(FMT) : "",
                        "NOUVEAU_SINISTRE",
                        "/admin/missions/" + m.getId(),
                        "fas fa-exclamation-circle",
                        "warning"
                ))
        );

        // Missions en carence
        missionRepository.findByStatut(StatutMission.CARENCE).forEach(m ->
                notifs.add(new NotificationDTO(
                        m.getId(), "Mission en carence : " + m.getRefSinistre(),
                        false,
                        m.getDateCreation() != null ? m.getDateCreation().format(FMT) : "",
                        "MISSION_RETARD",
                        "/admin/missions/" + m.getId(),
                        "fas fa-clock",
                        "danger"
                ))
        );

        // Devis en attente
        devisRepository.findAll().stream()
                .filter(d -> d.getStatut() == StatutDevis.EN_ATTENTE)
                .forEach(d ->
                        notifs.add(new NotificationDTO(
                                d.getId(), "Devis à valider - Mission " + d.getMission().getRefSinistre(),
                                false,
                                d.getDateCreation() != null ? d.getDateCreation().format(FMT) : "",
                                "DEVIS_A_VALIDER",
                                "/admin/missions/" + d.getMission().getId(),
                                "fas fa-file-invoice",
                                "info"
                        ))
                );

                notificationCenterService.list().stream()
                        .limit(10)
                        .map(this::mapNotification)
                        .forEach(notifs::add);

        return notifs.stream().limit(20).collect(Collectors.toList());
    }

    // --- Méthodes utilitaires ---

    private double calculerEvolution(long actuel, long precedent) {
        if (precedent == 0) return actuel > 0 ? 100.0 : 0.0;
        return Math.round((actuel - precedent) * 1000.0 / precedent) / 10.0;
    }

    private double calculerEvolutionBD(BigDecimal actuel, BigDecimal precedent) {
        if (precedent.compareTo(BigDecimal.ZERO) == 0) {
            return actuel.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return actuel.subtract(precedent)
                .multiply(BigDecimal.valueOf(100))
                .divide(precedent, 1, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }

    private int calculerProgression(StatutMission statut) {
        if (statut == null) return 0;
        return switch (statut) {
            case NOUVELLE -> 10;
                        case ACCEPTEE -> 35;
                        case EN_COURS -> 60;
            case NON_CLOTUREE -> 50;
            case HONORAIRES -> 75;
            case CARENCE -> 40;
                        case REEXAMEN -> 65;
            case REFUSEE -> 100;
            case CLOTUREE -> 100;
        };
    }

    private String formatDateRelative(LocalDateTime date, LocalDateTime now) {
        if (date == null) return "";
        long minutes = ChronoUnit.MINUTES.between(date, now);
        if (minutes < 1) return "à l'instant";
        if (minutes < 60) return "il y a " + minutes + " min";
        long heures = ChronoUnit.HOURS.between(date, now);
        if (heures < 24) return "il y a " + heures + "h";
        long jours = ChronoUnit.DAYS.between(date, now);
        if (jours < 30) return "il y a " + jours + "j";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

        private boolean inRange(LocalDateTime value, LocalDateTime start, LocalDateTime end) {
                return value != null && !value.isBefore(start) && value.isBefore(end);
        }

        private double round(double value) {
                return Math.round(value * 10.0) / 10.0;
        }

        private List<DashboardStatsDTO.MonthlyMissionPointDTO> buildMonthlySeries(List<Mission> missions, LocalDateTime now) {
                List<DashboardStatsDTO.MonthlyMissionPointDTO> series = new ArrayList<>();
                for (int index = 11; index >= 0; index--) {
                        LocalDateTime currentStart = now.minusMonths(index).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                        LocalDateTime currentEnd = currentStart.plusMonths(1);
                        LocalDateTime previousStart = currentStart.minusYears(1);
                        LocalDateTime previousEnd = currentEnd.minusYears(1);
                        long currentCount = missions.stream().filter(mission -> inRange(mission.getDateCreation(), currentStart, currentEnd)).count();
                        long previousCount = missions.stream().filter(mission -> inRange(mission.getDateCreation(), previousStart, previousEnd)).count();
                        series.add(DashboardStatsDTO.MonthlyMissionPointDTO.builder()
                                .label(MOIS_LABELS[currentStart.getMonthValue() - 1])
                                .anneeCourante(currentCount)
                                .anneePrecedente(previousCount)
                                .build());
                }
                return series;
        }

        private List<DashboardStatsDTO.TypeDistributionPointDTO> buildTypeDistribution(List<Mission> missions) {
                Map<String, String> palette = Map.of(
                        "AUTO", "#2563eb",
                        "HABITATION", "#f97316",
                        "VIE", "#10b981",
                        "RC PROFESSIONNELLE", "#ef4444"
                );
                return missions.stream()
                        .collect(Collectors.groupingBy(mission -> normalizeType(mission.getTypeMission()), Collectors.counting()))
                        .entrySet().stream()
                        .map(entry -> DashboardStatsDTO.TypeDistributionPointDTO.builder()
                                .type(entry.getKey())
                                .valeur(entry.getValue())
                                .couleur(palette.getOrDefault(entry.getKey(), "#8b5cf6"))
                                .build())
                        .sorted(Comparator.comparing(DashboardStatsDTO.TypeDistributionPointDTO::getValeur).reversed())
                        .toList();
        }

        private String normalizeType(String typeMission) {
                if (typeMission == null || typeMission.isBlank()) {
                        return "AUTO";
                }
                String upper = typeMission.toUpperCase(Locale.ROOT);
                if (upper.contains("HAB")) {
                        return "HABITATION";
                }
                if (upper.contains("VIE")) {
                        return "VIE";
                }
                if (upper.contains("RC")) {
                        return "RC PROFESSIONNELLE";
                }
                return "AUTO";
        }

        private List<ExpertPerformanceDTO> buildExpertPerformance(List<User> experts) {
                return experts.stream()
                        .map(user -> ExpertPerformanceDTO.builder()
                                .id(user.getId())
                                .nomComplet((user.getPrenom() != null ? user.getPrenom() : "") + " " + (user.getNom() != null ? user.getNom() : ""))
                                .email(user.getEmail())
                                .telephone(user.getTelephone())
                                .statut(Boolean.TRUE.equals(user.getActif()) ? "ACTIF" : "INACTIF")
                                .missionsTraitees(0)
                                .missionsEnCours(0)
                                .missionsTerminees(0)
                                .missionsEnRetard(0)
                                .delaiMoyenJours(0)
                                .tauxSatisfaction(4.5)
                                .note(4.5)
                                .derniereActivite(user.getDerniereConnexion() != null ? user.getDerniereConnexion().format(FMT) : "Jamais")
                                .specialites(user.getSpecialites() != null ? user.getSpecialites() : List.of())
                                .zonesIntervention(user.getZonesIntervention() != null ? user.getZonesIntervention() : List.of())
                                .build())
                        .limit(10)
                        .toList();
        }

        private DashboardStatsDTO.DelayStatsDTO buildDelayStats(List<Mission> missions, LocalDateTime now) {
                long green = missions.stream().filter(mission -> resolveDelay(mission, now) < 7).count();
                long orange = missions.stream().filter(mission -> {
                        long delay = resolveDelay(mission, now);
                        return delay >= 7 && delay <= 15;
                }).count();
                long red = missions.stream().filter(mission -> resolveDelay(mission, now) > 15).count();
                double average = missions.stream().mapToLong(mission -> resolveDelay(mission, now)).average().orElse(0);
                String level = average < 7 ? "VERT" : average <= 15 ? "ORANGE" : "ROUGE";
                return DashboardStatsDTO.DelayStatsDTO.builder()
                        .delaiMoyenGlobal(round(average))
                        .niveau(level)
                        .histogramme(List.of(
                                new DashboardStatsDTO.DelayBucketDTO("< 7j", green, "#10b981"),
                                new DashboardStatsDTO.DelayBucketDTO("7 - 15j", orange, "#f59e0b"),
                                new DashboardStatsDTO.DelayBucketDTO("> 15j", red, "#ef4444")
                        ))
                        .build();
        }

        private long resolveDelay(Mission mission, LocalDateTime now) {
                if (mission.getDateCreation() == null) {
                        return 0;
                }
                return Math.max(0, ChronoUnit.DAYS.between(mission.getDateCreation(), mission.getDateCloture() != null ? mission.getDateCloture() : now));
        }

        private List<DashboardStatsDTO.GeoPointDTO> buildGeoDistribution(List<Mission> missions) {
                return missions.stream()
                        .collect(Collectors.groupingBy(mission -> mission.getParcours() == null || mission.getParcours().isBlank() ? "Zone non renseignée" : mission.getParcours(), Collectors.counting()))
                        .entrySet().stream()
                        .map(entry -> DashboardStatsDTO.GeoPointDTO.builder()
                                .zone(entry.getKey())
                                .type("MISSIONS")
                                .volume(entry.getValue())
                                .build())
                        .sorted(Comparator.comparing(DashboardStatsDTO.GeoPointDTO::getVolume).reversed())
                        .toList();
        }

        private NotificationDTO mapNotification(AppNotificationDTO notification) {
                return new NotificationDTO(
                        notification.getId(),
                        notification.getTitle(),
                        notification.isRead(),
                        notification.getCreatedAt() != null ? notification.getCreatedAt().format(FMT) : "",
                        notification.getType(),
                        notification.getResourceUrl(),
                        "bell",
                        notification.isRead() ? "muted" : "primary"
                );
        }
}
