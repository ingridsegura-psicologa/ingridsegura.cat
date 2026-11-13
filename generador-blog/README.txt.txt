C:\web\ingrid\
│
├── sitemap.xml
│
├── data\
│   ├── posts-ca.json
│   ├── posts-es.json
│   ├── tags-ca.json
│   └── tags-es.json
│
├── blog\
│   └── img\
│       └── relacio-toxica.webp
│
└── generador-blog\
    ├── generar-posts.jar
    ├── generar-posts.bat
    ├── GenerarPosts.java
    ├── plantilla-post.html
    └── posts\
        ├── relacio-toxica.json
        └── relacio-toxica_es.json

Fitxers que necessita
================
posts/*.json
blog/img/*.webp


Fitxers que genera
================
blog/<id>.html
blog/<id>_es.html
data/posts-ca.json
data/posts-es.json
sitemap.xml


Novetats (novembre 2026)
================
- Tots els posts tenen el seu JSON a posts/. Per canviar un post, edita el
  JSON i executa generar-posts.bat --force (o esborra l'HTML i executa'l normal).
- Cada execució actualitza també les llistes d'articles de blog.html,
  blog_es.html i les pàgines d'àrea (entre els comentaris
  <!-- LLISTA-POSTS:INICI ... --> i <!-- LLISTA-POSTS:FI -->). No els esborris.
- Etiquetes disponibles (data/tags-ca.json): ansietat, autoestima, emocions,
  relacions, canvis, xarxes, terapia, autocura, parella, fobies.
- Ja no cal posar "date_text": la data es formata sola.
