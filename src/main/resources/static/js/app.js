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
    const removeButton = picker.querySelector("[data-remove-button]");
    const removeInput = picker.querySelector("[data-remove-input]");

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

    const updateRemoveButton = (
        enabled,
        removing = false,
        hasAnyImage = false,
    ) => {
        if (!removeButton) return;
        removeButton.disabled = !enabled;
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

    updateRemoveButton(hasImage, false, hasImage);

    fileInput?.addEventListener("change", (event) => {
        const [file] = event.target.files || [];
        if (!file) {
            if (hasImage && originalSrc) {
                showImage(originalSrc);
            } else {
                showPlaceholder();
            }
            setRemoveFlag(false);
            updateRemoveButton(hasImage, false, hasImage);
            return;
        }

        const objectUrl = URL.createObjectURL(file);
        showImage(objectUrl);
        setRemoveFlag(false);
        updateRemoveButton(true, false, true);
    });

    removeButton?.addEventListener("click", () => {
        if (!removeInput || removeInput.value === "true") {
            return;
        }
        if (fileInput) fileInput.value = "";
        showPlaceholder();
        setRemoveFlag(true);
        updateRemoveButton(false, true, false);
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
