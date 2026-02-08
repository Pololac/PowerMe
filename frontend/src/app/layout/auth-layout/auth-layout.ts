import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  imports: [],
  templateUrl: './auth-layout.html',
  styleUrl: './auth-layout.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthLayout {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  private readonly redirectUrl = this.route.snapshot.queryParamMap.get('redirectUrl') ?? '/';

  close(): void {
    this.router.navigateByUrl(this.redirectUrl);
  }
}
