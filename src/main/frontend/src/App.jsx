import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './components/LoginPage';
import Dashboard from './components/Dashboard';
import Logout from './components/Logout';
import SalaryHistory from './components/SalaryHistory';
import AdminPage from './components/AdminPage';
import HoursWorked from './components/HoursWorked';
import FuncionariosPage from './components/FuncionariosPage';
import GerirFolhasPage from './components/GerirFolhasPage';

function PrivateRoute({ children }) {
  let user = null;
  try {
    user = JSON.parse(localStorage.getItem('user') || 'null');
  } catch {
    user = null;
  }
  return user ? children : <Navigate to="/login" replace />;
}

function ManagerRoute({ children }) {
  let user = null;
  try {
    user = JSON.parse(localStorage.getItem('user') || 'null');
  } catch {
    user = null;
  }
  return user && user.permissao === 1 ? children : <Navigate to="/" replace />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/logout" element={<Logout />} />
        <Route
          path="/"
          element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          }
        />
        <Route
          path="/salarios"
          element={
            <PrivateRoute>
              <SalaryHistory />
            </PrivateRoute>
          }
        />
        <Route
          path="/horas"
          element={
            <PrivateRoute>
              <HoursWorked />
            </PrivateRoute>
          }
        />
        <Route
          path="/admin"
          element={
            <PrivateRoute>
              <AdminPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/funcionarios"
          element={
            <PrivateRoute>
              <ManagerRoute>
                <FuncionariosPage />
              </ManagerRoute>
            </PrivateRoute>
          }
        />
        <Route
          path="/funcionarios/:id/folhas"
          element={
            <PrivateRoute>
              <ManagerRoute>
                <GerirFolhasPage />
              </ManagerRoute>
            </PrivateRoute>
          }
        />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App
