import { CommonModule } from '@angular/common'
import { Component, inject } from '@angular/core'
import { Router } from '@angular/router'
import { ButtonModule } from 'primeng/button'
import { CardModule } from 'primeng/card'

@Component({
  selector: 'ro-events-placeholder',
  standalone: true,
  imports: [CommonModule, ButtonModule, CardModule],
  templateUrl: './events.component.html',
  styleUrl: './events.component.scss',
})
export class EventsComponent {
  private readonly router = inject(Router)

  goBackToDashboard(): void {
    this.router.navigateByUrl('/dashboard')
  }
}
