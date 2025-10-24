import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  error = '';
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });
  constructor(private fb: FormBuilder, private auth: AuthService) {}
  submit() {
    if (this.form.invalid) return;
    this.auth.login(this.form.value as any).subscribe({
      error: err => this.error = err?.error?.error || 'Невалидни данни'
    });
  }
}
