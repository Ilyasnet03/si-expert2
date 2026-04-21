/**
 * Dashboard Admin - SI Expert
 * Initialisation des graphiques Chart.js et chargement AJAX des données.
 */

document.addEventListener('DOMContentLoaded', function() {
    initChartMissions();
    initChartTypes();
    loadActivitesRecentes();
});

/**
 * Graphique 1 : Évolution mensuelle des missions (courbe)
 */
function initChartMissions() {
    const ctx = document.getElementById('chartMissions');
    if (!ctx) return;

    // Raccourcir les labels pour l'affichage
    const labels = (chartData.moisLabels || []).map(l => l.split(' ')[0]);

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Missions',
                data: chartData.missionsParMois || [],
                borderColor: '#1a73e8',
                backgroundColor: 'rgba(26, 115, 232, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#1a73e8',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 5,
                pointHoverRadius: 7
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#1f2937',
                    titleFont: { size: 13 },
                    bodyFont: { size: 12 },
                    padding: 12,
                    cornerRadius: 8,
                    displayColors: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { precision: 0, font: { size: 12 } },
                    grid: { color: 'rgba(0,0,0,0.05)' }
                },
                x: {
                    ticks: { font: { size: 11 } },
                    grid: { display: false }
                }
            }
        }
    });
}

/**
 * Graphique 2 : Répartition par type de mission (donut)
 */
function initChartTypes() {
    const ctx = document.getElementById('chartTypes');
    if (!ctx) return;

    const repartition = chartData.repartitionParType || {};
    const labels = Object.keys(repartition);
    const data = Object.values(repartition);

    const colors = ['#1a73e8', '#34a853', '#fbbc05', '#ea4335', '#673ab7', '#ff6d00', '#00bcd4'];

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors.slice(0, labels.length),
                borderWidth: 3,
                borderColor: '#fff',
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '65%',
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 16,
                        usePointStyle: true,
                        pointStyle: 'circle',
                        font: { size: 12 }
                    }
                },
                tooltip: {
                    backgroundColor: '#1f2937',
                    padding: 12,
                    cornerRadius: 8,
                    callbacks: {
                        label: function(context) {
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const pct = total > 0 ? ((context.raw / total) * 100).toFixed(1) : 0;
                            return context.label + ': ' + context.raw + ' (' + pct + '%)';
                        }
                    }
                }
            }
        }
    });
}

/**
 * Chargement AJAX des activités récentes
 */
function loadActivitesRecentes() {
    const container = document.getElementById('activities-container');
    if (!container) return;

    fetch('/api/admin/dashboard/activities', {
        credentials: 'same-origin'
    })
    .then(response => response.json())
    .then(activites => {
        if (activites.length === 0) {
            container.innerHTML = '<div class="text-center py-4 text-muted">Aucune activité récente</div>';
            return;
        }
        let html = '<div class="timeline-list">';
        activites.forEach(a => {
            html += `
                <a href="${escapeHtml(a.lien)}" class="timeline-item d-flex align-items-start gap-3 px-3 py-2 text-decoration-none border-bottom">
                    <div class="timeline-icon bg-${escapeHtml(a.couleur)}-subtle text-${escapeHtml(a.couleur)} rounded-circle d-flex align-items-center justify-content-center" style="min-width:36px; height:36px;">
                        <i class="${escapeHtml(a.icone)}" style="font-size:14px;"></i>
                    </div>
                    <div class="flex-grow-1">
                        <div style="font-size:13px; color:#374151;">${escapeHtml(a.message)}</div>
                        <small class="text-muted">${escapeHtml(a.dateRelative)}</small>
                    </div>
                </a>`;
        });
        html += '</div>';
        container.innerHTML = html;
    })
    .catch(() => {
        container.innerHTML = '<div class="text-center py-4 text-muted">Erreur de chargement</div>';
    });
}

/**
 * Filtre par période (placeholder)
 */
function filterPeriod(period) {
    // Mettre à jour l'état actif des boutons
    document.querySelectorAll('.btn-group .btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    // TODO: Recharger les stats avec la période sélectionnée
}

/**
 * Sécurité : échapper le HTML pour éviter les injections XSS
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(text));
    return div.innerHTML;
}
