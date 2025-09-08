import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login.component';
import { PreferencesComponent } from './pages/preferences.component';
import { authGuard } from './auth.guard';


export const routes: Routes = [
{ path: '', redirectTo: 'login', pathMatch: 'full' },
{ path: 'login', component: LoginComponent },
{ path: 'preferenze', component: PreferencesComponent, canActivate: [authGuard] },
{ path: '**', redirectTo: 'login' }
];