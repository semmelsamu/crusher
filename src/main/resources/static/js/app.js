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

    // Initialize bar charts
    initializeBarCharts();
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
