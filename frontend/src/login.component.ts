import { Component, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common'; 

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule],
  template: `
    <h1>Accedi</h1>

    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="card">
      <label>
        Username
        <input formControlName="username" placeholder="es. mario.rossi" />
      </label>

      <label>
        Password
        <input type="password" formControlName="password" placeholder="••••••••" />
      </label>

      <button type="submit" [disabled]="form.invalid">Registrati</button>

      <p *ngIf="error()" class="error">{{ error() }}</p>
    </form>
  `,
  styles: [`
    .card { display:grid; gap:12px; max-width:360px; padding:16px; border:1px solid #ddd; border-radius:12px; }
    label { display:grid; gap:6px; font-weight:600; }
    input { padding:10px; border:1px solid #ccc; border-radius:8px; }
    button { padding:10px 14px; border:0; border-radius:10px; cursor:pointer; }
    .error { color: #b00020; }
    h1 { margin-bottom: 12px; }
  `]
})
export class LoginComponent {
  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });
  error = signal('');

  constructor(
    private http: HttpClient,
    private fb: FormBuilder,
    private router: Router,           
  ) {}

  onSubmit() {
    this.error.set('');
    const { username, password } = this.form.value;

    this.http.post('/api/auth/register', { username, password }).subscribe({
      next: () => {
        const creds = btoa(`${username}:${password}`);
        sessionStorage.setItem('basic', creds); 
        // Salvataggio OK → vai alla pagina preferenze
        this.router.navigate(['/preferenze'], { state: { username }});
        localStorage.setItem('username', username!); 
      },
      error: (err) => {
        // 409 dal backend → utente già esistente
        if (err?.status === 409) {
          this.error.set('utente già autenticato');
          return;
        }
        // altrimenti prova a mostrare il messaggio ricevuto, o un fallback
        const msg = typeof err?.error === 'string' && err.error.trim()
          ? err.error
          : 'Errore durante la registrazione. Riprova.';
        this.error.set(msg);
      }
    });
  }
}
