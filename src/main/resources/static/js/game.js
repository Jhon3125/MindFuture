document.addEventListener('DOMContentLoaded', function () {
    // Elementos del modal
    const modal = document.getElementById('respirModal');
    const closeBtn = document.querySelector('.close-modal');
    const closeActivityBtn = document.getElementById('close-activity');
    const playButtons = document.querySelectorAll('.btn-play');

    // Elementos de la actividad
    const breathingCircle = document.getElementById('breathing-circle');
    const breathingText = document.getElementById('breathing-text');
    const timeLeft = document.getElementById('time-left');
    const activityCompleted = document.getElementById('activity-completed');
    const starsEarned = document.getElementById('stars-earned');

    // Estados de la actividad
    let activityTimer;
    let remainingTime = 120; // 2 minutos en segundos
    let currentActivity = null;

    // Abrir modal cuando se hace clic en un botón
    playButtons.forEach(button => {
        button.addEventListener('click', function () {
            const activityType = this.getAttribute('data-activity-type');
            const duration = parseInt(this.getAttribute('data-duration'));
            const stars = parseInt(this.getAttribute('data-stars'));
            const activityId = this.getAttribute('data-activity-id');

            if (activityType === 'RESPIRACION') {
                currentActivity = {
                    id: activityId,
                    type: activityType,
                    duration: duration,
                    stars: stars
                };

                // Configurar modal
                document.getElementById('modal-activity-title').textContent = 'Respiración Básica';
                remainingTime = duration * 60; // Convertir a segundos
                updateTimerDisplay();

                // Mostrar modal
                modal.style.display = 'block';

                // Iniciar actividad
                startBreathingActivity();
            }
        });
    });

    // Cerrar modal
    closeBtn.addEventListener('click', closeModal);
    closeActivityBtn.addEventListener('click', closeModal);
    window.addEventListener('click', function (event) {
        if (event.target === modal) {
            closeModal();
        }
    });

    function startBreathingActivity() {
        // Resetear estado
        activityCompleted.style.display = 'none';
        breathingCircle.style.display = 'flex';

        // Iniciar temporizador
        activityTimer = setInterval(function () {
            remainingTime--;
            updateTimerDisplay();

            if (remainingTime <= 0) {
                completeActivity();
            }
        }, 1000);

        // Animación de respiración
        let breathState = 'inhale';
        let breathCount = 0;

        const breathInterval = setInterval(function () {
            if (breathState === 'inhale') {
                breathingText.textContent = 'INHALA';
                breathState = 'hold';
            } else if (breathState === 'hold') {
                breathingText.textContent = 'RETIENE';
                breathState = 'exhale';
            } else {
                breathingText.textContent = 'EXHALA';
                breathState = 'inhale';
                breathCount++;
            }

            // Si la actividad se completa, detener la animación
            if (remainingTime <= 0) {
                clearInterval(breathInterval);
            }
        }, 4000);
    }

    function updateTimerDisplay() {
        const minutes = Math.floor(remainingTime / 60);
        const seconds = remainingTime % 60;
        timeLeft.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    }

    function completeActivity() {
        clearInterval(activityTimer);
        breathingCircle.style.display = 'none';
        activityCompleted.style.display = 'block';
        starsEarned.textContent = currentActivity.stars;

        // Aquí deberías enviar una petición al servidor para registrar la actividad completada
        // Ejemplo con fetch:
        fetch('/completar-actividad', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                actividadId: currentActivity.id,
                estrellas: currentActivity.stars
            })
        })
            .then(response => response.json())
            .then(data => {
                console.log('Actividad completada:', data);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    function closeModal() {
        modal.style.display = 'none';
        clearInterval(activityTimer);
        remainingTime = 120;
    }
});