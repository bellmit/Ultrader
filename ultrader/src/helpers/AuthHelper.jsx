export function logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('user');
    window.location.reload(true);
}