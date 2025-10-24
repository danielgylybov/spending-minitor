import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  msg = ''; error = '';
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    fullName: ['', Validators.required],
    password: ['', Validators.required],
  });
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}
  submit() {
    if (this.form.invalid) return;
    this.auth.register(this.form.value as any).subscribe({
      next: () => { this.msg = 'Готово! Влез с данните си.'; this.router.navigateByUrl('/login'); },
      error: err => this.error = err?.error?.error || 'Проблем при регистрация'
    });
  }
}
