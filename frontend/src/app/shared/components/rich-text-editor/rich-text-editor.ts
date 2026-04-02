import {Component, forwardRef, Input} from '@angular/core';
import {AbstractControl, ControlValueAccessor, FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors, Validator} from '@angular/forms';
import {ContentChange, QuillEditorComponent, QuillModules} from 'ngx-quill';

const MAX_IMAGE_WIDTH = 1400;
const JPEG_QUALITY = 0.85;

@Component({
    selector: 'app-rich-text-editor',
    standalone: true,
    imports: [QuillEditorComponent, FormsModule],
    providers: [
        {provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => RichTextEditor), multi: true},
        {provide: NG_VALIDATORS, useExisting: forwardRef(() => RichTextEditor), multi: true},
    ],
    templateUrl: './rich-text-editor.html',
    styleUrl: './rich-text-editor.scss',
})
export class RichTextEditor implements ControlValueAccessor, Validator {
    @Input() label = '';
    @Input() placeholder = '';
    @Input() required = false;

    value = '';
    touched = false;

    private _onChange: (val: string) => void = () => {};
    private _onTouched: () => void = () => {};
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private quillInstance: any = null;

    modules: QuillModules = {
        toolbar: {
            container: [
                ['bold', 'italic', 'underline', 'strike'],
                [{header: [1, 2, 3, false]}],
                [{size: ['small', false, 'large', 'huge']}],
                [{list: 'ordered'}, {list: 'bullet'}],
                [{align: []}],
                [{color: []}, {background: []}],
                ['link', 'image'],
                ['table'],
                ['clean'],
            ],
            handlers: {
                image: () => this.handleImageInsert(),
            },
        },
        table: true,
    };

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    onEditorCreated(quill: any): void {
        this.quillInstance = quill;
    }

    writeValue(val: string): void {
        this.value = val ?? '';
    }

    registerOnChange(fn: (val: string) => void): void {
        this._onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this._onTouched = fn;
    }

    onContentChanged(event: ContentChange): void {
        this.value = event.html ?? '';
        this._onChange(this.value);
    }

    onBlur(): void {
        this.touched = true;
        this._onTouched();
    }

    get showError(): boolean {
        return this.touched && this.required && this.isEmptyContent(this.value);
    }

    validate(control: AbstractControl): ValidationErrors | null {
        if (this.required && this.isEmptyContent(control.value)) {
            return {required: true};
        }
        return null;
    }

    private handleImageInsert(): void {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';
        input.onchange = () => {
            const file = input.files?.[0];
            if (file) {
                this.compressAndInsert(file);
            }
        };
        input.click();
    }

    private compressAndInsert(file: File): void {
        const reader = new FileReader();
        reader.onload = (readerEvent) => {
            const img = new Image();
            img.onload = () => {
                const scale = img.width > MAX_IMAGE_WIDTH ? MAX_IMAGE_WIDTH / img.width : 1;
                const canvas = document.createElement('canvas');
                canvas.width = Math.round(img.width * scale);
                canvas.height = Math.round(img.height * scale);
                canvas.getContext('2d')!.drawImage(img, 0, 0, canvas.width, canvas.height);
                const dataUrl = canvas.toDataURL('image/jpeg', JPEG_QUALITY);
                const range = this.quillInstance?.getSelection(true);
                if (range !== null) {
                    this.quillInstance.insertEmbed(range.index, 'image', dataUrl, 'user');
                    this.quillInstance.setSelection(range.index + 1, 0, 'silent');
                }
            };
            img.src = readerEvent.target!.result as string;
        };
        reader.readAsDataURL(file);
    }

    private isEmptyContent(value: string): boolean {
        if (!value) return true;
        return value.replace(/<[^>]*>/g, '').trim() === '';
    }
}
