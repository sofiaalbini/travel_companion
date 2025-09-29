import { Component, OnInit, ViewChild } from '@angular/core';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AVAILABLE_PREFS } from './preferences.constants';
import { NotificationComponent } from '../shared/system_notification/notification.component';


/**
 * PreferencesComponent
 * Lets authenticated users view, select, and save their travel preferences.
 */
@Component({
  selector: 'app-preferences',
  standalone: true,
  templateUrl: './preferences.html',
  styleUrl: './preferences.css',
  imports: [CommonModule, NotificationComponent]
})
export class PreferencesComponent implements OnInit {

  /** All available preference options (static list from constants). */
  availablePrefs = AVAILABLE_PREFS;


  /** Preferences currently selected in the UI but not yet saved. */
  selected: string[] = [];

  /** Preferences already stored in the backend for this user. */
  currentPrefs: string[] = [];
  /** Logged-in username. */
  user: string | null = null;

  /** Flag for showing loading state (while saving). */
  loading = false;

  /** Reference to the notification component for showing messages */
  @ViewChild(NotificationComponent) notification!: NotificationComponent;

  /**
 * Creates the PreferencesComponent
 * @param api Service for making API calls to the backend
 * @param auth Service for handling authentication state in the frontend
 * @param router Angular Router for navigation after login
 */
  constructor(private api: ApiService, private auth: AuthService, private router: Router) { }

  /** On component init, subscribe to user state and load saved preferences. */
  ngOnInit() {
    this.auth.user$.subscribe(u => this.user = u);
    this.loadPrefs();
  }

  /** Fetch preferences from backend and normalize response format. */
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

  /**
  * Toggle a preference in the selection list.
  * If already selected, remove it; otherwise, add it.
  */
  toggle(pref: string) {
    if (this.selected.includes(pref)) {
      this.selected = this.selected.filter(p => p !== pref);
    } else {
      this.selected.push(pref);
    }
  }

  /**
     * Save selected preferences:
     * - Creates new preferences if none exist (POST).
     * - Updates existing preferences (PUT).
     * Shows success/error notifications accordingly.
     */
  save() {
    if (this.selected.length === 0) {
      this.notification.showMessage('Please choose at least one preference.', 'error');
      return;
    }
    this.loading = true;

    const request$ = this.currentPrefs.length === 0
      ? this.api.createPreferences(this.selected)  // POST
      : this.api.updatePreferences(this.selected); // PUT

    request$.subscribe({
      next: () => {
        this.loading = false;
        this.notification.showMessage('Preferences saved!', 'success');
        this.selected = [];
        this.loadPrefs();  // refresh backend state
      },
      error: () => {
        this.loading = false;
        this.notification.showMessage('Failed to save preferences', 'error');
      }
    });
  }

  /** Logout the current user and navigate back to login screen. */
  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }


}
