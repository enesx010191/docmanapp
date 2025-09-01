import { useNavigate, useSearchParams } from "react-router";
import { useAuth } from "../context/AuthContext";
import { useEffect, useState } from "react";
import EditDocumentPage from "../dms/edit-document";

export function meta() {
  return [
    { title: "MoneyMate Belge Sistemi | Belge Düzenle" },
    { name: "description", content: "Belge düzenleme sayfası" },
  ];
}

export default function EditDocument() {
  const { authToken, logout, isAuthenticated, loading } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const documentId = searchParams.get('id');

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!loading && !isAuthenticated) {
      navigate('/login');
    }
  }, [isAuthenticated, loading, navigate]);

  // Redirect to home if no document ID is provided
  useEffect(() => {
    if (!loading && isAuthenticated && !documentId) {
      navigate('/');
    }
  }, [documentId, isAuthenticated, loading, navigate]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleEditSuccess = () => {
    navigate('/');
  };

  const handleCancel = () => {
    navigate('/');
  };

  // Show loading while checking authentication
  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Loading...</div>;
  }

  // Don't render if not authenticated or no document ID
  if (!isAuthenticated || !authToken || !documentId) {
    return null;
  }

  return (
    <EditDocumentPage
      authToken={authToken}
      documentId={documentId}
      onLogout={handleLogout}
      onEditSuccess={handleEditSuccess}
      onCancel={handleCancel}
    />
  );
}
