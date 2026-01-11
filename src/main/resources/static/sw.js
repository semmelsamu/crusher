const CACHE_NAME = "crusher-static-v1";
const PRECACHE_URLS = ["/", "/css/_build.css", "/js/app.js"];

self.addEventListener("install", (event) => {
    event.waitUntil(
        caches
            .open(CACHE_NAME)
            .then((cache) => cache.addAll(PRECACHE_URLS))
            .then(() => self.skipWaiting()),
    );
});

self.addEventListener("activate", (event) => {
    event.waitUntil(
        caches
            .keys()
            .then((keys) =>
                Promise.all(
                    keys
                        .filter((key) => key !== CACHE_NAME)
                        .map((key) => {
                            return caches.delete(key);
                        }),
                ),
            )
            .then(() => self.clients.claim()),
    );
});

self.addEventListener("fetch", (event) => {
    if (event.request.method !== "GET") {
        return;
    }

    const url = new URL(event.request.url);
    if (url.origin !== self.location.origin) {
        return;
    }

    if (event.request.mode === "navigate") {
        event.respondWith(
            caches
                .match("/", { ignoreSearch: true })
                .then((cached) => cached || fetch(event.request)),
        );
        return;
    }

    if (PRECACHE_URLS.includes(url.pathname)) {
        event.respondWith(
            caches
                .match(event.request)
                .then((cached) => cached || fetch(event.request)),
        );
    }
});
