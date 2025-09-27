import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, of } from 'rxjs';
import { ApiService } from './api.service';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private userSubject = new BehaviorSubject<string | null>(null);
  user$ = this.userSubject.asObservable();

  constructor(private api: ApiService) {
    // try to initialize current user (silent)
    this.fetchMe().subscribe(() => {}, () => {});
  }

  fetchMe() {
    return this.api.me().pipe(
      tap((username) => this.userSubject.next(username)),
      catchError((err) => {
        this.userSubject.next(null);
        return of(null);
      })
    );
  }

  setUser(username: string | null) {
    this.userSubject.next(username);
  }

  logout() {
    // clear JSESSIONID cookie (for simple cases)
    document.cookie = 'JSESSIONID=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    this.userSubject.next(null);
  }
}
