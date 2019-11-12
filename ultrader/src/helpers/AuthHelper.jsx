export function logout() {
  // remove user from local storage to log user out
  localStorage.removeItem("user");
  window.location.reload(true);
}

export function checkRolePermission(user, requiredRoleId) {
  return (
    !requiredRoleId ||
    (requiredRoleId && user && user.roleId && user.roleId <= requiredRoleId)
  );
}
