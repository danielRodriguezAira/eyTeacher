import {Pipe, PipeTransform, inject} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

/** Marks HTML from the rich text editor as safe for rendering via [innerHTML]. */
@Pipe({
    name: 'safeHtml',
    standalone: true,
})
export class SafeHtmlPipe implements PipeTransform {
    private sanitizer = inject(DomSanitizer);

    transform(value: string | null | undefined): SafeHtml {
        return this.sanitizer.bypassSecurityTrustHtml(value ?? '');
    }
}
