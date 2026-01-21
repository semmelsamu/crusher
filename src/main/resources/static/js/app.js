window.openDialog = (id) => {
    const dialog = document.getElementById(id);
    dialog.showModal();
};

window.closeDialog = (id) => {
    const dialog = document.getElementById(id);
    dialog.close();
};

window.dismissToast = () => {
    const toast = document.getElementById("toast");
    if (toast) {
        toast.style.animation = "slide-out-right 0.3s ease-in forwards";
        setTimeout(() => toast.remove(), 300);
    }
};

window.showToast = (message, type = "info") => {
    const existing = document.getElementById("toast");
    if (existing) existing.remove();

    const toast = document.createElement("div");
    toast.id = "toast";
    toast.className = "notification toast animate-slide-in-right";

    const icons = {
        success:
            '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-check-icon lucide-check"><path d="M20 6 9 17l-5-5" /></svg>',
        error: '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-x-icon lucide-x"><path d="M18 6 6 18" /><path d="m6 6 12 12" /></svg>',
        info: '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-info-icon lucide-info"><circle cx="12" cy="12" r="10" /><path d="M12 16v-4" /><path d="M12 8h.01" /></svg>',
    };

    const typeClass =
        type === "success"
            ? "bg-success"
            : type === "error"
              ? "bg-error"
              : "bg-main";
    toast.classList.add(typeClass);

    toast.innerHTML = `
        <span>${icons[type] || icons.info}</span>
        <p>${message}</p>
    `;

    document.body.appendChild(toast);
    setTimeout(() => dismissToast(), 8000);
    toast.addEventListener("click", dismissToast);
};

if ("serviceWorker" in navigator) {
    window.addEventListener("load", () => {
        navigator.serviceWorker.register("/sw.js").catch((error) => {
            console.error("Service worker registration failed:", error);
        });
    });
}

// Auto-dismiss after 8 seconds and add click handler
function setupImagePicker(picker) {
    const fileInput = picker.querySelector("[data-image-input]");
    const previewImage = picker.querySelector("[data-preview-image]");
    const placeholder = picker.querySelector("[data-placeholder]");
    const trigger = picker.querySelector("[data-image-trigger]");
    const removeButton =
        picker.closest("form")?.querySelector("[data-remove-button]") ??
        picker.querySelector("[data-remove-button]");
    const removeInput = picker.querySelector("[data-remove-input]");
    const maxSizeBytes = parseInt(picker.dataset.maxSizeBytes || "5242880", 10);

    const hasImage = picker.dataset.hasImage === "true";
    const originalSrc = picker.dataset.originalSrc || "";

    const showImage = (src) => {
        if (!previewImage) return;
        previewImage.src = src;
        previewImage.classList.remove("hidden");
        if (placeholder) placeholder.classList.add("hidden");
    };

    const showPlaceholder = () => {
        if (previewImage) previewImage.classList.add("hidden");
        if (placeholder) placeholder.classList.remove("hidden");
    };

    if (!hasImage) {
        showPlaceholder();
    }

    const setRemoveFlag = (value) => {
        if (removeInput) removeInput.value = value ? "true" : "false";
    };

    const updateRemoveButton = (hasAnyImage, removing = false) => {
        if (!removeButton) return;
        removeButton.classList.toggle("hidden", !hasAnyImage);
        removeButton.disabled = !hasAnyImage;
        const label = removeButton.querySelector("span");
        if (label) {
            if (!hasAnyImage) {
                label.textContent = "No image to remove";
            } else if (removing) {
                label.textContent = "Will remove on save";
            } else {
                label.textContent = "Remove image";
            }
        }
    };

    updateRemoveButton(hasImage, false);

    fileInput?.addEventListener("change", (event) => {
        const [file] = event.target.files || [];
        if (!file) {
            if (hasImage && originalSrc) {
                showImage(originalSrc);
            } else {
                showPlaceholder();
            }
            setRemoveFlag(false);
            updateRemoveButton(hasImage, false);
            return;
        }

        if (file.size > maxSizeBytes) {
            showPlaceholder();
            setRemoveFlag(false);
            if (fileInput) fileInput.value = "";
            updateRemoveButton(hasImage, false);
            showToast("Image too large. Maximum allowed size is 5MB.", "error");
            return;
        }

        const objectUrl = URL.createObjectURL(file);
        showImage(objectUrl);
        setRemoveFlag(false);
        updateRemoveButton(true, false);
    });

    trigger?.addEventListener("click", () => {
        fileInput?.click();
    });

    removeButton?.addEventListener("click", () => {
        if (!removeInput) return;
        if (fileInput) fileInput.value = "";
        showPlaceholder();
        setRemoveFlag(true);
        updateRemoveButton(false, true);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    const toast = document.getElementById("toast");
    if (toast) {
        setTimeout(() => dismissToast(), 8000);
        toast.addEventListener("click", dismissToast);
    }

    document
        .querySelectorAll("[data-image-picker]")
        .forEach((picker) => setupImagePicker(picker));

    // Auto-submit filter form when gym changes (for cascading filters)
    const gymSelect = document.getElementById("gym-select");
    if (gymSelect) {
        gymSelect.addEventListener("change", () => {
            const form = document.getElementById("filter-form");
            if (form) {
                form.submit();
            }
        });
    }

    const projectOnly = document.getElementById("project-only");
    if (projectOnly) {
        projectOnly.addEventListener("change", () => {
            const form = document.getElementById("filter-form");
            if (form) {
                form.submit();
            }
        });
    }

    // Restore scroll after toggling favorites
    const storedScroll = sessionStorage.getItem("projectScroll");
    if (storedScroll) {
        window.scrollTo(0, parseInt(storedScroll, 10));
        sessionStorage.removeItem("projectScroll");
    }

    // Restore scroll after posting comments
    const storedCommentScroll = sessionStorage.getItem("commentScroll");
    if (storedCommentScroll) {
        window.scrollTo(0, parseInt(storedCommentScroll, 10));
        sessionStorage.removeItem("commentScroll");
    }

    // Restore scroll after rating
    const storedRatingScroll = sessionStorage.getItem("ratingScroll");
    if (storedRatingScroll) {
        window.scrollTo(0, parseInt(storedRatingScroll, 10));
        sessionStorage.removeItem("ratingScroll");
    }

    // Restore scroll after pagination
    const storedPaginationScroll = sessionStorage.getItem("paginationScroll");
    if (storedPaginationScroll) {
        window.scrollTo(0, parseInt(storedPaginationScroll, 10));
        sessionStorage.removeItem("paginationScroll");
    }

    // Persist scroll before submitting toggle forms
    document.querySelectorAll("form.toggle-form").forEach((form) => {
        form.addEventListener("submit", () => {
            sessionStorage.setItem("projectScroll", String(window.scrollY));
        });
    });

    // Persist scroll before submitting comment forms
    document.querySelectorAll("form.comment-form").forEach((form) => {
        form.addEventListener("submit", () => {
            sessionStorage.setItem("commentScroll", String(window.scrollY));
        });
    });

    // Persist scroll before submitting rating forms
    document.querySelectorAll("form.rating-form").forEach((form) => {
        form.addEventListener("submit", () => {
            sessionStorage.setItem("ratingScroll", String(window.scrollY));
        });
    });

    // Handle pagination button clicks
    document.querySelectorAll(".pagination-btn").forEach((button) => {
        button.addEventListener("click", () => {
            if (button.disabled) return;

            const nav = button.closest(".pagination-nav");
            const baseUrl = nav?.getAttribute("data-base-url");
            const page = button.getAttribute("data-page");

            if (!baseUrl || !page) return;

            // Save scroll position
            sessionStorage.setItem("paginationScroll", String(window.scrollY));

            // Update URL parameters
            const params = new URLSearchParams(window.location.search);
            params.set("page", page);
            window.location.href = baseUrl + "?" + params.toString();
        });
    });

    // Initialize bar charts
    initializeBarCharts();

    // Initialize pie charts
    initializePieCharts();

    // Initialize avatars
    initAvatars();
});

/**
 * Initializes bar charts by calculating and setting heights based on data values
 */
function initializeBarCharts() {
    // Process each bar chart group independently
    document.querySelectorAll(".bar-chart-group").forEach((group) => {
        const bars = group.querySelectorAll(".column-chart-bar");
        const maxLabel = group.parentElement.querySelector(
            ".column-chart-max-label",
        );

        // Find the maximum value in this group
        let maxValue = 0;
        bars.forEach((bar) => {
            const value = parseInt(bar.getAttribute("data-value"), 10);
            if (value > maxValue) {
                maxValue = value;
            }
        });

        // Set the max label
        if (maxLabel && maxValue > 0) {
            maxLabel.textContent = maxValue;
        }

        // Set height for each bar as percentage of max
        if (maxValue > 0) {
            bars.forEach((bar) => {
                const value = parseInt(bar.getAttribute("data-value"), 10);

                if (value === 0) {
                    // Keep minimum height for zero values (set in CSS)
                    setTimeout(() => {
                        bar.style.height = "10px";
                    }, 50);
                } else {
                    const percentage = (value / maxValue) * 100;
                    // Use a small delay to trigger the CSS transition
                    setTimeout(() => {
                        bar.style.height = `${percentage}%`;
                    }, 50);
                }
            });
        }
    });
}

/**
 * Initializes pie charts by drawing SVG segments based on data values
 */
function initializePieCharts() {
    document.querySelectorAll(".pie-chart-group").forEach((group) => {
        const dataPoints = group.querySelectorAll(".pie-chart-data-point");
        const svg = group.querySelector(".pie-chart-svg");
        const wrapper = group.querySelector(".pie-chart-svg-wrapper");

        if (!svg || !wrapper || dataPoints.length === 0) {
            return;
        }

        // Extract data values, labels, and colors
        const data = Array.from(dataPoints).map((point) => ({
            value: parseInt(point.getAttribute("data-value"), 10),
            label: point.getAttribute("data-label"),
            color: point.getAttribute("data-color"),
        }));

        // Calculate total
        const total = data.reduce((sum, d) => sum + d.value, 0);

        if (total === 0) {
            return;
        }

        // SVG dimensions
        const size = 192; // 12rem = 192px
        const cx = size / 2; // center x
        const cy = size / 2; // center y
        const radius = size / 2 - 10; // radius with some padding

        let currentAngle = -90; // Start at top (-90 degrees)

        data.forEach((d, i) => {
            if (d.value === 0) return; // Skip zero values

            const percentage = d.value / total;
            const angle = percentage * 360;

            // Calculate start and end points on the circle
            const startAngle = (currentAngle * Math.PI) / 180;
            const endAngle = ((currentAngle + angle) * Math.PI) / 180;

            const x1 = cx + radius * Math.cos(startAngle);
            const y1 = cy + radius * Math.sin(startAngle);
            const x2 = cx + radius * Math.cos(endAngle);
            const y2 = cy + radius * Math.sin(endAngle);

            // Create SVG path for pie slice
            const largeArcFlag = angle > 180 ? 1 : 0;
            const pathData = [
                `M ${cx},${cy}`, // Move to center
                `L ${x1},${y1}`, // Line to start point
                `A ${radius},${radius} 0 ${largeArcFlag},1 ${x2},${y2}`, // Arc to end point
                "Z", // Close path back to center
            ].join(" ");

            const path = document.createElementNS(
                "http://www.w3.org/2000/svg",
                "path",
            );
            path.setAttribute("d", pathData);
            path.setAttribute("fill", `var(--${d.color})`);
            path.setAttribute("class", "pie-chart-slice");
            path.setAttribute("stroke", "var(--color-border)");
            path.setAttribute("stroke-width", "2");

            svg.appendChild(path);

            // Add label with count
            const labelAngle = ((currentAngle + angle / 2) * Math.PI) / 180;
            const labelRadius = radius * 0.65;
            const labelX = cx + labelRadius * Math.cos(labelAngle);
            const labelY = cy + labelRadius * Math.sin(labelAngle);

            const label = document.createElement("div");
            label.className = "pie-chart-label";
            label.textContent = d.value;
            label.style.left = `${labelX}px`;
            label.style.top = `${labelY}px`;
            label.style.transform = "translate(-50%, -50%)";
            label.style.color = "var(--background)";
            label.style.fontWeight = "bold";

            wrapper.appendChild(label);

            currentAngle += angle;
        });
    });
}

/**
 * Generates a deterministic numeric hash from a string
 */
function hashString(str) {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = (hash << 5) - hash + char;
        hash = hash & hash; // Convert to 32-bit integer
    }
    return Math.abs(hash);
}

function getStoredTheme() {
    try {
        return localStorage.getItem("theme");
    } catch (error) {
        return null;
    }
}

function setStoredTheme(theme) {
    try {
        localStorage.setItem("theme", theme);
    } catch (error) {
        // Ignore storage errors for privacy-restricted environments.
    }
}

function updateThemeToggle(theme) {
    const toggle = document.querySelector("[data-theme-toggle]");
    if (!toggle) return;
    const isDark = theme === "dark";
    const label = isDark ? "Switch to light mode" : "Switch to dark mode";
    toggle.setAttribute("aria-pressed", isDark ? "true" : "false");
    toggle.setAttribute("aria-label", label);
    toggle.setAttribute("title", label);
}

function applyTheme(theme) {
    document.documentElement.setAttribute("data-theme", theme);
    updateThemeToggle(theme);
}

function initThemeToggle() {
    const storedTheme = getStoredTheme();
    const initialTheme = storedTheme || "light";

    applyTheme(initialTheme);

    const toggle = document.querySelector("[data-theme-toggle]");
    if (!toggle) return;
    toggle.addEventListener("click", () => {
        const currentTheme =
            document.documentElement.getAttribute("data-theme") === "dark"
                ? "dark"
                : "light";
        const nextTheme = currentTheme === "dark" ? "light" : "dark";
        setStoredTheme(nextTheme);
        applyTheme(nextTheme);
    });
}

/**
 * Initializes all avatars by applying colors based on username hash
 */
function initAvatars() {
    // Color palette with good contrast for light and dark themes
    const colors = [
        "#E53E3E", // Red
        "#DD6B20", // Orange
        "#D69E2E", // Yellow
        "#38A169", // Green
        "#319795", // Teal
        "#3182CE", // Blue
        "#5A67D8", // Indigo
        "#805AD5", // Purple
        "#D53F8C", // Pink
        "#718096", // Gray
    ];

    // Select all avatars
    document.querySelectorAll("[data-username]").forEach((avatar) => {
        const username = avatar.getAttribute("data-username");
        if (username) {
            const hash = hashString(username);
            const colorIndex = hash % colors.length;
            avatar.style.backgroundColor = colors[colorIndex];
        }
    });
}

/**
 * Lazy loads crowd level data for a gym
 */
function loadCrowdLevel() {
    const container = document.getElementById("crowd-level-container");
    if (!container) return;

    const gymId = container.getAttribute("data-gym-id");
    if (!gymId) return;

    const percentageEl = document.getElementById("crowd-level-percentage");
    if (!percentageEl) return;

    // Show loading state
    percentageEl.textContent = "Loading...";

    // Fetch crowd level from API
    fetch(`/api/gyms/${gymId}/crowd-level`)
        .then((response) => {
            if (!response.ok) {
                throw new Error("Crowd level not available");
            }
            return response.json();
        })
        .then((data) => {
            // Round to nearest 10%
            const rounded = Math.round(data.percentage / 10) * 10;
            // Update UI with data
            percentageEl.textContent = rounded + "%";
        })
        .catch((error) => {
            // Show error state
            percentageEl.textContent = "N/A";
            console.error("Error loading crowd level:", error);
        });
}

/**
 * Initializes the gym map if coordinates are available.
 */
function initGymMap() {
    const mapEl = document.getElementById("gym-map");
    if (!mapEl) return;

    if (typeof L === "undefined") {
        console.warn("Leaflet is not loaded.");
        return;
    }

    const lat = parseFloat(mapEl.getAttribute("data-lat"));
    const lng = parseFloat(mapEl.getAttribute("data-lng"));

    if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
        return;
    }

    const name = mapEl.getAttribute("data-name") || "Gym";
    const address = mapEl.getAttribute("data-address");
    const popupText = address ? `${name}<br>${address}` : name;

    const map = L.map(mapEl, {
        zoomControl: true,
        scrollWheelZoom: false,
    }).setView([lat, lng], 15);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution:
            '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    L.marker([lat, lng]).addTo(map).bindPopup(popupText);

    map.on("click", () => map.scrollWheelZoom.enable());
    map.on("mouseout", () => map.scrollWheelZoom.disable());
}

/**
 * Initializes the gym form map for selecting location with a draggable marker
 * Uses MutationObserver to detect when Alpine.js shows/hides the map container
 */
function initGymFormMap() {
    const mapContainer = document.querySelector("[data-map-container]");
    const latInput = document.querySelector("[data-lat-input]");
    const lngInput = document.querySelector("[data-lng-input]");

    if (!mapContainer || !latInput || !lngInput) {
        return;
    }

    if (typeof L === "undefined") {
        console.warn("Leaflet is not loaded.");
        return;
    }

    let map = null;
    let marker = null;

    // Default center: Germany
    const DEFAULT_LAT = 51.1657;
    const DEFAULT_LNG = 10.4515;
    const DEFAULT_ZOOM = 6;

    /**
     * Creates and initializes the map
     */
    function createMap() {
        if (map) return; // Already initialized

        // Get existing coordinates or use defaults
        const existingLat = parseFloat(latInput.value);
        const existingLng = parseFloat(lngInput.value);

        const hasValidCoords =
            Number.isFinite(existingLat) && Number.isFinite(existingLng);
        const initialLat = hasValidCoords ? existingLat : DEFAULT_LAT;
        const initialLng = hasValidCoords ? existingLng : DEFAULT_LNG;
        const initialZoom = hasValidCoords ? 15 : DEFAULT_ZOOM;

        // Initialize map
        map = L.map(mapContainer, {
            zoomControl: true,
            scrollWheelZoom: true,
        }).setView([initialLat, initialLng], initialZoom);

        // Add tile layer
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19,
            attribution:
                '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        }).addTo(map);

        // Add draggable marker if coordinates exist
        if (hasValidCoords) {
            marker = L.marker([initialLat, initialLng], {
                draggable: true,
            }).addTo(map);

            // Update inputs when marker is dragged
            marker.on("dragend", function () {
                const pos = marker.getLatLng();
                latInput.value = pos.lat.toFixed(6);
                lngInput.value = pos.lng.toFixed(6);
            });
        }

        // Add marker on map click
        map.on("click", function (e) {
            const { lat, lng } = e.latlng;

            if (marker) {
                // Move existing marker
                marker.setLatLng([lat, lng]);
            } else {
                // Create new marker
                marker = L.marker([lat, lng], {
                    draggable: true,
                }).addTo(map);

                // Update inputs when marker is dragged
                marker.on("dragend", function () {
                    const pos = marker.getLatLng();
                    latInput.value = pos.lat.toFixed(6);
                    lngInput.value = pos.lng.toFixed(6);
                });
            }

            // Update hidden inputs
            latInput.value = lat.toFixed(6);
            lngInput.value = lng.toFixed(6);
        });

        // Fix display issues after container becomes visible
        setTimeout(() => {
            if (map) {
                map.invalidateSize();
            }
        }, 100);
    }

    /**
     * Destroys the map instance
     */
    function destroyMap() {
        if (map) {
            map.remove();
            map = null;
            marker = null;
        }
    }

    /**
     * Checks if element is visible
     */
    function isVisible(element) {
        return element.offsetWidth > 0 && element.offsetHeight > 0;
    }

    // Use MutationObserver to watch for style changes from Alpine.js
    const observer = new MutationObserver(() => {
        if (isVisible(mapContainer)) {
            createMap();
        } else {
            destroyMap();
        }
    });

    // Observe style and display attribute changes
    observer.observe(mapContainer, {
        attributes: true,
        attributeFilter: ["style", "class"],
    });

    // Initialize on load if already visible
    if (isVisible(mapContainer)) {
        createMap();
    }
}

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => {
    initThemeToggle();
    initAvatars();
    loadCrowdLevel();
    initGymMap();
    initGymFormMap();
});
