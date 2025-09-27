import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  register(username: string, password: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/register`, { username, password }, { 
      withCredentials: true,
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  login(username: string, password: string): Observable<string> {
    // Trim whitespace to avoid issues
    const loginData = { 
      username: username.trim(), 
      password: password.trim() 
    };
    
    console.log('Attempting login with:', { username: loginData.username }); // Don't log password
    
    return this.http.post<string>(`${this.baseUrl}/auth/login`, loginData, {
      withCredentials: true,
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      responseType: 'text' as 'json' // Since backend returns just a string (username)
    }).pipe(
      tap(username => {
        console.log('Login successful, received username:', username);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Login error:', error.status, error.error);
        return throwError(() => error);
      })
    );
  }

  me(): Observable<string> {
    return this.http.get(`${this.baseUrl}/auth/me`, { 
      withCredentials: true,
      responseType: 'text'
    }).pipe(
      tap(user => console.log('Current user:', user)),
      catchError((error: HttpErrorResponse) => {
        console.error('Me endpoint error:', error.status, error.error);
        return throwError(() => error);
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/logout`, {}, { 
      withCredentials: true,
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getPreferences(): Observable<any> {
    return this.http.get(`${this.baseUrl}/preferences`, { withCredentials: true });
  }

  addPreferences(preferences: string[]): Observable<any> {
    return this.http.post(`${this.baseUrl}/preferences`, { preferences }, { withCredentials: true });
  }
}