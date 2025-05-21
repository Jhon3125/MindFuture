// Función principal que se ejecuta cuando el DOM está cargado
function initRelaxationActivity(actividadData) {
    const actividad = {
        id: actividadData.idActividad,
        duracion: actividadData.duracionEstimada,
        estrellas: actividadData.estrellasRecompensa
    };

    // Elementos del DOM
    const elements = {
        bodyParts: document.querySelectorAll('.body-part'),
        instructions: document.getElementById('current-instruction'),
        timer: document.getElementById('time-left'),
        completed: document.getElementById('activity-completed'),
        stars: document.getElementById('stars-earned'),
        container: document.querySelector('.activity-container'),
        form: document.getElementById('completeActivityForm'),
        achievementsContainer: document.getElementById('achievements-container'),
        submitBtn: document.getElementById('submitActivityBtn'),
        audio: document.getElementById('background-audio'),
        playPauseBtn: document.getElementById('play-pause-btn'),
        volumeSlider: document.getElementById('volume-slider'),
        muteBtn: document.getElementById('mute-btn'),
        ttsToggle: document.getElementById('tts-toggle')
    };

    // Variables de estado
    let activityTimer;
    let remainingTime = actividad.duracion * 60;
    let currentPart = 0;
    let isAudioPlaying = false;
    let lastVolume = 0.5;
    let ttsEnabled = true;

    const bodyPartsGroups = [
        ['head'],
        ['shoulders'],
        ['arms1', 'arms2'],
        ['hands1', 'hands2'],
        ['chest'],
        ['legs'],
        ['feet']
    ];

    const instructions = [
        "Relaja tu cabeza... siente cómo la tensión se disipa",
        "Deja caer los hombros... libéralos de toda tensión",
        "Relaja tus brazos... déjalos pesados y tranquilos",
        "Afloja tus manos... siente cómo se suavizan",
        "Relaja tu pecho... respira profundamente",
        "Libera la tensión de tus piernas... déjalas descansar",
        "Relaja tus pies... siéntelos en contacto con el suelo",
        "Ahora relaja todo tu cuerpo... disfruta de esta calma"
    ];

    // Función para leer texto con TTS
    function speak(text) {
        if (!ttsEnabled) return;

        if ('speechSynthesis' in window) {
            window.speechSynthesis.cancel();
            const utterance = new SpeechSynthesisUtterance(text);
            utterance.lang = 'es-ES';
            utterance.rate = 0.7;
            utterance.pitch = 1;
            utterance.volume = 0.7;

            const voices = window.speechSynthesis.getVoices();
            const spanishVoice = voices.find(voice =>
                voice.lang === 'es-ES' && voice.name.includes('Female'));

            if (spanishVoice) {
                utterance.voice = spanishVoice;
            }

            window.speechSynthesis.speak(utterance);
        }
    }

    // Control de audio
    function initAudio() {
        elements.audio.volume = 0.5;
        elements.playPauseBtn.addEventListener('click', togglePlayPause);
        elements.volumeSlider.addEventListener('input', updateVolume);
        elements.muteBtn.addEventListener('click', toggleMute);
        startAudio();
        elements.audio.addEventListener('play', updateAudioControls);
        elements.audio.addEventListener('pause', updateAudioControls);
        elements.audio.addEventListener('volumechange', updateAudioControls);
    }

    function startAudio() {
        elements.audio.play().then(() => {
            isAudioPlaying = true;
            updateAudioControls();
        }).catch(error => {
            console.log("Autoplay bloqueado:", error);
            showAudioButton();
        });
    }

    function showAudioButton() {
        const audioButton = document.createElement('button');
        audioButton.className = 'btn-audio';
        audioButton.innerHTML = '<i class="fas fa-volume-up"></i> Activar audio de relajación';
        audioButton.onclick = () => {
            elements.audio.play();
            audioButton.remove();
        };
        document.querySelector('.audio-controls').prepend(audioButton);
    }

    function togglePlayPause() {
        if (elements.audio.paused) {
            elements.audio.play();
        } else {
            elements.audio.pause();
        }
    }

    function updateVolume() {
        elements.audio.volume = elements.volumeSlider.value;
        lastVolume = elements.audio.volume;
        if (elements.audio.muted && elements.audio.volume > 0) {
            elements.audio.muted = false;
        }
        updateAudioControls();
    }

    function toggleMute() {
        if (elements.audio.muted) {
            elements.audio.muted = false;
            elements.audio.volume = lastVolume;
            elements.volumeSlider.value = lastVolume;
        } else {
            elements.audio.muted = true;
            lastVolume = elements.audio.volume;
            elements.volumeSlider.value = 0;
        }
        updateAudioControls();
    }

    function updateAudioControls() {
        if (elements.audio.paused) {
            elements.playPauseBtn.innerHTML = '<i class="fas fa-play"></i>';
        } else {
            elements.playPauseBtn.innerHTML = '<i class="fas fa-pause"></i>';
        }

        if (elements.audio.muted || elements.audio.volume === 0) {
            elements.muteBtn.innerHTML = '<i class="fas fa-volume-mute"></i>';
        } else if (elements.audio.volume < 0.5) {
            elements.muteBtn.innerHTML = '<i class="fas fa-volume-down"></i>';
        } else {
            elements.muteBtn.innerHTML = '<i class="fas fa-volume-up"></i>';
        }

        if (!elements.audio.muted) {
            elements.volumeSlider.value = elements.audio.volume;
        }
    }

    // Función para enviar el formulario
    function submitActivityForm() {
        const iframe = document.createElement('iframe');
        iframe.name = 'form-response';
        iframe.style.display = 'none';
        document.body.appendChild(iframe);

        elements.form.target = 'form-response';
        elements.form.submit();

        iframe.onload = function () {
            try {
                const response = JSON.parse(iframe.contentDocument.body.textContent);
                if (response.success) {
                    elements.stars.textContent = response.estrellasGanadas;
                    if (response.logrosDesbloqueados && response.logrosDesbloqueados.length > 0) {
                        mostrarLogrosDesbloqueados(response.logrosDesbloqueados);
                    }
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

    // Funciones de la actividad de relajación
    function startRelaxationActivity() {
        activityTimer = setInterval(function () {
            remainingTime--;
            updateTimerDisplay();
            if (remainingTime <= 0) completeActivity();
        }, 1000);

        setTimeout(guideRelaxation, 2000);
    }

    function guideRelaxation() {
        if (remainingTime <= 0) return;

        elements.bodyParts.forEach(part => {
            part.classList.remove('active', 'relaxed');
        });

        if (currentPart < bodyPartsGroups.length) {
            const currentGroup = bodyPartsGroups[currentPart];
            currentGroup.forEach(partId => {
                const partElement = document.getElementById(partId);
                if (partElement) partElement.classList.add('active');
            });

            const currentInstruction = instructions[currentPart];
            elements.instructions.textContent = currentInstruction;
            speak(currentInstruction);

            setTimeout(() => {
                currentGroup.forEach(partId => {
                    const partElement = document.getElementById(partId);
                    if (partElement) {
                        partElement.classList.remove('active');
                        partElement.classList.add('relaxed');
                    }
                });
                currentPart++;
                guideRelaxation();
            }, 8000);
        } else if (currentPart === bodyPartsGroups.length) {
            const completeInstruction = instructions[currentPart];
            elements.instructions.textContent = completeInstruction;
            speak(completeInstruction);

            elements.bodyParts.forEach(part => {
                part.classList.add('relaxed');
            });
            currentPart++;
            setTimeout(guideRelaxation, 10000);
        } else {
            currentPart = 0;
            setTimeout(guideRelaxation, 2000);
        }
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
        elements.container.classList.add('activity-finished');
        elements.completed.style.display = 'block';

        // Animación de confeti
        const confettiElements = document.querySelectorAll('.confetti');
        confettiElements.forEach((confetti, index) => {
            confetti.style.left = `${Math.random() * 100}%`;
            confetti.style.animationDelay = `${index * 0.2}s`;
        });

        animateStars(actividad.estrellas);
        elements.audio.pause();
        updateAudioControls();
    }

    function animateStars(count) {
        const starsContainer = elements.stars;
        starsContainer.innerHTML = '';

        for (let i = 0; i < count; i++) {
            const star = document.createElement('i');
            star.className = 'fas fa-star';
            star.style.animationDelay = `${i * 0.3}s`;
            starsContainer.appendChild(star);
        }
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

    // Event listeners
    elements.ttsToggle.addEventListener('click', function () {
        ttsEnabled = !ttsEnabled;
        if (ttsEnabled) {
            elements.ttsToggle.innerHTML = '<i class="fas fa-volume-up"></i> Voz ON';
            if (currentPart < instructions.length) {
                speak(instructions[currentPart]);
            }
        } else {
            elements.ttsToggle.innerHTML = '<i class="fas fa-volume-mute"></i> Voz OFF';
            window.speechSynthesis.cancel();
        }
    });

    elements.submitBtn.addEventListener('click', submitActivityForm);

    // Inicialización
    initAudio();
    updateTimerDisplay();
    startRelaxationActivity();
}