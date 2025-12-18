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

    // Initialize pie charts
    initializePieCharts();
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
            path.setAttribute("stroke", "#000000");
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
