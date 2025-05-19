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

    

}