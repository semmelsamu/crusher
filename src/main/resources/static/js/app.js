window.openDialog = (id) => {
    const dialog = document.getElementById(id);
    dialog.showModal();
};

window.closeDialog = (id) => {
    const dialog = document.getElementById(id);
    dialog.close();
};
