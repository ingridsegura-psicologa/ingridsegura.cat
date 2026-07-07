document.addEventListener("DOMContentLoaded", () => {
  const list = document.querySelector("[data-blog-list]");
  const filters = document.querySelector("[data-blog-filters]");
  const searchInput = document.querySelector("[data-blog-search]");
  const emptyMessage = document.querySelector("[data-blog-empty]");

  if (!list || !filters) return;

  const lang = document.documentElement.lang === "es" ? "es" : "ca";
  const postsPath = `./data/posts-${lang}.json`;
  const tagsPath = `./data/tags-${lang}.json`;

  const labels = {
    ca: {
      all: "Tots",
      readMore: "Llegir més →",
      searchPlaceholder: "Buscar articles...",
      empty: "No hi ha articles amb aquest filtre.",
      loadError: "No s'han pogut carregar els articles. Comprova que existeixi la carpeta /data i que estiguis obrint la web des d'un servidor, no amb doble clic al fitxer."
    },
    es: {
      all: "Todos",
      readMore: "Leer más →",
      searchPlaceholder: "Buscar artículos...",
      empty: "No hay artículos con este filtro.",
      loadError: "No se han podido cargar los artículos. Comprueba que exista la carpeta /data y que estés abriendo la web desde un servidor, no haciendo doble clic en el archivo."
    }
  };

  let posts = [];
  let tags = {};
  let activeTag = "all";

  function escapeHTML(value) {
    return String(value || "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  function renderFilters() {
    const usedTags = [...new Set(posts.flatMap(post => post.tags || []))]
      .filter(tag => tags[tag])
      .sort((a, b) => tags[a].localeCompare(tags[b], lang));

    filters.innerHTML = `
      <button class="blog-filter active" type="button" data-tag="all">${labels[lang].all}</button>
      ${usedTags.map(tag => `
        <button class="blog-filter" type="button" data-tag="${escapeHTML(tag)}">${escapeHTML(tags[tag])}</button>
      `).join("")}
    `;

    filters.querySelectorAll("[data-tag]").forEach(button => {
      button.addEventListener("click", () => {
        activeTag = button.dataset.tag;
        filters.querySelectorAll("[data-tag]").forEach(btn => btn.classList.remove("active"));
        button.classList.add("active");
        renderPosts();
      });
    });
  }

  function renderPosts() {
    const query = searchInput ? searchInput.value.trim().toLowerCase() : "";

    const filteredPosts = posts.filter(post => {
      const matchesTag = activeTag === "all" || (post.tags || []).includes(activeTag);
      const text = `${post.title} ${post.description} ${(post.tags || []).map(tag => tags[tag] || tag).join(" ")}`.toLowerCase();
      const matchesSearch = !query || text.includes(query);
      return matchesTag && matchesSearch;
    });

    list.innerHTML = filteredPosts.map(post => renderCard(post)).join("");

    if (emptyMessage) {
      emptyMessage.textContent = labels[lang].empty;
      emptyMessage.hidden = filteredPosts.length > 0;
    }
  }

  function renderCard(post) {
    const tagHTML = (post.tags || [])
      .filter(tag => tags[tag])
      .map(tag => `<span class="blog-tag">${escapeHTML(tags[tag])}</span>`)
      .join("");

    const imageHTML = post.image
      ? `<img src="${escapeHTML(post.image)}" alt="${escapeHTML(post.image_alt || post.title)}" class="blog-card-image">`
      : "";

    const cardClass = post.featured ? "blog-card blog-card-featured" : "blog-card";

    return `
      <article class="${cardClass}">
        <div class="blog-card-tags">${tagHTML}</div>
        <p class="blog-date">${escapeHTML(post.date_text)}</p>
        <h2>${escapeHTML(post.title)}</h2>
        <p>${escapeHTML(post.description)}</p>
        ${imageHTML}
        <a href="${escapeHTML(post.url)}">${labels[lang].readMore}</a>
      </article>
    `;
  }

  Promise.all([
    fetch(postsPath).then(response => {
      if (!response.ok) throw new Error(postsPath);
      return response.json();
    }),
    fetch(tagsPath).then(response => {
      if (!response.ok) throw new Error(tagsPath);
      return response.json();
    })
  ])
    .then(([postsData, tagsData]) => {
      tags = tagsData;
      posts = postsData.sort((a, b) => new Date(b.date) - new Date(a.date));

      if (searchInput) {
        searchInput.placeholder = labels[lang].searchPlaceholder;
        searchInput.addEventListener("input", renderPosts);
      }

      renderFilters();
      renderPosts();
    })
    .catch(error => {
      console.error("Error carregant el blog:", error);
      list.innerHTML = `<p class="blog-load-error">${labels[lang].loadError}</p>`;
      if (searchInput) searchInput.placeholder = labels[lang].searchPlaceholder;
    });
});
