import React, { useState, useEffect } from 'react';
import { Calendar, Clock, ChevronLeft, ChevronRight, Plus, X, Loader2, CheckCircle , Bell, Trash2} from 'lucide-react';
import { getAppointmentsByRange, createAppointment, updateAppointmentStatus, deleteAppointment, type Appointment , } from '../services/appointments';
import { getCustomers, type Customer } from '../services/customers';

// Utility to get current week's Monday
const getStartOfWeek = (date: Date) => {
  const d = new Date(date);
  const day = d.getDay();
  const diff = d.getDate() - day + (day === 0 ? -6 : 1);
  d.setDate(diff);
  d.setHours(0, 0, 0, 0);
  return d;
};

const DAYS_IN_WEEK = 7;
const HOURS = Array.from({ length: 11 }, (_, i) => i + 8); // 08:00 to 18:00

export function Agenda() {
  const [currentWeekStart, setCurrentWeekStart] = useState<Date>(getStartOfWeek(new Date()));
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  
  // Modals
  const [isNewModalOpen, setIsNewModalOpen] = useState(false);
  const [isCompleteModalOpen, setIsCompleteModalOpen] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState<Appointment | null>(null);
  
  // Forms
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [toastMsg, setToastMsg] = useState('');

  const handleSendReminder = async (e: React.MouseEvent, id: string) => {
    e.stopPropagation();
    try {
      await (id);
      setToastMsg('Lembrete enviado!');
      setTimeout(() => setToastMsg(''), 3000);
    } catch (err) {
      alert('Erro ao enviar lembrete.');
    }
  };
  const [newFormData, setNewFormData] = useState({
    customerId: '',
    date: '',
    time: '09:00',
    notes: ''
  });
  const [completeAmount, setCompleteAmount] = useState('');

  const loadData = async () => {
    setIsLoading(true);
    try {
      const endOfWeek = new Date(currentWeekStart);
      endOfWeek.setDate(currentWeekStart.getDate() + 6);
      endOfWeek.setHours(23, 59, 59, 999);

      // We use simple ISO string, but for precision, it's good to keep track of local vs UTC.
      const [apptsData, custsData] = await Promise.all([
        getAppointmentsByRange(currentWeekStart.toISOString(), endOfWeek.toISOString()),
        getCustomers()
      ]);
      setAppointments(apptsData);
      setCustomers(custsData.content || custsData || []);
    } catch (error) {
      console.error('Error loading agenda data', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [currentWeekStart]);

  const prevWeek = () => {
    const d = new Date(currentWeekStart);
    d.setDate(d.getDate() - 7);
    setCurrentWeekStart(d);
  };

  const nextWeek = () => {
    const d = new Date(currentWeekStart);
    d.setDate(d.getDate() + 7);
    setCurrentWeekStart(d);
  };

  const handleCreateAppointment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newFormData.customerId || !newFormData.date || !newFormData.time) return;
    
    setIsSubmitting(true);
    try {
      const scheduledTime = new Date(`${newFormData.date}T${newFormData.time}:00`).toISOString();
      await createAppointment({
        customerId: newFormData.customerId,
        scheduledTime,
        notes: newFormData.notes
      });
      setIsNewModalOpen(false);
      setNewFormData({ customerId: '', date: '', time: '09:00', notes: '' });
      loadData();
    } catch (error) {
      console.error('Error creating appointment', error);
      alert('Erro ao criar agendamento.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDeleteAppointment = async () => {
    if (!selectedAppointment) return;
    if (window.confirm('Tem certeza que deseja excluir definitivamente este agendamento?')) {
      setIsSubmitting(true);
      try {
        await deleteAppointment(selectedAppointment.id);
        setIsCompleteModalOpen(false);
        setSelectedAppointment(null);
        setToastMsg('Agendamento excluído!');
        setTimeout(() => setToastMsg(''), 3000);
        loadData();
      } catch (error) {
        alert('Erro ao excluir agendamento.');
      } finally {
        setIsSubmitting(false);
      }
    }
  };

  const handleCompleteAppointment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAppointment) return;
    
    setIsSubmitting(true);
    try {
      await updateAppointmentStatus(selectedAppointment.id, 'COMPLETED', parseFloat(completeAmount));
      setIsCompleteModalOpen(false);
      setSelectedAppointment(null);
      setCompleteAmount('');
      loadData();
    } catch (error) {
      console.error('Error completing appointment', error);
      alert('Erro ao concluir agendamento.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'SCHEDULED': return 'bg-blue-100 text-blue-700 border-blue-200 dark:bg-blue-900/30 dark:text-blue-400 dark:border-blue-800';
      case 'CONFIRMED': return 'bg-green-100 text-green-700 border-green-200 dark:bg-green-900/30 dark:text-green-400 dark:border-green-800';
      case 'CANCELLED': return 'bg-red-100 text-red-700 border-red-200 dark:bg-red-900/30 dark:text-red-400 dark:border-red-800';
      case 'COMPLETED': return 'bg-slate-100 text-slate-700 border-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:border-slate-700';
      default: return 'bg-slate-100 text-slate-700 border-slate-200';
    }
  };

  const weekDays = Array.from({ length: DAYS_IN_WEEK }, (_, i) => {
    const d = new Date(currentWeekStart);
    d.setDate(d.getDate() + i);
    return d;
  });

  return (
    <div className="space-y-6 animate-fade-in relative">
      {toastMsg && (
        <div className="fixed bottom-4 right-4 bg-green-500 text-white px-4 py-2 rounded shadow-lg z-50 animate-fade-in-up">
          {toastMsg}
        </div>
      )}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white">Agenda</h1>
          <p className="text-slate-500 dark:text-slate-400">Gerencie seus compromissos e atendimentos.</p>
        </div>
        <button
          onClick={() => {
            setNewFormData({ customerId: '', date: '', time: '09:00', notes: '' });
            setIsNewModalOpen(true);
          }}
          className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2.5 rounded-lg font-medium transition-colors flex items-center justify-center gap-2 shadow-sm"
        >
          <Plus className="w-5 h-5" />
          Novo Agendamento
        </button>
      </div>

      <div className="flex items-center justify-between bg-white dark:bg-slate-800 p-4 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700">
        <button onClick={prevWeek} className="p-2 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-lg text-slate-600 dark:text-slate-300 transition-colors">
          <ChevronLeft className="w-5 h-5" />
        </button>
        <span className="font-semibold text-slate-900 dark:text-white">
          Semana de {currentWeekStart.toLocaleDateString('pt-BR')} a {weekDays[6].toLocaleDateString('pt-BR')}
        </span>
        <button onClick={nextWeek} className="p-2 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-lg text-slate-600 dark:text-slate-300 transition-colors">
          <ChevronRight className="w-5 h-5" />
        </button>
      </div>

      <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-x-auto">
        <div className="min-w-[800px]">
          <div className="grid grid-cols-8 border-b border-slate-200 dark:border-slate-700">
            <div className="p-4 flex items-center justify-center border-r border-slate-200 dark:border-slate-700 text-slate-400">
              <Clock className="w-5 h-5" />
            </div>
            {weekDays.map((day, i) => (
              <div key={i} className="p-4 text-center border-r border-slate-200 dark:border-slate-700 last:border-0">
                <div className="text-xs uppercase font-bold text-slate-500 dark:text-slate-400">
                  {day.toLocaleDateString('pt-BR', { weekday: 'short' })}
                </div>
                <div className={`text-lg font-bold mt-1 ${day.toDateString() === new Date().toDateString() ? 'text-blue-600 dark:text-blue-400' : 'text-slate-900 dark:text-white'}`}>
                  {day.getDate()}
                </div>
              </div>
            ))}
          </div>

          {isLoading ? (
            <div className="flex justify-center p-12">
              <Loader2 className="w-8 h-8 animate-spin text-blue-500" />
            </div>
          ) : (
            HOURS.map(hour => (
              <div key={hour} className="grid grid-cols-8 border-b border-slate-100 dark:border-slate-700/50 last:border-0">
                <div className="p-3 border-r border-slate-200 dark:border-slate-700 text-sm font-medium text-slate-500 dark:text-slate-400 text-center flex flex-col justify-center">
                  {hour.toString().padStart(2, '0')}:00
                </div>
                {weekDays.map((day, i) => {
                  const cellAppointments = appointments.filter(a => {
                    const ad = new Date(a.scheduledTime);
                    return ad.getDate() === day.getDate() && ad.getMonth() === day.getMonth() && ad.getHours() === hour;
                  });

                  return (
                    <div 
                      key={i} 
                      className="p-2 border-r border-slate-200 dark:border-slate-700 last:border-0 min-h-[100px] relative group cursor-pointer"
                      onClick={() => {
                        const y = day.getFullYear();
                        const m = String(day.getMonth() + 1).padStart(2, '0');
                        const d = String(day.getDate()).padStart(2, '0');
                        setNewFormData({
                          customerId: '',
                          date: `${y}-${m}-${d}`,
                          time: `${hour.toString().padStart(2, '0')}:00`,
                          notes: ''
                        });
                        setIsNewModalOpen(true);
                      }}
                    >
                      {cellAppointments.map(app => (
                        <div 
                          key={app.id} 
                          onClick={(e) => {
                            e.stopPropagation();
                            if (app.status !== 'COMPLETED' && app.status !== 'CANCELLED') {
                              setSelectedAppointment(app);
                              setIsCompleteModalOpen(true);
                            }
                          }}
                          className={`mb-2 p-2 rounded-md border text-xs ${app.status !== 'COMPLETED' && app.status !== 'CANCELLED' ? 'cursor-pointer hover:shadow-md transition-shadow' : ''} ${getStatusColor(app.status)}`}
                        >
                          <div className="font-bold truncate" title={app.customerName}>{app.customerName}</div>
                          <div className="opacity-80 flex items-center justify-between mt-1">
                            <span>{new Date(app.scheduledTime).toLocaleTimeString('pt-BR', {hour: '2-digit', minute:'2-digit'})}</span>
                            <div className="flex items-center gap-2">
                              {app.status !== 'COMPLETED' && app.status !== 'CANCELLED' && (
                                <button 
                                  onClick={(e) => handleSendReminder(e, app.id)}
                                  className="text-blue-600 dark:text-blue-400 hover:text-blue-800 dark:hover:text-blue-300 transition-colors"
                                  title="Enviar lembrete"
                                >
                                  <Bell className="w-3 h-3" />
                                </button>
                              )}
                              <span>{app.status === 'COMPLETED' ? 'Concluído' : 'Agendado'}</span>
                            </div>
                          </div>
                        </div>
                      ))}
                      
                      <div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity bg-blue-500/5 dark:bg-blue-400/5 pointer-events-none rounded flex items-center justify-center">
                        <Plus className="w-4 h-4 text-blue-500/30" />
                      </div>
                    </div>
                  );
                })}
              </div>
            ))
          )}
        </div>
      </div>

      {isNewModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm" onClick={() => !isSubmitting && setIsNewModalOpen(false)}></div>
          <div className="relative bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-md overflow-hidden animate-fade-in-up">
            <div className="flex items-center justify-between p-6 border-b border-slate-100 dark:border-slate-700">
              <h2 className="text-xl font-bold text-slate-900 dark:text-white flex items-center gap-2">
                <Calendar className="w-5 h-5 text-blue-500" />
                Novo Agendamento
              </h2>
              <button onClick={() => setIsNewModalOpen(false)} disabled={isSubmitting} className="text-slate-400 hover:text-slate-600 transition-colors">
                <X className="w-6 h-6" />
              </button>
            </div>
            
            <form onSubmit={handleCreateAppointment} className="p-6 space-y-5">
              <div>
                <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Cliente</label>
                <select
                  required
                  value={newFormData.customerId}
                  onChange={(e) => setNewFormData({...newFormData, customerId: e.target.value})}
                  className="w-full px-4 py-3 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                >
                  <option value="">Selecione um cliente...</option>
                  {customers.map(c => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Data</label>
                  <input
                    type="date"
                    required
                    value={newFormData.date}
                    onChange={(e) => setNewFormData({...newFormData, date: e.target.value})}
                    className="w-full px-4 py-3 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Horário</label>
                  <select
                    required
                    value={newFormData.time}
                    onChange={(e) => setNewFormData({...newFormData, time: e.target.value})}
                    className="w-full px-4 py-3 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  >
                    {HOURS.map(h => {
                      const time = `${h.toString().padStart(2, '0')}:00`;
                      return <option key={time} value={time}>{time}</option>;
                    })}
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Observações</label>
                <textarea
                  value={newFormData.notes}
                  onChange={(e) => setNewFormData({...newFormData, notes: e.target.value})}
                  className="w-full px-4 py-3 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  placeholder="Opcional..."
                  rows={2}
                />
              </div>

              <div className="pt-4 flex gap-3">
                <button type="button" onClick={() => setIsNewModalOpen(false)} disabled={isSubmitting} className="flex-1 px-4 py-3 text-slate-600 bg-slate-100 hover:bg-slate-200 dark:text-slate-300 dark:bg-slate-700 dark:hover:bg-slate-600 rounded-lg font-medium transition-colors">
                  Cancelar
                </button>
                <button type="submit" disabled={isSubmitting} className="flex-1 px-4 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors flex items-center justify-center gap-2">
                  {isSubmitting ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Agendar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isCompleteModalOpen && selectedAppointment && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm" onClick={() => !isSubmitting && setIsCompleteModalOpen(false)}></div>
          <div className="relative bg-white dark:bg-slate-800 rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden animate-fade-in-up">
            <div className="flex flex-col items-center p-8 text-center">
              <div className="w-16 h-16 bg-green-100 dark:bg-green-900/30 text-green-500 rounded-full flex items-center justify-center mb-4">
                <CheckCircle className="w-8 h-8" />
              </div>
              <h2 className="text-xl font-bold text-slate-900 dark:text-white mb-2">Concluir Atendimento</h2>
              <p className="text-slate-500 dark:text-slate-400 text-sm mb-6">
                Para <strong>{selectedAppointment.customerName}</strong>.<br/>Isso gerará um registro financeiro.
              </p>
              
              <form onSubmit={handleCompleteAppointment} className="w-full space-y-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1 text-left">Valor Cobrado (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={completeAmount}
                    onChange={(e) => setCompleteAmount(e.target.value)}
                    className="w-full px-4 py-3 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                    placeholder="0.00"
                  />
                </div>

                <div className="pt-2 flex gap-3">
                  <button type="button" onClick={handleDeleteAppointment} disabled={isSubmitting} className="px-4 py-3 text-red-600 bg-red-50 hover:bg-red-100 dark:bg-red-900/20 dark:hover:bg-red-900/40 rounded-lg transition-colors flex items-center justify-center" title="Excluir agendamento">
                    <Trash2 className="w-5 h-5" />
                  </button>
                  <button type="button" onClick={() => setIsCompleteModalOpen(false)} disabled={isSubmitting} className="flex-1 px-4 py-3 text-slate-600 bg-slate-100 hover:bg-slate-200 dark:text-slate-300 dark:bg-slate-700 dark:hover:bg-slate-600 rounded-lg font-medium transition-colors">
                    Cancelar
                  </button>
                  <button type="submit" disabled={isSubmitting} className="flex-1 px-4 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium transition-colors flex items-center justify-center gap-2">
                    {isSubmitting ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Confirmar'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}