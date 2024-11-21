// src/utils/authUtils.ts
export const isUserLoggedIn = () => {
    return !!localStorage.getItem("authToken");
  };
  