import { useState, useContext, useEffect } from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { Home, Users, Calendar, DollarSign, LogOut, Menu, X, Building2, Shield, Bell } from 'lucide-react';
import { AuthContext } from '../contexts/AuthContext';
import { getNotifications, markNotificationAsRead } from '../services/notifications';
import type { Notification } from '../services/notifications';

export function Layout() {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const location = useLocation();
  const { user, logout } = useContext(AuthContext);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [showNotifications, setShowNotifications] = useState(false);
  const unreadCount = notifications.filter(n => !n.read).length;

  useEffect(() => {
    const fetchNotifs = async () => {
      try {
        const data = await getNotifications(0, 10);
        setNotifications(data.content || []);
      } catch (err) {
        console.error('Erro ao buscar notificacoes', err);
      }
    };
    fetchNotifs();
    const interval = setInterval(fetchNotifs, 30000);
    return () => clearInterval(interval);
  }, []);

  const handleOpenNotifications = () => {
    setShowNotifications(!showNotifications);
    if (!showNotifications && unreadCount > 0) {
      notifications.filter(n => !n.read).forEach(async (n) => {
        try {
          await markNotificationAsRead(n.id);
          setNotifications(prev => prev.map(p => p.id === n.id ? { ...p, read: true } : p));
        } catch (e) {
          console.error(e);
        }
      });
    }
  };

  const handleLogout = () => {
    logout();
  };

  const navItems = [
    { name: 'Início', path: '/dashboard', icon: Home, adminOnly: false },
    { name: 'Clientes', path: '/dashboard/clientes', icon: Users, adminOnly: false },
    { name: 'Agenda', path: '/dashboard/agenda', icon: Calendar, adminOnly: false },
    { name: 'Financeiro', path: '/dashboard/financeiro', icon: DollarSign, adminOnly: true },
    { name: 'Minha Equipe', path: '/dashboard/equipe', icon: Shield, adminOnly: true },
  ];

  const filteredNavItems = navItems.filter(item => {
    if (item.adminOnly && user?.role !== 'ROLE_ADMIN') {
      return false;
    }
    if (item.name === 'Agenda' && user?.usaAgenda === false) return false;
    if (item.name === 'Financeiro' && user?.usaFinanceiro === false) return false;
    if (item.name === 'Clientes' && user?.usaClientes === false) return false;
    return true;
  });

  const NotificationDropdown = () => (
    <div className="relative">
      <button 
        onClick={handleOpenNotifications}
        className="relative p-2 text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-white transition-colors"
      >
        <Bell className="w-6 h-6" />
        {unreadCount > 0 && (
          <span className="absolute top-0 right-0 w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center border-2 border-white dark:border-slate-900">
            {unreadCount}
          </span>
        )}
      </button>

      {showNotifications && (
        <div className="absolute right-0 mt-2 w-80 bg-white dark:bg-slate-800 rounded-xl shadow-xl border border-slate-200 dark:border-slate-700 overflow-hidden z-50">
          <div className="p-4 border-b border-slate-100 dark:border-slate-700 font-bold text-slate-800 dark:text-white">
            Notificações
          </div>
          <div className="max-h-96 overflow-y-auto">
            {notifications.length === 0 ? (
              <div className="p-4 text-center text-slate-500 dark:text-slate-400 text-sm">Nenhuma notificação recente.</div>
            ) : (
              notifications.map(n => (
                <div key={n.id} className={`p-4 border-b border-slate-100 dark:border-slate-700 last:border-0 ${n.read ? 'opacity-60' : 'bg-blue-50/50 dark:bg-slate-700/30'}`}>
                  <p className="text-sm text-slate-700 dark:text-slate-300">{n.message}</p>
                  <span className="text-xs text-slate-400 mt-1 block">{new Date(n.createdAt).toLocaleString('pt-BR')}</span>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );

  const SidebarContent = () => (
    <div className="flex flex-col h-full bg-slate-900 text-slate-300">
      <div className="p-6 flex flex-col gap-1 border-b border-slate-800">
        <div className="flex items-center gap-3 text-white font-bold text-xl">
          <Building2 className="w-8 h-8 text-blue-500" />
          Gestão PRO
        </div>
        <div className="text-xs font-semibold text-slate-400 mt-2 px-1 uppercase tracking-wider">
          {user?.role === 'ROLE_ADMIN' ? 'Administrador' : 'Equipe (Staff)'}
        </div>
      </div>
      
      <nav className="flex-1 px-4 py-6 space-y-2">
        {filteredNavItems.map((item) => {
          const isActive = location.pathname === item.path;
          return (
            <Link
              key={item.path}
              to={item.path}
              onClick={() => setIsMobileMenuOpen(false)}
              className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                isActive 
                  ? 'bg-blue-600 text-white font-medium shadow-md' 
                  : 'hover:bg-slate-800 hover:text-white'
              }`}
            >
              <item.icon className={`w-5 h-5 ${isActive ? 'text-white' : 'text-slate-400'}`} />
              {item.name}
            </Link>
          );
        })}
      </nav>

      <div className="p-4 border-t border-slate-800">
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 w-full px-4 py-3 text-red-400 hover:bg-red-500/10 hover:text-red-300 rounded-lg transition-colors"
        >
          <LogOut className="w-5 h-5" />
          Sair do Sistema
        </button>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 flex">
      <aside className="hidden md:flex w-72 flex-col fixed inset-y-0 left-0 z-50">
        <SidebarContent />
      </aside>

      <header className="md:hidden fixed top-0 left-0 right-0 h-16 bg-slate-900 flex items-center justify-between px-4 z-40 border-b border-slate-800">
        <div className="flex items-center gap-2 text-white font-bold text-lg">
          <Building2 className="w-6 h-6 text-blue-500" />
          Gestão PRO
        </div>
        <div className="flex items-center gap-2">
          <NotificationDropdown />
          <button 
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="text-slate-300 hover:text-white p-2"
          >
            {isMobileMenuOpen ? <X className="w-7 h-7" /> : <Menu className="w-7 h-7" />}
          </button>
        </div>
      </header>

      {isMobileMenuOpen && (
        <div className="md:hidden fixed inset-0 z-30 flex">
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm" onClick={() => setIsMobileMenuOpen(false)} />
          <div className="relative w-4/5 max-w-sm flex flex-col bg-slate-900 h-full shadow-2xl">
            <SidebarContent />
          </div>
        </div>
      )}

      <main className="flex-1 md:ml-72 pt-16 md:pt-0 min-h-screen relative">
        <div className="hidden md:flex h-16 items-center justify-end px-8 border-b border-slate-200 dark:border-slate-800 bg-white/50 dark:bg-slate-900/50 backdrop-blur-sm sticky top-0 z-40">
          <NotificationDropdown />
        </div>
        <div className="p-4 md:p-8 max-w-7xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
