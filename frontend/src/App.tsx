import { Routes, Route, Navigate } from 'react-router-dom';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { ForgotPassword } from './pages/ForgotPassword';
import { ResetPassword } from './pages/ResetPassword';
import { Dashboard } from './pages/Dashboard';
import { Customers } from './pages/Customers';
import { Layout } from './components/Layout';
import { PrivateRoute } from './components/PrivateRoute';
import { Team } from './pages/Team';
import { Agenda } from './pages/Agenda';
import { Financeiro } from './pages/Financeiro';
import { PublicBooking } from './pages/PublicBooking';

function App() {
  return (
    <Routes>
        {/* Public Routes */}
        <Route path="/agendar/:slug" element={<PublicBooking />} />
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      
      <Route element={<PrivateRoute />}>
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/dashboard/clientes" element={<Customers />} />
          <Route path="/dashboard/equipe" element={<Team />} />
          <Route path="/dashboard/agenda" element={<Agenda />} />
          <Route path="/dashboard/financeiro" element={<Financeiro />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;