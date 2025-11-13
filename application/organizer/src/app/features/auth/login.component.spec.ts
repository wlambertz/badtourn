import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthService } from '../../core/services/auth.service';
import { LoginComponent } from './login.component';

class AuthServiceStub {
  login = jasmine.createSpy('login').and.returnValue(true);
}

describe('LoginComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, RouterTestingModule],
      providers: [{ provide: AuthService, useClass: AuthServiceStub }],
    }).compileComponents();
  });

  it('should mark controls invalid when fields are empty', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    component.submit();
    expect(component.form.invalid).toBeTrue();
    expect(component.showError).toBeFalse();
  });

  it('should surface an error message when auth fails', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    const authService = TestBed.inject(AuthService) as unknown as AuthServiceStub;
    authService.login.and.returnValue(false);

    component.form.setValue({ username: 'bad', password: 'creds' });
    component.submit();

    expect(component.showError).toBeTrue();
  });

  it('should navigate to the dashboard when auth succeeds', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    const router = TestBed.inject(Router);
    spyOn(router, 'navigateByUrl');

    component.form.setValue({ username: 'organizer', password: 'rallyon' });
    component.submit();

    expect(router.navigateByUrl).toHaveBeenCalledWith('/dashboard');
  });
});
