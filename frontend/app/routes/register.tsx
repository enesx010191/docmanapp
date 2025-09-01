import type { Route } from "./+types/register";
import { useNavigate } from "react-router";
import RegisterScreen from "../dms/register";
import { useAuth } from "../context/AuthContext";
import { useEffect } from "react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "MoneyMate Belge Sistemi | Kayıt" },
    { name: "description", content: "MoneyMate Belge Sistemi" },
  ];
}

export default function Register() {
  const { isAuthenticated, loading } = useAuth();
  const navigate = useNavigate();

  // Redirect authenticated users to home
  useEffect(() => {
    if (!loading && isAuthenticated) {
      navigate('/');
    }
  }, [isAuthenticated, loading, navigate]);

  const handleRegisterSuccess = () => {
    console.log("Registration successful, redirecting to login");
    navigate('/login');
  };

  const handleGoToLogin = () => {
    navigate('/login');
  };

  // Show loading while checking authentication
  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading...</div>;
  }

  // Don't render register form if already authenticated
  if (isAuthenticated) {
    return null; // This should not happen due to the useEffect redirect
  }

  return (
    <RegisterScreen 
      onRegisterSuccess={handleRegisterSuccess} 
      onGoToLogin={handleGoToLogin}
    />
  );
}
