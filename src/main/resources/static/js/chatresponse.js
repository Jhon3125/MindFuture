function getCookie(name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) return parts.pop().split(';').shift();
        }

        document.addEventListener("DOMContentLoaded", () => {
            const analyzeBtn = document.getElementById("analyze-text");
            const moodText = document.getElementById("mood-text");
            const responseContainer = document.getElementById("ia-response");
            const responseText = document.getElementById("ia-response-text");

            analyzeBtn.addEventListener("click", async (e) => {
                e.preventDefault();
                const mensaje = moodText.value.trim();

                if (!mensaje) {
                    responseContainer.style.display = "block";
                    responseText.innerText = "Por favor, describe cómo te sientes para realizar el análisis.";
                    return;
                }

                responseText.innerText = "Analizando...";
                responseContainer.style.display = "block";

                try {
                    const csrfToken = getCookie("XSRF-TOKEN");

                    const res = await fetch("/chat", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded",
                            "X-XSRF-TOKEN": csrfToken
                        },
                        credentials: "include", // enviar cookies de sesión
                        body: new URLSearchParams({ message: mensaje })
                    });

                    const text = await res.text();
                    responseText.innerText = text;
                } catch (err) {
                    responseText.innerText = "Ocurrió un error al obtener la respuesta.";
                }
            });
        });
