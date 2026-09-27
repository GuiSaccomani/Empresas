import { useState, useEffect } from 'react';
import { Users, Plus, Mail, Phone, Loader2, X, CheckCircle2 , Edit2, Trash2} from 'lucide-react';
import { getCustomers, createCustomer, updateCustomer, deleteCustomer } from '../services/customers';
import type { Customer } from '../services/customers';
import { formatPhone } from '../utils/masks';

export function Customers() {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [toastMessage, setToastMessage] = useState('');
  const [editingId, setEditingId] = useState<string | null>(null);

  // Form
  const [formData, setFormData] = useState({ name: '', email: '', phone: '' });

  const fetchCustomers = async () => {
    setIsLoading(true);
    try {
      const data = await getCustomers(0, 50);
      setCustomers(data.content || []);
    } catch (err) {
      console.error('Erro ao buscar clientes', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    let { name, value } = e.target;
    if (name === 'phone') {
      value = formatPhone(value);
    }
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmitCustomer = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const payload = {
        ...formData,
        phone: formData.phone.replace(/\D/g, '')
      };
      
      if (editingId) {
        await updateCustomer(editingId, payload);
        setToastMessage('Cliente atualizado com sucesso!');
      } else {
        await createCustomer(payload);
        setToastMessage('Cliente cadastrado com sucesso!');
      }
      
      setTimeout(() => setToastMessage(''), 3000);
      setIsModalOpen(false);
      setFormData({ name: '', email: '', phone: '' });
      setEditingId(null);
      fetchCustomers();
    } catch (error) {
      alert(editingId ? 'Erro ao atualizar cliente' : 'Erro ao cadastrar cliente');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleEdit = (customer: Customer) => {
    setEditingId(customer.id);
    setFormData({ name: customer.name, email: customer.email || '', phone: customer.phone || '' });
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      try {
        await deleteCustomer(id);
        setToastMessage('Cliente excluído com sucesso!');
        setTimeout(() => setToastMessage(''), 3000);
        fetchCustomers();
      } catch (err) {
        alert('Erro ao excluir cliente');
      }
    }
  };

  const openNewModal = () => {
    setEditingId(null);
    setFormData({ name: '', email: '', phone: '' });
    setIsModalOpen(true);
  };


  return (
    <div className="space-y-6">
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed top-20 right-4 md:right-8 z-50 animate-fade-in-down flex items-center gap-3 bg-green-500 text-white px-6 py-4 rounded-lg shadow-lg">
          <CheckCircle2 className="w-6 h-6" />
          <span className="font-medium">{toastMessage}</span>
        </div>
      )}

      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 dark:text-white">Meus Clientes</h1>
          <p className="text-slate-600 dark:text-slate-400 mt-1">Gerencie sua carteira de clientes</p>
        </div>
        <button
          onClick={openNewModal}
          className="w-full sm:w-auto flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium transition-colors shadow-sm"
        >
          <Plus className="w-5 h-5" />
          Novo Cliente
        </button>
      </div>

      {/* Content */}
      {isLoading ? (
        <div className="flex flex-col items-center justify-center py-20">
          <Loader2 className="w-10 h-10 text-blue-500 animate-spin mb-4" />
          <p className="text-slate-500 dark:text-slate-400">Carregando clientes...</p>
        </div>
      ) : customers.length === 0 ? (
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-12 shadow-sm border border-slate-200 dark:border-slate-700 flex flex-col items-center text-center">
          <div className="w-20 h-20 bg-blue-50 dark:bg-blue-900/20 rounded-full flex items-center justify-center mb-6">
            <Users className="w-10 h-10 text-blue-500 dark:text-blue-400" />
          </div>
          <h3 className="text-xl font-bold text-slate-900 dark:text-white mb-2">Nenhum cliente cadastrado</h3>
          <p className="text-slate-500 dark:text-slate-400 mb-6 max-w-md">
            Você ainda não tem clientes na sua base de dados. Comece adicionando o seu primeiro cliente agora mesmo.
          </p>
          <button
            onClick={openNewModal}
            className="text-blue-600 hover:text-blue-700 dark:text-blue-400 font-medium flex items-center gap-2"
          >
            <Plus className="w-5 h-5" /> Cadastrar meu primeiro cliente
          </button>
        </div>
      ) : (
        <>
          {/* Mobile Cards */}
          <div className="grid grid-cols-1 gap-4 md:hidden">
            {customers.map(customer => (
              <div key={customer.id} className="bg-white dark:bg-slate-800 p-5 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700">
                <div className="flex justify-between items-start mb-3">
                  <h3 className="font-bold text-slate-900 dark:text-white text-lg">{customer.name}</h3>
                  <div className="flex items-center gap-3">
                    <button onClick={() => handleEdit(customer)} className="text-blue-600 dark:text-blue-400" title="Editar">
                      <Edit2 className="w-4 h-4" />
                    </button>
                    <button onClick={() => handleDelete(customer.id)} className="text-red-600 dark:text-red-400" title="Excluir">
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
                <div className="space-y-2 text-slate-600 dark:text-slate-300">
                  <div className="flex items-center gap-3">
                    <Mail className="w-4 h-4 text-slate-400" />
                    <span className="truncate">{customer.email}</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <Phone className="w-4 h-4 text-slate-400" />
                    <span>{formatPhone(customer.phone || '')}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Desktop Table */}
          <div className="hidden md:block bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
            <table className="w-full text-left">
              <thead className="bg-slate-50 dark:bg-slate-900/50 border-b border-slate-200 dark:border-slate-700">
                <tr>
                  <th className="px-6 py-4 font-semibold text-slate-700 dark:text-slate-300">Nome do Cliente</th>
                  <th className="px-6 py-4 font-semibold text-slate-700 dark:text-slate-300">E-mail</th>
                  <th className="px-6 py-4 font-semibold text-slate-700 dark:text-slate-300">Telefone</th>
                  <th className="px-6 py-4 font-semibold text-slate-700 dark:text-slate-300 text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
                {customers.map(customer => (
                  <tr key={customer.id} className="hover:bg-slate-50 dark:hover:bg-slate-700/20 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-medium text-slate-900 dark:text-white">{customer.name}</div>
                    </td>
                    <td className="px-6 py-4 text-slate-600 dark:text-slate-300">
                      <div className="flex items-center gap-2">
                        <Mail className="w-4 h-4 text-slate-400" />
                        {customer.email}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-slate-600 dark:text-slate-300">
                      <div className="flex items-center gap-2">
                        <Phone className="w-4 h-4 text-slate-400" />
                        {formatPhone(customer.phone || '')}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <button onClick={() => handleEdit(customer)} className="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300 mr-4" title="Editar">
                        <Edit2 className="w-4 h-4" />
                      </button>
                      <button onClick={() => handleDelete(customer.id)} className="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300" title="Excluir">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm" onClick={() => !isSubmitting && setIsModalOpen(false)}></div>
          <div className="relative bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-md overflow-hidden animate-fade-in-up">
            <div className="flex items-center justify-between p-6 border-b border-slate-100 dark:border-slate-700">
              <h2 className="text-xl font-bold text-slate-900 dark:text-white flex items-center gap-2">
                <Users className="w-5 h-5 text-blue-500" />
                {editingId ? "Editar Cliente" : "Novo Cliente"}
              </h2>
              <button 
                onClick={() => setIsModalOpen(false)}
                disabled={isSubmitting}
                className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 transition-colors"
              >
                <X className="w-6 h-6" />
              </button>
            </div>
            
            <form onSubmit={handleSubmitCustomer} className="p-6 space-y-5">
              <div>
                <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Nome Completo</label>
                <input
                  name="name"
                  value={formData.name}
                  onChange={handleFormChange}
                  className="w-full px-4 py-3 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  placeholder="Ex: João da Silva"
                  required
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">E-mail</label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleFormChange}
                  className="w-full px-4 py-3 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  placeholder="joao@email.com"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Telefone / Celular</label>
                <input
                  name="phone"
                  value={formData.phone}
                  onChange={handleFormChange}
                  maxLength={15}
                  className="w-full px-4 py-3 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  placeholder="(00) 00000-0000"
                  required
                />
              </div>

              <div className="pt-4 flex gap-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  disabled={isSubmitting}
                  className="flex-1 px-4 py-3 text-slate-600 bg-slate-100 hover:bg-slate-200 dark:text-slate-300 dark:bg-slate-700 dark:hover:bg-slate-600 rounded-lg font-medium transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="flex-1 px-4 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors flex items-center justify-center gap-2"
                >
                  {isSubmitting ? (
                    <Loader2 className="w-5 h-5 animate-spin" />
                  ) : (
                    (editingId ? 'Salvar Alterações' : 'Salvar Cliente')
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}