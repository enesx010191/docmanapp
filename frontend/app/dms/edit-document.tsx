import React, { useState, useEffect, type ChangeEvent } from "react";
import {
  Save,
  X,
  Upload,
  LogOut,
  User,
  Building2,
  FileText,
  ArrowLeft,
} from "lucide-react";

// --- Backend API Endpoints using environment variable ---
const API_URL = import.meta.env.VITE_API_BASE_URL;
const DOCUMENTS_API_URL = `${API_URL}/documents/`; // Endpoint to fetch/update documents

interface Document {
  id: string;
  institutionName: string;
  institutionType: "bank" | "electronic_money";
  documentType: string;
  title: string;
  date: string;
  description?: string;
  fileUrl: string;
  fileSize: string;
}

interface UserInfo {
  firstName: string;
  lastName: string;
  email: string;
}

interface EditDocumentPageProps {
  authToken: string;
  documentId: string;
  onLogout: () => void;
  onEditSuccess: () => void;
  onCancel: () => void;
}

type InstitutionType = "bank" | "electronic_money";

const documentTypes = [
  "KVKK",
  "Hizmet Sözleşmesi",
  "Tarife",
  "Kullanım Şartları",
  "Bilgilendirme Formu",
  "Diğer",
];

const EditDocumentPage: React.FC<EditDocumentPageProps> = ({
  authToken,
  documentId,
  onLogout,
  onEditSuccess,
  onCancel,
}) => {
  const [userInfo] = useState<UserInfo>({
    firstName: localStorage.getItem('firstName') || '',
    lastName: localStorage.getItem('lastName') || '',
    email: localStorage.getItem('email') || ''
  });

  const [document, setDocument] = useState<Document | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [institutionNames, setInstitutionNames] = useState<string[]>([]);
  
  // Form state
  const [formData, setFormData] = useState({
    institutionName: "",
    institutionType: "bank" as InstitutionType,
    documentType: "KVKK",
    title: "",
    description: "",
  });
  
  const [newFile, setNewFile] = useState<File | null>(null);
  const [customDocumentType, setCustomDocumentType] = useState<string>("");
  const [showCustomInstitutionInput, setShowCustomInstitutionInput] = useState<boolean>(false);
  const [customInstitutionName, setCustomInstitutionName] = useState<string>("");

  // Fetch document details and institution names
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      
      try {
        const headers: HeadersInit = {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authToken}`,
        };

        // Fetch document details
        const docResponse = await fetch(`${DOCUMENTS_API_URL}${documentId}`, {
          method: 'GET',
          headers: headers,
        });

        if (!docResponse.ok) {
          if (docResponse.status === 404) {
            throw new Error('Belge bulunamadı.');
          }
          if (docResponse.status === 401 || docResponse.status === 403) {
            throw new Error('Bu belgeyi düzenleme yetkiniz yok.');
          }
          throw new Error(`HTTP error! status: ${docResponse.status}`);
        }

        const docData = await docResponse.json();
        const doc: Document = docData;
        setDocument(doc);
        
        // Set form data
        setFormData({
          institutionName: doc.institutionName,
          institutionType: doc.institutionType,
          documentType: doc.documentType,
          title: doc.title,
          description: doc.description || "",
        });

        // If document type is not in predefined list, it's custom
        if (!documentTypes.includes(doc.documentType)) {
          setCustomDocumentType(doc.documentType);
          setFormData(prev => ({ ...prev, documentType: "Diğer" }));
        }

        // Fetch all documents to get institution names
        const allDocsResponse = await fetch(DOCUMENTS_API_URL, {
          method: 'GET',
          headers: headers,
        });

        if (allDocsResponse.ok) {
          const allDocsData = await allDocsResponse.json();
          const uniqueInstitutionNames = Array.from(
            new Set(allDocsData.documents.map((d: Document) => d.institutionName))
          ) as string[];
          
          // Ensure current document's institution name is included
          if (!uniqueInstitutionNames.includes(doc.institutionName)) {
            uniqueInstitutionNames.push(doc.institutionName);
          }
          
          setInstitutionNames(uniqueInstitutionNames.sort());
        } else {
          // If we can't fetch all documents, at least include the current document's institution
          setInstitutionNames([doc.institutionName]);
        }

      } catch (err: any) {
        console.error("Failed to fetch document:", err);
        setError(err.message || 'Belge yüklenirken bir hata oluştu.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [authToken, documentId]);

  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    setSaving(true);
    setError(null);

    const finalDocumentType = formData.documentType === "Diğer" ? customDocumentType : formData.documentType;
    const finalInstitutionName = showCustomInstitutionInput ? customInstitutionName : formData.institutionName;

    if (!finalInstitutionName || !formData.title || !finalDocumentType) {
      setError("Lütfen tüm zorunlu alanları doldurun.");
      setSaving(false);
      return;
    }

    try {
      const updateData = new FormData();
      updateData.append('institutionName', finalInstitutionName);
      updateData.append('institutionType', formData.institutionType);
      updateData.append('documentType', finalDocumentType);
      updateData.append('title', formData.title);
      updateData.append('documentDescription', formData.description);
      
      if (newFile) {
        updateData.append('file', newFile);
      }

      const response = await fetch(`${DOCUMENTS_API_URL}${documentId}`, {
        method: "PUT",
        headers: {
          'Authorization': `Bearer ${authToken}`,
        },
        body: updateData,
      });

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error('Bu belgeyi düzenleme yetkiniz yok.');
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Mark that documents should be refreshed when returning to main page
      sessionStorage.setItem('shouldRefreshDocuments', 'true');
      onEditSuccess();
    } catch (error: any) {
      console.error("Failed to update document:", error);
      setError(`Belge güncellenirken bir hata oluştu: ${error.message}`);
    } finally {
      setSaving(false);
    }
  };

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>): void => {
    const files = e.target.files;
    if (files && files[0]) {
      setNewFile(files[0]);
    }
  };

  const handleInstitutionNameChange = (e: ChangeEvent<HTMLSelectElement | HTMLInputElement>): void => {
    const value = e.target.value;
    if (value === "DiğerKurum") {
      setShowCustomInstitutionInput(true);
      setFormData({ ...formData, institutionName: "" });
    } else {
      setShowCustomInstitutionInput(false);
      setCustomInstitutionName("");
      setFormData({ ...formData, institutionName: value });
    }
  };

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return "0 Bytes";
    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900"></div>
      </div>
    );
  }

  if (error && !document) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md mx-4">
          <div className="text-center">
            <FileText className="h-12 w-12 text-red-600 mx-auto mb-4" />
            <h2 className="text-xl font-bold text-gray-900 mb-2">Hata</h2>
            <p className="text-gray-600 mb-4">{error}</p>
            <button
              onClick={onCancel}
              className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
            >
              Ana Sayfaya Dön
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 font-sans">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <Building2 className="h-8 w-8 text-blue-600 mr-3" />
              <h1 className="text-xl font-semibold text-gray-900">
                Belge Düzenle
              </h1>
            </div>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <User className="h-5 w-5 text-gray-400" />
                <span className="text-sm text-gray-700">{userInfo.firstName}</span>
              </div>
              <div className="text-sm text-gray-500">
                E-posta: {userInfo.email}
              </div>
              <button 
                onClick={onLogout}
                className="p-2 text-gray-400 hover:text-gray-600 transition-colors"
                title="Çıkış Yap"
              >
                <LogOut className="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6">
          <button
            onClick={onCancel}
            className="flex items-center text-blue-600 hover:text-blue-800 transition-colors"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Ana Sayfaya Dön
          </button>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          {/* Current Document Info Section */}
          {document && (
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
              <h3 className="text-lg font-medium text-blue-900 mb-3 flex items-center">
                <FileText className="h-5 w-5 mr-2" />
                Düzenlenen Belge
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="font-medium text-gray-700">Kurum:</span>
                  <span className="ml-2 text-gray-900">{document.institutionName}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Kurum Türü:</span>
                  <span className="ml-2 text-gray-900">
                    {document.institutionType === 'bank' ? 'Banka' : 'Elektronik Para'}
                  </span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Belge Türü:</span>
                  <span className="ml-2 text-gray-900">{document.documentType}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Dosya Boyutu:</span>
                  <span className="ml-2 text-gray-900">{document.fileSize}</span>
                </div>
                <div className="md:col-span-2">
                  <span className="font-medium text-gray-700">Mevcut Başlık:</span>
                  <span className="ml-2 text-gray-900">{document.title}</span>
                </div>
                {document.description && (
                  <div className="md:col-span-2">
                    <span className="font-medium text-gray-700">Mevcut Açıklama:</span>
                    <span className="ml-2 text-gray-900">{document.description}</span>
                  </div>
                )}
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="border-t pt-6">
              <h4 className="text-md font-medium text-gray-900 mb-4">
                Aşağıdaki alanları düzenleyebilirsiniz:
              </h4>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Institution Name */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Kurum Adı *
                </label>
                {!showCustomInstitutionInput ? (
                  <select
                    value={formData.institutionName}
                    onChange={handleInstitutionNameChange}
                    className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  >
                    <option value="">Kurum Seçin</option>
                    {institutionNames.map((name) => (
                      <option key={name} value={name}>
                        {name}
                      </option>
                    ))}
                    <option value="DiğerKurum">Diğer (Yeni Kurum Ekle)</option>
                  </select>
                ) : (
                  <input
                    type="text"
                    placeholder="Yeni Kurum Adı Girin"
                    value={customInstitutionName}
                    onChange={(e) => setCustomInstitutionName(e.target.value)}
                    className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                )}
              </div>

              {/* Institution Type */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Kurum Türü *
                </label>
                <select
                  value={formData.institutionType}
                  onChange={(e) => setFormData({ ...formData, institutionType: e.target.value as InstitutionType })}
                  className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="bank">Banka</option>
                  <option value="electronic_money">Elektronik Para</option>
                </select>
              </div>

              {/* Document Type */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Belge Türü *
                </label>
                <select
                  value={formData.documentType}
                  onChange={(e) => {
                    setFormData({ ...formData, documentType: e.target.value });
                    if (e.target.value !== "Diğer") {
                      setCustomDocumentType("");
                    }
                  }}
                  className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  {documentTypes.map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </select>
                {formData.documentType === "Diğer" && (
                  <input
                    type="text"
                    placeholder="Belge türünü girin..."
                    value={customDocumentType}
                    onChange={(e) => setCustomDocumentType(e.target.value)}
                    className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent mt-2"
                    required
                  />
                )}
              </div>

              {/* Title */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Belge Başlığı *
                </label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  required
                />
              </div>
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Açıklama
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                rows={4}
                placeholder="Belge hakkında açıklama..."
              />
            </div>

            {/* New File Upload */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Yeni Dosya (İsteğe bağlı - mevcut dosyayı değiştirir)
              </label>
              <input
                type="file"
                accept=".pdf,.doc,.docx,.xls,.xlsx"
                onChange={handleFileChange}
                className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              {newFile && (
                <div className="mt-2 text-sm text-gray-600">
                  <div className="flex items-center gap-2">
                    <Upload className="h-4 w-4" />
                    <span>{newFile.name}</span>
                    <span className="text-gray-400">
                      ({formatFileSize(newFile.size)})
                    </span>
                  </div>
                </div>
              )}
            </div>

            {error && (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-md">
                {error}
              </div>
            )}

            {/* Action Buttons */}
            <div className="flex gap-4 pt-6">
              <button
                type="submit"
                disabled={saving}
                className="flex-1 bg-blue-600 text-white py-3 px-6 rounded-md font-medium hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                {saving ? (
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                ) : (
                  <>
                    <Save className="h-5 w-5" />
                    Değişiklikleri Kaydet
                  </>
                )}
              </button>
              <button
                type="button"
                onClick={onCancel}
                disabled={saving}
                className="flex-1 bg-gray-300 text-gray-700 py-3 px-6 rounded-md font-medium hover:bg-gray-400 transition-colors disabled:bg-gray-200 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                <X className="h-5 w-5" />
                İptal
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
};

export default EditDocumentPage;
