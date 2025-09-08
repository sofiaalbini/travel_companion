import { Component } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { Routes, provideRouter, RouterLink, RouterOutlet } from '@angular/router';
import 'zone.js';


// importa i componenti (vedi sotto i file)
import { LoginComponent } from './login.component';
import { PreferencesComponent } from './preferences.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  template: `
    <header class="topbar">
      <a class="brand" routerLink="/">Tourmate</a>
      <nav>
        <a routerLink="/">Login</a>
        <a routerLink="/preferenze">Preferenze</a>
      </nav>
    </header>

    <main class="container">
      <router-outlet></router-outlet>
    </main>
  `,
})
class AppComponent {}

const routes: Routes = [
  { path: '', component: LoginComponent },          // root = Login
  { path: 'preferenze', component: PreferencesComponent },
  { path: '**', redirectTo: '' },
];

bootstrapApplication(AppComponent, { providers: [provideRouter(routes)] })
  .catch(err => console.error(err));
