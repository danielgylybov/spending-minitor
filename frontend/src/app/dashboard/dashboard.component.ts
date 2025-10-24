import { Component } from '@angular/core';
import { AuthService, MeRes } from '../auth/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  me?: MeRes;
  constructor(private auth: AuthService) {}
  ngOnInit() { this.auth.me().subscribe(m => this.me = m); }
  logout() { this.auth.logout(); }
}
