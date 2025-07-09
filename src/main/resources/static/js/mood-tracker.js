let currentDate = new Date();
document.addEventListener('DOMContentLoaded', function () {
    // Configuración inicial
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
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
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

    document.querySelectorAll('input[name="mood"]').forEach(radio => {
        radio.addEventListener('change', function () {
            const value = parseInt(this.value);

            // Convertimos el valor a moodValue (estrés del 1 al 100)
            let emocionDetectada = ''; // nueva

            switch (value) {
                case 5:
                    moodValue = 90;
                    tags = ['Excelente', 'Positivo'];
                    analysis = 'Te sientes excelente, ¡qué buena vibra!';
                    emocionDetectada = 'excelente';
                    break;
                case 4:
                    moodValue = 75;
                    tags = ['Bien'];
                    analysis = 'Parece que estás teniendo un buen día.';
                    emocionDetectada = 'bien';
                    break;
                case 3:
                    moodValue = 55;
                    tags = ['Normal'];
                    analysis = 'Un día equilibrado, sin altibajos.';
                    emocionDetectada = 'normal';
                    break;
                case 2:
                    moodValue = 35;
                    tags = ['Mal'];
                    analysis = 'Parece que estás algo decaído.';
                    emocionDetectada = 'mal';
                    break;
                case 1:
                    moodValue = 15;
                    tags = ['Pésimo'];
                    analysis = 'Ánimo. Habla con alguien.';
                    emocionDetectada = 'pesimo';
                    break;
            }


            // Llamamos a updateUI con los datos simulados
            updateUI({
                moodValue,
                moodTags: tags,
                moodAnalysis: analysis,
                biometrics: {
                    heartRate: 72,
                    sleepHours: 7.5,
                    energyLevel: 6
                },
                emocionDetectada // <--- esto es nuevo
            });

        });
    });
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


function updateDateDisplay(date) {
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('current-date').textContent = date.toLocaleDateString('es-ES', options);
}

function loadMoodData(date) {
    const fechaISO = date.toISOString().split('T')[0];
    const emailUsuario = 'carlosperez@gmail.com';

    console.log(`🔄 Cargando datos para ${fechaISO}`);

    fetch(`/api/emociones?email=${encodeURIComponent(emailUsuario)}&fecha=${fechaISO}`)
        .then(async res => {
            const text = await res.text();
            if (!res.ok) {
                if (res.status === 404) {
                    console.log(`ℹ️ No hay emoción registrada para ${fechaISO}.`);
                    return null; // <-- No hay error, solo sin datos
                }
                throw new Error(text);
            }
            return JSON.parse(text);
        })
        .then(data => {
            if (!data) return; // No hacer nada si no hay datos

            updateUI({
                moodValue: data.nivelEstres,
                moodTags: data.tags || [],
                moodAnalysis: data.recomendacion || '',
                biometrics: {
                    heartRate: data.heartRate || 72,
                    sleepHours: data.sleepHours || 7,
                    energyLevel: data.energyLevel || 5
                },
                emocionDetectada: data.emocionDetectada
            });
        })
        .catch(err => {
            console.error('❌ Error al cargar datos desde el backend:', err.message);
        });
}



function updateUI(data) {
    // Actualizar valor
    document.getElementById('mood-value').textContent = data.moodValue;

    const moodFace = document.getElementById('mood-face');
    // resetear cara...

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

    // Lógica para estado de ánimo
    const mouth = moodFace.querySelector('.mouth');
    mouth.className = 'mouth';

    if (data.moodValue >= 80) {
        moodFace.classList.add('happy');
        mouth.classList.add('happy');
    } else if (data.moodValue >= 50) {
        moodFace.classList.add('neutral');
        mouth.classList.add('neutral');
    } else {
        moodFace.classList.add('sad');
        mouth.classList.add('sad');
    }

    // NO guardar automáticamente al seleccionar carita
    // guardarDatosEmocion({
    //     ...data,
    //     emocionDetectada: data.emocionDetectada || ''
    // });

}


function guardarDatosEmocion(data) {
    const emocionDetectada = data.emocionDetectada || (
        data.moodValue >= 85 ? 'excelente' :
            data.moodValue >= 70 ? 'bien' :
                data.moodValue >= 55 ? 'normal' :
                    data.moodValue >= 30 ? 'mal' : 'pesimo'
    );

    let recomendacion = '';
    if (data.moodValue >= 80) {
        recomendacion = 'Sigue así, mantén tus hábitos positivos.';
    } else if (data.moodValue >= 50) {
        recomendacion = 'Tómate un descanso y haz algo que te guste.';
    } else {
        recomendacion = 'Habla con alguien de confianza o escribe lo que sientes.';
    }

    const emocionData = {
        emocionDetectada: emocionDetectada,
        nivelEstres: data.moodValue,
        recomendacion: recomendacion,
        emailUsuario: 'carlosperez@gmail.com'
    };

    console.log("📤 Enviando datos a /api/emociones:", emocionData);

    fetch('/api/emociones', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(emocionData)
    })
        .then(res => {
            if (!res.ok) throw new Error('Error en la respuesta del servidor');
            console.log('✅ Emoción guardada correctamente');
        })
        .catch(err => {
            console.error('❌ Error al guardar la emoción:', err);
        });
}

function updateChart(timeRange) {
    console.log(`Actualizando gráfico para ${timeRange}`);
}

function analyzeText() {
    const text = document.getElementById('mood-text').value;
    if (!text.trim()) {
        alert('Por favor escribe cómo te sientes');
        return;
    }

    const btn = document.getElementById('analyze-text');
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Analizando...';
    btn.disabled = true;

    setTimeout(() => {
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

        updateUI({ moodValue, moodTags: tags, moodAnalysis: analysis, biometrics: { heartRate: 72, sleepHours: 7.5, energyLevel: 6 } });

        btn.innerHTML = '<i class="fas fa-robot"></i> Analizar con IA';
        btn.disabled = false;

        alert('Análisis completado. Hemos actualizado tu estado emocional.');
    }, 2000);
}

function syncWearable() {
    const btn = document.getElementById('sync-wearable');
    btn.innerHTML = '<i class="fas fa-sync fa-spin"></i> Sincronizando...';
    btn.disabled = true;

    setTimeout(() => {
        document.getElementById('heart-rate').textContent = Math.floor(Math.random() * 30) + 60;
        document.getElementById('sleep-hours').textContent = (Math.random() * 3 + 5).toFixed(1);
        document.getElementById('energy-level').textContent = Math.floor(Math.random() * 4) + 4;

        btn.innerHTML = '<i class="fas fa-bluetooth-b"></i> Sincronizar wearable';
        btn.disabled = false;

        alert('Datos biométricos actualizados correctamente');
    }, 1500);
}

function saveMood() {
    const btn = document.getElementById('save-mood');
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';
    btn.disabled = true;

    // Obtener los datos mostrados en la pantalla
    const nivelEstres = parseInt(document.getElementById('mood-value').textContent);
    const emocionDetectada = document.getElementById('mood-face').classList.contains('happy')
        ? 'feliz'
        : document.getElementById('mood-face').classList.contains('neutral')
            ? 'neutral'
            : 'triste';

    let recomendacion = '';
    if (nivelEstres >= 80) {
        recomendacion = 'Sigue así, mantén tus hábitos positivos.';
    } else if (nivelEstres >= 50) {
        recomendacion = 'Tómate un descanso y haz algo que te guste.';
    } else {
        recomendacion = 'Habla con alguien de confianza o escribe lo que sientes.';
    }

    const emocionData = {
        emocionDetectada: emocionDetectada,
        nivelEstres: nivelEstres,
        recomendacion: recomendacion,
        emailUsuario: 'carlosperez@gmail.com'
    };


    fetch('/api/emociones', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(emocionData)
    })
        .then(async res => {
            const text = await res.text();
            if (!res.ok) throw new Error(text);
            console.log("✅ Emoción guardada:", text);
            alert("Estado emocional guardado correctamente");
        })
        .catch(err => {
            console.error('❌ Error al guardar la emoción:', err);
            alert("Ocurrió un error al guardar el estado emocional");
        })
        .finally(() => {
            btn.innerHTML = '<i class="fas fa-save"></i> Guardar registro';
            btn.disabled = false;
        });
}

function loadRecommendations() {
    const recommendations = [
        { icon: 'spa', title: 'Meditación guiada', description: 'Prueba una sesión de 10 minutos para reducir el estrés' },
        { icon: 'music', title: 'Sonidos relajantes', description: 'Escucha la playlist "Océano de Calma"' },
        { icon: 'book', title: 'Diario de gratitud', description: 'Escribe 3 cosas por las que estés agradecido hoy' },
        { icon: 'wind', title: 'Ejercicio de respiración', description: 'Técnica 4-7-8 para calmar la ansiedad' }
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

