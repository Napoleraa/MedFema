import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap, timeout } from 'rxjs';

declare global {
  interface Window { MEDFEMA_CONFIG?: { apiUrl?: string }; }
}

export interface Endereco { logradouro: string; numero?: string; complemento?: string; bairro: string; cidade: string; uf: string; cep: string; }
export interface Medico { id: number; nome: string; email: string; telefone: string; crm: string; especialidade: string; endereco: Endereco; ativo: boolean; }
export interface Paciente { id: number; nome: string; email: string; telefone: string; cpf: string; endereco: Endereco; ativo: boolean; }
export interface Consulta { id: number; medicoId: number; medico: string; pacienteId: number; paciente: string; dataHora: string; cancelada: boolean; motivoCancelamento?: string; }
export interface EnderecoCep { cep: string; logradouro: string; complemento: string; bairro: string; cidade: string; uf: string; }
interface Pagina<T> { content: T[]; totalElements: number; totalPages: number; number: number; }

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = (window.MEDFEMA_CONFIG?.apiUrl || '/api').replace(/\/$/, '');

  login(usuario: string, senha: string) {
    return this.http.post<{token: string}>(`${this.base}/auth/login`, { usuario, senha }).pipe(
      timeout(8000),
      tap(r => localStorage.setItem('medfema_token', r.token))
    );
  }
  token() { return localStorage.getItem('medfema_token'); }
  logout() { localStorage.removeItem('medfema_token'); }
  buscarCep(cep: string) { return this.http.get<EnderecoCep>(`${this.base}/cep/${encodeURIComponent(cep)}`).pipe(timeout(8000)); }
  medicos(): Observable<Pagina<Medico>> { return this.http.get<Pagina<Medico>>(`${this.base}/medicos`); }
  salvarMedico(dados: unknown, id?: number) { return id ? this.http.put<Medico>(`${this.base}/medicos/${id}`, dados) : this.http.post<Medico>(`${this.base}/medicos`, dados); }
  excluirMedico(id: number) { return this.http.delete<void>(`${this.base}/medicos/${id}`); }
  pacientes(): Observable<Pagina<Paciente>> { return this.http.get<Pagina<Paciente>>(`${this.base}/pacientes`); }
  salvarPaciente(dados: unknown, id?: number) { return id ? this.http.put<Paciente>(`${this.base}/pacientes/${id}`, dados) : this.http.post<Paciente>(`${this.base}/pacientes`, dados); }
  excluirPaciente(id: number) { return this.http.delete<void>(`${this.base}/pacientes/${id}`); }
  consultas(): Observable<Consulta[]> { return this.http.get<Consulta[]>(`${this.base}/consultas`); }
  agendar(dados: unknown) { return this.http.post<Consulta>(`${this.base}/consultas`, dados); }
  cancelar(id: number, motivo: string) { return this.http.delete<Consulta>(`${this.base}/consultas/${id}`, { body: { motivo } }); }
}
