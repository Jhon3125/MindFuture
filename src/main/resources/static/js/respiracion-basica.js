function initRespiracionBasica(actividadData) {
    const elements = {
        circle: document.getElementById('breathing-circle'),
        text: document.getElementById('breathing-text'),
        timer: document.getElementById('time-left'),
        completed: document.getElementById('activity-completed'),
        stars: document.getElementById('stars-earned'),
        container: document.querySelector('.activity-container'),
        body: document.body,
        form: document.getElementById('completeActivityForm'),
        achievementsContainer: document.getElementById('achievements-container'),
        submitBtn: document.getElementById('submitActivityBtn')
    };

    let activityTimer;
    let remainingTime = actividadData.duracion * 60;
    let breathInterval;
    let breathState = 'inhale';

    const TIMINGS = {
        inhale: 5000,
        hold: 7000,
        exhale: 8000
    };

    // Event listener para el botón de enviar
    elements.submitBtn.addEventListener('click', function () {
        submitActivityForm();
    });

    // Mover la función al ámbito del DOMContentLoaded
    function submitActivityForm() {
        // Crear un iframe oculto para manejar la respuesta
        const iframe = document.createElement('iframe');
        iframe.name = 'form-response';
        iframe.style.display = 'none';
        document.body.appendChild(iframe);

        // Configurar el formulario para enviar al iframe
        elements.form.target = 'form-response';
        elements.form.submit();

        // Manejar la respuesta después de enviar el formulario
        iframe.onload = function () {
            try {
                const response = JSON.parse(iframe.contentDocument.body.textContent);
                if (response.success) {
                    elements.stars.textContent = response.estrellasGanadas;
                    if (response.logrosDesbloqueados && response.logrosDesbloqueados.length > 0) {
                        mostrarLogrosDesbloqueados(response.logrosDesbloqueados);
                    }
                    // Redirigir después de mostrar los logros
                    setTimeout(() => {
                        window.location.href = '/mindfulness/mindfulness-game';
                    }, 3000);
                } else {
                    mostrarError(response.error || 'Error al completar la actividad');
                }
            } catch (e) {
                console.error('Error parsing response:', e);
                window.location.href = '/mindfulness/mindfulness-game';
            }
            document.body.removeChild(iframe);
        };
    }

    function startBreathingActivity() {
        activityTimer = setInterval(function () {
            remainingTime--;
            updateTimerDisplay();
            if (remainingTime <= 0) completeActivity();
        }, 1000);

        runBreathCycle();
    }

    function runBreathCycle() {
        if (remainingTime <= 0) return;

        switch (breathState) {
            case 'inhale':
                setBreathState('INHALA', 'state-inhale', 'bg-inhale', TIMINGS.inhale);
                breathState = 'hold';
                break;
            case 'hold':
                setBreathState('RETIENE', 'state-hold', 'bg-hold', TIMINGS.hold);
                breathState = 'exhale';
                break;
            case 'exhale':
                setBreathState('EXHALA', 'state-exhale', 'bg-exhale', TIMINGS.exhale);
                breathState = 'inhale';
                break;
        }
    }

    function setBreathState(text, circleClass, bgClass, duration) {
        elements.text.textContent = text;
        elements.circle.className = `breathing-circle ${circleClass} active`;
        elements.body.className = bgClass;
        breathInterval = setTimeout(runBreathCycle, duration);
    }

    function updateTimerDisplay() {
        const minutes = Math.floor(remainingTime / 60);
        const seconds = remainingTime % 60;
        elements.timer.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;

        if (remainingTime <= 10) {
            elements.timer.style.animation = 'pulseGold 0.5s infinite alternate';
            elements.timer.style.color = '#ff5252';
        }
    }

    function completeActivity() {
        clearInterval(activityTimer);
        clearTimeout(breathInterval);
        elements.container.classList.add('activity-finished');
        elements.completed.style.display = 'block';

        // Mostrar animación de estrellas
        animateStars(actividad.estrellas);
    }

    function animateStars(count) {
        const starsContainer = document.createElement('div');
        starsContainer.className = 'stars-animation-container';

        for (let i = 0; i < count; i++) {
            const star = document.createElement('i');
            star.className = 'fas fa-star';
            star.style.setProperty('--delay', `${i * 0.2}s`);
            star.style.setProperty('--position', `${Math.random() * 100}%`);
            starsContainer.appendChild(star);
        }

        elements.completed.prepend(starsContainer);
        setTimeout(() => starsContainer.remove(), 3000);
    }

    function mostrarLogrosDesbloqueados(logros) {
        const logrosContainer = document.createElement('div');
        logrosContainer.className = 'logros-desbloqueados';

        const title = document.createElement('h3');
        title.textContent = '¡Nuevos Logros Desbloqueados!';
        logrosContainer.appendChild(title);

        const logrosList = document.createElement('div');
        logrosList.className = 'logros-lista';

        logros.forEach((logro, index) => {
            const logroItem = document.createElement('div');
            logroItem.className = 'logro-item';
            logroItem.style.setProperty('--index', index);

            const img = document.createElement('img');
            img.src = logro.imagenUrl;
            img.alt = logro.nombre;

            const h4 = document.createElement('h4');
            h4.textContent = logro.nombre;

            const p = document.createElement('p');
            p.textContent = logro.descripcion;

            logroItem.appendChild(img);
            logroItem.appendChild(h4);
            logroItem.appendChild(p);
            logrosList.appendChild(logroItem);
        });

        logrosContainer.appendChild(logrosList);
        elements.achievementsContainer.appendChild(logrosContainer);
    }

    function mostrarError(mensaje) {
        const errorElement = document.createElement('div');
        errorElement.className = 'error-actividad';
        errorElement.innerHTML = `
                    <p>${mensaje}</p>
                    <button onclick="window.location.href='/mindfulness/mindfulness-game'">Volver a los juegos</button>
                `;
        elements.completed.appendChild(errorElement);
    }

    elements.submitBtn.addEventListener('click', function () {
        submitActivityForm();
    });

    // Iniciar la actividad
    updateTimerDisplay();
    startBreathingActivity();
}