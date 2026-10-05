// SELECTOR D'IDIOMA COMÚ
// Manté el desplegable compacte i mostra el codi de l'idioma actiu.
(function () {

  // Traduccions del menú principal. Es gestionen aquí perquè aquest fitxer
  // és comú a totes les pàgines i així la navegació sempre canvia d'idioma.
  var navTranslations = {
    ca: { home:'Inici', contest:'El Certamen', participation:'Participació', program:'Programa', results:'Resultats', news:'Actualitat', territory:'La Sénia i el territori', faq:'FAQ', contact:'Contacte' },
    es: { home:'Inicio', contest:'El Certamen', participation:'Participación', program:'Programa', results:'Resultados', news:'Actualidad', territory:'La Sénia y el territorio', faq:'FAQ', contact:'Contacto' },
    gl: { home:'Inicio', contest:'O Certame', participation:'Participación', program:'Programa', results:'Resultados', news:'Actualidade', territory:'La Sénia e o territorio', faq:'FAQ', contact:'Contacto' },
    pt: { home:'Início', contest:'O Certame', participation:'Participação', program:'Programa', results:'Resultados', news:'Atualidade', territory:'La Sénia e o território', faq:'FAQ', contact:'Contacto' },
    en: { home:'Home', contest:'The Competition', participation:'Participation', program:'Programme', results:'Results', news:'News', territory:'La Sénia and the surrounding area', faq:'FAQ', contact:'Contact' }
  };

  function updateNavigation(lang) {
    var texts = navTranslations[lang] || navTranslations.ca;
    document.querySelectorAll('.nav [data-nav]').forEach(function (link) {
      var key = link.dataset.nav;
      if (texts[key]) link.textContent = texts[key];
    });
  }

  function updateLanguageLabel(lang) {
    document.querySelectorAll('.lang-current').forEach(function (el) {
      el.textContent = String(lang || 'ca').toUpperCase();
    });
    document.querySelectorAll('.lang-menu button[data-lang]').forEach(function (button) {
      button.setAttribute('aria-pressed', String(button.dataset.lang === lang));
    });
  }

  function storedLanguage() {
    try {
      return localStorage.getItem('certamen-lang') || localStorage.getItem('certamenLang') || 'ca';
    } catch (_) {
      return 'ca';
    }
  }

  document.addEventListener('DOMContentLoaded', function () {
    var initialLang = storedLanguage();
    updateLanguageLabel(initialLang);
    updateNavigation(initialLang);

    document.querySelectorAll('.lang-menu button[data-lang]').forEach(function (button) {
      button.addEventListener('click', function () {
        var lang = button.dataset.lang;
        updateLanguageLabel(lang);
        updateNavigation(lang);
        try {
          // Conservem les dues claus perquè les pàgines existents utilitzen
          // temporalment noms diferents per recordar l'idioma.
          localStorage.setItem('certamen-lang', lang);
          localStorage.setItem('certamenLang', lang);
        } catch (_) {}
        var details = button.closest('.langs');
        if (details) details.removeAttribute('open');
      });
    });
  });
})();
