import {TestBed} from '@angular/core/testing';
import {provideRouter} from '@angular/router';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideAnimations} from '@angular/platform-browser/animations';
import {App} from './app';

describe('App', () => {
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [App],
            providers: [
                provideRouter([]),
                provideHttpClient(),
                provideHttpClientTesting(),
                provideAnimations(),
                {provide: 'LOCALSTORAGE', useValue: window.localStorage}
            ]
        }).compileComponents();
    });

    it('should create the app', () => {
        const fixture = TestBed.createComponent(App);
        const app = fixture.componentInstance;
        expect(app).toBeTruthy();
    });

    it('should have the eyTeacher title', () => {
        const fixture = TestBed.createComponent(App);
        const app = fixture.componentInstance;
        expect(app['title']()).toBe('EyTeacher');
    });
});
