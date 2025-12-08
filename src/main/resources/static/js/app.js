window.openDialog = (id) => {
    const dialog = document.getElementById(id);
    dialog.showModal();
};

window.closeDialog = (id) => {
    const dialog = document.getElementById(id);
    dialog.close();
};

// Auto-dismiss toast notifications
document.addEventListener("DOMContentLoaded", () => {
    const toast = document.querySelector(".toast-success");
    if (toast) {
        setTimeout(() => {
            toast.style.animation = "toast-slide-in 0.3s ease-out reverse";
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
});
