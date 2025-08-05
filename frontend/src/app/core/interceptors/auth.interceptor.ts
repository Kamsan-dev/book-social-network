import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { HttpErrorResponse, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../features/auth/services/auth.service';
import { catchError, Observable, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Skip certain endpoints
  // if (shouldNotIntercept(request.url)) {
  //   return next(request);
  // }

  const requestWithCredentials = request.clone({
    withCredentials: true,
  });

  return next(requestWithCredentials).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        //authService.logout();
        router.navigate(['/auth']);
      }
      return throwError(() => error);
    })
  );
};

// Helper function to exclude URLs from interception
const shouldNotIntercept = (url: string): boolean => {
  const excludedUrls = ['/auth/authenticate', '/auth/register', '/auth/refresh-token', '/auth/account-validation'];
  return excludedUrls.some((excludedUrl) => url.includes(excludedUrl));
};
