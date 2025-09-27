import { Component, ViewChild} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../services/api.service';
import { NotificationComponent } from '../shared/system_notification/notification.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, NotificationComponent],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  username = '';
  password = '';
  loading = false;
  @ViewChild(NotificationComponent) notification!: NotificationComponent;

  constructor(private api: ApiService, private router: Router) {}

  register() {
    this.loading = true;
    this.api.register(this.username, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.notification.showMessage('Registration successful! Please log in.', 'success');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        const msg = err?.error || 'Registration failed (username may already exist)';
        this.notification.showMessage(msg, 'error');
      }
    });
  }
}
