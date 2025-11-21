import { Component, Input } from '@angular/core'
import { CommonModule } from '@angular/common'

@Component({
    selector: 'ro-rolling-ribbon',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './rolling-ribbon.html',
    styleUrl: './rolling-ribbon.scss',
})
export class RollingRibbon {
    @Input() items: string[] = []
    @Input() separator: string = '|'
    @Input() backgroundColor: string = 'white'
    @Input() textColor: string = 'primary-950'
    @Input() highlightWord: string = ''
    @Input() animationDuration: number = 20
    @Input() styleClass: string = ''

    get displayText(): string {
        return this.items.join(` ${this.separator} `)
    }

    get hasHighlight(): boolean {
        return this.highlightWord.trim().length > 0
    }

    get containerStyles(): { [key: string]: string } {
        return {
            'background-color': this.backgroundColor,
            '--text-color': `var(--p-${this.textColor})`,
            '--animation-duration': `${this.animationDuration}s`,
        }
    }
}
