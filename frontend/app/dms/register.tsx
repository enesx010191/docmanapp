import React, { useState } from 'react';
import { UserPlus, Mail, Lock, User } from 'lucide-react'; // Icons for registration

interface RegisterScreenProps {
  onRegisterSuccess: () => void; // Callback to notify parent of successful registration
  onGoToLogin: () => void; // Callback to navigate to login screen
}

const RegisterScreen: React.FC<RegisterScreenProps> = ({ onRegisterSuccess, onGoToLogin,  }) => {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [firstName, setFirstName] = useState<string>(''); // New state for first name
  const [lastName, setLastName] = useState<string>('');   // New state for last name
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // --- Backend API Endpoint using environment variable ---
  const API_URL = import.meta.env.VITE_API_BASE_URL;
  const REGISTER_API_URL = `${API_URL}/auth/register`;

  const handleRegister = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    // Basic validation
    if (!email || !password || !confirmPassword || !firstName || !lastName) {
      setError('Lütfen tüm alanları doldurun.');
      setLoading(false);
      return;
    }
    if (password !== confirmPassword) {
      setError('Şifreler eşleşmiyor.');
      setLoading(false);
      return;
    }
    if (password.length < 6) {
      setError('Şifre en az 6 karakter olmalıdır.');
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(REGISTER_API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        // Include firstName and lastName in the request body
        body: JSON.stringify({ email, password, firstName, lastName }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: 'Bilinmeyen bir hata oluştu.' }));
        throw new Error(errorData.message || `Kayıt başarısız oldu: ${response.statusText}`);
      }

      const data = await response.json();
      console.log('Registration successful:', data);
      setSuccessMessage('Kayıt başarılı! Şimdi giriş yapabilirsiniz.');
      
      // Optionally, you can automatically redirect to login or trigger a login flow
      setTimeout(() => {
        onRegisterSuccess(); // Notify parent
        onGoToLogin(); // Redirect to login screen
      }, 2000); // Give user time to see success message

    } catch (err: any) {
      console.error('Registration error:', err);
      setError(err.message || 'Beklenmedik bir hata oluştu.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center font-sans p-4">
      <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md mx-auto">
        <div className="flex flex-col items-center mb-6">
          <UserPlus className="h-12 w-12 text-green-600 mb-3" />
          <h2 className="text-2xl font-bold text-gray-900">Kayıt Ol</h2>
          <p className="text-gray-500 text-sm mt-1">Yeni bir hesap oluşturun</p>
        </div>

        <form onSubmit={handleRegister} className="space-y-5">
          {/* First Name Input */}
          <div>
            <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-1">
              Ad
            </label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                placeholder="Adınızı girin"
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent"
                required
                autoComplete="given-name"
              />
            </div>
          </div>

          {/* Last Name Input */}
          <div>
            <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-1">
              Soyad
            </label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                type="text"
                id="lastName"
                name="lastName"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                placeholder="Soyadınızı girin"
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent"
                required
                autoComplete="family-name"
              />
            </div>
          </div>

          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
              E-posta
            </label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                type="email"
                id="email"
                name="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="örnek@kurum.com"
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent"
                required
                autoComplete="email"
              />
            </div>
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
              Şifre
            </label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                type="password"
                id="password"
                name="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Şifrenizi girin"
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent"
                required
                autoComplete="new-password"
              />
            </div>
          </div>

          <div>
            <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">
              Şifreyi Onayla
            </label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Şifrenizi tekrar girin"
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent"
                required
                autoComplete="new-password"
              />
            </div>
          </div>

          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-md text-sm" role="alert">
              {error}
            </div>
          )}

          {successMessage && (
            <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-md text-sm" role="alert">
              {successMessage}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-green-600 text-white py-2.5 px-4 rounded-md font-semibold hover:bg-green-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {loading ? (
              <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            ) : (
              <>
                <UserPlus className="h-5 w-5" />
                Kayıt Ol
              </>
            )}
          </button>
        </form>

        <div className="mt-6 text-center text-sm">
          <p className="text-gray-600">
            Zaten bir hesabınız var mı?{' '}
          </p>
          <button
            onClick={onGoToLogin}
            className="font-medium text-blue-600 hover:text-blue-500 focus:outline-none"
          >
            Giriş Yap
            </button>
        </div>
      </div>
    </div>
  );
};

export default RegisterScreen;