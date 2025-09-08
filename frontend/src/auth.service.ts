import { Injectable, signal, computed } from '@angular/core';


const AUTH_KEY = 'tourmate_auth_user';


@Injectable({ providedIn: 'root' })
export class AuthService {
private _user = signal<string | null>(this.load());


readonly user = computed(() => this._user());
readonly isLoggedIn = computed(() => this._user() !== null);


login(username: string) {
const u = username.trim();
if (!u) return;
this._user.set(u);
localStorage.setItem(AUTH_KEY, u);
}


logout() {
this._user.set(null);
localStorage.removeItem(AUTH_KEY);
}


private load(): string | null {
const raw = localStorage.getItem(AUTH_KEY);
return raw ? raw : null;
}
}