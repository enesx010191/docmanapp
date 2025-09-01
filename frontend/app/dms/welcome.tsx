import React, { useState, useEffect, type ChangeEvent } from "react";
import {
  Search,
  Download,
  Eye,
  Plus,
  Filter,
  LogOut,
  User,
  Building2,
  FileText,
  Calendar,
  Tag,
  Edit,
  Trash2,
} from "lucide-react";

// --- Backend API Endpoints using environment variable ---
const API_URL = import.meta.env.VITE_API_BASE_URL;
const DOCUMENTS_API_URL = `${API_URL}/documents/`; // Endpoint to fetch/add documents
interface Document {
  id: string; // Assuming backend provides a string ID
  institutionName: string;
  institutionType: "bank" | "electronic_money";
  documentType: string;
  title: string;
  updateAt: string; // Stored as ISO string for consistency
  documentDescription?: string; // Make description optional
  fileUrl: string;
  fileSize: string;
}

interface UserInfo {
  firstName: string;
  lastName: string;
  email: string;
}

interface NewDocument {
  institutionName: string;
  institutionType: "bank" | "electronic_money";
  documentType: string;
  title: string;
  description: string;
  file: File | null;
}

type InstitutionType = "all" | "bank" | "electronic_money";

const topBanks = [
  "Ziraat Bankası",
  "İş Bankası",
  "Garanti BBVA",
  "Akbank",
  "Yapı Kredi",
];
// Added 'Diğer' (Other) option for custom input
const documentTypes = [
  "Tümü",
  "KVKK",
  "Hizmet Sözleşmesi",
  "Tarife",
  "Kullanım Şartları",
  "Bilgilendirme Formu",
  "Diğer",
];

interface BankingDocumentAppProps {
  authToken: string | null; // The JWT token received after login
  onLogout: () => void; // Callback function to handle logout
}

const BankingDocumentApp: React.FC<BankingDocumentAppProps> = ({ authToken, onLogout }) => {
  // User info is now static as backend authentication is not handled here
  const [userInfo] = useState<UserInfo>({ firstName: localStorage.getItem('firstName') || '', lastName: localStorage.getItem('lastName') || '', email: localStorage.getItem('email') || '' });
  const [documents, setDocuments] = useState<Document[]>([]);
  const [filteredDocuments, setFilteredDocuments] = useState<Document[]>([]);
  const [selectedInstitutionType, setSelectedInstitutionType] = useState<InstitutionType>('all');
  const [selectedDocumentType, setSelectedDocumentType] = useState<string>('Tümü');
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [showAddModal, setShowAddModal] = useState<boolean>(false);
  const [isTopBanksFilter, setIsTopBanksFilter] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [institutionNames, setInstitutionNames] = useState<string[]>([]); // New state for unique institution names
  const [showPdfViewer, setShowPdfViewer] = useState<boolean>(false);
  const [currentPdfUrl, setCurrentPdfUrl] = useState<string>('');
  const [currentPdfTitle, setCurrentPdfTitle] = useState<string>('');
  const [showEditModal, setShowEditModal] = useState<boolean>(false);
  const [editingDocument, setEditingDocument] = useState<Document | null>(null);
  const [showDeleteModal, setShowDeleteModal] = useState<boolean>(false);
  const [deletingDocument, setDeletingDocument] = useState<Document | null>(null);
  const [deleting, setDeleting] = useState<boolean>(false);

  // Function to fetch documents from the backend
  const fetchDocuments = async () => {
    setLoading(true);
    setError(null);
    try {
      const headers: HeadersInit = {
        'Content-Type': 'application/json',
      };

      // Add Authorization header if a token is present
      if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
      } else {
        // If no token, it means the user is not authenticated, so we should not proceed
        // or handle it as an unauthorized state.
        setError('Authentication token is missing. Please log in.');
        setLoading(false);
        onLogout(); // Force logout if token is missing
        return;
      }

      const response = await fetch(DOCUMENTS_API_URL, {
        method: 'GET',
        headers: headers,
      });

      console.log('Fetch response status:', response.status);
      if (!response.ok) {
        // Handle specific error codes like 401 Unauthorized or 403 Forbidden
        if (response.status === 401 || response.status === 403) {
          setError('Yetkiniz yok veya oturumunuz sona erdi. Lütfen tekrar giriş yapın.');
          // Only logout if it's a clear authentication failure, not network issues
          setTimeout(() => {
            onLogout(); // Trigger logout with a slight delay to show the error message
          }, 1000);
          return; // Don't throw error after logout
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      let body = await response.json();
      const data: Document[] = body.data;
      setDocuments(data);

      // Extract unique institution names from fetched documents
      const uniqueInstitutionNames = Array.from(new Set(data.map(doc => doc.institutionName)));
      setInstitutionNames(uniqueInstitutionNames.sort()); // Sort alphabetically
    } catch (err: any) {
      console.error("Failed to fetch documents:", err);
      setError(`Belgeler yüklenirken bir hata oluştu: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Fetch documents on component mount or when authToken changes
  useEffect(() => {
    if (authToken) { // Only fetch if a token is available
      fetchDocuments();
    }
  }, [authToken]); // Only re-run when authToken changes, not on every render

  // Filtering logic (now depends on 'documents' state which is populated by fetch)
  useEffect(() => {
    let filtered = documents;

    if (selectedInstitutionType !== "all") {
      filtered = filtered.filter(
        (doc) => doc.institutionType === selectedInstitutionType,
      );
    }

    if (isTopBanksFilter) {
      filtered = filtered.filter((doc) =>
        topBanks.includes(doc.institutionName),
      );
    }

    if (selectedDocumentType !== "Tümü") {
      if (selectedDocumentType === "Diğer") {
        // Filter for documents with custom document types (not in predefined list)
        const predefinedTypes = documentTypes.slice(1); // Remove "Tümü" from the list
        filtered = filtered.filter(
          (doc) => !predefinedTypes.includes(doc.documentType)
        );
      } else {
        // Filter for specific document type
        filtered = filtered.filter(
          (doc) => doc.documentType === selectedDocumentType,
        );
      }
    }

    if (searchTerm) {
      filtered = filtered.filter(
        (doc) =>
          doc.institutionName
            ?.toLowerCase()
            .includes(searchTerm.toLowerCase()) ||
          doc.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
          doc.documentDescription?.toLowerCase().includes(searchTerm.toLowerCase()) ||
          doc.documentType?.toLowerCase().includes(searchTerm.toLowerCase()),
      );
    }

    setFilteredDocuments(filtered);
  }, [
    documents,
    selectedInstitutionType,
    selectedDocumentType,
    searchTerm,
    isTopBanksFilter,
  ]);

  const handleInstitutionTypeFilter = (type: InstitutionType): void => {
    setSelectedInstitutionType(type);
    setIsTopBanksFilter(false);
  };

  const handleTopBanksFilter = (): void => {
    setIsTopBanksFilter(true);
    setSelectedInstitutionType("bank");
  };

  // New handler for document type filter
  const handleDocumentTypeFilter = (type: string): void => {
    setSelectedDocumentType(type);
    
    // When a specific document type is selected, reset institution filters
    // to show results across all institutions by default, unless explicitly set otherwise.
    if (type !== "Tümü") {
      setSelectedInstitutionType("all");
      setIsTopBanksFilter(false);
    }
  };

  const formatDate = (dateString: string): string => {
    if (!dateString) return "N/A";
    try {
      return new Date(dateString).toLocaleDateString("tr-TR");
    } catch (e) {
      console.error("Invalid date string:", dateString);
      return dateString; // Return original if invalid
    }
  };

  const getInstitutionTypeLabel = (
    type: "bank" | "electronic_money",
  ): string => {
    return type === "bank" ? "Banka" : "Elektronik Para";
  };

  // Function to handle PDF viewing
  const handleViewDocument = (doc: Document) => {
    setCurrentPdfUrl(doc.fileUrl);
    setCurrentPdfTitle(doc.title);
    setShowPdfViewer(true);
  };

  // Function to handle edit navigation
  const handleEditDocument = (doc: Document) => {
    setEditingDocument(doc);
    setShowEditModal(true);
  };

  // Function to handle delete navigation
  const handleDeleteDocument = (doc: Document) => {
    setDeletingDocument(doc);
    setShowDeleteModal(true);
  };

  // Function to perform document deletion
  const performDeleteDocument = async () => {
    if (!deletingDocument) return;

    setDeleting(true);
    try {
      const response = await fetch(`${DOCUMENTS_API_URL}${deletingDocument.id}`, {
        method: "DELETE",
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error('Bu belgeyi silme yetkiniz yok.');
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Refresh the documents list
      await fetchDocuments();
      
      // Close modal and reset state
      setShowDeleteModal(false);
      setDeletingDocument(null);
    } catch (error: any) {
      console.error("Failed to delete document:", error);
      setError(`Belge silinirken bir hata oluştu: ${error.message}`);
    } finally {
      setDeleting(false);
    }
  };

  // Function to handle PDF downloading
  const handleDownloadDocument = async (doc: Document) => {
    console.log('Download initiated for:', doc.title, 'URL:', doc.fileUrl);
    
    try {
      // First, try authenticated fetch to get the file as blob
      const response = await fetch(doc.fileUrl, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${authToken}`,
        },
      });

      console.log('Fetch response status:', response.status);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Get filename from response headers if available
      const contentDisposition = response.headers.get('content-disposition');
      let filename = `${doc.title}.pdf`;
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }

      // Create blob from response
      const blob = await response.blob();
      console.log('Blob created, size:', blob.size, 'type:', blob.type);
      
      // Force download using blob URL
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = filename;
      link.style.display = "none";
      
      // Add to DOM, click, and remove
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      // Cleanup blob URL
      setTimeout(() => {
        window.URL.revokeObjectURL(url);
      }, 100);
      
      console.log('Authenticated download completed successfully');
      
    } catch (error) {
      console.error('Authenticated download failed, trying direct download:', error);
      
      try {
        // Fallback: Direct download approach without fetch
        const link = document.createElement("a");
        link.href = doc.fileUrl;
        link.download = `${doc.title}.pdf`;
        link.style.display = "none";
        link.setAttribute('target', '_self'); // Force same tab
        
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        console.log('Direct download initiated');
        
      } catch (directError) {
        console.error('Direct download failed:', directError);
        setError(`Dosya indirilemedi: ${error instanceof Error ? error.message : 'Bilinmeyen hata'}`);
        
        // Last resort: try to force download by creating a temporary anchor with download attribute
        try {
          const tempLink = document.createElement("a");
          tempLink.href = doc.fileUrl + "?download=1"; // Add download parameter
          tempLink.download = `${doc.title}.pdf`;
          tempLink.click();
          console.log('Final fallback download attempted');
        } catch (finalError) {
          console.error('All download methods failed:', finalError);
          alert('Dosya indirilemedi. Lütfen tarayıcı ayarlarınızı kontrol edin veya dosyayı manuel olarak kaydedin.');
        }
      }
    }
  };

  const EditDocumentModal: React.FC<{
    show: boolean;
    onClose: () => void;
    onDocumentUpdated: () => void;
    document: Document | null;
    existingInstitutionNames: string[];
  }> = ({ show, onClose, onDocumentUpdated, document, existingInstitutionNames }) => {
    const [formData, setFormData] = useState({
      institutionName: "",
      institutionType: "bank" as "bank" | "electronic_money",
      documentType: "KVKK",
      title: "",
      description: "",
    });
    const [newFile, setNewFile] = useState<File | null>(null);
    const [customDocumentType, setCustomDocumentType] = useState<string>("");
    const [showCustomInstitutionInput, setShowCustomInstitutionInput] = useState<boolean>(false);
    const [customInstitutionName, setCustomInstitutionName] = useState<string>("");
    const [updating, setUpdating] = useState<boolean>(false);
    const [updateProgress, setUpdateProgress] = useState<number>(0);
    const [updateError, setUpdateError] = useState<string | null>(null);

    // Initialize form with document data when modal opens
    useEffect(() => {
      if (show && document) {
        setFormData({
          institutionName: document.institutionName,
          institutionType: document.institutionType,
          documentType: document.documentType,
          title: document.title,
          description: document.documentDescription || "",
        });

        // Handle custom document type
        if (!documentTypes.slice(1).includes(document.documentType)) {
          setCustomDocumentType(document.documentType);
          setFormData(prev => ({ ...prev, documentType: "Diğer" }));
        } else {
          setCustomDocumentType("");
        }

        // Reset other states
        setNewFile(null);
        setShowCustomInstitutionInput(false);
        setCustomInstitutionName("");
        setUpdateError(null);
        setUpdating(false);
        setUpdateProgress(0);
      }
    }, [show, document]);

    const handleSubmit = async (): Promise<void> => {
      if (!document) return;

      const finalDocumentType = formData.documentType === "Diğer" ? customDocumentType : formData.documentType;
      const finalInstitutionName = showCustomInstitutionInput ? customInstitutionName : formData.institutionName;

      if (!finalInstitutionName || !formData.title || !finalDocumentType) {
        setUpdateError("Lütfen tüm zorunlu alanları doldurun.");
        return;
      }

      setUpdating(true);
      setUpdateProgress(0);
      setUpdateError(null);

      try {
        const progressInterval = setInterval(() => {
          setUpdateProgress((prev) => {
            if (prev >= 90) {
              clearInterval(progressInterval);
              return 90;
            }
            return prev + 10;
          });
        }, 200);

        const updateData = new FormData();
        updateData.append('institutionName', finalInstitutionName);
        updateData.append('institutionType', formData.institutionType);
        updateData.append('documentType', finalDocumentType);
        updateData.append('title', formData.title);
        updateData.append('documentDescription', formData.description);
        
        if (newFile) {
          updateData.append('file', newFile);
        }

        const response = await fetch(`${DOCUMENTS_API_URL}${document.id}`, {
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

        clearInterval(progressInterval);
        setUpdateProgress(100);

        setTimeout(() => {
          onClose();
          setUpdating(false);
          setUpdateProgress(0);
          onDocumentUpdated();
        }, 500);
      } catch (error: any) {
        console.error("Failed to update document:", error);
        setUpdateError(`Belge güncellenirken bir hata oluştu: ${error.message}`);
        setUpdating(false);
        setUpdateProgress(0);
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

    if (!show || !document) return null;

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div className="bg-white rounded-lg p-6 w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
          <h3 className="text-lg font-semibold mb-4">Belge Düzenle</h3>
          
          {/* Current Document Info */}
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
            <h4 className="text-sm font-medium text-blue-900 mb-2 flex items-center">
              <FileText className="h-4 w-4 mr-2" />
              Mevcut Belge Bilgileri
            </h4>
            <div className="grid grid-cols-2 gap-2 text-xs">
              <div><span className="font-medium">Kurum:</span> {document.institutionName}</div>
              <div><span className="font-medium">Tür:</span> {document.institutionType === 'bank' ? 'Banka' : 'Elektronik Para'}</div>
              <div><span className="font-medium">Belge Türü:</span> {document.documentType}</div>
              <div><span className="font-medium">Boyut:</span> {document.fileSize}</div>
            </div>
          </div>

          <div className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Institution Name */}
              <div>
                <label className="block text-sm font-medium mb-1">Kurum Adı</label>
                {!showCustomInstitutionInput ? (
                  <select
                    value={formData.institutionName}
                    onChange={handleInstitutionNameChange}
                    className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  >
                    <option value="">Kurum Seçin</option>
                    {existingInstitutionNames.map((name) => (
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
                    className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                )}
              </div>

              {/* Institution Type */}
              <div>
                <label className="block text-sm font-medium mb-1">Kurum Türü</label>
                <select
                  value={formData.institutionType}
                  onChange={(e) => setFormData({ ...formData, institutionType: e.target.value as "bank" | "electronic_money" })}
                  className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="bank">Banka</option>
                  <option value="electronic_money">Elektronik Para</option>
                </select>
              </div>

              {/* Document Type */}
              <div>
                <label className="block text-sm font-medium mb-1">Belge Türü</label>
                <select
                  value={formData.documentType}
                  onChange={(e) => {
                    setFormData({ ...formData, documentType: e.target.value });
                    if (e.target.value !== "Diğer") {
                      setCustomDocumentType("");
                    }
                  }}
                  className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  {documentTypes.slice(1).map((type) => (
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
                    className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent mt-2"
                    required
                  />
                )}
              </div>

              {/* Title */}
              <div>
                <label className="block text-sm font-medium mb-1">Belge Başlığı</label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  required
                />
              </div>
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-medium mb-1">Açıklama</label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                rows={3}
              />
            </div>

            {/* File Upload */}
            <div>
              <label className="block text-sm font-medium mb-1">
                Yeni Dosya (İsteğe bağlı - mevcut dosyayı değiştirir)
              </label>
              <input
                type="file"
                accept=".pdf,.doc,.docx,.xls,.xlsx"
                onChange={handleFileChange}
                className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              {newFile && (
                <div className="mt-2 text-sm text-gray-600">
                  <div className="flex items-center gap-2">
                    <FileText className="h-4 w-4" />
                    <span>{newFile.name}</span>
                    <span className="text-gray-400">({formatFileSize(newFile.size)})</span>
                  </div>
                </div>
              )}
              {updating && (
                <div className="mt-2">
                  <div className="flex justify-between text-sm text-gray-600 mb-1">
                    <span>Güncelleniyor...</span>
                    <span>{updateProgress}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                      style={{ width: `${updateProgress}%` }}
                    ></div>
                  </div>
                </div>
              )}
              {updateError && (
                <p className="text-red-500 text-sm mt-2">{updateError}</p>
              )}
            </div>

            <div className="flex gap-3 pt-4">
              <button
                type="button"
                onClick={handleSubmit}
                disabled={
                  updating ||
                  !formData.title ||
                  (formData.documentType === "Diğer" && !customDocumentType) ||
                  (!showCustomInstitutionInput && !formData.institutionName) ||
                  (showCustomInstitutionInput && !customInstitutionName)
                }
                className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
              >
                {updating ? "Güncelleniyor..." : "Güncelle"}
              </button>
              <button
                type="button"
                onClick={() => {
                  onClose();
                  setUpdateError(null);
                }}
                disabled={updating}
                className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400 transition-colors disabled:bg-gray-200 disabled:cursor-not-allowed"
              >
                İptal
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  };

  const AddDocumentModal: React.FC<{
    show: boolean;
    onClose: () => void;
    onDocumentAdded: () => void; // Callback to re-fetch documents
    existingInstitutionNames: string[]; // Pass existing names
  }> = ({ show, onClose, onDocumentAdded, existingInstitutionNames }) => {
    const [newDoc, setNewDoc] = useState<NewDocument>({
      institutionName: "",
      institutionType: "bank",
      documentType: "KVKK",
      title: "",
      description: "",
      file: null,
    });
    const [uploading, setUploading] = useState<boolean>(false);
    const [uploadProgress, setUploadProgress] = useState<number>(0);
    const [addError, setAddError] = useState<string | null>(null);
    const [customDocumentType, setCustomDocumentType] = useState<string>("");
    const [showCustomInstitutionInput, setShowCustomInstitutionInput] =
      useState<boolean>(false); // New state for custom institution input
    const [customInstitutionName, setCustomInstitutionName] =
      useState<string>(""); // New state for custom institution name

    // Reset modal state when it opens
    useEffect(() => {
      if (show) {
        setNewDoc({
          institutionName: "",
          institutionType: "bank",
          documentType: "KVKK",
          title: "",
          description: "",
          file: null,
        });
        setCustomDocumentType("");
        setShowCustomInstitutionInput(false);
        setCustomInstitutionName("");
        setAddError(null);
        setUploading(false);
        setUploadProgress(0);
      }
    }, [show]);

    const handleSubmit = async (): Promise<void> => {
      const finalDocumentType =
        newDoc.documentType === "Diğer"
          ? customDocumentType
          : newDoc.documentType;
      const finalInstitutionName = showCustomInstitutionInput
        ? customInstitutionName
        : newDoc.institutionName;

      if (
        !finalInstitutionName ||
        !newDoc.title ||
        !newDoc.file ||
        !finalDocumentType
      ) {
        setAddError("Lütfen tüm zorunlu alanları doldurun ve bir dosya seçin.");
        return;
      }

      setUploading(true);
      setUploadProgress(0);
      setAddError(null);

      try {
        const progressInterval = setInterval(() => {
          setUploadProgress((prev) => {
            if (prev >= 90) {
              clearInterval(progressInterval);
              return 90;
            }
            return prev + 10;
          });
        }, 200);

        await new Promise((resolve) => setTimeout(resolve, 1500));

        // Create FormData for file upload
        const formData = new FormData();
        formData.append('institutionName', finalInstitutionName);
        formData.append('institutionType', newDoc.institutionType);
        formData.append('documentType', finalDocumentType);
        formData.append('title', newDoc.title);
        formData.append('documentDescription', newDoc.description);
        formData.append('file', newDoc.file!); // Append the actual file
        formData.append('institutionUrl', 'google.com'); // Placeholder for institution URL

        const response = await fetch(DOCUMENTS_API_URL, {
          method: "POST",
          headers: {
            'Authorization': `Bearer ${authToken}`, // Add auth header if needed
          },
          body: formData, // Use FormData instead of JSON
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        await response.json();

        clearInterval(progressInterval);
        setUploadProgress(100);

        setTimeout(() => {
          onClose();
          setUploading(false);
          setUploadProgress(0);
          onDocumentAdded();
        }, 500);
      } catch (error: any) {
        console.error("Failed to add document to backend:", error);
        setAddError(`Belge eklenirken bir hata oluştu: ${error.message}`);
        setUploading(false);
        setUploadProgress(0);
      }
    };

    const formatFileSize = (bytes: number): string => {
      if (bytes === 0) return "0 Bytes";
      const k = 1024;
      const sizes = ["Bytes", "KB", "MB", "GB"];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
    };

    const handleFileChange = (e: ChangeEvent<HTMLInputElement>): void => {
      const files = e.target.files;
      if (files && files[0]) {
        setNewDoc({ ...newDoc, file: files[0] });
      }
    };

    const handleInstitutionNameChange = (
      e: ChangeEvent<HTMLSelectElement | HTMLInputElement>,
    ): void => {
      const value = e.target.value;
      if (value === "DiğerKurum") {
        // Special value to indicate custom input
        setShowCustomInstitutionInput(true);
        setNewDoc({ ...newDoc, institutionName: "" }); // Clear selected value
      } else {
        setShowCustomInstitutionInput(false);
        setCustomInstitutionName(""); // Clear custom input
        setNewDoc({ ...newDoc, institutionName: value });
      }
    };

    if (!show) return null;

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div className="bg-white rounded-lg p-6 w-full max-w-md mx-4">
          <h3 className="text-lg font-semibold mb-4">Yeni Belge Ekle</h3>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium mb-1">
                Kurum Adı
              </label>
              {!showCustomInstitutionInput ? (
                <select
                  value={newDoc.institutionName}
                  onChange={handleInstitutionNameChange}
                  className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  required
                >
                  <option value="">Kurum Seçin</option>
                  {existingInstitutionNames.map((name) => (
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
                  className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  required
                />
              )}
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">
                Kurum Türü
              </label>
              <select
                value={newDoc.institutionType}
                onChange={(e) =>
                  setNewDoc({
                    ...newDoc,
                    institutionType: e.target.value as
                      | "bank"
                      | "electronic_money",
                  })
                }
                className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="bank">Banka</option>
                <option value="electronic_money">Elektronik Para</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">
                Belge Türü
              </label>
              <select
                value={newDoc.documentType}
                onChange={(e) => {
                  setNewDoc({ ...newDoc, documentType: e.target.value });
                  if (e.target.value !== "Diğer") {
                    setCustomDocumentType("");
                  }
                }}
                className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                {documentTypes.slice(1).map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
              {newDoc.documentType === "Diğer" && (
                <input
                  type="text"
                  placeholder="Belge türünü girin..."
                  value={customDocumentType}
                  onChange={(e) => setCustomDocumentType(e.target.value)}
                  className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent mt-2"
                />
              )}
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">
                Belge Başlığı
              </label>
              <input
                type="text"
                required
                value={newDoc.title}
                onChange={(e) =>
                  setNewDoc({ ...newDoc, title: e.target.value })
                }
                className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Açıklama</label>
              <textarea
                value={newDoc.description}
                onChange={(e) =>
                  setNewDoc({ ...newDoc, description: e.target.value })
                }
                className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                rows={3}
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Dosya</label>
              <input
                type="file"
                accept=".pdf,.doc,.docx,.xls,.xlsx"
                onChange={handleFileChange}
                className="w-full p-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              {newDoc.file && (
                <div className="mt-2 text-sm text-gray-600">
                  <div className="flex items-center gap-2">
                    <FileText className="h-4 w-4" />
                    <span>{newDoc.file.name}</span>
                    <span className="text-gray-400">
                      ({formatFileSize(newDoc.file.size)})
                    </span>
                  </div>
                </div>
              )}
              {uploading && (
                <div className="mt-2">
                  <div className="flex justify-between text-sm text-gray-600 mb-1">
                    <span>Yükleniyor...</span>
                    <span>{uploadProgress}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                      style={{ width: `${uploadProgress}%` }}
                    ></div>
                  </div>
                </div>
              )}
              {addError && (
                <p className="text-red-500 text-sm mt-2">{addError}</p>
              )}
            </div>
            <div className="flex gap-3 pt-4">
              <button
                type="button"
                onClick={handleSubmit}
                disabled={
                  uploading ||
                  !newDoc.title ||
                  !newDoc.file ||
                  (newDoc.documentType === "Diğer" && !customDocumentType) ||
                  (!showCustomInstitutionInput && !newDoc.institutionName) ||
                  (showCustomInstitutionInput && !customInstitutionName)
                }
                className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
              >
                {uploading ? "Yükleniyor..." : "Ekle"}
              </button>
              <button
                type="button"
                onClick={() => {
                  onClose();
                  setAddError(null);
                  setCustomDocumentType("");
                  setShowCustomInstitutionInput(false);
                  setCustomInstitutionName("");
                }}
                disabled={uploading}
                className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400 transition-colors disabled:bg-gray-200 disabled:cursor-not-allowed"
              >
                İptal
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-gray-50 font-sans">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <Building2 className="h-8 w-8 text-blue-600 mr-3" />
              <h1 className="text-xl font-semibold text-gray-900">
                Moneymate Belge Yönetim Sistemi
              </h1>
            </div>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <User className="h-5 w-5 text-gray-400" />
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

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Filters */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <div className="flex flex-col space-y-4">
            {/* Institution Type Buttons */}
            <div className="flex flex-wrap gap-3">
              <button
                onClick={() => handleInstitutionTypeFilter("all")}
                className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                  selectedInstitutionType === "all" && !isTopBanksFilter
                    ? "bg-blue-600 text-white"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                Tüm Kurumlar
              </button>
              <button
                onClick={() => handleInstitutionTypeFilter("bank")}
                className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                  selectedInstitutionType === "bank" && !isTopBanksFilter
                    ? "bg-blue-600 text-white"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                Bankalar
              </button>
              {/* <button
                onClick={handleTopBanksFilter}
                className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                  isTopBanksFilter
                    ? "bg-blue-600 text-white"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                Top 5 Banka
              </button> */ /* Uncomment if needed */}
              
              <button
                onClick={() => handleInstitutionTypeFilter("electronic_money")}
                className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                  selectedInstitutionType === "electronic_money" &&
                  !isTopBanksFilter
                    ? "bg-blue-600 text-white"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                Elektronik Para Kuruluşları
              </button>
            </div>

            {/* Dropdown and Search */}
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="flex-1">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                  <input
                    type="text"
                    placeholder="Kurum adı, belge başlığı veya açıklama ile ara..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
              </div>
              <div className="flex gap-3">
                <select
                  value={selectedDocumentType}
                  onChange={(e) => handleDocumentTypeFilter(e.target.value)} // Use new handler
                  className="px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  {documentTypes.map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </select>
                <button
                  onClick={() => setShowAddModal(true)}
                  className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition-colors flex items-center gap-2"
                >
                  <Plus className="h-4 w-4" />
                  Belge Ekle
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Loading and Error states */}
        {loading && (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
            <p className="mt-4 text-gray-600">Belgeler yükleniyor...</p>
          </div>
        )}

        {error && (
          <div
            className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4"
            role="alert"
          >
            <strong className="font-bold">Hata!</strong>
            <span className="block sm:inline"> {error}</span>
          </div>
        )}

        {/* Document Count */}
        {!loading && !error && (
          <div className="mb-4">
            <p className="text-sm text-gray-600">
              {filteredDocuments.length} belge bulundu
            </p>
          </div>
        )}

        {/* Documents Table */}
        {!loading && !error && (
          <div className="bg-white rounded-lg shadow-sm overflow-hidden">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Kurum
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Belge Bilgileri
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Tarih
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Boyut
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      İşlemler
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {filteredDocuments.map((doc) => (
                    <tr key={doc.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div>
                          <div className="text-sm font-medium text-gray-900">
                            {doc.institutionName}
                          </div>
                          <div className="text-sm text-gray-500">
                            {getInstitutionTypeLabel(doc.institutionType)}
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="max-w-xs">
                          <div className="text-sm font-medium text-gray-900 mb-1">
                            {doc.title}
                          </div>
                          <div className="flex items-center gap-2 mb-2">
                            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                              {doc.documentType}
                            </span>
                          </div>
                          <div className="text-sm text-gray-500">
                            {doc.documentDescription || 'Açıklama bulunmuyor'}
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <div className="flex items-center">
                          <Calendar className="h-4 w-4 mr-1" />
                          {formatDate(doc.updateAt)}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {doc.fileSize}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        <div className="flex items-center justify-end space-x-2">
                          <button
                            onClick={() => handleViewDocument(doc)}
                            className="text-blue-600 hover:text-blue-800 p-2 hover:bg-blue-50 rounded-md transition-colors"
                            title="Görüntüle"
                          >
                            <Eye className="h-4 w-4" />
                          </button>
                          <button
                            onClick={() => handleEditDocument(doc)}
                            className="text-orange-600 hover:text-orange-800 p-2 hover:bg-orange-50 rounded-md transition-colors"
                            title="Düzenle"
                          >
                            <Edit className="h-4 w-4" />
                          </button>
                          <button
                            onClick={() => handleDeleteDocument(doc)}
                            className="text-red-600 hover:text-red-800 p-2 hover:bg-red-50 rounded-md transition-colors"
                            title="Sil"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                          <button
                            onClick={() => handleDownloadDocument(doc)}
                            className="text-green-600 hover:text-green-800 p-2 hover:bg-green-50 rounded-md transition-colors"
                            title="İndir"
                          >
                            <Download className="h-4 w-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {filteredDocuments.length === 0 && !loading && !error && (
              <div className="text-center py-12">
                <FileText className="mx-auto h-12 w-12 text-gray-400" />
                <h3 className="mt-2 text-sm font-medium text-gray-900">
                  Belge bulunamadı
                </h3>
                <p className="mt-1 text-sm text-gray-500">
                  Arama kriterlerinize uygun belge bulunmuyor.
                </p>
              </div>
            )}
          </div>
        )}
      </main>

      <AddDocumentModal
        show={showAddModal}
        onClose={() => setShowAddModal(false)}
        onDocumentAdded={fetchDocuments}
        existingInstitutionNames={institutionNames} // Pass the list of names
      />

      <EditDocumentModal
        show={showEditModal}
        onClose={() => {
          setShowEditModal(false);
          setEditingDocument(null);
        }}
        onDocumentUpdated={fetchDocuments}
        document={editingDocument}
        existingInstitutionNames={institutionNames}
      />

      {/* Delete Confirmation Modal */}
      {showDeleteModal && deletingDocument && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md mx-4">
            <div className="flex items-center mb-4">
              <div className="flex-shrink-0">
                <Trash2 className="h-6 w-6 text-red-600" />
              </div>
              <div className="ml-3">
                <h3 className="text-lg font-medium text-gray-900">Belgeyi Sil</h3>
              </div>
            </div>
            
            <div className="mb-4">
              <p className="text-sm text-gray-700 mb-3">
                Bu belgeyi silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.
              </p>
              
              {/* Document Info */}
              <div className="bg-gray-50 border border-gray-200 rounded-lg p-3">
                <div className="text-sm">
                  <div className="font-medium text-gray-900 mb-1">{deletingDocument.title}</div>
                  <div className="text-gray-600 mb-1">{deletingDocument.institutionName}</div>
                  <div className="text-gray-500">{deletingDocument.documentType}</div>
                </div>
              </div>
            </div>

            {error && (
              <div className="mb-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded p-2">
                {error}
              </div>
            )}

            <div className="flex gap-3">
              <button
                type="button"
                onClick={performDeleteDocument}
                disabled={deleting}
                className="flex-1 bg-red-600 text-white py-2 px-4 rounded-md hover:bg-red-700 transition-colors disabled:bg-red-400 disabled:cursor-not-allowed flex items-center justify-center"
              >
                {deleting ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    Siliniyor...
                  </>
                ) : (
                  <>
                    <Trash2 className="h-4 w-4 mr-2" />
                    Sil
                  </>
                )}
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowDeleteModal(false);
                  setDeletingDocument(null);
                  setError(null); // Clear any errors
                }}
                disabled={deleting}
                className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400 transition-colors disabled:bg-gray-200 disabled:cursor-not-allowed"
              >
                İptal
              </button>
            </div>
          </div>
        </div>
      )}

      {/* PDF Viewer Modal */}
      {showPdfViewer && (
        <div className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg w-full h-full max-w-6xl max-h-[90vh] m-4 flex flex-col">
            <div className="flex justify-between items-center p-4 border-b">
              <h3 className="text-lg font-semibold truncate">{currentPdfTitle}</h3>
              <div className="flex gap-2">
                <button
                  onClick={() => {
                    // Create a temporary document object for download
                    const tempDoc: Document = {
                      fileUrl: currentPdfUrl,
                      title: currentPdfTitle,
                      id: '',
                      institutionName: '',
                      institutionType: 'bank',
                      documentType: '',
                      updateAt: '',
                      documentDescription: '',
                      fileSize: ''
                    };
                    handleDownloadDocument(tempDoc);
                  }}
                  className="bg-green-600 text-white px-3 py-1 rounded-md hover:bg-green-700 transition-colors flex items-center gap-1"
                  title="İndir"
                >
                  <Download className="h-4 w-4" />
                  İndir
                </button>
                <button
                  onClick={() => setShowPdfViewer(false)}
                  className="bg-gray-600 text-white px-3 py-1 rounded-md hover:bg-gray-700 transition-colors"
                >
                  Kapat
                </button>
              </div>
            </div>
            <div className="flex-1 p-4">
              <iframe
                src={currentPdfUrl}
                className="w-full h-full border rounded"
                title={currentPdfTitle}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default BankingDocumentApp;
