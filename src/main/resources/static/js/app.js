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

    // Persist scroll before submitting toggle forms
    document.querySelectorAll("form.toggle-form").forEach((form) => {
        form.addEventListener("submit", () => {
            sessionStorage.setItem("projectScroll", String(window.scrollY));
        });
    });
});
