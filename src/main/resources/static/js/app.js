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
});
