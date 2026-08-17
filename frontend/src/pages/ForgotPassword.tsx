import { useState } from 'react';
import { Mail, ArrowLeft, Loader2, CheckCircle2 } from 'lucide-react';
import { Link } from 'react-router-dom';
import { api } from '../services/api';

export function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      await api.post('/auth/forgot-password', { email });
    } catch (err) {
      // Ignoramos o erro visualmente por segurança
    } finally {
      setIsLoading(false);
      setSuccess(true);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-slate-50 dark:bg-slate-900">
      <div className="max-w-md w-full bg-white dark:bg-slate-800 rounded-xl shadow-xl overflow-hidden border border-slate-200 dark:border-slate-700 p-8">
        <div className="mb-6">
          <Link to="/login" className="inline-flex items-center gap-2 text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200 transition-colors">
            <ArrowLeft className="w-4 h-4" /> Voltar para o Login
          </Link>
        </div>

        <h1 className="text-3xl font-bold text-slate-900 dark:text-white mb-2">Recuperar Senha</h1>
        
        {success ? (
          <div className="mt-6 p-6 bg-green-50 dark:bg-green-900/30 border border-green-200 dark:border-green-800 rounded-lg text-center">
            <CheckCircle2 className="w-12 h-12 text-green-500 mx-auto mb-4" />
            <p className="text-green-800 dark:text-green-200 font-medium text-lg">
              Se o e-mail existir na nossa base, enviamos um link com as instruções para redefinir sua senha.
            </p>
            <p className="text-green-700 dark:text-green-300 text-sm mt-2">
              Verifique sua caixa de entrada e a pasta de spam.
            </p>
          </div>
        ) : (
          <>
            <p className="text-slate-600 dark:text-slate-300 mb-8">
              Digite o e-mail cadastrado e enviaremos um link para você criar uma nova senha.
            </p>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-lg font-medium text-slate-700 dark:text-slate-200 mb-2">E-mail</label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <Mail className="h-6 w-6 text-slate-400" />
                  </div>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="block w-full pl-12 pr-4 py-4 text-lg border border-slate-300 dark:border-slate-600 rounded-lg bg-slate-50 dark:bg-slate-700 text-slate-900 dark:text-white placeholder-slate-400 focus:ring-4 focus:ring-blue-300 outline-none transition-all"
                    placeholder="seu@email.com"
                    required
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={isLoading || !email}
                className="w-full flex items-center justify-center gap-2 py-4 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow-sm text-xl font-medium transition-all disabled:opacity-70 disabled:cursor-not-allowed"
              >
                {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : 'Enviar Link de Recuperação'}
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
}