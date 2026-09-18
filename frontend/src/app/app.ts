import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, Consulta, Endereco, Medico, Paciente } from './api.service';

const enderecoVazio = (): Endereco => ({ logradouro: '', numero: '', complemento: '', bairro: '', cidade: '', uf: '', cep: '' });

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule],
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  private readonly api = inject(ApiService);
  private readonly cdr = inject(ChangeDetectorRef);
  logado = !!this.api.token();
  carregando = false;
  readonly ambienteLocal = ['localhost', '127.0.0.1'].includes(window.location.hostname);
  buscandoCep = false;
  processando = false;
  mensagem = '';
  erro = '';
  aba: 'medicos' | 'pacientes' | 'consultas' = 'medicos';
  loginForm = { usuario: 'admin', senha: 'admin123' };
  medicos: Medico[] = [];
  pacientes: Paciente[] = [];
  consultas: Consulta[] = [];
  medicoId?: number;
  pacienteId?: number;
  mostrarFormulario = false;
  medicoForm = this.novoMedico();
  pacienteForm = this.novoPaciente();
  consultaForm: { pacienteId?: number; medicoId?: number; especialidade: string; dataHora: string } = { especialidade: '', dataHora: '' };
  especialidades = ['ORTOPEDIA', 'CARDIOLOGIA', 'GINECOLOGIA', 'DERMATOLOGIA'];
  private readonly cepsConsultados = new WeakMap<Endereco, string>();

  constructor() { if (this.logado) this.carregarTudo(); }

  entrar() {
    this.limparAvisos(); this.carregando = true;
    this.api.login(this.loginForm.usuario, this.loginForm.senha).subscribe({
      next: () => { this.logado = true; this.carregando = false; this.carregarTudo(); this.atualizarTela(); },
      error: (e) => {
        this.carregando = false;
        this.erro = e.status === 401
          ? 'Usuário ou senha inválidos.'
          : 'O sistema não respondeu. Execute a tarefa "MedFEMA: executar tudo" no VS Code e tente novamente.';
        this.atualizarTela();
      }
    });
  }

  sair() { this.api.logout(); this.logado = false; }
  selecionarAba(aba: typeof this.aba) { this.aba = aba; this.mostrarFormulario = false; this.limparAvisos(); }

  carregarTudo() {
    this.api.medicos().subscribe({ next: r => { this.medicos = r.content; this.atualizarTela(); }, error: e => this.tratarErro(e) });
    this.api.pacientes().subscribe({ next: r => { this.pacientes = r.content; this.atualizarTela(); }, error: e => this.tratarErro(e) });
    this.api.consultas().subscribe({ next: r => { this.consultas = r; this.atualizarTela(); }, error: e => this.tratarErro(e) });
  }

  abrirNovo() {
    this.medicoId = undefined; this.pacienteId = undefined;
    this.medicoForm = this.novoMedico(); this.pacienteForm = this.novoPaciente();
    this.mostrarFormulario = true; this.limparAvisos();
  }

  buscarCep(endereco: Endereco, avisarInvalido = true) {
    const cep = (endereco.cep || '').replace(/\D/g, '');
    if (!cep) return;
    if (cep.length !== 8) {
      if (avisarInvalido) this.erro = 'O CEP deve conter 8 números.';
      return;
    }
    if (this.cepsConsultados.get(endereco) === cep) return;
    this.cepsConsultados.set(endereco, cep);
    this.buscandoCep = true;
    this.limparAvisos();
    this.api.buscarCep(cep).subscribe({
      next: dados => {
        endereco.cep = dados.cep;
        endereco.logradouro = dados.logradouro;
        endereco.bairro = dados.bairro;
        endereco.cidade = dados.cidade;
        endereco.uf = dados.uf;
        if (dados.complemento) endereco.complemento = dados.complemento;
        this.buscandoCep = false;
        this.atualizarTela();
      },
      error: e => {
        this.cepsConsultados.delete(endereco);
        this.buscandoCep = false;
        this.erro = e.error?.mensagem || 'Não foi possível consultar o CEP.';
        this.atualizarTela();
      }
    });
  }

  salvarMedico() {
    if (this.processando || this.buscandoCep) return;
    this.limparAvisos();
    this.processando = true;
    const dados = this.medicoId ? { nome: this.medicoForm.nome, telefone: this.medicoForm.telefone, endereco: this.medicoForm.endereco } : this.medicoForm;
    this.api.salvarMedico(dados, this.medicoId).subscribe({
      next: salvo => {
        this.medicos = this.medicoId
          ? this.medicos.map(m => m.id === salvo.id ? salvo : m)
          : [...this.medicos, salvo].sort((a, b) => a.nome.localeCompare(b.nome));
        this.processando = false; this.sucesso('Médico salvo com sucesso.'); this.mostrarFormulario = false;
        this.atualizarTela();
      },
      error: e => { this.processando = false; this.tratarErro(e); }
    });
  }

  editarMedico(m: Medico) {
    this.medicoId = m.id; this.medicoForm = { ...m, endereco: { ...m.endereco } };
    this.mostrarFormulario = true; this.limparAvisos();
  }

  excluirMedico(id: number) {
    if (!confirm('Deseja desativar este médico?')) return;
    this.api.excluirMedico(id).subscribe({ next: () => { this.medicos = this.medicos.filter(m => m.id !== id); this.sucesso('Médico desativado.'); this.atualizarTela(); }, error: e => this.tratarErro(e) });
  }

  salvarPaciente() {
    if (this.processando || this.buscandoCep) return;
    this.limparAvisos();
    this.processando = true;
    const dados = this.pacienteId ? { nome: this.pacienteForm.nome, telefone: this.pacienteForm.telefone, endereco: this.pacienteForm.endereco } : this.pacienteForm;
    this.api.salvarPaciente(dados, this.pacienteId).subscribe({
      next: salvo => {
        this.pacientes = this.pacienteId
          ? this.pacientes.map(p => p.id === salvo.id ? salvo : p)
          : [...this.pacientes, salvo].sort((a, b) => a.nome.localeCompare(b.nome));
        this.processando = false; this.sucesso('Paciente salvo com sucesso.'); this.mostrarFormulario = false;
        this.atualizarTela();
      },
      error: e => { this.processando = false; this.tratarErro(e); }
    });
  }

  editarPaciente(p: Paciente) {
    this.pacienteId = p.id; this.pacienteForm = { ...p, endereco: { ...p.endereco } };
    this.mostrarFormulario = true; this.limparAvisos();
  }

  excluirPaciente(id: number) {
    if (!confirm('Deseja desativar este paciente?')) return;
    this.api.excluirPaciente(id).subscribe({ next: () => { this.pacientes = this.pacientes.filter(p => p.id !== id); this.sucesso('Paciente desativado.'); this.atualizarTela(); }, error: e => this.tratarErro(e) });
  }

  agendar() {
    if (this.processando) return;
    this.processando = true;
    this.limparAvisos();
    const dados = { pacienteId: Number(this.consultaForm.pacienteId), medicoId: this.consultaForm.medicoId ? Number(this.consultaForm.medicoId) : null,
      especialidade: this.consultaForm.especialidade || null, dataHora: this.consultaForm.dataHora };
    this.api.agendar(dados).subscribe({
      next: consulta => { this.consultas = [...this.consultas, consulta].sort((a, b) => a.dataHora.localeCompare(b.dataHora)); this.processando = false; this.sucesso('Consulta agendada.'); this.consultaForm = { especialidade: '', dataHora: '' }; this.atualizarTela(); },
      error: e => { this.processando = false; this.tratarErro(e); }
    });
  }

  cancelar(c: Consulta) {
    const motivo = prompt('Motivo: PACIENTE_DESISTIU, MEDICO_CANCELOU ou OUTRO', 'PACIENTE_DESISTIU');
    if (!motivo) return;
    this.api.cancelar(c.id, motivo).subscribe({ next: atualizada => { this.consultas = this.consultas.map(item => item.id === atualizada.id ? atualizada : item); this.sucesso('Consulta cancelada.'); this.atualizarTela(); }, error: e => this.tratarErro(e) });
  }

  private novoMedico(): Omit<Medico, 'id' | 'ativo'> { return { nome: '', email: '', telefone: '', crm: '', especialidade: '', endereco: enderecoVazio() }; }
  private novoPaciente(): Omit<Paciente, 'id' | 'ativo'> { return { nome: '', email: '', telefone: '', cpf: '', endereco: enderecoVazio() }; }
  private sucesso(texto: string) { this.mensagem = texto; this.erro = ''; }
  private limparAvisos() { this.mensagem = ''; this.erro = ''; }
  private tratarErro(e: any) { if (e.status === 401) this.sair(); this.erro = e.error?.mensagem || e.error?.message || 'Não foi possível concluir a operação.'; this.mensagem = ''; this.atualizarTela(); }
  private atualizarTela() { this.cdr.detectChanges(); }
}
