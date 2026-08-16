
import { Navigate, Outlet } from 'react-router-dom';

export function PrivateRoute() {
  const token = localStorage.getItem('@ProjetoEmpresa:token');

  if (!token) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
