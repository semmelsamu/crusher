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

// Auto-dismiss after 8 seconds and add click handler
document.addEventListener("DOMContentLoaded", () => {
    const toast = document.getElementById("toast");
    if (toast) {
        setTimeout(() => dismissToast(), 8000);
        toast.addEventListener("click", dismissToast);
    }

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

    // Persist scroll before submitting toggle forms
    document.querySelectorAll("form.toggle-form").forEach((form) => {
        form.addEventListener("submit", () => {
            sessionStorage.setItem("projectScroll", String(window.scrollY));
        });
    });

    // Toggle progress sliders on go forms
    document.querySelectorAll("[data-progress-wrapper]").forEach((wrapper) => {
        const toggle = wrapper.querySelector("[data-progress-toggle]");
        const container = wrapper.querySelector("[data-progress-container]");
        const input = wrapper.querySelector("[data-progress-input]");
        const valueDisplay = wrapper.querySelector("[data-progress-value]");

        if (!toggle || !container || !input) {
            return;
        }

        const syncProgressState = () => {
            const enabled = toggle.checked;
            input.disabled = !enabled;
            container.dataset.disabled = String(!enabled);
            const currentValue = input.value || input.min || "0";
            if (valueDisplay) {
                valueDisplay.textContent = currentValue;
            }
        };

        input.addEventListener("input", () => {
            if (valueDisplay) {
                valueDisplay.textContent = input.value;
            }
        });

        toggle.addEventListener("change", syncProgressState);
        syncProgressState();
    });
});
