import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class GenerarPosts {

    private static Path GENERATOR_DIR;
    private static Path ROOT_DIR;
    private static Path POSTS_DIR;
    private static Path TEMPLATE_FILE;
    private static Path BLOG_DIR;
    private static Path IMG_DIR;
    private static Path DATA_DIR;
    private static Path SITEMAP_FILE;

    private static boolean force = false;
    private static boolean htmlChanged = false;

    public static void main(String[] args) {
        force = Arrays.asList(args).contains("--force");

        try {
            initPaths();
            validateStructure();

            System.out.println("Generant articles...\n");
            System.out.println("Generador: " + GENERATOR_DIR);
            System.out.println("Posts:     " + POSTS_DIR);
            System.out.println("Blog:      " + BLOG_DIR);
            System.out.println("Data:      " + DATA_DIR);
            System.out.println("Sitemap:   " + SITEMAP_FILE);
            System.out.println();

            String template = readUtf8(TEMPLATE_FILE);

            List<Path> jsonFiles = new ArrayList<Path>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(POSTS_DIR, "*.json")) {
                for (Path p : stream) {
                    jsonFiles.add(p);
                }
            }

            Collections.sort(jsonFiles, new Comparator<Path>() {
                public int compare(Path a, Path b) {
                    return a.getFileName().toString().compareToIgnoreCase(b.getFileName().toString());
                }
            });

            for (Path jsonFile : jsonFiles) {
                try {
                    generatePost(jsonFile, template);
                } catch (Exception e) {
                    System.err.println("ERROR processant " + jsonFile.getFileName() + ": " + e.getMessage());
                }
            }

            if (htmlChanged) {
                updateSitemap("https://ingridsegura.cat/blog.html");
                updateSitemap("https://ingridsegura.cat/blog_es.html");
            }

            System.out.println("\nProcés finalitzat.");

        } catch (Exception e) {
            System.err.println("\nERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initPaths() throws Exception {
        URI location = GenerarPosts.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI();

        Path locationPath = Paths.get(location).toAbsolutePath().normalize();

        if (Files.isRegularFile(locationPath)
                || locationPath.toString().toLowerCase(Locale.ROOT).endsWith(".jar")) {
            GENERATOR_DIR = locationPath.getParent();
        } else {
            GENERATOR_DIR = locationPath;
        }

        ROOT_DIR = GENERATOR_DIR.getParent();
        if (ROOT_DIR == null) {
            throw new IOException("No s'ha pogut determinar el directori pare de generador-blog.");
        }

        POSTS_DIR = GENERATOR_DIR.resolve("posts");
        TEMPLATE_FILE = GENERATOR_DIR.resolve("plantilla-post.html");
        BLOG_DIR = ROOT_DIR.resolve("blog");
        IMG_DIR = BLOG_DIR.resolve("img");
        DATA_DIR = ROOT_DIR.resolve("data");
        SITEMAP_FILE = ROOT_DIR.resolve("sitemap.xml");
    }

    private static void validateStructure() throws IOException {
        if (!Files.exists(TEMPLATE_FILE)) {
            throw new IOException("No s'ha trobat la plantilla: " + TEMPLATE_FILE);
        }
        if (!Files.isDirectory(POSTS_DIR)) {
            throw new IOException("No s'ha trobat la carpeta: " + POSTS_DIR);
        }
        if (!Files.isDirectory(BLOG_DIR)) {
            throw new IOException("No s'ha trobat la carpeta: " + BLOG_DIR);
        }
        if (!Files.isDirectory(DATA_DIR)) {
            throw new IOException("No s'ha trobat la carpeta: " + DATA_DIR);
        }
        if (!Files.exists(SITEMAP_FILE)) {
            System.out.println("AVÍS: no s'ha trobat sitemap.xml. Els HTML i /data sí que es generaran.");
        }
    }

    @SuppressWarnings("unchecked")
    private static void generatePost(Path jsonFile, String template) throws Exception {
        Object parsed = new SimpleJsonParser(readUtf8(jsonFile)).parse();
        if (!(parsed instanceof Map)) {
            throw new IllegalArgumentException("El JSON ha de contenir un objecte.");
        }

        Map<String, Object> data = (Map<String, Object>) parsed;

        String language = string(data.get("lang")).toLowerCase(Locale.ROOT);
        if (!"ca".equals(language) && !"es".equals(language)) {
            throw new IllegalArgumentException("Falta \"lang\" o no és \"ca\"/\"es\".");
        }

        String id = string(data.get("id"));
        if (id.isEmpty()) {
            id = string(data.get("slug")); // compatibilitat amb els JSON anteriors
        }
        if (id.isEmpty()) {
            String name = jsonFile.getFileName().toString();
            id = name.substring(0, name.length() - 5);
            if ("es".equals(language) && id.endsWith("_es")) {
                id = id.substring(0, id.length() - 3);
            }
        }

        validateId(id);

        String suffix = "es".equals(language) ? "_es" : "";
        Path outputFile = BLOG_DIR.resolve(id + suffix + ".html");

        if (Files.exists(outputFile) && !force) {
            System.out.println("Ja existeix: " + outputFile.getFileName()
                    + "  (usa --force si has modificat el JSON)");
            return;
        }

        String title = required(data, "title");
        String date = required(data, "date");

        String cardDescription = string(data.get("description"));
        String seoDescription = valueOr(data.get("seoDescription"), cardDescription);
        String seoTitle = valueOr(data.get("seoTitle"), title + " | Ingrid Segura Psicòloga");
        String ogTitle = valueOr(data.get("ogTitle"), title);
        String ogDescription = valueOr(data.get("ogDescription"), seoDescription);

        String caUrl = "https://ingridsegura.cat/blog/" + id + ".html";
        String esUrl = "https://ingridsegura.cat/blog/" + id + "_es.html";
        String currentUrl = "es".equals(language) ? esUrl : caUrl;

        boolean hasImage = Files.exists(IMG_DIR.resolve(id + ".webp"));

        String html = template;
        html = replace(html, "{{lang}}", language);
        html = replace(html, "{{seoTitle}}", escapeHtml(seoTitle));
        html = replace(html, "{{seoDescription}}", escapeHtmlAttribute(seoDescription));
        html = replace(html, "{{ogTitle}}", escapeHtmlAttribute(ogTitle));
        html = replace(html, "{{ogDescription}}", escapeHtmlAttribute(ogDescription));
        html = replace(html, "{{ogImageTag}}", renderOgImage(id, hasImage));
        html = replace(html, "{{canonicalUrl}}", currentUrl);
        html = replace(html, "{{urlCa}}", caUrl);
        html = replace(html, "{{urlEs}}", esUrl);
        html = replace(html, "{{jsonLd}}",
                renderJsonLd(language, id, title, seoDescription, date, currentUrl, hasImage));

        html = replace(html, "{{fileCa}}", id + ".html");
        html = replace(html, "{{fileEs}}", id + "_es.html");
        html = replace(html, "{{menuLabel}}", "es".equals(language) ? "Abrir menú" : "Obrir menú");
        html = replace(html, "{{navLabel}}", "es".equals(language) ? "Navegación principal" : "Navegació principal");
        html = replace(html, "{{langLabel}}", "es".equals(language) ? "Selector de idioma" : "Selector d'idioma");
        html = replace(html, "{{navLinks}}", renderNavLinks(language));
        html = replace(html, "{{blogHref}}", "es".equals(language) ? "../blog_es.html" : "../blog.html");
        html = replace(html, "{{backText}}", "es".equals(language) ? "← Volver al blog" : "← Tornar al blog");
        html = replace(html, "{{title}}", escapeHtml(title));
        html = replace(html, "{{publishedText}}", publishedText(language, date, data));
        html = replace(html, "{{contentHtml}}", renderContent(list(data.get("content")), id, hasImage));
        html = replace(html, "{{referencesHtml}}", renderReferences(list(data.get("references")), language));
        html = replace(html, "{{relatedHtml}}", renderRelated(map(data.get("related"))));
        html = replace(html, "{{ctaHtml}}", renderCTA(data.get("cta"), language));

        html = replace(html, "{{legalHref}}", "es".equals(language) ? "../avis-legal_es.html" : "../avis-legal.html");
        html = replace(html, "{{legalText}}", "es".equals(language) ? "Aviso legal y privacidad" : "Avís legal i privacitat");
        html = replace(html, "{{faqHref}}", "es".equals(language) ? "../faqs_es.html" : "../faqs.html");
        html = replace(html, "{{faqText}}", "es".equals(language) ? "Preguntas frecuentes" : "Preguntes freqüents");
        html = replace(html, "{{cookieText}}",
                "es".equals(language)
                        ? "Este sitio utiliza cookies técnicas necesarias para su funcionamiento."
                        : "Aquest lloc utilitza cookies tècniques necessàries per al seu funcionament.");
        html = replace(html, "{{acceptText}}", "es".equals(language) ? "Aceptar" : "Acceptar");
        html = replace(html, "{{rejectText}}", "es".equals(language) ? "Rechazar" : "Rebutjar");

        writeUtf8(outputFile, html);
        htmlChanged = true;
        System.out.println("Generat HTML: " + outputFile.getFileName());

        updatePostsIndex(data, language, id, title, date, hasImage);
        updateSitemap(currentUrl);
    }

    private static void updatePostsIndex(Map<String, Object> data,
                                         String language,
                                         String id,
                                         String title,
                                         String date,
                                         boolean hasImage) throws Exception {
        Path postsFile = DATA_DIR.resolve("posts-" + language + ".json");

        List<Object> posts = new ArrayList<Object>();

        if (Files.exists(postsFile)) {
            Object parsed = new SimpleJsonParser(readUtf8(postsFile)).parse();
            if (!(parsed instanceof List)) {
                throw new IllegalArgumentException(postsFile.getFileName() + " no conté un array JSON.");
            }
            posts.addAll(list(parsed));
        }

        LinkedHashMap<String, Object> entry = new LinkedHashMap<String, Object>();
        entry.put("id", id);
        entry.put("title", title);
        entry.put("date", date);
        entry.put("date_text", formatDate(date, language));

        String description = string(data.get("description"));
        if (description.isEmpty()) {
            description = string(data.get("seoDescription"));
        }
        entry.put("description", description);

        if (hasImage) {
            entry.put("image", "blog/img/" + id + ".webp");

            String imageAlt = string(data.get("image_alt"));
            if (imageAlt.isEmpty()) {
                imageAlt = string(data.get("imageAlt"));
            }
            if (imageAlt.isEmpty()) {
                imageAlt = findImageAlt(list(data.get("content")));
            }
            if (!imageAlt.isEmpty()) {
                entry.put("image_alt", imageAlt);
            }
        }

        entry.put("url", "blog/" + id + ("es".equals(language) ? "_es" : "") + ".html");

        Object featured = data.get("featured");
        entry.put("featured", featured instanceof Boolean ? featured : Boolean.FALSE);

        List<Object> tags = list(data.get("tags"));
        entry.put("tags", new ArrayList<Object>(tags));

        boolean replaced = false;
        for (int i = 0; i < posts.size(); i++) {
            Map<String, Object> current = map(posts.get(i));
            if (id.equals(string(current.get("id")))) {
                posts.set(i, entry);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            posts.add(entry);
        }

        Collections.sort(posts, new Comparator<Object>() {
            public int compare(Object a, Object b) {
                String da = string(map(a).get("date"));
                String db = string(map(b).get("date"));
                return db.compareTo(da);
            }
        });

        writeUtf8(postsFile, JsonWriter.write(posts) + "\n");

        System.out.println((replaced ? "Actualitzat" : "Afegit")
                + " a data/" + postsFile.getFileName());
    }

    private static String findImageAlt(List<Object> content) {
        for (Object obj : content) {
            Map<String, Object> block = map(obj);
            if ("image".equals(string(block.get("type")))) {
                return string(block.get("alt"));
            }
        }
        return "";
    }

    private static String formatDate(String isoDate, String language) {
        try {
            LocalDate d = LocalDate.parse(isoDate);

            String[] caMonths = {
                    "", "gener", "febrer", "març", "abril", "maig", "juny",
                    "juliol", "agost", "setembre", "octubre", "novembre", "desembre"
            };
            String[] esMonths = {
                    "", "enero", "febrero", "marzo", "abril", "mayo", "junio",
                    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
            };

            if ("es".equals(language)) {
                return d.getDayOfMonth() + " de " + esMonths[d.getMonthValue()]
                        + " de " + d.getYear();
            }

            String month = caMonths[d.getMonthValue()];
            String connector = startsWithVowel(month) ? " d'" : " de ";
            return d.getDayOfMonth() + connector + month + " " + d.getYear();

        } catch (Exception e) {
            return isoDate;
        }
    }

    private static boolean startsWithVowel(String s) {
        if (s == null || s.isEmpty()) return false;
        char c = Character.toLowerCase(s.charAt(0));
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

    private static String publishedText(String language, String date, Map<String, Object> data) {
        String explicit = string(data.get("date_text"));
        if (explicit.isEmpty()) explicit = string(data.get("dateText"));
        String formatted = explicit.isEmpty() ? formatDate(date, language) : explicit;
        return ("es".equals(language) ? "Publicado el " : "Publicat el ") + escapeHtml(formatted);
    }

    private static void updateSitemap(String url) throws IOException {
        if (!Files.exists(SITEMAP_FILE)) return;

        String sitemap = readUtf8(SITEMAP_FILE);
        String today = LocalDate.now().toString();
        String locTag = "<loc>" + url + "</loc>";

        int locPos = sitemap.indexOf(locTag);
        if (locPos >= 0) {
            int urlStart = sitemap.lastIndexOf("<url", locPos);
            int urlEnd = sitemap.indexOf("</url>", locPos);

            if (urlStart >= 0 && urlEnd >= 0) {
                urlEnd += "</url>".length();
                String block = sitemap.substring(urlStart, urlEnd);
                String newBlock;

                if (block.contains("<lastmod>")) {
                    newBlock = block.replaceFirst(
                            "<lastmod>[^<]*</lastmod>",
                            "<lastmod>" + today + "</lastmod>");
                } else {
                    int close = block.lastIndexOf("</url>");
                    newBlock = block.substring(0, close)
                            + "<lastmod>" + today + "</lastmod>"
                            + block.substring(close);
                }

                sitemap = sitemap.substring(0, urlStart)
                        + newBlock
                        + sitemap.substring(urlEnd);

                writeUtf8(SITEMAP_FILE, sitemap);
                System.out.println("Sitemap actualitzat: " + url);
                return;
            }
        }

        int end = sitemap.lastIndexOf("</urlset>");
        if (end < 0) {
            System.out.println("AVÍS: sitemap.xml no conté </urlset>. No s'ha modificat.");
            return;
        }

        String entry = "<url><loc>" + url + "</loc><lastmod>" + today + "</lastmod></url>\n";
        sitemap = sitemap.substring(0, end) + entry + sitemap.substring(end);
        writeUtf8(SITEMAP_FILE, sitemap);
        System.out.println("Sitemap afegit: " + url);
    }

    private static String renderContent(List<Object> content, String id, boolean hasImage) {
        StringBuilder out = new StringBuilder();

        for (Object obj : content) {
            Map<String, Object> block = map(obj);
            String type = string(block.get("type"));

            if ("p".equals(type)) {
                String raw = string(block.get("html"));
                out.append("<p>")
                        .append(raw.isEmpty() ? escapeHtml(string(block.get("text"))) : raw)
                        .append("</p>");

            } else if ("h2".equals(type)) {
                out.append("<h2>").append(escapeHtml(string(block.get("text")))).append("</h2>");

            } else if ("h3".equals(type)) {
                out.append("<h3>").append(escapeHtml(string(block.get("text")))).append("</h3>");

            } else if ("ul".equals(type)) {
                out.append("<ul>\n");
                for (Object item : list(block.get("items"))) {
                    out.append("<li>").append(String.valueOf(item)).append("</li>\n");
                }
                out.append("</ul>");

            } else if ("html".equals(type)) {
                out.append(string(block.get("html")));

            } else if ("audio".equals(type)) {
                out.append("<p><a href=\"")
                        .append(escapeHtmlAttribute(string(block.get("url"))))
                        .append("\" target=\"_blank\"><span class=\"audio-icon\">🎧</span> ")
                        .append(escapeHtml(string(block.get("text"))))
                        .append("</a></p>");

            } else if ("image".equals(type)) {
                if (hasImage) {
                    out.append("<div class=\"post-image post-image-right\">")
                            .append("<img src=\"img/").append(id).append(".webp\" alt=\"")
                            .append(escapeHtmlAttribute(string(block.get("alt"))))
                            .append("\"></div>");
                }

            } else if (!type.isEmpty()) {
                System.out.println("AVÍS: tipus de bloc desconegut: " + type);
            }

            out.append("\n\n");
        }

        return out.toString();
    }

    private static String renderReferences(List<Object> refs, String language) {
        if (refs.isEmpty()) return "";

        StringBuilder out = new StringBuilder();
        out.append("<div class=\"post-references\">\n<h3>")
                .append("es".equals(language) ? "Referencias" : "Referències")
                .append("</h3>\n<ul>\n");

        for (Object ref : refs) {
            out.append("<li>").append(String.valueOf(ref)).append("</li>\n");
        }

        out.append("</ul>\n</div>");
        return out.toString();
    }

    private static String renderRelated(Map<String, Object> related) {
        if (related.isEmpty()) return "";

        String href = valueOr(related.get("href"), string(related.get("url")));

        return "<div class=\"related-post\">\n"
                + "<p>" + string(related.get("text")) + "</p>\n"
                + "<a href=\"" + escapeHtmlAttribute(href) + "\">"
                + string(related.get("title")) + "</a>\n"
                + "</div>";
    }

    private static String renderCTA(Object ctaObject, String language) {
        if (Boolean.FALSE.equals(ctaObject)) return "";

        Map<String, Object> cta = map(ctaObject);

        String title = valueOr(cta.get("title"),
                "es".equals(language)
                        ? "Si sientes que necesitas apoyo, no tienes que gestionarlo solo/a."
                        : "Si sents que necessites suport, no ho has de gestionar sol/a.");

        String text = valueOr(cta.get("text"),
                "es".equals(language)
                        ? "Podemos mirarlo con calma y entender qué necesitas. La primera conversación es gratuita y sin compromiso."
                        : "Podem mirar-ho amb calma i entendre què necessites. La primera conversa és gratuïta i sense compromís.");

        String button = valueOr(cta.get("button"),
                "es".equals(language)
                        ? "Reserva una llamada de 15 minutos"
                        : "Reserva una trucada de 15 minuts");

        String href = "es".equals(language) ? "../contacte_es.html" : "../contacte.html";

        return "<section class=\"cta-block\">\n<div class=\"cta-inner\">\n"
                + "<h3>" + title + "</h3>\n"
                + "<p>" + text + "</p>\n"
                + "<a href=\"" + href + "\" class=\"button-line\">" + button + "</a>\n"
                + "</div>\n</section>";
    }

    private static String renderOgImage(String id, boolean hasImage) {
        if (!hasImage) return "";
        return "<meta property=\"og:image\" content=\"https://ingridsegura.cat/blog/img/"
                + id + ".webp\">";
    }

    private static String renderJsonLd(String language,
                                       String id,
                                       String title,
                                       String description,
                                       String date,
                                       String currentUrl,
                                       boolean hasImage) {
        StringBuilder s = new StringBuilder();
        s.append("{\n");
        s.append("  \"@context\": \"https://schema.org\",\n");
        s.append("  \"@type\": \"BlogPosting\",\n");
        s.append("  \"inLanguage\": \"").append(jsonEscape(language)).append("\",\n");
        s.append("  \"headline\": \"").append(jsonEscape(title)).append("\",\n");
        s.append("  \"description\": \"").append(jsonEscape(description)).append("\",\n");
        s.append("  \"author\": {\"@type\":\"Person\",\"name\":\"Ingrid Segura Bertomeu\"},\n");
        s.append("  \"publisher\": {\"@type\":\"Person\",\"name\":\"Ingrid Segura Bertomeu\"},\n");
        s.append("  \"datePublished\": \"").append(jsonEscape(date)).append("\",\n");
        s.append("  \"dateModified\": \"").append(jsonEscape(date)).append("\",\n");

        if (hasImage) {
            s.append("  \"image\": \"https://ingridsegura.cat/blog/img/")
                    .append(jsonEscape(id)).append(".webp\",\n");
        }

        s.append("  \"mainEntityOfPage\": {\"@type\":\"WebPage\",\"@id\":\"")
                .append(jsonEscape(currentUrl)).append("\"}\n");
        s.append("}");

        return s.toString();
    }

    private static String renderNavLinks(String language) {
        if ("es".equals(language)) {
            return "<a href=\"../index_es.html\">Inicio</a>\n"
                    + "<a href=\"../sobre-mi_es.html\">Sobre mí</a>\n"
                    + "<a href=\"../arees-especialitzacio_es.html\">Áreas de especialización</a>\n"
                    + "<a href=\"../serveis_es.html\">Servicios</a>\n"
                    + "<a href=\"../blog_es.html\" class=\"active\">Blog</a>\n"
                    + "<a href=\"../contacte_es.html\">Contacto</a>";
        }

        return "<a href=\"../index.html\">Inici</a>\n"
                + "<a href=\"../sobre-mi.html\">Sobre mi</a>\n"
                + "<a href=\"../arees-especialitzacio.html\">Àrees d'especialització</a>\n"
                + "<a href=\"../serveis.html\">Serveis</a>\n"
                + "<a href=\"../blog.html\" class=\"active\">Blog</a>\n"
                + "<a href=\"../contacte.html\">Contacte</a>";
    }

    private static void validateId(String id) {
        if (!id.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
            throw new IllegalArgumentException(
                    "\"id\" no vàlid: " + id
                            + ". Utilitza minúscules, números i guions.");
        }
    }

    private static String required(Map<String, Object> data, String key) {
        String value = string(data.get(key));
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Falta el camp obligatori \"" + key + "\".");
        }
        return value;
    }

    private static String readUtf8(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static void writeUtf8(Path path, String text) throws IOException {
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
    }

    private static String replace(String source, String key, String value) {
        return source.replace(key, value == null ? "" : value);
    }

    private static String valueOr(Object value, String fallback) {
        String s = string(value);
        return s.isEmpty() ? fallback : s;
    }

    private static String string(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private static List<Object> list(Object value) {
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return Collections.emptyList();
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String escapeHtmlAttribute(String text) {
        return escapeHtml(text).replace("'", "&#39;");
    }

    private static String jsonEscape(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private static final class JsonWriter {
        static String write(Object value) {
            StringBuilder sb = new StringBuilder();
            append(value, sb, 0);
            return sb.toString();
        }

        private static void append(Object value, StringBuilder sb, int level) {
            if (value == null) {
                sb.append("null");
            } else if (value instanceof String) {
                quote((String) value, sb);
            } else if (value instanceof Boolean || value instanceof Number) {
                sb.append(String.valueOf(value));
            } else if (value instanceof Map) {
                appendObject(map(value), sb, level);
            } else if (value instanceof List) {
                appendArray(list(value), sb, level);
            } else {
                quote(String.valueOf(value), sb);
            }
        }

        private static void appendObject(Map<String, Object> obj, StringBuilder sb, int level) {
            sb.append("{");
            if (!obj.isEmpty()) sb.append("\n");

            int i = 0;
            for (Map.Entry<String, Object> e : obj.entrySet()) {
                indent(sb, level + 1);
                quote(e.getKey(), sb);
                sb.append(": ");
                append(e.getValue(), sb, level + 1);
                if (++i < obj.size()) sb.append(",");
                sb.append("\n");
            }

            if (!obj.isEmpty()) indent(sb, level);
            sb.append("}");
        }

        private static void appendArray(List<Object> list, StringBuilder sb, int level) {
            sb.append("[");
            if (!list.isEmpty()) sb.append("\n");

            for (int i = 0; i < list.size(); i++) {
                indent(sb, level + 1);
                append(list.get(i), sb, level + 1);
                if (i + 1 < list.size()) sb.append(",");
                sb.append("\n");
            }

            if (!list.isEmpty()) indent(sb, level);
            sb.append("]");
        }

        private static void quote(String s, StringBuilder sb) {
            sb.append("\"");
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '"': sb.append("\\\""); break;
                    case '\\': sb.append("\\\\"); break;
                    case '\b': sb.append("\\b"); break;
                    case '\f': sb.append("\\f"); break;
                    case '\n': sb.append("\\n"); break;
                    case '\r': sb.append("\\r"); break;
                    case '\t': sb.append("\\t"); break;
                    default:
                        if (c < 0x20) {
                            String hex = Integer.toHexString(c);
                            sb.append("\\u");
                            for (int j = hex.length(); j < 4; j++) sb.append("0");
                            sb.append(hex);
                        } else {
                            sb.append(c);
                        }
                }
            }
            sb.append("\"");
        }

        private static void indent(StringBuilder sb, int level) {
            for (int i = 0; i < level; i++) sb.append("  ");
        }
    }

    private static final class SimpleJsonParser {
        private final String text;
        private int pos = 0;

        SimpleJsonParser(String text) {
            this.text = text == null ? "" : text;
        }

        Object parse() {
            skipWhitespace();
            Object value = readValue();
            skipWhitespace();
            if (pos != text.length()) {
                error("Hi ha contingut extra després del JSON");
            }
            return value;
        }

        private Object readValue() {
            skipWhitespace();
            if (pos >= text.length()) error("Final de JSON inesperat");

            char c = text.charAt(pos);

            if (c == '{') return readObject();
            if (c == '[') return readArray();
            if (c == '"') return readString();
            if (c == 't') return readLiteral("true", Boolean.TRUE);
            if (c == 'f') return readLiteral("false", Boolean.FALSE);
            if (c == 'n') return readLiteral("null", null);
            if (c == '-' || (c >= '0' && c <= '9')) return readNumber();

            error("Caràcter inesperat: " + c);
            return null;
        }

        private Map<String, Object> readObject() {
            LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
            expect('{');
            skipWhitespace();

            if (peek('}')) {
                pos++;
                return obj;
            }

            while (true) {
                skipWhitespace();
                if (!peek('"')) error("S'esperava una clau entre cometes");
                String key = readString();

                skipWhitespace();
                expect(':');

                Object value = readValue();
                obj.put(key, value);

                skipWhitespace();
                if (peek('}')) {
                    pos++;
                    return obj;
                }
                expect(',');
            }
        }

        private List<Object> readArray() {
            ArrayList<Object> array = new ArrayList<Object>();
            expect('[');
            skipWhitespace();

            if (peek(']')) {
                pos++;
                return array;
            }

            while (true) {
                array.add(readValue());
                skipWhitespace();

                if (peek(']')) {
                    pos++;
                    return array;
                }
                expect(',');
            }
        }

        private String readString() {
            expect('"');
            StringBuilder sb = new StringBuilder();

            while (pos < text.length()) {
                char c = text.charAt(pos++);

                if (c == '"') return sb.toString();

                if (c == '\\') {
                    if (pos >= text.length()) error("Escape incomplet");

                    char e = text.charAt(pos++);
                    switch (e) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            if (pos + 4 > text.length()) error("Unicode incomplet");
                            String hex = text.substring(pos, pos + 4);
                            try {
                                sb.append((char) Integer.parseInt(hex, 16));
                            } catch (NumberFormatException ex) {
                                error("Escape unicode no vàlid");
                            }
                            pos += 4;
                            break;
                        default:
                            error("Escape no vàlid: \\" + e);
                    }
                } else {
                    sb.append(c);
                }
            }

            error("Cadena sense tancar");
            return null;
        }

        private Object readNumber() {
            int start = pos;

            if (peek('-')) pos++;
            while (pos < text.length() && Character.isDigit(text.charAt(pos))) pos++;

            boolean decimal = false;

            if (peek('.')) {
                decimal = true;
                pos++;
                while (pos < text.length() && Character.isDigit(text.charAt(pos))) pos++;
            }

            if (peek('e') || peek('E')) {
                decimal = true;
                pos++;
                if (peek('+') || peek('-')) pos++;
                while (pos < text.length() && Character.isDigit(text.charAt(pos))) pos++;
            }

            String number = text.substring(start, pos);

            try {
                if (decimal) return Double.valueOf(number);
                return Long.valueOf(number);
            } catch (NumberFormatException e) {
                error("Número no vàlid: " + number);
                return null;
            }
        }

        private Object readLiteral(String literal, Object value) {
            if (!text.startsWith(literal, pos)) {
                error("Valor no vàlid");
            }
            pos += literal.length();
            return value;
        }

        private void skipWhitespace() {
            while (pos < text.length()) {
                char c = text.charAt(pos);
                if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                    pos++;
                } else {
                    break;
                }
            }
        }

        private void expect(char c) {
            skipWhitespace();
            if (pos >= text.length() || text.charAt(pos) != c) {
                error("S'esperava '" + c + "'");
            }
            pos++;
        }

        private boolean peek(char c) {
            return pos < text.length() && text.charAt(pos) == c;
        }

        private void error(String message) {
            throw new IllegalArgumentException(message + " (posició " + pos + ")");
        }
    }
}
