import { useEffect, useState, useContext } from 'react';
import { api } from '../services/api';
import { getCustomers } from '../services/customers';
import { getAppointmentsByRange } from '../services/appointments';
import { AuthContext } from '../contexts/AuthContext';
import { TrendingUp, TrendingDown, DollarSign, AlertCircle, Users, Calendar } from 'lucide-react';

export function Dashboard() {
  const { user } = useContext(AuthContext);
  const [balance, setBalance] = useState<number | null>(null);
  const [customersCount, setCustomersCount] = useState<number | null>(null);
  const [appointmentsToday, setAppointmentsToday] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setIsLoading(true);
        // 1. Fetch balance
        try {
          const balanceRes = await api.get('/financial/balance');
          setBalance(balanceRes.data.currentBalance || 0);
        } catch (e) {
          console.warn('Endpoint financeiro nao implementado ou sem permissao');
          setBalance(0);
        }

        // 2. Fetch total customers
        try {
          const custsData = await getCustomers(0, 1);
          setCustomersCount(custsData.totalElements || (custsData.content ? custsData.content.length : (custsData.length || 0)));
        } catch (e) {
          console.error('Erro ao buscar clientes:', e);
          setCustomersCount(0);
        }

        // 3. Fetch appointments today
        try {
          const today = new Date();
          const start = new Date(today.setHours(0,0,0,0)).toISOString();
          const end = new Date(today.setHours(23,59,59,999)).toISOString();
          const apptRes = await getAppointmentsByRange(start, end);
          setAppointmentsToday(apptRes.length || 0);
        } catch (e) {
          console.error('Erro ao buscar agendamentos:', e);
          setAppointmentsToday(0);
        }

      } catch (err: any) {
        console.error('Erro ao buscar dados do dashboard:', err);
        setError('Não foi possível carregar alguns dados no momento.');
      } finally {
        setIsLoading(false);
      }
    };

    if (user?.companyId) {
      fetchDashboardData();
    }
  }, [user]);

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  const isPositive = balance !== null && balance >= 0;

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 dark:text-white">Visão Geral</h1>
          <p className="text-slate-600 dark:text-slate-400 mt-1">Acompanhe os principais indicadores do seu negócio.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* Card Financeiro */}
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-slate-200 dark:border-slate-700">
          <div className="flex justify-between items-start mb-4">
            <div className="p-3 rounded-lg bg-blue-50 dark:bg-blue-900/20">
              <DollarSign className="w-6 h-6 text-blue-600 dark:text-blue-400" />
            </div>
            {balance !== null && (
              <span className={"flex items-center gap-1 text-sm font-medium px-2.5 py-1 rounded-full " + (
                isPositive 
                  ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' 
                  : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
              )}>
                {isPositive ? <TrendingUp className="w-4 h-4" /> : <TrendingDown className="w-4 h-4" />}
                {isPositive ? 'Positivo' : 'Negativo'}
              </span>
            )}
          </div>
          
          <div>
            <h3 className="text-slate-500 dark:text-slate-400 text-sm font-medium mb-1">Saldo Atual</h3>
            {isLoading && balance === null ? (
              <div className="h-9 w-32 bg-slate-200 dark:bg-slate-700 rounded animate-pulse" />
            ) : (
              <p className={"text-3xl font-bold " + (
                isPositive ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'
              )}>
                {balance !== null ? formatCurrency(balance) : 'R$ 0,00'}
              </p>
            )}
          </div>
        </div>

        {/* Card Clientes Ativos */}
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-slate-200 dark:border-slate-700">
          <div className="flex justify-between items-start mb-4">
            <div className="p-3 rounded-lg bg-purple-50 dark:bg-purple-900/20">
              <Users className="w-6 h-6 text-purple-600 dark:text-purple-400" />
            </div>
          </div>
          <div>
            <h3 className="text-slate-500 dark:text-slate-400 text-sm font-medium mb-1">Clientes Ativos</h3>
            {isLoading && customersCount === null ? (
              <div className="h-9 w-16 bg-slate-200 dark:bg-slate-700 rounded animate-pulse" />
            ) : (
              <p className="text-3xl font-bold text-slate-900 dark:text-white">
                {customersCount !== null ? customersCount : '--'}
              </p>
            )}
          </div>
        </div>

        {/* Card Agendamentos Hoje */}
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-slate-200 dark:border-slate-700">
          <div className="flex justify-between items-start mb-4">
            <div className="p-3 rounded-lg bg-orange-50 dark:bg-orange-900/20">
              <Calendar className="w-6 h-6 text-orange-600 dark:text-orange-400" />
            </div>
          </div>
          <div>
            <h3 className="text-slate-500 dark:text-slate-400 text-sm font-medium mb-1">Agendamentos Hoje</h3>
            {isLoading && appointmentsToday === null ? (
              <div className="h-9 w-16 bg-slate-200 dark:bg-slate-700 rounded animate-pulse" />
            ) : (
              <p className="text-3xl font-bold text-slate-900 dark:text-white">
                {appointmentsToday !== null ? appointmentsToday : '--'}
              </p>
            )}
          </div>
        </div>
      </div>
      
      {error && (
        <div className="mt-4 flex items-center gap-2 text-red-500 text-sm">
          <AlertCircle className="w-5 h-5" />
          <span>{error}</span>
        </div>
      )}
    </div>
  );
}