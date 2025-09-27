import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { NotificationComponent } from '../shared/system_notification/notification.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, NotificationComponent],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;
  @ViewChild(NotificationComponent) notification!: NotificationComponent;

  constructor(private api: ApiService, private auth: AuthService, private router: Router) {}

  login() {
    if (!this.username || !this.password) {
      this.notification.showMessage('Please enter username and password', 'success');
      return;
    }

    this.loading = true;
    console.log('Starting login process...');

    this.api.login(this.username, this.password).subscribe({
      next: (username) => {
        console.log('Login succeeded, received username:', username);
        
        // Small delay to ensure session is properly set
        setTimeout(() => {
          this.api.me().subscribe({
            next: (user) => {
              console.log('Successfully fetched user:', user);
              this.auth.setUser(user);
              this.router.navigate(['/preferences']);
              this.loading = false;
            },
            error: (err: HttpErrorResponse) => {
              console.error('Error fetching user info:', err);
              this.loading = false;
              this.notification.showMessage(`Login succeeded but fetching user info failed: ${err.status} ${err.error}`, 'error');
            }
          });
        }, 100);
      },
      error: (err: HttpErrorResponse) => {
        console.error('Login error:', err);
        this.loading = false;
        
        if (err.status === 401) {
          this.notification.showMessage('Invalid username or password', 'error');
        } else if (err.status === 0) {
          this.notification.showMessage('Cannot connect to server. Please check if the backend is running.', 'error');
        } else {
          this.notification.showMessage(`Login failed: ${err.status} - ${err.error || err.message}`, 'error');
        }
      }
    });
  }
}