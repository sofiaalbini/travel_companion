import { Component, OnInit, ViewChild } from '@angular/core';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AVAILABLE_PREFS } from './preferences.constants';
import { NotificationComponent } from '../shared/system_notification/notification.component';


@Component({
  selector: 'app-preferences',
  standalone: true,
  templateUrl: './preferences.html',
  styleUrl: './preferences.css',
  imports: [CommonModule, NotificationComponent]
})
export class PreferencesComponent implements OnInit {
  availablePrefs = AVAILABLE_PREFS;
  selected: string[] = [];
  currentPrefs: string[] = [];
  user: string | null = null;
  loading = false;
  @ViewChild(NotificationComponent) notification!: NotificationComponent;
  
  constructor(private api: ApiService, private auth: AuthService, private router: Router) { }

  ngOnInit() {
    this.auth.user$.subscribe(u => this.user = u);
    this.loadPrefs();
  }

  loadPrefs() {
    this.api.getPreferences().subscribe({
      next: (res: any) => {
        if (Array.isArray(res)) {
          this.currentPrefs = res.map((p: any) =>
            typeof p === 'string' ? p : p.preferenceName ?? p.name ?? JSON.stringify(p)
          );
        } else {
          this.currentPrefs = [];
        }
      },
      error: () => this.currentPrefs = []
    });
  }

  toggle(pref: string) {
    if (this.selected.includes(pref)) {
      this.selected = this.selected.filter(p => p !== pref);
    } else {
      this.selected.push(pref);
    }
  }


  save() {
    if (this.selected.length === 0) {
      this.notification.showMessage('Please choose at least one preference.', 'error');;
      return;
    }
    this.loading = true;
    this.api.addPreferences(this.selected).subscribe({
      next: () => {
        this.loading = false;
        this.notification.showMessage('Preferences saved!', 'success');
        this.selected = [];
        this.loadPrefs();
      },
      error: () => {
        this.loading = false;
        this.notification.showMessage('Failed to save preferences', 'error');
      }
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }


}
