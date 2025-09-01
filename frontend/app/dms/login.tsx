import React, { useState } from 'react';
import { LogIn, Mail, Lock } from 'lucide-react'; // Icons for email and password

const API_URL = import.meta.env.VITE_API_BASE_URL; // Use environment variable directly
const LOGIN_API_URL = `${API_URL}/auth/login`; // Define the login API endpoint

interface LoginScreenProps {
  onLoginSuccess: (token: string) => void;
  onGoToRegister: () => void;
}

const LoginScreen: React.FC<LoginScreenProps> = ({ onLoginSuccess, onGoToRegister }) => {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  

  const handleLogin = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    // Basic validation
    if (!email || !password) {
      setError('Lütfen e-posta ve şifrenizi girin.');
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(LOGIN_API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Giriş başarısız oldu.');
      }
      
      const data = await response.json();
      const jwtToken = data.data.token; 
      console.log('Login successful:', data);


      // Dummy login logic:
      if (data.success && jwtToken) {
        // Here you would typically redirect the user or update global auth state
        // For this example, we'll just log success.

        localStorage.setItem('authToken', jwtToken);
        localStorage.setItem('email', email); // Store email for future use
        onLoginSuccess(jwtToken); // Notify parent component of successful login
        
        // Remove React Router navigation - let parent component handle navigation
      } else {
        console.log("token", jwtToken);
        throw new Error('Geçersiz e-posta veya şifre.');
      }

    } catch (err: any) {
      console.error('Login error:', err);
      setError(err.message || 'Beklenmedik bir hata oluştu.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center font-sans p-4">
      <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md mx-auto">
        <div className="flex flex-col items-center mb-6">
          <LogIn className="h-12 w-12 text-blue-600 mb-3" />
          <h2 className="text-2xl font-bold text-gray-900">Giriş Yap</h2>
          <p className="text-gray-500 text-sm mt-1">Belge Yönetim Sistemine Hoş Geldiniz</p>
        </div>

        <form onSubmit={handleLogin} className="space-y-5">
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
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                required
                autoComplete="current-password"
              />
            </div>
          </div>

          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-md text-sm" role="alert">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-2.5 px-4 rounded-md font-semibold hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {loading ? (
              <svg className="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            ) : (
              <>
                <LogIn className="h-5 w-5" />
                Giriş Yap
              </>
            )}
          </button>
        </form>

        <div className="mt-6 text-center text-sm">
          <a href="#" className="font-medium text-blue-600 hover:text-blue-500">
            Şifrenizi mi unuttunuz?
          </a>
          <p className="mt-2 text-gray-600">
            Hesabınız yok mu?{' '}
         <button
            onClick={onGoToRegister}
            className="font-medium text-blue-600 hover:text-blue-500 focus:outline-none"
          >
            Şimdi kaydolun
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginScreen;
