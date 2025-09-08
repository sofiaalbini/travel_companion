import { Injectable } from '@angular/core';


export type UserPrefs = {
username: string;
prefs: string[];
createdAt: string;
};


const STORAGE_KEY = 'tourmate_users';


@Injectable({ providedIn: 'root' })
export class UsersService {
private readAll(): UserPrefs[] {
const raw = localStorage.getItem(STORAGE_KEY);
try { return raw ? JSON.parse(raw) as UserPrefs[] : []; } catch { return []; }
}


private writeAll(list: UserPrefs[]) {
localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
}


getByUsername(username: string): UserPrefs | undefined {
return this.readAll().find(u => u.username.toLowerCase() === username.toLowerCase());
}


usernameExists(username: string): boolean {
return !!this.getByUsername(username);
}


upsert(user: UserPrefs) {
const all = this.readAll();
const idx = all.findIndex(u => u.username.toLowerCase() === user.username.toLowerCase());
if (idx >= 0) all[idx] = user; else all.push(user);
this.writeAll(all);
}


savePrefs(username: string, prefs: string[]) {
const now = new Date().toISOString();
const existing = this.getByUsername(username);
const record: UserPrefs = existing ? { ...existing, prefs } : { username, prefs, createdAt: now };
this.upsert(record);
}
}