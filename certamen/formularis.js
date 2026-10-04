/* CONFIGURACIÓ: canvia aquests valors quan tingueu el servei de formularis. */
const CONFIG = {
  // Exemple de servei: URL real de l'endpoint de recepció del formulari de contacte.
  contacteEndpoint: 'https://script.google.com/macros/s/AKfycby9FrwShLTUE-LHcMXyDNl1i-leyWwvuvpyC_w0QIBiRfCI3IahzRSYBNf1ntayWBErYA/exec',
  // Endpoint DIFERENT per a les inscripcions, per separar els dos correus.
  inscripcionsEndpoint: '',
  // Activa-ho només dins el termini oficial i després de configurar l'endpoint.
  inscripcionsObertes: false
};

let idiomaActual = 'ca';
function canviaIdioma(idioma) {
  idiomaActual = formTranslations[idioma] ? idioma : 'ca';
  document.documentElement.lang = idiomaActual;
  const textos = formTranslations[idiomaActual];
  document.querySelectorAll('[data-f]').forEach(element => {
    const clau = element.dataset.f;
    if (textos[clau]) element.textContent = textos[clau];
  });
  document.querySelectorAll('[data-lang]').forEach(boto =>
    boto.setAttribute('aria-pressed', String(boto.dataset.lang === idiomaActual)));
  try { localStorage.setItem('certamen-lang', idiomaActual); } catch (_) {}
}

// El formulari d'inscripció està ocult fora de termini. L'endpoint és obligatori.
const inscripcio = document.querySelector('#registration-form');
if (inscripcio && CONFIG.inscripcionsObertes && CONFIG.inscripcionsEndpoint) {
  inscripcio.hidden = false;
  document.querySelector('#registration-closed').hidden = true;
}

// Enviament asíncron a un servei de formularis que accepti POST de FormData.
// Sense endpoint no es fa cap enviament ni es mostra un missatge d'èxit fals.
async function enviaFormulari(formulari, endpoint) {
  const estat = formulari.querySelector('.form-status');
  if (!endpoint) {
    estat.textContent = formTranslations[idiomaActual].setupText;
    return;
  }
  const boto = formulari.querySelector('button[type="submit"]');
  boto.disabled = true;
  estat.textContent = '';
  try {
    // Preparem les dades. El formulari de contacte utilitza noms de camps
    // llegibles a l'HTML, però Google Apps Script espera nom/assumpte/missatge.
    let dades = new FormData(formulari);

    if (formulari.id === 'contact-form') {
      const nom = dades.get('name') || '';
      const banda = dades.get('band') || '';
      const missatge = dades.get('message') || '';

      dades = new FormData();
      dades.append('nom', nom);
      dades.append('email', formulari.elements.email.value);
      dades.append('assumpte', banda ? 'Consulta web - ' + banda : 'Consulta web');
      dades.append('missatge', banda ? 'Banda o entitat: ' + banda + '\n\n' + missatge : missatge);
    }

    // Apps Script respon des d'un domini de Google diferent. Amb no-cors podem
    // enviar el formulari des de localhost i, més endavant, des de GitHub Pages
    // sense que el navegador bloquegi l'enviament per CORS.
    await fetch(endpoint, {
      method: 'POST',
      body: dades,
      mode: 'no-cors'
    });

    estat.textContent = formTranslations[idiomaActual].confirmation;
    formulari.reset();
  } catch (error) {
    estat.textContent = formTranslations[idiomaActual].sendError;
    console.error('Error en enviar el formulari:', error);
  } finally { boto.disabled = false; }
}

const contacte = document.querySelector('#contact-form');
if (contacte) contacte.addEventListener('submit', event => {
  event.preventDefault();
  if (contacte.reportValidity()) enviaFormulari(contacte, CONFIG.contacteEndpoint);
});
if (inscripcio) inscripcio.addEventListener('submit', event => {
  event.preventDefault();
  if (inscripcio.reportValidity()) enviaFormulari(inscripcio, CONFIG.inscripcionsEndpoint);
});

document.querySelectorAll('[data-lang]').forEach(boto =>
  boto.addEventListener('click', () => canviaIdioma(boto.dataset.lang)));
const menu = document.querySelector('.menu-toggle');
const nav = document.querySelector('#nav');
menu.addEventListener('click', () => {
  const obert = nav.classList.toggle('open');
  menu.setAttribute('aria-expanded', String(obert));
});
try { canviaIdioma(localStorage.getItem('certamen-lang') || 'ca'); }
catch (_) { canviaIdioma('ca'); }
