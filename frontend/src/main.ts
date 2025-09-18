import { Component } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { Routes, provideRouter, RouterLink, RouterOutlet } from '@angular/router';
import { provideHttpClient,withInterceptorsFromDi } from '@angular/common/http';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import 'zone.js';


// importa i componenti (vedi sotto i file)
import { LoginComponent } from './login.component';
import { PreferencesComponent } from './preferences.component';

import { AuthInterceptor } from './auth.interceptor';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  template: `
 

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

bootstrapApplication(AppComponent, { providers: [
    provideHttpClient(withInterceptorsFromDi()),                         //  prende gli interceptor dal DI
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }, //  registra la classe
    provideRouter(routes),
  ] })
  .catch(err => console.error(err));
