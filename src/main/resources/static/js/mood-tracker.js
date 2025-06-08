document.addEventListener('DOMContentLoaded', function () {
    // Configuración inicial
    const currentDate = new Date();
    updateDateDisplay(currentDate);

    // Inicializar gráfico
    const ctx = document.getElementById('mood-chart').getContext('2d');
    const moodChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
            datasets: [{
                label: 'Estado de ánimo',
                data: [65, 59, 80, 81, 56, 72, 90],
                fill: true,
                backgroundColor: 'rgba(108, 99, 255, 0.2)',
                borderColor: 'rgba(108, 99, 255, 1)',
                tension: 0.4,
                pointBackgroundColor: 'rgba(108, 99, 255, 1)'
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    min: 0,
                    max: 100,
                    ticks: {
                        callback: function (value) {
                            if (value === 0) return 'Bajo';
                            if (value === 50) return 'Medio';
                            if (value === 100) return 'Alto';
                            return '';
                        }
                    }
                }
            }
        }
    });

    // Event listeners
    document.getElementById('prev-day').addEventListener('click', function () {
        currentDate.setDate(currentDate.getDate() - 1);
        updateDateDisplay(currentDate);
        loadMoodData(currentDate);
    });

    document.getElementById('next-day').addEventListener('click', function () {
        currentDate.setDate(currentDate.getDate() + 1);
        updateDateDisplay(currentDate);
        loadMoodData(currentDate);
    });

    document.querySelectorAll('.time-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.time-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            updateChart(this.textContent.trim());
        });
    });

    document.getElementById('analyze-text').addEventListener('click', analyzeText);
    document.getElementById('sync-wearable').addEventListener('click', syncWearable);
    document.getElementById('save-mood').addEventListener('click', saveMood);

    // Cargar datos iniciales
    loadMoodData(currentDate);
    loadRecommendations();

    // Simular datos de wearable
    setTimeout(() => {
        document.getElementById('heart-rate').textContent = '72';
        document.getElementById('sleep-hours').textContent = '7.5';
        document.getElementById('energy-level').textContent = '6';
    }, 1500);
});

function updateDateDisplay(date) {
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('current-date').textContent = date.toLocaleDateString('es-ES', options);
}

function loadMoodData(date) {
    // Simular carga de datos desde el backend
    console.log(`Cargando datos para ${date.toISOString().split('T')[0]}`);

    // Aquí iría la llamada fetch a tu API Spring Boot
    // fetch(`/api/mood-data?date=${date.toISOString().split('T')[0]}`)
    //   .then(response => response.json())
    //   .then(data => updateUI(data));

    // Datos de ejemplo
    const mockData = {
        moodValue: 72,
        moodTags: ['Calmado', 'Enfocado'],
        moodAnalysis: 'Nuestro análisis indica que te encuentras en un estado emocional equilibrado. Tu nivel de estrés es bajo y tu frecuencia cardíaca se mantiene estable.',
        biometrics: {
            heartRate: 72,
            sleepHours: 7.5,
            energyLevel: 6
        }
    };

    updateUI(mockData);
}

function updateUI(data) {
    document.getElementById('mood-value').textContent = data.moodValue;

    const moodFace = document.getElementById('mood-face');
    moodFace.className = 'mood-face'; // Reset classes

    // Actualizar cara según el estado de ánimo
    if (data.moodValue >= 80) {
        moodFace.classList.add('happy');
        // Cambiar boca a sonrisa
        moodFace.querySelector('.mouth').style.borderRadius = '50%';
        moodFace.querySelector('.mouth').style.height = '15px';
        moodFace.querySelector('.mouth').style.border = '3px solid white';
        moodFace.querySelector('.mouth').style.borderTop = 'none';
    } else if (data.moodValue >= 50) {
        moodFace.classList.add('neutral');
        // Cambiar boca a línea recta
        moodFace.querySelector('.mouth').style.borderRadius = '0';
        moodFace.querySelector('.mouth').style.height = '3px';
        moodFace.querySelector('.mouth').style.border = 'none';
        moodFace.querySelector('.mouth').style.backgroundColor = 'white';
    } else {
        moodFace.classList.add('sad');
        // Cambiar boca a triste
        moodFace.querySelector('.mouth').style.borderRadius = '50%';
        moodFace.querySelector('.mouth').style.height = '15px';
        moodFace.querySelector('.mouth').style.border = '3px solid white';
        moodFace.querySelector('.mouth').style.borderBottom = 'none';
    }

    // Actualizar tags
    const tagsContainer = document.getElementById('mood-tags');
    tagsContainer.innerHTML = '';

    data.moodTags.forEach(tag => {
        const span = document.createElement('span');
        span.className = `tag ${tag.toLowerCase()}`;
        span.textContent = tag;
        tagsContainer.appendChild(span);
    });

    // Actualizar análisis
    document.getElementById('mood-analysis').textContent = data.moodAnalysis;

    // Actualizar biometría
    if (data.biometrics) {
        document.getElementById('heart-rate').textContent = data.biometrics.heartRate;
        document.getElementById('sleep-hours').textContent = data.biometrics.sleepHours;
        document.getElementById('energy-level').textContent = data.biometrics.energyLevel;
    }
}

function updateChart(timeRange) {
    console.log(`Actualizando gráfico para ${timeRange}`);
    // Aquí iría la llamada fetch para obtener datos históricos
}

function analyzeText() {
    const text = document.getElementById('mood-text').value;
    if (!text.trim()) {
        alert('Por favor escribe cómo te sientes');
        return;
    }

    // Simular análisis de IA
    console.log('Analizando texto con IA:', text);

    // Mostrar carga
    const btn = document.getElementById('analyze-text');
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Analizando...';
    btn.disabled = true;

    // Simular demora de red
    setTimeout(() => {
        // Resultados simulados basados en palabras clave
        let moodValue, tags, analysis;

        if (text.toLowerCase().includes('bien') || text.toLowerCase().includes('feliz')) {
            moodValue = 85;
            tags = ['Positivo', 'Energético'];
            analysis = 'Tu texto refleja un estado emocional positivo. ¡Sigue así!';
        } else if (text.toLowerCase().includes('ansioso') || text.toLowerCase().includes('estrés')) {
            moodValue = 40;
            tags = ['Ansioso', 'Estrés'];
            analysis = 'Detectamos signos de ansiedad en tu texto. Te recomendamos ejercicios de respiración.';
        } else {
            moodValue = 65;
            tags = ['Neutral'];
            analysis = 'Tu estado emocional parece equilibrado. No detectamos emociones extremas.';
        }

        // Actualizar UI con resultados
        document.getElementById('mood-value').textContent = moodValue;

        const tagsContainer = document.getElementById('mood-tags');
        tagsContainer.innerHTML = '';
        tags.forEach(tag => {
            const span = document.createElement('span');
            span.className = `tag ${tag.toLowerCase()}`;
            span.textContent = tag;
            tagsContainer.appendChild(span);
        });

        document.getElementById('mood-analysis').textContent = analysis;

        // Restaurar botón
        btn.innerHTML = '<i class="fas fa-robot"></i> Analizar con IA';
        btn.disabled = false;

        // Mostrar confirmación
        alert('Análisis completado. Hemos actualizado tu estado emocional.');
    }, 2000);
}

function syncWearable() {
    console.log('Sincronizando con wearable...');
    // Aquí iría la integración con Web Bluetooth API

    // Simular sincronización
    const btn = document.getElementById('sync-wearable');
    btn.innerHTML = '<i class="fas fa-sync fa-spin"></i> Sincronizando...';
    btn.disabled = true;

    setTimeout(() => {
        // Datos simulados
        document.getElementById('heart-rate').textContent = Math.floor(Math.random() * 30) + 60;
        document.getElementById('sleep-hours').textContent = (Math.random() * 3 + 5).toFixed(1);
        document.getElementById('energy-level').textContent = Math.floor(Math.random() * 4) + 4;

        btn.innerHTML = '<i class="fas fa-bluetooth-b"></i> Sincronizar wearable';
        btn.disabled = false;

        alert('Datos biométricos actualizados correctamente');
    }, 1500);
}

function saveMood() {
    const selectedMood = document.querySelector('input[name="mood"]:checked');
    if (!selectedMood) {
        alert('Por favor selecciona cómo te sientes');
        return;
    }

    const moodText = document.getElementById('mood-text').value;
    const heartRate = document.getElementById('heart-rate').textContent;
    const sleepHours = document.getElementById('sleep-hours').textContent;
    const energyLevel = document.getElementById('energy-level').textContent;

    const data = {
        moodLevel: selectedMood.value,
        moodText: moodText,
        biometrics: {
            heartRate: heartRate !== '--' ? parseInt(heartRate) : null,
            sleepHours: sleepHours !== '--' ? parseFloat(sleepHours) : null,
            energyLevel: energyLevel !== '--' ? parseInt(energyLevel) : null
        },
        date: new Date().toISOString()
    };

    console.log('Guardando estado de ánimo:', data);

    // Mostrar carga
    const btn = document.getElementById('save-mood');
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';
    btn.disabled = true;

    // Simular envío al backend
    setTimeout(() => {
        // Aquí iría la llamada fetch a tu API Spring Boot
        // fetch('/api/save-mood', {
        //   method: 'POST',
        //   headers: { 'Content-Type': 'application/json' },
        //   body: JSON.stringify(data)
        // })

        btn.innerHTML = '<i class="fas fa-check"></i> Guardado';

        setTimeout(() => {
            btn.innerHTML = '<i class="fas fa-save"></i> Guardar registro';
            btn.disabled = false;
        }, 1500);

        alert('Tu estado de ánimo ha sido registrado correctamente');
    }, 1000);
}

function loadRecommendations() {
    // Simular carga de recomendaciones desde el backend
    const recommendations = [
        {
            icon: 'spa',
            title: 'Meditación guiada',
            description: 'Prueba nuestra sesión de 10 minutos para reducir el estrés'
        },
        {
            icon: 'music',
            title: 'Sonidos relajantes',
            description: 'Escucha nuestra playlist "Océano de Calma"'
        },
        {
            icon: 'book',
            title: 'Diario de gratitud',
            description: 'Escribe 3 cosas por las que estés agradecido hoy'
        },
        {
            icon: 'wind',
            title: 'Ejercicio de respiración',
            description: 'Técnica 4-7-8 para calmar la ansiedad'
        }
    ];

    const container = document.getElementById('recommendations');
    container.innerHTML = '';

    recommendations.forEach(rec => {
        const card = document.createElement('div');
        card.className = 'recommendation-card';
        card.innerHTML = `
            <i class="fas fa-${rec.icon}"></i>
            <h4>${rec.title}</h4>
            <p>${rec.description}</p>
        `;
        container.appendChild(card);
    });
}


