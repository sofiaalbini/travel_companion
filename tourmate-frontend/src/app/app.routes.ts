import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register';
import { PreferencesComponent } from './preferences/preferences';
import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'preferences', component: PreferencesComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'login' }
];
