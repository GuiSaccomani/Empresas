import { createContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { api } from '../services/api';
import { useNavigate } from 'react-router-dom';

interface User {
  companyId: string;
  sub: string;
  role: string;
  usaAgenda: boolean;
  usaFinanceiro: boolean;
  usaClientes: boolean;
}

interface AuthContextData {
  signed: boolean;
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
}

export const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const storagedUser = localStorage.getItem('@App:user');
    const storagedToken = localStorage.getItem('@App:token');

    if (storagedUser && storagedToken) {
      setUser(JSON.parse(storagedUser));
      api.defaults.headers.Authorization = `Bearer ${storagedToken}`;
    }
    setLoading(false);
  }, []);

  async function login(email: string, password: string) {
    const response = await api.post('/auth/login', { email, password });
    const { token } = response.data;

    const payload = JSON.parse(atob(token.split('.')[1]));
    
    const loggedUser = {
      companyId: payload.companyId,
      sub: payload.sub,
      role: payload.role,
      usaAgenda: payload.usaAgenda !== false,
      usaFinanceiro: payload.usaFinanceiro !== false,
      usaClientes: payload.usaClientes !== false
    };

    setUser(loggedUser);
    api.defaults.headers.Authorization = `Bearer ${token}`;

    localStorage.setItem('@App:user', JSON.stringify(loggedUser));
    localStorage.setItem('@App:token', token);
    
    navigate('/dashboard');
  }

  function logout() {
    setUser(null);
    localStorage.removeItem('@App:user');
    localStorage.removeItem('@App:token');
    delete api.defaults.headers.Authorization;
    navigate('/');
  }

  return (
    <AuthContext.Provider value={{ signed: !!user, user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};