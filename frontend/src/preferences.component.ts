import { Component, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-preferences',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section class="page">
      <h1>Preferenze di viaggio</h1>

      <form [formGroup]="form" class="card" (ngSubmit)="salva()">
      

        <div class="group">
          <div class="group-title">Scegli i tuoi interessi:</div>

          <div class="chips" role="group" aria-label="Interessi di viaggio">
            <button
              type="button"
              class="chip"
              *ngFor="let opt of opzioni"
              [class.active]="isSelected(opt)"
              role="checkbox"
              [attr.aria-checked]="isSelected(opt)"
              (click)="toggle(opt)">
              <span class="box" aria-hidden="true">
                <span class="tick" *ngIf="isSelected(opt)">✓</span>
              </span>
              {{ opt }}
            </button>
          </div>
        </div>

        <div class="actions">
          <button class="primary" type="submit">Salva preferenze</button>
          <span class="saved" *ngIf="saved()">Salvato ✓</span>
        </div>
      </form>
    </section>
  `,
  styles: [`
    :host { display:block; }
    .page { max-width: 760px; margin: 2rem auto; padding: 0 1rem; }
    h1 { font-size: 1.6rem; margin-bottom: 1rem; }

    .card {
      background: #fff;
      border: 1px solid #e6e6e6;
      border-radius: 16px;
      padding: 1.25rem;
      box-shadow: 0 1px 2px rgba(0,0,0,.03);
    }

    .field { display:block; margin-bottom: 1rem; }
    .label { display:block; font-weight: 600; margin-bottom: .35rem; }
    .input {
      width:100%;
      border: 1px solid #d6d6d6;
      border-radius: 10px;
      padding: .6rem .8rem;
      outline: none;
      font-size: .95rem;
    }
    .input:focus { border-color:#4c8bf7; box-shadow: 0 0 0 3px rgba(76,139,247,.15); }

    .group { margin-top: .5rem; }
    .group-title { font-weight: 600; margin-bottom: .5rem; }

    .chips {
      display: flex;
      flex-wrap: wrap;
      gap: .5rem .6rem;
    }

    .chip {
      display: inline-flex;
      align-items: center;
      gap: .5rem;
      border-radius: 999px;
      padding: .45rem .7rem;
      border: 1.5px solid #d9d9d9;
      background: #fff;
      color: #222;
      cursor: pointer;
      user-select: none;
      transition: background .15s, border-color .15s, color .15s, box-shadow .15s;
      font-size: .92rem;
      line-height: 1;
    }
    .chip:hover { border-color:#bdbdbd; }
    .chip:focus { outline:none; box-shadow: 0 0 0 3px rgba(76,139,247,.18); }

    .chip .box {
      width: 18px; height: 18px; border-radius: 6px;
      border: 1.6px solid #bfbfbf;
      display: grid; place-items: center;
      background: #fff;
    }
   .chip .tick { font-size: .85rem; line-height: 1; }

    .chip.active {
      background: #2f7ddc;
      border-color: #2f7ddc;
      color: #fff;
    }
    .chip.active .box {
      background: rgba(255,255,255,.18);
      border-color: rgba(255,255,255,.9);
    }

    .actions { margin-top: 1rem; display:flex; align-items:center; gap:.75rem; }
    .primary {
      border: none; border-radius: 10px;
      padding: .6rem .9rem;
      background: #2f7ddc; color: #fff; font-weight: 600;
      cursor: pointer;
    }
    .primary:hover { filter: brightness(.95); }
    .saved { color: #138a36; font-weight: 600; }
  `]
})
export class PreferencesComponent {
  // elenco delle pillole come nell'immagine, adattate al viaggio
  opzioni = [
    'Mare', 'Montagna', 'Città d’arte', 'Natura', 'Storia',
    'Avventura', 'Relax', 'Cibo & vino', 'Benessere/SPA', 'Vita notturna',
    'Road trip', 'Festival/Concerti', 'Family-friendly', 'Low cost', 'Lusso'
  ];

  saved = signal(false);

  form = this.fb.group({
    
    categorie: this.fb.control<string[]>([])
  });

  constructor(private fb: FormBuilder) {
    // ripristina da localStorage se presente
    const raw = localStorage.getItem('tourmate_prefs');
    if (raw) {
      try {
        const parsed = JSON.parse(raw);
        this.form.patchValue(parsed, { emitEvent: false });
      } catch {}
    }

    // persistenza automatica ad ogni modifica
    this.form.valueChanges.subscribe(v => {
      localStorage.setItem('tourmate_prefs', JSON.stringify(v));
    });
  }

  isSelected(opt: string): boolean {
    return (this.form.value.categorie ?? []).includes(opt);
  }

  toggle(opt: string) {
    const current = [...(this.form.value.categorie ?? [])];
    const i = current.indexOf(opt);
    if (i >= 0) current.splice(i, 1);
    else current.push(opt);
    this.form.patchValue({ categorie: current });
  }

  salva() {
    localStorage.setItem('tourmate_prefs', JSON.stringify(this.form.value));
    this.saved.set(true);
    setTimeout(() => this.saved.set(false), 1800);
  }
}
