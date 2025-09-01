// import React from 'react';
// import { useAuth } from '../context/AuthContext';
// import BankingDocumentApp from './welcome';
// import LoginScreen from './login';
// import RegisterScreen from './register';

// const App: React.FC = () => {
//   const { authToken, login, logout, isAuthenticated, loading } = useAuth();

//   const [showRegister, setShowRegister] = React.useState(false);

//   const handleGoToRegister = () => setShowRegister(true);
//   const handleGoToLogin = () => setShowRegister(false);

//   // While loading, show a spinner or nothing
//   if (loading) {
//     return <div>Loading...</div>;
//   }

//   // // If user is authenticated, show the main app
//   // if (isAuthenticated) {
//   //   return <BankingDocumentApp authToken={authToken} onLogout={logout} />;
//   // }

//   // Otherwise, show auth screens
//   if (showRegister) {
//     return <RegisterScreen onRegisterSuccess={handleGoToLogin} onGoToLogin={handleGoToLogin} />;
//   }

//   return <LoginScreen onLoginSuccess={login} onGoToRegister={handleGoToRegister} />;
// };

// export default App;