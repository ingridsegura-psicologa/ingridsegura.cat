# Agrupació Musical Senienca — nova web

Web estàtica per a GitHub Pages amb HTML, CSS, JavaScript i dades JSON.

## Com provar-la

Des d’aquesta carpeta: `python -m http.server 8000` i obre `http://localhost:8000`. Cal un servidor perquè `fetch()` pugui carregar els JSON.

## Fotografies

Desa a `img/` les fotografies amb aquests noms exactes:
- `portada.jpg` — fotografia principal de la banda (la web la mostra en blanc i negre)
- `santa_cecilia.jpg` — Santa Cecília
- `festes.jpg` — Festes Majors
- `cruilla.jpg` — Terres de Cruïlla

Els concerts es mostren en color. Pots canviar els noms i rutes des de `data/activitats.json`. Les imatges absents es substitueixen per un espai neutre.

## Continguts

`data/activitats.json` defineix concerts. `destacat: true` permet destacar una activitat; el filtre selecciona la primera destacada de cada categoria, o la més recent. La resta apareix a l'arxiu.

`data/cursos.json` i `data/iniciatives.json` alimenten la secció «Altres». Les dades són buides fins que s'hi incorporin continguts. No hi ha secció de subvencions.

Les fitxes es mostren automàticament a `detall.html`.

## Publicació

Puja els fitxers al repositori GitHub i activa Pages. Revisa el contingut i els enllaços abans de publicar.
