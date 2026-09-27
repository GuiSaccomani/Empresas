import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getCompanyInfo, getAvailableSlots, createPublicAppointment } from '../services/booking';
import type { PublicCompanyInfo } from '../services/booking';
import { Calendar, Clock, User, CheckCircle2, ChevronRight, Loader2, ArrowLeft } from 'lucide-react';

export function PublicBooking() {
  const { slug } = useParams<{ slug: string }>();
  
  const [step, setStep] = useState(1);
  const [company, setCompany] = useState<PublicCompanyInfo | null>(null);
  const [loading, setLoading] = useState(true);
  
  // Selections
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [selectedTime, setSelectedTime] = useState<string | null>(null);
  
  // Available slots
  const [slots, setSlots] = useState<string[]>([]);
  const [loadingSlots, setLoadingSlots] = useState(false);
  
  // Form data
  const [formData, setFormData] = useState({ name: '', email: '', phone: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [acceptedTerms, setAcceptedTerms] = useState(false);
  
  // Completion
  const [isSuccess, setIsSuccess] = useState(false);

  // Generate next 14 days
  const nextDays = Array.from({ length: 14 }).map((_, i) => {
    const d = new Date();
    d.setDate(d.getDate() + i);
    return d;
  });

  useEffect(() => {
    if (slug) {
      getCompanyInfo(slug)
        .then(setCompany)
        .catch(err => {
          console.error("Empresa não encontrada", err);
          // O ideal seria redirecionar para uma página de 404, mas para simplificar:
        })
        .finally(() => setLoading(false));
    }
  }, [slug]);

  useEffect(() => {
    if (slug && selectedDate) {
      setLoadingSlots(true);
      const formattedDate = selectedDate.toISOString().split('T')[0];
      getAvailableSlots(slug, formattedDate)
        .then(setSlots)
        .catch(console.error)
        .finally(() => setLoadingSlots(false));
    }
  }, [slug, selectedDate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!slug || !selectedDate || !selectedTime) return;
    
    setIsSubmitting(true);
    try {
      const scheduledTime = `${selectedDate.toISOString().split('T')[0]}T${selectedTime}`;
      await createPublicAppointment(slug, {
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        scheduledTime
      });
      setIsSuccess(true);
        } catch (error: any) {
      console.error(error);
      if (error.response?.status === 409) {
          alert('Esse horário acabou de ser reservado por outra pessoa, escolha outro.');
          setStep(2);
          const formattedDate = selectedDate.toISOString().split('T')[0];
          setLoadingSlots(true);
          getAvailableSlots(slug, formattedDate)
            .then(setSlots)
            .finally(() => setLoadingSlots(false));
      } else {
          alert('Infelizmente, ocorreu um erro. Por favor, tente novamente.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleNextStep = () => {
    if (step === 1 && selectedDate) setStep(2);
    else if (step === 2 && selectedTime) setStep(3);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
      </div>
    );
  }

  if (!company && !loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="text-center bg-white p-8 rounded-2xl shadow-sm border border-slate-200">
          <h2 className="text-2xl font-bold text-slate-800 mb-2">Empresa não encontrada</h2>
          <p className="text-slate-500">O link que você acessou pode estar incorreto ou expirado.</p>
        </div>
      </div>
    );
  }

  if (isSuccess) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4 sm:p-6">
        <div className="bg-white w-full max-w-md rounded-2xl shadow-xl border border-slate-100 p-8 sm:p-12 text-center animate-fade-in-up">
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6 scale-in-center">
            <CheckCircle2 className="w-10 h-10 text-green-600" />
          </div>
          <h2 className="text-2xl font-bold text-slate-900 mb-3">Tudo certo, {formData.name.split(' ')[0]}!</h2>
          <p className="text-slate-500 mb-8">
            Seu agendamento na <strong className="text-slate-700">{company?.name}</strong> foi confirmado para o dia <strong className="text-slate-700">{selectedDate.toLocaleDateString('pt-BR')}</strong> às <strong className="text-slate-700">{selectedTime?.substring(0,5)}</strong>.
          </p>
          <button 
            onClick={() => window.location.reload()}
            className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-medium transition-colors"
          >
            Fazer novo agendamento
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center py-8 px-4 sm:py-12">
      <div className="w-full max-w-[500px] mb-6 flex justify-center items-center">
        <div className="text-center">
          <h1 className="text-2xl sm:text-3xl font-bold text-slate-900 tracking-tight">{company?.name}</h1>
          <p className="text-slate-500 mt-1">Agendamento Online</p>
        </div>
      </div>

      <div className="bg-white w-full max-w-[500px] rounded-3xl shadow-xl shadow-slate-200/50 border border-slate-100 overflow-hidden relative min-h-[500px] flex flex-col">
        
        {/* Progress Bar */}
        <div className="bg-slate-50 px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <div className="flex items-center gap-2">
            {step > 1 && (
              <button 
                onClick={() => setStep(step - 1)}
                className="p-1.5 hover:bg-slate-200 rounded-full text-slate-500 transition-colors mr-1"
              >
                <ArrowLeft className="w-4 h-4" />
              </button>
            )}
            <span className="text-sm font-semibold text-blue-600">
              Passo {step} <span className="text-slate-400 font-normal">de 3</span>
            </span>
          </div>
          <div className="flex gap-1.5">
            {[1, 2, 3].map(i => (
              <div 
                key={i} 
                className={`h-1.5 rounded-full transition-all duration-300 ${
                  i === step ? 'w-6 bg-blue-600' : i < step ? 'w-2 bg-blue-300' : 'w-2 bg-slate-200'
                }`}
              />
            ))}
          </div>
        </div>

        <div className="p-6 sm:p-8 flex-1 flex flex-col">
          {/* STEP 1: DATE */}
          {step === 1 && (
            <div className="animate-fade-in flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-blue-50 p-2.5 rounded-xl text-blue-600">
                  <Calendar className="w-6 h-6" />
                </div>
                <h2 className="text-xl font-bold text-slate-800">Escolha o dia</h2>
              </div>

              <div className="flex-1">
                <div className="flex overflow-x-auto pb-4 pt-2 -mx-2 px-2 snap-x scrollbar-hide gap-3">
                  {nextDays.map((date, idx) => {
                    const isSelected = selectedDate.getDate() === date.getDate() && selectedDate.getMonth() === date.getMonth();
                    const dayName = new Intl.DateTimeFormat('pt-BR', { weekday: 'short' }).format(date).replace('.', '');
                    const dayNum = date.getDate();
                    
                    return (
                      <button
                        key={idx}
                        onClick={() => { setSelectedDate(date); setSelectedTime(null); }}
                        className={`snap-center shrink-0 flex flex-col items-center justify-center w-[72px] h-[90px] rounded-2xl border-2 transition-all duration-200 ${
                          isSelected 
                            ? 'border-blue-600 bg-blue-50/50 shadow-sm' 
                            : 'border-slate-100 hover:border-blue-200 bg-white'
                        }`}
                      >
                        <span className={`text-xs font-medium uppercase tracking-wider mb-1 ${isSelected ? 'text-blue-600' : 'text-slate-400'}`}>
                          {dayName}
                        </span>
                        <span className={`text-2xl font-bold ${isSelected ? 'text-blue-700' : 'text-slate-700'}`}>
                          {dayNum}
                        </span>
                      </button>
                    );
                  })}
                </div>
              </div>

              <button 
                onClick={handleNextStep}
                className="mt-8 w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-medium transition-colors flex items-center justify-center gap-2"
              >
                Continuar <ChevronRight className="w-5 h-5" />
              </button>
            </div>
          )}

          {/* STEP 2: TIME */}
          {step === 2 && (
            <div className="animate-fade-in flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-blue-50 p-2.5 rounded-xl text-blue-600">
                  <Clock className="w-6 h-6" />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-slate-800">Escolha o horário</h2>
                  <p className="text-sm text-slate-500">{selectedDate.toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long' })}</p>
                </div>
              </div>

              <div className="flex-1">
                {loadingSlots ? (
                  <div className="flex justify-center py-12">
                    <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
                  </div>
                ) : slots.length === 0 ? (
                  <div className="text-center py-12 bg-slate-50 rounded-2xl border border-slate-100 border-dashed">
                    <p className="text-slate-500 font-medium">Nenhum horário disponível para este dia.</p>
                  </div>
                ) : (
                  <div className="grid grid-cols-3 gap-3">
                    {slots.map((slot, idx) => {
                      const timeString = slot.substring(11, 16); // Extracts HH:mm from 2026-09-27T08:00:00
                      const isSelected = selectedTime === timeString + ':00';
                      
                      return (
                        <button
                          key={idx}
                          onClick={() => setSelectedTime(timeString + ':00')}
                          className={`py-3.5 rounded-xl text-sm font-semibold transition-all duration-200 border-2 ${
                            isSelected
                              ? 'bg-blue-600 border-blue-600 text-white shadow-md'
                              : 'bg-white border-slate-100 text-slate-700 hover:border-blue-200 hover:bg-blue-50/30'
                          }`}
                        >
                          {timeString}
                        </button>
                      );
                    })}
                  </div>
                )}
              </div>

              <button 
                onClick={handleNextStep}
                disabled={!selectedTime}
                className="mt-8 w-full py-3.5 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-200 disabled:text-slate-400 text-white rounded-xl font-medium transition-colors flex items-center justify-center gap-2"
              >
                Continuar <ChevronRight className="w-5 h-5" />
              </button>
            </div>
          )}

          {/* STEP 3: DETAILS */}
          {step === 3 && (
            <div className="animate-fade-in flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-blue-50 p-2.5 rounded-xl text-blue-600">
                  <User className="w-6 h-6" />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-slate-800">Quase lá!</h2>
                  <p className="text-sm text-slate-500">Informe seus dados para confirmar.</p>
                </div>
              </div>

              <form id="booking-form" onSubmit={handleSubmit} className="flex-1 space-y-4">
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1.5 ml-1">Nome Completo</label>
                  <input
                    required
                    type="text"
                    value={formData.name}
                    onChange={e => setFormData({...formData, name: e.target.value})}
                    placeholder="Como prefere ser chamado?"
                    className="w-full px-4 py-3.5 rounded-xl border-2 border-slate-200 focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition-all text-slate-800 placeholder:text-slate-400 bg-slate-50 focus:bg-white"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1.5 ml-1">E-mail</label>
                  <input
                    required
                    type="email"
                    value={formData.email}
                    onChange={e => setFormData({...formData, email: e.target.value})}
                    placeholder="seu.email@exemplo.com"
                    className="w-full px-4 py-3.5 rounded-xl border-2 border-slate-200 focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition-all text-slate-800 placeholder:text-slate-400 bg-slate-50 focus:bg-white"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1.5 ml-1">WhatsApp / Telefone</label>
                  <input
                    required
                    type="tel"
                    value={formData.phone}
                    onChange={e => setFormData({...formData, phone: e.target.value})}
                    placeholder="(00) 00000-0000"
                    className="w-full px-4 py-3.5 rounded-xl border-2 border-slate-200 focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition-all text-slate-800 placeholder:text-slate-400 bg-slate-50 focus:bg-white"
                  />
                </div>
                <div className="flex items-start gap-3 pt-2">
                  <input
                    type="checkbox"
                    id="terms"
                    checked={acceptedTerms}
                    onChange={(e) => setAcceptedTerms(e.target.checked)}
                    className="mt-1 w-4 h-4 text-blue-600 bg-slate-100 border-slate-300 rounded focus:ring-blue-500"
                  />
                  <label htmlFor="terms" className="text-sm text-slate-600 leading-relaxed cursor-pointer">
                    Concordo com o uso dos meus dados para fins de agendamento e contato desta clínica.
                  </label>
                </div>
              </form>

              <div className="mt-8 pt-6 border-t border-slate-100 flex items-center justify-between">
                <div className="text-sm text-slate-500">
                  <span className="block">Data escolhida:</span>
                  <strong className="text-slate-800">{selectedDate.toLocaleDateString('pt-BR')} às {selectedTime?.substring(0,5)}</strong>
                </div>
                <button 
                  type="submit"
                  form="booking-form"
                  disabled={isSubmitting || !acceptedTerms}
                  className="py-3.5 px-8 bg-blue-600 hover:bg-blue-700 disabled:opacity-70 text-white rounded-xl font-medium transition-colors flex items-center gap-2 shadow-lg shadow-blue-600/20"
                >
                  {isSubmitting ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Confirmar'}
                </button>
              </div>
            </div>
          )}

        </div>
      </div>
    </div>
  );
}