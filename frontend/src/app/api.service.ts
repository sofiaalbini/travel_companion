import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private api = environment.apiBase;
  constructor(private http: HttpClient) {}

  register(username: string, password: string) {
    return this.http.post(`${this.api}/auth/register`, { username, password });
  }

  private auth(username: string, password: string) {
    const token = btoa(`${username}:${password}`);
    return new HttpHeaders({ Authorization: `Basic ${token}` });
  }

  me(username: string, password: string) {
    return this.http.get(`${this.api}/auth/me`, {
      headers: this.auth(username, password), responseType: 'text'
    });
  }

  createPreference(username: string, password: string, destinazione: string) {
    return this.http.post(`${this.api}/preferences`, { destinazione }, {
      headers: this.auth(username, password)
    });
  }

  myPreferences(username: string, password: string) {
    return this.http.get(`${this.api}/preferences`, {
      headers: this.auth(username, password)
    });
  }
}
