document.addEventListener('DOMContentLoaded', function() {
    const ctx = document.getElementById('missionsChart');
    if (ctx && window.Chart && window.chartData) {
        new Chart(ctx, {
            type: 'bar',
            data: window.chartData,
            options: { responsive: true }
        });
    }
});
