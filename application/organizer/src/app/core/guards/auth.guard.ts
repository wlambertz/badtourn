import { inject } from '@angular/core';
import { CanActivateChildFn, CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

const handleAuthCheck = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAuthenticated() ? true : router.createUrlTree(['/login']);
};

export const authGuard: CanActivateFn = () => handleAuthCheck();

export const authChildGuard: CanActivateChildFn = () => handleAuthCheck();
