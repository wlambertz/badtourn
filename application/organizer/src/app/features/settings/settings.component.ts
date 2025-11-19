import { CommonModule } from '@angular/common'
import { Component } from '@angular/core'
import { CardModule } from 'primeng/card'

@Component({
  selector: 'ro-settings-placeholder',
  standalone: true,
  imports: [CommonModule, CardModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss',
})
export class SettingsComponent {}
