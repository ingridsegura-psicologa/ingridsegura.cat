// PORTADA: traduccions i interaccions.
// Els atributs data-t de l’HTML indiquen quin text es tradueix.
// Mantén les mateixes claus a totes les llengües: CA, ES, GL, PT i EN.

// Edit the text for each language here.
const translations = {
  ca: {
    menu:'Menú',
    contest:'El certamen',
    participation:'Participació',
    program:'Programa',
    results:'Resultats',
    history:'Història',
    news:'Actualitat',
    faq:'Preguntes freqüents', territory:'La Sénia i el territori',
    eyebrow:'LA SÉNIA · CATALUNYA',
    heroTitle:'La música ens mou.',
    heroSub:'Certamen Internacional de Bandes de Música Vila de la Sénia',
    discover:'Descobreix el certamen ↗',
    join:'Participa-hi ↗',
    introTitle:'Un escenari per a la música de banda.',
    introText:'La Sénia es converteix en un punt de trobada per a bandes, directors i públic. Descobreix la música, les persones i la història del Certamen.',
    knowMore:'Coneix la nostra història →',
    exploreEyebrow:'DESCOBREIX',
    exploreTitle:'Viu el Certamen',
    tileJoin:'Bases i inscripcions',
    tileProgram:'Actuacions i horaris',
    tileResults:'Premis i classificacions',
    tileHistory:'Edicions anteriors',
    participateTitle:'La teva banda.<br>El nostre escenari.',
    participateText:'Consulta les bases i prepara la participació de la teva banda en una pròxima edició.',
    contactJoin:'Demana informació ↗',
    programTitle:'Cada actuació, un moment únic.',
    programText:'El programa de la pròxima edició es publicarà aquí quan estigui disponible.',
    resultsTitle:'El talent deixa empremta.',
    resultsText:'Consulta els resultats oficials i els reconeixements de les edicions del Certamen.',
    historyTitle:'Una història que continua sonant.',
    historyText:'Un espai per descobrir les bandes participants i els moments de les edicions anteriors.',
    newsTitle:'Segueix el ritme del Certamen.',
    newsText:'Aquí trobaràs les novetats i els anuncis de les pròximes edicions.',
    footer:'Certamen Internacional de Bandes de Música Vila de la Sénia'
  },
  es: {
    menu:'Menú',
    contest:'El certamen',
    participation:'Participación',
    program:'Programa',
    results:'Resultados',
    history:'Historia',
    news:'Actualidad',
    faq:'Preguntas frecuentes', territory:'La Sénia y el territorio',
    eyebrow:'LA SÉNIA · CATALUÑA',
    heroTitle:'La música nos mueve.',
    heroSub:'Certamen Internacional de Bandas de Música Vila de la Sénia',
    discover:'Descubre el certamen ↗',
    join:'Participa ↗',
    introTitle:'Un escenario para la música de banda.',
    introText:'La Sénia se convierte en un punto de encuentro para bandas, directores y público. Descubre la música, las personas y la historia del Certamen.',
    knowMore:'Conoce nuestra historia →',
    exploreEyebrow:'DESCUBRE',
    exploreTitle:'Vive el Certamen',
    tileJoin:'Bases e inscripciones',
    tileProgram:'Actuaciones y horarios',
    tileResults:'Premios y clasificaciones',
    tileHistory:'Ediciones anteriores',
    participateTitle:'Tu banda.<br>Nuestro escenario.',
    participateText:'Consulta las bases y prepara la participación de tu banda en una próxima edición.',
    contactJoin:'Solicita información ↗',
    programTitle:'Cada actuación, un momento único.',
    programText:'El programa de la próxima edición se publicará aquí cuando esté disponible.',
    resultsTitle:'El talento deja huella.',
    resultsText:'Consulta los resultados oficiales y los reconocimientos de las ediciones del Certamen.',
    historyTitle:'Una historia que sigue sonando.',
    historyText:'Un espacio para descubrir las bandas participantes y los momentos de ediciones anteriores.',
    newsTitle:'Sigue el ritmo del Certamen.',
    newsText:'Aquí encontrarás las novedades y los anuncios de las próximas ediciones.',
    footer:'Certamen Internacional de Bandas de Música Vila de la Sénia'
  },
  en: {
    menu:'Menu',
    contest:'The contest',
    participation:'Participation',
    program:'Program',
    results:'Results',
    history:'History',
    news:'News',
    faq:'Frequently asked questions', territory:'La Sénia & the region',
    eyebrow:'LA SÉNIA · CATALONIA',
    heroTitle:'Music moves us.',
    heroSub:'Vila de la Sénia International Wind Band Competition',
    discover:'Discover the contest ↗',
    join:'Take part ↗',
    introTitle:'A stage for wind band music.',
    introText:'La Sénia brings together bands, conductors and audiences. Discover the music, the people and the history of the contest.',
    knowMore:'Discover our history →',
    exploreEyebrow:'EXPLORE',
    exploreTitle:'Experience the contest',
    tileJoin:'Rules and registration',
    tileProgram:'Performances and times',
    tileResults:'Awards and rankings',
    tileHistory:'Previous editions',
    participateTitle:'Your band.<br>Our stage.',
    participateText:'Read the rules and prepare your band to take part in an upcoming edition.',
    contactJoin:'Request information ↗',
    programTitle:'Every performance, a unique moment.',
    programText:'The next edition’s program will be published here when available.',
    resultsTitle:'Talent leaves its mark.',
    resultsText:'Explore official results and awards from past editions of the contest.',
    historyTitle:'A history that keeps playing.',
    historyText:'Discover participating bands and memorable moments from previous editions.',
    newsTitle:'Stay in tune with the contest.',
    newsText:'Find news and announcements about upcoming editions here.',
    footer:'Vila de la Sénia International Wind Band Competition'
  }
  ,pt: {
    menu:'Menu', contest:'O certame', participation:'Participação', program:'Programa',
    results:'Resultados', history:'História', news:'Notícias', faq:'Perguntas frequentes', territory:'La Sénia e o território',
    eyebrow:'LA SÉNIA · CATALUNHA', heroTitle:'A música move-nos.',
    heroSub:'Certame Internacional de Bandas de Música Vila de la Sénia',
    discover:'Descobre o certame ↗', join:'Participa ↗',
    introTitle:'Um palco para a música de banda.',
    introText:'La Sénia torna-se um ponto de encontro para bandas, maestros e público. Descobre a música, as pessoas e a história do Certame.',
    knowMore:'Conhece a nossa história →', exploreEyebrow:'DESCOBRE',
    exploreTitle:'Vive o Certame', tileJoin:'Regulamento e inscrições',
    tileProgram:'Atuações e horários', tileResults:'Prémios e classificações',
    tileHistory:'Edições anteriores', participateTitle:'A tua banda.<br>O nosso palco.',
    participateText:'Consulta o regulamento e prepara a participação da tua banda numa próxima edição.',
    contactJoin:'Pede informações ↗', programTitle:'Cada atuação, um momento único.',
    programText:'O programa da próxima edição será publicado aqui quando estiver disponível.',
    resultsTitle:'O talento deixa a sua marca.',
    resultsText:'Consulta os resultados oficiais e os prémios das edições do Certame.',
    historyTitle:'Uma história que continua a soar.',
    historyText:'Um espaço para descobrir as bandas participantes e os momentos das edições anteriores.',
    newsTitle:'Acompanha o ritmo do Certame.',
    newsText:'Aqui encontrarás as novidades e os anúncios das próximas edições.',
    footer:'Certame Internacional de Bandas de Música Vila de la Sénia'
  },
  gl: {
    menu:'Menú', contest:'O certame', participation:'Participación', program:'Programa',
    results:'Resultados', history:'Historia', news:'Actualidade', faq:'Preguntas frecuentes', territory:'La Sénia y el territorio',
    eyebrow:'LA SÉNIA · CATALUÑA', heroTitle:'A música móvenos.',
    heroSub:'Certame Internacional de Bandas de Música Vila de la Sénia',
    discover:'Descubre o certame ↗', join:'Participa ↗',
    introTitle:'Un escenario para a música de banda.',
    introText:'La Sénia convértese nun punto de encontro para bandas, directores e público. Descubre a música, as persoas e a historia do Certame.',
    knowMore:'Coñece a nosa historia →', exploreEyebrow:'DESCUBRE',
    exploreTitle:'Vive o Certame', tileJoin:'Bases e inscricións',
    tileProgram:'Actuacións e horarios', tileResults:'Premios e clasificacións',
    tileHistory:'Edicións anteriores', participateTitle:'A túa banda.<br>O noso escenario.',
    participateText:'Consulta as bases e prepara a participación da túa banda nunha próxima edición.',
    contactJoin:'Solicita información ↗', programTitle:'Cada actuación, un momento único.',
    programText:'O programa da próxima edición publicarase aquí cando estea dispoñible.',
    resultsTitle:'O talento deixa pegada.',
    resultsText:'Consulta os resultados oficiais e os recoñecementos das edicións do Certame.',
    historyTitle:'Unha historia que segue soando.',
    historyText:'Un espazo para descubrir as bandas participantes e os momentos das edicións anteriores.',
    newsTitle:'Segue o ritmo do Certame.',
    newsText:'Aquí atoparás as novidades e os anuncios das próximas edicións.',
    footer:'Certame Internacional de Bandas de Música Vila de la Sénia'
  }
};
function setLanguage(lang) {
    const dictionary = translations[lang];
    document.documentElement.lang = lang;

    document.querySelectorAll('[data-i18n]').forEach((element) => {
        const key = element.dataset.i18n;
        const value = dictionary[key];
        if (value === undefined) return;

        // This heading intentionally contains a line break.
        if (key === 'participateTitle') {
            element.innerHTML = value;
        } else {
            element.textContent = value;
        }
    });

    document.querySelectorAll('[data-lang]').forEach((button) => {
        button.setAttribute('aria-pressed', String(button.dataset.lang === lang));
    });

    document.title = dictionary.footer;

    // Conservem l'idioma perquè les pàgines de formularis l'obrin en la mateixa llengua.
    try { localStorage.setItem('certamen-lang', lang); } catch (_) {}
}

const menuButton = document.querySelector('.menu-toggle');
const navigation = document.querySelector('.nav');

document.querySelectorAll('[data-lang]').forEach((button) => {
    button.addEventListener('click', () => setLanguage(button.dataset.lang));
});

menuButton.addEventListener('click', () => {
    const isOpen = navigation.classList.toggle('open');
    menuButton.setAttribute('aria-expanded', String(isOpen));
});

navigation.querySelectorAll('a').forEach((link) => {
    link.addEventListener('click', () => {
        navigation.classList.remove('open');
        menuButton.setAttribute('aria-expanded', 'false');
    });
});


// Recuperem l'idioma seleccionat en una visita anterior o en una altra pàgina.
try {
    const savedLanguage = localStorage.getItem('certamen-lang');
    if (savedLanguage && translations[savedLanguage]) setLanguage(savedLanguage);
} catch (_) {}
