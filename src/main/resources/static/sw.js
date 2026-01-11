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
