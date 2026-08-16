import { useEffect, useState } from 'react';
import { api } from '../services/api';
import { TrendingUp, TrendingDown, DollarSign, Activity, AlertCircle } from 'lucide-react';

export function Dashboard() {
  const [balance, setBalance] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchBalance = async () => {
      try {
        const response = await api.get('/financial/balance');
        // Supondo que a API retorne algo como { balance: 1500.50 }
        setBalance(response.data.balance || 0);
      } catch (err: any) {
        console.error('Erro ao buscar saldo:', err);
        // Fallback temporário para UI não quebrar caso a rota backend ainda não exista
        setError('Não foi possível carregar os dados financeiros no momento.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchBalance();
  }, []);

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
              <span className={`flex items-center gap-1 text-sm font-medium px-2.5 py-1 rounded-full ${
                isPositive 
                  ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' 
                  : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
              }`}>
                {isPositive ? <TrendingUp className="w-4 h-4" /> : <TrendingDown className="w-4 h-4" />}
                {isPositive ? 'Positivo' : 'Negativo'}
              </span>
            )}
          </div>
          
          <div>
            <h3 className="text-slate-500 dark:text-slate-400 text-sm font-medium mb-1">Saldo Atual</h3>
            {isLoading ? (
              <div className="h-9 w-32 bg-slate-200 dark:bg-slate-700 rounded animate-pulse" />
            ) : error ? (
              <div className="flex items-center gap-2 text-red-500 text-sm mt-2">
                <AlertCircle className="w-4 h-4" />
                <span>{error}</span>
              </div>
            ) : (
              <p className={`text-3xl font-bold ${
                isPositive ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'
              }`}>
                {balance !== null ? formatCurrency(balance) : 'R$ 0,00'}
              </p>
            )}
          </div>
        </div>

        {/* Card Placeholder 1 */}
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-slate-200 dark:border-slate-700">
          <div className="flex justify-between items-start mb-4">
            <div className="p-3 rounded-lg bg-purple-50 dark:bg-purple-900/20">
              <Activity className="w-6 h-6 text-purple-600 dark:text-purple-400" />
            </div>
          </div>
          <div>
            <h3 className="text-slate-500 dark:text-slate-400 text-sm font-medium mb-1">Clientes Ativos</h3>
            <p className="text-3xl font-bold text-slate-900 dark:text-white">
              --
            </p>
          </div>
        </div>

        {/* Card Placeholder 2 */}
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-slate-200 dark:border-slate-700">
          <div className="flex justify-between items-start mb-4">
            <div className="p-3 rounded-lg bg-orange-50 dark:bg-orange-900/20">
              <Activity className="w-6 h-6 text-orange-600 dark:text-orange-400" />
            </div>
          </div>
          <div>
            <h3 className="text-slate-500 dark:text-slate-400 text-sm font-medium mb-1">Agendamentos Hoje</h3>
            <p className="text-3xl font-bold text-slate-900 dark:text-white">
              --
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}