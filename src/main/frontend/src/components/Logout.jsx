import { useEffect } from 'react';
import { Navigate } from 'react-router-dom';

export default function Logout() {
  useEffect(() => {
    localStorage.removeItem('auth');
    localStorage.removeItem('user');
    localStorage.removeItem('login');
  }, []);

  return <Navigate to="/login" replace />;
}
