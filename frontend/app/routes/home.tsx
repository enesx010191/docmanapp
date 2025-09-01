import type { Route } from "./+types/home";
import { useNavigate } from "react-router";
import { useAuth } from "../context/AuthContext";
import BankingDocumentApp from "../dms/welcome";
import { useEffect } from "react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "MoneyMate Belge Sistemi | Ana Sayfa" },
    { name: "description", content: "MoneyMate Belge Sistemi" },
  ];
}

export default function Home() {
  const { authToken, logout, isAuthenticated, loading } = useAuth();
  const navigate = useNavigate();

  // Wait for loading to complete before redirecting
  useEffect(() => {
    if (!loading && !isAuthenticated) {
      navigate('/login');
    }
  }, [isAuthenticated, loading, navigate]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Show loading while checking authentication
  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading...</div>;
  }

  // Don't render the dashboard if not authenticated
  if (!isAuthenticated || !authToken) {
    return null; // This should not happen due to the useEffect redirect
  }

  return (
    <BankingDocumentApp
      authToken={authToken}
      onLogout={handleLogout}
    />
  );
}
