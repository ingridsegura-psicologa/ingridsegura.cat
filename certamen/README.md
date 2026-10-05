# Web del Certamen — versió editable

Aquesta carpeta conté la portada i la pàgina «El Certamen». No cal instal·lar cap eina ni generar un ZIP per fer-hi canvis.

## Per on començar

- `index.html`: estructura i contingut inicial de la portada.
- `el-certamen.html`: estructura, història i bloc de logotips. Busca el comentari `PATROCINADORS` per trobar-los.
- `styles.css`: estils generals de la web.
- `el-certamen.css`: estils específics de la pàgina «El Certamen», inclosa la mida dels logotips (`.sponsor img`).
- `script.js` i `el-certamen.js`: traduccions i interaccions de cada pàgina. Les claus `data-t` dels HTML es corresponen amb les traduccions dels JS.
- `assets/`: fotografies i logotips. Els fitxers `logo-diputacio.png` i `logo-generalitat-cultura.png` són els que has facilitat.

## Canviar un logotip

1. Copia la imatge nova dins `assets/`.
2. Obre `el-certamen.html`, busca `PATROCINADORS` i canvia el valor `src` de la imatge corresponent.
3. Desa el fitxer i actualitza el navegador. Si mantens el mateix nom del fitxer, només cal substituir la imatge.

## Provar al mòbil

Des de la carpeta que conté `index.html`, executa `python3 -m http.server 8000 --bind 0.0.0.0`. Al mòbil, connectat a la mateixa xarxa, obre `http://IP_DEL_PORTATIL:8000` (amb **http**, no https).

## Notes

Ordre de llengües: CA · ES · GL · PT · EN. Les dades d'edicions, inscripcions, programa i resultats de la portada encara són provisionals. Els logotips de les empreses col·laboradores s'afegiran quan estiguin disponibles.


## Formularis (NOVETAT)

- `contacte.html`: consulta general, disponible tot l’any.
- `inscripcions.html`: pàgina d’inscripció, inicialment amb **inscripcions tancades**.
- `formularis.css`: estils dels dos formularis.
- `formularis-traduccions.js`: textos en CA, ES, GL, PT i EN.
- `formularis.js`: configuració del termini i de l’enviament.
- `politica-privacitat.html`: **marcador provisional**; cal substituir-lo per la política real abans de publicar els formularis.

**IMPORTANT: els formularis encara NO envien correus.** Una web estàtica necessita un servei receptor (p. ex. Formspree) o un servidor propi. Quan l’organització tingui dos endpoints reals, posa’ls a `CONFIG.contacteEndpoint` i `CONFIG.inscripcionsEndpoint` dins `formularis.js`. Per obrir inscripcions, canvia `CONFIG.inscripcionsObertes` a `true` només dins del termini oficial. Abans d’activar res, revisa la política de privacitat, el text de les bases i prova els dos enviaments reals.

Si més endavant calen documents adjunts, caldrà comprovar que el servei receptor admet fitxers i afegir els camps corresponents. No s’han afegit encara perquè no s’ha decidit.

## Botó destacat a inscripcions tancades

A `inscripcions.html`, el bloc `registration-contact` mostra un botó gran que enllaça a `contacte.html`. Els textos per a les cinc llengües són a `formularis-traduccions.js` i l’estil és a `formularis.css`. Quan activeu les inscripcions, aquest bloc s’oculta amb tot l’avís de termini tancat.
