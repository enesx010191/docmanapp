import type { Route } from "./+types/login";
import { useNavigate } from "react-router";
import LoginScreen from "../dms/login";
import { useAuth } from "../context/AuthContext";
import { useEffect } from "react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "MoneyMate Belge Sistemi | Giriş" },
    { name: "description", content: "MoneyMate Belge Sistemi" },
  ];
}

export default function Login() {
  const { login, isAuthenticated, loading } = useAuth();
  const navigate = useNavigate();

  // Redirect authenticated users to home
  useEffect(() => {
    if (!loading && isAuthenticated) {
      navigate('/');
    }
  }, [isAuthenticated, loading, navigate]);

  const handleLoginSuccess = (token: string) => {
    login(token); // Store token in context
    navigate('/'); // Redirect to home after successful login
    console.log("Login successful, token:", token);
  };

  const handleGoToRegister = () => {
    navigate('/register');
  };

  // Show loading while checking authentication
  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading...</div>;
  }

  // Don't render login form if already authenticated
  if (isAuthenticated) {
    return null; // This should not happen due to the useEffect redirect
  }

  return (
    <LoginScreen 
      onLoginSuccess={handleLoginSuccess} 
      onGoToRegister={handleGoToRegister}
    />
  );
}
