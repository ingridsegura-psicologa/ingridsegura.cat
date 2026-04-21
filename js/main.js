document.addEventListener("DOMContentLoaded", function () {

  const toggle = document.querySelector('.nav-toggle');
  const nav = document.querySelector('.main-nav');

  if (toggle && nav) {
    toggle.addEventListener('click', function () {
      const isOpen = nav.classList.toggle('is-open');
      toggle.classList.toggle('is-open', isOpen);
      toggle.setAttribute('aria-expanded', String(isOpen));
    });

    nav.querySelectorAll('a').forEach(function (link) {
      link.addEventListener('click', function () {
        nav.classList.remove('is-open');
        toggle.classList.remove('is-open');
        toggle.setAttribute('aria-expanded', 'false');
      });
    });
  }

  const form = document.getElementById("contact-form");
  const status = document.getElementById("form-status");

  if (form && status) {
    const submitButton = form.querySelector('button[type="submit"]');
    const phoneInput = document.getElementById("phone");
    const contactOptions = form.querySelectorAll('input[name="contact_preference"]');

    function updatePhoneRequired() {
      if (!phoneInput) return;
      const selected = form.querySelector('input[name="contact_preference"]:checked')?.value;
      phoneInput.required = selected === "phone" || selected === "whatsapp";
    }

    contactOptions.forEach(option => {
      option.addEventListener("change", updatePhoneRequired);
    });

    updatePhoneRequired();

    form.addEventListener("submit", async function (e) {
      e.preventDefault();

      submitButton.disabled = true;
      status.style.display = "block";

      const isES = document.documentElement.lang === "es";
      status.textContent = isES ? "Enviando..." : "Enviant...";

      const data = new FormData(form);

      try {
        const response = await fetch(form.action, {
          method: "POST",
          body: data,
          headers: { "Accept": "application/json" }
        });

        if (response.ok) {
          window.location.href = isES
            ? "https://ingridsegura.cat/gracies_es.html"
            : "https://ingridsegura.cat/gracies.html";
        } else {
          status.textContent = isES
            ? "Hay un error en el envío. Vuelve a probar."
            : "Hi ha hagut un error en l'enviament. Torna-ho a provar.";

          submitButton.disabled = false;
        }
      } catch (error) {
        status.textContent = isES
          ? "No se ha podido enviar el formulario. Revisa la conexión y vuelve a probar."
          : "No s'ha pogut enviar el formulari. Revisa la connexió i torna-ho a provar.";

        submitButton.disabled = false;
      }
    });
  }
  
    const banner = document.getElementById("cookie-banner");
  const acceptBtn = document.getElementById("accept-cookies");
  const rejectBtn = document.getElementById("reject-cookies");

  const consent = localStorage.getItem("cookie-consent");

  if (consent === "accepted") {
    loadAnalytics();
    banner.style.display = "none";
  } else if (consent === "rejected") {
    banner.style.display = "none";
  }

  acceptBtn?.addEventListener("click", function () {
    localStorage.setItem("cookie-consent", "accepted");
    loadAnalytics();
    banner.style.display = "none";
  });

  rejectBtn?.addEventListener("click", function () {
    localStorage.setItem("cookie-consent", "rejected");
    banner.style.display = "none";
  });

  function loadAnalytics() {
    const script = document.createElement("script");
    script.src = "https://www.googletagmanager.com/gtag/js?id=G-D55G8427FP";
    script.async = true;
    document.head.appendChild(script);

    window.dataLayer = window.dataLayer || [];
    function gtag(){dataLayer.push(arguments);}
    window.gtag = gtag;

    gtag('js', new Date());
    gtag('config', 'G-D55G8427FP', { anonymize_ip: true });
  }

});