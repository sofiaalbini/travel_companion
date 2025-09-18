import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const basic = sessionStorage.getItem('basic');
    if (basic) {
      req = req.clone({
        setHeaders: { Authorization: `Basic ${basic}` }
      });
    }
    return next.handle(req);
  }
}
