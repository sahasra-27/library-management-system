import React, { createContext, useState, useEffect } from 'react';
import axios from 'axios';

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [role, setRole] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    const storedRole = localStorage.getItem('role');
    const storedToken = localStorage.getItem('token');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
    if (storedRole) {
      setRole(storedRole);
    }
    if (storedToken) {
      setToken(storedToken);
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    setLoading(true);
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        username,
        password,
      });
      const userPayload = response.data; // { id, username, email, role, token }
      setUser(userPayload);
      setRole(userPayload.role);
      setToken(userPayload.token);
      localStorage.setItem('user', JSON.stringify(userPayload));
      localStorage.setItem('role', userPayload.role);
      localStorage.setItem('token', userPayload.token);
      return userPayload;
    } catch (error) {
      logout();
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const register = async (username, email, password, phone, role, occupation) => {
    await axios.post('http://localhost:8080/api/auth/register', {
      username,
      email,
      password,
      phone,
      role,
      occupation,
    });
  };

  const forgotPassword = async (email) => {
    await axios.post('http://localhost:8080/api/auth/forgotpassword', { email });
  };

  const resetPassword = async (tokenParam, newPassword) => {
    await axios.post('http://localhost:8080/api/auth/resetpassword', {
      token: tokenParam,
      newPassword,
    });
  };

  const logout = async () => {
    try {
      await axios.post('http://localhost:8080/api/auth/logout');
    } catch (e) {
      console.error("Logout request failed", e);
    }
    setUser(null);
    setRole(null);
    setToken(null);
    localStorage.removeItem('user');
    localStorage.removeItem('role');
    localStorage.removeItem('token');
  };

  const updateProfile = (updatedUser) => {
    setUser(updatedUser);
    setRole(updatedUser.role);
    localStorage.setItem('user', JSON.stringify(updatedUser));
    localStorage.setItem('role', updatedUser.role);
  };

  const hasRole = (checkRole) => {
    return role === checkRole;
  };

  const isAdmin = () => {
    return role === 'ADMIN';
  };

  const isUser = () => {
    return role === 'USER';
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        role,
        token,
        loading,
        login,
        register,
        forgotPassword,
        resetPassword,
        logout,
        updateProfile,
        hasRole,
        isAdmin,
        isUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
