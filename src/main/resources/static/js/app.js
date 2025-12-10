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
function setupImagePicker(picker) {
    const fileInput = picker.querySelector("[data-image-input]");
    const previewImage = picker.querySelector("[data-preview-image]");
    const placeholder = picker.querySelector("[data-placeholder]");
    const removeCheckbox = picker.querySelector("[data-remove-checkbox]");

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

    fileInput?.addEventListener("change", (event) => {
        const [file] = event.target.files || [];
        if (!file) {
            if (hasImage && originalSrc) {
                showImage(originalSrc);
            } else {
                showPlaceholder();
            }
            return;
        }

        const objectUrl = URL.createObjectURL(file);
        showImage(objectUrl);
        if (removeCheckbox) {
            removeCheckbox.checked = false;
        }
    });

    removeCheckbox?.addEventListener("change", (event) => {
        if (event.target.checked) {
            if (fileInput) fileInput.value = "";
            showPlaceholder();
        } else if (hasImage && originalSrc) {
            showImage(originalSrc);
        } else {
            showPlaceholder();
        }
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
});
